package com.hdyl.bttech.utils.bufferknife;

import android.support.annotation.IdRes;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * <p>Created by liugd on 2018/4/4.<p>
 * <p>佛祖保佑，永无BUG<p>
 */
@Retention(RetentionPolicy.RUNTIME) //运行时
@Target(METHOD)
public @interface MyOnClick {
    /**
     * View IDs to which the method will be bound.
     */
    @IdRes int[] value() default {View.NO_ID};
}
