package net.tccn.base.dbq.fbean;

import net.tccn.base.Kv;
import net.tccn.base.X;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liangxianyou at 2018/12/14 15:34.
 */
public enum FilterType {
    EQUAL("=", "等于"),
    NOTEQUAL("!=", "不等于"),
    GREATERTHANOREQUALTO(">=", ">="),
    LESSTHAN("<", "小于"),
    LIKE("LIKE", "LIKE"),
    IN("IN", "包含"),
    RANGE("RANGE", "区间"),
    SQL("SQL", "SQL") //直接使用sql查询
    ;

    private String expre;
    private String remark;

    FilterType(String expre, String remark) {
        this.expre = expre;
        this.remark = remark;
    }

    //不同的条件构建过滤语句
    public static String buildSql(Filter filter) {
        if (X.isEmpty(filter.getValue()) && X.isEmpty(filter.getValues())) {
            return "";
        }
        FilterType filterType = getFilterType(filter.getType());
        if (filterType == null) {
            return "";
        }

        String _sql = "";
        switch (filterType) {
            case IN:
                _sql = String.format(" AND %s IN (%s)", filter.getCol(), filter.getValue());
                break;
            case LIKE:
                _sql = String.format(" AND %s LIKE '%s'", filter.getCol(), "%" + filter.getValue() + "%");
                break;
            case RANGE:
                if (filter.getValues() != null) {
                    if (filter.getValues().length == 1) {
                        _sql = String.format(" AND %s>='%s'", filter.getCol(), filter.getValues()[0]);
                    } else if (filter.getValues().length == 2) {
                        _sql = String.format(" AND (%s>='%s' and %s<'%s')", filter.getCol(), filter.getValues()[0], filter.getCol(), filter.getValues()[1]);
                    }
                }
                break;
            case SQL:
                _sql = String.format(" AND (%s)", filter.getValue());
                break;
            default:
                _sql = String.format(" AND %s %s '%s'", filter.getCol(), filterType.expre, filter.getValue());
        }

        return _sql;
    }

    public static FilterType getFilterType(String name) {
        for (FilterType t : FilterType.values()) {
            if (t.name().equalsIgnoreCase(name)) {
                return t;
            }
        }

        return null;
    }

    //获取所有的查询类型
    public static List<Map> getAllTypes() {
        List<Map> list = new ArrayList<>();
        for (FilterType type : FilterType.values()) {
            Kv kv = Kv.of("name", type.name())/*.set("expre", type.expre)*/.set("remark", type.remark);
            list.add(kv);
        }

        return list;
    }

    public String getExpre() {
        return expre;
    }

    public String getRemark() {
        return remark;
    }}
