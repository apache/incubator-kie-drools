package ${package};

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName = "CorsFilter", urlPatterns = "/*", asyncSupported = true)
public class CorsFilter implements Filter {

    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    private static final String ORIGIN = "Origin";

    private static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";

    private static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    private static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String origin = request.getHeader(ORIGIN);
        if (origin == null) {
            chain.doFilter(servletRequest, servletResponse);
        } else {
            String requestMethods = request.getHeader(ACCESS_CONTROL_REQUEST_METHOD);
            if (requestMethods != null) {
                response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, requestMethods);
            }
            String allowHeaders = request.getHeader(ACCESS_CONTROL_REQUEST_HEADERS);
            if (allowHeaders != null) {
                response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders);
            }
            response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            response.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition");
            if (!"OPTIONS".equalsIgnoreCase(request.getMethod())) {
                chain.doFilter(servletRequest, servletResponse);
            } else {
                response.flushBuffer();
            }
        }
    }

    @Override
    public void destroy() {

    }
}
