package net.tccn.plat;

import net.tccn.base.BaseService;
import net.tccn.base.JBean;
import net.tccn.base.Kv;
import net.tccn.base.MetaKit;
import net.tccn.base.dbq.jdbc.api.DbAccount;
import net.tccn.base.dbq.jdbc.api.DbKit;
import net.tccn.base.dbq.table.Column;
import net.tccn.base.dbq.table.Table;
import net.tccn.meta.MetaTable;
import org.redkale.net.http.RestMapping;
import org.redkale.net.http.RestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@RestService(automapping = true, comment = "数据库操作类")
public class _DbService extends BaseService {

    @RestMapping(name = "catalog_list", comment = "获取数据源的database")
    public JBean catalogList(DbAccount dbAccount, String dbPlatId) {
        JBean jBean = new JBean();
        DbKit dbKit = null;
        if (dbAccount != null) {
            dbKit = new DbKit(dbAccount, "");
        } else {
            dbKit = MetaKit.getDbKit(dbPlatId, "");
        }

        List<Map> list = dbKit.findList("SHOW DATABASES;", Map.class);

        Stream<String> database = list.stream().map(x -> String.valueOf(x.get("Database")));

        return jBean.setBody(database.toArray());
    }

    @RestMapping(name = "table_list", comment = "数据库表列表")
    public List<Table> tableList(String dbPlatId, String catalog, String[] tables) {
        DbKit dbKit = MetaKit.getDbKit(dbPlatId, "");

        String sql = tplKit.getTpl("db.table_list", Kv.of("catalog", catalog).set("tables", tables));
        return dbKit.findList(sql, Table.class);
    }

    @RestMapping(ignore = true)
    public List<Table> tableInfoList(String dbPlatId, String catalog, String[] tables) {
        List<Table> list = new ArrayList<>(tables.length);
        for (String table : tables) {
            list.add(tableInfo(dbPlatId, catalog, table));
        }
        return list;
    }

    @RestMapping(name = "table_info", comment = "数据库表详情")
    public JBean MetatableInfo(String dbPlatId, String catalog, String tableName) {
        JBean jBean = new JBean();
        try {
            Table table = tableInfo(dbPlatId, catalog, tableName);

            jBean.setBody(MetaTable.toAs(table));
        } catch (Exception e) {
            jBean.set(-1, "查询表信息失败");
            new IllegalArgumentException("查询表信息失败", e);
        }
        return jBean;
    }

    @RestMapping(ignore = true, comment = "查询表信息")
    public Table tableInfo(String dbPlatId, String catalog, String tableName) {
        DbKit dbKit = MetaKit.getDbKit(dbPlatId, catalog);

        String sql = tplKit.getTpl("db.table_list", Kv.of("table", tableName));
        String columnSql = String.format("SHOW FULL COLUMNS FROM %s.`%s`", catalog, tableName);

        CompletableFuture<Table> tableFuture = dbKit.findfirstAsync(sql, Table.class);
        CompletableFuture<List<Column>> columnFuture = dbKit.findListAsync(columnSql, Column.class);

        try {
            Table table = tableFuture.get();
            table.setColumns(columnFuture.get());

            return table;
        } catch (InterruptedException | ExecutionException e) {
            new IllegalArgumentException("查询表信息失败", e);
        }
        return null;
    }

    @RestMapping(name = "table_create", comment = "新建表[mysql]")
    public JBean tableCreate(String dbPlatId, String catalog, String sql) {
        JBean jBean = new JBean();

        DbKit dbKit = MetaKit.getDbKit(dbPlatId, catalog);
        dbKit.createTable(sql);

        return jBean;
    }
}
