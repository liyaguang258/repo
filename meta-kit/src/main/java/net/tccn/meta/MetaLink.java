package net.tccn.meta;

import lombok.Data;
import net.tccn.base.arango.Doc;

import javax.persistence.Table;
import java.util.Map;

/**
 * Created by liangxianyou at 2018/12/25 16:22.
 */
@Data
@Table(name = "MetaLink", catalog = "db_demo")
public class MetaLink extends Doc<MetaLink> {
    public static MetaLink dao = Doc.dao(MetaLink.class);

    private String[] tables;

    public static MetaLink getDao() {
        return dao;
    }

    public static void setDao(MetaLink dao) {
        MetaLink.dao = dao;
    }

    public String[] getTables() {
        return tables;
    }

    public void setTables(String[] tables) {
        this.tables = tables;
    }

    public Map<String, String> getLink() {
        return link;
    }

    public void setLink(Map<String, String> link) {
        this.link = link;
    }

    private Map<String, String> link;
}
