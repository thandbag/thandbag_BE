package com.example.thandbag.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProfileControllerUnitTest {

    @DisplayName("RealProfile 조회")
    @Test
    public void getRealProfile() {
        /* given */
        MockEnvironment env = new MockEnvironment();
        env.addActiveProfile("real");
        env.addActiveProfile("oauth");
        env.addActiveProfile("real-db");

        ProfileController controller = new ProfileController(env);

        /* when */
        String profile = controller.profile();

        /* then */
        assertEquals("real", profile);
    }

    @DisplayName("RealProfile 조회안됨")
    @Test
    public void getRealProfileFail() {
        /* given */
        MockEnvironment env = new MockEnvironment();

        env.addActiveProfile("oauth");

        ProfileController controller = new ProfileController(env);

        /* when */
        String profile = controller.profile();

        /* then */
        assertEquals("oauth", profile);
    }

    @DisplayName("ActiveProfile 없음")
    @Test
    public void noActiveProfile() {
        /* given */
        MockEnvironment env = new MockEnvironment();
        ProfileController controller = new ProfileController(env);

        /* when */
        String profile = controller.profile();

        /* then */
        assertEquals("default", profile);
    }
}