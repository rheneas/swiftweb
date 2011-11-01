package swiftweb.server;

import swiftweb.dsl.HttpMethod;
import swiftweb.dsl.Route;

import java.lang.reflect.Method;

public class RouteWrapper {

    private final Class handlerClass;
    private final Method method;
    private final Route routeAnnotation;
    private final String path;
    private final int status;
    private final String contentType;
    private final HttpMethod httpMethod;

    public RouteWrapper(Class handlerClass, Method method, Route routeAnnotation) {
        this.handlerClass = handlerClass;
        this.method = method;
        this.routeAnnotation = routeAnnotation;
        this.path = routeAnnotation.path();
        this.status = routeAnnotation.status();
        this.contentType = routeAnnotation.contentType();
        this.httpMethod = routeAnnotation.method();
    }

    public String getPath() {
        return (path.length() == 0) ? method.getName() : path;
    }

    public boolean isStatusSet() {
        return status != -1;
    }

    public int getStatus() {
        return status;
    }

    public Class getHandlerClass() {
        return handlerClass;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isContentTypeSet() {
        return contentType.length() != 0;
    }

    public String getContentType() {
        return contentType;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public boolean canHandle(HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }
}
