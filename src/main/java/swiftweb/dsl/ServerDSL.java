package swiftweb.dsl;

import org.eclipse.jetty.http.security.Constraint;
import org.eclipse.jetty.http.security.Password;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import swiftweb.server.RouteHandlerServlet;
import swiftweb.server.RouteWrapper;
import swiftweb.server.RouteWrapperFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServerDSL {

    public static final int DEFAULT_PORT = 8080;

    public static DSL run(Class... classes) throws Exception {
        return runClasses(DEFAULT_PORT, classes);
    }

    public static DSL runInHeroku(Class... classes) throws Exception {
        return runClasses(Integer.valueOf(System.getenv("PORT")), classes);
    }

    private static DSL runClasses(int port, Class... classes) throws Exception {
        DSL dsl = new DSL(port);
        for (Class clazz : classes) {
            dsl.run(clazz);
        }
        return dsl;
    }

    public static final class DSL {
        private int port;
        private Server server;
        private ServletContextHandler servletContextHandler;

        public DSL(int port) {
            server = new Server(port);
            servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
            server.setHandler(servletContextHandler);
            this.port = port;
        }

        public DSL stop() throws Exception {
            server.stop();
            return this;
        }

        public DSL start() throws Exception {
            if (servletContextHandler.getServletHandler().getServletMappings().length == 0) {
                throw new IllegalStateException("No registered classes, not starting the server");
            }

            Connector connector = getFirstConnector();
            connector.setPort(port);
            server.start();
            return this;
        }

        private Connector getFirstConnector() {
            return server.getConnectors()[0];
        }

        public DSL run(Class clazz) throws Exception {
            processMethods(clazz);
            processConfig(clazz);
            return start();
        }

        private void processConfig(Class clazz) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
            setPort(clazz);
            setSecurity(clazz);
        }

        @SuppressWarnings("unchecked")
        private void setPort(Class clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
            Method portMethod;
            try {
                portMethod = clazz.getMethod("port");
            } catch (NoSuchMethodException e) {
                portMethod = null;
            }
            Object instance = clazz.newInstance();
            if (portMethod != null) {
                this.port = (Integer) portMethod.invoke(instance);
            }
        }

        private void setSecurity(Class clazz) {
            Security securityAnnotation = (Security) clazz.getAnnotation(Security.class);
            if (securityAnnotation != null) {
                Constraint constraint = new Constraint();
                constraint.setName(Constraint.__BASIC_AUTH);
                String genericRole = "genericRole";
                constraint.setRoles(new String[]{genericRole});
                constraint.setAuthenticate(true);

                ConstraintMapping cm = new ConstraintMapping();
                cm.setConstraint(constraint);
                cm.setPathSpec("/*");

                ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
                securityHandler.setConstraintMappings(new ConstraintMapping[]{cm});
                HashLoginService loginService = new HashLoginService("role");
                loginService.putUser(securityAnnotation.user(), new Password(securityAnnotation.password()), new String[]{genericRole});
                securityHandler.setLoginService(loginService);

                securityHandler.setHandler(servletContextHandler);
                server.setHandler(securityHandler);
            }
        }

        private void processMethods(Class clazz) throws InstantiationException, IllegalAccessException {
            for (Method m : clazz.getMethods()) {
                if (m.isAnnotationPresent(Route.class)) {
                    Route routeAnnotation = m.getAnnotation(Route.class);
                    addRouteToContext(new RouteWrapperFactory().newRouteWrapper(clazz, m, routeAnnotation), servletContextHandler);
                }
            }
        }

        private void addRouteToContext(RouteWrapper routeWrapper, ServletContextHandler context) throws InstantiationException, IllegalAccessException {
            String path = routeWrapper.getServletPath();
            path = path.startsWith("/") ? path : "/" + path;
            context.addServlet(new ServletHolder(new RouteHandlerServlet(routeWrapper)), path);
        }

    }
}
