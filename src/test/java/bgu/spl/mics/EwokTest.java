package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class EwokTest {

    private Ewok ewok;

    @BeforeEach
    public void setUp(){
        ewok = new Ewok(0);
    }

    @Test
    public void testDefaultAvailable(){
        assertTrue(ewok.getAvailable());
    }

    @Test
    public void testAcquire(){
        assertTrue(ewok.getAvailable());
        ewok.acquire();
        assertFalse(ewok.getAvailable());
    }

    @Test
    public void testRelease(){
        assertTrue(ewok.getAvailable());
        ewok.acquire();
        assertFalse(ewok.getAvailable());
        ewok.release();
        assertTrue(ewok.getAvailable());
    }
}
