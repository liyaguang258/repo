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
