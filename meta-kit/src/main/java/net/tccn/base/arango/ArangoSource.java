package net.tccn.base.arango;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.Function;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentDeleteEntity;
import com.arangodb.entity.MultiDocumentEntity;
import net.tccn.base.MetaListenter;
import net.tccn.base.Utils;

import javax.annotation.Resource;
import javax.persistence.Table;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Arrays.asList;

/**
 * 管理 数据源连接对象
 *
 * @author: liangxianyou at 2018/12/15 11:35.
 */
public class ArangoSource {

    @Resource(name = "property.arango.host")
    private String host = "127.0.0.1";
    @Resource(name = "property.arango.user")
    private String user = "root";
    @Resource(name = "property.arango.password")
    private String password = "123456";
    @Resource(name = "property.arango.port")
    private int port = 8529;

    public Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private ArangoDB arangoDb;

    private static Map<String, ArangoSource> sourceMap = new HashMap();

    private ArangoSource() {
        MetaListenter.resourceFactory.inject(this);

        arangoDb = new ArangoDB.Builder().host(host, port).user(user).password(password).build();
    }

    private ArangoSource(ArangoDB arangoDb) {
        MetaListenter.resourceFactory.inject(this);

        this.arangoDb = arangoDb;
    }

    public static ArangoSource use(String unit) {
        ArangoSource arangoSource = new ArangoSource();
        return arangoSource;
    }

    public ArangoDatabase db(String db) {
        return arangoDb.db(db);
    }

    public <T extends Doc> ArangoCollection collection(Doc<T> doc) {
        return collection(doc.getClass());
    }

    public <T extends Doc> ArangoCollection collection(Class<T> type) {
        Table table = type.getAnnotation(Table.class);
        //createDb(table.catalog());
        return arangoDb.db(table.catalog()).collection(table.name());
    }

    public boolean createDb(String db) {
        ArangoDatabase database = arangoDb.db(db);
        if (!database.exists()) {
            return database.create();
        }
        logger.log(Level.INFO, "arango database exists");
        return true;
    }

    public <T extends Doc> ArangoCollection createDocument(Doc<T> doc) {
        Class<? extends Doc> type = doc.getClass();
        Table table = type.getAnnotation(Table.class);

        ArangoDatabase database = arangoDb.db(table.catalog());
        if (!database.exists()) {
            database.create();
        }

        ArangoCollection collection = database.collection(table.name());
        if (!collection.exists()) {
            collection.create();
        }

        return collection;
    }

    // 首字母大写
    private static Function<String, String> upFirst = (s) -> {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    };

    /**
     * Doc 转为查询对象
     *
     * @param t
     * @param <T>
     * @return
     */
    private Function<Doc, StringBuilder> filterBuilder = (t) -> {
        Table table = t.getClass().getAnnotation(Table.class);
        StringBuilder buf = new StringBuilder();

        buf.append("for d in ").append(table.name());
        buf.append(" filter 1==1");
        t.toDoc().forEach((k, v) -> {
            if (v != null && (v.getClass() == String.class || v instanceof Number)) {
                buf.append(" and d.").append(k).append("==");
            }

            if (v.getClass() == String.class) {
                buf.append("'").append(v).append("'");
            } else if (v instanceof Number) {
                buf.append(v);
            }
        });
        return buf;
    };

    private Function<Doc, StringBuilder> orderBuilder = (t) -> {
        StringBuilder buf = new StringBuilder();
        Map<String, Integer> order = t.getOrder();
        if (Utils.isEmpty(order)) {
            return buf.append(" sort d._key desc");
        }
        buf.append(" sort ");
        order.forEach((k, v) -> {
            buf.append("d.").append(k).append(v == 1 ? " desc," : " asc,");
        });
        buf.deleteCharAt(buf.length() - 1);
        return buf;
    };

    private Function<Doc, StringBuilder> returnBuilder = (t) -> {
        StringBuilder buf = new StringBuilder();

        if (Utils.isEmpty(t.get_Shows())) {
            return buf.append(" return d");
        }

        buf.append(" return {");
        t.get_Shows().forEach(x -> {
            buf.append(x).append(":d.").append(x).append(",");
        });
        buf.deleteCharAt(buf.length() - 1).append("}");
        return buf;
    };

    public <T extends Doc> String parseAqlCount(T t) {
        StringBuilder buf = new StringBuilder();
        buf.append(filterBuilder.apply(t));
        buf.append(" COLLECT  WITH COUNT INTO total");
        buf.append(" return total");
        //logger.log(Level.INFO, buf.toString());
        return buf.toString();
    }

    public <T extends Doc> String parseAql(T t, int offset, int ps) {
        if (offset < 0) offset = 0;
        if (ps <= 0) ps = 1000;
        StringBuilder buf = new StringBuilder();

        buf.append(filterBuilder.apply(t));
        buf.append(orderBuilder.apply(t));
        buf.append(" limit ").append(offset).append(",").append(ps);
        buf.append(returnBuilder.apply(t));
        //logger.log(Level.INFO, buf.toString());
        return buf.toString();
    }

    //----------------------------------------
    //ok
    public <T extends Doc> T save(T doc) {
        DocumentCreateEntity<Map> tmap = collection(doc).insertDocument(doc.toDoc());
        doc.setKey(tmap.getKey());
        doc.setId(tmap.getId());
        return doc;
    }

    //ok
    public <T extends Doc> void update(T doc) {
        collection(doc).updateDocument(doc.getKey(), doc.toDoc());
    }

    //ok
    public <T extends Doc> T getDoc(Object key, Class<T> type) {
        return collection(type).getDocument(String.valueOf(key), type);
    }

    //ok
    public <T extends Doc> DocumentDeleteEntity<Void> delete(String key, Class<T> type) {
        return collection(type).deleteDocument(key);
    }

    //ok
    public <T extends Doc> MultiDocumentEntity<DocumentDeleteEntity<Void>> deleteAll(Doc<T>... docs) {
        return collection(docs[0]).deleteDocuments(asList(docs));
    }

    public <T extends Doc> MultiDocumentEntity find(Collection keys, Class<T> type) {
        return collection(type).getDocuments(keys, type);
    }
}
