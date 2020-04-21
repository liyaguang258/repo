package net.tccn.base;

import java.util.List;

/**
 * Created by JUECHENG at 2018/5/7 11:20.
 */
public class PageBean<M> {
    private List<M> rows;
    private long total;

    public static PageBean by(List rows, long total) {
        return new PageBean(rows, total);
    }

    public PageBean() {
    }

    public PageBean(List<M> rows, long total) {
        this.rows = rows;
        this.total = total;
    }

    public List<M> getRows() {
        return rows;
    }

    public void setRows(List<M> rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
