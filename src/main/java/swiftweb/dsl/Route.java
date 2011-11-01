package swiftweb.dsl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    HttpMethod method() default HttpMethod.GET;

    String path() default "";

    String contentType() default "";

    int status() default -1;
}
