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

    public static MetaService getDao() {
        return dao;
    }

    public static void setDao(MetaService dao) {
        MetaService.dao = dao;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSysPlatId() {
        return sysPlatId;
    }

    public void setSysPlatId(String sysPlatId) {
        this.sysPlatId = sysPlatId;
    }

    public List<Map<String, String>> getShows() {
        return shows;
    }

    public void setShows(List<Map<String, String>> shows) {
        this.shows = shows;
    }

    public List<FromItem> getEdits() {
        return edits;
    }

    public void setEdits(List<FromItem> edits) {
        this.edits = edits;
    }

    public List<Map<String, String>> getDetails() {
        return details;
    }

    public void setDetails(List<Map<String, String>> details) {
        this.details = details;
    }

    public Map<String, String> getDels() {
        return dels;
    }

    public void setDels(Map<String, String> dels) {
        this.dels = dels;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public List<Map<String, String>> getExports() {
        return exports;
    }

    public void setExports(List<Map<String, String>> exports) {
        this.exports = exports;
    }
}
