import org.redkale.convert.json.JsonConvert;
import org.redkale.util.TypeToken;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

//获取es记录条数
public class GetEsResultCount {

    public static void main(String[] args) {
        String esResult = readFileAsString("C:\\Users\\wh\\Desktop\\yunying\\esResult.txt");
        String esLastWeekReg = readFileAsString("C:\\Users\\wh\\Desktop\\yunying\\esLastWeekReg.txt");
        SearchResult<SearchVisLogRecord> temp = JsonConvert.root().convertFrom((new TypeToken<SearchResult<SearchVisLogRecord>>() {
        }).getType(), esResult);
        Collection<SearchVisLogRecord> esResultList = temp.getSheet().getRows();

//        List<SearchVisLogRecord> esResultList = searchResult.getSheet(new Page()).getRecords();
        List<Integer> esLastWeekRegList = Arrays.asList(esLastWeekReg.split(",")).stream().map(x -> Integer.parseInt(x.trim())).collect(Collectors.toList());

        System.out.println("周活跃用户总数：" + esResultList.size());
        System.out.println("前一周注册用户数：" + esLastWeekRegList.size());
        System.out.println("前一周注册本周活跃用户数：" + esResultList.stream().filter(x -> {
            return esLastWeekRegList.contains(x.getUserid());
        }).count());
    }

    public static List<Map<String, Object>> stringToObject(String str) {
        return JsonConvert.root().convertFrom((new TypeToken<ArrayList<Map<String, Object>>>() {
        }).getType(), str);
    }

    //文件读取成String
    public static String readFileAsString(String path) {
        File file = new File(path, "");
        if (!file.exists()) return "";

        StringBuffer b = new StringBuffer();
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            while (true) {
                int ch = r.read();
                if (ch == -1) {
                    break;
                }
                b.append((char) ch);
            }
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b.toString();
    }

}
