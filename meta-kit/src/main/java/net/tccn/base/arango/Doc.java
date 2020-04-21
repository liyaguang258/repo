package net.tccn.base.arango;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import net.tccn.base.PageBean;
import org.redkale.source.Flipper;

import javax.persistence.Table;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author: liangxianyou at 2018/12/1 22:59.
 */
public abstract class Doc<T extends Doc> {
    private HashMap attr = new HashMap();

    private String _id;
    private String _key;

    private Set<String> _shows;
    private Map _order;
    private List<Map> _filters;//[{col, value, expr}]

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getKey() {
        return _key;
    }

    public void setKey(String key) {
        this._key = key;
    }

    protected Doc<T> set(String k, Object v) {
        attr.put(k, v);
        return this;
    }

    protected <V> V get(String k) {
        return (V)attr.get(k);
    }

    /*public T setShows(String... show) {
        if (_shows == null) {
            _shows = new HashSet<>();
        }
        for (String s : show) {
            _shows.add(s);
        }
        return (T) this;
    }*/

    public Set<String> get_Shows() {
        return _shows;
    }

    /*public void set_Shows(Set<String> shows) {
        this._shows = shows;
    }*/

    public T setOrder(String col, int desc) {
        if (_order == null) {
            _order = new LinkedHashMap();
        }

        _order.put(col, desc);
        return (T) this;
    }

    public Map<String, Integer> getOrder() {
        return _order;
    }

    /*public void setOrder(Map order) {
        this._order = order;
    }*/
    /*@Override
    public String toString() {
        //convert.

        String doc = convert.convertTo(this);
        if (attr.size() == 0) {
            return doc;
        }

        StringBuilder builder = new StringBuilder();
        if (attr.size() != 0) {
            String attrStr = convert.convertTo(attr);
            builder.append("{");
            builder.append(attrStr, 1, attrStr.length() - 1);
            builder.append(",");
            builder.append(doc, 1, doc.length()); //builder.append(doc.substring(1));
        }

        return builder.toString();
    }*/

    private Function<String, String> fieldName = (s) -> {
        return s.replace("get", "").substring(0, 1).toLowerCase() + s.substring(4);
    };

    /**
     * 提取Doc 属性到 Map，用于存贮到Arango中
     * 提取规则：
     * 1、将doc中自带非空（ !=null ）属性提取都map中，
     * 2、将attr中属性覆盖到map中（如果attr中存在同名属性，attr为主）
     * @return
     */
    public Map toDoc() {
        HashMap clone = (HashMap) attr.clone();

        Class<? extends Doc> type = this.getClass();
        Method[] methods = type.getDeclaredMethods();

        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("get") && method.getParameterCount() == 0) {
                //Type mt = method.getAnnotatedReturnType().getType();
                try {
                    //System.out.println(method.getName());
                    Object v = method.invoke(this);
                    if (v != null) {
                        clone.put(fieldName.apply(name), v);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return clone;
    }

    //---------------------------------------------------------------------------------------------------

    private ArangoSource arangoSource;
    private ArangoDatabase db;
    private ArangoCollection collection;

    private static ConcurrentHashMap<Class, Doc> daos = new ConcurrentHashMap();
    public Doc() {
        Table table = this.getClass().getAnnotation(Table.class);
        String sourceName = null;
        Source source = this.getClass().getAnnotation(Source.class);
        if (source != null) {
            sourceName = source.name();
        }

        try {
            arangoSource = ArangoSource.use(sourceName);
            this.db = arangoSource.db(table.catalog());
            this.collection = arangoSource.collection(this);
        } catch (Exception e) {

        }
    }
    protected final static <T extends Doc> T dao(Class<T> type) {

        if (daos.get(type) == null) {
            try {
                daos.put(type, type.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return (T) daos.get(type);
    }

    //ok
    public T save() {
        DocumentCreateEntity<Map> tmap = collection.insertDocument(this.toDoc());
        this.setId(tmap.getId());
        this.setKey(tmap.getKey());
        return (T) this;
    }

    //ok
    public void update() {
        collection.updateDocument(this.getKey(), this.toDoc());
    }

    public <T extends Doc> PageBean<T> findPage(T t, Flipper flipper) {
        if (flipper == null) {
            flipper = new Flipper();
        }
        if (t == null) {
            t = (T) this;
        }

        List<T> list = find(t, flipper.getOffset(), flipper.getLimit());
        long count = count(t);
        return PageBean.by(list, count);
    }

    public T findFirst(T t) {
        return findFirst(arangoSource.parseAql(t, 0, 1), (Class<T>) t.getClass());
    }
    public List<T> find() {
        int count = count(this);
        return find((T) this, 0, count);
    }
    public List<T> find(T t) {
        return find(t, 0, 1000);
    }
    public <T extends Doc> List<T> find(T t, int offset, int ps) {
        if (t == null) {
            t = (T) this;
        }
        return find(arangoSource.parseAql(t, offset, ps), (Class<T>)this.getClass());
    }

    public <T> List<T> find(String aql, Class<T> clazz) {
        try {
            return db.query(aql, clazz).asListRemaining();
        } catch (ArangoDBException e) {
            System.out.println(aql);
            e.printStackTrace();
            ArangoSource.use();
        }
        return db.query(aql, clazz).asListRemaining();
    }
    public <T> T findFirst(String aql, Class<T> clazz) {
        try {
            return db.query(aql, clazz).first();
        } catch (ArangoDBException e) {
            System.out.println(aql);
            e.printStackTrace();
            ArangoSource.use();
        }
        return db.query(aql, clazz).first();
    }

    public int count() {
        return count(this);
    }
    public <T extends Doc> int count(T t) {
        if (t == null) {
            t = (T) this;
        }
        return db.query(arangoSource.parseAqlCount(t), int.class).first();
    }
    //ok
    public <T extends Doc> T findByKey(Object key) {
        return (T) collection.getDocument(String.valueOf(key), this.getClass());
    }

    // ok todo: 将数据放入回收库
    public void delete() {
        collection.deleteDocument(getKey());
    }

    public void delete(Collection<String> key) {
        collection.deleteDocuments(key);
    }

}
