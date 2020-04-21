package net.tccn.user;

import net.tccn.base.BaseService;
import net.tccn.base.JBean;
import net.tccn.base.MetaKit;
import org.redkale.net.http.RestMapping;
import org.redkale.net.http.RestService;
import org.redkale.net.http.RestSessionid;

/**
 * @author: liangxianyou at 2018/11/22 17:16.
 */
@RestService(name = "user", automapping = true, comment = "用户服务")
public class UserService extends BaseService {

    @RestMapping(name = "login", auth = false, comment = "登陆验证")
    public JBean login(@RestSessionid String sessionid,
                       String username,
                       String pwd) {
        User bean = new User();
        bean.setUsername(username);

        User user = MetaKit.findFirst(bean);
        if (user == null) {
            return JBean.by(-1, "登陆失败:账号无效");
        }

        JBean jBean = user.checkLogin(pwd);
        if (jBean.getCode() == 0) {
            cacheSource.set(30 * 60 * 2,sessionid, User.class, user);

            user.setSessionid(sessionid);
            user.setLoginTime(System.currentTimeMillis());
            MetaKit.save(user);
        }

        return jBean;
    }

    @RestMapping(name = "current")
    public User current(@RestSessionid String sessionid) {
        return getT("user_" + sessionid, User.class, () -> MetaKit.findFirst(new User(sessionid)));
    }

    @RestMapping(name = "logout", comment = "退出登陆")
    public JBean logout(@RestSessionid String sessionid) {
        User user = MetaKit.findFirst(new User(sessionid));
        if (user != null) {
            user.setSessionid("");
            MetaKit.save(user);
        }
        cacheSource.removeAsync("user_" + sessionid);

        return JBean.OK;
    }

}
