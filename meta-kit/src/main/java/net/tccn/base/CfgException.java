package net.tccn.base;

import org.redkale.net.http.HttpRequest;

/**
 * 自定义异常处理
 * @author: liangxianyou
 */
public class CfgException extends IllegalArgumentException {

    public CfgException(String msg) {
        super(msg);
    }

    public CfgException(String msgTpl, Object ... objects) {
        super(String.format(msgTpl, objects));
    }

    public void log(HttpRequest request) {
        System.out.printf("CfgException: {uri: %s,%n Parameters:%s,%n body: %s} %n", request.getRequestURI(), request.getParameters().toString(), request.getBody());
    }
}
