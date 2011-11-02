package swiftweb.server;

import swiftweb.dsl.Route;

import java.lang.reflect.Method;

public class RouteWrapperFactory {

    public RouteWrapper newRouteWrapper(Class handlerClass, Method method, Route routeAnnotation) {
        if (isUriTemplate(routeAnnotation.path())) {
            return new UriTemplateRouteWrapper(handlerClass, method, routeAnnotation);
        } else {
            return new RouteWrapper(handlerClass, method, routeAnnotation);
        }
    }

    private boolean isUriTemplate(String uri) {
        // TODO need a better way of doing this
        return uri.contains("{:");
    }
}
