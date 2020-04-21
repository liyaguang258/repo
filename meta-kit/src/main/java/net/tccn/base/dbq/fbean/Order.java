package net.tccn.base.dbq.fbean;

import java.util.List;

/**
 * Created by liangxianyou at 2018/12/14 15:36.
 */
public class Order {
    private String col;
    private int desc;//1 or -1

    public Order() {
    }

    public Order(String col, int desc) {
        this.col = col;
        this.desc = desc;
    }
    // --------------------------------

    public static String order(List<Order> orders, DbType dbType) {
        if (orders == null || orders.size() == 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        switch (dbType) {
            case MYSQL:
                buf.append(" ORDER BY");
                orders.forEach(x -> {
                    buf.append(String.format(" %s %s,", x.col, (x.desc == 1 ? "desc" : "asc")));
                });
                buf.deleteCharAt(buf.length() - 1);
                break;

            case ARANGODB:
                // 待实现
                break;
        }

        return buf.toString();
    }
}
