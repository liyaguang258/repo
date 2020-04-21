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

}
