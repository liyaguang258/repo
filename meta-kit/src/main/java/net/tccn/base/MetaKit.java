package net.tccn.base;

import lombok.Getter;
import net.tccn.base.arango.Doc;
import net.tccn.base.dbq.jdbc.api.DbAccount;
import net.tccn.base.dbq.jdbc.api.DbKit;
import net.tccn.base.dbq.table.Field;
import net.tccn.dict.Dict;
import net.tccn.meta.*;
import net.tccn.plat.DbPlat;
import net.tccn.plat.SysPlat;
import net.tccn.qtask.TaskEntity;
import net.tccn.qtask.TaskKit;
import net.tccn.user.User;
import org.redkale.convert.json.JsonConvert;
import org.redkale.util.Comment;
import org.redkale.util.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Created by liangxianyou at 2019/1/7 13:31.
 */
public final class MetaKit {
    //基础数据缓存
    @Getter
    private static List<MetaTable> metaTables;
    @Getter
    private static List<MetaLink> metaLinks;
    @Getter
    private static List<MetaService> metaServices;
    @Getter
    private static List<DbAccount> dbPlats;
    @Getter
    private static List<SysPlat> sysPlats;
    @Getter
    private static List<User> users;
    @Getter
    private static List<TaskEntity> taskEntities;
    @Getter
    private static List<Dict> dicts;

    protected static String dcate = "db";
    protected static String dataPath;
    private static final JsonConvert convert = JsonConvert.root();

    // -----------------------------------
    public static void init() {
        reload(MetaTable.class);
        reload(MetaLink.class);
        reload(MetaService.class);
        reload(DbAccount.class);
        reload(SysPlat.class);
        reload(User.class);
        reload(TaskEntity.class);
        reload(Dict.class);

    }

    public static <T extends Doc> void reload(Class<T> clazz) {
        reload(clazz, null);
    }

    public static <T extends Doc> void reload(T t) {
        reload(t.getClass(), t.getKey());
    }

