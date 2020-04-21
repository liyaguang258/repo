package net.tccn.qtask;

import net.tccn.qtask.impl.QTaskEs;
import net.tccn.qtask.impl.QTaskHttp;
import net.tccn.qtask.impl.QTaskMethod;
import net.tccn.qtask.impl.QTaskMysql;

public class QRuner {

    public static Object query(Task task) {
        String cate = task.getDbAccount().getCate();

        switch (cate.toLowerCase()) {
            case "mysql":
                return new QTaskMysql(task).execute();
            case "localapi":
                return new QTaskMethod(task).execute();
            case "http":
                return new QTaskHttp(task).execute();
            case "es":
                return new QTaskEs(task).execute();
        }
        return null;
    }
}
