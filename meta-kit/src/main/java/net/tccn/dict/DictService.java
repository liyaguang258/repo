package net.tccn.dict;

import net.tccn.base.BaseService;
import net.tccn.base.JBean;
import org.redkale.net.http.RestMapping;
import org.redkale.net.http.RestParam;
import org.redkale.net.http.RestService;

import java.util.List;
import java.util.Map;

/**
 * 字典服务
 *
 * @author: liangxianyou
 */
@RestService(name = "dict", automapping = true, comment = "字典服务")
public class DictService extends BaseService {

    @RestMapping(name = "list", comment = "根据type 加载对应的字典列表")
    public JBean list(@RestParam(name = "platToken") String token, String type) {
        JBean jBean = new JBean();
        DictKit dictKit = DictKit.use(token);

        if (!isEmpty(type)) {
            List<Dict> dicts = dictKit.getDicts(type);
            jBean.setBody(dicts);
        } else {
            Map dicts = dictKit.getDicts();
            jBean.setBody(dicts);
        }

        return jBean;
    }
}
