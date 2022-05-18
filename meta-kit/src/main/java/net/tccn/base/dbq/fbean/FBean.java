package net.tccn.base.dbq.fbean;

import lombok.Data;

import java.util.List;

/**
 * 查询用实体
 * @author: liangxianyou at 2018/10/25 14:49.
 */
@Data
public class FBean {

    private String platToken; // 平台token
    private String  name; // 业务名称
    private String type; // 操作类型 list：列表，export：导出

    private List<Filter> filters;//[{f:xx, v: v, type:t}] -- t,
    private List<Order> orders;//{f1: 1, f2: -1}
    private Limit limit;//{pn: 1, ps: 10}

    //-----------------------------------

    public String getPlatToken() {
        return platToken;
    }

    public void setPlatToken(String platToken) {
        this.platToken = platToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }
}