    public static <T extends Doc> void reload(Class<T> clazz, String key) {

        try {
            File file = new File(String.format("%s%s.json", dataPath, clazz.getSimpleName()));
            if ("file".equals(dcate)) {
                if (MetaTable.class == clazz) {
                    Type type = new TypeToken<List<MetaTable>>() {
                    }.getType();
                    metaTables = convert.convertFrom(type, new FileInputStream(file));
                } else if (MetaLink.class == clazz) {
                    Type type = new TypeToken<List<MetaLink>>() {
                    }.getType();
                    metaLinks = convert.convertFrom(type, new FileInputStream(file));
                } else if (MetaService.class == clazz) {
                    Type type = new TypeToken<List<MetaService>>() {
                    }.getType();
                    metaServices = convert.convertFrom(type, new FileInputStream(file));
                } else if (DbAccount.class == clazz) {
                    Type type = new TypeToken<List<DbAccount>>() {
                    }.getType();
                    dbPlats = convert.convertFrom(type, new FileInputStream(file));
                } else if (DbPlat.class == clazz) {
                    Type type = new TypeToken<List<DbAccount>>() {
                    }.getType();
                    dbPlats = convert.convertFrom(type, new FileInputStream(file));
                } else if (SysPlat.class == clazz) {
                    Type type = new TypeToken<List<SysPlat>>() {
                    }.getType();
                    sysPlats = convert.convertFrom(type, new FileInputStream(file));
                } else if (User.class == clazz) {
                    Type type = new TypeToken<List<User>>() {
                    }.getType();
                    users = convert.convertFrom(type, new FileInputStream(file));
                } else if (TaskEntity.class == clazz) {
                    Type type = new TypeToken<List<TaskEntity>>() {
                    }.getType();
                    taskEntities = convert.convertFrom(type, new FileInputStream(file));
                } else if (Dict.class == clazz) {
                    Type type = new TypeToken<List<Dict>>() {
                    }.getType();
                    dicts = convert.convertFrom(type, new FileInputStream(file));
                }
            } else {
                if (MetaTable.class == clazz) metaTables = MetaTable.dao.find();
                else if (MetaLink.class == clazz) metaLinks = MetaLink.dao.find();
                else if (MetaService.class == clazz) metaServices = MetaService.dao.find();
                else if (DbAccount.class == clazz) dbPlats = DbAccount.dao.find();
                else if (DbPlat.class == clazz) dbPlats = DbAccount.dao.find();
                else if (SysPlat.class == clazz) sysPlats = SysPlat.dao.find();
                else if (User.class == clazz) users = User.dao.find();
                else if (TaskEntity.class == clazz) {
                    taskEntities = TaskEntity.dao.find();
                    TaskKit.init();
                } else if (Dict.class == clazz) {
                    dicts = Dict.dao.find();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cacheSave() {
        cacheSave(MetaTable.class);
        cacheSave(MetaLink.class);
        cacheSave(MetaService.class);
        cacheSave(DbAccount.class);
        cacheSave(SysPlat.class);
        cacheSave(User.class);
        cacheSave(TaskEntity.class);
        cacheSave(Dict.class);
    }

    private static void cacheSave(Class clazz) {
        List list = null;
        if (MetaTable.class == clazz) list = metaTables;
        else if (MetaLink.class == clazz) list = metaLinks;
        else if (MetaService.class == clazz) list = metaServices;
        else if (DbAccount.class == clazz) list = dbPlats;
        else if (SysPlat.class == clazz) list = sysPlats;
        else if (User.class == clazz) list = users;
        else if (TaskEntity.class == clazz) list = taskEntities;
        else if (Dict.class == clazz) list = dicts;

        if (list == null || list.size() == 0) return;

        File file = new File(String.format("%s%s.json", dataPath, list.get(0).getClass().getSimpleName()));
        file.getParentFile().mkdirs();
        FileKit.strToFile(MetaKit.convert.convertTo(list), file);
    }

    /**
     * 得到业务主表
     *
     * @param serviceName
     * @param token
     * @return
     */
    public static MetaTable getMainTable(String serviceName, String token) {
        MetaService metaService = MetaKit.getMetaService(serviceName, token);
        return MetaKit.getMetaTableByAlias(metaService.getTable());
    }

    /**
     * 通过平台token 得到平台字典数据
     *
     * @param platToken
     */
    public static Map<String, List<Dict>> getDictData(String platToken) {
        String platId = MetaKit.getPlatId(platToken);
        Map<String, List<Dict>> dicts = MetaKit.dicts.stream().filter(x -> x.getSysPlatId().equals(platId)).collect(Collectors.groupingBy(Dict::getType));
        return dicts;
    }

    // -----------------------------------
    public void cleanData() {

    }

    /**
     * 通过 别名 查询 MetaTable
     *
     * @param alias
     * @return
     */
    public static MetaTable getMetaTableByAlias(String alias) {
        Optional<MetaTable> table = metaTables.stream().filter(x -> x.getAlias().equals(alias)).findAny();
        return table.orElse(null);
    }

    public static MetaTable getMetaTable(String name, String token) {
        Optional<MetaTable> any = getMetaTables().stream().filter(x -> {
            return x.getName().equals(name) && x.getSysPlatId().equals(getPlatId(token));
        }).findAny();

        return any.get();
    }

    public static MetaService getMetaService(String name, String token) {
        Optional<MetaService> service = metaServices.stream()
                .filter(x -> x.getName().equals(name) && x.getSysPlatId().equals(getPlatId(token)))
                .findAny();
        return service.orElse(null);
    }

    //字段特征排序
    public static BiFunction<MetaTable, String[], MetaTable> sortItem = (t, arr) -> {
        List<Field> items = t.getItems();

        //x 是跨越值
        a:
        for (int i = 0, x = 0; i < arr.length; i++) {
            int inx = i - x;

            if (!items.get(inx).equals(arr[i])) {
                Field tmp = items.get(inx);
                for (int j = inx + 1; j < items.size(); j++) {
                    if (items.get(j).equals(arr[i])) {
                        items.set(inx, items.get(j));
                        items.set(j, tmp);
                        continue a;
                    }
                }

                //运行到这里，说明在  list 中找不到排序标识对应的数据， 让排序角标差值+1
                x++;
            }
        }
        t.setItems(items);
        return t;
    };

    public static Map cfg(String name, String token) {
        MetaService metaService = getMetaService(name, token);
        Kv<String, MetaTable> metaTables = getMetaTables(metaService, false);

        List<Map<String, String>> shows = metaService.getShows();
        List<FromItem> edits = metaService.getEdits();
        List<Map<String, String>> details = metaService.getDetails();
        List<Filter> filters = metaService.getFilters();
        String comment = metaService.getComment();

        //List<Field> items = new ArrayList<>();
        /*Kv<String, Kv<String, Kv>> _items2 = Kv.of();
        metaTables.forEach((k, v) -> {
            Kv<String, Kv> _items = Kv.of();
            v.getItems().forEach(x -> {
                Kv kv = Kv.of();
                kv.set("col", x.getName());
                kv.set("label", x.getLabel());
                kv.set("InExt", x.getInExt());
                kv.set("inType", x.getInType());
                kv.set("expr", x.showField());
                _items.set(x.getName(), kv);
            });

            _items2.set(k, _items);
        });*/

        //shows
        /*List _shows = new ArrayList();
        shows.forEach(x -> {
            MetaTable metaTable = metaTables.get(x.split("[.]")[0]); // 表别名
            metaTable.getItems().stream()
                    .filter(y -> x.split("[.]")[1].equals(y.getName()))
                    .findFirst().ifPresent(y -> _shows.add(Kv.of("col", x).set("order", true)));
        });*/
        /*List<Kv> _shows = shows.stream().map(x -> {
            Kv kv = Kv.of();
            kv.putAll(x);

            if ("FUNC".equalsIgnoreCase(x.get("inType")) || "QTASK".equalsIgnoreCase(x.get("inType")) || "DICT".equalsIgnoreCase(x.get("inType"))) {
                kv.set("fmt", x.get("inType") + "|" + x.get("inExt"));
            } else if ("HIDDEN".equalsIgnoreCase(x.get("inType"))) {
                kv.set("fmt", "HIDDEN");
            } else {
                kv.set("fmt", "");
            }
            kv.remove("inType");
            kv.remove("inExt");
            return kv;
        }).collect(Collectors.toList());*/


        //filters
        /*List<Map> _filters = new ArrayList<>();
        filters.forEach(x -> {
            String col = String.valueOf(x.getName());
            MetaTable metaTable = metaTables.get(col.split("[.]")[0]); // 表别名

            Kv filter = Kv.of();
            metaTable.getItems().stream()
                    .filter(y -> col.split("[.]")[1].equals(y.getName()))
                    .findFirst()
                    .ifPresent(y -> {
                        filter.set("name", x.getName());
                        filter.set("label", x.getLabel() != null ? x.getLabel() : y.getLabel());
                        List<Kv> types = new ArrayList<>();
                        List<String> list = (List) x.getFilterType();
                        list.forEach(t -> {
                            FilterType type = FilterType.getFilterType(t);
                            if (type != null) {
                                types.add(Kv.of("name", t).set("remark", type.getRemark()));
                            }
                        });

                        filter.set("filterType", types);
                        if (!types.isEmpty()) {//设置默认查询项
                            filter.set("type", types.get(0).get("name"));
                        }
                        filter.set("checked", x.isChecked());
                    });
            _filters.add(filter);
        });*/

        //edits
        //List _edits = new ArrayList();//edits;
        /*edits.forEach(x -> {
            Kv kv = Kv.of();
            kv.set("col", x);

            String col = x.get("col") + "";
            //readonly,hidden
            if ("id".equalsIgnoreCase(col)) {
                kv.set("upAttr", "hidden");
                kv.set("addAttr", "none");
            }
            if ("reportWay".equalsIgnoreCase(col)) {
                kv.set("upAttr", "readonly");
                kv.set("addAttr", "none");
            }
            if ("insertTime".equalsIgnoreCase(col)) {
                kv.set("upAttr", "readonly");
                kv.set("addAttr", "none");
            }

            if ("ip".equalsIgnoreCase(col)) {
                kv.set("ck", "ip");
            }

            _edits.add(kv);
        });*/

        //details
        /*List _details = new ArrayList();//details;
        details.forEach(x -> {
            Kv kv = Kv.of();
            kv.set("col", x);

            _details.add(kv);
        });*/

        //pk：业务主表的主键
        StringBuffer _pks = new StringBuffer();
        MetaTable mainTable = metaTables.get(metaService.getTable());
        mainTable.getItems().stream().filter(x -> x.getPk() != null && x.getPk()).forEach(x -> {
            _pks.append(String.format("%s.%s,", metaService.getTable(), x.getName()));
        });
        if (_pks.length() > 0) {
            _pks.deleteCharAt(_pks.length() - 1);
        }
        if (_pks.length() == 0) { // 默认主键
            List<Field> items = metaTables.get(metaService.getTable()).getItems();
            //存在id字段，取id
            items.stream().filter(x -> x.getName().equalsIgnoreCase("id")).findAny().ifPresent(x -> {
                _pks.append(String.format("%s.%s", metaService.getTable(), x.getName()));
            });
        }

        return Kv.of()
                .set("pk", _pks.toString())
                .set("title", comment)
                //.set("items", _items2)
                .set("shows", shows)
                .set("filters", filters)
                .set("edits", edits)
                .set("details", details);
    }

    @Comment("获取导出excel表头配置k-v")
    public static Kv cfgExport(String name, String token) {
        MetaService metaService = getMetaService(name, token);
        List<Map<String, String>> exports = metaService.getExports();

        Kv kv = Kv.of(); // {col:label}
        exports.forEach(x -> {
            kv.put(x.get("col"), x.get("label"));
        });
        return kv;
    }

    //itemUpdate
    public static BiFunction<MetaTable, List<Field>, MetaTable> itemUpdate = (t, fields) -> {
        List<Field> items = t.getItems();
        for (int i = 0; i < fields.size(); i++) {
            for (int j = 0; j < items.size(); j++) {
                if (items.get(j).equals(fields.get(i).getName())) {
                    items.set(j, fields.get(i));
                }
            }
        }
        return t;
    };

    //showUpdate
    /*public static BiFunction<String, List<String>, MetaService> showUpdate = (name, shows) -> {

        MetaService metaService = getMetaService(name, "");

        MetaService _metaService = MetaService.dao.findByKey(metaService.getKey());
        _metaService.setShows(shows);
        _metaService.update();

        metaService.setShows(shows);//更新缓存
        return metaService;
    };*/


    public static MetaTable getMetaTableByKey(String key) {
        return metaTables.stream().filter(x -> x.getKey().equals(key)).findAny().orElse(null);
    }

    public static List<MetaLink> getMetaLinks(String t, List<String> shows, List<String> filters) {


        Predicate<String> contain = s -> {
            for (String item : shows) {
                if (s.equals(item.split("[.]")[0])) {
                    return true;
                }
            }
            for (String item : filters) {
                if (s.equals(item.split("[.]")[0])) {
                    return true;
                }
            }
            return false;
        };

        //得到直接关联
        //1、直接关联 表： t.equals(x.getTables()[0]) || t.equals(x.getTables()[1]
        //2、关联且有过滤：
        //3、关联有展示：
        List<MetaLink> links = metaLinks.stream()
                .filter(x -> {
                    return (t.equals(x.getTables()[0]) || t.equals(x.getTables()[1]))
                            && (contain.test(x.getTables()[0]) || contain.test(x.getTables()[1]));
                })
                .collect(Collectors.toList());
        return links;
    }

    public static Kv buildeDetail(MetaService metaService) {
        //tables
        Kv<String, MetaTable> tables = getMetaTables(metaService, true);
        return Kv.of("tables", tables)
                .set("links", Kv.of());
    }

    public static Kv<String, MetaTable> getMetaTables(MetaService metaService, Boolean all) {
        Kv<String, MetaTable> tables = Kv.of();

        String table = metaService.getTable();//
        tables.set(table, getMetaTableByAlias(table));

        //收集所有的col
        Set<String> allAlias;

        if (!all) {
            allAlias = X.concat(
                    metaService.getFilters().stream().map(f -> {
                        String col = (String) f.getName();
                        String alias = col.split("[.]")[0];
                        return alias;
                    }),
                    metaService.getExports().stream().map(x -> x.get("col").split("[.]")[0]),
                    metaService.getShows().stream().map(x -> x.get("col").split("[.]")[0])/*,  todo: xxx
                    metaService.getEdits().stream().map(x -> x.split("[.]")[0])*/
            ).collect(Collectors.toSet());

        } else {
            allAlias = new HashSet<>();
            metaLinks.stream().forEach(x -> {
                if (table.equals(x.getTables()[0])) {
                    allAlias.add(x.getTables()[1]);
                } else if (table.equals(x.getTables()[1])) {
                    allAlias.add(x.getTables()[0]);
                }
            });
        }

        allAlias.forEach(x -> tables.set(x, getMetaTableByAlias(x)));
        return tables;
    }

    /*public static DbKit getDbKit(String dbPlatId) {
        Optional<DbAccount> dbAccount = dbPlats.stream().filter(x -> x.getKey().equals(dbPlatId)).findAny();

        return new DbKit(dbAccount.get());
    }*/
    public static DbKit getDbKit(String dbPlatId, String catalog) {
        Optional<DbAccount> dbAccount = dbPlats.stream().filter(x -> x.getKey().equals(dbPlatId)).findAny();
        return new DbKit(dbAccount.get(), catalog);
    }

    public static DbAccount getDbPlat(String dbPlatId) {
        Optional<DbAccount> dbAccount = dbPlats.stream().filter(x -> x.getKey().equals(dbPlatId)).findFirst();

        return dbAccount.get();
    }

    @Comment("通过平台token  得到平台id")
    public static String getPlatId(String platToken) { //
        Optional<SysPlat> plat = sysPlats.stream().filter(x -> x.getToken().equals(platToken)).findAny();
        return plat.get().getKey();
    }

    public static String lastAlias;

    public static String nextAlias() {
        if (lastAlias == null) {
            String aql = TplKit.use(true).getTpl("metaTable.lastAlias");
            lastAlias = MetaTable.dao.findFirst(aql, String.class);
        }
        return lastAlias = next(lastAlias, "");
    }

    //使用['a',...,'z'] 创建Table 别名
    private static String next(String x, String end) {
        if (x == null || "".equals(x)) {
            return "a" + end;
        }
        char c = x.charAt(x.length() - 1);
        String sub = x.substring(0, x.length() - 1);
        if (c < 'z') {
            return sub + (++c) + end;
        } else if (c == 'z') {
            return next(sub, "a" + end);
        }
        return null;
    }

    public static List<String> tableExist(String[] tableArr, String token) {
        List<String> _tableArr = asList(tableArr);
        List<String> hv = metaTables.stream()
                .filter(x -> _tableArr.contains(x.getName()) && x.getSysPlatId().equals(getPlatId(token)))
                .map(MetaTable::getName)
                .collect(Collectors.toList());
        return hv;
    }

    // ---------------------- repository -------------------
    public static <T extends Doc> void save(T... ts) {
        if (ts == null || ts.length == 0) return;

        for (T t : ts) {
            if ("file".equals(dcate)) {
                if (t.getKey() == null) { //数据新增异常提示
                    throw new UnsupportedOperationException("数据文件形式启动服务，不支持配置新增！");
                }

                cacheSave(t.getClass());
                return;
            }

            if (t.getKey() == null) {
                t.save();
            } else {

                if (t instanceof MetaLink) {
                    //避免删除属性无效
                    if (((MetaLink) t).getLink() != null && ((MetaLink) t).getLink().size() > 0) {
                        t.find(String.format("UPDATE '%s' WITH { link:null  } IN MetaLink", t.getKey()), Map.class);
                    }
                }

                t.update();
            }
        }

        reload(ts[0].getClass());
    }

    public static <T extends Doc> T findFirst(T t) {
        Objects.nonNull(t);

        List<T> list = asList();
        Map doc = t.toDoc();
        if (t instanceof User) {
            list = (List<T>) users;
        }

        Optional any = list.stream().filter(x -> {
            Map map = x.toDoc();
            Set<String> keySet = doc.keySet();
            for (String k : keySet) {
                if (doc.get(k) != null && (doc.get(k) instanceof String && doc.get(k).equals(map.get(k)))) {
                    return true;
                }
            }
            return false;
        }).findAny();

        return (T) any.orElse(null);
    }
}
