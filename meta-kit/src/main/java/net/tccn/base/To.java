package net.tccn.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by liangxianyou at 2019/3/26 20:35.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface To {
    String value();
}
