package com.bykov.project.conference.model;

import com.bykov.project.conference.services.ServiceUser;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class ServiceUserTest {
    private static ServiceUser serviceUser;

    @BeforeClass
    public static void init() {
        serviceUser = new ServiceUser();
    }

    @Test
    public void returnTrueIfUserWithSuchEmailExist() {
        String email = "kratt202@gmail.com";
        assertTrue(serviceUser.isUserExist(email));
    }

    @Test
    public void returnTrueIfUserWithSuchEmailNotExist() {
        String email = "email-not-exist@email.com";
        assertFalse(serviceUser.isUserExist(email));
    }
}
