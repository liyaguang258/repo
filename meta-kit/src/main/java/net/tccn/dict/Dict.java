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

    public static Dict getDao() {
        return dao;
    }

    public static void setDao(Dict dao) {
        Dict.dao = dao;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getpValue() {
        return pValue;
    }

    public void setpValue(String pValue) {
        this.pValue = pValue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSysPlatId() {
        return sysPlatId;
    }

    public void setSysPlatId(String sysPlatId) {
        this.sysPlatId = sysPlatId;
    }

    private String label;       // 中文名
    private String pValue;      // 父级字典值
    private String code;        //
    private String sysPlatId;   // 系统平台id
}
