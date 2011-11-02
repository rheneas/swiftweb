package swiftweb.server.methodinvoker;

import swiftweb.server.UriTemplateRouteWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UriTemplateMethodInvoker implements MethodInvokerInterface {

    private final UriTemplateRouteWrapper routeWrapper;

    private interface Invoker {
        Object invoke(Method method, Object instance, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException;

        boolean handlesMethod(Method method);
    }

    private class NoArgsInvoker implements Invoker {
        public Object invoke(Method method, Object instance, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
            String[] parameters = routeWrapper.getParameters(request.getRequestURI());
            return method.invoke(instance, (Object[]) parameters);
        }

        public boolean handlesMethod(Method method) {
            return method.getParameterTypes().length == routeWrapper.getNoOfParametersInUri() && method.getReturnType().isAssignableFrom(String.class);
        }
    }

    private class ResponseParamInvoker implements Invoker {
        public Object invoke(Method method, Object instance, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
            List<Object> parametersAsArray = new ArrayList<Object>();
            Collections.addAll(parametersAsArray, routeWrapper.getParameters(request.getRequestURI()));
            parametersAsArray.add(response);

            return method.invoke(instance, parametersAsArray.toArray());
        }

        public boolean handlesMethod(Method method) {
            int expectedNoOfParameters = routeWrapper.getNoOfParametersInUri() + 1;
            return method.getParameterTypes().length == expectedNoOfParameters && method.getParameterTypes()[expectedNoOfParameters - 1].isAssignableFrom(HttpServletResponse.class);
        }
    }

    private class RequestParamInvoker implements Invoker {
        public Object invoke(Method method, Object instance, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
            List<Object> parametersAsArray = new ArrayList<Object>();
            Collections.addAll(parametersAsArray, routeWrapper.getParameters(request.getRequestURI()));
            parametersAsArray.add(request);

            return method.invoke(instance, parametersAsArray.toArray());
        }

        public boolean handlesMethod(Method method) {
            int expectedNoOfParameters = routeWrapper.getNoOfParametersInUri() + 1;
            return method.getParameterTypes().length == expectedNoOfParameters && method.getParameterTypes()[expectedNoOfParameters - 1].isAssignableFrom(HttpServletRequest.class);
        }
    }

    private class RequestAndResponseParamsInvoker implements Invoker {
        public Object invoke(Method method, Object instance, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
            List<Object> parametersAsArray = new ArrayList<Object>();
            Collections.addAll(parametersAsArray, routeWrapper.getParameters(request.getRequestURI()));
            parametersAsArray.add(request);
            parametersAsArray.add(response);

            return method.invoke(instance, parametersAsArray.toArray());
        }

        public boolean handlesMethod(Method method) {
            int expectedNoOfParameters = routeWrapper.getNoOfParametersInUri() + 2;

            return method.getParameterTypes().length == expectedNoOfParameters && method.getParameterTypes()[expectedNoOfParameters - 2].isAssignableFrom(HttpServletRequest.class)
                    && method.getParameterTypes()[expectedNoOfParameters - 1].isAssignableFrom(HttpServletResponse.class);
        }
    }

    private final List<Invoker> invokers = Arrays.asList(new NoArgsInvoker(), new ResponseParamInvoker(), new RequestParamInvoker(), new RequestAndResponseParamsInvoker());

    public UriTemplateMethodInvoker(UriTemplateRouteWrapper routeWrapper) {
        this.routeWrapper = routeWrapper;
    }

    public Invoker getInvoker(Method method) {
        for (Invoker invoker : invokers) {
            if (invoker.handlesMethod(method)) {
                return invoker;
            }
        }
        return null;
    }

    public Object invokeMethod(Method method, Object instance, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
        Invoker invoker = getInvoker(method);
        if (invoker == null) {
            throw new IllegalStateException("Could not find a matching response/request arg combination");
        }

        return invoker.invoke(method, instance, request, response);
    }
}
