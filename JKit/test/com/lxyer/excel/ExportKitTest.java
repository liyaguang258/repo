package com.lxyer.excel;

import net.tccn.kit.poi.ExcelExportKit;
import net.tccn.kit.poi.ExcelKit;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.Arrays.asList;

/**
 *  以下均为非poi导出使用示例
 * 1、导出List<javabean>示例
 * 2、导出List<Map> 示例
 * 3、javax.servlet web项目中开发使用示例
 * 4、导出压缩的excel示例
 * 5、redkale web项目导出示例
 *
 * Created by liangxianyou at 2018/7/6 10:29.
 */
public class ExportKitTest {

    //1、导出List<javabean>示例
    //@Test
    public void exportBean(){
        Map<String, String> heads = new LinkedHashMap();
        heads.put("姓名", "name");
        heads.put("年龄", "age");
        heads.put("生日", "bir");

        int total = 5_0000;
        List rows = new ArrayList<>(total);
        Random random = new Random();

        for (int i = 0; i < total; i++) {
            String name = UUID.randomUUID().toString();
            int age = random.nextInt(100);
            rows.add(new Person(name, age, new Date()));
        }
        File file = new File("target/user.xls");
        if (file.exists()) file.delete();

        try {
            ExcelExportKit.exportExceltoFile(heads, rows, file, ExcelExportKit.TC.BEAN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //2、导出List<Map> 示例
    //@Test
    public void exportMapToZip(){

        Map<String, String> heads = new LinkedHashMap();
        heads.put("姓名", "name");
        heads.put("姓名1", "name1");
        heads.put("姓名2", "name2");
        heads.put("年龄", "age");

        int total = 5_0000;
        List<Map> rows = new ArrayList<>(total);

        Map r1 = new HashMap();
        r1.put("name", "张三");
        r1.put("age", 12);
        rows.add(r1);

        Random random = new Random();

        //使用uuid 模拟复杂数据导出
        for (int i = 0; i < total; i++) {
            Map r2 = new HashMap();
            r2.put("name", UUID.randomUUID());
            r2.put("name1", UUID.randomUUID());
            r2.put("name2", UUID.randomUUID());
            r2.put("age", random.nextInt(100));
            rows.add(r2);
        }
        File file = new File("target/hello.zip");
        if (file.exists()) file.delete();

        try {
            exportExcelZip(heads, rows, file, ExcelExportKit.TC.MAP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //==================项目中使用示例，实际项目根据实际情况拷贝修改使用========================
    /**
     * 3、javax.servlet web项目中开发使用示例
     * @param head 表头
     * @param rows 数据
     * @param request
     * @param response
     * @param xlsName 导出的文件名称
     * @return
     * @throws IOException
     */
    /*
    public <T> boolean exportExcel(Map head, List<T> rows, HttpServletRequest request, HttpServletResponse response, String xlsName, BiFunction fun) throws IOException {
        if(request.getHeader("user-agent") != null && request.getHeader("user-agent").indexOf("MSIE") != -1) {
            xlsName = java.net.URLEncoder.encode(xlsName,"utf-8") + ".xls";
        } else {
            xlsName = new String(xlsName.getBytes("utf-8"),"iso-8859-1")+ ".xls";
        }

        CountDownLatch latch = new CountDownLatch(187);


        latch.countDown();

        OutputStream os = response.getOutputStream();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment;filename="+xlsName);

        os.write(ExcelExportKit.exportExcel(head, rows, fun).toString().getBytes());
        return true;
    }
    */

    /**
     * 4、导出压缩的excel示例
     * @param head
     * @param rows
     * @param file
     * @param fun
     * @param <T>
     * @throws IOException
     */
    public <T,U,R> void exportExcelZip(Map<String,String> head, List<T> rows, File file, BiFunction<T, U, R> fun) throws IOException {

        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
        BufferedOutputStream bos = new BufferedOutputStream(zos);

        ZipEntry zipEntry = new ZipEntry(file.getName().split("[.]")[0]+".xls");
        zos.putNextEntry(zipEntry);

        StringBuilder buf = ExcelExportKit.exportExcel(head, rows, fun);
        bos.write(buf.toString().getBytes());
        bos.flush();

        bos.close();
        zos.close();
    }

    /**
     * 5、redkale web项目导出示例
     * @return
     * @throws IOException
     */
    /*
    public static <T,U,R> void exportExcel(Map head, List<T> rows, HttpResponse response, String xlsName, BiFunction<T,U,R> fun) throws IOException {

        xlsName = new String(xlsName.getBytes("utf-8"),"iso-8859-1")+ ".xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment;filename="+xlsName);

        StringBuilder buf = ExcelExportKit.exportExcel(head, rows, fun);
        response.finish(buf.toString().getBytes());
    }
    */

    @Test
    public void t() {
        Object t = new Person();
        ((Person) t).setAge(20);
        ((Person) t).setName("'zhangsan'");
        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (field.getType() == String.class) {
                    field.setAccessible(true);
                    String v = (String) field.get(t);
                    v = v.replace("'", "");
                    field.set(t, v);
                    System.out.println(field.getName() + "\t|" + field.getType() + "\t|" + v);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        System.out.println("--------------------------------");

        System.out.println(((Person) t).getName());

        //Field field = fields[0];
/*

            field.getGenericType();


            field.setAccessible(true); //暴力反射
            Object o = field.get(t);//得到字段数据的值
*/


    }

    @Test
    public void t2() {
        Person person = new Person();
        /*person.setName("dsafd''s'f");
        person.setAge(123);*/

        //HashMap<Object, Object> person = new HashMap<>();
        //person.put("name", "sdfdsdfad''sa");


        dealField(person, (s) -> {
            String v = s.replace("'", "");

            return v;
        });

        System.out.println(person.getName());
    }

    /**
     * 对象属性处理，
     * @param o
     * @param function
     * @auther lxyer
     */
    public void dealField(Object o, Function<String, String> function) {
        try {
            Class<?> clazz = o.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == String.class) {
                    field.setAccessible(true);
                    String v = (String) field.get(o);
                    //v = v.replace("'", "");
                    if (v != null) {
                        v = function.apply(v);
                        field.set(o, v);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
