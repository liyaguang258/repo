package net.tccn.kit.poi;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
	下载示例
	private static boolean downloadExcel(Workbook wb, String xlsName, HttpServletRequest request, HttpServletResponse response) throws IOException{
			if(request.getHeader("user-agent").indexOf("MSIE") != -1) {
				xlsName = java.net.URLEncoder.encode(xlsName,"utf-8") + ".xls";
			} else {
			    xlsName = new String(xlsName.getBytes("utf-8"),"iso-8859-1")+ ".xls";
			}
			OutputStream os = response.getOutputStream();
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment;filename="+xlsName);

			wb.write(os);
			return true;
	}

 * 使用poi报表导出工具类
 * 把poi的一个调用接口抽出来，便于导出功能的管理
 * @author LiangXianYou lxy208@126.com
 * @param
 *
 */
public class ExcelKit {

    // 在已有的excel中新增sheet并写入数据
    public static <T> Workbook exportExcel(List<T> list, LinkedHashMap<String, String> headMap, Workbook workbook, String sheetName) {
        return dataToWorkBook(list, headMap, workbook, sheetName);
    }

    public static <T> Workbook exportExcel(List<T> list, LinkedHashMap<String, String> headMap, Workbook workbook) {
        return dataToWorkBook(list, headMap, workbook, null);
    }

    /**
     * Excels导出多个sheet
     *
     * @author LiangXianYou
     * @date 2015-6-16 下午5:56:56
     */
    //map:data,sheetName,hds,hdNames,
    public static <T> Workbook exportExcels(List<Map<String, Object>> list) {

        Workbook wb = new SXSSFWorkbook();            //创建工作薄
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            Sheet sheet = wb.createSheet();                //创建工作表
            String sheetName = (String) map.get("sheetName");
            wb.setSheetName(i, sheetName);
            //写入表头---Excel的第一行数据
            Row nRow = sheet.createRow(0);                //创建行
            String[] hdNames = (String[]) map.get("hdNames");
            for (int j = 0; j < hdNames.length; j++) {
                Cell nCell = nRow.createCell(j);            //创建单元格
                nCell.setCellValue(hdNames[j]);
            }

            //写入每一行数据---一条记录就是一行数据
            @SuppressWarnings("unchecked")
            List<T> data = (List<T>) map.get("data");
            String[] hds = (String[]) map.get("hds");
            dataToSheet(sheet, data, hds, 1);
        }

