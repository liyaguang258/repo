package net.tccn.meta;

import net.tccn.base.BaseService;
import net.tccn.base.JBean;
import net.tccn.base.Kv;
import net.tccn.base.MetaKit;
import net.tccn.base.dbq.table.Field;
import net.tccn.plat.SysPlat;
import org.redkale.net.http.RestMapping;
import org.redkale.net.http.RestParam;
import org.redkale.net.http.RestService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: liangxianyou at 2018/10/17 16:59.
 */
@RestService(name = "meta", automapping = true, comment = "元数据服务")
public class MetadataService extends BaseService { //arango

    //-----------  元数据管理 ---------------
    @RestMapping(name = "tablelist", comment = "table列表")
    public JBean tableList(@RestParam(name = "platToken") String token, String catalog, String dbPlatId, String name) {
        JBean jBean = new JBean();

        List<Kv> list = MetaKit.getMetaTables().stream().filter(x
                -> (isEmpty(catalog) || catalog.equals(x.getCatalog())) &&
                (isEmpty(dbPlatId) || dbPlatId.equals(x.getDbPlatId())) &&
                (isEmpty(name) || x.getName().contains(name)) &&
                (isEmpty(token) || x.getSysPlatId().equals(platId(token)))
        ).map(x -> Kv.of("name", x.getName())
                .set("comment", x.getComment())
                .set("catalog", x.getCatalog())
                .set("alias", x.getAlias())
                .set("dbPlatId", x.getDbPlatId())
                .set("linkCount", MetaKit.getMetaLinks().stream().filter(link -> { // 关联表数量
                    String[] tables = link.getTables();
                    return x.getAlias().equals(tables[0]) || x.getAlias().equals(tables[1]);
                }).count())
        ).collect(Collectors.toList());
        return jBean.setBody(list);
    }


    @RestMapping(name = "service_list", comment = "业务列表")
    public JBean serviceList(@RestParam(name = "platToken") String token) {
        JBean jBean = new JBean();

        List<MetaService> list = MetaKit.getMetaServices().stream()
                .filter(x -> platId(token).equals(x.getSysPlatId()))
                .collect(Collectors.toList());
        jBean.setBody(list);

        return jBean;
    }

    @RestMapping(name = "tableinfo", comment = "table详情")
    public JBean tableInfo(@RestParam(name = "platToken") String token, String name, String alias) {
        JBean jBean = new JBean();

        MetaTable metaTable = null;
        if (!isEmpty(name)) {
            metaTable = MetaKit.getMetaTable(name, token);
        }
        if (metaTable == null && !isEmpty(alias)) {
            metaTable = MetaKit.getMetaTableByAlias(alias);
        }

        return jBean.setBody(metaTable);
    }

    @RestMapping(name = "service_save", comment = "service信息保存")
    public JBean serviceSave(@RestParam(name = "platToken") String token, @RestParam(name = "service") MetaService service) {
        JBean jBean = new JBean();
        do {
            // 标识码非空校验
            if (isEmpty(service.getTable())) {
                jBean.set(-1, "请选择业务主表");
                break;
            }

            // 标识码非空校验
            if (isEmpty(service.getName())) {
                jBean.set(-1, "业务标识码无效");
                break;
            }

            // 标识码重复校验
            MetaService metaService = MetaKit.getMetaService(service.getName(), token);
            if (metaService != null && !metaService.getKey().equals(service.getKey())) {
                jBean.set(-1, "业务标识码被占用，修改业务标识码重试");
                break;
            }

            if (service.getKey() == null) {
                service.setSysPlatId(platId(token));
            }
            MetaKit.save(service);
        } while (false);
        return jBean;
    }

    @RestMapping(name = "service_info", comment = "service基本信息")
    public JBean serviceInfo(@RestParam(name = "platToken") String token, String name) {
        MetaService metaService = MetaKit.getMetaService(name, token);
        return JBean.by(0, "", metaService);
    }

