package net.tccn.base;

import org.redkale.convert.json.JsonConvert;
import org.redkale.net.http.RestMapping;
import org.redkale.service.Service;
import org.redkale.source.CacheSource;

import javax.annotation.Resource;
import java.io.File;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * @author: liangxianyou at 2018/10/22 11:49.
 */
public class BaseService implements Service {

    protected final static JsonConvert convert = JsonConvert.root();

    @Resource(name = "SERVER_ROOT")
    protected File webroot;

    public Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    public static boolean isWinos = System.getProperty("os.name").contains("Window");

    @Resource(name = "cacheSource")
    protected CacheSource cacheSource;

    @Resource(name = "APP_HOME")
    protected File APP_HOME;

    public static Properties prop = new Properties();
    protected static TplKit tplKit = TplKit.use(true);

    private static boolean tplInit = false;

    @RestMapping(ignore = true)
    public <T> T getT(String key, Class<T> clazz, Supplier<T> supplier) {
        Object obj = cacheSource.getAndRefresh(key, 1000 * 60 * 3, clazz);
        if (obj != null) {
            return (T) obj;
        }

        T t = supplier.get();
        if (t != null) {
            cacheSource.set(1000 * 60 * 3, key, clazz, t);
        }
        return t;
    }

    @RestMapping(ignore = true)
    public String platId(String token) {
        return MetaKit.getPlatId(token);
    }

    public boolean isEmpty(Object obj) {
        return Utils.isEmpty(obj);
    }
}
