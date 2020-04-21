package net.tccn.base.dbq.parser;

import net.tccn.base.*;
import net.tccn.base.dbq.fbean.*;
import net.tccn.meta.MetaLink;
import net.tccn.meta.MetaService;
import net.tccn.meta.MetaTable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 查询的数据是否同库，
 * 是：支持副表过滤
 * 否：只支持主表过滤
 * Created by liangxianyou at 2018/12/24 15:49.
 */
public class ParseMysql implements Parser {

    Predicate<Kv<String, MetaTable>> sameDbFun = (kv) -> {
        String dbPlatId = null;
        for (MetaTable metaTable : kv.values()) {
            if (dbPlatId == null) {
                dbPlatId = metaTable.getDbPlatId();
            } else if (!dbPlatId.equals(metaTable.getDbPlatId())) {
                return false;
            }
        }
        return true;
    };

    /**
     * 查询数据解析
     * @param fBean
     * @return
     */
    @Override
    public String[] parseList(FBean fBean) {
        MetaService metaService = MetaKit.getMetaService(fBean.getName(), fBean.getPlatToken());

        Kv<String, MetaTable> tables = MetaKit.getMetaTables(metaService, false);//所有的关联表信息
        MetaTable metaTable = tables.get(metaService.getTable());//基础元数据
        List<Map<String, String>> shows = metaService.getShows();//查询的属性-列表
        List<Map<String, String>> exports = metaService.getExports();//查询的属性-导出

        List<String> _filters = fBean.getFilters().stream().map(Filter::getCol).collect(Collectors.toList());
        List<MetaLink> links = MetaKit.getMetaLinks(
                metaService.getTable(),
                shows.stream().map(x -> x.get("col")).collect(Collectors.toList()),
                _filters
        );
        //查询条件
        List<Filter> filters = fBean.getFilters();
        Limit limit = fBean.getLimit();
        List<Order> orders = fBean.getOrders();
        //Map<String, List<Filter>> filterMap = filters.stream().collect(Collectors.groupingBy(x -> x.getCol().split("[.]")[0]));
        //Map<String, List<String>> showMap = shows.stream().collect(Collectors.groupingBy(x -> x.split("[.]")[0]));


        //判断是否为同库
        if (sameDbFun.test(tables) || true) {
            // where 1=1 and xx=xx
            StringBuffer bufWhere = new StringBuffer();
            if (!X.isEmpty(filters)) {
                bufWhere.append(Filter.filter(filters, DbType.MYSQL));
            }

            //select a.x, b.y, c.z
            StringBuffer bufSelect = new StringBuffer();
            bufSelect.append("select ");
            if ("export".equals(fBean.getType()) && !X.isEmpty(exports)) {
                exports.forEach(x -> {
                    bufSelect.append(x.get("col")).append(" as ").append("'").append(x.get("col")).append("',");
                });
                bufSelect.deleteCharAt(bufSelect.length() - 1);
            }
            else if ("list".equals(fBean.getType()) && !X.isEmpty(shows)) {
                shows.forEach(x -> {
                    bufSelect.append(x.get("col")).append(" as ").append("'").append(x.get("col")).append("',");
                });
                bufSelect.deleteCharAt(bufSelect.length() - 1);
            } else {
                bufSelect.append("*");
            }

            //from
            StringBuilder bufFrom = new StringBuilder();
            bufFrom.append(" from ").append(metaTable.getCatalog()).append(".`").append(metaTable.getName()).append("` ").append(metaTable.getAlias());
            //left join
            if (!X.isEmpty(links)) {
                links.forEach(x -> {
                    MetaTable rightTable = tables.get(metaTable.getAlias().equals(x.getTables()[0]) ? x.getTables()[1] : x.getTables()[0]);
                    if (rightTable != null) {
                        bufFrom.append(" left join ").append(rightTable.getCatalog()).append(".").append(rightTable.getName()).append(" ").append(rightTable.getAlias()).append(" on ");
                        int tag = bufFrom.length();
                        x.getLink().forEach((k, v) -> {
                            if (bufFrom.length() > tag) {
                                bufFrom.append(" and ");
                            }
                            bufFrom.append(k).append("=").append(v);
                        });
                    }
                });
            }

            StringBuffer bufOth = new StringBuffer();
            //order by
            if (!X.isEmpty(orders)) {
                bufOth.append(" ").append(Order.order(orders, DbType.MYSQL));
            }
            //limit
            bufOth.append(" ").append(limit.limit());

            return new String[]{
                    "select count(1) " + bufFrom + bufWhere,
                    "" + bufSelect + bufFrom + bufWhere + bufOth
            };
        }

        return null;
    }

