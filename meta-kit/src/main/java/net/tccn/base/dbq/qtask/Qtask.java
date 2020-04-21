package net.tccn.base.dbq.qtask;

import lombok.Data;
import net.tccn.base.arango.Doc;

import javax.persistence.Table;

/**
 * @author: liangxianyou at 2018/11/13 14:59.
 */
@Data
@Table(name = "qtask", catalog = "db_dev")
public class Qtask extends Doc<Qtask> {
    public static Qtask dao = dao(Qtask.class);

    private String queryId; //查询id
    private String name; //业务名称
    private String remark; //说明
    private String sql; //SQL
    private String para; //默认查询参数
    private String cate; //任务类型 find|update
    private Integer sysPlatId; //数据平台id
    private Integer platId; //数据平台id
    private String catalog; //数据库名
    private Integer status; //状态  1启用|0未启用|-1删除

    //-------------------------------------

}
