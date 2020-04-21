package net.tccn.base.dbq.jdbc.api;

import net.tccn.base.IService;

import java.util.Date;
import java.util.List;

/**
 * Created by liangxianyou at 2019/3/12 14:07.
 */
public interface DbSource extends IService {

    void setDbAccount(DbAccount dbAccount);
    void setCatalog(String catelog);

    <T> List<T> findList(String sql, Class<T> type);

    <T> T findFirst(String sql, Class<T> type);

    <T> T queryColumn(String sql, Class<T> type);

    //待实现
    default <T> void save(String tableName, T t) {}

    //待实现
    default <T> void update(String tableName, T t) {}

    default int queryInt(String sql) {
        return queryColumn(sql, int.class);
    }
    default long queryLong(String sql) {
        return queryColumn(sql, long.class);
    }
    default double queryDouble(String sql) {
        return queryColumn(sql, double.class);
    }
    default Date queryDate(String sql) {
        return queryColumn(sql, Date.class);
    }

    void createTable(String sql);
    void dropTable(String tableName);

    void exetute(String sql);
}
