import lombok.Getter;
import lombok.Setter;
import org.redkale.convert.json.JsonConvert;
import org.redkale.util.TypeToken;

import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SearchVisLogRecord {
    @Id
    private String vislogid;//用户id+'-'+时间戳
    private int userid;
    private String uri;
    private String importid;
    private Map<String, String> params;
    private Map<String, String> headers;
    private String ip;
    private String fomarttime;
    private long createtime;
    private int vislogstatus;//10：正常 80：用户清除
    private int status;//10：正常 80：删除

    public String getParamStr() {
        return JsonConvert.root().convertTo(new TypeToken<HashMap<String, String>>() {
        }.getType(), params);
    }

    public String getHeaderStr() {
        return JsonConvert.root().convertTo(new TypeToken<HashMap<String, String>>() {
        }.getType(), headers);
    }

}
