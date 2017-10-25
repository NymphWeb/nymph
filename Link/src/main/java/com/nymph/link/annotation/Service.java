package com.nymph.link.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;
/**
 *  为了简单,使用spring 的容器
 * @author Nymph
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Service {
	
	/**服务接口类型*/
	Class<?> value();
	
	/**服务版本号*/
	String version() default "";
}
