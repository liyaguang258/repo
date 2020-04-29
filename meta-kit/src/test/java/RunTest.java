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
import org.junit.Test;
import org.redkale.convert.json.JsonConvert;
import org.redkale.source.CacheMemorySource;
import org.redkale.util.TypeToken;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * @author: liangxianyou at 2019/1/20 12:43.
 */
public class RunTest<T> {
    static {
        MetaKit.init();
    }

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

    TplKit tplKit = TplKit.use();

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
//        dbAccount.setUrl("jdbc:mysql://47.111.150.118:6063/v09x_platf_core");
        dbAccount.setUrl("jdbc:mysql://122.112.180.156:6033/v09x_platf_core");
        dbAccount.setUser("root");
//        dbAccount.setPwd("*Zhong123098!");

        dbAccount.setPwd("*Hello@27.com!");


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
            map.put("updatetime",updatetime);
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
            wb.write(new FileOutputStream(new File("tmp/邀请码_邀请记录_4-29.xls"))); // 将工作簿对象写到磁盘文件
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
        String start = "2020-04-28 00:00:00";
        String end = "2020-04-29 00:00:00";
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
            );

        }
         //生成邀请码,
        List<Map<String, Object>> sheets = new ArrayList<>();
        StringBuilder buf = new StringBuilder("INSERT INTO `v09x_platf_core`.`userinvitecode` (`invitecode`,`userid`,`createtime`) VALUES \n");
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
        FileKit.strToFile(buf.toString(), new File("tmp/邀请码_04-29_严谨.sql"));

        // 创建文件
        Workbook wb = ExcelKit.exportExcels(sheets);
        try {
            wb.write(new FileOutputStream(new File("tmp/邀请码_04-29_严谨.xls"))); // 将工作簿对象写到磁盘文件
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
            buff.append("SET invitecode = '"+invitecode+"' ,");
            buff.append(" batch = '"+batch+"' ,");
            buff.append(" status = "+status+" ");
            buff.append("WHERE mobile = '"+mobile+"';"+"\n");
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
            buff.append("SET healthvalue = "+100+"  ");
//            buff.append(" mobile = '"+mobile+"'  ");
            buff.append("WHERE userid = "+userid+";"+"\n");
        });
//        System.out.println(buff);
        // 问卷更新sql
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
}


