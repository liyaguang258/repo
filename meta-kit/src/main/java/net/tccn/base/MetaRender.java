package net.tccn.base;

import org.apache.poi.ss.usermodel.Workbook;
import org.redkale.convert.Convert;
import org.redkale.net.http.*;
import org.redkale.util.AnyValue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author: liangxianyou
 */
public class MetaRender implements HttpRender<HttpScope> {
    @Override
    public void init(HttpContext context, AnyValue config) {

    }

    @Override
    public <V extends HttpScope> void renderTo(HttpRequest request, HttpResponse response, Convert convert, V scope) {
        String referid = scope.getReferid();
        Map<String, Object> attr = scope.getAttributes();
        if ("excel".equals(referid)) {
            List list = (List) attr.get("data");
            Kv heads = (Kv) attr.get("heads");
            String fileName = (String) attr.get("fileName");
            if (!fileName.endsWith(".xls")) {
                fileName += ".xls";
            }

            try {
                Workbook workbook = ExcelKit.exportExcel(list, heads);

                File file = new File(String.format("tmp/%s", fileName));
                file.getParentFile().mkdirs();
                if (file.exists()) file.delete();

                FileOutputStream fos = new FileOutputStream(file);
                workbook.write(fos);
                try {
                    fos.close();
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                response.finish(fileName, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Class<HttpScope> getType() {
        return HttpScope.class;
    }

}
