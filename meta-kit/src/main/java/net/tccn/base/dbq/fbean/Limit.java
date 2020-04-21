package net.tccn.base.dbq.fbean;

import lombok.Data;

/**
 * Created by liangxianyou at 2018/12/14 15:36.
 */
@Data
public class Limit {
    private int pn;
    private int ps;

    public Limit() {
    }

    public Limit(int pn, int ps) {
        this.pn = pn;
        this.ps = ps;
    }

    //--------------------
    public String limit() {
        if (pn < 1) {
            pn = 1;
        }
        if (ps < 1) {
            ps = 10;
        }
        return String.format(" LIMIT %s, %s", (pn - 1) * ps, ps);
    }
}

