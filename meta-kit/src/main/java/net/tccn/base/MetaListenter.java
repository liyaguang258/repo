package net.tccn.base;

import org.redkale.boot.Application;
import org.redkale.boot.ApplicationListener;
import org.redkale.util.ResourceFactory;

import javax.annotation.Resource;
import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * @author: liangxianyou
 */
public class MetaListenter implements ApplicationListener {

    @Resource(name = "property.dataCate")
    private String dcate;
    @Resource(name = "property.tplPath")
    private String tplPath;
    @Resource(name = "property.dataPath")
    private String dataPath;


    @Override
    public void preStart(Application application) {
        CompletableFuture.runAsync(()-> {
            ResourceFactory rf = application.getResourceFactory();
            rf.inject(this);

            MetaKit.dcate = dcate;
            MetaKit.dataPath = dataPath;
            MetaKit.init();
            TplKit.use(true).addTpl(new File(FileKit.rootPath(), tplPath));
        });
    }

    @Override
    public void preShutdown(Application application) {
        if ("db".equals(dcate)) {
            MetaKit.cacheSave();
        }
    }

}
