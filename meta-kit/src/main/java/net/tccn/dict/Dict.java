package net.tccn.dict;

import lombok.Data;
import net.tccn.base.arango.Doc;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author: liangxianyou
 */
@Data
@Table(name = "Dict", catalog = "db_demo")
public class Dict extends Doc<Dict> implements Serializable {
    public static Dict dao = Doc.dao(Dict.class);

    private String type;        // 字典类型
    private String value;       // 字典值
    private String label;       // 中文名
    private String pValue;      // 父级字典值
    private String code;        //
    private String sysPlatId;   // 系统平台id
}
