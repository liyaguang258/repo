package net.tccn.open;

import net.tccn.base.*;
import net.tccn.base.dbq.fbean.FBean;
import net.tccn.base.dbq.*;
import net.tccn.dict.DictKit;
import net.tccn.meta.MetaService;
import org.redkale.net.http.HttpScope;
import org.redkale.net.http.RestMapping;
import org.redkale.net.http.RestParam;
import org.redkale.net.http.RestService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 数据服务 对外提供服务
 *
 * @author: liangxianyou at 2019/1/6 20:46.
 */
@RestService(name = "data", automapping = true, comment = "数据服务")
public class DataService extends BaseService {

    @RestMapping(name = "list", auth = false, comment = "数据分页列表")
    public JBean findList(FBean fBean, @RestParam(name = "platToken") String token) {
        JBean jBean = new JBean();
        try {
            fBean.setPlatToken(token);
            fBean.setType("list");
            PageBean page = DbExecutors.findPage(fBean);
            jBean.setBody(page);
        } catch (Exception e) {
            jBean.set(-1, "数据查询失败");
            e.printStackTrace();
        }
        return jBean;
    }

    @RestMapping(name = "save", comment = "数据保存")
    public JBean save(String name, Map<String, String> data, @RestParam(name = "platToken") String token) {
        JBean jBean = new JBean();
        try {
            DbExecutors.save(name, data, token);
        } catch (Exception e) {
            throw new CfgException("保存数据失败");
        }

        return jBean;
    }

    @RestMapping(name = "export", auth = false, comment = "数据导出excel")
    public HttpScope export(FBean fBean, String fileName, @RestParam(name = "platToken") String token) {
        try {
            fBean.setPlatToken(token);
            fBean.setType("export");
            PageBean page = DbExecutors.findPage(fBean);
            Kv heads = MetaKit.cfgExport(fBean.getName(), token);

            if (X.isEmpty(fileName)) {
                fileName = String.format("export_excel_%s", System.currentTimeMillis());
            }

            List<Map> data = page.getRows();

            //dataDeal
            MetaService metaService = MetaKit.getMetaService(fBean.getName(), token);
            List<Map<String, String>> exports = metaService.getExports();
            DictKit dictKit = DictKit.use(token);
            //字典映射 、日期转换 、时间转换
            data.forEach(r -> {
                exports.forEach(x -> {
                    String oldV = r.getOrDefault(x.get("col"), "") + "";
                    String inType = x.getOrDefault("inType", "");

                    String label = "";
                    switch (inType) {
                        case "DICT":
                            label = dictKit.getDictLabel(x.get("inExt"), oldV);
                            break;
                        case "DAY":
                            label = ""; //TODO: 日期转换
                            break;
                        case "TIME":
                            label = ""; //TODO: 时间转换
                            break;
                    }

                    if (!"".equals(label)) {
                        r.put(x.get("col"), label);
                    }
                });
            });

            Kv attrs = Kv.of();
            attrs.put("data", data);
            attrs.put("heads", heads);
            attrs.put("fileName", fileName);

            return HttpScope.refer("excel").attr(attrs);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return HttpScope.refer("excel");
    }

    @RestMapping(name = "del", auth = false, comment = "数据删除")
    public JBean del(String name, Map<String, String> data, @RestParam(name = "platToken") String token) {
        JBean jBean = new JBean();
        try {
            DbExecutors.del(name, data, token);
        } catch (Exception e) {
            jBean.set(-1, "删除数据失败!");
        }
        return jBean;
    }
}
