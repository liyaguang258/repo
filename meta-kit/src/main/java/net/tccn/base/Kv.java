package net.tccn.base;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by liangxianyou@eversec.cn at 2018/3/12 14:17.
 */
public class Kv<K,V> extends LinkedHashMap<K,V> {
    public static Kv of(){
        return new Kv();
    }

    public static Kv of(Object k, Object v){
        return new Kv().set(k,v);
    }

    public Kv<K, V> set(K k, V v){
        put(k, v);
        return this;
    }

    public Kv<K,V> putAll(Kv<K,V> kv) {
        kv.forEach((k,v) -> put(k, v));
        return this;
    }

    //  将obj 属性映射到Kv 中
    public static Kv toKv(Object m, String ... fields) {
        Kv kv = Kv.of();
        Stream.of(fields).forEach(field -> {
            String filedT = field;
            String filedS = field;

            try {
                if (field.contains("=")) {
                    String[] arr = field.split("=");
                    filedT = arr[0];
                    filedS= arr[1];
                }

                Method method = m.getClass().getDeclaredMethod("get" + X.toUpperCaseFirst(filedS));
                if (method != null) {
                    kv.set(filedT, method.invoke(m));
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                new IllegalArgumentException(String.format("Kv.toKv获取 获取参数[]失败", field), e);
            }
        });

        return kv;
    }

    public static Kv toKv(Object m) {
        return toKv(m, Kv.of(), m.getClass());
    }

    private static Kv toKv(Object m, Kv kv, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (!kv.containsKey(field.getName())) {
                    kv.set(field.getName(), field.get(m));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        Class superclass = clazz.getSuperclass();
        if (superclass != null) {
            kv = toKv(m, kv, superclass);
        }
        return kv;
    }

    public <T> T toBean(Class<T> type) {
        return toBean(this, type);
    }

    // 首字母大写
    private static Function<String, String> upFirst = (s) -> {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    };

    private static Predicate<Class> isNumber = (t) -> {
        return t == Integer.class || t == int.class
                || t == Long.class || t == long.class
                || t == float.class || t == Float.class
                || t == Double.class || t == double.class
                || t == Short.class || t == short.class
                || t == Byte.class || t == byte.class
                ;
    };

    public static <T> T toAs(Object v, Class<T> clazz) {
        if (v == null) {
            return null;
        } else if (v.getClass() == clazz) {
            return (T) v;
        } else if (clazz ==  String.class) {
            return (T) String.valueOf(v);
        }

        Object v1 = null;
        try {
            if (v.getClass() == Long.class) {//多种数值类型的处理: Long => x
                switch (clazz.getSimpleName()) {
                    case "int":
                    case "Integer": v1 = (int)(long) v; break;
                    case "short":
                    case "Short": v1 = (short)(long) v; break;
                    case "float":
                    case "Float": v1 = (float)(long) v; break;
                    case "byte":
                    case "Byte": v1 = (byte)(long) v; break;
                    default: v1 = v;
                }
            } else if (v.getClass() == Double.class) {
                if (isNumber.test(clazz)) {
                    switch (clazz.getSimpleName()) {
                        case "long":
                        case "Long": v1 = (long)(double) v; break;
                        case "int":
                        case "Integer": v1 = (int)(double) v; break;
                        case "short":
                        case "Short": v1 = (short)(double) v; break;
                        case "float":
                        case "Float": v1 = (float)(double) v; break;
                        case "byte":
                        case "Byte": v1 = (byte)(double) v; break;
                        default: v1 = v;
                    }
                } else if (clazz == String.class){
                    v1 = String.valueOf(v);
                }
            } else if (v.getClass() == String.class) {
                switch (clazz.getSimpleName()) {
                    case "Date":
                        v1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String) v); break;
                    case "short":
                    case "Short": v1 = (short)Double.parseDouble((String) v); break;
                    case "float":
                    case "Float": v1 = (float)Double.parseDouble((String) v); break;
                    case "int":
                    case "Integer": v1 = (int)Double.parseDouble((String) v); break;
                    case "long":
                    case "Long": v1 = (long)Double.parseDouble((String) v); break;
                    case "double":
                    case "Double": v1 = Double.parseDouble((String) v); break;
                    case "byte":
                    case "Byte": v1 = Byte.parseByte((String) v); break;
                    default: v1 = v;
                }
            } else if (v.getClass() == Integer.class) {
                switch (clazz.getSimpleName()) {
                    case "long":
                    case "Long": v1 = (long) (int) v; break;
                    case "short":
                    case "Short": v1 = (short) (int) v; break;
                    case "float":
                    case "Float": v1 = (float) (int) v; break;
                    case "byte":
                    case "Byte": v1 = (byte)(int) v; break;
                    default: v1 = v;
                }
            } else if (v.getClass() == Float.class) {
                switch (clazz.getSimpleName()) {
                    case "long":
                    case "Long": v1 = (long) (float) v; break;
                    case "int":
                    case "Integer": v1 = (int) (float)v; break;
                    case "short":
                    case "Short": v1 = (short) (float) v; break;
                    case "byte":
                    case "Byte": v1 = (byte)(float) v; break;
                    default: v1 = v;
                }
            }

            else {
                v1 = v;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (T) v1;
    }

    public static <T> T toBean(Map map, Class<T> clazz) {
        //按照方法名 + 类型寻找，
        //按照方法名 寻找
        //+
        Object obj = null;
        try {
            obj = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            new IllegalArgumentException("创建对象实列失败", e); // 检查clazz是否有无参构造
        }

        for (String k : (Set<String>)map.keySet()) {
            Object v = map.get(k);
            if (v == null) continue;
            //寻找method
            try {
                String methodName = "set" + upFirst.apply(k);
                Class tClazz = null;
                Method method = null;
                try {
                    method = clazz.getMethod(methodName, tClazz = v.getClass());
                } catch (NoSuchMethodException e) {
                    //e.printStackTrace();
                }
                if (method == null) {
                    for (Method _method : clazz.getMethods()) {
                        if (methodName.equals(_method.getName()) && _method.getParameterCount() == 1) {
                            method = _method;
                            tClazz = _method.getParameterTypes()[0];
                        }
                    }
                }

                if (method == null) {
                    for (Method _method : clazz.getMethods()) {
                        if (methodName.equalsIgnoreCase(_method.getName()) && _method.getParameterCount() == 1) {
                            method = _method;
                            tClazz = _method.getParameterTypes()[0];
                        }
                    }
                }

                if (method != null) {
                    method.invoke(obj, toAs(v, tClazz));
                }

                //没有方法，找属性注解
                if (method == null) {
                    Field field = null;
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field _field : fields) {
                        To to = _field.getAnnotation(To.class);
                        if (to != null && k.equals(to.value())) {
                            field = _field;
                            tClazz = _field.getType();
                            break;
                        }
                    }

                    if (field != null) {
                        field.setAccessible(true);
                        field.set(obj, toAs(v, tClazz));
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return (T) obj;
    }



}