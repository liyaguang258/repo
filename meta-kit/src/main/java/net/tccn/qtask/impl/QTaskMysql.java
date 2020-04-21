package net.tccn.qtask.impl;

import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import net.tccn.base.MetaKit;
import net.tccn.base.dbq.jdbc.api.DbKit;
import net.tccn.qtask.QTask;
import net.tccn.qtask.Task;

import java.util.Map;

public class QTaskMysql extends QTaskAbs implements QTask {

    public static Engine engine = Engine.create("sqlTpl");
    private static MysqlDialect dialect = new MysqlDialect();

    protected DbKit dbKit;

    static {
        engine.setDevMode(true);
    }

    public QTaskMysql(Task task) {
        super(task);
        this.dbKit = MetaKit.getDbKit(task.getDbPlatId(), task.getCatalog());
    }

    @Override
    public Object execute() {
        Template tpl = engine.getTemplateByString(task.getContent());
        String sql = tpl.renderToString(getTask().getPara()).replaceAll("[\\s]+", " ");

        /*
        // todo: 从sql分析，支持多种sql处理类别，
        if (sql.startsWith("select count")) {
            return dbKit.queryInt(sql);
        } else if (sql.startsWith("select ")) {
            String[] sqls = PageSqlKit.parsePageSql(sql);

            String findTotal = "select count(*) " + dialect.replaceOrderBy(sqls[1]);
            String findList = sqls[0] + " " + sqls[1];

            CompletableFuture<Integer> countFuture = CompletableFuture.supplyAsync(() -> dbKit.queryColumn(findTotal, int.class));
            CompletableFuture<List<Map>> listFuture = CompletableFuture.supplyAsync(() -> dbKit.findList(findList, Map.class));

            try {
                return PageBean.by(listFuture.get(), countFuture.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else if (sql.startsWith("update ") || sql.startsWith("UPDATE ") ||
                sql.startsWith("delete ") || sql.startsWith("DELETE ") ||
                sql.startsWith("delete ") || sql.startsWith("DELETE ") ||
                sql.startsWith("insert ") || sql.startsWith("INSERT ")){
            dbKit.exetute(sql);
        }*/

        return dbKit.findList(sql, Map.class);
    }
}
