package net.tccn.base;

import org.redkale.convert.json.JsonConvert;
import org.redkale.net.http.RestMapping;
import org.redkale.service.Service;
import org.redkale.source.CacheSource;
import org.redkale.util.AnyValue;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    @Override
    public void init(AnyValue config) {
        try {
            File file = new File(APP_HOME.toPath() + "/conf/config.txt");
            if (file.exists()) {
                prop.load(new FileInputStream(file));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (!tplInit) {
                tplInit = true;
                //tplKit.addTpl(new File(FileKit.rootPath(), tplPath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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
    public String getProperty(String k, String defaultValue){
        return prop.getProperty(k, defaultValue).replace("${APP_HOME}", APP_HOME.getPath());
    }
    @RestMapping(ignore = true)
    public String getProperty(String k){
        return prop.getProperty(k);
    }

    @RestMapping(ignore = true)
    public String platId(String token) {
        return MetaKit.getPlatId(token);
    }

    public boolean isEmpty(Object obj) {
        return X.isEmpty(obj);
    }
}
