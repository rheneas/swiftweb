package swiftweb.server.methodinvoker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MethodInvoker implements MethodInvokerInterface {

    private interface Invoker {
        Object invoke(Method method, Object instance, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException;

        boolean handlesMethod(Method method);
    }

    /**
     * Handles a method looking like
     *
     * public void doSomething();
     *
     */
    private class NoArgsInvoker implements Invoker {
        public Object invoke(Method method, Object instance, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
            return method.invoke(instance);
        }

        public boolean handlesMethod(Method method) {
            return method.getParameterTypes().length == 0 && method.getReturnType().isAssignableFrom(String.class);
        }
    }

    private class ResponseParamInvoker implements Invoker {
        public Object invoke(Method method, Object instance, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
            return method.invoke(instance, response);
        }

        public boolean handlesMethod(Method method) {
            return method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(HttpServletResponse.class);
        }
    }

    private class RequestParamInvoker implements Invoker {
        public Object invoke(Method method, Object instance, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
            return method.invoke(instance, request);
        }

        public boolean handlesMethod(Method method) {
            return method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(HttpServletRequest.class);
        }
    }

    private class RequestAndResponseParamsInvoker implements Invoker {
        public Object invoke(Method method, Object instance, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
            return method.invoke(instance, request, response);
        }

        public boolean handlesMethod(Method method) {
            return method.getParameterTypes().length == 2 && method.getParameterTypes()[0].isAssignableFrom(HttpServletRequest.class)
                    && method.getParameterTypes()[1].isAssignableFrom(HttpServletResponse.class);
        }
    }

    private List<Invoker> invokers = Arrays.asList(new NoArgsInvoker(), new ResponseParamInvoker(), new RequestParamInvoker(), new RequestAndResponseParamsInvoker());

    private Invoker getInvoker(Method method) {
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
