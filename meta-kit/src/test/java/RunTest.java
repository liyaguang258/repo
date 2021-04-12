import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONObject;
import net.tccn.base.*;
import net.tccn.base.dbq.fbean.FBean;
import net.tccn.base.dbq.jdbc.api.DbAccount;
import net.tccn.base.dbq.jdbc.api.DbKit;
import net.tccn.base.dbq.parser.ParseMysql;
import net.tccn.dict.Dict;
import net.tccn.dict.DictKit;
import net.tccn.meta.MetaService;
import net.tccn.meta.MetaTable;
import net.tccn.qtask.TaskEntity;
import net.tccn.qtask.TaskKit;
import net.tccn.user.User;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Test;
import org.redkale.convert.json.JsonConvert;
import org.redkale.source.CacheMemorySource;
import org.redkale.util.TypeToken;
import org.redkale.util.Utility;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * @author: liangxianyou at 2019/1/20 12:43.
 */
public class RunTest<T> {
    /*static {
        MetaKit.init();
    }*/

    JsonConvert convert = JsonConvert.root();

    /*public static Task A = new Task("mysql", "select * from user where userid=#(userid)", "查询用户列表", Kv.of("userid", 1));
    public static Task B = new Task("method", "User.say", "user调用", Kv.of("name", "张三").set("age", 13));
    public static Task C = new Task("http", "http://127.0.0.1/meta/db_plat_list?platToken=3421432", "查询数据平台列表", Kv.of("abx", "abx111"));
    public static Task d = new Task("es", "http://192.168.91.5:9200/_sql?", "查询数据平台列表", Kv.of("sql", "select * from  basic_iotdevice_all limit 10"));
    public static Task e = new Task("http", "http://192.168.91.5:9200/_sql?sql=select%20*%20from%20basic_iotdevice_all%20limit%2010", "查询数据平台列表", Kv.of());
*/

    //@Test
    /*public void qtaskTest() {
        long start = System.currentTimeMillis();
        Object query = QRuner.query(d);
        System.out.printf("耗时：%s MS" ,System.currentTimeMillis() - start);
        System.out.println();

        System.out.println(query);


        //System.out.println(query.getClass());
    }*/
    ParseMysql parser = new ParseMysql();

    //@Test
    public void parseFBeanTest() {
        String str = "{'platToken':'ipsm_v4','name':'historyTrack'," +
                "'filters':[{'col':'ap.comName','value':'贵阳市第十九中学','type':'LIKE'},{'col':'','value':'name=213113','type':'SQL'}," +
                "{'col':'age','values':[1,2],'type':'RANGE'}]," +

                "'orders':[],'limit':{'pn':1,'ps':10,'total':1}}";

        FBean fBean = convert.convertFrom(FBean.class, str);

        String[] parse = parser.parseList(fBean);

        System.out.println("count:" + parse[0]);
        System.out.println("list:" + parse[1]);
    }

    //@Test
    public void jdbcTest() {
        //DbAccount jdbcAccount = new DbAccount("jdbc:mysql://192.168.202.11:3306/gxbii_dev", "root", "eversec123098");
        DbAccount dbAccount = new DbAccount();
        dbAccount.setCate("mysql");
        dbAccount.setUrl("jdbc:mysql://192.168.202.11:3306/gxbii_dev");
        dbAccount.setUser("root");
        dbAccount.setPwd("eversec123098");

        DbKit dbKit = new DbKit(dbAccount, "");

        //String sql = "select * from basic_concat limit 1";
        String sql = "show databases;";

        // find list
        List<Map> list = dbKit.findList(sql, Map.class);
        System.out.println(list.get(0));


        //find count
        /*long total = dbKit.queryColumn("select count(1) from basic_device", long.class);
        System.out.println(total);
        System.out.println(int.class);*/
    }

    //@Test
    public void toAsTest() {
        Date date = Kv.toAs("2019-03-17 12:11:12", Date.class);
        System.out.println(date);
        System.out.println("--------------");
        Long aLong = Kv.toAs("34132213", Long.class);
        System.out.println(aLong);

        System.out.println("--------------");
        Integer integer = Kv.toAs("34132213", Integer.class);
        System.out.println(integer);

        System.out.println("--------------");
        Short aShort = Kv.toAs("121", short.class);
        System.out.println(aShort);

        System.out.println("--------------");
        Double aDouble = Kv.toAs("4658132213", double.class);
        System.out.println(aDouble);

        System.out.println("--------------");
        String s = Kv.toAs("4658132213", String.class);
        System.out.println(s);

        System.out.println("--------------");
        //Date date1 = Kv.toAs("Sun Mar 17 12:11:12 CST 2019", Date.class);
        //System.out.println(date1);

    }

    //@Test
    public void buildeDetailTest() {
        Kv kv = MetaKit.buildeDetail(MetaKit.getMetaService("user_service", "3421432"));

        System.out.println(kv);
    }

    //@Test
    public void upDb$() {
        /*MetaKit.getMetaServices().forEach(m -> {
            List<String> shows = new ArrayList<>();
            m.getShows().forEach(x -> {
                shows.add(x.replace(".", "$"));
            });
            m.setShows(shows);
            m.update();
        });*/


        //System.out.println("a$id".split("[$]")[0]);

        System.out.println("x.abx".replace(".", "\\."));

    }

    //@Test
    public void findMaxNum() {
        int xx = asList("1", "5", "3").stream().filter(x -> !x.isEmpty()).mapToInt(x -> {
            return Integer.parseInt(x) * 2;
        }).min().orElseGet(() -> 0);

        System.out.println(xx);
    }

    //@Test
    public void userCreate() {
        User user = new User();
        user.setUsername("admin");
        user.setCreateTime(System.currentTimeMillis());
        user.setPwd(User.md5IfNeed("123456"));
        user.setStatus(1);

        user.save();
    }

    //@Test
    public void t() {
        System.out.println(MetaKit.nextAlias());
    }

    //@Test
    public void kvTest() {
        /*Map map = new HashMap<>();

        map.put("name", "xxxx");
        map.put("age", 12);
        map.put("abx", 123);

        UserBean user = Kv.toBean(map, UserBean.class);
        Kv kv = Kv.toKv(user, "name", "abxx=age");

        System.out.println(user);
        System.out.println(kv);*/
        Class[] clazzs = {
                int.class, long.class, short.class, byte.class,
                Integer.class, Long.class, Short.class, Byte.class, float.class, Float.class,
                String.class,
        };
        Object[] ks = new Object[]{"1", (int) 1, (Integer) 1, 1l, 1.0, 1f, 1.0d};

        for (Object k : ks) {
            System.out.println("--------------------------------------------");
            for (Class v : clazzs) {
                System.out.printf("%s -> %s = ", k.getClass().getSimpleName(), v.getSimpleName());

                Object o = Kv.toAs(k, v);
                switch (v.getSimpleName()) {
                    case "int":
                        System.out.println((int) o);
                        break;
                    case "Integer":
                        System.out.println((Integer) o);
                        break;
                    case "long":
                        System.out.println((long) o);
                        break;
                    case "Long":
                        System.out.println((Long) o);
                        break;
                    case "short":
                        System.out.println((short) o);
                        break;
                    case "Short":
                        System.out.println((Short) o);
                        break;
                    case "byte":
                        System.out.println((byte) o);
                        break;
                    case "float":
                        System.out.println((float) o);
                        break;
                    case "Float":
                        System.out.println((Float) o);
                        break;
                    case "Byte":
                        System.out.println((Byte) o);
                        break;
                    case "String":
                        System.out.println((String) o);
                        break;
                }
            }
        }

    }

    //@Test
    public void tplTest() {
        TplKit use = TplKit.use(true);
        use.addTpl(new File(FileKit.rootPath(), "tpl")); //ok
        String tpl = use.getTpl("db.table_list", Kv.of("catalogs", asList("redbbs")));
        System.out.println(tpl);

    }

    //@Test
    public void T() {
        List<MetaTable> metaTables = MetaKit.getMetaTables();

        System.out.println(metaTables.size());
    }

    TplKit tplKit = TplKit.use("", true);

    // @Test
    public void buildMethod() {
        tplKit.addTpl(new File(FileKit.rootPath(), "/tpl/_t.tpl"));

        //buildMethod("table_link_list", "实体表，包含link信息的列表");
        buildMethod("link_info", "关联信息     ");
    }

    private void buildMethod(String url, String comment) {

        String[] arr = url.split("_");
        String methodName = "";

        for (int i = 0; i < arr.length; i++) {
            if (i == 0) {
                methodName = arr[i].toLowerCase();
            } else {
                methodName += toUpperCaseFirst(arr[i].toLowerCase());
            }
        }

        Kv kv = Kv.of("url", url).set("comment", comment).set("methodName", methodName);
        String tpl = tplKit.getTpl("service.method", kv, false);
        System.out.println(tpl);
    }

    private String toUpperCaseFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    //@Test
    public void taskRunTest() {
        TaskEntity entity = TaskEntity.dao.findByKey("23074420");

        if (entity != null) {
            System.out.printf("------------------------%n%s%n------------------------%n", convert.convertTo(TaskKit.taskRun(entity)));
        }
    }

    //@Test
    public void dataToFileTest() {

        List<MetaService> metaServices = MetaService.dao.find();

        MetaKit metaKit = new MetaKit();

        File file = new File("tmp/metaKit.json");
        file.getParentFile().mkdirs();

        FileKit.strToFile(convert.convertTo(metaKit), file);
    }

    //@Test
    public void readJson() {
        File file = new File("tmp/MetaService.json");
        try {
            Type type = new TypeToken<List<MetaService>>() {
            }.getType();


            List<MetaService> list = convert.convertFrom(type, new FileInputStream(file));

            System.out.println(list);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Class clazz = MetaService.class;

        //File file = new File(String.format("tmp/%s.json", clazz.getSimpleName()));

        /*
        写入数据到  文件
        MetaKit.cacheSave(MetaTable.class);
        MetaKit.cacheSave(MetaLink.class);
        MetaKit.cacheSave(MetaService.class);
        MetaKit.cacheSave(DbAccount.class);
        MetaKit.cacheSave(SysPlat.class);*/

    }

    //@Test
    public void cacheMemorySourceTest() {
        CacheMemorySource source = new CacheMemorySource();
        //MetaKit.dcate = "db";
        //MetaKit.init();


        List<MetaTable> list = MetaKit.getMetaTables();

        list.forEach(x -> {
            source.set(x.getKey(), x.getClass(), x);
        });

        MetaTable metaTable = (MetaTable) source.get(list.get(0).getKey(), MetaTable.class);

        metaTable.setAlias("xxxxx");

        metaTable = (MetaTable) source.get(list.get(0).getKey(), MetaTable.class);

        System.out.println(metaTable);


    }

    public List<Map> dbKitTest() {
        DbAccount dbAccount = new DbAccount();
        dbAccount.setCate("mysql");
        dbAccount.setUrl("jdbc:mysql://192.168.202.11:3306/gxbii_dev");
        dbAccount.setUser("root");
        dbAccount.setPwd("eversec123098");

        DbKit dbKit = new DbKit(dbAccount, "");

        String sql = "select platID,platDomain,platIP from basic_domain limit 10";
        String countSql = "select count(*) from basic_domain";

        // find list
        List<Map> list = dbKit.findList(sql, Map.class);
        int total = dbKit.queryColumn(countSql, int.class);

        System.out.println("总记录数：" + total);
        System.out.println(list);

        return list;
    }

    // 通用导出组件测试
    @Test
    public void exportTest() {
        List<Map> list = dbKitTest();

        Kv kv = Kv.of("platID", "平台id")
                .set("platDomain", "平台域名").set("platIP", "平台IP");

        try {
            Workbook workbook = ExcelKit.exportExcel(list, kv);

            workbook.write(new FileOutputStream(new File("target/basic_domain.xls")));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //@Test
    public void switchTest() {
        String a = "2";
        switch (a) {
            case "1":
                System.out.println(1);
            case "2":
                System.out.println(2);
            case "3":
                System.out.println(3);
            case "4":
                System.out.println(4);
        }
        System.out.println("end");
    }

    //@Test
    public void exceptionTest() {

        try {
            throw new CfgException("hello exception: %s - %s - %s", 1, 2, "x");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * IP区间包含IP数计算，含首尾
     *
     * @param startIp 其实ip
     * @param endIp   结束ip
     * @return
     */
    public int ipv4Count(String startIp, String endIp) {
        String[] srartArr = startIp.split("[.]");
        String[] endArr = endIp.split("[.]");
        int[] c = new int[4];
        int[] carryOver = {16581375, 65025, 255, 1}; // IP进位数

        for (int i = 0; i < 4; i++) {
            if (!srartArr[i].equals(endArr[i])) {
                c[i] = Integer.parseInt(endArr[i]) - Integer.parseInt(srartArr[i]);
            }
        }
        int count = 0;
        for (int i = 0; i < c.length; i++) {
            if (c[i] != 0) {
                count += c[i] * carryOver[i];
            }
        }
        return (count < 0 ? -count : count) + 1;
    }

    //@Test
    public void timerTest() {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("run task..");
            }
        }, 5000, 2000);

        int[] n = {0};
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("周期任务 .. " + ++n[0]);
                if (n[0] == 2) {
                    System.out.println("周期任务完成");
                    cancel();
                }
            }
        }, 6000, 1000);


        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //---------------------------- LOCK TEST -----------------------------
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    ReentrantReadWriteLock.ReadLock readLock = lock.readLock();


    int[] n = {0};

    public String read() {
        readLock.lock();
        try {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("read result:" + n[0]);
            return n[0] + "";
        } finally {
            readLock.unlock();
        }
    }

    public void write() {
        writeLock.lock();
        try {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            n[0]++;

        } finally {
            writeLock.unlock();
        }
    }

