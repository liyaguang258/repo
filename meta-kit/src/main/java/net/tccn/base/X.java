package net.tccn.base;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by liangxianyou at 2019/3/19 18:01.
 */
public class X {

    /**
     * 将集合数组合并到一个Set<T> 集合中
     * @param <T>     泛型
     * @param streams 集合数组
     * @return
     */
    public static <T> Stream<T> concat(Stream<T> ... streams) {
        Stream<T> stream = Stream.empty();
        for (int i = 0; i < streams.length; i++) {
            stream = Stream.concat(stream, streams[i]);
        }
        return stream;
    }

    /**
     * 将字符串第一个字母转大写
     * @param str 待转换字符串
     * @return
     */
    public static String toUpperCaseFirst(String str) {
        Objects.requireNonNull(str);
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 转化集合为数组，带泛型支持
     * @param values 集合
     * @param <T>    泛型
     * @return
     */
    public static <T> T[] toArray(Collection<T> values) {
        if (values == null || values.size() == 0)
            throw new UnsupportedOperationException("Not supported yet.");
        Class<?> clazz = null;
        for (T entity : values) {
            clazz = entity.getClass();
            break;
        }
        return values.toArray((T[]) Array.newInstance(clazz, values.size()));
    }

    /**
     * 转化集合为数组，带泛型支持
     * @param values 集合
     * @param <T>    泛型
     * @return
     */
    public static <T> T[] toArray(Stream<T> values) {
        if (values == null || values.count() == 0)
            throw new UnsupportedOperationException("Not supported yet.");

        List<T> list = values.collect(Collectors.toList());
        return toArray(list);
    }

    /**
     *
     * @param type 待加载的class 类型
     * @param name class 的实现名称
     * @param <T> 泛型<T>
     * @return
     */
    public static <T extends IService> T getDbSource(Class<T> type, String name) {
        ServiceLoader<T> loader = ServiceLoader.load(type);
        Iterator<T> iterator = loader.iterator();

        if (iterator.hasNext()) {
            T t = iterator.next();
            if (name.equalsIgnoreCase(t.getType())) {
                return t;
            }
        }
        return null;
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null)
            return true;
        if (obj instanceof List)
            return ((List) obj).isEmpty();
        if (obj instanceof String)
            return ((String) obj).isEmpty();
        if (obj instanceof Map)
            return ((Map) obj).isEmpty();
        if (obj instanceof Collection)
            return ((Collection) obj).isEmpty();
        if (obj instanceof Object[])
            return ((Object[]) obj).length == 0;
        return false;
    }

}
