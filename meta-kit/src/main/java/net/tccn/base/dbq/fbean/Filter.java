package net.tccn.base.dbq.fbean;

import lombok.Data;

import java.util.List;

/**
 * 查询条件实体
 * Created by liangxianyou at 2018/12/14 15:34.
 */
@Data
public class Filter {
    private String col;

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String value;
    private String[] values;
    private String type;
    //----------------------

    public static Filter by(String col, Object value) {
        return by(col, "==", value + "");//todo: == 不是mysql 语法，不具备通用性
    }

    public static Filter by(String col, String type, String value) {
        Filter filter = new Filter();
        filter.setCol(col);
        filter.setType(type);
        filter.setValue(value);
        return filter;
    }

    //mysql 查询组装
    public static String filter(List<Filter> filters) {
        if (filters == null || filters.size() == 0) {
            return "";
        }

        StringBuilder buf = new StringBuilder();
        buf.append(" where 1=1");
        filters.forEach(x -> {
            buf.append(FilterType.buildSql(x));
        });
        return buf.toString();
    }

    public static String filter(List<Filter> filters, DbType dbType) {
        if (DbType.MYSQL == dbType) {
            return filter(filters);
        } else if (DbType.ARANGODB == dbType) {
            StringBuilder buf = new StringBuilder();
            buf.append(" filter 1==1");
            if (filters == null || filters.size() == 0) {
                return "";
            }

            filters.forEach(x -> {
                buf.append(" and d.").append(x.col).append(" " + (x.type == null ? "==" : x.type) + " ");
                //处理数值型字段查询
                if ("sysPlatId".equals(x.col) || "platId".equals(x.col) || "status".equals(x.col) || false) {
                    buf.append(x.value);
                } else {
                    buf.append("'" + x.value + "'");
                }
            });
            return buf.toString();
        }

        return "";
    }
}
