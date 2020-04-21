package net.tccn.base;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author: liangxianyou
 */
public final class PropKit {
    private static Properties properties = new Properties();
    static {
        try {
            // 读取导入配置文件

            properties.load(new FileReader(new File("conf/config.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    public static String getProperty(String key, String value) {
        String oldValue = getProperty(key);
        properties.setProperty(key, value);
        return oldValue;
    }
}
