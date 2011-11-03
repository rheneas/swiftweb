package swiftweb.dsl;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import swiftweb.server.RouteHandlerServlet;
import swiftweb.server.RouteWrapper;
import swiftweb.server.RouteWrapperFactory;
import swiftweb.server.realm.InMemoryRealm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServerDSL {

    public static DSL run(Class... classes) throws Exception {
        DSL dsl = new DSL();
        for (Class clazz : classes) {
            dsl.run(clazz);
        }
        return dsl;
    }

    public static final class DSL {
        private int port = 8080;
        private Server server;
        private Context context;
        private SecurityHandler securityHandler;

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
            if (securityHandler != null) {
                context.setSecurityHandler(securityHandler);
            }
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

        @SuppressWarnings("unchecked")
        private void processConfig(Class clazz) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
            setPort(clazz);
            setSecurity(clazz);
        }

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
                securityAnnotation.password();
                Constraint constraint = new Constraint();
                constraint.setName(Constraint.__BASIC_AUTH);
                String genericRole = "genericRole";
                constraint.setRoles(new String[]{genericRole});
                constraint.setAuthenticate(true);

                ConstraintMapping cm = new ConstraintMapping();
                cm.setConstraint(constraint);
                cm.setPathSpec("/*");

                securityHandler = new SecurityHandler();
                securityHandler.setUserRealm(new InMemoryRealm(genericRole, securityAnnotation.user(), securityAnnotation.password()));
                securityHandler.setConstraintMappings(new ConstraintMapping[]{cm});
            }
        }

        private void processMethods(Class clazz) throws InstantiationException, IllegalAccessException {
            for (Method m : clazz.getMethods()) {
                if (m.isAnnotationPresent(Route.class)) {
                    Route routeAnnotation = m.getAnnotation(Route.class);
                    addRouteToContext(new RouteWrapperFactory().newRouteWrapper(clazz, m, routeAnnotation), context);
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