    /**
     * 删除执行语句解析
     * @param name
     * @param data
     * @param token
     * @return
     */
    public String parseDel(String name, Map data, String token) {
        MetaService metaService = MetaKit.getMetaService(name, token);
        Map<String, String> dels = metaService.getDels();
        MetaTable mainTable = MetaKit.getMetaTableByAlias(metaService.getTable());

        String sql = "";
        if ("UP_FIELD".equalsIgnoreCase(dels.get("cate"))) {
            data.put("table", mainTable.getName());
            sql = TplKit.parseTpl("update #(table) set status=9 where id=#(id)", data);
        } else if ("SQL".equalsIgnoreCase(dels.get("cate"))) {
            sql = TplKit.parseTpl(dels.get("sql"), data);
        } else if ("QTASK".equalsIgnoreCase(dels.get("cate"))) {
            sql = TplKit.parseTpl("qtask", data);
        }

        return sql;
    }

    /**
     * 保存数据解析逻辑：
     * 1.得到主表信息
     * 2.根据主表信息 使用数据创建sql
     * @param serviceName 业务名称
     * @param data 待保存的数据
     * @param token 平台token
     * @return sql 待执行的sql语句
     */
    public String parseSave(String serviceName, Map<String, String> data, String token) {
        MetaTable mainTable = MetaKit.getMainTable(serviceName, token);
        String alias = mainTable.getAlias();
        String[] pks = mainTable.pk();

        // 异常 sql： update `user2` set `aa.name`='null',`aa.deptId`='null',`aa.id`='null' where `id` = '1';

        // 取出有效的数据key
        List<String> keys = data.keySet()
                .stream()
                .filter(x ->
                        x.startsWith(alias + ".") || !X.isEmpty(data.get(alias + "." + x))
                ).collect(Collectors.toList());
        if (pks.length == 0) {
            throw new CfgException("保存数据失败，检查业务主表[%s-%S]主键配置", mainTable.getName(), mainTable.getComment());
        } else if (keys.size() == 0) {
            throw new CfgException("保存数据失败，提交数据不能改空");
        }


        //单主键
        String pv = data.get(alias + "." + pks[0]);
        if (X.isEmpty(pv)) { //新增
            String sqlTpl = "INSERT INTO `%s` (%s) VALUES %s;"; // para: table、 ks、 vs
            StringBuffer ks = new StringBuffer();// `k1`,`k2`,`k3`, ...
            StringBuffer vs = new StringBuffer();// `v1`,`v2`,`v3`, ...

            for (String k : keys) {
                ks.append(String.format("`%s`,", k.substring(k.indexOf(".") + 1)));
                vs.append(String.format("'%s',", data.get(k)));
            }
            if (ks.length() > 0) {
                ks.deleteCharAt(ks.length() - 1);
                vs.deleteCharAt(vs.length() - 1);
            }

            return String.format(sqlTpl, mainTable.getName(), ks, vs);
        }

        else { //修改
            String sqlTpl = "update `%s` set %s where `%s` = '%s';"; // para: table、 kvs、 pk、 pv
            StringBuilder kvs = new StringBuilder(); // `k1`='v1',`k2`='v2', ...
            String pk = pks[0];

            for (String k : keys) {
                kvs.append(String.format("`%s`='%s',", k.substring(k.indexOf(".") + 1), data.get(k)));
            }

            if (kvs.length() > 0) {
                kvs.deleteCharAt(kvs.length() - 1);
            }

            return String.format(sqlTpl, mainTable.getName(), kvs, pk, pv);
        }
    }
}
