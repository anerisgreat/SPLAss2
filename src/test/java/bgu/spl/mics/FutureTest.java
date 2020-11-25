package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {

    private Future<String> future;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
    }

    @Test
    public void isDone(){
        assertFalse(future.isDone());
        future.resolve("test");
        assertTrue(future.isDone());
    }

    @Test
    public void testGet(){

        string str = "abcdefghijlmnopqrstuvwxyz";
        future.resolve(str);
        assertTrue(str.compareTo(future.get()) == 0);
    }

    @Test
    public void testResolve(){
        String str = "someResult";
        future.resolve(str);
        assertTrue(future.isDone());
        assertTrue(str.equals(future.get()));
    }

    @Test
    public void testGetWithTimeout(){
        String str = "teststr";
        new Thread(() -> {
                assertTrue(str.compareTo(future.get(1, TimeUnit.SECONDS)) == 0);
        }).start();
        future.resolve(str);
    }

    @Test
    public void testGetWithTimeoutNull(){
        String str = "teststr";
        new Thread(() -> {
                assertTrue(future.get(1, TimeUnit.SECONDS) == null);
        }).start();
    }
}
