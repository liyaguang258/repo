package net.tccn.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by liangxianyou at 2019/3/15 16:39.
 */
public class RealType<T> {
    public Class<T> getType() {

        Type type = this.getClass().getGenericSuperclass();
        ParameterizedType p=(ParameterizedType)type;

        Class<T> c=(Class<T>) p.getActualTypeArguments()[0];
        System.out.println(c.getName());
        return c;
    }
}
