package net.tccn.file;

import net.tccn.base.*;
import net.tccn.base.dbq.table.Field;
import net.tccn.meta.MetaTable;
import net.tccn.plat.SysPlat;
import org.redkale.net.http.RestMapping;
import org.redkale.net.http.RestParam;
import org.redkale.net.http.RestService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

/**
 * @author: liangxianyou at 2018/10/24 10:57.
 */
@RestService(automapping = true, comment = "文件服务")
public class _FileService extends BaseService {

    private final static String[] FIELDS = {"field", "cate", "must", "remark1", "remark2", "tag", "selects", "column", "filter", "ck", "edit"};


    @RestMapping(name = "sheets", comment = "得到所有的sheetName")
    public List<String> sheets(String filePath) {
        List<String> sheets = new ArrayList<>();

        File file = new File(webroot, filePath);
        if (file.exists()) {
            sheets = ExcelKit.getSheetNames(file);
        }

        return sheets.stream().filter(x -> {
            return !x.contains("版本记录") && !x.contains("表说明") && !x.contains("表名称");
        }).collect(Collectors.toList());
    }

    public SysPlat getSysPlat(String token) {
        return getT(token, SysPlat.class, () -> SysPlat.dao.findFirst(new SysPlat(token)));
    }

    @RestMapping(name = "data", comment = "得到文件数据")
    public JBean data(String filePath, @RestParam(name = "platToken") String token) {
        JBean jBean = new JBean();
        //SysPlat sysPlat = getSysPlat(token);
        File file = new File(webroot, filePath);
        if (file.exists()) {
            Map<String, List<Map>> map = ExcelKit.readExcelAll(file, FIELDS);

            Kv<String, MetaTable> data = Kv.of();
            map.forEach((k, v) -> {
                if (v.size() > 2) {
                    data.put(k.replace(" ", ""), toMetaTable(v));
                }
            });

            String[] tableArr = data.keySet().toArray(new String[data.size()]);
            List<String> hv = MetaKit.tableExist(tableArr, token);

            Kv res = Kv.of();
            data.forEach((k, v) -> {
                Kv kv = Kv.of();
                res.put(k,
                        kv.set("name", v.getName())
                                .set("hv", hv.contains(v.getName()) ? 1 : 0)
                                .set("comment", v.getComment())
                );
            });

            jBean.setBody(res);
        }

        return jBean;
    }

    @RestMapping(name = "sheet_data", comment = "得到sheet数据")
    public JBean sheetData(String filePath, String sheetName, @RestParam(name = "platToken") String token) {
        JBean jBean = new JBean();
        File file = new File(webroot, filePath);
        List<Map> list = ExcelKit.readExcel(file, FIELDS, sheetName);
        MetaTable metaTable = toMetaTable(list);

        jBean.setBody(metaTable);

        return jBean;
    }

    @RestMapping(ignore = true, comment = "导入excel数据到metatable")
    public JBean saveSheet(@RestParam(name = "sheetArr", comment = "sheet名") String[] sheetArr,
                           @RestParam(name = "filePath", comment = "文件路径") String filePath,
                           @RestParam(name = "platToken") String token) {
        JBean jBean = new JBean();

        File file = new File(webroot, filePath);
        String[] fields = {"field", "cate", "must", "remark1", "remark2", "tag", "selects", "column", "filter", "ck", "edit"};
        Map<String, List<Map>> map = ExcelKit.readExcelAll(file, fields);
        Set<String> ks = map.keySet();

        // 找到需要导入的sheet名，并组装对应的数据
        MetaTable[] metaTables = Stream.of(sheetArr).filter(x -> {
            if (!ks.contains(x)) return false;

            MetaTable bean = new MetaTable();
            bean.setSysPlatId(platId(token));
            bean.setName(x);
            return MetaTable.dao.findFirst(bean) == null;
        }).map(x -> {
            MetaTable metaTable = toMetaTable(map.get(x));
            metaTable.setSysPlatId(platId(token));
            metaTable.setAlias(MetaKit.nextAlias());
            return metaTable;
        }).toArray(MetaTable[]::new);

        MetaKit.save(metaTables);
        return jBean;
    }

    /**
     * 组装元数据
     */
    private MetaTable toMetaTable(List<Map> list) {

        //Kv col = Kv.of();
        MetaTable metaTable = new MetaTable();

        list.remove(1);//list[0] head info
        Map rowHead = list.remove(0);

        String comment = getComment(rowHead);//list[1] comment,
        String tableName = getTableName(rowHead);
        //col.set("name", tableName).set("comment", comment);
        metaTable.setName(tableName);
        metaTable.setComment(comment);

        //所有字段
        List<Field> items = new ArrayList<>();

        //展示的字段
        List<String> shows = new ArrayList<>();

        //编辑的字段
        List<Map> edits = new ArrayList<>();

        //查询过滤用字段
        List<Map> filters = new ArrayList<>();


        list.forEach(x -> {
            String colName = x.getOrDefault("field", "") + "";

            Field item = new Field();
            item.setName(colName);
            item.setLabel(x.getOrDefault("remark1", "") + "");
            item.setType(x.getOrDefault("cate", "") + "");
            item.setInType(x.getOrDefault("tag", "") + "");
            item.setInExt(x.getOrDefault("selects", "") + "");

            items.add(item);

            if ("1".equals(x.getOrDefault("column", "") + "")) {
                shows.add(colName);
            }

            if ("1".equals(x.getOrDefault("edit", "") + "")) {
                edits.add(Kv.of("col", colName));
            }

            if (x.get("filter") != null && !"".equals(x.get("filter") + "")) {
                String filter = x.getOrDefault("filter", "") + "";
                filter = filter.replace("1", "EQUAL");
                filter = filter.replace("!1", "NOTEQUAL");
                filter = filter.replace("like", "LIKE");

                filters.add(Kv.of("name", item).set("filterType", asList(filter.split(","))));
            }

        });

        metaTable.setItems(items);
        //metaTable.setShows(shows);
        //metaTable.setEdits(edits);
        //metaTable.setFilters(filters);
        return metaTable;
    }

    private String getTableName(Map rowHead) {
        String field = rowHead.get("field") + "";

        int s = field.indexOf("(");
        if (s > 0) {
            return field.substring(0, s);
        }
        return field;
    }

    private String getComment(Map rowHead) {
        String field = rowHead.get("field") + "";

        int s = field.indexOf("(");
        int e = field.indexOf(")");
        if (s > 0) {
            return field.substring(s + 1, e > 0 ? e : field.length());
        }
        return "";
    }
}
