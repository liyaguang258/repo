package x;

import net.tccn.kit.poi.ExcelKit;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Demo {

    public static void main(String[] args) {
//        List<Map> maps = ExcelKit.readExcel(new File("tmp/媒体评分数据.xlsx"));
        String[] FIELDS = {"mediaid", "gameid", "avgscore", "totalscore", "scorelink", "remark"};
        List<Map> maps = ExcelKit.readExcel(new File("tmp/媒体评分数据.xlsx"),FIELDS);
        System.out.println(maps);
    }
}
