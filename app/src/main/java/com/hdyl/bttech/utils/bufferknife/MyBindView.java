package com.hdyl.bttech.utils.bufferknife;

import android.support.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Created by liugd on 2018/4/4.<p>
 * <p>佛祖保佑，永无BUG<p>
 */
@Retention(RetentionPolicy.RUNTIME) //运行时
@Target(ElementType.FIELD)//注解使用情景用于字段
public @interface MyBindView {

    /**
     * View ID to which the field will be bound.
     */
    @IdRes int value();


    @Deprecated
    boolean click() default false;
}