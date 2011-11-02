package swiftweb.server;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import swiftweb.dsl.HttpMethod;
import swiftweb.dsl.Route;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void shouldCanHandle() throws NoSuchMethodException {
        UriTemplateRouteWrapper uriTemplateRouteWrapper = new UriTemplateRouteWrapper(null, null, getAnnotationFromMethod("twoParameters"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test/1000/more/2000");

        assertTrue(uriTemplateRouteWrapper.canHandle(request, HttpMethod.GET));
    }
    
    private Route getAnnotationFromMethod(String methodName) throws NoSuchMethodException {
        Method method = AnnotatedClass.class.getMethod(methodName);
        return method.getAnnotation(Route.class);
    }
}
