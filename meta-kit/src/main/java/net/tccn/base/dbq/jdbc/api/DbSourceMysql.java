package net.tccn.base.dbq.jdbc.api;

import net.tccn.base.CfgException;
import net.tccn.base.Kv;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liangxianyou at 2019/3/12 14:20.
 */
public class DbSourceMysql implements DbSource {

    private static ConcurrentHashMap<String, LinkedBlockingQueue<Connection>> conns = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, AtomicInteger> counter = new ConcurrentHashMap<>();

    private String accountKey;
    private DbAccount dbAccount;
    private String catalog;

    public DbSourceMysql() {

    }

    public void setDbAccount(DbAccount dbAccount) {
        this.dbAccount = dbAccount;
        this.accountKey = dbAccount.accountKey();
    }

    @Override
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public DbSourceMysql(DbAccount dbAccount) {
        this.dbAccount = dbAccount;
        this.accountKey = dbAccount.accountKey();
    }
    public DbSourceMysql(DbAccount dbAccount, String catalog) {
        this.dbAccount = dbAccount;
        this.catalog = catalog;
        this.accountKey = dbAccount.accountKey();
    }

    @Override
    public String getType() {
        return "mysql";
    }

    @Override
    public <T> List<T> findList(String sql, Class<T> type) {
        Connection connection = connection();
        try (
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            List list = new ArrayList();
            while (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int count = metaData.getColumnCount();

                Map row = new HashMap();
                for (int i = 1; i <= count; i++) {
                    String columnTypeName = metaData.getColumnTypeName(i);
                    //String columnName = metaData.getColumnName(i);
                    String columnLabel = metaData.getColumnLabel(i);
                    row.put(columnLabel, null);

                    if (rs.getObject(i) != null) {
                        switch (columnTypeName) {
                            case "DATETIME":
                            case "TIMESTAMP":
                            case "DATE":
                                row.put(columnLabel, rs.getTimestamp(i).getTime()); break;
                            default:
                                row.put(columnLabel, rs.getObject(i));
                        }
                    }
                }
                list.add(Map.class == type ? row : Kv.toBean(row, type));
            }

            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            release(connection);
        }
    }

    @Override
    public <T> T findFirst(String sql, Class<T> type) {
        List<T> list = findList(sql, type);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public <T> T queryColumn(String sql, Class<T> type) {
        Connection connection = connection();
        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            Object v = null;
            while (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int count = metaData.getColumnCount();

                for (int i = 1; i <= count; i++) {
                    String columnTypeName = metaData.getColumnTypeName(i);
                    if (rs.getObject(i) != null) {
                        switch (columnTypeName) {
                            case "DATETIME":
                            case "TIMESTAMP":
                            case "DATE":
                                v = rs.getTimestamp(i).getTime(); break;
                            default:
                                v = rs.getObject(i);
                        }
                    }
                }
            }

            return Kv.toAs(v, type);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            release(connection);
        }
    }

    @Override
    public void createTable(String sql) {
        new RuntimeException("DbSourceMysql.createTable NOT SUPPORT right now" ); // todo:
    }

    @Override
    public void dropTable(String tableName) {
        new RuntimeException("[DbSourceMysql.dropTable] NOT SUPPORT right now" ); // todo:
    }

    @Override
    public void exetute(String sql) {
        Connection connection = connection();
        try (
                PreparedStatement ps = connection.prepareStatement(sql);
        ){
            ps.execute();
            //ps.executeUpdate();
        } catch (SQLException e) {
            throw new CfgException("SQL 执行失败：", sql);
        } finally {
            release(connection);
        }
    }

    //fixme: lxy 处理连接超过8小时失效问题
    private Connection connection() {
        Connection connection = connection(0);
        if (connection != null && catalog != null && !catalog.isEmpty()) {
            try {
                connection.setCatalog(catalog); //还回连接的时候是否需要重置catalog？ 后续观察
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
    private Connection connection(int n) {
        LinkedBlockingQueue<Connection> queue = conns.getOrDefault(accountKey, new LinkedBlockingQueue<>(15));

        Connection conn = null;
        AtomicInteger num = counter.getOrDefault(accountKey, new AtomicInteger(0));
        try {
            if (queue.size() == 0 && num.get() < 15) {
                conn = DriverManager.getConnection(dbAccount.getUrl(), dbAccount.getUser(), dbAccount.getPwd());
                int x = num.incrementAndGet();
                counter.put(accountKey, num);
                System.out.println("创建新的连接:" + x);
            } else {
                conn = queue.take();
                if (conn.isClosed()) {
                    System.out.println("connetion had closed,");
                    conn = connection(n);
                }
            }
        } catch (SQLException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                try {
                    conn = DriverManager.getConnection(dbAccount.getUrl(), dbAccount.getUser(), dbAccount.getPwd());
                    num.getAndIncrement();
                    if (conn != null) {
                        System.out.println("获取连接异常，并重新创建成功");
                    }
                } catch (SQLException ex) {
                    new IllegalArgumentException("创建连接失败", e);
                }
                num.getAndIncrement();
                counter.put(accountKey, num);
            } else {
                new IllegalArgumentException("获取连接失败", e);
            }
        }
        conns.put(accountKey, queue);
        return conn;
    }
    private void release(Connection connection) {
        LinkedBlockingQueue<Connection> queue = conns.getOrDefault(accountKey, new LinkedBlockingQueue<>(15));
        try {
            if (connection != null) {
                queue.put(connection);
                conns.put(accountKey, queue);
                //System.out.println("还回连接：" + connection);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
