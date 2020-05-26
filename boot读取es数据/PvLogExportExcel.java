package com.zchd.daily;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wind.module.es.EsUtils;
import com.wind.utils.CommonUtils;
import com.wind.utils.RetResult;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Id;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wind.Application.class)
//es日志导出excel
public class PvLogExportExcel {

    @Resource
    private EsUtils esUtils;

    @Test
    public void exportExcel() throws Exception {
        Map<String, Object> para = new HashMap<>();
        para.put("cate", "active");
        //para.put("link_from","");
        List<SearchPvLogRecord> list = queryList(para);
        Field[] fields = SearchPvLogRecord.class.getDeclaredFields();

        File excelFile = new File("C:/Users/whh/Desktop/pvlog.xls");
        if (!excelFile.exists()) excelFile.createNewFile();

        WritableWorkbook book = Workbook.createWorkbook(excelFile);
        WritableSheet sheet = book.createSheet("PvLog", 0);

        //设置表头
        int index = 0;
        for (Field field : fields) {
            sheet.addCell(new Label(index, 0, field.getName()));
            index++;
        }

        //设置数据
        for (int j = 0; j < list.size(); j++) {
            SearchPvLogRecord record = CommonUtils.mapToObject((Map) list.get(j), SearchPvLogRecord.class);

            index = -1;
            for (int i = 0; i < fields.length; i++) {
                index++;

                Field field = fields[i];
                Object val = field.get(record);
                sheet.addCell(new Label(index, j + 1, val.toString()));
            }
        }

        //数据统计
        Map<String, Integer> appMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            SearchPvLogRecord record = CommonUtils.mapToObject((Map) list.get(i), SearchPvLogRecord.class);

            Integer count = appMap.get(record.getLink_from());
            if(count == null) count = 0;
            appMap.put(record.getLink_from(), ++count);
        }

        List<Map<String, Object>> newList = new ArrayList<>();
        appMap.forEach((k, v) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("link_from", k);
            map.put("count", v);
            newList.add(map);
        });
        newList.sort(Comparator.comparing(x -> -(Integer) x.get("count")));

        sheet = book.createSheet("统计", 1);

        index = 0;
        for (Map<String, Object> map : newList) {
            sheet.addCell(new Label(0, index, map.get("link_from").toString()));
            sheet.addCell(new Label(1, index, map.get("count").toString()));
            index++;
        }

        //写入文件
        book.write();
        book.close();

    }


    //查询ES所有记录
    private List<SearchPvLogRecord> queryList(Map<String, Object> para) {
        Function<IPage, IPage<SearchPvLogRecord>> querySupplier = (page) -> {
            RetResult<IPage<SearchPvLogRecord>> list = esUtils.searchDoc(EsUtils.EsEnum.PVLOG_ALL_QUERY, page, para);

            if (!list.isSuccess() || list.getData().getTotal() == 0) {
                return new Page<>();
            }
            return list.getData();
        };

        //默认查询9999条
        IPage flipper = new Page(0, 9999);
        IPage<SearchPvLogRecord> sheet = querySupplier.apply(flipper);
        if (sheet.getTotal() == 0) return new ArrayList<>();

        List<SearchPvLogRecord> arrays = new LinkedList<>(sheet.getRecords());

        //根据分页信息查询剩下的数据
        if (sheet.getTotal() > flipper.getSize()) {
            long num = sheet.getTotal() / flipper.getSize();
            if (sheet.getTotal() % flipper.getSize() > 0) num++;

            for (int i = 1; i < num; i++) {
                flipper.setCurrent(flipper.getCurrent() + 1);
                sheet = querySupplier.apply(flipper);
                arrays.addAll(sheet.getRecords());
            }
        }

        return arrays;
    }


    @Getter
    @Setter
    public static class SearchPvLogRecord {
        @Id
        private String pvid;//[记录ID]uuid
        private int userid;//用户id
        private String targetid = "";//对象id
        private String cate = "";//pv界面
        private String link_from = "";//渠道来源
        private String ip = "";//来源IP
        private Map<String, String> header;//头信息
        private long createtime;//更新时间

    }
}

