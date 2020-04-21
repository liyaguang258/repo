package net.tccn.plat;

import net.tccn.base.BaseService;
import net.tccn.base.JBean;
import net.tccn.base.Kv;
import net.tccn.base.MetaKit;
import net.tccn.base.dbq.table.Table;
import net.tccn.file._FileService;
import net.tccn.meta.MetaTable;
import org.redkale.net.http.RestMapping;
import org.redkale.net.http.RestParam;
import org.redkale.net.http.RestService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 元数据实体管理
 * 1、实体导入，
 * 2、实体信息维护
 * 3、数据库表管理
 */
@RestService(automapping = true, comment = "元数据实体管理")
public class _TableService extends BaseService {

    @Resource
    private _FileService fileService;
    @Resource
    private _DbService dbService;


    @RestMapping(name = "sheets", comment = "导入选择列表数据准备")
    public JBean sheets(String cate, //类型  excel|mysql
                        // excel
                        String filePath,
                        //mysql {数据库连接账号、数据源id、数据库database数组}
                        String dbPlatId, String catalog,
                        @RestParam(name = "platToken") String token) {

        JBean jBean = new JBean();

        if ("excel".equals(cate)) {
            jBean = fileService.data(filePath, token);

        } else if ("mysql".equals(cate)) {
            List<Table> list = dbService.tableList(dbPlatId, catalog, null);

            String[] tableArr = list.stream().map(Table::getName).toArray(String[]::new);
            List<String> hv = MetaKit.tableExist(tableArr, token);

            List<MetaTable> sheets = new ArrayList<>();
            list.forEach(x -> {
                MetaTable bean = MetaTable.toAs(x);
                bean.setHv(hv.contains(x.getName()) ? 1 : 0);
                sheets.add(bean);
            });

            //对数据分组后返回
            Kv<String, MetaTable> data = Kv.of();
            sheets.forEach(x -> data.set(x.getName(), x));

            jBean.setBody(data);
        }

        return jBean;
    }

    @RestMapping(name = "sheet_info", comment = "sheet详情")
    public JBean sheetInfo(String cate, @RestParam(name = "platToken") String token,
                           // excel
                           String filePath, String sheetName,
                           // mysql
                           String dbPlatId, String catalog, String tableName) {
        if ("excel".equals(cate)) {
            return fileService.sheetData(filePath, sheetName, token);
        } else if ("mysql".equals(cate)) {
            return new JBean().setBody(dbService.tableInfo(dbPlatId, catalog, tableName));
        }
        return null;
    }


    @RestMapping(name = "table_save", comment = "保存数据源实体到元数据实体表")
    public JBean tableSave(String cate, @RestParam(name = "platToken") String token,
                           String filePath, String[] sheetNames,
                           String dbPlatId, String catalog, String[] tableArr) {

        if ("excel".equals(cate)) {
            return fileService.saveSheet(sheetNames, filePath, token);
        } else if ("mysql".equals(cate)) {
            List<String> hv = MetaKit.tableExist(tableArr, token);
            List<Table> tables = dbService.tableInfoList(dbPlatId, catalog, tableArr);

            MetaTable[] metaTables = tables.stream()
                    .filter(t -> !hv.contains(t.getName())) // 去除同名
                    .map(t -> {
                        MetaTable metaTable = MetaTable.toAs(t);
                        metaTable.setCatalog(catalog);
                        metaTable.setDbPlatId(dbPlatId);
                        metaTable.setAlias(MetaKit.nextAlias());// 表别名
                        metaTable.setSysPlatId(platId(token));
                        return metaTable;
                    }).toArray(MetaTable[]::new);

            MetaKit.save(metaTables);

            // 已经有的表 更新
            MetaTable[] metaTables2 = tables.stream()
                    .filter(t -> hv.contains(t.getName())) // 去除同名
                    .map(t -> {
                        MetaTable table = MetaKit.getMetaTable(t.getName(), token);

                        MetaTable metaTable = MetaTable.toAs(t);

                        table.setCatalog(catalog);
                        table.setDbPlatId(dbPlatId);
                        table.setAlias(table.getAlias());// 表别名
                        table.setSysPlatId(platId(token));

                        table.setItems(metaTable.getItems());
                        table.setComment(metaTable.getComment());
                        return table;
                    }).toArray(MetaTable[]::new);

            MetaKit.save(metaTables2);
        }
        return JBean.OK;
    }
}
