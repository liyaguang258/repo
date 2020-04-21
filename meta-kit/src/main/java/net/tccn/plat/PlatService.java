package net.tccn.plat;

import net.tccn.base.BaseService;
import net.tccn.base.JBean;
import net.tccn.base.MetaKit;
import net.tccn.base.PageBean;
import net.tccn.base.dbq.jdbc.api.DbAccount;
import org.redkale.net.http.RestMapping;
import org.redkale.net.http.RestService;
import org.redkale.source.Flipper;
import org.redkale.util.Comment;

import java.util.List;

@RestService(name = "plat", automapping = true, comment = "业务/数据平台")
public class PlatService extends BaseService {

    @RestMapping(name = "list", comment = "平台列表")
    public JBean list(SysPlat plat, Flipper flipper) {
        JBean jBean = new JBean();

        //PageBean<SysPlat> page = SysPlat.dao.findPage(plat, flipper);
        List<SysPlat> list = MetaKit.getSysPlats();
        PageBean page = PageBean.by(list, list.size());

        return jBean.setBody(page);
    }

    @Comment("平台信息保存")
    public JBean save(SysPlat plat) {
        MetaKit.save(plat);
        return JBean.OK;
    }

    @RestMapping(name = "info", comment = "平台详情")
    public void info(int key) {

    }

    //------------------------

    @RestMapping(name = "db_list", comment = "数据源列表")
    public JBean dbList(DbPlat plat, Flipper flipper) {
        JBean jBean = new JBean();

        List<DbAccount> list = MetaKit.getDbPlats();
        PageBean page = PageBean.by(list, list.size());

        return jBean.setBody(page);
    }

    @RestMapping(name = "db_save", comment = "数据源信息保存")
    public JBean dbSave(DbPlat plat) {
        MetaKit.save(plat);

        return JBean.OK;
    }


}
