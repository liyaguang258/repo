package net.tccn.base.dbq.table;

import lombok.Data;

/**
 * @author: liangxianyou at 2018/10/17 17:24.
 */
@Data
public class Field {
    private String name;
    private String label;
    private String remark;
    private String type;
    private String inType;
    private String inExt;
    private Boolean pk; // 是否主键
    private Boolean notNull; // 是否主键

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInType() {
        return inType;
    }

    public void setInType(String inType) {
        this.inType = inType;
    }

    public String getInExt() {
        return inExt;
    }

    public void setInExt(String inExt) {
        this.inExt = inExt;
    }

    public Boolean getPk() {
        return pk;
    }

    public void setPk(Boolean pk) {
        this.pk = pk;
    }

    public Boolean getNotNull() {
        return notNull;
    }

    public void setNotNull(Boolean notNull) {
        this.notNull = notNull;
    }

    public Field() {}

    //------------------------------------
    public enum InType {
        SELECT_EXT("SELECT_EXT"),
        INPUT_DT("INPUT_DT"),
        FMT_FUN("FMT_FUN");

        String name;

        InType(String name) {
            this.name = name;
        }
    }

    public enum QueryType {

    }

    public String showField() {
        if (InType.SELECT_EXT.name.equalsIgnoreCase(inType)) {
            return name + "|" + inExt;
        } else if (InType.INPUT_DT.name.equalsIgnoreCase(inType)) {
            return name + "=dt";
        } else if (InType.FMT_FUN.name.equalsIgnoreCase(inType)) {
            return name + "=" + inExt;
        }

        return name;
    }

    public boolean isDict() {
        return InType.SELECT_EXT.name.equalsIgnoreCase(inType);
    }

    public static Field toAs(Column column) {
        Field _bean = new Field();
        _bean.setName(column.getField());
        _bean.setType(column.getType());
        _bean.setLabel(column.getComment());

        return _bean;
    }

    @Override
    public boolean equals(Object name) {
        return (this.name == null && name == null) || this.name.equals(name);
    }
}
