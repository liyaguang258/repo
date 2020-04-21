package net.tccn.file;

import net.tccn.base.BaseServlet;
import net.tccn.base.JBean;
import net.tccn.base.Kv;
import org.redkale.net.http.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: liangxianyou at 2018/10/11 17:08.
 */
@WebServlet(value = {"/upload/*"}, comment = "测试servlet")
public class FileServlet extends BaseServlet {

    @HttpMapping(url = "/upload/", comment = "文件上传，访问地址：/upload/x")
    public void uploadExcel(HttpRequest request, HttpResponse response) {
        JBean jBean = new JBean();
        List list = new ArrayList();
        try {
            for (MultiPart part : request.multiParts()) {
                String filePath = "u/table/" + part.getFilename();
                File destFile = new File(webroot, filePath);
                destFile.getParentFile().mkdirs();
                part.save(destFile);

                list.add(
                        Kv.of("name", part.getFilename())
                        .set("filePath", filePath)
                        .set("viewPath", filePath)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        response.finish(jBean.set(0, "", list));
    }
}
