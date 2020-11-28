package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class MessageBusTest {
    private class BCast implements Broadcast {}
    private MessageBus msgBus;
    private MicroService ms;
    private MicroService OtherMs;
    private AttackEvent ae;
    private BCast bc;
    @BeforeEach
    public void setUp(){
        msgBus = MessageBusImpl.getInstance();
        ms = new C3POMicroservice();
        OtherMs = new HanSoloMicroservice();
        ae = new AttackEvent();
        bc = new BCast();
    }

    @Test
    public void testRegister(){
        assertEquals(msgBus.getQueues().size(), 0);
        msgBus.register(ms);
        assertEquals(msgBus.getQueues().size(), 1);
        msgBus.unregister(ms);
    }

    @Test
    public void testUnregister() {
        msgBus.register(ms);
        assertEquals(msgBus.getQueues().size(), 1);
        msgBus.unregister(ms);
        assertEquals(msgBus.getQueues().size(), 0);
    }

    @Test
    public void testSubscribeEvent(){
        msgBus.register(ms);
        msgBus.sendEvent(ae);
        assertTrue(msgBus.getQueues().get(ms).isEmpty());
        msgBus.subscribeEvent(ae.getClass(), ms);
        msgBus.sendEvent(ae);
        assertEquals(msgBus.getQueues().get(ms).peek(), ae);
    }

    @Test
    public void testSubscribeBroadcast() {
        msgBus.register(ms);
        msgBus.sendBroadcast(bc);
        assertTrue(msgBus.getQueues().get(ms).isEmpty());
        msgBus.subscribeBroadcast(bc.getClass(),ms);
        msgBus.sendBroadcast(bc);
        assertEquals(msgBus.getQueues().get(ms).peek(), bc);
    }
    @Test
    public void testComplete(){
        msgBus.register(ms);
        msgBus.subscribeEvent(ae.getClass(), ms);
        msgBus.sendEvent(ae);
        assertEquals(msgBus.getQueues().get(ms).peek(), ae);
        msgBus.complete(ae,true);
        assertEquals(msgBus.getQueues().get(ms).size(), 0);
    }

    @Test
    public void testSendBroadcast(){
        msgBus.register(ms);
        msgBus.register(OtherMs);
        msgBus.subscribeBroadcast(bc.getClass(), ms);
        msgBus.subscribeBroadcast(bc.getClass(), OtherMs);
        msgBus.sendBroadcast(bc);
        assertEquals(msgBus.getQueues().get(ms).peek(), bc);
        assertEquals(msgBus.getQueues().get(OtherMs).peek(), bc);
        msgBus.unregister(ms);
        msgBus.unregister(OtherMs);
    }

    @Test
    public void testSendEvent(){
        msgBus.register(ms);
        msgBus.subscribeEvent(ae.getClass(), ms);
        msgBus.sendEvent(ae);
        assertEquals(msgBus.getQueues().get(ms).peek(), ae);
        msgBus.unregister(ms);
    }



    @Test
    public void testAwaitMessage() {
       try {
           msgBus.awaitMessage(ms);
       }
       catch (Exception e) { }
    }
}
