package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.*;



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
        msgBus.subscribeEvent(ae.getClass(), ms);
        msgBus.sendEvent(ae);
        Message m;
        try {
            m = msgBus.awaitMessage(ms);
            assert false;
        } catch (Exception e) {
            assert true;
        }
        msgBus.register(ms);
        msgBus.subscribeEvent(ae.getClass(), ms);
        msgBus.sendEvent(ae);

        try {
            m = msgBus.awaitMessage(ms);
            assertEquals(m, ae);
        } catch (Exception e) {
            assert false;
        }
        msgBus.unregister(ms);
    }


    @Test
    public void testSubscribeEvent(){
        msgBus.register(ms);
        msgBus.register(OtherMs);
        Message m;
        msgBus.subscribeEvent(ae.getClass(), ms);
        msgBus.sendEvent(ae);
        try {
            m = msgBus.awaitMessage(ms);
            assertEquals(m, ae);
        } catch (Exception e) {
            assert false;
        }
        try {
            m = msgBus.awaitMessage(OtherMs);
            assert false;
        } catch (Exception e) {
            assert true;
        }
        msgBus.unregister(ms);
        msgBus.unregister(OtherMs);
    }

    @Test
    public void testSubscribeBroadcast() {
        msgBus.register(ms);
        msgBus.register(OtherMs);
        Message m;
        msgBus.subscribeBroadcast(bc.getClass(), ms);
        msgBus.sendBroadcast(bc);
        try {
            m = msgBus.awaitMessage(ms);
            assertEquals(m, bc);
        } catch (Exception e) {
            assert false;
        }
        try {
            m = msgBus.awaitMessage(OtherMs);
            assert false;
        } catch (Exception e) {
            assert true;
        }
        msgBus.unregister(ms);
        msgBus.unregister(OtherMs);
    }
    @Test
    public void testComplete(){
        msgBus.register(ms);
        msgBus.subscribeEvent(ae.getClass(), ms);
        msgBus.sendEvent(ae);
        Message m;
        try {
            m = msgBus.awaitMessage(ms);
            assertEquals(m, ae);
        } catch (Exception e) {
            assert false;
        }
        msgBus.complete(ae, true);
        try {
            m = msgBus.awaitMessage(OtherMs);
            assert false;
        } catch (Exception e) {
            assert true;
        }
        msgBus.unregister(ms);
    }

    @Test
    public void testSendBroadcast(){
        msgBus.register(ms);
        msgBus.register(OtherMs);
        msgBus.subscribeBroadcast(bc.getClass(), ms);
        msgBus.sendBroadcast(bc);
        Message m;
        try {
            m = msgBus.awaitMessage(ms);
            assertEquals(m, bc);
        } catch (Exception e) {
            assert false;
        }
        try {
            msgBus.awaitMessage(OtherMs);
            assert false;
        } catch (Exception e) {
            assert true;
        }
        msgBus.unregister(ms);
        msgBus.unregister(OtherMs);
    }

    @Test
    public void testSendEvent(){
        msgBus.register(ms);
        msgBus.register(OtherMs);
        msgBus.subscribeEvent(ae.getClass(), ms);
        msgBus.sendEvent(ae);
        Message m;
        try {
            m = msgBus.awaitMessage(ms);
            assertEquals(m, ae);
        } catch (Exception e) {
            assert false;
        }
        try {
            msgBus.awaitMessage(OtherMs);
            assert false;
        } catch (Exception e) {
            assert true;
        }
        msgBus.unregister(ms);
        msgBus.unregister(OtherMs);
    }



    @Test
    public void testAwaitMessage() {
        msgBus.register(ms);
        msgBus.register(OtherMs);
        msgBus.subscribeEvent(ae.getClass(), ms);
        msgBus.subscribeBroadcast(bc.getClass(), OtherMs);
        msgBus.sendEvent(ae);
        msgBus.sendBroadcast(bc);
        Message m;
       try {
          m = msgBus.awaitMessage(ms);
          assertEquals(m, ae);
          m = msgBus.awaitMessage(OtherMs);
           assertEquals(m, bc);
       }
       catch (Exception e) {
           assert false;
       }
    }
}
