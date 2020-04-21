package net.tccn.plat;

import lombok.Data;
import net.tccn.base.arango.Doc;

import javax.persistence.Table;
import java.util.List;

/**
 * 数据库平台
 * @author: liangxianyou at 2018/11/14 12:58.
 */
@Data
@Table(name = "db_plat", catalog = "db_dev")
public class DbPlat extends Doc<DbPlat> {
    public static DbPlat dao = dao(DbPlat.class);

    private String name; //名称
    private String cate; //类型 mysql|ArangoDb
    private String remark; //备注
    private String url; //数据库连接地址
    private String user; //账号
    private String pwd; //密码
    private List<String> catalogs; //库
    private Integer status;//状态 1启用， 0 未启用
}
