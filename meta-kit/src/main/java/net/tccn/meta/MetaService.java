package net.tccn.meta;

import lombok.Data;
import net.tccn.base.arango.Doc;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangxianyou at 2018/12/24 16:15.
 */
@Data
@Table(name = "MetaService", catalog = "db_demo")
public class MetaService extends Doc<MetaService> {
    public static MetaService dao = Doc.dao(MetaService.class);

    private String name; //业务标识
    private String table; //主体表别名
    private String comment; //业务中文名
    private String sysPlatId; //平台id

    private List<Map<String,String>> shows = new ArrayList<>();
    private List<FromItem> edits = new ArrayList<>();
    private List<Map<String,String>> details = new ArrayList<>();
    private Map<String, String> dels = new HashMap<>();
    private List<Filter> filters = new ArrayList<>();//{name:"", label:"", checked:true, }
    private List<Map<String,String>> exports = new ArrayList<>();

    //------

}
