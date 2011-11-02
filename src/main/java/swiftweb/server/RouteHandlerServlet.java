package swiftweb.server;

import swiftweb.dsl.HttpMethod;
import swiftweb.server.methodinvoker.MethodInvokerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class RouteHandlerServlet extends HttpServlet {

    private final RouteWrapper routeWrapper;
    private final Object instance;

    public RouteHandlerServlet(RouteWrapper routeWrapper) throws IllegalAccessException, InstantiationException {
        this.routeWrapper = routeWrapper;
        this.instance = routeWrapper.getHandlerClass().newInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(HttpMethod.GET, req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(HttpMethod.PUT, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(HttpMethod.POST, req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(HttpMethod.DELETE, req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(HttpMethod.OPTIONS, req, resp);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(HttpMethod.TRACE, req, resp);
    }

    private void handle(HttpMethod method, HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        if (!routeWrapper.canHandle(req, method)) throw new ServletException("Can not handle request");

        try {
            Object invokedResponse = new MethodInvokerFactory().newMethodInvoker(routeWrapper).invokeMethod(routeWrapper.getMethod(), instance, req, resp);
            if (invokedResponse instanceof String) {
                handleStringResponse((String) invokedResponse, resp);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void handleStringResponse(String stringResponse, HttpServletResponse resp) throws IllegalAccessException, InvocationTargetException, IOException {
        if (routeWrapper.isStatusSet()) {
            resp.setStatus(routeWrapper.getStatus());
        }

        if (routeWrapper.isContentTypeSet()) {
            resp.setContentType(routeWrapper.getContentType());
        }

        resp.getWriter().write(stringResponse);
    }
}
