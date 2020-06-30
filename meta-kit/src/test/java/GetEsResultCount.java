
import net.tccn.base.Kv;

import net.tccn.base.TplKit;
import org.junit.Test;
import org.redkale.convert.json.JsonConvert;
import org.redkale.service.RetResult;
import org.redkale.source.Flipper;
import org.redkale.util.Sheet;
import org.redkale.util.TypeToken;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

//获取es记录条数
public class GetEsResultCount {

    public static void main(String[] args) {
        String esResult = readFileAsString("C:\\Users\\wh\\Desktop\\yunying\\esResult.txt");
        String esLastWeekReg = readFileAsString("C:\\Users\\wh\\Desktop\\yunying\\esLastWeekReg.txt");
        SearchResult<SearchVisLogRecord> temp = JsonConvert.root().convertFrom((new TypeToken<SearchResult<SearchVisLogRecord>>() {
        }).getType(), esResult);
        Collection<SearchVisLogRecord> esResultList = temp.getSheet().getRows();

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

    public TplKit tplKit = TplKit.use("", true);
    @Resource(name = "property.es.path")
    protected String esPath;

    //查询ES所有记录
    public List<SearchVisLogRecord> queryAllVisit(Kv<String, Object> kv, String tplName) {
        SearchEnum searchEnum = SearchEnum.VISLOG;
        if (tplName == null) tplName = searchEnum.findTplName;

        String finalTplName = tplName;
        Function<Flipper, Sheet<SearchVisLogRecord>> querySupplier = (flipper) -> {
            RetResult<Sheet<SearchVisLogRecord>> vislogList = searchDoc(searchEnum
                    , finalTplName, flipper, kv);
            if (!vislogList.isSuccess() || vislogList.getResult().getTotal() == 0) {
                return new Sheet<>();
            }
            return vislogList.getResult();
        };

        //默认查询9999条
        Flipper flipper = new Flipper(999);
        Sheet<SearchVisLogRecord> sheet = querySupplier.apply(flipper);
        if (sheet.isEmpty()) return new ArrayList<>();

        List<SearchVisLogRecord> arrays = new LinkedList<>(sheet.getRows());

        //根据分页信息查询剩下的数据
        if (sheet.getTotal() > flipper.getLimit()) {
            long num = sheet.getTotal() / flipper.getLimit();
            if (sheet.getTotal() % flipper.getLimit() > 0) num++;

            for (int i = 1; i < num; i++) {
                flipper.next();
                sheet = querySupplier.apply(flipper);
                arrays.addAll(sheet.getRows());
            }
        }

        return arrays;
    }

    public <T> RetResult<Sheet<T>> searchDoc(SearchEnum searchEnum, String tplName, Flipper flipper, Kv para) {
        HttpClient client = HttpClient.newBuilder().build();
        para.put("from", flipper.getOffset());
        para.put("size", flipper.getLimit());

//        String tpl = getTpl(searchEnum.findTplName, para);
        String tpl = tplKit.getTpl(tplName, para);
//        logger.info(tpl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(esPath + String.format("/%s/_doc/_search", searchEnum.esIndex)))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(tpl))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
//            return RetCodes.retResult(-1, "关键字搜索失败");
        }
//        logger.info(response.body());
        Type type = searchEnum.typeToken.getType();
        SearchResult<T> esResult = JsonConvert.root().convertFrom(type, response.body());
        Sheet<T> sheet = esResult.getSheet();
        return RetResult.success(sheet);
    }

    @Test
    public void getvislog() {
        Kv<String, Object> kv = new Kv<>();
        kv.put("starttime", 1592755200000l);
        kv.put("endtime", 1593360000000l);
        List<SearchVisLogRecord> searchVisLogRecords = queryAllVisit(kv, "vislog.7DaysActiveCount");
        System.out.println(searchVisLogRecords.size());
    }
}
