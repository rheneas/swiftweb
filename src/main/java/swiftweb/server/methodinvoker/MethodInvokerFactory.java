package swiftweb.server.methodinvoker;

import swiftweb.server.RouteWrapper;
import swiftweb.server.UriTemplateRouteWrapper;

public class MethodInvokerFactory {

    public MethodInvokerInterface newMethodInvoker(RouteWrapper routeWrapper) {
        if (routeWrapper instanceof UriTemplateRouteWrapper) {
            return new UriTemplateMethodInvoker((UriTemplateRouteWrapper) routeWrapper);
        } else {
            return new MethodInvoker();
        }
    }
}