    @RestMapping(name = "service_detail", comment = "service详情")
    public JBean serviceDetail(@RestParam(name = "platToken") String token, String name) {
        MetaService metaService = MetaKit.getMetaService(name, token);
        Kv detail = MetaKit.buildeDetail(metaService);

        return JBean.by(0, "", detail);
    }

    //修改item的排序
    @RestMapping(name = "itemsort", comment = "字段排序")
    public JBean itemSortSave(String alias, String[] items, @RestParam(name = "platToken") String token) {

        MetaTable metaTable = MetaKit.getMetaTableByAlias(alias);

        MetaKit.sortItem.apply(metaTable, items);

        MetaKit.save(metaTable);
        return JBean.OK;
    }

    @RestMapping(name = "itemupdate", comment = "字段修改")
    public JBean itemUpdate(String alias, List<Field> items, @RestParam(name = "platToken") String token) {
        MetaTable metaTable = MetaKit.getMetaTableByAlias(alias);

        MetaKit.itemUpdate.apply(metaTable, items);
        MetaKit.save(metaTable);
        return JBean.OK;
    }

    @RestMapping(name = "showsort", comment = "展示字段修改")
    public JBean showSort(String name, List<Map<String, String>> shows, @RestParam(name = "platToken") String token) {
        if (shows == null || shows.size() == 0) return null;

        MetaService metaService = MetaKit.getMetaService(name, token);
        metaService.setShows(shows);
        MetaKit.save(metaService);
        return JBean.OK;
    }

    @RestMapping(name = "exportsave", comment = "导出配置保存")
    public JBean exportSave(String name, List<Map<String, String>> exports, @RestParam(name = "platToken") String token) {
        if (exports == null || exports.size() == 0) return null;

        MetaService metaService = MetaKit.getMetaService(name, token);
        metaService.setExports(exports);

        MetaKit.save(metaService);
        return JBean.OK;
    }

    @RestMapping(name = "detailsave", comment = "详情配置保存")
    public JBean detailSave(String name, List<Map<String, String>> details, @RestParam(name = "platToken") String token) {
        if (details == null || details.size() == 0) return null;

        MetaService metaService = MetaKit.getMetaService(name, token);
        metaService.setDetails(details);

        MetaKit.save(metaService);
        return JBean.OK;
    }

    @RestMapping(name = "editsave", comment = "表单配置保存")
    public JBean editSave(String name, List<FromItem> edits, @RestParam(name = "platToken") String token) {
        if (edits == null || edits.size() == 0) return null;

        MetaService metaService = MetaKit.getMetaService(name, token);
        metaService.setEdits(edits);

        MetaKit.save(metaService);
        return JBean.OK;
    }

    @RestMapping(name = "delsave", comment = "删除配置保存")
    public JBean delSave(String name, Map<String, String> dels, @RestParam(name = "platToken") String token) {
        if (dels == null || dels.size() == 0) return null;

        MetaService metaService = MetaKit.getMetaService(name, token);
        metaService.setDels(dels);

        MetaKit.save(metaService);
        return JBean.OK;
    }

    @RestMapping(name = "importsort", comment = "导入字段保存")
    public JBean importSort(String serviceKey, List<String> items, @RestParam(name = "platToken") String token) {
        if (isEmpty(items)) return null;

        MetaTable metaTable = MetaKit.getMetaTable(serviceKey, token);
        //fixme: metaTable.setImports(items);

        MetaKit.save(metaTable);
        return JBean.OK;
    }

    @RestMapping(name = "dbplatupdate", comment = "数据平台修改")
    public JBean dbPlatUpdate(MetaTable metaTable, @RestParam(name = "platToken") String token) {

        MetaTable _metaTable = MetaKit.getMetaTableByKey(metaTable.getKey());
        _metaTable.setComment(metaTable.getComment());
        _metaTable.setCatalog(metaTable.getCatalog());
        _metaTable.setDbPlatId(metaTable.getDbPlatId());
        _metaTable.setCatalog(metaTable.getCatalog());

        MetaKit.save(_metaTable);
        return JBean.OK;
    }