        return wb;
    }

    /**
     * 通过数据构建 Workbook 对象
     * @param list    数据
     * @param headMap 每一列的字段名和对应的表头名称 如：{name:"姓名"， age:"年龄"}
     * @param <T>     数据泛型，支持javaBean 或Map
     * @return
     * @throws Exception
     * @author LiangXianYou
     * @date 2015-3-13 上午11:00:30
     */
    public static <T> Workbook exportExcel(List<T> list, LinkedHashMap<String, String> headMap) {
        return dataToWorkBook(list, headMap, null, null);
    }

    private static <T> Workbook dataToWorkBook(List<T> list, LinkedHashMap<String, String> headMap, Workbook wb, String sheetName) {
        if (wb == null) {
            wb = new SXSSFWorkbook();
        }

        Sheet sheet = sheetName != null && !sheetName.isEmpty() ?
                wb.createSheet(sheetName) : wb.createSheet();

        String[] hdNames = new String[headMap.size()]; //
        String[] hds = new String[headMap.size()];

        int[] tag = {0};
        headMap.forEach((k,v) -> {
            hds[tag[0]] = k;
            hdNames[tag[0]] = v;
            tag[0] ++;
        });

        Row nRow = sheet.createRow(0);
        for (int i = 0; i < hdNames.length; i++) {
            Cell nCell = nRow.createCell(i);
            nCell.setCellValue(hdNames[i]);
        }

        // 写入每一条记录
        dataToSheet(sheet, list, hds, 1);
        return wb;
    }

    /**
     * 通过泛型实例对象得到某一字段值
     *
     * @author LiangXianYou
     * @date 2015-3-13 上午10:53:32
     */
    private static <T> Object getFieldValue(T t, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Object v = null;

        if (t == null) {
            v = null;
        } else if (t instanceof Map) {
            v = ((Map) t).get(fieldName);
        } else {
            Field field = t.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            v = field.get(t);
        }

        return v;
    }

    /**
     * 将数据写到Excel中
     *
     * @author LiangXianYou
     * @date 2015-3-13 上午10:37:55
     */
    private static <T> void dataToSheet(Sheet sheet, List<T> list, String[] hds, int skipRow) {
        if (list == null || list.size() == 0) return;

        for (int j = 0; j < list.size(); j++) {
            for (int k = 0; k < hds.length; k++) {
                Row nRow = sheet.getRow(j + skipRow);
                if (nRow == null) {
                    nRow = sheet.createRow(j + 1);
                }
                Cell cell = nRow.createCell(k);

                try {
                    Object v = getFieldValue(list.get(j), hds[k]);    //得到列的值
                    dataToCell(cell, v);                    //将值写入Excel
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void dataToCell(Cell cell, Object v) {
        //根据不同类型进行转化，如有其它类型没有考虑周全的，使用发现的时候添加
        if (v instanceof Integer) cell.setCellValue((Integer) v);
        else if (v instanceof Double) cell.setCellValue((Double) v);
        else if (v instanceof Float) cell.setCellValue((Float) v);
        else if (v instanceof String) cell.setCellValue((String) v);
        else if (v instanceof Date) cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(v));
        else if (v instanceof Calendar) cell.setCellValue((Calendar) v);
        else if (v instanceof Boolean) cell.setCellValue((Boolean) v);
        else if (v == null) cell.setCellValue("");
        else cell.setCellValue(v + "");
    }

    //======================= 读取excel ===============================

    //read excel head
    public static Map readExcelHead(File file, String[] fields) {
        List<Map> list = readExcel(file, fields, 1, null);
        return list.size() > 0 ? list.get(0) : new HashMap();
    }

    //read excel head by sheetName
    public static Map readExcelHead(File file, String[] fields, String sheetName) {
        List<Map> list = readExcel(file, fields, 1, sheetName);
        return list.size() > 0 ? list.get(0) : new HashMap();
    }

    //read excel sheet[0]
    public static List<Map> readExcel(File file, String[] fields) {
        return readExcel(file, fields, -1, null);
    }

    //read excel sheet[0] no fields
    public static List<Map> readExcel(File file) {
        return readExcel(file, null, -1, null);
    }

    //read excel by sheetName
    public static List<Map> readExcel(File file, String[] fields, String sheetName) {
        return readExcel(file, fields, -1, sheetName);
    }

    //read excel by sheetName no fields
    public static List<Map> readExcel(File file, String sheetName) {
        return readExcel(file, null, -1, sheetName);
    }

    //read excel all sheet
    public static Map<String, List<Map>> readExcelAll(File file, String[] fields) {
        return readExcelAll(file, fields, -1);
    }

    //read all excel no fields
    public static Map<String, List<Map>> readExcelAll(File file) {
        return readExcelAll(file, null, -1);
    }

    //read excel sheet[0]
    private static List<Map> readExcel(File file, String[] fields, int lastRowNum, String sheetName) {
        Workbook wk = getWorkbook(file);
        Sheet sheet = sheetName == null ? wk.getSheetAt(0) : wk.getSheet(sheetName);
        if (sheet == null) throw new OfficeXmlFileException("sheet[" + sheetName + "] can't findList");

        if (lastRowNum < 0 || lastRowNum > sheet.getLastRowNum()) {
            lastRowNum = sheet.getLastRowNum();
        }
        return readExcel(sheet, fields, lastRowNum);
    }

    /**
     * read excel all sheet,
     * can read excel 2007+ and 2007-
     *
     * @param file
     * @param fields
     * @param lastRowNum
     * @return
     * @throws IOException
     */
    private static Map<String, List<Map>> readExcelAll(File file, String[] fields, int lastRowNum) {
        Workbook wk = getWorkbook(file);

        Map<String, List<Map>> data = new LinkedHashMap<>();
        //reading all sheet
        for (int i = 0; i < wk.getNumberOfSheets(); i++) {
            Sheet sheet = wk.getSheetAt(i);
            List<Map> maps = readExcel(sheet, fields, lastRowNum);

            //move head
            /*if (lastRowNum < 0 && maps.size() > 0) {
                maps.remove(0);
            }*/
            data.put(sheet.getSheetName(), maps);
        }
        return data;
    }

    /**
     * read excel
     *
     * @author Lxyer 2016/8/1 10:32.
     */
    private static List<Map> readExcel(Sheet sheet, String[] fields, int lastRowNum) {
        if (lastRowNum < 0 || lastRowNum > sheet.getLastRowNum()) {
            lastRowNum = sheet.getLastRowNum();
        }

        if (fields == null || fields.length == 0) {
            return readExcel(sheet, lastRowNum);
        }

        List<Map> list = new ArrayList<>();
        int t = 0;
        r:for (int i = 0; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            short cellNum = row.getLastCellNum();
            //空跳过/连续三行为空结束
            if (isEmptyRow(row, fields.length)) {
                if (t++ > 3) break;
                continue;
            }

            Map map = new HashMap();
            for (int j = 0; j < cellNum && j < fields.length; j++) {
                if (fields[j] == null || "".equals(fields[j])) {
                    continue;
                }
                Cell cell = row.getCell(j);
                if (cell == null) {
                    map.put(fields[j], "");
                    continue;
                }

                if (cell.getCellType() == CellType.NUMERIC) {
                    map.put(fields[j], cell.getNumericCellValue() + "");
                } else {
                    map.put(fields[j], cell.getStringCellValue());
                }
            }
            list.add(map);
        }
        return list;
    }

    /**
     * @author Lxyer 2016/9/21 00:38.
     */
    private static List<Map> readExcel(Sheet sheet, int lastRowNum) {
        List<Map> list = new ArrayList<>();
        if (lastRowNum < 0 || lastRowNum > sheet.getLastRowNum()) {
            lastRowNum = sheet.getLastRowNum();
        }

        int t = 0;
        r:
        for (int i = 0; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            short cellNum = row.getLastCellNum();
            //空跳过/连续三行为空结束
            if (isEmptyRow(row, 3)) {
                if (t++ > 3) break;
                continue;
            }

            Map map = new HashMap();
            for (int j = 0; j < cellNum; j++) {
                String field = (char) ((j / 26 == 0 ? ' ' : 'a') + j / 26) + "" + (char) ('a' + j % 26);
                field = field.replace(" ", "");
                Cell cell = row.getCell(j);
                if (cell == null) {
                    map.put(field, "");
                    continue;
                }

                if (cell.getCellType() == CellType.NUMERIC) {
                    map.put(field, cell.getNumericCellValue() + "");
                } else {
                    map.put(field, cell.getStringCellValue());
                }
            }
            list.add(map);
        }
        return list;
    }

    //空跳过/连续三行为空结束
    private static boolean isEmptyRow(Row row, int len) {
        for (int i = 0; i < row.getLastCellNum() && i < len; i++) {
            Cell cell = row.getCell(i);//列
            if (cell != null) {
                if (cell.getCellType() != CellType.NUMERIC && cell.getStringCellValue() != null && !cell.getStringCellValue().isEmpty()) {
                    return false;
                } else if (cell.getCellType() == CellType.NUMERIC && cell.getNumericCellValue() != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    //get all sheet names
    public static List<String> getSheetNames(File file) {
        Workbook wk = getWorkbook(file);
        List<String> sheetNames = new ArrayList<>();
        for (int i = 0; i < wk.getNumberOfSheets(); i++) {
            sheetNames.add(wk.getSheetAt(i).getSheetName());
        }
        return sheetNames;
    }

    public static Workbook getWorkbook(File file) {
        try {
            return WorkbookFactory.create(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //dev
    public void setWorkbook(File file) {

    }

}
