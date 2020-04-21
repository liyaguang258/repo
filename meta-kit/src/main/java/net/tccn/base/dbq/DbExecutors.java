package net.tccn.base.dbq;

import net.tccn.base.MetaKit;
import net.tccn.base.PageBean;
import net.tccn.base.dbq.fbean.FBean;
import net.tccn.base.dbq.jdbc.api.DbKit;
import net.tccn.base.dbq.parser.ParseMysql;
import net.tccn.meta.MetaService;
import net.tccn.meta.MetaTable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Db 调度层
 */
public class DbExecutors {
    private final static ParseMysql PARSER = new ParseMysql();

    public static PageBean findPage(FBean fBean) throws ExecutionException, InterruptedException {
        //sql解析
        String[] sqls = PARSER.parseList(fBean);

        //当前的业务 => 获取主表 信息 => 数据源信息 => 数据源对象  => 创建数据工具对象 => 查询数据
        MetaService metaService = MetaKit.getMetaService(fBean.getName(), fBean.getPlatToken());
        MetaTable mainTable = MetaKit.getMetaTableByAlias(metaService.getTable());
        DbKit dbKit = MetaKit.getDbKit(mainTable.getDbPlatId(), mainTable.getCatalog());
        //System.out.printf("----------------%n countSql:%s%n findSql:%s%n----------------%n", sqls[0], sqls[1]);

        CompletableFuture<Integer> countFuture = CompletableFuture.supplyAsync(() -> dbKit.queryColumn(sqls[0], int.class));
        CompletableFuture<List<Map>> listFuture = CompletableFuture.supplyAsync(() -> dbKit.findList(sqls[1], Map.class));

        List<Map> rows = listFuture.get();
        Integer total = countFuture.get();

        return PageBean.by(rows, total);
    }

    public static void del(String name, Map data, String token) {
        MetaService metaService = MetaKit.getMetaService(name, token);
        MetaTable mainTable = MetaKit.getMetaTableByAlias(metaService.getTable());
        DbKit dbKit = MetaKit.getDbKit(mainTable.getDbPlatId(), mainTable.getCatalog());

        String delSql = PARSER.parseDel(name, data, token);
        dbKit.exetute(delSql);
    }

    public static void save(String name, Map data, String token) {
        MetaTable mainTable = MetaKit.getMainTable(name, token);
        DbKit dbKit = MetaKit.getDbKit(mainTable.getDbPlatId(), mainTable.getCatalog());

        String sql = PARSER.parseSave(name, data, token);
        dbKit.exetute(sql);
    }
}