    @RestMapping(name = "filter_update", comment = "查询配置修改")
    public JBean filterUpdate(String name, List<Filter> filters, @RestParam(name = "platToken") String token) {
        MetaService metaService = MetaKit.getMetaService(name, token);
        metaService.setFilters(filters);

        MetaKit.save(metaService);
        return JBean.OK;
    }

    @RestMapping(name = "table_link_list", comment = "实体表，包含link信息的列表,(metalink 管理页面使用)")
    public JBean tableLinkList(@RestParam(name = "platToken") String token) {
        JBean jBean = new JBean();

        List<Kv> list = MetaKit.getMetaTables().stream()
                .filter(x -> (isEmpty(token) || x.getSysPlatId().equals(platId(token))))
                .map(x -> {
                    Kv kv = Kv.of("name", x.getName())
                            .set("comment", x.getComment())
                            .set("alias", x.getAlias())
                            .set("linkCount", 0);

                    //关联表数量
                    long count = MetaKit.getMetaLinks().stream().filter(link -> {
                        String[] tables = link.getTables();
                        return x.getAlias().equals(tables[0]) || x.getAlias().equals(tables[1]);
                    }).count();
                    kv.set("linkCount", count);

                    return kv;
                }).collect(Collectors.toList());

        return jBean.setBody(list);
    }


    @RestMapping(name = "link_info_list", comment = "关联信息")
    public JBean linkInfo(String alias) {
        JBean jBean = new JBean();
        //MetaTable metaTable = MetaKit.getMetaTableByAlias(alias);
        List<Kv> list = MetaKit.getMetaLinks().stream().filter(x ->
                x.getTables()[0].equals(alias) || x.getTables()[1].equals(alias)
        ).map(x -> {
            MetaTable linkTable = MetaKit.getMetaTableByAlias(x.getTables()[0].equals(alias) ? x.getTables()[1] : x.getTables()[0]);
            Kv kv = Kv.of("name", linkTable.getName())
                    .set("alias", linkTable.getAlias())
                    .set("comment", linkTable.getComment())
                    .set("linkSize", x.getLink().size())
                    .set("link", x.getLink());
            return kv;
        }).collect(Collectors.toList());

        return jBean.setBody(list);
    }

    @RestMapping(name = "link_list", comment = "实体关系列表")
    public JBean linkList(String alias) {
        JBean jBean = new JBean();

        List<MetaLink> links = MetaKit.getMetaLinks();

        if (!isEmpty(alias)) {
            links = links.stream()
                    .filter(x -> x.getTables()[0].equals(alias) || x.getTables()[1].equals(alias))
                    .collect(Collectors.toList());
        }

        return jBean.setBody(links);
    }

    @RestMapping(name = "link_save", comment = "实体关系列表")
    public JBean linkSave(MetaLink link, @RestParam(name = "platToken") String token) {
        MetaKit.save(link);
        return JBean.OK;
    }


    @RestMapping(name = "plat_list", comment = "平台列表")
    public JBean platList() {
        JBean jBean = new JBean();
        List<SysPlat> plats = MetaKit.getSysPlats();

        return jBean.setBody(plats);
    }

    @RestMapping(name = "refresh", comment = "刷新服务端缓存数据")
    public JBean refresh() {
        JBean jBean = new JBean();
        MetaKit.init();
        return jBean;
    }

    // ------------------------------------ 对外服务 --------------------------------------
    @RestMapping(name = "cfg", auth = false, comment = " 功能配置")
    public JBean cfg(String name, @RestParam(name = "platToken") String token) {
        JBean jBean = new JBean();
        Map cfg = MetaKit.cfg(name, token);

        return jBean.setBody(cfg);
    }
}
