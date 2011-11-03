package swiftweb.dsl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Route {
    HttpMethod method() default HttpMethod.GET;

    String path() default "";

    String contentType() default "";

    int status() default -1;
}
