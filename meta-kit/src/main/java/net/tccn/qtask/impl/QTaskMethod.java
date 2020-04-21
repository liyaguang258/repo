package net.tccn.qtask.impl;

import net.tccn.base.Kv;
import net.tccn.qtask.QTask;
import net.tccn.qtask.Task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class QTaskMethod extends QTaskAbs implements QTask {

    public QTaskMethod(Task e) {
        super(e);
    }

    public String getClazz(){
        String content = getContent(); //class + method
        return content.substring(0, content.lastIndexOf("."));
    }

    public String getMethod(){
        String content = getTask().getContent();
        return content.substring(content.lastIndexOf(".") + 1);
    }

    @Override
    public Object execute() {
        try {
            Class<?> type = Class.forName(getClazz());
            Object obj = type.newInstance();

            Method method = type.getMethod(getMethod(), Kv.class);

            return method.invoke(obj, getPara());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
