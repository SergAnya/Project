package com.bykov.project.conference.filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.bykov.project.conference.utils.UtilsSecurity;
import com.bykov.project.conference.exceptions.AccessDeniedException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class FilterAuthorization implements Filter {
    private final static Logger LOG = LogManager.getLogger(FilterAuthorization.class);

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpSession session = request.getSession();
        String uri = request.getRequestURI();
        String userRole = (String) session.getAttribute("role");

        if (UtilsSecurity.isSecuredPage(uri)) {
            boolean hasPermission = UtilsSecurity.checkPermission(uri, userRole);
            if (!hasPermission) {
                String email = (String) request.getSession().getAttribute("email");
                LOG.warn("Attempt of unauthorized access was caught , email: " + (email == null ? "user unknown" : email));
                throw new AccessDeniedException();
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}