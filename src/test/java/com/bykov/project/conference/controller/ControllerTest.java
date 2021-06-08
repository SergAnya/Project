package com.bykov.project.conference.controller;

import com.bykov.project.conference.services.ServiceUser;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ControllerTest {
    @Test
    public void WhenUnregisteredUserTryToSignInThenForwardToLoginPageWithWrongEmailAttribute() throws ServletException, IOException {
        final Controller servlet = new Controller();
        String email = "emailTest";
        String role = "guest";
        String commandUri = "/guest/login";
        String path = "/WEB-INF/login.jsp";

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        final HttpSession session = mock(HttpSession.class);
        final ServiceUser service = mock(ServiceUser.class);

        when(request.getRequestURI()).thenReturn(commandUri);
        when(request.getRequestDispatcher(path)).thenReturn(dispatcher);
        when(request.getParameter(anyString())).thenReturn(email);
        when(request.getSession()).thenReturn(session);
        when(service.isUserExist(email)).thenReturn(false);
        when(session.getAttribute("role")).thenReturn(role);
        when(session.getAttribute("locale")).thenReturn(new Locale("en", "US"));
        servlet.doPost(request, response);

        verify(request, times(1)).getRequestDispatcher(path);
        verify(request, times(2)).getSession();
        verify(request, times(2)).getParameter(anyString());
        verify(request, times(1)).setAttribute(eq("wrongEmail"), anyString());
        verify(dispatcher).forward(request, response);
    }
}
