package swiftweb.server;

import org.springframework.web.util.UriTemplate;
import swiftweb.dsl.HttpMethod;
import swiftweb.dsl.Route;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriTemplateRouteWrapper extends RouteWrapper {

    private static final Pattern BASE_PATH_PATTERN = Pattern.compile("[^\\{]*\\{");

    private final UriTemplate uriTemplate;

    public UriTemplateRouteWrapper(Class handlerClass, Method method, Route routeAnnotation) {
        super(handlerClass, method, routeAnnotation);
        this.uriTemplate = new UriTemplate(routeAnnotation.path());
    }

    public int getNoOfParametersInUri() {
        return uriTemplate.getVariableNames().size();
    }

    @Override
    public boolean canHandle(HttpServletRequest req, HttpMethod httpMethod) {
        return super.canHandle(req, httpMethod);
    }

    @Override
    public String getServletPath() {
        Matcher matcher = BASE_PATH_PATTERN.matcher(path);
        if (matcher.find()) {
            return matcher.group().replace("{", "*");
        } else {
            throw new IllegalArgumentException("Could not parse servlet path " + path);
        }
    }

    public String[] getParameters(String requestURI) {
        return uriTemplate.match(requestURI).values().toArray(new String[]{});
    }
}
