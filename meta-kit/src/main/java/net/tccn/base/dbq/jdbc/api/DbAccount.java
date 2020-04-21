package net.tccn.base.dbq.jdbc.api;

import lombok.Data;
import net.tccn.base.arango.Doc;

import javax.persistence.Table;

/**
 * 数据库平台
 * @author: liangxianyou at 2018/11/14 12:58.
 */
@Data
@Table(name = "db_plat", catalog = "db_dev")
public class DbAccount extends Doc<DbAccount> {
    public static DbAccount dao = dao(DbAccount.class);

    private String name; //名称
    private String cate; //类型 mysql|ArangoDb
    private String remark; //备注
    private String url; //数据库连接地址
    private String user; //账号
    private String pwd; //密码
    private String[] catalogs; //库

    //----------------------------

    public String accountKey() {
        int start = url.indexOf("//") + 2;
        int end = url.indexOf("/", start);
        int endDef = url.indexOf("?", end);
        if (endDef == -1) {
            endDef = url.length();
        }
        String host = url.substring(start, end == -1 ? url.length() : end);
        return user + "@" + host;
    }
}
