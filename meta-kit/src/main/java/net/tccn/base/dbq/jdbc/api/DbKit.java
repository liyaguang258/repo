package net.tccn.base.dbq.jdbc.api;

import net.tccn.base.Utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Db 最终执行层
 * Created by liangxianyou at 2019/3/12 14:11.
 */
public class DbKit implements DbSource{

    private DbAccount dbAccount;
    private DbSource dbSource;
    private String catalog;

    /*public DbKit(DbAccount dbAccount) {
        this.dbAccount = dbAccount;
        try {
            DbSource dbSource = X.getDbSource(DbSource.class, dbAccount.getCate());
            dbSource.setDbAccount(dbAccount);

            this.dbSource = dbSource;
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("创建DbKit失败：数据库类型[cate:%s]未知", dbAccount.getCate()));
        }
    }*/
    public DbKit(DbAccount dbAccount, String catalog) {
        this.dbAccount = dbAccount;
        this.catalog = catalog;

        try {
            DbSource dbSource = Utils.getDbSource(DbSource.class, dbAccount.getCate());
            dbSource.setDbAccount(dbAccount);
            dbSource.setCatalog(catalog);

            this.dbSource = dbSource;
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("创建DbKit失败：数据库类型[cate:%s]未知", dbAccount.getCate()));
        }
    }


    @Override
    public void setDbAccount(DbAccount dbAccount) {
        this.dbAccount = dbAccount;
        dbSource.setDbAccount(dbAccount);
    }

    @Override
    public void setCatalog(String catelog) {
        this.catalog = catelog;
        dbSource.setCatalog(catalog);
    }

    @Override
    public <T> List<T> findList(String sql, Class<T> type) {
        return dbSource.findList(sql, type);
    }

    @Override
    public <T> T findFirst(String sql, Class<T> type) {
        return dbSource.findFirst(sql, type);
    }

    @Override
    public <T> T queryColumn(String sql, Class<T> type) {
        return dbSource.queryColumn(sql, type);
    }

    @Override
    public void createTable(String sql) {
        dbSource.createTable(sql);
    }

    @Override
    public void dropTable(String tableName) {
        dbSource.dropTable(tableName);
    }

    public void exetute(String sql) {
        dbSource.exetute(sql);
    }

    // -----------------------------------------
    public <T> CompletableFuture<T> findfirstAsync(String sql, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> findFirst(sql, type));
    }
    public <T> CompletableFuture<List<T>> findListAsync(String sql, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> findList(sql, type));
    }
    public <T> CompletableFuture<T> queryColumnAsync(String sql, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> queryColumn(sql, type));
    }
    public CompletableFuture<Void> exetuteAsync(String sql) {
        return CompletableFuture.runAsync(() -> exetute(sql));
    }

    @Override
    public String getType() {
        return null;
    }
}
