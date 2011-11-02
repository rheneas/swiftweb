package swiftweb.dsl;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import swiftweb.server.RouteHandlerServlet;
import swiftweb.server.RouteWrapper;
import swiftweb.server.RouteWrapperFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServerDSL {

    public static DSL run(Class clazz) throws Exception {
        return new DSL().run(clazz);
    }

    public static final class DSL {
        private int port = 8080;
        private Server server;
        private Context context;

        public DSL() {
            server = new Server(port);
            context = new Context(server, "/", Context.NO_SESSIONS);
        }

        public DSL stop() throws Exception {
            new Thread() {
                @Override
                public void run() {
                    try {
                        server.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            return this;
        }

        public DSL start() throws Exception {
            if (context.getServletHandler().getServletMappings().length == 0) {
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

        private void processMethods(Class clazz) throws InstantiationException, IllegalAccessException {
            for (Method m : clazz.getMethods()) {
                if (m.isAnnotationPresent(Route.class)) {
                    Route routeAnnotation = m.getAnnotation(Route.class);
                    addRouteToContext(new RouteWrapperFactory().newRouteWrapper(clazz, m, routeAnnotation), context);
                } else {
                }
            }
        }

        private void addRouteToContext(RouteWrapper routeWrapper, Context context) throws InstantiationException, IllegalAccessException {
            String path = routeWrapper.getServletPath();
            path = path.startsWith("/") ? path : "/" + path;
            context.addServlet(new ServletHolder(new RouteHandlerServlet(routeWrapper)), path);
        }

    }
}