    //@Test
    public void lockTest() {

        new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                read();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                write();
                System.out.println("写入数据次数：--------" + (i + 1));
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                write();
                System.out.println("write：--------" + (i + 1));
            }
        }).start();

        try {
            Thread.sleep(1000 * 30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    //@Test
    public void tx() {
        System.out.println(ipv4Count("0.0.0.2", "0.0.0.0"));
    }

    Predicate<Dict> isProvice = (s) -> String.valueOf(s.getValue()).trim().endsWith("0000");
    Predicate<Dict> isCity = (s) -> !isProvice.test(s) && String.valueOf(s.getValue()).trim().endsWith("00");
    BiPredicate<Dict, Dict> belongProvice = (p, x) -> String.valueOf(x.getValue()).trim().startsWith(String.valueOf(p.getValue()).trim().substring(0, 2));

    @Test
    public void dictTest() {
        MetaKit.init();
        System.out.println("-------- 1 -------");
        DictKit dictKit = DictKit.use("ipsm_v4");
        System.out.println(dictKit.getDictLabel("useSubclass", "3"));
        System.out.println(dictKit.getDictValue("isp", "广电"));


        /*List<Dict> list = dictKit.getDicts("city");

        list.stream().filter(x -> x.getValue().startsWith("52") && isCity.test(x)).sorted((a, b) -> a.getValue().compareTo(b.getValue())).forEach(x -> {
            System.out.println(x);
        });


        List<Map> data = new ArrayList<>();
        list.stream().filter(x -> isProvice.test(x)).sorted((a, b) -> a.getValue().compareTo(b.getValue())).forEach(x -> {
            Kv kv = Kv.of("value", x.get("value")).set("label", x.get("label"));
            List<Kv> childs = new ArrayList<>();
            list.stream().filter(y -> y.getValue().startsWith(x.getValue().substring(0,2))
                    && isCity.test(y)
            ).forEach(y -> {
                childs.add(Kv.of("label", y.get("label")).set("value", y.get("value")));
            });

            childs.forEach(y -> {
                List<Kv> childs2 = new ArrayList<>();
                list.stream().filter(z -> z.getValue().startsWith(String.valueOf(y.get("value")).substring(0, 3))
                        && !z.getValue().endsWith("00")
                ).forEach(z -> {
                    childs2.add(Kv.of("label", z.get("label")).set("value", z.get("value")));
                });
                y.set("childs", childs2);
            });


            kv.set("childs", childs);
            data.add(kv);
        });

        FileKit.strToFile(convert.convertTo(data), new File("tmp/city.json"));*/

    }

    public void dictType() {

    }

    //@Test
    public void txx() {
        //2400:18c0:0:0:0:0:0:0

        //System.out.println(Integer.parseInt("2400", 16));
        //System.out.println(Math.pow(2, 32));

        /*String[] arr = "213123".split("");

        for (String x : arr) {
            System.out.println(x);
        }*/


        /*for (int i = 1; i < 8; i++) {
            String v = "1";
            for (int j = 0; j < i; j++) {
                v = ride(v , "65536");
            }
            System.out.printf("%s - %s%n", i, v);
        }*/

        /*String ip = "::9";

        System.out.println(ipv6Num("::1"));
        System.out.println(ip);*/


        //System.out.println(ipv6Num("2400:18c0:0:0:0:0:0:0"));

        /*for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                long a = Integer.parseInt(IpKit.add(x + "", y + ""));
                long b = x + y;
                if (a != b) {
                    System.out.printf("%d + %d =  %s, \t %s \n", x, y, a, b);
                } else {
                    System.out.printf("=");
                }
            }
        }*/

        /*System.out.println(toNum("127.0.0.1"));

        System.out.println(ipCount("0::0:0", "0::0:2"));*/

        //System.out.println(toNum("254.16.3.56"));

    }

    @Test
    public void dbrun() {
        //DbAccount jdbcAccount = new DbAccount("jdbc:mysql://192.168.202.11:3306/gxbii_dev", "root", "eversec123098");
        DbAccount dbAccount = new DbAccount();
        dbAccount.setCate("mysql");
        dbAccount.setUrl("jdbc:mysql://47.111.150.118:6063/v09x_platf_core");
        dbAccount.setUser("root");
        dbAccount.setPwd("*Zhong123098!");

        DbKit dbKit = new DbKit(dbAccount, "");

//        //String sql = "select * from basic_concat limit 1";
////        String sql = "show databases;";
//        String sql = "select * FROM userinviterecord where userid = 5;";
//
//        // find list
//        List<Map> list = dbKit.findList(sql, Map.class);
//        System.out.println(list.get(0));

        //find count
        /*long total = dbKit.queryColumn("select count(1) from basic_device", long.class);
        System.out.println(total);
        System.out.println(int.class);*/
    }

    /**
     * 判断对象是否为空
     *
     * @param obj 待判断的对象
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null)
            return true;
        if (obj instanceof String)
            return ((String) obj).trim().isEmpty() || "null".equals(obj);
        if (obj instanceof Collection)
            return ((Collection) obj).isEmpty();
        if (obj instanceof Map)
            return ((Map) obj).isEmpty();

        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            return true;
        }

        /*if (obj instanceof Object[]) {
            for (Object o : (Object[]) obj) {
                if (o != null) return false;
            }
            return true;
        }*/
        return false;
    }

    public static <T> List<T> strToArr(String str, Class<T> clazz) {
        String[] strArr = str.split(",");
        List<T> list = new ArrayList(strArr.length);

        for (String s : strArr) {
            if (isEmpty(s)) continue;

            list.add(Kv.toAs(s, clazz));
        }

        return list;
    }

    public List<Map> findList(String sql) {
        DbAccount dbAccount = new DbAccount();
        dbAccount.setCate("mysql");
//        dbAccount.setUrl("jdbc:mysql://47.111.150.118:6063/platf_quest");
//        dbAccount.setUrl("jdbc:mysql://121.196.17.55:6063/platf_quest");
        dbAccount.setUrl("jdbc:mysql://122.112.180.156:6033/v09x_platf_core");
        dbAccount.setUser("guest");
        dbAccount.setPwd("*Zhong123098!");

//        dbAccount.setPwd("*Hello@27.com!");


        DbKit dbKit = new DbKit(dbAccount, "v09x_platf_core");


        //dbKit.exetute("UPDATE articleinfo SET gameids=NULL WHERE gameids =',';");

        // find list
        List<Map> list = dbKit.findList(sql, Map.class);
        return list;
    }


    @Test
    public void xxx() {
        //System.out.println(findList("SELECT g.`gameid`,g.`lastscore`,g.`tagids` FROM gameinfo g WHERE g.`tagids` LIKE '%,201,%' ORDER BY g.`lastscore` DESC"));

        Kv dictKv = Kv.of();
        findList("SELECT d.`numvalue`,d.`keydesc` FROM `dictinfo` d WHERE d.`keyname` LIKE 'TAG-102-2%'").forEach(x ->
                dictKv.put(x.get("numvalue"), x.get("keydesc")));

        // 游戏数据
        List<Kv> games = new ArrayList<>();
        // WHERE g.`tagids` LIKE '%,203,%'
        findList("SELECT g.`gameid`,g.`lastscore`,g.`tagids` FROM gameinfo g where 1=1 ORDER BY g.`lastscore` DESC").forEach(x -> {
            Kv game = Kv.of();
            game.set("gameid", x.get("gameid"));
            game.set("lastscore", x.get("lastscore"));

            List<Integer> tags = strToArr(x.get("tagids") + "", Integer.class);
            game.set("tags", tags);
            games.add(game);
        });

        Kv<Integer, List> of = Kv.of();// cateid:[gameid,]

        for (Object c : dictKv.keySet()) {
            Integer cate = Kv.toAs(c, int.class);
            List gids = new ArrayList<>();
            games.stream().filter(x -> ((List) x.get("tags")).contains(cate)).forEach(x -> { //.sorted(Comparator.comparing(x -> -(Long) x.get("lastscore")))
                gids.add(x.get("gameid"));
            });
            of.set(cate, gids);
        }


        //FileKit.strToFile(JsonConvert.root().convertTo(games), new File("target/games.json"));
        FileKit.strToFile(JsonConvert.root().convertTo(of), new File("target/games-ids.json"));

    }

    @Test
    public void writeinto() {

        List<Map<String, Object>> list = new ArrayList<>();

        Kv sheet1 = Kv.of();
        sheet1.set("data", asList(
                Kv.of("name", "张三").set("age", 12),
                Kv.of("name", "李四").set("age", 15)
        ));
        sheet1.set("sheetName", "人员名单");
        sheet1.set("hdNames", new String[]{"姓名", "年龄"});
        sheet1.set("hds", new String[]{"name", "age"});
        list.add(sheet1);


        Kv sheet2 = Kv.of();
        sheet2.set("data", asList(
                Kv.of("name", "苹果").set("price", 12),
                Kv.of("name", "梨子").set("price", 15)
        ));
        sheet2.set("sheetName", "水果价目表");
        sheet2.set("hdNames", new String[]{"水果名称", "单价"});
        sheet2.set("hds", new String[]{"name", "price"});
        list.add(sheet2);

        Workbook wb = ExcelKit.exportExcels(list);
        try {
            wb.write(new FileOutputStream(new File("target/xxx.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void writeout() {
        DbAccount dbAccount = new DbAccount();
        dbAccount.setCate("mysql");
        dbAccount.setUrl("jdbc:mysql://47.111.150.118:6063/v09x_platf_core");
        dbAccount.setUser("root");
        dbAccount.setPwd("*Zhong123098!");

        DbKit dbKit = new DbKit(dbAccount, "v09x_platf_core");
        System.out.println(dbAccount.getUrl());
        String sql1 = "SELECT * FROM userinviterecord ORDER BY createtime DESC;";
        String sql2 = "SELECT userid,username FROM userdetail;";
        List<Map> list1 = dbKit.findList(sql1, Map.class);
        Kv users = Kv.of();
        findList(sql2).forEach(x -> {
            users.put(x.get("userid"), x.get("username"));
        });
        List<Map> list = new ArrayList<>();

        list1.forEach(x -> {
            HashMap<Object, Object> map = new HashMap<>();
            map.putAll(x);
            Object createtime = x.get("createtime");
            String s = datechange((Long) createtime);
            map.put("createtime", s);
            map.put("username", users.get(x.get("userid")));
            map.put("inviteename", users.get(x.get("inviteeid")));
            list.add(map);
        });

        Kv kv = Kv.of();

        kv.set("inviteid", "邀请ID");
        kv.set("userid", "邀请人ID");
        kv.set("username", "邀请人昵称");
        kv.set("inviteeid", "被邀请人ID");
        kv.set("inviteename", "被邀请人昵称");
        kv.set("invitecode", "邀请码");
        kv.set("createtime", "创建时间");

        try {
            Workbook workbook = ExcelKit.exportExcel(list, kv);

            workbook.write(new FileOutputStream(new File("target/invite.xls")));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test//每人所属邀请码信息
    public void inviteout() {
//        dbrun();

//        String sql1 = "SELECT userid FROM userinvitecode GROUP BY userid;";
//        String sql1 = "SELECT a.userid,b.username FROM userinvitecode a ,userdetail b WHERE a.userid = b.userid GROUP BY userid;";
//        Kv users = Kv.of();
//
//        findList(sql1).forEach(x -> {
//            users.put(x.get("userid"), x.get("username"));
//        });
        HashMap<Integer, String> map = new HashMap<>();
        map.put(11244, "岩茹");
        map.put(11245, "胡胡");
        map.put(11037, "汪志");
        map.put(11038, "包月琪");
        map.put(11039, "王思佳");
        map.put(11041, "梁显优");
        map.put(11042, "张曼玲");
        map.put(11043, "李佺林");
        map.put(11044, "曾昌");
        map.put(11047, "姜文洁");
        map.put(11049, "胡梦娇");
        map.put(11050, "唐华锋");
        map.put(11051, "李亚光");
        map.put(11053, "戴文婷");
        map.put(11054, "吴双江");
        map.put(11058, "瞿俏");
        map.put(11189, "吴文俊");
        map.put(11190, "鲁萍");
        map.put(11191, "严谨");
        map.put(11192, "周琴");
        map.put(11193, "张成");
        map.put(11194, "王伟");
        map.put(11195, "熊宇");
        map.put(11196, "赵才华");
        map.put(11197, "李文龙");
        map.put(11198, "曹亚军");
        map.put(11199, "常重阳");
        map.put(11200, "曾柏超");
        map.put(11201, "邓聪");
        map.put(11202, "李凯华");


        List<Map<String, Object>> l = new ArrayList<>();

        map.keySet().forEach(x -> {
            String username = map.get(x);
            System.out.println(username.toString());
            String sql3 = "SELECT  invitecode FROM userinvitecode WHERE userid = " + x + ";";
            List<Map> list2 = findList(sql3);
//            invites.put(userid, list2);
            Kv sheet1 = Kv.of();
            List<Kv> kvs = new ArrayList<>();
            for (int i = 0; i < list2.size(); i++) {
                kvs.add(Kv.of("num", i + 1).set("invitecode", list2.get(i).get("invitecode")));
            }

            sheet1.set("data", kvs);
            sheet1.set("sheetName", username);
            sheet1.set("hdNames", new String[]{"序号", "邀请码"});
            sheet1.set("hds", new String[]{"num", "invitecode"});
            l.add(sheet1);
        });

//        List<Map> list1 = findList(sql1);
//        list1.forEach(x -> {
//            Object userid = x.get("userid");
//            String sql3 = "SELECT  invitecode FROM userinvitecode WHERE userid = " + userid + ";";
//            List<Map> list2 = findList(sql3);
////            invites.put(userid, list2);
//            Kv sheet1 = Kv.of();
//            List<Kv> kvs = new ArrayList<>();
//            for (int i = 0; i < list2.size(); i++) {
//                kvs.add(Kv.of("num", i + 1).set("invitecode", list2.get(i).get("invitecode")));
//            }
//
//            sheet1.set("data", kvs);
//            sheet1.set("sheetName", x.toString());
//            sheet1.set("hdNames", new String[]{"序号", "邀请码"});
//            sheet1.set("hds", new String[]{"num", "invitecode"});
//            l.add(sheet1);
//        });


//        invites.keySet().forEach(x -> {
//            List<Map> list3 = (List) invites.get(x);
//            Kv sheet1 = Kv.of();
//            List<Kv> kvs = new ArrayList<>();
//            for (int i = 0; i < list3.size(); i++) {
//                kvs.add(Kv.of("num", i + 1).set("invitecode", list3.get(i).get("invitecode")));
//            }
//
//            sheet1.set("data", kvs);
//            sheet1.set("sheetName", x.toString());
//            sheet1.set("hdNames", new String[]{"序号", "邀请码"});
//            sheet1.set("hds", new String[]{"num", "invitecode"});
//            l.add(sheet1);
//        });


        Workbook wb = ExcelKit.exportExcels(l);
        try {
            wb.write(new FileOutputStream(new File("target/邀请码信息.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test//每日问卷导出
    public void questionout() {
        DbAccount dbAccount = new DbAccount();
        dbAccount.setCate("mysql");
//        dbAccount.setUrl("jdbc:mysql://47.111.150.118:6063");
        dbAccount.setUrl("jdbc:mysql://122.112.180.156:6033/v09x_platf_core");
        dbAccount.setUser("root");
//        dbAccount.setPwd("*Zhong123098!");

        dbAccount.setPwd("*Hello@27.com!");
        DbKit dbKit = new DbKit(dbAccount, "");
        String sql1 = "SELECT * FROM `platf_oth`.`questionrecord` where createtime<1588089600000 and createtime>1588003200000 ORDER BY `createtime` DESC ;";
        String sql2 = "SELECT userid,username FROM `v09x_platf_core`.userdetail;";
        List<Map> list1 = dbKit.findList(sql1, Map.class);
        Kv users = Kv.of();
        findList(sql2).forEach(x -> {
            users.put(x.get("userid"), x.get("username"));
        });
        List<Map> list = new ArrayList<>();

        list1.forEach(x -> {
            HashMap<Object, Object> map = new HashMap<>();
            map.putAll(x);
            Object createtime = x.get("createtime");
            String s = datechange((Long) createtime);
            Object link_from = x.get("link_from");
//            int i =0;
//            if (link_from!=null&&!link_from.equals("sina")&&!link_from.equals("default")&&!link_from.equals("")){
//                i = Integer.parseInt(link_from.toString());
//            }
//            map.put("link_from", users.get(i));
            map.put("createtime", s);
//            Object status = x.get("status");
//            if (status.equals(10)){
//                map.put("status","正常");
//            }else if (status.equals(20)){
//                map.put("status","待审核");
//            }else if (status.equals(11)){
//                map.put("status","审核通过");
//            }else if (status.equals(30)){
//                map.put("status","审核不通过");
//            }

            list.add(map);
        });

        Kv kv = Kv.of();

        kv.set("email", "邮箱");
        kv.set("mobile", "电话");
        kv.set("phone_os", "手机系统");
        kv.set("playgame_count", "玩过几款游戏");
        kv.set("playgame_age", "游戏年龄");
        kv.set("game_cate", "游戏类型");
        kv.set("game_device", "游戏设备");
        kv.set("profession", "职业状态");
        kv.set("playgame_usertype", "玩家类型");
        kv.set("playgame_role", "主要角色");
        kv.set("daypaly_timelong", "每天游戏时常");
        kv.set("monthpay_rmb", "每月花费金额");
        kv.set("creation_platform", "创作平台");
        kv.set("creation_cate", "创作类型");
        kv.set("creation_fanscount", "粉丝量");
        kv.set("creation_frequency", "创作频率");
        kv.set("creation_mainpage", "主页截图");
        kv.set("live_platform", "直播平台");
        kv.set("live_followcount", "直播关注度");
        kv.set("play_qq_group", "玩家QQ群");
        kv.set("union_name", "公会名字");
        kv.set("want_say", "想说的话");
        kv.set("link_from", "渠道来源");
        kv.set("ip", "来源IP");
        kv.set("invitecode", "邀请码");
        kv.set("batch", "封测批次");
        kv.set("createtime", "创建时间");
        kv.set("status", "状态");

        try {
            Workbook workbook = ExcelKit.exportExcel(list, kv);

            workbook.write(new FileOutputStream(new File("target/问卷4-28日数据.xls")));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test//厂商信息导出
    public void merchantout() {
        DbAccount dbAccount = new DbAccount();
        dbAccount.setCate("mysql");
//        dbAccount.setUrl("jdbc:mysql://47.111.150.118:6063");
        dbAccount.setUrl("jdbc:mysql://122.112.180.156:6033/v09x_platf_core");
        dbAccount.setUser("root");
//        dbAccount.setPwd("*Zhong123098!");

        dbAccount.setPwd("*Hello@27.com!");
        DbKit dbKit = new DbKit(dbAccount, "");
        String sql1 = "SELECT * FROM `v09x_platf_core`.`merchantinfo`  ORDER BY `createtime` DESC ;";
        String sql2 = "SELECT userid,username FROM `v09x_platf_core`.userdetail;";
        List<Map> list1 = dbKit.findList(sql1, Map.class);
        Kv users = Kv.of();
        findList(sql2).forEach(x -> {
            users.put(x.get("userid"), x.get("username"));
        });
        List<Map> list = new ArrayList<>();

        list1.forEach(x -> {
            HashMap<Object, Object> map = new HashMap<>();
            map.putAll(x);
            Object createtime = x.get("createtime");
            String s = datechange((Long) createtime);
            map.put("createtime", s);
            String updatetime = datechange((Long) x.get("updatetime"));
            map.put("updatetime", updatetime);
            list.add(map);
        });

        Kv kv = Kv.of();

        kv.set("merchantid", "游戏开发/运营商ID");
        kv.set("merchantname", "商家名称");
        kv.set("merchantgovurl", "商家官网");
        kv.set("merchantnameen", "商家英文名称");
        kv.set("merchantlogo", "商家logo");
        kv.set("merchantalias", "商家别名");
        kv.set("creditcode", "统一社会信用代码");
        kv.set("businesscode", "工商号码");
        kv.set("teamsize", "团队规模");
        kv.set("operationstatus ", "经营状态");
        kv.set("businessstart", "营业期限开始");
        kv.set("businessend", "营业期限结束");
        kv.set("behalfuser", "法定代表人");
        kv.set("mobile", "联系电话");
        kv.set("createtime", "创建时间");
        kv.set("creatememberid", "创建人ID");
        kv.set("updatetime", "修改时间");
        kv.set("updatememberid", "修改人ID");
        kv.set("email", "邮箱");
        kv.set("registration", "登记机关");
        kv.set("capital", "注册资金");
        kv.set("zone", "行政区划");
        kv.set("address", "企业地址");
        kv.set("introduction", "企业简介");
        kv.set("licenseimg", "营业执照");
        kv.set("gamecount", "游戏数量");
        kv.set("publishedcount", "发行游戏数量");
        kv.set("developedcount", "开发游戏数量");
        kv.set("status", "状态");
        kv.set("followcount", "关注数");

        try {
            Workbook workbook = ExcelKit.exportExcel(list, kv);

            workbook.write(new FileOutputStream(new File("target/厂商4-28日数据.xls")));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // 邀请码使用情况
    @Test
    public void invitecodeuse() {
//        String sql = "SELECT v.`invitecode` '邀请码',u.`username` '邀请人', u1.`username` '被邀请人',FROM_UNIXTIME(v.`createtime`/1000, '%Y-%m-%d %H:%i:%S') '激活码使用时间' FROM `userinviterecord` v LEFT JOIN userdetail u ON v.`userid`=u.`userid` LEFT JOIN userdetail u1 ON v.`inviteeid`=u1.`userid`\n" +
//                "WHERE v.`userid` IN (11244,11255,11192 , 11047 , 11039 , 11189 , 11190 , 11191 , 11042 , 11038 , 11041 , 11043 , 11044 , 11050 , 11049 , 11051 , 11053 , 11054 , 11058 , 11193 , 11194 , 11195 , 11196 , 11197 , 11198 , 11199 , 11200 , 11201 , 11202 , 11037)  ORDER BY v.`createtime` DESC;";

        String sql = "SELECT v.`invitecode` '邀请码',u.`userid` '邀请人', u1.`username` '被邀请人',FROM_UNIXTIME(v.`createtime`/1000, '%Y-%m-%d %H:%i:%S') '激活码使用时间' FROM `userinviterecord` v LEFT JOIN userdetail u ON v.`userid`=u.`userid` LEFT JOIN userdetail u1 ON v.`inviteeid`=u1.`userid`\n" +
                "WHERE v.`userid` IN (11244,11245,11192 , 11047 , 11039 , 11189 , 11190 , 11191 , 11042 , 11038 , 11041 , 11043 , 11044 , 11050 , 11049 , 11051 , 11053 , 11054 , 11058 , 11193 , 11194 , 11195 , 11196 , 11197 , 11198 , 11199 , 11200 , 11201 , 11202 , 11037)  ORDER BY v.`createtime` DESC;";

        HashMap<Object, Object> map = new HashMap<>();
        map.put(11244, "岩茹");
        map.put(11245, "胡胡");
        map.put(11037, "汪志");
        map.put(11038, "包月琪");
        map.put(11039, "王思佳");
        map.put(11041, "梁显优");
        map.put(11042, "张曼玲");
        map.put(11043, "李佺林");
        map.put(11044, "曾昌");
        map.put(11047, "姜文洁");
        map.put(11049, "胡梦娇");
        map.put(11050, "唐华锋");
        map.put(11051, "李亚光");
        map.put(11053, "戴文婷");
        map.put(11054, "吴双江");
        map.put(11058, "瞿俏");
        map.put(11189, "吴文俊");
        map.put(11190, "鲁萍");
        map.put(11191, "严谨");
        map.put(11192, "周琴");
        map.put(11193, "张成");
        map.put(11194, "王伟");
        map.put(11195, "熊宇");
        map.put(11196, "赵才华");
        map.put(11197, "李文龙");
        map.put(11198, "曹亚军");
        map.put(11199, "常重阳");
        map.put(11200, "曾柏超");
        map.put(11201, "邓聪");
        map.put(11202, "李凯华");


        Map<Object, List<Map>> listMap = findList(sql).stream().collect(Collectors.groupingBy(x -> x.get("邀请人")));
//        listMap.keySet().forEach(x->{
//            System.out.println(x);
//        });

        List<Map<String, Object>> sheets = new ArrayList<>();
        listMap.forEach((k, v) -> {
            Map<String, Object> sheet = new HashMap<>();
            sheet.put("sheetName", map.get(k).toString());
            sheet.put("hdNames", new String[]{"邀请码", "被邀请人", "激活码使用时间"});
            sheet.put("hds", new String[]{"邀请码", "被邀请人", "激活码使用时间"});

            sheet.put("data", v);
            sheets.add(sheet);
        });

        sheets.sort(Comparator.comparing(x -> -((List) x.get("data")).size()));
        // 创建文件
        Workbook wb = ExcelKit.exportExcels(sheets);
        try {
            wb.write(new FileOutputStream(new File("tmp/邀请码_邀请记录_5-06.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //时间格式转换
    public String datechange(long date) {
        Date current = new Date(date);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(current);
        return time;
    }

    @Test//时间转换
    public void time() throws ParseException {
//        Date current = new Date();
//        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
//                "yyyy-MM-dd HH:mm:ss");
//        String time = sdf.format(current);
//        System.out.println(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = "2020-04-20 00:00:00";
        String end = "2020-04-27 00:00:00";
        //1587657600000
        //1587744000000
//得到毫秒数
        long timeStart = sdf.parse(start).getTime();
        System.out.println(timeStart);
        long timeEnd = sdf.parse(end).getTime();
        System.out.println(timeEnd);

    }

    @Test//生成邀请码
    public void createInvitiCode() {
        // data,sheetName,hds,hdNames,

        List<Map> codeList = findList("SELECT v.`invitecode` FROM `userinvitecode` v");

        Set<String> allCode = new HashSet<>(); // 全部的邀请码
        codeList.forEach(x -> allCode.add(x.get("invitecode") + ""));

        List<Kv> users = null;
        {
            users = asList(
                  /*Kv.of("userid", 11192).set("name", "周琴").set("n", 50),
                    Kv.of("userid", 11047).set("name", "姜文洁").set("n", 50),
                    Kv.of("userid", 11039).set("name", "王思佳").set("n", 50),
                    Kv.of("userid", 11189).set("name", "吴文俊").set("n", 50),
                    Kv.of("userid", 11190).set("name", "鲁萍").set("n", 50),
                    Kv.of("userid", 11191).set("name", "严谨").set("n", 50),
                    Kv.of("userid", 11042).set("name", "张曼玲").set("n", 10),
                    Kv.of("userid", 11038).set("name", "包月琪").set("n", 10),
                    Kv.of("userid", 11041).set("name", "梁显优").set("n", 10),
                    Kv.of("userid", 11043).set("name", "李佺林").set("n", 10),
                    Kv.of("userid", 11044).set("name", "曾昌").set("n", 10),
                    Kv.of("userid", 11050).set("name", "唐华锋").set("n", 10),
                    Kv.of("userid", 11049).set("name", "胡梦娇").set("n", 10),
                    Kv.of("userid", 11051).set("name", "李亚光").set("n", 10),
                    Kv.of("userid", 11053).set("name", "戴文婷").set("n", 10),
                    Kv.of("userid", 11054).set("name", "吴双江").set("n", 10),
                    Kv.of("userid", 11058).set("name", "瞿俏").set("n", 10),
                    Kv.of("userid", 11193).set("name", "张成").set("n", 10),
                    Kv.of("userid", 11194).set("name", "王伟").set("n", 10),
                    Kv.of("userid", 11195).set("name", "熊宇").set("n", 10),
                    Kv.of("userid", 11196).set("name", "赵才华").set("n", 10),
                    Kv.of("userid", 11197).set("name", "李文龙").set("n", 10),
                    Kv.of("userid", 11198).set("name", "曹亚军").set("n", 10),
                    Kv.of("userid", 11199).set("name", "常重阳").set("n", 10),
                    Kv.of("userid", 11200).set("name", "曾柏超").set("n", 10),
                    Kv.of("userid", 11201).set("name", "邓聪").set("n", 10),
                    Kv.of("userid", 11202).set("name", "李凯华").set("n", 10),
                    Kv.of("userid", 11037).set("name", "汪志").set("n", 10)*/

                    /*Kv.of("userid", 11244).set("name", "岩茹").set("n", 50),
                    Kv.of("userid", 11245).set("name", "胡胡").set("n", 50)*/

                    /*Kv.of("userid", 11047).set("name", "姜文洁").set("n", 350),
                    Kv.of("userid", 11039).set("name", "王思佳").set("n", 225)*/

                    /*Kv.of("userid", 11037).set("name", "汪志").set("n", 100)*/

//                    Kv.of("userid", 10000).set("name", "小彩虹").set("n", 19)
//                    Kv.of("userid", 11047).set("name", "姜文洁").set("n", 100)
//                    Kv.of("userid", 10000).set("name", "小彩虹").set("n", 33)
//                    Kv.of("userid", 10000).set("name", "小彩虹").set("n", 1)
//                    Kv.of("userid", 11047).set("name", "姜文洁").set("n", 200)
//                    Kv.of("userid", 10000).set("name", "小彩虹").set("n", 2)
//                    Kv.of("userid", 11047).set("name", "姜文洁").set("n", 200)
//                    Kv.of("userid", 10000).set("name", "小彩虹").set("n", 3)
//                    Kv.of("userid", 11042).set("name", "墨菲").set("n", 3)
//                    Kv.of("userid", 10000).set("name", "小彩虹").set("n", 87)
                    Kv.of("userid", 10000).set("name", "小彩虹").set("n", 20)
            );

        }
        //生成邀请码,
        List<Map<String, Object>> sheets = new ArrayList<>();
        StringBuilder buf = new StringBuilder("INSERT INTO `platf_quest`.`userinvitecode` (`invitecode`,`userid`,`createtime`) VALUES \n");
        for (Kv x : users) {
            Map<String, Object> sheet = new HashMap<>();
            sheet.put("sheetName", x.get("name"));
            sheet.put("hdNames", new String[]{"序号", "邀请码"});
            sheet.put("hds", new String[]{"inx", "code"});

            List<Kv> data = new ArrayList<>();
            for (int i = 0; i < (Integer) x.get("n"); i++) {
                String code = null;
                do {
                    code = buildCode();
                    if (!allCode.contains(code)) {
                        allCode.add(code);
                        data.add(Kv.of("inx", i + 1).set("code", code));
                        buf.append(String.format("('%s',%s,%s),\n", code, x.get("userid"), System.currentTimeMillis()));
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                } while (true);
            }
            sheet.put("data", data);
            sheets.add(sheet);
        }
        buf.delete(buf.length() - 1, buf.length() + 1);
        buf.append(";");
        // 入库邀请码
        FileKit.strToFile(buf.toString(), new File("tmp/邀请码_06-28_汪志.sql"));

        // 创建文件
        Workbook wb = ExcelKit.exportExcels(sheets);
        try {
            wb.write(new FileOutputStream(new File("tmp/邀请码_06-28_汪志.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //读取问卷excle生成updatesql语句；
    @Test
    public void updatequestion() throws FileNotFoundException {
        StringBuffer buff = new StringBuffer();
        String[] FIELDS = {"email", "mobile", "phone_os", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "invitecode", "batch", "", "status"};
//        List<Map> list = ExcelKit.readExcel(new File("target/问卷4-27日数据.xls"), FIELDS, "sheet0");
        List<Map> list = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\编辑每日数据\\4月28日数据.xlsx"), FIELDS);
        list.remove(0);//去除多余的行首
        list.forEach(x -> {
            buff.append("UPDATE `platf_oth`.`questionrecord` ");
            String invitecode = x.get("invitecode").toString();
            String batch = x.get("batch").toString();
            String status = x.get("status").toString();
            String mobile = x.get("mobile").toString();
            buff.append("SET invitecode = '" + invitecode + "' ,");
            buff.append(" batch = '" + batch + "' ,");
            buff.append(" status = " + status + " ");
            buff.append("WHERE mobile = '" + mobile + "';" + "\n");
        });
//        System.out.println(buff);
        // 问卷更新sql
        FileKit.strToFile(buff.toString(), new File("tmp/问卷_04-23_3文3.sql"));

    }

    //读取员工账号生成updatesql语句；
    @Test
    public void usersql() throws FileNotFoundException {
        StringBuffer buff = new StringBuffer();
//        String[] FIELDS = {"email", "mobile", "phone_os", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "invitecode", "batch", "", "status"};
        String[] FIELDS = {"userid", "userno", "", "", "mobile"};
//        List<Map> list = ExcelKit.readExcel(new File("target/问卷4-27日数据.xls"), FIELDS, "sheet0");
        List<Map> list = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\文档\\员工账号.xls"), FIELDS);
        list.remove(0);//去除多余的行首
        list.forEach(x -> {
            buff.append("UPDATE `v09x_platf_core`.`userdetail` ");
            String userid = x.get("userid").toString();
//            String userno = x.get("userno").toString();
//            String mobile = x.get("mobile").toString();
            buff.append("SET healthvalue = " + 100 + "  ");
//            buff.append(" mobile = '"+mobile+"'  ");
            buff.append("WHERE userid = " + userid + ";" + "\n");
        });
        FileKit.strToFile(buff.toString(), new File("tmp/员工健康值更新.sql"));

    }

    private String buildCode() {
        char[] str = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        StringBuffer buf = new StringBuffer();
        Random random = new Random();
        while (buf.length() < 6) {
            buf.append(str[random.nextInt(str.length)]);
        }

        return buf.toString();
    }

    //读取商家信息
    @Test
    public void readmerchant() {
        StringBuffer buff = new StringBuffer();
        String[] FIELDS = {"merchantname", "", "merchantnameen", "merchantalias", "merchantgovurl", "creditcode", "businesscode", "teamsize", "operationstatus",
                "businessstart", "businessend", "behalfuser", "mobile", "email", "registration", "capital", "zone", "address", "introduction", "", "merchantid"};
        List<Map> list = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\编辑数据\\商家信息.xlsx"), FIELDS);
        list.remove(0);//去除多余的行首
        list.remove(0);//去除多余的行首
        list.forEach(x -> {
            Object merchantname = x.get("merchantname").toString().trim();
            Object merchantnameen = x.get("merchantnameen").toString().trim();
            Object merchantalias = x.get("merchantalias").toString().trim();
            Object merchantgovurl = x.get("merchantgovurl").toString().trim();
            Object creditcode = x.get("creditcode").toString().trim();
            Object businesscode = x.get("businesscode").toString().trim();
            Object teamsize = x.get("teamsize").toString().trim();
            Object operationstatus = x.get("operationstatus").toString().trim();
            Object businessstart = x.get("businessstart").toString().trim();
            Object businessend = x.get("businessend").toString().trim();
            Object behalfuser = x.get("behalfuser").toString().trim();
            Object mobile = x.get("mobile").toString().trim();
            Object email = x.get("email").toString().trim();
            Object registration = x.get("registration").toString().trim();
            Object capital = x.get("capital").toString().trim();
            Object zone = x.get("zone").toString().trim();
            Object address = x.get("address").toString().trim();
            Object introduction = x.get("introduction").toString().trim();
            Object merchantid = x.get("merchantid").toString().trim();
            buff.append("UPDATE `v09x_platf_core`.`merchantinfo` ");
            buff.append("SET merchantname = \"" + merchantname + "\",  ");
            buff.append("merchantnameen = \"" + merchantnameen + "\",  ");
            buff.append("merchantalias = \"" + merchantalias + "\",  ");
            buff.append("merchantgovurl = \"" + merchantgovurl + "\",  ");
            buff.append("creditcode = \"" + creditcode + "\",  ");
            buff.append("businesscode = \"" + businesscode + "\",  ");
            buff.append("teamsize = \"" + teamsize + "\",  ");
            buff.append("operationstatus = \"" + operationstatus + "\",  ");
            buff.append("businessstart = \"" + businessstart + "\",  ");
            buff.append("businessend = \"" + businessend + "\",  ");
            buff.append("behalfuser = \"" + behalfuser + "\",  ");
            buff.append("mobile = \"" + mobile + "\",  ");
            buff.append("email = \"" + email + "\",  ");
            buff.append("registration = \"" + registration + "\",  ");
            buff.append("capital = \"" + capital + "\",  ");
            buff.append("zone = \"" + zone + "\",  ");
            buff.append("address = \"" + address + "\",  ");
            buff.append("introduction = \"" + introduction + "\"  ");
            buff.append("WHERE merchantid = " + merchantid + ";" + "\n");
        });
        // 更新sql
        FileKit.strToFile(buff.toString(), new File("target/厂商数据更新.sql"));

    }

    //读取商家图片信息
    @Test
    public void readpicture() {
//        File file = new File();
        //添加文件路径
//        getFiles("C:\\Users\\wh\\Desktop\\编辑数据\\商家图标汇总\\");
        ArrayList<String> files = new ArrayList<String>();
        File file = new File("C:\\Users\\wh\\Desktop\\编辑数据\\图标汇总\\");
        File[] tempLists = file.listFiles();
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < tempLists.length; i++) {
            buff.append("UPDATE `v09x_platf_core`.`merchantinfo` ");
            if (tempLists[i].isFile()) {
                String name = tempLists[i].getName().trim();
                String id = tempLists[i].getName().substring(0, name.lastIndexOf("."));
//                files.add(tempLists[i].getName().substring(0,name.lastIndexOf(".")));
                buff.append("SET merchantlogo = '" + "http://aimg.woaihaoyouxi.com/app/merchant/logo/" + name + "'  ");
                buff.append("WHERE merchantid = " + id + ";" + "\n");

            }
        }
        FileKit.strToFile(buff.toString(), new File("target/商家logo更新.sql"));

//        for (int i = 0; i < files.size(); i++) {
//            System.out.println(files.get(i));
//        }
    }

    //查询数据库表及表数据量
    @Test
    public void readMysql() {
        String[] database = {"v09x_platf_core", "platf_im", "platf_oss", "platf_oth", "platf_pay", "platf_questx", "platf_stat", "platf_warband"};
        List<Map<String, Object>> l = new ArrayList<>();

        for (String s : database) {

            String sql = "SELECT table_schema,TABLE_NAME,CONCAT(truncate(data_length/1024/1024,2),'MB') as data_size,table_rows rs,TABLE_COMMENT" +
                    " FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = '" + s +
                    "' ORDER BY rs DESC";

            List<Map> list1 = findList(sql);
            Kv sheet1 = Kv.of();
            List<Kv> kvs = new ArrayList<>();
            for (int i = 0; i < list1.size(); i++) {
                kvs.add(Kv.of("TABLE_SCHEMA", list1.get(i).get("TABLE_SCHEMA"))
                        .set("TABLE_NAME", list1.get(i).get("TABLE_NAME"))
                        .set("data_size", list1.get(i).get("data_size"))
                        .set("rs", list1.get(i).get("rs"))
                        .set("TABLE_COMMENT", list1.get(i).get("TABLE_COMMENT")));
            }


            sheet1.set("data", kvs);
            sheet1.set("sheetName", s);
            sheet1.set("hdNames", new String[]{"数据库名", "表名", "表数据大小", "表数据数量", "表注释"});
            sheet1.set("hds", new String[]{"TABLE_SCHEMA", "TABLE_NAME", "data_size", "rs", "TABLE_COMMENT"});
            l.add(sheet1);

        }

        Workbook wb = ExcelKit.exportExcels(l);
        try {
            wb.write(new FileOutputStream(new File("target/数据库信息.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //查询数据库表及表字段属性
    @Test
    public void getMySql() {
        String[] database = {"official_core", "official_ipci", "official_oss", "v09x_platf_core", "platf_im", "platf_sdk", "platf_live", "platf_oss", "platf_oth", "platf_pay", "platf_quest", "platf_questx", "platf_stat", "platf_warband"};
        List<Map<String, Object>> l = new ArrayList<>();

        for (String s : database) {

            String sql = "SELECT table_schema,TABLE_NAME" +
                    " FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = '" + s +
                    "' ";
            List<Map> list1 = findList(sql);

            Kv sheet1 = Kv.of();
            List<Kv> kvs = new ArrayList<>();

            for (int i = 0; i < list1.size(); i++) {
                Object table_schema = list1.get(i).get("TABLE_SCHEMA");
                String table_name = (String) list1.get(i).get("TABLE_NAME");
                String sql2 = "SELECT TABLE_SCHEMA, TABLE_NAME,COLUMN_NAME,COLUMN_DEFAULT,IS_NULLABLE,COLUMN_TYPE,COLUMN_COMMENT FROM information_schema.COLUMNS WHERE table_name = '" + table_name + "' and table_schema = '" + table_schema + "'; ";


                List<Map> list = findList(sql2);

                list.forEach(x -> {
                    if (x.get("COLUMN_DEFAULT") == null || x.get("IS_NULLABLE").equals("YES")) {
                        if (x.get("COLUMN_TYPE").equals("mediumtext") || x.get("COLUMN_TYPE").equals("text")) {
                            return;
                        }
                        kvs.add(Kv.of("TABLE_SCHEMA", x.get("TABLE_SCHEMA"))
                                .set("TABLE_NAME", x.get("TABLE_NAME"))
                                .set("COLUMN_NAME", x.get("COLUMN_NAME"))
                                .set("COLUMN_DEFAULT", x.get("COLUMN_DEFAULT"))
                                .set("IS_NULLABLE", x.get("IS_NULLABLE"))
                                .set("COLUMN_TYPE", x.get("COLUMN_TYPE"))
                                .set("COLUMN_COMMENT", x.get("COLUMN_COMMENT"))
                        );

                    }

                });

            }
            sheet1.set("data", kvs);
            sheet1.set("sheetName", s);
            sheet1.set("hdNames", new String[]{"数据库名", "表名", "字段名", "字段类型", "默认值", "是否为空", "字段注释"});
            sheet1.set("hds", new String[]{"TABLE_SCHEMA", "TABLE_NAME", "COLUMN_NAME", "COLUMN_TYPE", "COLUMN_DEFAULT", "IS_NULLABLE", "COLUMN_COMMENT"});
            l.add(sheet1);
        }

        Workbook wb = ExcelKit.exportExcels(l);
        try {
            wb.write(new FileOutputStream(new File("target/数据库表字段属性默认值信息0818.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //查询数据库表及表字段属性
    @Test
    public void attribute() {
        String[] database = {"official_core", "official_ipci", "official_oss", "v09x_platf_core", "platf_im", "platf_sdk", "platf_live", "platf_oss", "platf_oth", "platf_pay", "platf_quest", "platf_questx", "platf_stat", "platf_warband"};
        List<Map<String, Object>> l = new ArrayList<>();
        Kv kv = Kv.of();
        StringBuffer buff = new StringBuffer();
        StringBuffer buff2 = new StringBuffer();
        for (String s : database) {

            String sql = "SELECT table_schema,TABLE_NAME" +
                    " FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = '" + s +
                    "' ";
            List<Map> list1 = findList(sql);
            for (int i = 0; i < list1.size(); i++) {
                Object table_schema = list1.get(i).get("TABLE_SCHEMA");
                String table_name = (String) list1.get(i).get("TABLE_NAME");
                String sql2 = "SELECT TABLE_SCHEMA, TABLE_NAME,COLUMN_NAME,COLUMN_DEFAULT,IS_NULLABLE,COLUMN_TYPE,COLUMN_COMMENT FROM information_schema.COLUMNS WHERE table_name = '" + table_name + "' and table_schema = '" + table_schema + "'; ";
                Map map = list1.get(i);
                List<Map> list = findList(sql2);
                list.forEach(x -> {
                    if (x.get("COLUMN_DEFAULT") == null || x.get("IS_NULLABLE").equals("YES")) {
//                    if (  x.get("IS_NULLABLE").equals("YES")&&x.get("COLUMN_DEFAULT") == null) {
                        x.putAll(map);

                        l.add(x);
                        Object column_default = x.get("COLUMN_DEFAULT");
//                        if (x.get("COLUMN_DEFAULT") == null) {
                        if (Utils.isEmpty(x.get("COLUMN_DEFAULT"))) {
                            buff.append("ALTER TABLE " + table_schema + "." + table_name + " MODIFY COLUMN " + x.get("COLUMN_NAME") + " " + x.get("COLUMN_TYPE"));
                            if (x.get("COLUMN_TYPE").toString().startsWith("v")) {
                                buff.append(" NOT NULL DEFAULT '' ");
                            } else if (x.get("COLUMN_TYPE").toString().startsWith("s") || x.get("COLUMN_TYPE").toString().startsWith("b") || x.get("COLUMN_TYPE").toString().startsWith("i")) {
                                buff.append(" NOT NULL DEFAULT 0 ");
                            } else if (x.get("COLUMN_TYPE").toString().startsWith("t") || x.get("COLUMN_TYPE").toString().startsWith("m")) {
                                buff.append(" CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL ");
                            } else if (x.get("COLUMN_TYPE").toString().startsWith("f") || x.get("COLUMN_TYPE").toString().startsWith("d")) {
                                buff.append(" NOT NULL DEFAULT 0.00 ");
                            }
                            String comment = x.get("COLUMN_COMMENT").toString();
                            if (comment.contains("-")) {
                                String[] split = comment.split("-");
                                if (split[0].endsWith("'")) {
                                    String s1 = split[0].replace("'", "‘");
                                    String s2 = split[1].replace("'", "’");
                                    comment = s1 + "-" + s2;
                                }
                            }
                            buff.append(" COMMENT '" + comment + "' ;" + "\n");
                        }
//                        if (x.get("IS_NULLABLE").equals("YES") && x.get("COLUMN_DEFAULT") != null) {
                        if (x.get("IS_NULLABLE").equals("YES") && !Utils.isEmpty(x.get("COLUMN_DEFAULT"))) {
                            buff.append("ALTER TABLE " + table_schema + "." + table_name + " MODIFY COLUMN " + x.get("COLUMN_NAME") + " " + x.get("COLUMN_TYPE") + " NOT NULL ");
                            String comment = x.get("COLUMN_COMMENT").toString();
                            if (comment.contains("-")) {
                                String[] split = comment.split("-");
                                if (split[0].endsWith("'")) {
                                    String s1 = split[0].replace("'", "‘");
                                    String s2 = split[1].replace("'", "’");
                                    comment = s1 + s2;
                                }
                            }
                            buff.append(" COMMENT '" + comment + "' ;" + "\n");
                        }
                        if (x.get("IS_NULLABLE").equals("YES")) {
                            buff2.append("UPDATE " + table_schema + "." + table_name + " SET " + x.get("COLUMN_NAME") + " = ");
                            if (x.get("COLUMN_TYPE").toString().startsWith("v") || x.get("COLUMN_TYPE").toString().startsWith("t") || x.get("COLUMN_TYPE").toString().startsWith("m")) {
                                buff2.append("''");
                            } else if (x.get("COLUMN_TYPE").toString().startsWith("s") || x.get("COLUMN_TYPE").toString().startsWith("b") || x.get("COLUMN_TYPE").toString().startsWith("i")) {
                                buff2.append(0);
                            } else if (x.get("COLUMN_TYPE").toString().startsWith("f") || x.get("COLUMN_TYPE").toString().startsWith("d")) {
                                buff2.append(0.00);
                            }
//                            else if (x.get("Type").toString().startsWith("t") || x.get("Type").toString().startsWith("m")) {
//                                buff2.append(" CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL ;" + "\n");
//                            }
                            buff2.append(" WHERE " + x.get("COLUMN_NAME") + " IS NULL ;" + "\n");
                        }

                    }
                });

            }
        }
        kv.set("TABLE_SCHEMA", "数据库名");
        kv.set("TABLE_NAME", "表名");
        kv.set("COLUMN_NAME", "字段名");
        kv.set("COLUMN_TYPE", "类型");
        kv.set("IS_NULLABLE", "是否为空");
        kv.set("COLUMN_COMMENT", "注释");
        kv.set("COLUMN_DEFAULT", "默认值");
        FileKit.strToFile(buff.toString(), new File("target/表字段默认值更新0822.sql"));
        FileKit.strToFile(buff2.toString(), new File("target/表字段值为Null更新0822.sql"));
        try {
            Workbook workbook = ExcelKit.exportExcel(l, kv);
            workbook.write(new FileOutputStream(new File("target/表字段默认值为null信息0822.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //运营数据统计
    @Test
    public void getdata() {
        DbAccount dbAccount = new DbAccount();
        dbAccount.setCate("mysql");
        dbAccount.setUrl("jdbc:mysql://122.112.180.156:6033/v09x_platf_core");
        dbAccount.setUser("root");
        dbAccount.setPwd("*Hello@27.com!");
        DbKit dbKit = new DbKit(dbAccount, "");

        //本周时间区间
        long starttime = 1599321600000l;
        long endtime = 1599753600000l;

        //上周时间区间
        long laststart = starttime - 5 * 24 * 3600000;
        long lastend = endtime - 5 * 24 * 3600000;
        //本周活跃用户
        String esResult = readFileAsString("C:\\Users\\wh\\Desktop\\yunying\\esResult.txt");
        SearchResult<SearchVisLogRecord> temp = JsonConvert.root().convertFrom((new TypeToken<SearchResult<SearchVisLogRecord>>() {
        }).getType(), esResult);
        Collection<SearchVisLogRecord> esResultList = temp.getSheet().getRows();

        System.out.println("本周DAU用户数：" + esResultList.size());

        //上周注册用户1590940800000
        String sql1 = "SELECT userid  FROM v09x_platf_core.`userdetail`  WHERE  regtime >= " + laststart + " AND regtime <= " + lastend + " and status = 10 and usertype != 21 ; ";
//        String sql1 = "SELECT userid  FROM v09x_platf_core.`userdetail`  WHERE  regtime >= 1592150400000 AND regtime <= 1592755200000 and status = 10 ; ";
        List<Map> list1 = dbKit.findList(sql1, Map.class);
        HashSet<Integer> set1 = new HashSet<>();
        list1.forEach(x -> set1.add((Integer) x.get("userid")));
        System.out.println("上周注册用户数：" + set1.size());

        //上周注册用户与本周活跃用户比对
        HashSet<Integer> set2 = new HashSet<>();
        esResultList.forEach(x -> {
            if (set1.contains(x.getUserid())) {
                set2.add(x.getUserid());
            }
        });

        System.out.println("上周注册用户在本周活跃人数：" + set2.size());
        double a = (double) set2.size() / (double) set1.size();
        //获取格式化对象
        NumberFormat nt = NumberFormat.getPercentInstance();
        //设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(2);
        //最后格式化并输出
        System.out.println("次留周百分数：" + nt.format(a));

        //本周注册用户数
        String usersql = "SELECT userid FROM v09x_platf_core.`userdetail` WHERE regtime >= " + starttime + " AND regtime <= " + endtime + " AND status=10 and usertype != 21 ; ";
        List<Map> users = dbKit.findList(usersql, Map.class);
        System.out.println("本周注册用户数：" + users.size());

        //本周新增贴数
        String articlesql = "SELECT articleid FROM v09x_platf_core.articleinfo WHERE  createtime >= " + starttime + " AND createtime <= " + endtime + " and status !=80 ; ";
        List<Map> articles = dbKit.findList(articlesql, Map.class);
        System.out.println("本周新增贴数：" + articles.size());

        //本周新增推广员人数
        String promotersql = "SELECT * FROM platf_oth.promoterrecord  WHERE  createtime >= " + starttime + " AND createtime <= " + endtime + " and status = 10 ; ";
        List<Map> promoters = dbKit.findList(promotersql, Map.class);
        System.out.println("本周新增推广员人数：" + promoters.size());

        //本周活动参与用户数
        String activitysql = "SELECT userid FROM platf_oth.articleactivity WHERE  createtime >= " + starttime + " AND createtime <= " + endtime + " ; ";
        List<Map> activitys = dbKit.findList(activitysql, Map.class);
        HashSet<Integer> activityset = new HashSet<>();
        activitys.forEach(x -> activityset.add((Integer) x.get("userid")));
        System.out.println("本周活动参与用户数：" + activityset.size());
        //六一活动参与用户通过获取徽章查询
        String sixsql = "SELECT userid FROM platf_questx.userbadge WHERE badgeid = 26 AND createtime >= " + starttime + " AND createtime <= " + endtime + " ; ";
        List<Map> six = dbKit.findList(sixsql, Map.class);
        System.out.println("儿童节活动参与用户数：" + six.size());

        //本周新增会员数  取值逻辑有待确定
        String vipsql = "SELECT * FROM v09x_platf_core.vippayrecord  WHERE  createtime >= " + starttime + " AND createtime <= " + endtime + " and status = 10 ; ";
        List<Map> vips = dbKit.findList(vipsql, Map.class);
        System.out.println("本周新增会员数：" + vips.size());

        List<Map<String, Object>> l = new ArrayList<>();
        Kv sheet1 = Kv.of();
        List<Kv> kvs = new ArrayList<>();
        kvs.add(Kv.of("name", "本周注册用户数").set("num", users.size()));
        kvs.add(Kv.of("name", "次留周百分数").set("num", nt.format(a)));
        kvs.add(Kv.of("name", "本周DAU用户数").set("num", esResultList.size()));
        kvs.add(Kv.of("name", "本周新增贴数").set("num", articles.size()));
        kvs.add(Kv.of("name", "本周新增推广员人数").set("num", promoters.size()));
        kvs.add(Kv.of("name", "本周活动参与用户数").set("num", activityset.size() + six.size()));
        kvs.add(Kv.of("name", "本周新增会员数").set("num", vips.size()));
        sheet1.set("data", kvs);
        sheet1.set("sheetName", "运营数据");
        sheet1.set("hdNames", new String[]{"名称", "数量"});
        sheet1.set("hds", new String[]{"name", "num"});
        l.add(sheet1);
        Workbook wb = ExcelKit.exportExcels(l);
        try {
            wb.write(new FileOutputStream(new File("target/运营数据0828.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //摆摊数据统计
    @Test
    public void getStall() {
        DbAccount dbAccount = new DbAccount();
        dbAccount.setCate("mysql");
        dbAccount.setUrl("jdbc:mysql://122.112.180.156:6033/v09x_platf_core");
        dbAccount.setUser("root");
        dbAccount.setPwd("*Hello@27.com!");
        DbKit dbKit = new DbKit(dbAccount, "");

        //本周时间区间
        long starttime = 1598198400000l;
        long endtime = 1598803200000l;

        //上周时间区间
        long laststart = starttime - 7 * 24 * 3600000;
        long lastend = endtime - 7 * 24 * 3600000;

        //本周摊位的用户记录(包括前一天)
        String esResult = readFileAsString("C:\\Users\\wh\\Desktop\\yunying\\esResult.txt");
        SearchResult<SearchVisLogRecord> temp = JsonConvert.root().convertFrom((new TypeToken<SearchResult<SearchVisLogRecord>>() {
        }).getType(), esResult);


        Collection<SearchVisLogRecord> esResultList = temp.getSheet().getRows();
        //上周日访问用户
        HashSet<Integer> lastdayset = new HashSet<>();
        //周一访问用户
        HashSet<Integer> mondayset = new HashSet<>();
        //周二访问用户
        HashSet<Integer> tuesdayset = new HashSet<>();
        //周三访问用户
        HashSet<Integer> wednesdayset = new HashSet<>();
        //周四访问用户
        HashSet<Integer> thursdayset = new HashSet<>();
        //周五访问用户
        HashSet<Integer> fridayset = new HashSet<>();
        //周六访问用户
        HashSet<Integer> saturdayset = new HashSet<>();
        //周日访问用户
        HashSet<Integer> sundayset = new HashSet<>();
        esResultList.forEach(x -> {
            long createtime = x.getCreatetime();
            if (createtime >= starttime && createtime <= starttime + 24 * 3600000) {
                mondayset.add(x.getUserid());
            } else if (createtime >= starttime + 24 * 3600000 && createtime <= starttime + 2 * 24 * 3600000) {
                tuesdayset.add(x.getUserid());
            } else if (createtime >= starttime + 2 * 24 * 3600000 && createtime <= starttime + 3 * 24 * 3600000) {
                wednesdayset.add(x.getUserid());
            } else if (createtime >= starttime + 3 * 24 * 3600000 && createtime <= starttime + 4 * 24 * 3600000) {
                thursdayset.add(x.getUserid());
            } else if (createtime >= starttime + 4 * 24 * 3600000 && createtime <= starttime + 5 * 24 * 3600000) {
                fridayset.add(x.getUserid());
            } else if (createtime >= starttime + 5 * 24 * 3600000 && createtime <= starttime + 6 * 24 * 3600000) {
                saturdayset.add(x.getUserid());
            } else if (createtime >= starttime + 6 * 24 * 3600000 && createtime <= starttime + 7 * 24 * 3600000) {
                sundayset.add(x.getUserid());
            } else if (createtime >= starttime - 24 * 3600000 && createtime <= starttime) {
                lastdayset.add(x.getUserid());
            }

        });

        //获取格式化对象
        NumberFormat nt = NumberFormat.getPercentInstance();
        //设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(2);

        //周一次留
        HashSet<Integer> mondayset1 = new HashSet<>();
        lastdayset.forEach(x -> {
            if (mondayset.contains(x)) {
                mondayset1.add(x);
            }
        });
        double monday = lastdayset.size() > 0 ? (double) mondayset1.size() / (double) lastdayset.size() : 0;

        //周二次留
        HashSet<Integer> tuesdayset1 = new HashSet<>();
        mondayset.forEach(x -> {
            if (tuesdayset.contains(x)) {
                tuesdayset1.add(x);
            }
        });
        double tuesday = mondayset.size() > 0 ? (double) tuesdayset1.size() / (double) mondayset.size() : 0;

        //周三次留
        HashSet<Integer> wednesdayset1 = new HashSet<>();
        tuesdayset.forEach(x -> {
            if (wednesdayset.contains(x)) {
                wednesdayset1.add(x);
            }
        });
        double wednesday = tuesdayset.size() > 0 ? (double) wednesdayset1.size() / (double) tuesdayset.size() : 0;

        //周四次留
        HashSet<Integer> thursdayset1 = new HashSet<>();
        wednesdayset.forEach(x -> {
            if (thursdayset.contains(x)) {
                thursdayset1.add(x);
            }
        });
        double thursday = wednesdayset.size() > 0 ? (double) thursdayset1.size() / (double) wednesdayset.size() : 0;

        //周五次留
        HashSet<Integer> fridayset1 = new HashSet<>();
        thursdayset.forEach(x -> {
            if (fridayset.contains(x)) {
                fridayset1.add(x);
            }
        });
        double friday = thursdayset.size() > 0 ? (double) fridayset1.size() / (double) thursdayset.size() : 0;

        //周六次留
        HashSet<Integer> saturdayset1 = new HashSet<>();
        fridayset.forEach(x -> {
            if (saturdayset.contains(x)) {
                saturdayset1.add(x);
            }
        });
        double saturday = fridayset.size() > 0 ? (double) saturdayset1.size() / (double) fridayset.size() : 0;

        //周日次留
        HashSet<Integer> sundayset1 = new HashSet<>();
        saturdayset.forEach(x -> {
            if (sundayset.contains(x)) {
                sundayset1.add(x);
            }
        });
        double sunday = saturdayset.size() > 0 ? (double) sundayset1.size() / (double) saturdayset.size() : 0;
        //最后格式化并输出
        System.out.println("周一次留：" + nt.format(monday));
        System.out.println("周二次留：" + nt.format(tuesday));
        System.out.println("周三次留：" + nt.format(wednesday));
        System.out.println("周四次留：" + nt.format(thursday));
        System.out.println("周五次留：" + nt.format(friday));
        System.out.println("周六次留：" + nt.format(saturday));
        System.out.println("周日次留：" + nt.format(sunday));

        //===================================================================================================

        //分享率   stall在分享记录里的type=120     时间区间被分享总次数/时间区间访问摊位的总人数
        // 分享数
        String mondaysharesql = "SELECT userid FROM platf_quest.`sharerecord` WHERE createtime >= " + starttime + " AND createtime <= " + (starttime + 24 * 3600000) + " AND type=120 ; ";
        String tuesdaysharesql = "SELECT userid FROM platf_quest.`sharerecord` WHERE createtime >= " + (starttime + 24 * 3600000) + " AND createtime <= " + (starttime + 2 * 24 * 3600000) + " AND type=120 ; ";
        String wednesdaysharesql = "SELECT userid FROM platf_quest.`sharerecord` WHERE createtime >= " + (starttime + 2 * 24 * 3600000) + " AND createtime <= " + (starttime + 3 * 24 * 3600000) + " AND type=120 ; ";
        String thursdaysharesql = "SELECT userid FROM platf_quest.`sharerecord` WHERE createtime >= " + (starttime + 3 * 24 * 3600000) + " AND createtime <= " + (starttime + 4 * 24 * 3600000) + " AND type=120 ; ";
        String fridaysharesql = "SELECT userid FROM platf_quest.`sharerecord` WHERE createtime >= " + (starttime + 4 * 24 * 3600000) + " AND createtime <= " + (starttime + 5 * 24 * 3600000) + " AND type=120 ; ";
        String saturdaysharesql = "SELECT userid FROM platf_quest.`sharerecord` WHERE createtime >= " + (starttime + 5 * 24 * 3600000) + " AND createtime <= " + (starttime + 6 * 24 * 3600000) + " AND type=120 ; ";
        String sundaysharesql = "SELECT userid FROM platf_quest.`sharerecord` WHERE createtime >= " + (starttime + 6 * 24 * 3600000) + " AND createtime <= " + (starttime + 7 * 24 * 3600000) + " AND type=120 ; ";

        List<Map> mondayshares = dbKit.findList(mondaysharesql, Map.class);
        List<Map> tuesdayshares = dbKit.findList(tuesdaysharesql, Map.class);
        List<Map> wednesdayshares = dbKit.findList(wednesdaysharesql, Map.class);
        List<Map> thursdayshares = dbKit.findList(thursdaysharesql, Map.class);
        List<Map> fridayshares = dbKit.findList(fridaysharesql, Map.class);
        List<Map> saturdayshares = dbKit.findList(saturdaysharesql, Map.class);
        List<Map> sundayshares = dbKit.findList(sundaysharesql, Map.class);

        //分享率
        double mondayshare = mondayset.size() > 0 ? (double) mondayshares.size() / (double) mondayset.size() : 0;
        double tuesdayshare = tuesdayset.size() > 0 ? (double) tuesdayshares.size() / (double) tuesdayset.size() : 0;
        double wednesdayshare = wednesdayset.size() > 0 ? (double) wednesdayshares.size() / (double) wednesdayset.size() : 0;
        double thursdayshare = thursdayset.size() > 0 ? (double) thursdayshares.size() / (double) thursdayset.size() : 0;
        double fridayshare = fridayset.size() > 0 ? (double) fridayshares.size() / (double) fridayset.size() : 0;
        double saturdayshare = saturdayset.size() > 0 ? (double) saturdayshares.size() / (double) saturdayset.size() : 0;
        double sundayshare = sundayset.size() > 0 ? (double) sundayshares.size() / (double) sundayset.size() : 0;

        System.out.println("周一分享率：" + nt.format(mondayshare));
        System.out.println("周二分享率：" + nt.format(tuesdayshare));
        System.out.println("周三分享率：" + nt.format(wednesdayshare));
        System.out.println("周四分享率：" + nt.format(thursdayshare));
        System.out.println("周五分享率：" + nt.format(fridayshare));
        System.out.println("周六分享率：" + nt.format(saturdayshare));
        System.out.println("周日分享率：" + nt.format(sundayshare));
        //===================================================================================================

        //新增充值用户数据
        String mondaysql = "SELECT userid FROM platf_oth.`promoterrecord` WHERE createtime >= " + starttime + " AND createtime <= " + (starttime + 24 * 3600000) + " AND status=10 ; ";
        String tuesdaysql = "SELECT userid FROM platf_oth.`promoterrecord` WHERE createtime >= " + (starttime + 24 * 3600000) + " AND createtime <= " + (starttime + 2 * 24 * 3600000) + " AND status=10 ; ";
        String wednesdaysql = "SELECT userid FROM platf_oth.`promoterrecord` WHERE createtime >= " + (starttime + 2 * 24 * 3600000) + " AND createtime <= " + (starttime + 3 * 24 * 3600000) + " AND status=10 ; ";
        String thursdaysql = "SELECT userid FROM platf_oth.`promoterrecord` WHERE createtime >= " + (starttime + 3 * 24 * 3600000) + " AND createtime <= " + (starttime + 4 * 24 * 3600000) + " AND status=10 ; ";
        String fridaysql = "SELECT userid FROM platf_oth.`promoterrecord` WHERE createtime >= " + (starttime + 4 * 24 * 3600000) + " AND createtime <= " + (starttime + 5 * 24 * 3600000) + " AND status=10 ; ";
        String saturdaysql = "SELECT userid FROM platf_oth.`promoterrecord` WHERE createtime >= " + (starttime + 5 * 24 * 3600000) + " AND createtime <= " + (starttime + 6 * 24 * 3600000) + " AND status=10 ; ";
        String sundaysql = "SELECT userid FROM platf_oth.`promoterrecord` WHERE createtime >= " + (starttime + 6 * 24 * 3600000) + " AND createtime <= " + (starttime + 7 * 24 * 3600000) + " AND status=10 ; ";
        String stalls = "SELECT owner FROM platf_oth.`stallinfo` WHERE status=10 ; ";

        List<Map> mondayusers = dbKit.findList(mondaysql, Map.class);
        List<Map> tuesdayusers = dbKit.findList(tuesdaysql, Map.class);
        List<Map> wednesdayusers = dbKit.findList(wednesdaysql, Map.class);
        List<Map> thursdayusers = dbKit.findList(thursdaysql, Map.class);
        List<Map> fridayusers = dbKit.findList(fridaysql, Map.class);
        List<Map> saturdayusers = dbKit.findList(saturdaysql, Map.class);
        List<Map> sundayusers = dbKit.findList(sundaysql, Map.class);
        List<Map> stallusers = dbKit.findList(stalls, Map.class);
        HashSet<Integer> mondayuserset = new HashSet<>();
        HashSet<Integer> tuesdayuserset = new HashSet<>();
        HashSet<Integer> wednesdayuserset = new HashSet<>();
        HashSet<Integer> thursdayuserset = new HashSet<>();
        HashSet<Integer> fridayuserset = new HashSet<>();
        HashSet<Integer> saturdayuserset = new HashSet<>();
        HashSet<Integer> sundayuserset = new HashSet<>();
        HashSet<Integer> stalluserssset = new HashSet<>();
        HashSet<Integer> mondayuserset1 = new HashSet<>();
        HashSet<Integer> tuesdayuserset1 = new HashSet<>();
        HashSet<Integer> wednesdayuserset1 = new HashSet<>();
        HashSet<Integer> thursdayuserset1 = new HashSet<>();
        HashSet<Integer> fridayuserset1 = new HashSet<>();
        HashSet<Integer> saturdayuserset1 = new HashSet<>();
        HashSet<Integer> sundayuserset1 = new HashSet<>();
        mondayusers.forEach(x -> mondayuserset.add((Integer) x.get("userid")));
        tuesdayusers.forEach(x -> tuesdayuserset.add((Integer) x.get("userid")));
        wednesdayusers.forEach(x -> wednesdayuserset.add((Integer) x.get("userid")));
        thursdayusers.forEach(x -> thursdayuserset.add((Integer) x.get("userid")));
        fridayusers.forEach(x -> fridayuserset.add((Integer) x.get("userid")));
        saturdayusers.forEach(x -> saturdayuserset.add((Integer) x.get("userid")));
        sundayusers.forEach(x -> sundayuserset.add((Integer) x.get("userid")));
        stallusers.forEach(x -> stalluserssset.add((Integer) x.get("owner")));

        stalluserssset.forEach(x -> {
            if (mondayuserset.contains(x)) {
                mondayuserset1.add(x);
            } else if (tuesdayuserset.contains(x)) {
                tuesdayuserset1.add(x);
            } else if (wednesdayuserset.contains(x)) {
                wednesdayuserset1.add(x);
            } else if (thursdayuserset.contains(x)) {
                thursdayuserset1.add(x);
            } else if (fridayuserset.contains(x)) {
                fridayuserset1.add(x);
            } else if (saturdayuserset.contains(x)) {
                saturdayuserset1.add(x);
            } else if (sundayuserset.contains(x)) {
                sundayuserset1.add(x);
            }
        });

        System.out.println("周一注册新增充值用户数据：" + mondayuserset1.size());
        System.out.println("周二注册新增充值用户数据：" + tuesdayuserset1.size());
        System.out.println("周三注册新增充值用户数据：" + wednesdayuserset1.size());
        System.out.println("周四注册新增充值用户数据：" + thursdayuserset1.size());
        System.out.println("周五注册新增充值用户数据：" + fridayuserset1.size());
        System.out.println("周六注册新增充值用户数据：" + saturdayuserset1.size());
        System.out.println("周日注册新增充值用户数据：" + sundayuserset1.size());

        //===================================================================================================

        //新增摆摊人数
        String mondaysql1 = "SELECT owner FROM platf_oth.`stallinfo` WHERE createtime >= " + starttime + " AND createtime <= " + (starttime + 24 * 3600000) + " AND status=10 ; ";
        String tuesdaysql1 = "SELECT owner FROM platf_oth.`stallinfo` WHERE createtime >= " + (starttime + 24 * 3600000) + " AND createtime <= " + (starttime + 2 * 24 * 3600000) + " AND status=10 ; ";
        String wednesdaysql1 = "SELECT owner FROM platf_oth.`stallinfo` WHERE createtime >= " + (starttime + 2 * 24 * 3600000) + " AND createtime <= " + (starttime + 3 * 24 * 3600000) + " AND status=10 ; ";
        String thursdaysql1 = "SELECT owner FROM platf_oth.`stallinfo` WHERE createtime >= " + (starttime + 3 * 24 * 3600000) + " AND createtime <= " + (starttime + 4 * 24 * 3600000) + " AND status=10 ; ";
        String fridaysql1 = "SELECT owner FROM platf_oth.`stallinfo` WHERE createtime >= " + (starttime + 4 * 24 * 3600000) + " AND createtime <= " + (starttime + 5 * 24 * 3600000) + " AND status=10 ; ";
        String saturdaysql1 = "SELECT owner FROM platf_oth.`stallinfo` WHERE createtime >= " + (starttime + 5 * 24 * 3600000) + " AND createtime <= " + (starttime + 6 * 24 * 3600000) + " AND status=10 ; ";
        String sundaysql1 = "SELECT owner FROM platf_oth.`stallinfo` WHERE createtime >= " + (starttime + 6 * 24 * 3600000) + " AND createtime <= " + (starttime + 7 * 24 * 3600000) + " AND status=10 ; ";

        List<Map> mondayusers1 = dbKit.findList(mondaysql1, Map.class);
        List<Map> tuesdayusers1 = dbKit.findList(tuesdaysql1, Map.class);
        List<Map> wednesdayusers1 = dbKit.findList(wednesdaysql1, Map.class);
        List<Map> thursdayusers1 = dbKit.findList(thursdaysql1, Map.class);
        List<Map> fridayusers1 = dbKit.findList(fridaysql1, Map.class);
        List<Map> saturdayusers1 = dbKit.findList(saturdaysql1, Map.class);
        List<Map> sundayusers1 = dbKit.findList(sundaysql1, Map.class);

        System.out.println("周一注册新增摆摊用户数据：" + mondayusers1.size());
        System.out.println("周二注册新增摆摊用户数据：" + tuesdayusers1.size());
        System.out.println("周三注册新增摆摊用户数据：" + wednesdayusers1.size());
        System.out.println("周四注册新增摆摊用户数据：" + thursdayusers1.size());
        System.out.println("周五注册新增摆摊用户数据：" + fridayusers1.size());
        System.out.println("周六注册新增摆摊用户数据：" + saturdayusers1.size());
        System.out.println("周日注册新增摆摊用户数据：" + sundayusers1.size());


        //===================================================================================================

        //新增邀请人数  stalluserssset摆摊用户数据
        String mondaysql2 = "SELECT userid FROM platf_quest.`userinviterecord` WHERE createtime >= " + starttime + " AND createtime <= " + (starttime + 24 * 3600000) + " AND finishstatus=10 OR finishstatus=20; ";
        String tuesdaysql2 = "SELECT userid FROM platf_quest.`userinviterecord` WHERE createtime >= " + (starttime + 24 * 3600000) + " AND createtime <= " + (starttime + 2 * 24 * 3600000) + " AND finishstatus=10 OR finishstatus=20; ";
        String wednesdaysql2 = "SELECT userid FROM platf_quest.`userinviterecord` WHERE createtime >= " + (starttime + 2 * 24 * 3600000) + " AND createtime <= " + (starttime + 3 * 24 * 3600000) + " AND finishstatus=10 OR finishstatus=20; ";
        String thursdaysql2 = "SELECT userid FROM platf_quest.`userinviterecord` WHERE createtime >= " + (starttime + 3 * 24 * 3600000) + " AND createtime <= " + (starttime + 4 * 24 * 3600000) + " AND finishstatus=10 OR finishstatus=20; ";
        String fridaysql2 = "SELECT userid FROM platf_quest.`userinviterecord` WHERE createtime >= " + (starttime + 4 * 24 * 3600000) + " AND createtime <= " + (starttime + 5 * 24 * 3600000) + " AND finishstatus=10 OR finishstatus=20; ";
        String saturdaysql2 = "SELECT userid FROM platf_quest.`userinviterecord` WHERE createtime >= " + (starttime + 5 * 24 * 3600000) + " AND createtime <= " + (starttime + 6 * 24 * 3600000) + " AND finishstatus=10 OR finishstatus=20; ";
        String sundaysql2 = "SELECT userid FROM platf_quest.`userinviterecord` WHERE createtime >= " + (starttime + 6 * 24 * 3600000) + " AND createtime <= " + (starttime + 7 * 24 * 3600000) + " AND finishstatus=10 OR finishstatus=20; ";

        List<Map> mondayinvites = dbKit.findList(mondaysql2, Map.class);
        List<Map> tuesdayuinvites = dbKit.findList(tuesdaysql2, Map.class);
        List<Map> wednesdayinvites = dbKit.findList(wednesdaysql2, Map.class);
        List<Map> thursdayuinvites = dbKit.findList(thursdaysql2, Map.class);
        List<Map> fridayinvites = dbKit.findList(fridaysql2, Map.class);
        List<Map> saturdayinvites = dbKit.findList(saturdaysql2, Map.class);
        List<Map> sundayinvites = dbKit.findList(sundaysql2, Map.class);

        ArrayList<Integer> mondayuserList = new ArrayList<>();
        ArrayList<Integer> tuesdayuserList = new ArrayList<>();
        ArrayList<Integer> wednesdayuserList = new ArrayList<>();
        ArrayList<Integer> thursdayuserList = new ArrayList<>();
        ArrayList<Integer> fridayuserList = new ArrayList<>();
        ArrayList<Integer> saturdayuserList = new ArrayList<>();
        ArrayList<Integer> sundayuserList = new ArrayList<>();

        ArrayList<Integer> mondayuserList1 = new ArrayList<>();
        ArrayList<Integer> tuesdayuserList1 = new ArrayList<>();
        ArrayList<Integer> wednesdayuserList1 = new ArrayList<>();
        ArrayList<Integer> thursdayuserList1 = new ArrayList<>();
        ArrayList<Integer> fridayuserList1 = new ArrayList<>();
        ArrayList<Integer> saturdayuserList1 = new ArrayList<>();
        ArrayList<Integer> sundayuserList1 = new ArrayList<>();

        mondayinvites.forEach(x -> mondayuserList.add((Integer) x.get("userid")));
        tuesdayuinvites.forEach(x -> tuesdayuserList.add((Integer) x.get("userid")));
        wednesdayinvites.forEach(x -> wednesdayuserList.add((Integer) x.get("userid")));
        thursdayuinvites.forEach(x -> thursdayuserList.add((Integer) x.get("userid")));
        fridayinvites.forEach(x -> fridayuserList.add((Integer) x.get("userid")));
        saturdayinvites.forEach(x -> saturdayuserList.add((Integer) x.get("userid")));
        sundayinvites.forEach(x -> sundayuserList.add((Integer) x.get("userid")));

        mondayuserList.forEach(x -> {
            if (stalluserssset.contains(x)) {
                mondayuserList1.add(x);
            }
        });
        tuesdayuserList.forEach(x -> {
            if (stalluserssset.contains(x)) {
                tuesdayuserList1.add(x);
            }
        });
        wednesdayuserList.forEach(x -> {
            if (stalluserssset.contains(x)) {
                wednesdayuserList1.add(x);
            }
        });
        thursdayuserList.forEach(x -> {
            if (stalluserssset.contains(x)) {
                thursdayuserList1.add(x);
            }
        });
        fridayuserList.forEach(x -> {
            if (stalluserssset.contains(x)) {
                fridayuserList1.add(x);
            }
        });
        saturdayuserList.forEach(x -> {
            if (stalluserssset.contains(x)) {
                saturdayuserList1.add(x);
            }
        });
        sundayuserList.forEach(x -> {
            if (stalluserssset.contains(x)) {
                sundayuserList1.add(x);
            }
        });

        System.out.println("周一新增邀请用户数据：" + mondayuserList1.size());
        System.out.println("周二新增邀请用户数据：" + tuesdayuserList1.size());
        System.out.println("周三新增邀请用户数据：" + wednesdayuserList1.size());
        System.out.println("周四新增邀请用户数据：" + thursdayuserList1.size());
        System.out.println("周五新增邀请用户数据：" + fridayuserList1.size());
        System.out.println("周六新增邀请用户数据：" + saturdayuserList1.size());
        System.out.println("周日新增邀请用户数据：" + sundayuserList1.size());



       /* List<Map<String, Object>> l = new ArrayList<>();
        Kv sheet1 = Kv.of();
        List<Kv> kvs = new ArrayList<>();
        kvs.add(Kv.of("name", "本周注册用户数").set("num", users.size()));
        kvs.add(Kv.of("name", "次留周百分数").set("num", nt.format(a)));
        kvs.add(Kv.of("name", "本周DAU用户数").set("num", esResultList.size()));
        kvs.add(Kv.of("name", "本周新增贴数").set("num", articles.size()));
        kvs.add(Kv.of("name", "本周新增推广员人数").set("num", promoters.size()));
        kvs.add(Kv.of("name", "本周活动参与用户数").set("num", activityset.size() + six.size()));
        kvs.add(Kv.of("name", "本周新增会员数").set("num", vips.size()));
        sheet1.set("data", kvs);
        sheet1.set("sheetName", "运营数据");
        sheet1.set("hdNames", new String[]{"名称", "数量"});
        sheet1.set("hds", new String[]{"name", "num"});
        l.add(sheet1);
        Workbook wb = ExcelKit.exportExcels(l);
        try {
            wb.write(new FileOutputStream(new File("target/运营数据0808.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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

    //评论数据insert语句；
    @Test
    public void insertgamecomment() throws FileNotFoundException {
        StringBuffer buff = new StringBuffer();
        String[] FIELDS = {"gameid", "", "userno", "commentcontent", "score", "supportcount"};

        List<Map> list = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\评论0717.xlsx"), FIELDS);
        list.remove(0);//去除多余的行首
        HashSet<Object> set = new HashSet<>();
        list.forEach(x -> set.add(x.get("userno")));
        set.remove("用户ID");
        String sql = "SELECT userno,userid FROM userdetail WHERE userno IN (" + set.toString().replace("[", "").replace("]", "").trim() + ");";
        List<Map> list1 = findList(sql);
        Map<Object, Object> map = new HashMap<>();
        list1.forEach(x -> {
            map.put(Integer.parseInt(x.get("userno").toString()), x.get("userid"));
        });
        System.out.println(map.size());

        buff.append("INSERT INTO `v09x_platf_core`.`gamecomment`(`commentid`, `userid`, `gameid`, `versionid`, `commentcontent`,`supportcount`,`createtime`) VALUES  \n");
        Random random = new Random();
        //所有评论发表时间在5月20日——6月29日之间随机  1589904000000   1593446400000
        List<Map<String, Object>> l = new ArrayList<>();
        Kv kv = Kv.of();

        list.forEach(x -> {
            if (x.get("gameid").toString() == "" || x.get("commentcontent").toString() == "" || x.get("supportcount").toString() == "" || x.get("score").toString() == "") {
                return;
            }

            HashMap<String, Object> map1 = new HashMap<>();
            long createtime = 1589904000000l + (long) (Math.random() * (1593446400000l - 1589904000000l));
            int userid = (int) Float.parseFloat(x.get("userno").toString());
            int gameid = (int) Float.parseFloat(x.get("gameid").toString());
            String commentcontent = x.get("commentcontent").toString();

            int supportcount = (int) Float.parseFloat(x.get("supportcount").toString());
            String score = x.get("score").toString();
            String commentid = map.get(userid) + "-" + Utility.format36time(createtime);

            buff.append(String.format("('%s',%s,'%s','%s','%s',%s,%s),\n", commentid, map.get(userid), gameid, gameid + "-0", commentcontent, supportcount, createtime));


            map1.put("commentid", commentid);
            map1.put("userid", map.get(userid));
            map1.put("gameid", gameid);
            map1.put("commentcontent", commentcontent);
            map1.put("createtime", createtime);
            map1.put("supportcount", supportcount);
            map1.put("score", score);
            l.add(map1);

        });
        kv.set("commentid", "评论ID");
        kv.set("userid", "用户id");
        kv.set("gameid", "游戏id");
        kv.set("commentcontent", "评论内容");
        kv.set("createtime", "创建时间");
        kv.set("supportcount", "点赞数");
        kv.set("score", "评分");

        buff.delete(buff.length() - 2, buff.length() + 1);
        buff.append(";");
        // 入库数据
        FileKit.strToFile(buff.toString(), new File("tmp/游戏评论数据0717.sql"));
        try {
            Workbook workbook = ExcelKit.exportExcel(l, kv);
            workbook.write(new FileOutputStream(new File("target/编辑评论录入数据0717.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //评论数据insert语句；
    @Test
    public void gamecomment() throws IOException, InterruptedException {
        StringBuffer buff = new StringBuffer();
//        String[] FIELDS = {"gameid", "", "userno", "commentcontent", "score", "supportcount"};
//        String[] FIELDS = {"gameid", "", "userid", "commentcontent", "score", "supportcount"};
        String[] FIELDS = {"userid", "commentcontent", "score1", "score2", "score3", "score4", "score5", "score6"};

        List<Map> list = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\评论.xls"), FIELDS);
        list.remove(0);//去除多余的行首
       /* HashSet<Object> set = new HashSet<>();
        list.forEach(x -> {
            if (x.get("userno").toString() != "") {

                set.add((int) Float.parseFloat(x.get("userno").toString()));
            }
        });*/

       /* String sql = "SELECT userno,userid FROM userdetail WHERE userno IN (" + set.toString().replace("[", "").replace("]", "").trim() + ");";
        List<Map> list1 = findList(sql);
        Map<Integer, Integer> map = new HashMap<>();
        list1.forEach(x -> {
            map.put(Integer.parseInt(x.get("userno").toString()), Integer.parseInt(x.get("userid").toString()));
        });
        System.out.println(map.size());*/

        List<Map<String, Object>> l = new ArrayList<>();
        List<Map<String, Object>> l1 = new ArrayList<>();
        Kv kv = Kv.of();
        //======================================================
        StringBuffer buffer1 = new StringBuffer();
        buffer1.append("{mobile:15697177873,vercode:159753}");
        String login = "https://api.woaihaoyouxi.com/account/login";
        HashMap<String, Object> loginmap = new HashMap<>();
        loginmap.put("bean", buffer1.toString());
        HttpResponse<String> httpResponse1 = HttpUtils.send(login, loginmap, HttpUtils.HttpMethod.GET);
        JSONObject jsonResule = JSONObject.fromObject(httpResponse1.body());
        System.out.println(httpResponse1.body());
        System.out.println(httpResponse1.request().uri());
        JSONObject attachJSON = jsonResule.getJSONObject("attach");
        String jsessionid = attachJSON.getString("sessionid");

        int count = 0;
        list.forEach(x -> {
            /*if (x.get("gameid").toString() == "" || x.get("commentcontent").toString() == "" || x.get("supportcount").toString() == "" || x.get("score").toString() == "") {
                return;
            }*/
            if (x.get("commentcontent").toString() == "") {
                return;
            }
            HashMap<String, Object> map1 = new HashMap<>();
            HashMap<String, Object> map2 = new HashMap<>();
//            long createtime = 1589904000000l + (long) (Math.random() * (1593446400000l - 1589904000000l));
            //评论的时间区间
            long createtime = 1597939620000l + (long) (Math.random() * (1598199780000l - 1597939620000l));
            /*int userno = 0;
            int userid = 0;
            if (x.get("userno").toString() != "") {

                userno = (int) Float.parseFloat(x.get("userno").toString());
            }
            if (map.get(userno) != null) {

                userid = map.get(userno);
            }*/
            int userid = (int) Float.parseFloat(x.get("userid").toString());
//            String gameid = ((Number) (int) Float.parseFloat(x.get("gameid").toString())).toString();
            String gameid = "23082";
            String commentcontent = x.get("commentcontent").toString();
            int supportcount = (int) Float.parseFloat(x.get("supportcount").toString());
            float score = Float.parseFloat(x.get("score").toString());

            int s1 = new Random().nextInt(50);
            float score1 = score + s1 / 10f;
            if (score1 >= 100) {
                score1 = 100f;
            } else if (score1 < 50) {
                score1 = score;
            }
            int s2 = new Random().nextInt(50);
            float score2 = score - s2 / 10f;
            if (score2 >= 100) {
                score2 = 100f;
            } else if (score2 < 50) {
                score2 = score;
            }
            int s3 = new Random().nextInt(50);
            float score3 = score + s3 / 10f;
            if (score3 >= 100) {
                score3 = 100f;
            } else if (score3 < 50) {
                score3 = score;
            }
            int s4 = new Random().nextInt(50);
            float score4 = score - s4 / 10f;
            if (score4 >= 100) {
                score4 = 100f;
            } else if (score4 < 50) {
                score4 = score;
            }
            int s5 = new Random().nextInt(50);
            float score5 = score + s5 / 10f;
            if (score5 >= 100) {
                score5 = 100f;
            } else if (score5 < 50) {
                score5 = score;
            }


            //=============================================

            StringBuffer buffer = new StringBuffer();
            buffer.append("{gameid:'" + gameid + "',commentcontent:'");
            String encode = URLEncoder.encode(commentcontent);
//            buffer.append(encode);
            buffer.append("commentcontent");
            buffer.append("',experience:" + score + " ,frame:" + score1 + " ,music:" + score2 + ",operation:" + score3 + " ,fluent:" + score4 + " ,original:" + score5 + "}");

            //==================================
            String url = "https://api.woaihaoyouxi.com/game/comment_create_tmp";
            HashMap<String, Object> mapx = new HashMap<>();
            Map<String, Object> mmm = new HashMap<>();
            mmm.put("gameid", gameid);
            mmm.put("commentcontent", commentcontent);
            mmm.put("experience", score);
            mmm.put("frame", score1);
            mmm.put("music", score2);
            mmm.put("fluent", score3);
            mmm.put("original", score4);
            mmm.put("operation", score5);
            mapx.put("bean", mmm);
            mapx.put("targetid", userid);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("jsessionid", jsessionid);

            HttpResponse<String> httpResponse = HttpUtils.send(url, mapx, HttpUtils.HttpMethod.POST_X, headers);
            System.out.println("body:" + httpResponse.body());
            System.out.println("gameid:" + gameid + "--userid" + userid);

            if (httpResponse.body().startsWith("{")) {
                JSONObject resule = JSONObject.fromObject(httpResponse.body());
                int retcode = (int) resule.get("retcode");

                if (retcode > 0) {
                    map2.put("userid", userid);
                    map2.put("gameid", gameid);
                    map2.put("commentcontent", commentcontent);
                    map2.put("createtime", createtime);
                    map2.put("supportcount", supportcount);
                    map2.put("score", score);
                    l1.add(map2);
                }

            } else {
                System.out.println("gameid + userno:" + gameid + userid);
            }


            try {
                Thread.currentThread().sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //==================================================

            map1.put("userid", userid);
            map1.put("gameid", gameid);
            map1.put("commentcontent", commentcontent);
            map1.put("createtime", createtime);
            map1.put("supportcount", supportcount);
            map1.put("score", score);
            l.add(map1);


        });

        kv.set("userid", "用户id");
        kv.set("gameid", "游戏id");
        kv.set("commentcontent", "评论内容");
        kv.set("createtime", "创建时间");
        kv.set("supportcount", "点赞数");
        kv.set("score", "评分");
        try {
            Workbook workbook = ExcelKit.exportExcel(l, kv);
            Workbook workbook1 = ExcelKit.exportExcel(l1, kv);
            workbook.write(new FileOutputStream(new File("target/编辑评论录入数据0814-1ce.xls"))); // 将工作簿对象写到磁盘文件
            workbook1.write(new FileOutputStream(new File("target/编辑评论录入数据0814-1cuowu.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取评论数据，删除所属评论；
    @Test
    public void deletecomment() throws IOException, InterruptedException {
        StringBuffer buff = new StringBuffer();
        StringBuffer buff1 = new StringBuffer();
        StringBuffer buff2 = new StringBuffer();
        StringBuffer buff3 = new StringBuffer();
//        String[] FIELDS = {"userid", "gameid", "", "createtime", "supportcount"};
        String[] FIELDS = {"userid", "gameid", "", "createtime"};

        List<Map> list = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\编辑评论录入数据1208-ce.xls"), FIELDS);
        list.remove(0);//去除多余的行首

        list.forEach(x -> {
            if (x.get("gameid").toString() == "" || x.get("createtime").toString() == "" || x.get("userid").toString() == "") {
                return;
            }

            buff.append("DELETE FROM `v09x_platf_core`.`gamecomment` WHERE gameid = '");
            buff1.append("DELETE FROM `v09x_platf_core`.`gamescore` WHERE gameid = '");
            buff2.append("UPDATE  `v09x_platf_core`.`gamecomment` SET `createtime` = ");
            buff3.append("UPDATE  `v09x_platf_core`.`gamescore` SET `createtime` =  ");
            int userid = (int) Float.parseFloat(x.get("userid").toString());
            String gameid = x.get("gameid").toString();
            long createtime = (long) Float.parseFloat(x.get("createtime").toString());
//            int supportcount = (int) Float.parseFloat(x.get("supportcount").toString());
            buff.append(gameid + "' AND userid = ");
            buff1.append(gameid + "' AND userid = ");
//            buff2.append(createtime + " , supportcount = ");
//            buff2.append(supportcount + " WHERE gameid = '");
            buff2.append(createtime + " WHERE gameid = '");
            buff2.append(gameid + "' AND userid = ");
            buff3.append(createtime + " WHERE gameid = ");
            buff3.append(gameid + " AND userid = ");
            buff.append(userid + " ;\n");
            buff1.append(userid + " ;\n");
            buff2.append(userid + " ;\n");
            buff3.append(userid + " ;\n");
        });
      /*  buff.delete(buff.length() - 2, buff.length() + 1);
        buff.append(";");*/
        // 入库数据
        /*FileKit.strToFile(buff.toString(), new File("tmp/删除0826评论录入数据.sql"));
        FileKit.strToFile(buff1.toString(), new File("tmp/删除0826评分录入数据.sql"));*/
        FileKit.strToFile(buff2.toString(), new File("tmp/修改1208评论录入时间数据.sql"));
        FileKit.strToFile(buff3.toString(), new File("tmp/修改1208评分录入时间数据.sql"));

    }

    //编辑用户insert语句；
    @Test
    public void insertUserinfo() {
        StringBuffer buff = new StringBuffer();
        StringBuffer buff2 = new StringBuffer();
        StringBuffer buff3 = new StringBuffer();
        StringBuffer buff4 = new StringBuffer();
        StringBuffer buff5 = new StringBuffer();
        String[] FIELDS = {"username", "mobile", "regtime"};
        int userid = 5090;
        int userno = 9300;
        int face = 1;
        int count = 1;

        List<Map> list = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\编辑数据\\名字.xlsx"), FIELDS);
        buff.append("INSERT INTO `v09x_platf_core`.`userdetail` (`userid`,userno,`username`,mobile,gender,usertype,regtime,face) VALUES  \n");
       /* list.forEach(x -> {
            String username = x.get("username").toString().trim();
            String mobile = x.get("mobile").toString().trim();
            String regtime = x.get("regtime").toString().trim();
            int gender = 2;
            if (mobile.endsWith("1") || mobile.endsWith("3") || mobile.endsWith("5") || mobile.endsWith("7") || mobile.endsWith("9")) {
                gender = 4;
            }

            buff.append(String.format("(%s,%s,'%s','%s',%s,%s,%s),\n", userid, userno, username, mobile, gender, 21,regtime));
        });*/
        for (Map x : list) {
            String username = x.get("username").toString().trim();
            String mobile = x.get("mobile").toString().trim();
            String regtime = x.get("regtime").toString().trim();
            int gender = 2;
            if (mobile.endsWith("1") || mobile.endsWith("3") || mobile.endsWith("5") || mobile.endsWith("7") || mobile.endsWith("9")) {
                gender = 4;
            }
            buff.append(String.format("(%s,%s,'%s','%s',%s,%s,%s,'%s'),\n", userid, userno, username, mobile, gender, 21, regtime, "http://aimg.woaihaoyouxi.com/app/zimg/face0628/timg" + face + ".jpg"));
            if (count <= 100) {
                buff2.append(userid + ",");
            }
            if (100 < count && count <= 200) {
                buff3.append(userid + ",");
            }
            if (200 < count && count <= 300) {
                buff4.append(userid + ",");
            }
            if (300 < count && count <= 400) {
                buff5.append(userid + ",");
            }
            userid++;
            userno++;
            face++;
            count++;
        }

        buff.delete(buff.length() - 2, buff.length() + 1);
        buff.append(";");
        System.out.println(buff2);
        System.out.println(buff3);
        System.out.println(buff4);
        System.out.println(buff5);
        // 入库
        FileKit.strToFile(buff.toString(), new File("tmp/编辑批量入库数据.sql"));

    }

    //处理每日点赞数据
    @Test
    public void insertsupport() throws FileNotFoundException {
        StringBuffer buff = new StringBuffer();
        String[] FIELDS = {"articleid", "supportcount", "readcount"};

        List<Map> list = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\点赞数增加0716.xlsx"), FIELDS);
        list.remove(0);//去除多余的行首

        list.forEach(x -> {
            buff.append("UPDATE `v09x_platf_core`.`articleinfo` SET `supportcount` = supportcount + ");
            String articleid = x.get("articleid").toString();
            int supportcount = (int) Float.parseFloat(x.get("supportcount").toString());
            int readcount = (int) Float.parseFloat(x.get("readcount").toString());
            buff.append(supportcount + " , `readcount` = readcount + ");
            buff.append(readcount + " WHERE `articleid` = '");
//            buff.append(supportcount + " WHERE `articleid` = '");
            buff.append(articleid + "' ;" + "\n");
//            buff.append(String.format("('%s',%s,'%s','%s','%s',%s),\n", map.get(userid) + "-" + Utility.format36time(createtime), map.get(userid), gameid, gameid + "-0", commentcontent, createtime));
        });

        buff.delete(buff.length() - 2, buff.length() + 1);
        buff.append(";");
        // 入库数据
        FileKit.strToFile(buff.toString(), new File("tmp/点赞数据0716.sql"));

    }

    @Getter
    @Setter
    public static class RetResult<T> {
        protected int retcode;
        protected Map<String, String> attach;
        protected T result;
    }

    @Test
    public void sss() {

        List<Object> list1 = new ArrayList<>();
        list1.add(1);
        list1.add(2);
        list1.add(3);
        list1.add(4);
        System.out.println(list1.subList(0, 1).size());
        System.out.println(list1.subList(0, 2).size());
        System.out.println(list1.subList(0, 3).size());
        System.out.println(list1.subList(0, 4).size());
        System.out.println(list1.subList(0, 5).size());

    }

    //读取文件夹信息
    @Test
    public void readdir() {
        List<String> list = new ArrayList<>();
        File iconList = new File("C:\\Users\\wh\\Desktop\\正方形封面图\\gamecover2");
        File[] files = iconList.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                list.add(files[i].getName());
            }
        }
        StringBuffer buff = new StringBuffer();
        ArrayList<String> list1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            buff.append("UPDATE `v09x_platf_core`.`gameinfo` SET `gamecover2` = 'https://aimg.woaihaoyouxi.com/app/game/gamecover2/");
            String[] split = new String[0];
            if (list.get(i).endsWith("jpg")) {
                split = list.get(i).split(".jpg");
            } else if (list.get(i).endsWith("JPG")) {
                split = list.get(i).split(".JPG");
            }
            buff.append(list.get(i));
            buff.append("' WHERE `gameid` = '" + split[0] + "'; \n");
            list1.add(split[0]);
        }
        System.out.println(list1);
        // 入库数据
        FileKit.strToFile(buff.toString(), new File("tmp/游戏封面处理0708.sql"));
    }

    @Test
    public void gameplatfinfo() {
        //填写了批准文号或出版物号任一一个的统计表
        String sql1 = "SELECT gameid , gamename ,approvalno,publicationno FROM gameinfo  WHERE approvalno !='' OR publicationno !='' AND gameid IN (SELECT gameid FROM gameplatformdetail WHERE platid = 501 OR platid = 502 " +
                " GROUP BY gameid" +
                " );";

        //没有填写了批准文号或出版物号任一一个的统计表
        String sql2 = "SELECT gameid , gamename ,approvalno,publicationno FROM gameinfo  WHERE approvalno ='' AND publicationno ='' AND gameid IN (SELECT gameid FROM gameplatformdetail WHERE platid = 501 OR platid = 502 " +
                " GROUP BY gameid" +
                " );";

        //选择了安卓载体的游戏id
        String sql3 = "SELECT gameid FROM gameplatformdetail WHERE platid = 502 ";

        //选择了ios载体的游戏id
        String sql4 = "SELECT gameid FROM gameplatformdetail WHERE platid = 501 ";

        List<Map> list1 = findList(sql1);
        List<Map> list2 = findList(sql2);
        List<Map> list3 = findList(sql3);
        List<Map> list4 = findList(sql4);
        List<String> androids = list3.stream().map(x -> x.get("gameid").toString()).collect(Collectors.toList());
        List<String> iosids = list4.stream().map(x -> x.get("gameid").toString()).collect(Collectors.toList());

        list1.forEach(x -> {
            String gameid = x.get("gameid").toString();
            if (androids.contains(gameid)) {
                x.put("android", "1");
            } else {
                x.put("android", "0");
            }
            if (iosids.contains(gameid)) {
                x.put("ios", "1");
            } else {
                x.put("ios", "0");
            }
        });
        list2.forEach(x -> {
            String gameid = x.get("gameid").toString();
            if (androids.contains(gameid)) {
                x.put("android", "1");
            } else {
                x.put("android", "0");
            }
            if (iosids.contains(gameid)) {
                x.put("ios", "1");
            } else {
                x.put("ios", "0");
            }
        });
        Kv kv = Kv.of();
        kv.set("gameid", "游戏ID");
        kv.set("gamename", "游戏昵称");
        kv.set("approvalno", "批准文号");
        kv.set("publicationno", "出版物号");
        kv.set("android", "是否选择安卓载体");
        kv.set("ios", "是否选择ios载体");

        try {
            Workbook workbook = ExcelKit.exportExcel(list1, kv);
            Workbook workbook1 = ExcelKit.exportExcel(list2, kv);
            workbook.write(new FileOutputStream(new File("target/填写了批准文号或出版物号任一一个的统计表0827.xls"))); // 将工作簿对象写到磁盘文件
            workbook1.write(new FileOutputStream(new File("target/没有填写了批准文号或出版物号任一一个的统计表0827.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void gamecomments() throws IOException, InterruptedException {
        StringBuffer buff = new StringBuffer();
        String[] FIELDS = {"userid", "commentcontent", "score1", "score2", "score3", "score4", "score5", "score6"};

        List<Map> list = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\赛博朋克2077 2.xls"), FIELDS);
        list.remove(0);

        List<Map<String, Object>> l = new ArrayList<>();
        List<Map<String, Object>> l1 = new ArrayList<>();
        Kv kv = Kv.of();
        //======================================================
        StringBuffer buffer1 = new StringBuffer();
        buffer1.append("{mobile:15697177873,vercode:159753}");
        String login = "https://api.woaihaoyouxi.com/account/login";
//        buffer1.append("{mobile:15697177873,vercode:20422}");
//        String login = "https://api.1216.top/account/login";
        HashMap<String, Object> loginmap = new HashMap<>();
        loginmap.put("bean", buffer1.toString());
        HttpResponse<String> httpResponse1 = HttpUtils.send(login, loginmap, HttpUtils.HttpMethod.GET);
        JSONObject jsonResule = JSONObject.fromObject(httpResponse1.body());
        System.out.println(httpResponse1.body());
        System.out.println(httpResponse1.request().uri());
        JSONObject attachJSON = jsonResule.getJSONObject("attach");
        String jsessionid = attachJSON.getString("sessionid");

        list.forEach(x -> {
            if (x.get("commentcontent").toString() == "") {
                return;
            }
            HashMap<String, Object> map1 = new HashMap<>();
            HashMap<String, Object> map2 = new HashMap<>();
            //评论的时间区间
//            long createtime = 1605056400000l + (long) (Math.random() * (1606572000000l - 1605056400000l));
            long createtime = getRandomTime();

            int userid = (int) Float.parseFloat(x.get("userid").toString());
            String gameid = "15841";
            String commentcontent = x.get("commentcontent").toString();
            float score1 = Float.parseFloat(x.get("score1").toString());
            float score2 = Float.parseFloat(x.get("score2").toString());
            float score3 = Float.parseFloat(x.get("score3").toString());
            float score4 = Float.parseFloat(x.get("score4").toString());
            float score5 = Float.parseFloat(x.get("score5").toString());
            float score6 = Float.parseFloat(x.get("score6").toString());


            //=============================================

            StringBuffer buffer = new StringBuffer();
            buffer.append("{gameid:'" + gameid + "',commentcontent:'");
            buffer.append("commentcontent");
            buffer.append("',experience:" + score1 + " ,frame:" + score2 + " ,music:" + score3 + ",operation:" + score4 + " ,fluent:" + score5 + " ,original:" + score6 + "}");

            //==================================
            String url = "https://api.woaihaoyouxi.com/game/comment_create_tmp";
//            String url = "https://api.1216.top/game/comment_create_tmp";
            HashMap<String, Object> mapx = new HashMap<>();
            Map<String, Object> mmm = new HashMap<>();
            mmm.put("gameid", gameid);
            mmm.put("commentcontent", commentcontent);
            mmm.put("experience", score1);
            mmm.put("frame", score2);
            mmm.put("music", score3);
            mmm.put("fluent", score5);
            mmm.put("original", score6);
            mmm.put("operation", score4);
            mapx.put("bean", mmm);
            mapx.put("targetid", userid);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("jsessionid", jsessionid);

            HttpResponse<String> httpResponse = HttpUtils.send(url, mapx, HttpUtils.HttpMethod.POST_X, headers);
            System.out.println("body:" + httpResponse.body());
            System.out.println("gameid:" + gameid + "--userid:" + userid);

            if (httpResponse.body().startsWith("{")) {
                JSONObject resule = JSONObject.fromObject(httpResponse.body());
                int retcode = (int) resule.get("retcode");

                if (retcode > 0) {
                    map2.put("userid", userid);
                    map2.put("gameid", gameid);
                    map2.put("commentcontent", commentcontent);
                    map2.put("createtime", createtime);
                    map2.put("score1", score1);
                    map2.put("score2", score2);
                    map2.put("score3", score3);
                    map2.put("score4", score4);
                    map2.put("score5", score5);
                    map2.put("score6", score6);
                    l1.add(map2);
                }

            } else {
                System.out.println("gameid + userid:" + gameid + userid);
            }


            try {
                Thread.currentThread().sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //==================================================

            map1.put("userid", userid);
            map1.put("gameid", gameid);
            map1.put("commentcontent", commentcontent);
            map1.put("createtime", createtime);
            map1.put("score1", score1);
            map1.put("score2", score2);
            map1.put("score3", score3);
            map1.put("score4", score4);
            map1.put("score5", score5);
            map1.put("score6", score6);

            l.add(map1);


        });

        kv.set("userid", "用户id");
        kv.set("gameid", "游戏id");
        kv.set("commentcontent", "评论内容");
        kv.set("createtime", "创建时间");
        kv.set("supportcount", "点赞数");
        kv.set("score1", "体验感");
        kv.set("score2", "画面感");
        kv.set("score3", "音乐感");
        kv.set("score4", "操作性");
        kv.set("score5", "流畅性");
        kv.set("score6", "原创性");
        try {
            Workbook workbook = ExcelKit.exportExcel(l, kv);
            Workbook workbook1 = ExcelKit.exportExcel(l1, kv);
            workbook.write(new FileOutputStream(new File("target/编辑评论录入数据1208-ce.xls"))); // 将工作簿对象写到磁盘文件
            workbook1.write(new FileOutputStream(new File("target/编辑评论录入数据1208-cuowu.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getRandomTime() {
        long createtime = 0;
        for (int i = 0; i < 50; i++) {
            createtime = 1607216400000l + (long) (Math.random() * (1607389200000l - 1607216400000l));
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(createtime);
            LocalDate startDay = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            System.out.println(startDay);
            LocalDateTime localDateTime1 = LocalDateTime.of(startDay, LocalTime.of(8, 01, 00));
            LocalDateTime localDateTime2 = LocalDateTime.of(startDay, LocalTime.of(21, 01, 00));
            ZoneId zoneId = ZoneId.systemDefault();
            long time1 = localDateTime1.atZone(zoneId).toInstant().toEpochMilli();
            long time2 = localDateTime2.atZone(zoneId).toInstant().toEpochMilli();
            if (createtime >= time1 && createtime <= time2) {
                break;
            }
        }
        return createtime;
    }

    //游戏达人配置信息；
    @Test
    public void setGameV() throws IOException, InterruptedException {
        StringBuffer buff = new StringBuffer();
        String[] FIELDS = {"name", "company", "position", "mobile", "idnum"};

        List<Map> list = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\游戏达人表格1031.xlsx"), FIELDS);
        list.remove(0);//去除多余的行首


        list.forEach(x -> {
            String mobile = x.get("mobile").toString().trim();
            if (Utils.isEmpty(mobile)) {
                return;
            }

            String sql = "select mobile from platf_oth.cjrecord where mobile = " + mobile + ";";
//            System.out.println(sql);
            List<Map> tempList = findList(sql);
            System.out.println(tempList.size());
            if (tempList == null || tempList.size() == 0) {
                insertCj(x);
            }

            String sql1 = "select userid from userdetail where mobile = " + mobile + ";";
//            System.out.println(sql1);
            List<Map> tempList2 = findList(sql1);
            System.out.println(tempList2.size());
            if (tempList2 == null || tempList2.size() == 0) {
                accountlogin(x);
            }

        });

    }

    public void accountlogin(Map x) {
        if (x.get("mobile").toString() == "") {
            return;
        }
        String mobile = x.get("mobile").toString().trim();
        StringBuffer buffer = new StringBuffer();
        buffer.append("{mobile:'" + mobile + "',vercode:159753}");
        String login = "https://api.woaihaoyouxi.com/account/signup";
//        String login = "https://api.1216.top/account/signup";
        HashMap<String, Object> loginmap = new HashMap<>();
        loginmap.put("bean", buffer.toString());
        HttpResponse<String> httpResponsexx = HttpUtils.send(login, loginmap, HttpUtils.HttpMethod.GET);
        String body = httpResponsexx.body();
        if (body.contains("retinfo")) {
            System.out.println("body:" + body);
            System.out.println("mobile:" + mobile);
        }

        if (httpResponsexx.body().startsWith("{")) {
            JSONObject resule = JSONObject.fromObject(httpResponsexx.body());
        } else {
            System.out.println("mobile:" + mobile);
        }

        try {
            Thread.currentThread().sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insertCj(Map x) {
        if (x.get("name").toString() == "" || x.get("mobile").toString() == "" || x.get("company").toString() == "" || x.get("position").toString() == "" || x.get("idnum").toString() == "") {
            return;
        }

        String name = x.get("name").toString();
        String company = x.get("company").toString();
        String position = x.get("position").toString();
        String mobile = x.get("mobile").toString();
        String idnum = x.get("idnum").toString();
        //=============================================

//        StringBuffer buffer = new StringBuffer();
//        buffer.append("{name:'" + name + "',company:'" + company + "',position:'" + position + "',mobile:'" + mobile + "',idnum:'" + idnum);
        //==================================
        String url = "https://api.woaihaoyouxi.com/activity/cj_create";
//        String url = "https://api.1216.top/activity/cj_create";
        HashMap<String, Object> mapx = new HashMap<>();
        Map<String, Object> mmm = new HashMap<>();
        mmm.put("name", name);
        mmm.put("company", company);
        mmm.put("position", position);
        mmm.put("mobile", mobile);
        mmm.put("idnum", idnum);
        mapx.put("bean", mmm);

//        HashMap<String, String> headers = new HashMap<>();
//        headers.put("jsessionid", jsessionid);

        HttpResponse<String> httpResponse = HttpUtils.send(url, mapx, HttpUtils.HttpMethod.POST_X);
        String body = httpResponse.body();
        if (body.contains("retinfo")) {
            System.out.println("body:" + httpResponse.body());
            System.out.println("name:" + name + "--mobile" + mobile);

        }

        if (httpResponse.body().startsWith("{")) {
            JSONObject resule = JSONObject.fromObject(httpResponse.body());
        } else {
            System.out.println("name + mobile:" + name + mobile);
        }

        try {
            Thread.currentThread().sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void checkGameV() throws IOException, InterruptedException {
        StringBuffer buff = new StringBuffer();
        String[] FIELDS = {"name", "company", "position", "mobile", "idnum"};

        List<Map> list = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\游戏达人2(1).xlsx"), FIELDS);
        list.remove(0);//去除多余的行首


        HashSet<String> mobileset = new HashSet<>();
        ArrayList<String> list1 = new ArrayList<>();
        list.forEach(x -> {
            String mobile = x.get("mobile").toString().trim();
            if (Utils.isEmpty(mobile)) {
                return;
            }
            mobileset.add(mobile);
            list1.add(mobile);
        });
        System.out.println("mobileset:" + mobileset.size());

        String sql = "select mobile from platf_oth.cjrecord where mobile IN (" + mobileset.toString().replace("[", "").replace("]", "").trim() + ");";
        System.out.println(sql);
        List<Map> tempList = findList(sql);
        System.out.println("tempList:" + tempList.size());

        String sql1 = "select userid from userdetail where mobile IN (" + list1.toString().replace("[", "").replace("]", "").trim() + ");";
        System.out.println(sql1);
        List<Map> tempList2 = findList(sql1);
        System.out.println("tempList2:" + tempList2.size());

        HashSet<String> userset = new HashSet<>();
        tempList2.forEach(x -> userset.add(x.get("userid").toString()));
        System.out.println("userset:" + userset.size());

        String sql2 = "select * from platf_quest.useridentity where userid IN (" + userset.toString().replace("[", "").replace("]", "").trim() + ");";
        List<Map> tempList3 = findList(sql2);
        System.out.println("tempList3:" + tempList3.size());

        HashSet<String> userset1 = new HashSet<>();
        tempList3.forEach(x -> userset1.add(x.get("userid").toString()));

        userset.forEach(x -> {
            if (!userset1.contains(x)) {
                System.out.println(x);
            }
        });

    }

    //手机品牌型号导入
    @Test
    public void insertModel() {
        StringBuffer buff = new StringBuffer();
        String[] FIELDS = {"manufacturer", "model", "mobilename"};
        List<Map> list = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\手机品牌机型信息.xlsx"), FIELDS);
        list.remove(0);
        buff.append("INSERT INTO `v09x_platf_core`.`mobilemodel` (`modelid`,manufacturer,`model`,mobilename,status) VALUES  \n");
        int modelid = 10000;
        for (Map x : list) {
            String manufacturer = x.get("manufacturer").toString().trim();
            String model = x.get("model").toString().trim();
            String mobilename = x.get("mobilename").toString().trim();
            int status = 1;
            buff.append(String.format("('%s','%s','%s','%s',%s),\n", modelid, manufacturer, model, mobilename, status));
            modelid++;
        }

        buff.delete(buff.length() - 2, buff.length() + 1);
        buff.append(";");
        // 入库
        FileKit.strToFile(buff.toString(), new File("tmp/2020929手机型号入库数据.sql"));

    }

    //9-10月用户活跃明细
    @Test
    public void activeusers() {
        StringBuffer buff = new StringBuffer();
        String[] FIELDS = {"", "realname", "department", "userno"};

        List<Map> listxxx = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\员工统计表.xlsx"), FIELDS);
        listxxx.remove(0);//去除多余的行首
        listxxx.remove(0);//去除多余的行首

        Map<String, Map> usermap = Utils.toMap(listxxx, x -> x.get("userno").toString(), v -> v);
        List<Long> usernos = listxxx.stream().map(x -> Long.parseLong(x.get("userno").toString().equals("") ? "0" : x.get("userno").toString())).collect(Collectors.toList());
        String usernoo = Utils.arrToStr(usernos);
        String s = usernoo.substring(1, usernoo.length() - 1);

        String buffer = "(11037 ,11041 ,11042 ,11047 ,11050 ,11051 ,11053 ,11173 ,11189 ,11192 ,11195 ,11196 ,11201 ,11202 ,11668 ,11873 ,12148 ,14794 ,15226 ,16047 ,16904 ,17070 ,18152 ,18252 ,18369 ,18710 ,19110 ,19282 ,19352 ,20327 ,20395 ,21935 ,22153 ,22465)";
//        String buffer = "(11037, 11041, 11042, 11047, 11050, 11051, 11053, 11173, 11189, 11192, 11196, 11197, 11202, 11668, 11873, 12148, 14794, 15226, 16047, 16904, 17070, 18152, 18155, 18252, 18369, 18710, 19110, 19282, 19352, 20395, 11195)";
        String sql1 = "SELECT u.userid,u.userno,u.username,u.explevel,u.mobile,u.age,u.birthday,u.gender,u.constellation,COUNT(ua.userid) count FROM v09x_platf_core.userdetail u " +
                " LEFT JOIN platf_quest.useractiverecord ua ON u.userid = ua.userid " +
                " WHERE ua.intday >= 20201101 AND ua.intday <= 20201130  AND u.userno IN (" + s + ")" +
                " GROUP BY ua.userid ORDER BY ua.userid";
        List<Map> list = findList(sql1);

        list.sort(Comparator.comparing(x -> usernos.indexOf(x.get("userno"))));
        List<Integer> userids = list.stream().map(x -> Integer.parseInt(x.get("userid").toString())).collect(Collectors.toList());
        String useridss = Utils.arrToStr(userids);
        String s1 = useridss.substring(1, useridss.length() - 1);
        //微动态
        String sql2 = "SELECT a.userid,count(userid) count FROM articleinfo a" +
                " WHERE  a.articletype = 1 AND a.status IN (10,20,21) AND reprintid = '' AND createtime >= 1604160000000 AND createtime <= 1606751999999 AND " +
                " a.userid IN (" + s1 + ")" +
                "  GROUP BY a.userid ";
        //游戏说
        String sql3 = "SELECT a.userid,count(userid) count FROM articleinfo a" +
                " WHERE a.articletype = 2 AND a.status IN (10,20,21) AND createtime >= 1604160000000 AND createtime <= 1606751999999  AND " +
                " a.userid IN  (" + s1 + ")" +
                " GROUP BY a.userid ";
        //标记评论游戏数
        String sql4 = "SELECT userid,count(userid) count FROM gamemark WHERE  createtime >= 1604160000000 AND createtime <= 1606751999999 " +
                " AND userid IN  (" + s1 + ")" +
                "  GROUP BY userid";
        String sql4x = "SELECT userid,count(userid) count FROM gamecomment WHERE status = 10 AND createtime >= 1604160000000 AND createtime <= 1606751999999 " +
                " AND userid IN  (" + s1 + ")" +
                " GROUP BY userid";
        //点赞数
        String sql5 = "SELECT userid,count(userid) count FROM articlesupport WHERE  createtime >= 1604160000000 AND createtime <= 1606751999999 " +
                " AND userid IN  (" + s1 + ")" +
                " GROUP BY userid";
        String sql5x = "SELECT userid,count(userid) count FROM articlecommentsupport WHERE  createtime >= 1604160000000 AND createtime <= 1606751999999 " +
                " AND userid IN   (" + s1 + ")" +
                " GROUP BY userid";
        String sql5y = "SELECT userid,count(userid) count FROM gamecommentsupport WHERE  createtime >= 1604160000000 AND createtime <= 1606751999999 " +
                " AND userid IN  (" + s1 + ")" +
                " GROUP BY userid";
        //转发数
        String sql6 = "SELECT a.userid,count(0) count FROM articleinfo a" +
                " WHERE reprintid <> '' AND a.status <> 80 AND createtime >= 1604160000000 AND createtime <= 1606751999999 AND " +
                " a.userid IN   (" + s1 + ")" +
                " GROUP BY a.userid ";
        //充值或购买记录
        String sql7 = "SELECT userid,count(userid) count FROM platf_pay.payrecord WHERE status = 10 AND createtime >= 1604160000000 AND createtime <= 1606751999999 " +
                " AND userid IN  (" + s1 + ")" +
                " GROUP BY userid";


        List<Map> dyns = findList(sql2);
        List<Map> plays = findList(sql3);
        List<Map> comments = findList(sql4);
        List<Map> gamecomments = findList(sql4x);
        List<Map> supports = findList(sql5);
        List<Map> commentsupports = findList(sql5x);
        List<Map> gamecommentsupports = findList(sql5y);
        List<Map> reprints = findList(sql6);
        List<Map> pays = findList(sql7);

        Kv<Integer, Long> dynkv = Kv.of();
        Kv<Integer, Long> playskv = Kv.of();
        Kv<Integer, Long> commentskv = Kv.of();
        Kv<Integer, Long> gamecommentskv = Kv.of();
        Kv<Integer, Long> supportskv = Kv.of();
        Kv<Integer, Long> commentsupportskv = Kv.of();
        Kv<Integer, Long> gamecommentsupportskv = Kv.of();
        Kv<Integer, Long> reprintskv = Kv.of();
        Kv<Integer, Long> payskv = Kv.of();
        dyns.forEach(x -> dynkv.set((Integer) x.get("userid"), (Long) x.get("count")));
        plays.forEach(x -> playskv.set((Integer) x.get("userid"), (Long) x.get("count")));
        comments.forEach(x -> commentskv.set((Integer) x.get("userid"), (Long) x.get("count")));
        gamecomments.forEach(x -> gamecommentskv.set((Integer) x.get("userid"), (Long) x.get("count")));
        supports.forEach(x -> supportskv.set((Integer) x.get("userid"), (Long) x.get("count")));
        commentsupports.forEach(x -> commentsupportskv.set((Integer) x.get("userid"), (Long) x.get("count")));
        gamecommentsupports.forEach(x -> gamecommentsupportskv.set((Integer) x.get("userid"), (Long) x.get("count")));
        reprints.forEach(x -> reprintskv.set((Integer) x.get("userid"), (Long) x.get("count")));
        pays.forEach(x -> payskv.set((Integer) x.get("userid"), (Long) x.get("count")));


        ArrayList<Object> list1 = new ArrayList<>();
        list.forEach(x -> {
            Integer userid = (Integer) x.get("userid");
            Kv kv1 = Kv.of();
            Integer gender = (Integer) x.get("gender");
            String g = "未知";
            if (gender == 2) {
                g = "男";
            } else if (gender == 4) {
                g = "女";
            }


            kv1.set("userid", userid)
                    .set("username", x.get("username"))
                    .set("userno", x.get("userno"))
                    .set("count", x.get("count"))
                    .set("explevel", x.get("explevel"))
                    .set("mobile", x.get("mobile"))
                    .set("age", x.get("age"))
                    .set("birthday", x.get("birthday"))
                    .set("gender", g)
                    .set("constellation", x.get("constellation"))
                    .set("day", x.get("day"))
                    .set("dyns", dynkv.getOrDefault(userid, 0l))
                    .set("plays", playskv.getOrDefault(userid, 0l))
                    .set("comments", commentskv.getOrDefault(userid, 0l) + gamecommentskv.getOrDefault(userid, 0l))
                    .set("supports", supportskv.getOrDefault(userid, 0l) + commentsupportskv.getOrDefault(userid, 0l) + gamecommentsupportskv.getOrDefault(userid, 0l))
                    .set("reprints", reprintskv.getOrDefault(userid, 0l))
                    .set("pays", payskv.getOrDefault(userid, 0l))
            ;
            list1.add(kv1);
        });
        Kv kv = Kv.of();
        kv.set("userid", "用户ID")
                .set("userno", "彩虹号")
                .set("username", "用户昵称")
                .set("explevel", "用户等级")
                .set("count", "活跃天数")
                .set("supports", "点赞数")
                .set("dyns", "发布微动态数")
                .set("plays", "发布游戏说数")
                .set("comments", "标记评论游戏数")
                .set("reprints", "转发数")
                .set("mobile", "手机号")
                .set("age", "年龄")
                .set("birthday", "生日")
                .set("gender", "性别")
                .set("constellation", "星座")
                .set("pays", "充值或购买次数")
        ;

        try {
            Workbook workbook = ExcelKit.exportExcel(list1, kv);
            workbook.write(new FileOutputStream(new File("target/11月内部用户活跃数据.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //各个社区用户活跃排行
    @Test
    public void active() {


        String sql = "SELECT u.userno ,u.username,u.explevel ,c.activecount,c.communityid,cf.communityname FROM communitybook c" +
                " LEFT JOIN userdetail u ON u.userid = c.userid" +
                " LEFT JOIN communityinfo cf ON c.communityid = cf.communityid" +
                " ORDER BY c.activecount DESC ";

        List<Map> list = findList(sql);
        Map<String, List<Map>> map = list.stream().collect(Collectors.groupingBy(x -> x.get("communityname").toString()));
        List<Map<String, Object>> l = new ArrayList<>();
        map.keySet().forEach(x -> {
            List<Map> list1 = map.get(x);
            Kv sheet1 = Kv.of();
            List<Kv> kvs = new ArrayList<>();
            for (int i = 0; i < list1.size(); i++) {
                kvs.add(Kv.of("userno", list1.get(i).get("userno"))
                        .set("username", list1.get(i).get("username"))
                        .set("explevel", list1.get(i).get("explevel"))
                        .set("activecount", list1.get(i).get("activecount")));
            }

            sheet1.set("data", kvs);
            sheet1.set("sheetName", x);
            sheet1.set("hdNames", new String[]{"彩虹号", "用户昵称", "用户等级", "用户活跃值"});
            sheet1.set("hds", new String[]{"userno", "username", "explevel", "activecount"});
            l.add(sheet1);
        });


        Workbook wb = ExcelKit.exportExcels(l);
        try {
            wb.write(new FileOutputStream(new File("target/各个社区用户活跃排行数据.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //10月份全月的用户发贴率和用户分享率
    @Test
    public void articleStat() {
        String starttime = "1604160000000";
        String endtime = "1605340653810";

        Supplier<List<String>> daysSupplier = () -> {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(starttime));

            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(Long.parseLong(endtime));

            LocalDate startDay = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            LocalDate endDay = LocalDate.of(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH) + 1, cal2.get(Calendar.DAY_OF_MONTH));

            List<String> days = new ArrayList<>();
            while (true) {
                int _year = startDay.getYear();
                int _month = startDay.getMonthValue();
                int _day = startDay.getDayOfMonth();

                days.add(String.format("%s-%s-%s", _year,
                        _month < 10 ? "0" + _month : "" + _month,
                        _day < 10 ? "0" + _day : "" + _day));
                startDay = startDay.plusDays(1);
                if (startDay.isAfter(endDay)) {
                    break;
                }
            }
            return days;
        };
        List<String> days = daysSupplier.get();

        String where = " WHERE a.status IN (10,20) AND content NOT LIKE '大家好，我是%' AND (a.memberid='' or a.memberid=1001052) AND u.usertype = 2 ";
//        String where = " WHERE status IN (10,20) AND content NOT LIKE '大家好，我是%' ";
        if (!Utils.isEmpty(starttime)) {
            where += " AND a.createtime >= " + starttime;
        }
        if (!Utils.isEmpty(endtime)) {
            where += " AND a.createtime < " + endtime;
        }

        // 总发帖人数
        String select1 = "SELECT COUNT(DISTINCT a.`userid`) 'count',FROM_UNIXTIME(a.`createtime`/1000, '%Y-%m-%d') `day` FROM articleinfo a LEFT JOIN userdetail u ON a.userid = u.userid ";
        List<Map> list = findList(select1 + (where + " GROUP BY `day`"));
        List<Map> list1 = findList(select1 + (where + " AND a.manufacturer = 'iPhone' GROUP BY `day`"));
        System.out.println(select1 + (where + " AND a.manufacturer = 'iPhone' GROUP BY `day`"));
        List<Map> list2 = findList(select1 + (where + " AND a.manufacturer NOT IN ('iPhone','') GROUP BY `day`"));
        System.out.println(select1 + (where + " AND a.manufacturer NOT IN ('iPhone','') GROUP BY `day`"));
        Map<String, Object> data = Utils.toMap(list, x -> Kv.toAs(x.get("day"), String.class), x -> x.get("count"));
        Map<String, Object> data1 = Utils.toMap(list1, x -> Kv.toAs(x.get("day"), String.class), x -> x.get("count"));
        Map<String, Object> data2 = Utils.toMap(list2, x -> Kv.toAs(x.get("day"), String.class), x -> x.get("count"));
        // 数据补齐
        Kv<String, Object> _data = Utils.toMap(days, x -> x, x -> data.getOrDefault(x, 0l));
        Kv<String, Object> _data1 = Utils.toMap(days, x -> x, x -> data1.getOrDefault(x, 0l));
        Kv<String, Object> _data2 = Utils.toMap(days, x -> x, x -> data2.getOrDefault(x, 0l));
        long total = 0;
        for (Object value : _data.values()) {
            total += (long) value;
        }
        long total1 = 0;
        for (Object value : _data1.values()) {
            total1 += (long) value;
        }
        long total2 = 0;
        for (Object value : _data2.values()) {
            total2 += (long) value;
        }


        String userwhere = " WHERE u.`status`!=80 AND  u.usertype = 2 ";
        String userwhere1 = " WHERE u.`status`!=80 AND  u.usertype = 2 AND u.appos = 'ios' ";
        String userwhere2 = " WHERE u.`status`!=80 AND  u.usertype = 2 AND u.appos = 'android' ";
           /* if (!Utils.isEmpty(bean.get("starttime"))) {
                userwhere += " AND regtime >= " +starttime;
            }*/
        if (!Utils.isEmpty(endtime)) {
            userwhere += " AND regtime < " + endtime;
        }

        String usersql = "SELECT COUNT(n.userid) 'count',FROM_UNIXTIME(n.`regtime`/1000, '%Y-%m-%d') `day` FROM" +
                " (SELECT u.`userid`,IF(u.`regtime`< " + starttime + ", " + starttime + ", u.`regtime`) 'regtime' FROM userdetail u " + userwhere + ") n" +
                " GROUP BY `day` ORDER BY `day`";
        String usersql1 = "SELECT COUNT(n.userid) 'count',FROM_UNIXTIME(n.`regtime`/1000, '%Y-%m-%d') `day` FROM" +
                " (SELECT u.`userid`,IF(u.`regtime`< " + starttime + ", " + starttime + ", u.`regtime`) 'regtime' FROM userdetail u " + userwhere1 + ") n" +
                " GROUP BY `day` ORDER BY `day`";
        String usersql2 = "SELECT COUNT(n.userid) 'count',FROM_UNIXTIME(n.`regtime`/1000, '%Y-%m-%d') `day` FROM" +
                " (SELECT u.`userid`,IF(u.`regtime`< " + starttime + ", " + starttime + ", u.`regtime`) 'regtime' FROM userdetail u " + userwhere2 + ") n" +
                " GROUP BY `day` ORDER BY `day`";
        List<Map> usercountList = findList(usersql);
        List<Map> usercountList1 = findList(usersql1);
        List<Map> usercountList2 = findList(usersql2);
        Kv kv = Kv.of();
        long hiscount = 0;
        for (Map m : usercountList) {
            String day = Kv.toAs(m.get("day"), String.class);
            hiscount += Kv.toAs(m.get("count"), long.class);
            kv.put(day, hiscount);
        }
        String day = days.get(days.size() - 1);
        long usercount = (long) kv.get(day);
        double totalvalue = new BigDecimal((double) total / usercount).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println(totalvalue);

        Kv kvios = Kv.of();
        long hiscountios = 0;
        for (Map m : usercountList1) {
            String dayios = kvios.toAs(m.get("day"), String.class);
            hiscountios += kvios.toAs(m.get("count"), long.class);
            kvios.put(dayios, hiscountios);
        }
        String dayios = days.get(days.size() - 1);
        long usercountios = (long) kvios.get(dayios);
        double totalvalueios = 0d;
        if (usercountios > 0) {
            totalvalueios = new BigDecimal((double) total1 / usercountios).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

        }
        System.out.println(totalvalueios);

        Kv kvan = Kv.of();
        long hiscountan = 0;
        for (Map m : usercountList2) {
            String dayan = kvan.toAs(m.get("day"), String.class);
            hiscountan += kvan.toAs(m.get("count"), long.class);
            kvan.put(dayan, hiscountan);
        }
        String dayan = days.get(days.size() - 1);
        long usercountan = (long) kvan.get(dayan);
        double totalvaluean = 0d;
        if (usercountan > 0) {
            totalvaluean = new BigDecimal((double) total2 / usercountan).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        System.out.println(totalvaluean);

        data.forEach((k, v) -> {
            long userCount = getUserCount(kv, k);
            if (userCount != 0) {
                double value = new BigDecimal((double) (long) v / getUserCount(kv, k)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                data.put(k, value);
            }
        });
        data1.forEach((k, v) -> {
            long userCount = getUserCount(kvios, k);
            if (userCount != 0) {
                double value = new BigDecimal((double) (long) v / getUserCount(kvios, k)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                data1.put(k, value);
            }
        });
        data2.forEach((k, v) -> {
            long userCount = getUserCount(kvan, k);
            if (userCount != 0) {
                double value = new BigDecimal((double) (long) v / getUserCount(kvan, k)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                data2.put(k, value);
            }
        });
        // 数据补齐
        Kv<String, Object> alldata = Utils.toMap(days, x -> x, x -> data.getOrDefault(x, 0));
        Kv<String, Object> iosdata = Utils.toMap(days, x -> x, x -> data1.getOrDefault(x, 0));
        Kv<String, Object> andata = Utils.toMap(days, x -> x, x -> data2.getOrDefault(x, 0));
        alldata.put("总计", totalvalue);
        iosdata.put("总计", totalvalueios);
        andata.put("总计", totalvaluean);

        ArrayList<Object> l = new ArrayList<>();
        alldata.keySet().forEach(x -> {
            Kv of = Kv.of();
            of.set("day", x);
            of.set("value", alldata.get(x));
            of.set("iosvalue", iosdata.get(x));
            of.set("anvalue", andata.get(x));
            l.add(of);
        });
        Kv kv2 = Kv.of();
        kv2.set("day", "日期")
                .set("value", "总发文率")
                .set("iosvalue", "ios发文率")
                .set("anvalue", "安卓发文率");


        try {
            Workbook workbook = ExcelKit.exportExcel(l, kv2);
            workbook.write(new FileOutputStream(new File("target/发文率.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void articleshare() {
        String starttime = "1601481600000";
        String endtime = "1604159999000";

        Supplier<List<String>> daysSupplier = () -> {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(starttime));

            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(Long.parseLong(endtime));

            LocalDate startDay = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            LocalDate endDay = LocalDate.of(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH) + 1, cal2.get(Calendar.DAY_OF_MONTH));

            List<String> days = new ArrayList<>();
            while (true) {
                int _year = startDay.getYear();
                int _month = startDay.getMonthValue();
                int _day = startDay.getDayOfMonth();

                days.add(String.format("%s-%s-%s", _year,
                        _month < 10 ? "0" + _month : "" + _month,
                        _day < 10 ? "0" + _day : "" + _day));
                startDay = startDay.plusDays(1);
                if (startDay.isAfter(endDay)) {
                    break;
                }
            }
            return days;
        };
        List<String> days = daysSupplier.get();

        String where = " WHERE s.type = 20 AND u.usertype = 2 ";
        if (!Utils.isEmpty(starttime)) {
            where += " AND s.createtime >= " + starttime;
        }
        if (!Utils.isEmpty(endtime)) {
            where += " AND s.createtime < " + endtime;
        }

        // 分享人数
        String sql = "SELECT COUNT(DISTINCT s.`userid`) 'count',FROM_UNIXTIME(s.`createtime`/1000, '%Y-%m-%d') `day` FROM  platf_quest.sharerecord s LEFT JOIN userdetail u ON s.userid=u.userid ";
        List<Map> list = findList(sql + (where + " GROUP BY `day`"));
        Map<String, Object> data = Utils.toMap(list, x -> Kv.toAs(x.get("day"), String.class), x -> x.get("count"));
        // 数据补齐
        Kv<String, Object> _data = Utils.toMap(days, x -> x, x -> data.getOrDefault(x, 0l));
        long total = 0;
        for (Object value : _data.values()) {
            total += (long) value;
        }

        String userwhere = " WHERE u.`status`!=80";
           /* if (!Utils.isEmpty(bean.get("starttime"))) {
                userwhere += " AND regtime >= " +starttime;
            }*/
        if (!Utils.isEmpty(endtime)) {
            userwhere += " AND regtime < " + endtime;
        }

        String usersql = "SELECT COUNT(n.userid) 'count',FROM_UNIXTIME(n.`regtime`/1000, '%Y-%m-%d') `day` FROM" +
                " (SELECT u.`userid`,IF(u.`regtime`< " + starttime + ", " + starttime + ", u.`regtime`) 'regtime' FROM userdetail u " + userwhere + ") n" +
                " GROUP BY `day` ORDER BY `day`";
        List<Map> usercountList = findList(usersql);
        Kv kv = Kv.of();
        long hiscount = 0;
        for (Map m : usercountList) {
            String day = Kv.toAs(m.get("day"), String.class);
            hiscount += Kv.toAs(m.get("count"), long.class);
            kv.put(day, hiscount);
        }
        String day = days.get(days.size() - 1);
        long usercount = (long) kv.get(day);
        double totalvalue = new BigDecimal((double) total / usercount).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println(totalvalue);

        data.forEach((k, v) -> {
            double value = new BigDecimal((double) (long) v / getUserCount(kv, k)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
            data.put(k, value);
        });
        // 数据补齐
        Kv<String, Object> _data1 = Utils.toMap(days, x -> x, x -> data.getOrDefault(x, 0));
        _data1.put("总计", totalvalue);
        ArrayList<Object> list1 = new ArrayList<>();
        _data1.keySet().forEach(x -> {
            Kv of = Kv.of();
            of.set("day", x);
            of.set("value", _data1.get(x));
            list1.add(of);
        });
        Kv kv2 = Kv.of();
        kv2.set("day", "日期")
                .set("value", "值");
        try {
            Workbook workbook = ExcelKit.exportExcel(list1, kv2);
            workbook.write(new FileOutputStream(new File("target/分享率.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private long getUserCount(Kv<String, Long> kv, String day) {
        long count = 0;
        for (Map.Entry<String, Long> entry : kv.entrySet()) {
            count = entry.getValue();
            if (day.compareTo(entry.getKey()) <= 0) {
                break;
            }
        }
        return count;
    }

    @Test
    public void platfUserActive() {
        String sql1 = "SELECT u.userid,u.userno,u.username,u.mobile,u.age,u.birthday,u.gender,u.constellation,MAX(ua.intday) day FROM v09x_platf_core.userdetail u " +
                " LEFT JOIN platf_quest.useractiverecord ua ON u.userid = ua.userid " +
                " WHERE ua.intday > 20200831 AND u.usertype = 2" +
                " GROUP BY ua.userid ORDER BY ua.userid";
        //微动态
        String sql2 = "SELECT a.userid,count(0) count FROM articleinfo a" +
                " WHERE  a.articletype = 1 AND reprintid = '' AND a.status <> 80 AND createtime >= 1598889600000 AND " +
                " a.userid IN (SELECT userid FROM platf_quest.useractiverecord WHERE intday > 20200831 GROUP BY userid) GROUP BY a.userid ";
        //游戏说
        String sql3 = "SELECT a.userid,count(0) count FROM articleinfo a" +
                " WHERE a.articletype = 2 AND a.status <> 80 AND createtime >= 1598889600000  AND " +
                " a.userid IN (SELECT userid FROM platf_quest.useractiverecord WHERE intday > 20200831 GROUP BY userid) GROUP BY a.userid ";
        //评论数
        String sql4 = "SELECT userid,count(userid) count FROM articlecomment WHERE status = 10 AND createtime >= 1598889600000 " +
                " AND userid IN (SELECT userid FROM platf_quest.useractiverecord WHERE intday > 20200831 GROUP BY userid) GROUP BY userid";
        String sql4x = "SELECT userid,count(userid) count FROM gamecomment WHERE status = 10 AND createtime >= 1598889600000 " +
                " AND userid IN (SELECT userid FROM platf_quest.useractiverecord WHERE intday > 20200831 GROUP BY userid) GROUP BY userid";
        //点赞数
        String sql5 = "SELECT userid,count(userid) count FROM articlesupport WHERE status = 10 AND createtime >= 1598889600000 " +
                " AND userid IN (SELECT userid FROM platf_quest.useractiverecord WHERE intday > 20200831 GROUP BY userid) GROUP BY userid";
        String sql5x = "SELECT userid,count(userid) count FROM articlecommentsupport WHERE status = 10 AND createtime >= 1598889600000 " +
                " AND userid IN (SELECT userid FROM platf_quest.useractiverecord WHERE intday > 20200831 GROUP BY userid) GROUP BY userid";
        String sql5y = "SELECT userid,count(userid) count FROM gamecommentsupport WHERE  createtime >= 1598889600000 " +
                " AND userid IN (SELECT userid FROM platf_quest.useractiverecord WHERE intday > 20200831 GROUP BY userid) GROUP BY userid";
        //转发数
        String sql6 = "SELECT a.userid,count(0) count FROM articleinfo a" +
                " WHERE reprintid <> '' AND a.status <> 80 AND createtime >= 1598889600000 AND " +
                " a.userid IN (SELECT userid FROM platf_quest.useractiverecord WHERE intday > 20200831 GROUP BY userid) GROUP BY a.userid ";
        //充值或购买记录
        String sql7 = "SELECT userid,count(userid) count FROM platf_pay.payrecord WHERE status = 10 AND createtime >= 1598889600000 " +
                " AND userid IN (SELECT userid FROM platf_quest.useractiverecord WHERE intday > 20200831 GROUP BY userid) GROUP BY userid";

        List<Map> list = findList(sql1);
        List<Map> dyns = findList(sql2);
        List<Map> plays = findList(sql3);
        List<Map> comments = findList(sql4);
        List<Map> gamecomments = findList(sql4x);
        List<Map> supports = findList(sql5);
        List<Map> commentsupports = findList(sql5x);
        List<Map> gamecommentsupports = findList(sql5y);
        List<Map> reprints = findList(sql6);
        List<Map> pays = findList(sql7);

        Kv<Integer, Long> dynkv = Kv.of();
        Kv<Integer, Long> playskv = Kv.of();
        Kv<Integer, Long> commentskv = Kv.of();
        Kv<Integer, Long> gamecommentskv = Kv.of();
        Kv<Integer, Long> supportskv = Kv.of();
        Kv<Integer, Long> commentsupportskv = Kv.of();
        Kv<Integer, Long> gamecommentsupportskv = Kv.of();
        Kv<Integer, Long> reprintskv = Kv.of();
        Kv<Integer, Long> payskv = Kv.of();
        dyns.forEach(x -> dynkv.set((Integer) x.get("userid"), (Long) x.get("count")));
        plays.forEach(x -> playskv.set((Integer) x.get("userid"), (Long) x.get("count")));
        comments.forEach(x -> commentskv.set((Integer) x.get("userid"), (Long) x.get("count")));
        gamecomments.forEach(x -> gamecommentskv.set((Integer) x.get("userid"), (Long) x.get("count")));
        supports.forEach(x -> supportskv.set((Integer) x.get("userid"), (Long) x.get("count")));
        commentsupports.forEach(x -> commentsupportskv.set((Integer) x.get("userid"), (Long) x.get("count")));
        gamecommentsupports.forEach(x -> gamecommentsupportskv.set((Integer) x.get("userid"), (Long) x.get("count")));
        reprints.forEach(x -> reprintskv.set((Integer) x.get("userid"), (Long) x.get("count")));
        pays.forEach(x -> payskv.set((Integer) x.get("userid"), (Long) x.get("count")));


        ArrayList<Object> list1 = new ArrayList<>();
        list.forEach(x -> {
            Integer userid = (Integer) x.get("userid");
            Kv kv1 = Kv.of();
            Integer gender = (Integer) x.get("gender");
            String g = "未知";
            if (gender == 2) {
                g = "男";
            } else if (gender == 4) {
                g = "女";
            }


            kv1.set("userid", userid)
                    .set("username", x.get("username"))
                    .set("userno", x.get("userno"))
                    .set("mobile", x.get("mobile"))
                    .set("age", x.get("age"))
                    .set("birthday", x.get("birthday"))
                    .set("gender", g)
                    .set("constellation", x.get("constellation"))
                    .set("day", x.get("day"))
                    .set("dyns", dynkv.getOrDefault(userid, 0l))
                    .set("plays", payskv.getOrDefault(userid, 0l))
                    .set("comments", commentskv.getOrDefault(userid, 0l) + gamecommentskv.getOrDefault(userid, 0l))
                    .set("supports", supportskv.getOrDefault(userid, 0l) + commentsupportskv.getOrDefault(userid, 0l) + gamecommentsupportskv.getOrDefault(userid, 0l))
                    .set("reprints", reprintskv.getOrDefault(userid, 0l))
                    .set("pays", payskv.getOrDefault(userid, 0l))
            ;
            list1.add(kv1);
        });

        Kv kv = Kv.of();
        kv.set("userid", "用户ID")
                .set("username", "用户昵称")
                .set("userno", "彩虹号")
                .set("mobile", "手机号")
                .set("age", "年龄")
                .set("birthday", "生日")
                .set("gender", "性别")
                .set("constellation", "星座")
                .set("day", "最后活跃日期")
                .set("dyns", "发布微动态数")
                .set("plays", "发布游戏说数")
                .set("comments", "评论数")
                .set("supports", "点赞数")
                .set("reprints", "转发数")
                .set("pays", "充值或购买次数");

        try {
            Workbook workbook = ExcelKit.exportExcel(list1, kv);
            workbook.write(new FileOutputStream(new File("target/9-10月用户活跃数据.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void xxxx() throws ParseException {

        /*Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(1606266000000l);
        LocalDate startDay = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

        System.out.println(startDay);
        LocalDateTime localDateTime1 = LocalDateTime.of(startDay, LocalTime.of(9, 00, 00));
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = localDateTime1.atZone(zoneId).toInstant();
        System.out.println(instant.toEpochMilli());

        double v = Math.random() * (1606572000000l - 1605056400000l);
        System.out.println(1605056400000l + (long) (Math.random() * (1606572000000l - 1605056400000l)));*/

        /*String s = "5080 ,5090 ,5091 ,5092 ,5093 ,5094 ,5095 ,5096 ,5097 ,5098 ,5099 ,5100 ,5101 ,5102 ,5103 ,5104 ,5105 ,5106 ,5107 ,5108 ,5109 ,5110 ,5111 ,5112 ,5113 ,5114 ,5115 ,5116 ,5117 ,5118 ,5119 ,5120 ,5121 ,5122 ,5123 ,5124 ,5125 ,5126 ,5127 ,5128 ,5129 ,5130 ,5131 ,5132 ,5133 ,5134 ,5135 ,5136 ,5137 ,5138 ,5139 ,5140 ,5141 ,5142 ,5143 ,5144 ,5145 ,5146 ,5147 ,5148 ,5149 ,5150 ,5151 ,5152 ,5153 ,5154 ,5155 ,5156 ,5157 ,5158 ,5159 ,5160 ,5161 ,5162 ,5163 ,5164 ,5165 ,5166 ,5167 ,5168 ,5169 ,5170 ,5171 ,5172 ,5173 ,5174 ,5175 ,5176 ,5177 ,5178 ,5179 ,5180 ,5181 ,5182 ,5183 ,5184 ,5185 ,5186 ,5187 ,5188 ,5189 ,5190 ,5191 ,5192 ,5193 ,5194 ,5195 ,5196 ,5197 ,5198 ,5199 ,5200 ,5201 ,5202 ,5203 ,5204 ,5205 ,5206 ,5207 ,5208 ,5209 ,5210 ,5211 ,5212 ,5213 ,5214 ,5215 ,5216 ,5217 ,5218 ,5219 ,5220 ,5221 ,5222 ,5223 ,5224 ,5225 ,5226 ,5227 ,5228 ,5229 ,5230 ,5231 ,5232 ,5233 ,5234 ,5235 ,5236 ,5237 ,5238 ,5239 ,5240 ,5241 ,5242 ,5243 ,5244 ,5245 ,5246 ,5247 ,5248 ,5249 ,5250 ,5251 ,5252 ,5253 ,5254 ,5255 ,5256 ,5257 ,5258 ,5259 ,5260 ,5261 ,5262 ,5263 ,5264 ,5265 ,5266 ,5267 ,5268 ,5269 ,5270 ,5271 ,5272 ,5273 ,5274 ,5275 ,5276 ,5277 ,5278 ,5279 ,5280 ,5281 ,5282 ,5283 ,5284 ,5285 ,5286 ,5287 ,5288 ,5289 ,5290 ,5291 ,5292 ,5293 ,5294 ,5295 ,5296 ,5297 ,5298 ,5299 ,5300 ,5301 ,5302 ,5303 ,5304 ,5305 ,5306 ,5307 ,5308 ,5309 ,5310 ,5311 ,5312 ,5313 ,5314 ,5315 ,5316 ,5317 ,5318 ,5319 ,5320 ,5321 ,5322 ,5323 ,5324 ,5325 ,5326 ,5327 ,5328 ,5329 ,5330 ,5331 ,5332 ,5333 ,5334 ,5335 ,5336 ,5337 ,5338 ,5339 ,5340 ,5341 ,5342 ,5343 ,5344 ,5345 ,5346 ,5347 ,5348 ,5349 ,5350 ,5351 ,5352 ,5353 ,5354 ,5355 ,5356 ,5357 ,5358 ,5359 ,5360 ,5361 ,5362 ,5363 ,5364 ,5365 ,5366 ,5367 ,5368 ,5369 ,5370 ,5371 ,5372 ,5373 ,5374 ,5375 ,5376 ,5377 ,5378 ,5379 ,5380 ,5381 ,5382 ,5383 ,5384 ,5385 ,5386 ,5387 ,5388 ,5389 ,5390 ,5391 ,5392 ,5393 ,5394 ,5395 ,5396 ,5397 ,5398 ,5399 ,5400 ,5401 ,5402 ,5403 ,5404 ,5405 ,5406 ,5407 ,5408 ,5409 ,5410 ,5411 ,5412 ,5413 ,5414 ,5415 ,5416 ,5417 ,5418 ,5419 ,5420 ,5421 ,5422 ,5423 ,5424 ,5425 ,5426 ,5427 ,5428 ,5429 ,5430 ,5431 ,5432 ,5433 ,5434 ,5435 ,5436 ,5437 ,5438 ,5439 ,5440 ,5441 ,5442 ,5443 ,5444 ,5445 ,5446 ,5447 ,5448 ,5449 ,5450 ,5451 ,5452 ,5453 ,5454 ,5455 ,5456 ,5457 ,5458 ,5459 ,5460 ,5461 ,5462 ,5463 ,5464 ,5465 ,5466 ,5467 ,5468 ,5469 ,5470 ,5471 ,5472 ,5473 ,5474 ,5475 ,5476 ,5477 ,5478 ,5479 ,5480 ,5481 ,5482 ,5483 ,5484 ,5485 ,5486 ,5487 ,5488 ,5489 ,10000 ,11037 ,11038 ,11039 ,11040 ,11041 ,11042 ,11043 ,11044 ,11045 ,11046 ,11047 ,11048 ,11049 ,11050 ,11051 ,11052 ,11053 ,11054 ,11055 ,11056 ,11057 ,11058 ,11059 ,11060 ,11061 ,11062 ,11063 ,11064 ,11065 ,11066 ,11067 ,11068 ,11069 ,11070 ,11071 ,11072 ,11073 ,11074 ,11075 ,11076 ,11077 ,11078 ,11079 ,11080 ,11081 ,11082 ,11083 ,11084 ,11085 ,11086 ,11087 ,11088 ,11089 ,11090 ,11091 ,11092 ,11093 ,11094 ,11095 ,11096 ,11097 ,11098 ,11099 ,11100 ,11101 ,11102 ,11103 ,11104 ,11105 ,11106 ,11107 ,11108 ,11189 ,11190 ,11191 ,11209 ,11264 ,11453 ,11454 ,11455 ,11456 ,11457 ,11458 ,11459 ,11460 ,11461 ,11462 ,11463 ,11464 ,11465 ,11466 ,11467 ,11468 ,11469 ,11470 ,11471 ,11472 ,11501 ,11502 ,11503 ,11504 ,11697 ,11767 ,11834 ,11835 ,11836 ,11837 ,11838 ,11839 ,11840 ,11841 ,11842 ,11843 ,11890 ,12150 ,12588 ,14776 ,14778 ,15248 ,15442 ,15621 ,16403 ,17286 ,17288 ,17690 ,17691 ,17693 ,18189 ,18478 ,18629 ,18745 ,18746 ,18757 ,18760 ,19004 ,19071 ,19077 ,19131 ,19350 ,19730 ,20225 ,20385 ,20458 ,20653 ,20701 ,21320 ,22039 ,22041 ,22047 ,22053 ,22156 ,22540 ,22821";
        String replace = s.replace(" ", "");
        System.out.println(replace);*/
      /*  List<Integer> list = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list2.add(3);
        list2.add(4);
        list2.add(5);
        list.retainAll(list2);
        list.forEach(x -> {
            System.out.println(x);
        });*/

        /*long differDate = getDifferDate("2021-01-22", "2021-01-19");
        System.out.println(differDate);*/

        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        System.out.println(list.indexOf(7));
        list.sort(Integer::compareTo);
      /*  list.forEach(x->{
            System.out.println(x);
        });*/
        list.subList(0, 7).forEach(x -> {
            System.out.println(x);
        });

        list.subList(7, list.size()).forEach(x -> {
            System.out.println(x);
        });
    }

    //获取两个日期相差天数 date1 - date2
    public static long getDifferDate(int date, int date2) {
        LocalDate dateFirst = LocalDate.parse(String.valueOf(date), DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate dateSecond = LocalDate.parse(String.valueOf(date2), DateTimeFormatter.ofPattern("yyyyMMdd"));
        return dateSecond.until(dateFirst, ChronoUnit.DAYS);
    }

    public static long getDifferDate(String date, String date2) {
        LocalDate dateFirst = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate dateSecond = LocalDate.parse(date2, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return dateSecond.until(dateFirst, ChronoUnit.DAYS);
    }

    public void dateToStamp(String str) throws Exception {
        str = "2019-03-13 13:54:00";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(str);
        long ts = date.getTime();
        System.out.println(ts);

    }

    @Test
    public void convertTimeToLong() throws ParseException {
        /*DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDateTime parse = LocalDateTime.parse("2020-11-14 02:00:36", ftf);
        LocalDateTime parse = LocalDateTime.parse("2020-11-07", ftf);
        long l = LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        System.out.println(l);*/
       /* SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = df.parse("2020-11-07");
        long time = date.getTime();
        String format = df.format(time);
        System.out.println(time);*/
        System.out.println(Utility.today());
    }

    //后台编辑绑定前台app用户明细
    @Test
    public void getBind() {
        String sql = "SELECT strvalue,keydesc FROM `dictinfo` WHERE keyname LIKE 'SYS-MEMEBER-USER%'";
        List<Map> list = findList(sql);
        StringBuffer buff = new StringBuffer();
        list.forEach(x -> {
            String strvalue = x.get("strvalue").toString();
            if (strvalue.length() > 0) {
                buff.append(strvalue.substring(1));
            }

        });
        String s = buff.substring(0, buff.length() - 1);
        String sql1 = "SELECT userid,username FROM `userdetail` WHERE userid IN (" + s + ")";
        System.out.println(sql1);
        List<Map> list1 = findList(sql1);
        Kv userkv = Kv.of();
        list1.forEach(x -> {
            userkv.set(Integer.parseInt(x.get("userid").toString()), x.get("username").toString());
        });

        List<Map<String, Object>> l = new ArrayList<>();
        list.forEach(x -> {
            String strvalue = x.get("strvalue").toString();
            String keydesc = x.get("keydesc").toString();
            List<Integer> userids = Utils.strToArr(strvalue, Integer.class);

            String username = keydesc.split("-")[0];
            Kv sheet1 = Kv.of();
            List<Kv> kvs = new ArrayList<>();
            for (int i = 0; i < userids.size(); i++) {
                kvs.add(Kv.of("userid", userids.get(i)).set("username", userkv.get(userids.get(i))));
            }

            sheet1.set("data", kvs);
            sheet1.set("sheetName", username);
            sheet1.set("hdNames", new String[]{"用户ID"});
            sheet1.set("hds", new String[]{"userid", "username"});
            l.add(sheet1);

        });

        Workbook wb = ExcelKit.exportExcels(l);
        try {
            wb.write(new FileOutputStream(new File("target/编辑绑定账户信息.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //处理热搜数据
    @Test
    public void hotSearch() {
        StringBuffer buff = new StringBuffer();
        StringBuffer buff1 = new StringBuffer();
        StringBuffer buff2 = new StringBuffer();
        StringBuffer buff3 = new StringBuffer();
        String[] FIELDS = {"id", "type", "hotvalue"};

        List<Map> list = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\热搜榜xxx.xls"), FIELDS);
//        list.remove(0);//去除多余的行首
        List<Map> maps = findList("SELECT recordid FROM hotsearchrecord WHERE status = 10 ");
        ArrayList<Object> recordids = new ArrayList<>();
        maps.forEach(x -> {
            recordids.add(x.get("recordid").toString());
        });
        list.forEach(x -> {
            if (x.get("id").toString() == "" || x.get("type").toString() == "" || x.get("hotvalue").toString() == "") {
                return;
            }

            String id = x.get("id").toString();
            String type = x.get("type").toString();
            long hotvalue = (long) Float.parseFloat(x.get("hotvalue").toString());
            long createtime = System.currentTimeMillis();
            int t = 0;
            if ("文章".equals(type)) {
                t = 10;
            } else if ("游戏".equals(type)) {
                t = 20;
            } else if ("话题".equals(type)) {
                t = 30;
            }
            String re = t + "-" + id;
            if (recordids.contains(re)) {
                buff.append("UPDATE  `v09x_platf_core`.`hotsearchrecord` SET `hotvalue` = ");

                buff.append(hotvalue + " WHERE recordid = " + "'" + t + "-" + id + "';\n");
            } else {
                buff1.append("INSERT INTO `v09x_platf_core`.`hotsearchrecord`(`recordid`, `type`, `targetid`, `tagname`, `hotvalue`, `updatetime`, `status`)" +
                        " VALUES ( ");
                buff1.append("'" + t + "-" + id + "'," + t + ",'" + id + "'," + "''," + hotvalue + "," + createtime + ",10 );\n");
            }
        });

        FileKit.strToFile(buff1.toString(), new File("tmp/新增热搜数据.sql"));
        FileKit.strToFile(buff.toString(), new File("tmp/修改热搜数据.sql"));

    }

    //用户注册明细 ios和安卓
    @Test
    public void userRegStat() {
        String starttime = "1604160000000";
        String endtime = "1608220800000";

        Supplier<List<String>> daysSupplier = () -> {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(starttime));

            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(Long.parseLong(endtime));

            LocalDate startDay = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            LocalDate endDay = LocalDate.of(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH) + 1, cal2.get(Calendar.DAY_OF_MONTH));

            List<String> days = new ArrayList<>();
            while (true) {
                int _year = startDay.getYear();
                int _month = startDay.getMonthValue();
                int _day = startDay.getDayOfMonth();

                days.add(String.format("%s-%s-%s", _year,
                        _month < 10 ? "0" + _month : "" + _month,
                        _day < 10 ? "0" + _day : "" + _day));
                startDay = startDay.plusDays(1);
                if (startDay.isAfter(endDay)) {
                    break;
                }
            }
            return days;
        };
        List<String> days = daysSupplier.get();

        String where = " WHERE a.status !=80 ";
//        String where = " WHERE status IN (10,20) AND content NOT LIKE '大家好，我是%' ";
        if (!Utils.isEmpty(starttime)) {
            where += " AND a.regtime >= " + starttime;
        }
        if (!Utils.isEmpty(endtime)) {
            where += " AND a.regtime < " + endtime;
        }

        // 总发帖人数
        String select1 = "SELECT COUNT(DISTINCT a.`userid`) 'count',FROM_UNIXTIME(a.`regtime`/1000, '%Y-%m-%d') `day` FROM  userdetail a  ";
        List<Map> list = findList(select1 + (where + " GROUP BY `day`"));
        List<Map> list1 = findList(select1 + (where + " AND a.appos = 'ios' GROUP BY `day`"));
        List<Map> list2 = findList(select1 + (where + " AND a.appos = 'android' GROUP BY `day`"));
        Map<String, Object> data = Utils.toMap(list, x -> Kv.toAs(x.get("day"), String.class), x -> x.get("count"));
        Map<String, Object> data1 = Utils.toMap(list1, x -> Kv.toAs(x.get("day"), String.class), x -> x.get("count"));
        Map<String, Object> data2 = Utils.toMap(list2, x -> Kv.toAs(x.get("day"), String.class), x -> x.get("count"));
        // 数据补齐
        Kv<String, Object> _data = Utils.toMap(days, x -> x, x -> data.getOrDefault(x, 0l));
        Kv<String, Object> _data1 = Utils.toMap(days, x -> x, x -> data1.getOrDefault(x, 0l));
        Kv<String, Object> _data2 = Utils.toMap(days, x -> x, x -> data2.getOrDefault(x, 0l));

        ArrayList<Object> l = new ArrayList<>();
        _data.keySet().forEach(x -> {
            Kv of = Kv.of();
            of.set("day", x);
            of.set("value", _data.get(x));
            of.set("iosvalue", _data1.get(x));
            of.set("anvalue", _data2.get(x));
            l.add(of);
        });
        Kv kv2 = Kv.of();
        kv2.set("day", "日期")
                .set("value", "总数")
                .set("iosvalue", "ios")
                .set("anvalue", "安卓");


        try {
            Workbook workbook = ExcelKit.exportExcel(l, kv2);
            workbook.write(new FileOutputStream(new File("target/注册人数.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //后台编辑绑定前台app用户更新
    @Test
    public void updateBind() {
        String[] FIELDS = {"account", "", "userno"};
        Map<String, List<Map>> map = ExcelKit.readExcelAll(new File("C:\\Users\\wh\\Desktop\\编辑后台帐号管理.xlsx"), FIELDS);

        StringBuffer buff = new StringBuffer();
        map.keySet().forEach(x -> {
            List<Map> list = map.get(x);
            list.remove(0);
            String account = list.get(0).get("account").toString();
            List<Map> maps = findList("SELECT memberid FROM platf_oss.`sys_usermember` WHERE account =  '" + account + "';");
            Set<String> set = new HashSet<>();
            List<String> xxx = new ArrayList<>();
            StringBuffer buffer = new StringBuffer();
            list.forEach(y -> {
                String userno = y.get("userno").toString();
                set.add(userno);
                xxx.add(userno);
            });
//            String s = Utils.arrToStr(set);
            String s = Utils.arrToStr(xxx);
            List<Map> mapss = findList("SELECT userid,userno FROM v09x_platf_core.userdetail WHERE userno IN (" + s.trim().substring(1, s.length() - 1) + ")");
            System.out.println("SELECT userid FROM v09x_platf_core.userdetail WHERE userno IN (" + s.trim().substring(1, s.length() - 1) + ")");
            Set<String> set1 = new HashSet<>();
            List<String> xxx2 = new ArrayList<>();

          /*  mapss.forEach(z -> {
                xxx.forEach(v->{
                    if (v.equals(z.get("userno").toString())){
                        xxx2.add(z.get("userid").toString());
                    }
                });
            });*/

            xxx.forEach(v -> {
                mapss.forEach(z -> {
                    if (v.equals(z.get("userno").toString())) {
                        xxx2.add(z.get("userid").toString());
                    }
                });
            });

            buff.append("UPDATE v09x_platf_core.`dictinfo` SET strvalue = '" + Utils.arrToStr(xxx2));
            buff.append("' WHERE keyname = 'SYS-MEMEBER-USER-" + maps.get(0).get("memberid") + "';\n");


        });
        FileKit.strToFile(buff.toString(), new File("tmp/修改绑定数据.sql"));
    }

    @Test
    public void updateBind2() {
        String[] FIELDS = {"account", "", "", "username"};
        Map<String, List<Map>> map = ExcelKit.readExcelAll(new File("C:\\Users\\wh\\Desktop\\编辑后台帐号管理xxx.xlsx"), FIELDS);

        StringBuffer buff = new StringBuffer();
        map.keySet().forEach(x -> {
            List<Map> list = map.get(x);
            list.remove(0);
            String account = list.get(0).get("account").toString();
            List<Map> maps = findList("SELECT memberid FROM platf_oss.`sys_usermember` WHERE account =  '" + account + "';");
            Set<String> set = new HashSet<>();
            StringBuffer buffer = new StringBuffer();
            list.forEach(y -> {
                String username = y.get("username").toString();
                set.add("'" + username + "'");
            });
            String s = Utils.arrToStr(set);
            List<Map> mapss = findList("SELECT userid FROM v09x_platf_core.userdetail WHERE username IN (" + s.trim().substring(1, s.length() - 1) + ")");

            Set<String> set1 = new HashSet<>();
            mapss.forEach(z -> {
                set1.add(z.get("userid").toString());
            });

            buff.append("UPDATE v09x_platf_core.`dictinfo` SET strvalue = '" + Utils.arrToStr(set1));
            buff.append("' WHERE keyname = 'SYS-MEMEBER-USER-" + maps.get(0).get("memberid") + "';\n");


        });
        FileKit.strToFile(buff.toString(), new File("tmp/修改绑定数据qqq.sql"));
    }

    @Test
    public void dataStat() {
        Workbook workbook = new SXSSFWorkbook();
        for (String catalog : asList("v09x_platf_core", "platf_stat", "platf_sdk", "platf_quest", "platf_pay", "platf_oth", "platf_oss", "platf_oauth", "platf_mall", "platf_live", "platf_im",
                "official_ipci", "official_core")) {

            String sql = String.format("SELECT TABLE_NAME 'name',TABLE_COMMENT 'comment',table_schema 'catalog' " +
                    "        FROM INFORMATION_SCHEMA.TABLES " +
                    "        WHERE TABLE_SCHEMA = '%s'", catalog);

            List<Map> list = findList(sql);
            list.forEach(m -> {
                m.put("count", findColumn(String.format("select count(1) from %s.%s", m.get("catalog"), m.get("name"))));
            });
            list.sort(Comparator.comparing(m -> -Kv.toAs(m.get("count"), int.class)));

            Kv head = Kv.of("name", "name").set("comment", "comment").set("catalog", "catalog").set("count", "count");
            ExcelKit.exportExcel(list, head, workbook, catalog);
        }

        try {
            new File("table-stat.xlsx").delete();
            FileOutputStream fos = new FileOutputStream("table-stat.xlsx");
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int findColumn(String sql) {
        DbAccount dbAccount = new DbAccount();
        dbAccount.setCate("mysql");
//        dbAccount.setUrl("jdbc:mysql://47.111.150.118:6063/platf_quest");
//        dbAccount.setUrl("jdbc:mysql://121.196.17.55:6063/platf_quest");
        dbAccount.setUrl("jdbc:mysql://122.112.180.156:6033/v09x_platf_core");
        dbAccount.setUser("guest");
        dbAccount.setPwd("*Zhong123098!");

//        dbAccount.setPwd("*Hello@27.com!");


        DbKit dbKit = new DbKit(dbAccount, "v09x_platf_core");


        Integer count = dbKit.findColumn(sql, int.class);
        return count;
    }

    //读取账号，批量订阅圈子
    @Test
    public void updateusercile() {
        String[] FIELDS = {"username", "c1", "c2", "c3"};
        List<Map> map = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\账号.xls"), FIELDS);
        map.remove(0);
        List<String> usernames = Utils.toList(map, x -> "'" + x.get("username") + "'");
        String s = Utils.arrToStr(usernames);
        List<Map> userlist = findList("SELECT userid,username,userno,gender FROM v09x_platf_core.userdetail WHERE username IN (" + s.trim().substring(1, s.length() - 1) + ")");
        Kv<String, Integer> toMap = Utils.toMap(userlist, x -> x.get("username").toString(), x -> Integer.parseInt(x.get("userid").toString()));
        Set<String> set = new HashSet<>();
        Map<Integer, List<String>> m = new HashMap<>();
        map.forEach(x -> {
            List l = new ArrayList();
            if (!Utils.isEmpty(x.get("c1"))) {
                set.add("'" + x.get("c1") + "'");
                l.add(x.get("c1").toString());
            }
            if (!Utils.isEmpty(x.get("c2"))) {
                set.add("'" + x.get("c2") + "'");
                l.add(x.get("c2").toString());
            }
            if (!Utils.isEmpty(x.get("c3"))) {
                set.add("'" + x.get("c3") + "'");
                l.add(x.get("c3").toString());
            }
        });
        String c = Utils.arrToStr(set);
        List<Map> clist = findList("SELECT communityid,communityname,gameid FROM v09x_platf_core.communityinfo WHERE communityname IN (" + c.trim().substring(1, c.length() - 1) + ")");
        Kv<String, Map> cmap = Utils.toMap(clist, x -> x.get("communityname").toString(), v -> v);
        StringBuffer buff = new StringBuffer();
        buff.append("INSERT INTO `v09x_platf_core`.`communitybook`(`bookid`, `userid`, `communityid`, `gameid`, `useridentity`, `activecount`, `createtime`, `sequence`, `slogan`, `tags`, `status`) VALUES ");
        map.forEach(x -> {
            String username = x.get("username").toString();
            Integer userid = toMap.get(username);
            if (!Utils.isEmpty(x.get("c1"))) {
                String communityid = cmap.get(x.get("c1").toString()).get("communityid").toString();
                String gameid = cmap.get(x.get("c1").toString()).get("gameid").toString();
                long time = System.currentTimeMillis();
                String bookid = String.format("%s-%s-%s", Utils.fmt36(Long.parseLong(communityid)),
                        Utils.fmt36(userid), Utils.fmt36(time));
                buff.append(String.format("('%s',%s,'%s','%s',%s,%s,%s,%s,'%s','%s',%s),\n", bookid, userid, communityid, gameid, 1, 0, time, 0, "", "", 10));
            }
            if (!Utils.isEmpty(x.get("c2"))) {
                String communityid = cmap.get(x.get("c2").toString()).get("communityid").toString();
                String gameid = cmap.get(x.get("c2").toString()).get("gameid").toString();
                long time = System.currentTimeMillis();
                String bookid = String.format("%s-%s-%s", Utils.fmt36(Long.parseLong(communityid)),
                        Utils.fmt36(userid), Utils.fmt36(time));
                buff.append(String.format("('%s',%s,'%s','%s',%s,%s,%s,%s,'%s','%s',%s),\n", bookid, userid, communityid, gameid, 1, 0, time, 0, "", "", 10));

            }
            if (!Utils.isEmpty(x.get("c3"))) {
                String communityid = cmap.get(x.get("c3").toString()).get("communityid").toString();
                String gameid = cmap.get(x.get("c3").toString()).get("gameid").toString();
                long time = System.currentTimeMillis();
                String bookid = String.format("%s-%s-%s", Utils.fmt36(Long.parseLong(communityid)),
                        Utils.fmt36(userid), Utils.fmt36(time));
                buff.append(String.format("('%s',%s,'%s','%s',%s,%s,%s,%s,'%s','%s',%s),\n", bookid, userid, communityid, gameid, 1, 0, time, 0, "", "", 10));
            }
        });

        FileKit.strToFile(buff.toString(), new File("tmp/批量订阅圈子.sql"));


        ArrayList<Object> l = new ArrayList<>();
        userlist.forEach(x -> {
            Kv of = Kv.of();
            of.set("userno", Long.parseLong(x.get("userno").toString()));
            of.set("username", x.get("username").toString());
            int gender = Integer.parseInt(x.get("gender").toString());
            if (gender == 0) {
                of.set("gender", "未知");
            } else if (gender == 2) {
                of.set("gender", "男");
            } else if (gender == 4) {
                of.set("gender", "女");
            }
            l.add(of);
        });
        Kv kv2 = Kv.of();
        kv2.set("userno", "彩虹号")
                .set("username", "昵称")
                .set("gender", "性别");


        try {
            Workbook workbook = ExcelKit.exportExcel(l, kv2);
            workbook.write(new FileOutputStream(new File("target/编辑用户数据.xls"))); // 将工作簿对象写到磁盘文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //修改编辑账号信息
    @Test
    public void updateuser() {
        String[] FIELDS = {"userno", "username", "gender"};
        List<Map> map = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\编辑用户数据.xls"), FIELDS);
        map.remove(0);
        StringBuffer buff = new StringBuffer();
        StringBuffer buff1 = new StringBuffer();
        map.forEach(x -> {
            long userno = Long.parseLong(x.get("userno").toString());
            String username = x.get("username").toString();
            String gender = x.get("gender").toString();
            int g = 0;
            String face = "";
            if ("男".equals(gender)) {
                g = 2;
            } else if ("女".equals(gender)) {
                g = 4;
            }
            buff.append("UPDATE v09x_platf_core.`userdetail` SET username = '" + username + "',gender = " + g);
            buff.append(" WHERE userno = " + userno + ";\n");

            buff1.append("UPDATE v09x_platf_core.`userdetail` SET face = '" + username + "'");
            buff1.append(" WHERE userno = " + userno + ";\n");
        });

        FileKit.strToFile(buff.toString(), new File("tmp/批量修改编辑用户数据.sql"));
    }

    //修改编辑账号头像信息
    @Test
    public void updateuserface() {
        String[] FIELDS = {"userno", "username", "gender"};
        List<Map> map = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\编辑用户数据.xls"), FIELDS);
        map.remove(0);
        StringBuffer buff = new StringBuffer();
        int nan = 1;
        int nv = 1;
        for (int i = 0; i < map.size(); i++) {
            long userno = Long.parseLong(map.get(i).get("userno").toString());
            String gender = map.get(i).get("gender").toString();
            String face = "";
            if ("男".equals(gender)) {
                if (nan > 9) {
                    face = "https://aimg.woaihaoyouxi.com/app/20210206_face/nan" + nan + ".jpg";
                } else {
                    face = "https://aimg.woaihaoyouxi.com/app/20210206_face/nan0" + nan + ".jpg";
                }
                nan++;
            } else if ("女".equals(gender)) {
                if (nv > 9) {
                    face = "https://aimg.woaihaoyouxi.com/app/20210206_face/nv" + nv + ".jpg";
                } else {
                    face = "https://aimg.woaihaoyouxi.com/app/20210206_face/nv0" + nv + ".jpg";
                }
                nv++;
            }

            buff.append("UPDATE v09x_platf_core.`userdetail` SET face = '" + face + "'");
            buff.append(" WHERE userno = " + userno + ";\n");
        }

        FileKit.strToFile(buff.toString(), new File("tmp/批量修改编辑用户头像数据.sql"));
    }


    @Test
    public void reload() {
        String[] FIELDS = {"userno", "username", "gender"};
        List<Map> map = ExcelKit.readExcel(new File("C:\\Users\\wh\\Desktop\\编辑用户数据.xls"), FIELDS);
        map.remove(0);
        List<String> usernos = Utils.toList(map, x -> "'" + x.get("userno") + "'");
        String s = Utils.arrToStr(usernos);
        List<Map> userlist = findList("SELECT userid FROM v09x_platf_core.userdetail WHERE userno IN (" + s.trim().substring(1, s.length() - 1) + ")");
        List<Integer> userid = Utils.toList(userlist, x -> Integer.parseInt(x.get("userid").toString()));
        System.out.println(Utils.arrToStr(userid));
    }

    @Test
    public void sssss(){
        String x ="\u001FJesusDelAlamo也在会上介绍，888在未来\u001D, \u001f,,,,\b，，\u001D";
        String a = x.replaceAll("[\\u0000-\\u001f\b]","");//转义字符 回车等范围
        System.out.println(a);
        System.out.println(x);

    }

}


