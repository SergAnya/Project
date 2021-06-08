package com.bykov.project.conference.filters;

import com.bykov.project.conference.exceptions.AccessDeniedException;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilterAuthorizationTest {

    @Test(expected = AccessDeniedException.class)
    public void AccessDeniedExceptionThrownOnDifferentSessionRoleAndUriRole() throws IOException, ServletException {
        final FilterAuthorization filter = new FilterAuthorization();
        final FilterChain filterChain = mock(FilterChain.class);

        String role = "guest";
        String commandUri = "/admin/createconference";


        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final HttpSession session = mock(HttpSession.class);

        when(request.getRequestURI()).thenReturn(commandUri);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("role")).thenReturn(role);

        filter.doFilter(request, response, filterChain);
    }

    @Test(expected = AccessDeniedException.class)
    public void AccessDeniedExceptionThrownWhenUserTryToDeleteConference() throws IOException, ServletException {
        final FilterAuthorization filter = new FilterAuthorization();
        final FilterChain filterChain = mock(FilterChain.class);
        String role = "user";
        String commandUri = "/admin/deleteconference";
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final HttpSession session = mock(HttpSession.class);

        when(request.getRequestURI()).thenReturn(commandUri);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("role")).thenReturn(role);

        filter.doFilter(request, response, filterChain);


    }


}
