package swiftweb.server.methodinvoker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface MethodInvokerInterface {
    Object invokeMethod(Method method, Object instance, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException;
}
