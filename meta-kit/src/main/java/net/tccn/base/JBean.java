package net.tccn.base;

import org.redkale.convert.json.JsonConvert;

/**
 * Created by liangxianyou at 2018/7/30 16:51.
 */
public class JBean {
    private int code;
    private String message;
    private Object body;
    private long timestamp;

    public final  static JBean OK = by(0, "操作成功");

    public static JBean by(int code, String message){
        JBean jBean = new JBean();
        jBean.code = code;
        jBean.message = message;
        jBean.timestamp = System.currentTimeMillis();
        return jBean;
    }
    public static JBean by(int code, String message, Object result){
        JBean jBean = new JBean();
        jBean.code = code;
        jBean.message = message;
        jBean.body = result;
        jBean.timestamp = System.currentTimeMillis();
        return jBean;
    }
    public JBean set(int code, String message){
        this.code = code;
        this.message = message;
        return this;
    }

    public JBean set(int code, String message, Object result){
        this.code = code;
        this.message = message;
        this.body = result;
        this.timestamp = System.currentTimeMillis();
        return this;
    }

    public JBean setCode(int code) {
        this.code = code;
        return this;
    }

    public JBean setMessage(String message) {
        this.message = message;
        return this;
    }

    public JBean setBody(Object body) {
        this.body = body;
        return this;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return JsonConvert.root().convertTo(this);
    }
}
