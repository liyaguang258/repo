package com.lxyer.excel;

import net.tccn.kit.poi.ExcelExportKit;
import net.tccn.kit.poi.ExcelKit;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * @author: liangxianyou at 2018/9/15 7:52.
 */
public class ExcelKitTest {

    @Test
    public void mapExport(){
        // 数据List<T>  T可以是map，也可以是某个Javabean
        Map m1 = new HashMap();
        m1.put("name", "张三");
        m1.put("age", 12);

        Map m2 = new HashMap();
        m2.put("name", "李四");
        m2.put("age", 11);

        List list = asList(m1, m2);
        // 表头数据 k-v，k：map的数据key，v：表头展示的名称
        LinkedHashMap heads = new LinkedHashMap();
        heads.put("name", "姓名");
        heads.put("age", "年龄");


        // 将List<T> 数据写入到新创建的一个Excel工作薄对象中
        //Workbook workbook = ExcelKit.getWorkbook(new File("target/abx2.xls"));

        Workbook wb = ExcelKit.exportExcel(list, heads);

        try {
            // 存贮数据到文件中
            wb.write(new FileOutputStream(new File("target/abx.xls")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //read excel by fields
    @Test
    public void readTest() throws IOException {
        /*String sql1 = "select a, b, c from Sheet1";
        String sql2 = "select a, b, c from Sheet1 where a=1 order by b";
        String sql3 = "select a, b, c from Sheet1 s1, Sheet2 s2 order by";
        String sql4 = "select a, b, c from Sheet1 s1, Sheet2 s2 where order by";
        String select = sql1.substring(0,sql1.indexOf("from")).replace("select", "").replace(" ", "");

        System.out.println(sql1.substring(0,sql1.indexOf("from")).replace("select", "").replace(" ", ""));
        */

        String[] fields ={"name", "age"};
        File file = new File("target/abx.xls");
        List<Map> list = ExcelKit.readExcel(file, fields);
        System.out.println("list.size: " + list.size());


        list.forEach(x->{
            x.forEach((k,v)->{
                System.out.print(String.format("%s:%s \t", k, v));
            });
            System.out.println();
        });
    }

    //read excel no fields
    @Test
    public void readTest2() throws IOException {
        // 在不传 String[] fields 会把值从左到右分别赋值给 a...z,这样的key上
        List<Map> list = ExcelKit.readExcel(new File("target/abx.xls"));

        list.forEach(x->{
            x.forEach((k,v)->{
                System.out.print(String.format("%s:%s \t", k, v));
            });
            System.out.println();
        });
    }

    //---------------------------  通用导入案例  --------------------------------

    static Properties properties = new Properties();
    static {
        try {
            // 读取导入配置文件
            properties.load(new FileReader(new File("test/resource/import.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建入库语句
     * @param list  数据list<Map>
     * @param heads 数据库需要入库的字段
     * @param table 实体表
     * @return
     */
    private String buildSql(List<Map> list, String[] heads, String table) {
        StringBuilder bufKs = new StringBuilder();
        StringBuilder bufVs = new StringBuilder();

        for (String k : heads) {
            if (!k.isEmpty()) {
                bufKs.append("`" + k + "`,");
            }
        }
        bufKs.deleteCharAt(bufKs.length() - 1);

        list.forEach(m -> {
            bufVs.append("(");
            for (String k : heads) {
                if (!k.isEmpty()) {
                    bufVs.append("'" + m.get(k) + "',");
                }
            }
            bufVs.deleteCharAt(bufVs.length() - 1); //去除最后多余的 逗号
            bufVs.append("),");
        });
        bufVs.deleteCharAt(bufVs.length() - 1); //去除最后多余的 逗号
        return String.format("INSERT INTO %s (%s) VALUES %s;", table, bufKs, bufVs);    // table, ks, vs
    }

    /**
     * 通用数据导入
     */
    @Test
    public void importTest() throws IOException {
        String key = "user";    // 假定使用业务实体表名作为key

        // 获取需要导入的列
        String cfg = properties.getProperty(key);
        String[] heads = cfg.split(",");
        for (int i = 0; i < heads.length; i++) {
            heads[i] = heads[i].trim();
        }
        System.out.println("heads:"+heads.toString());
        // 通过配置读取数据,userData.xlsx为待导入数据
        List<Map> list = ExcelKit.readExcel(new File("test/resource/userData.xlsx"), heads);
        list.remove(0); // 去除多余的行首两行
        list.remove(0);

        System.out.println(list);   // 打印出数据看看是否ok

        // 创建入库语句
        String sql = buildSql(list, heads, key);
        ExcelExportKit.strToFile(sql, new File("tmp/xxx.sql"));
    }

}
