package com.bykov.project.conference.filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.bykov.project.conference.dao.entity.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;


public class FilterGuest implements Filter {
    private final static Logger LOG =  LogManager.getLogger(FilterGuest.class);
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");
        if (Objects.isNull(role)) {
            session.setAttribute("role", User.Role.GUEST.getStringRole());
            LOG.debug("PATH IN FILTER: " + request.getRequestURI());
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
