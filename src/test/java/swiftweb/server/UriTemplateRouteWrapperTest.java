package swiftweb.server;

import org.junit.Test;
import swiftweb.dsl.Route;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class UriTemplateRouteWrapperTest {

    private class AnnotatedClass {
        @Route(path = "/test/{:1}/more/{:2}")
        public void twoParameters() {
        }

        @Route(path = "/test/{:1}")
        public void oneParameter() {
        }

        @Route(path = "test/{:1}")
        public void noLeadingSlash() {
        }
    }

    @Test
    public void shouldReturnPathWithTwoParameters() throws NoSuchMethodException {
        assertEquals("/test/*", new UriTemplateRouteWrapper(null, null, getAnnotationFromMethod("twoParameters")).getServletPath());
    }

    @Test
    public void shouldReturnPathWithOneParameter() throws NoSuchMethodException {
        assertEquals("/test/*", new UriTemplateRouteWrapper(null, null, getAnnotationFromMethod("oneParameter")).getServletPath());
    }

    @Test
    public void shouldReturnPathWithoutALeadingSlash() throws NoSuchMethodException {
        assertEquals("test/*", new UriTemplateRouteWrapper(null, null, getAnnotationFromMethod("noLeadingSlash")).getServletPath());
    }

    private Route getAnnotationFromMethod(String methodName) throws NoSuchMethodException {
        Method method = AnnotatedClass.class.getMethod(methodName);
        return method.getAnnotation(Route.class);
    }
}
