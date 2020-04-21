package net.tccn.base;

import com.arangodb.ArangoDBException;
import net.tccn.base.arango.ArangoSource;
import net.tccn.user.User;
import net.tccn.user.UserService;
import org.redkale.net.http.HttpRequest;
import org.redkale.net.http.HttpResponse;
import org.redkale.net.http.HttpServlet;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author: liangxianyou at 2018/11/8 17:05.
 */
public class BaseServlet extends HttpServlet {

    @Resource(name = "SERVER_ROOT")
    protected File webroot;
    @Resource
    private UserService userService;

    public Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    protected void preExecute(HttpRequest request, HttpResponse response) throws IOException {
        String sessionid = request.getParameter("token");
        if (sessionid == null) {
            sessionid = request.getHeader("token");
        }
        if (sessionid == null) {
            sessionid = request.getSessionid(true);
        }

        if (sessionid != null) {
            User user = userService.current(sessionid);
            request.setCurrentUser(user);
        }

        super.preExecute(request, response);
    }

    @Override
    protected void authenticate(HttpRequest request, HttpResponse response) throws IOException {
         //fixme: 权限拦截
        if (request.currentUser() == null) {
            String accept = request.getHeader("Accept");
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With")) || (accept != null && accept.contains("application/json"))) {
                response.finish(JBean.by(-2, "未登陆"));
            } else {
                response.setStatus(302);
                response.setHeader("location", "/user/login.html");
                response.finish();
            }
            return;
        }
        super.authenticate(request, response);
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        try {
            // logger.log(Level.INFO, String.format("%s : %s", new Date(), request.getRequestURI()));
            super.execute(request, response);
        } catch (ArangoDBException e) {
            logger.log(Level.INFO, "arangodb init!", e);
            ArangoSource.use();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            response.finish(JBean.by(-1, e.getMessage()));
        } catch (CfgException e) { // 系统元数据配置异常
            e.printStackTrace();
            System.out.println("-------------------------------------------");
            e.log(request);
            System.out.println("-------------------------------------------");
            response.finish(JBean.by(-1, e.getMessage()));
        }
    }

    public Kv getParams(HttpRequest request, String... key) {
        Kv kv = Kv.of();
        for (String k : key) {
            if (k.contains("=")) {   //如果没有值使用默认值
                kv.put(k.split("=")[0], request.getParameter(k.split("=")[0], k.split("=")[1]));
                continue;
            } else if (k.contains("<")) {  //强制使用"<"右侧的值
                kv.put(k.split("<")[0], k.split("<")[1]);
                continue;
            }
            kv.put(k, request.getParameter(k));
        }
        return kv;
    }
}
