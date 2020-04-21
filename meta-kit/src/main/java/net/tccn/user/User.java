package net.tccn.user;

import lombok.Data;
import net.tccn.base.JBean;
import net.tccn.base.arango.Doc;
import org.redkale.util.Utility;

import javax.persistence.Table;

/**
 * @author: liangxianyou at 2018/11/22 17:37.
 */
@Data
@Table(name = "sys_user", catalog = "db_dev")
public class User extends Doc<User> {
    public static User dao = dao(User.class);

    private String username;
    //@ConvertColumn(ignore = true,type = ConvertType.JSON)
    private String pwd;
    private Long createTime;
    private Long loginTime;
    private Integer status;
    private String sessionid;

    public User() {
    }

    public User(String sessionid) {
        this.sessionid = sessionid;
    }
    //-------------------------------

    public static String md5IfNeed(String password){
        if (password == null || password.length() == 0) {
            return "";
        }
        return Utility.md5Hex(password);
    }

    public JBean checkLogin(String pwd) {
        JBean jBean = JBean.by(0, "");

        if (this.pwd == null || this.pwd.isEmpty()) {
            jBean.set(-1, "密码错误");
        }
        if (!this.pwd.equalsIgnoreCase(md5IfNeed(pwd))) {
            jBean.set(-1, "密码错误");
        } else if (status != 1) {
            jBean.set(-1, "登陆失败，限制登陆。");
        }
        jBean.setBody(this);
        return jBean;
    }

}
