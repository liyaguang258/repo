package net.tccn.dict;

import net.tccn.base.MetaKit;

import java.util.*;

/**
 * 字典工具类
 * 每个 token（平台token） 对应一个DictKit实例
 *
 * @author: liangxianyou
 */
public final class DictKit {
    private static Map<String, DictKit> kits = new HashMap<>();
    private String platToken;
    private Map<String, List<Dict>> dicts;

    private DictKit() {
    }

    public synchronized static DictKit use(String platToken) {
        DictKit dictKit = kits.get(platToken);
        if (dictKit == null) {
            dictKit = new DictKit();
            dictKit.platToken = platToken;
            dictKit.dicts = MetaKit.getDictData(platToken);
            kits.put(platToken, dictKit);
        }
        return dictKit;
    }

    public synchronized void reload() {
        this.dicts = MetaKit.getDictData(platToken);
    }

    // 初始化字典，不同模式下，数据来源不同

    private void stop() {
        //文件模式下将 字典数据写入到json文件
    }

    private void loadByFile() {

    }

    /**
     * 通过arangodb 加载字典数据
     */
    private void loadByArangodb() {
        /*String host = PropKit.getProperty("arango.host");
        int port = Kv.toAs(PropKit.getProperty("arango.port", "8529"), int.class);
        String user = PropKit.getProperty("arango.user");
        String passwd = PropKit.getProperty("arango.passwd");

        ArangoDB arangoDb = new ArangoDB.Builder().host(host, port).user(user).password(passwd).build();
        ArangoDatabase dbDemo = arangoDb.db("db_demo");

        String platId = "28121369";//MetaKit.getPlatId(platToken);
        String dictAQL = String.format("for d in Dict filter d.sysPlatId=='%s'return d", platId);
        List<Dict> list = (List) dbDemo.query(dictAQL, Map.class).asListRemaining();

        String[] fields = {"label", "value", "pValue"};
        list.forEach(x -> {
            String type = x.get("type");
            List<Dict> items = dicts.getOrDefault(type, new ArrayList<>());
            Map dict = new HashMap();
            for (String field : fields) {
                dict.put(field, x.get(field));
            }
            items.add(dict);
            dicts.put(type, items);
        });*/
    }

    /**
     * 通过excel 文件加载字典
     */
    private void loadByExcel() {
        /*Map<String, List<Map>> dicts = ExcelKit.readExcelAll(new File("tmp/ip_dict_ok.xlsx"), new String[]{"", "label", "value", "pValue"});
        dicts.forEach((k, v) -> {
            String code = k.substring(k.lastIndexOf("|") + 1);
            List<Map> list = v;
            list.remove(0);
            DictKit.dicts.put(code, list);
        });

        Predicate<Map> isProvice = (s) -> String.valueOf(s.get("value")).trim().endsWith("0000");
        Predicate<Map> isCity = (s) -> !isProvice.test(s); // && String.valueOf(s.get("value")).trim().endsWith("00");
        BiPredicate<Map, Map> belongProvice = (p, x) -> String.valueOf(x.get("value")).trim().startsWith(String.valueOf(p.get("value")).trim().substring(0, 2));


        // 城市数据
        List<Map> cityAll = ExcelKit.readExcel(new File("tmp/city.xls"), new String[]{"value", "label1", "label2"});
        List<Map> provice = cityAll.stream().filter(x -> isProvice.test(x)).map(x -> {
            x.put("label", String.valueOf(x.get("label1")).trim());
            x.put("value", String.valueOf(x.get("value")).trim());
            x.put("pValue", "0");
            return x;
        }).collect(Collectors.toList());

        List<Map> city = cityAll.stream().filter(x -> isCity.test(x)).collect(Collectors.toList());
        provice.forEach(p -> {
            city.stream().filter(x -> belongProvice.test(p, x)).forEach(x -> {
                x.put("label", String.valueOf(x.get("label2")).trim());
                x.put("value", String.valueOf(x.get("value")).trim());
                x.put("pValue", String.valueOf(p.get("value")).trim());
            });
        });

        List<Map> cityDict = new ArrayList<>();
        cityDict.addAll(provice);
        cityDict.addAll(city);
        cityDict.forEach(x -> {
            x.remove("label1");
            x.remove("label2");
        });
        DictKit.dicts.put("city", cityDict);*/
    }

    /**
     * 查询指定类别的字典
     *
     * @param code
     * @return
     */
    public List<Dict> getDicts(String code) {
        Objects.requireNonNull(code, "code 不能为空");
        return dicts.getOrDefault(code, new ArrayList<>());
    }

    public Map<String, List<Dict>> getDicts() {
        return dicts;
    }

    public String getDictLabel(String code, String value) {
        Objects.requireNonNull(code, "code 不能为空");
        Objects.requireNonNull(value, "value 不能为空");
        List<Dict> dicts = getDicts(code);
        Optional<Dict> any = dicts.stream().filter(x -> value.equals(x.getValue())).findAny();
        return any.isPresent() ? any.get().getLabel() : "";
    }

    public String getDictValue(String code, String label) {
        Objects.requireNonNull(code, "code 不能为空");
        Objects.requireNonNull(label, "label 不能为空");

        List<Dict> dicts = getDicts(code);
        Optional<Dict> any = dicts.stream().filter(x -> label.equals(x.getLabel())).findAny();
        return any.isPresent() ? any.get().getValue() : "";
    }

    //@Test
    public void dictTest() {
        System.out.println(getDictLabel("useSubclass", "3"));   // 云
        System.out.println(getDictValue("isp", "广电"));        // 5

    }

}
