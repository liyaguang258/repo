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
