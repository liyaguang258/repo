package net.tccn.base.dbq.table;

import lombok.Data;

/**
 * 数据库表的列
 * @author: liangxianyou at 2018/10/8 10:59.
 */
@Data
public class Column {
    private String field; //列名称
    private String type; //列类型
    private boolean notNull; //不为null

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private String comment; //列说明

    public Column() {
    }

    public Column(String name, String type, boolean notNull, String comment) {
        this.field = name;
        this.type = type;
        this.notNull = notNull;
        this.comment = comment;
    }

    //-----------------------
    public void setNull(String notNull) {
        this.notNull = "NO".equalsIgnoreCase(notNull) ? true : false;
    }
}
