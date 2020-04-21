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
    private Map<String, String> link;
}
