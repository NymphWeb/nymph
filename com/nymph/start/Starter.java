package com.nymph.start;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * main方法启动应用时 启动类必须标注此注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Starter {

    /**
     * 配置文件的路径
     * @return
     */
    String[] configurations() default {};

    /**
     * 包扫描的路径
     * @return
     */
    String[] packageScanner() default {};
}
