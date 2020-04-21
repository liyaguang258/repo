package net.tccn.base.dbq.parser;

import net.tccn.base.dbq.fbean.FBean;

/**
 * Db 执行解释层
 * Created by liangxianyou at 2018/12/24 15:47.
 */
public interface Parser {

    /**
     * 组装完整分页查询
     * @param fBean
     * @return [countSql, listSql]
     */
    String[] parseList(FBean fBean);


    /**
     * 解析入库语句
     * @param data
     * @return
     */
    //String parseSave(MetaService ms, Map data);

}
