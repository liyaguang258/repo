package net.tccn.qtask;

import net.tccn.base.BaseService;
import net.tccn.base.JBean;
import net.tccn.base.Kv;
import org.redkale.net.http.RestMapping;
import org.redkale.net.http.RestParam;
import org.redkale.net.http.RestService;

import java.util.Map;

/**
 * @author: liangxianyou at 2018/11/13 18:14.
 */
@RestService(automapping = true, comment = "qtask查询服务")
public class QtaskService extends BaseService {

    // 调用示例: http://qtask_service_addr_xxxxxx/qtask/call?name=abxx&platToken=3421432&para={h:1}
    @RestMapping(name = "call", auth = false)
    public JBean call(String name, Map<String, String> para, @RestParam(name = "platToken") String token) {
        JBean jBean = new JBean();

        Kv kv = Kv.of();
        if (para != null) {
            para.forEach((k, v) -> kv.put(k, v));
        }

        jBean.setBody(TaskKit.taskRun(name, token, kv));
        return jBean;

    }

}
