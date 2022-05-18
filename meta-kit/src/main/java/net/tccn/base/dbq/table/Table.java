package net.tccn.base.dbq.table;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库表.
 * @author: liangxianyou at 2018/10/8 10:58.
 */
@Data
public class Table {
    private String catalog; //库名称
    private String name; //表名称
    private String comment; //表备注
    private List<Column> columns = new ArrayList<>(); //表的字段列

    public Table() {}

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public Table(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }

    //------------------------------

    //Dev
    public String _getTableDdl() {
        StringBuilder buf = new StringBuilder();

        buf.append("CREATE TABLE " + name + "(");
        columns.forEach(x -> {
            buf.append("\n " + x.getField() + " " + x.getType() + ",");
        });

        buf.deleteCharAt(buf.length() - 1);
        buf.append("\n) COMMENT '" + comment + "';");
        return buf.toString();
    }
    //----------

}
