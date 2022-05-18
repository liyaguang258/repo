import net.tccn.base.dbq.jdbc.api.DbAccount;
import net.tccn.base.dbq.jdbc.api.DbKit;
import org.junit.Test;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class SqliteTest {

   /* public static void main( String args[] )
    {
        Connection c = null;
        try {
            // 0 连接SQLite的JDBC
            Class.forName("org.sqlite.JDBC");
//            dbAccount.setUrl("jdbc:mysql://192.168.202.11:3306/gxbii_dev");
            c = DriverManager.getConnection("jdbc:sqlite://47.94.94.185:1433/usr/local/sqlite3/databases/userinfo.db","root","Lyg#1579624%");
            System.out.println("Opened database successfully");
            Statement stat = c.createStatement();
            ResultSet rs = stat.executeQuery( "SELECT * FROM COMPANY;" );
            while ( rs.next() ) {
                int id = rs.getInt("id");
                String  name = rs.getString("name");
                int age  = rs.getInt("age");
                String  address = rs.getString("address");
                float salary = rs.getFloat("salary");
                System.out.println( "ID = " + id );
                System.out.println( "NAME = " + name );
                System.out.println( "AGE = " + age );
                System.out.println( "ADDRESS = " + address );
                System.out.println( "SALARY = " + salary );
                System.out.println();
            }
            stat.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");

    }

    @Test
    public void findList() {
        DbAccount dbAccount = new DbAccount();
//        dbAccount.setCate("sqlite3");
        dbAccount.setUrl("jdbc:sqlite:userinfo.db");
        dbAccount.setUser("root");
        dbAccount.setPwd("Lyg#1579624%");

        DbKit dbKit = new DbKit(dbAccount, "userinfo");

        String sql = "show databases;";

        // find list
        List<Map> list = dbKit.findList(sql, Map.class);
        System.out.println(list.get(0));
    }


    @Test
    public void findListxxx() {
//        QSqlDatabase db =QSqlDatabase::addDatabase("QSQLITE");
        Connection c = null;
        try {
            // 0 连接SQLite的JDBC
//            Class.forName("org.sqlite.JDBC");
            Class.forName("org.sqlite.NativeDB");
//            c = DriverManager.getConnection("jdbc:sqlite://47.94.94.185/usr/local/sqlite3/databases/userinfo.db","root","Lyg#1579624%");
            c = DriverManager.getConnection("jdbc:sqlite://47.94.94.185/usr/local/sqlite3/databases/20220329test.db","root","Lyg#1579624%");
            System.out.println("Opened database successfully");
            Statement stat = c.createStatement();
            ResultSet rs = stat.executeQuery( "SELECT * FROM COMPANY;" );
            while ( rs.next() ) {
                int id = rs.getInt("id");
                String  name = rs.getString("name");
                int age  = rs.getInt("age");
                String  address = rs.getString("address");
                float salary = rs.getFloat("salary");
                System.out.println( "ID = " + id );
                System.out.println( "NAME = " + name );
                System.out.println( "AGE = " + age );
                System.out.println( "ADDRESS = " + address );
                System.out.println( "SALARY = " + salary );
                System.out.println();
            }
            stat.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }*/
}
