package swiftweb.server;

import swiftweb.dsl.HttpMethod;
import swiftweb.dsl.Route;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class RouteWrapper {

    protected final Class handlerClass;
    protected final Method method;
    protected final Route routeAnnotation;
    protected final String path;
    protected final int status;
    protected final String contentType;
    protected final HttpMethod httpMethod;

    public RouteWrapper(Class handlerClass, Method method, Route routeAnnotation) {
        this.handlerClass = handlerClass;
        this.method = method;
        this.routeAnnotation = routeAnnotation;
        this.path = routeAnnotation.path();
        this.status = routeAnnotation.status();
        this.contentType = routeAnnotation.contentType();
        this.httpMethod = routeAnnotation.method();
    }

    public String getServletPath() {
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

    public boolean canHandle(HttpServletRequest req, HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }

    public int getNoOfParametersInUri() {
        return 0;
    }
}
