package tests.freemind;

import freemind.common.XmlBindingTools;
import freemind.main.HeadlessFreeMind;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Thread safety verification tests for Phase H fixes and review findings.
 */
@DisplayName("Thread Safety Verification")
class ThreadSafetyTest {

    @BeforeAll
    static void ensureResources() {
        new HeadlessFreeMind();
    }

    @Test
    @DisplayName("XmlBindingTools.getInstance() returns same instance from 100 concurrent threads")
    void xmlBindingToolsSingletonThreadSafety() throws Exception {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<XmlBindingTools>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                startGate.await();
                return XmlBindingTools.getInstance();
            }));
        }

        startGate.countDown();

        Set<XmlBindingTools> uniqueInstances = ConcurrentHashMap.newKeySet();
        for (Future<XmlBindingTools> future : futures) {
            XmlBindingTools instance = future.get(5, TimeUnit.SECONDS);
            assertNotNull(instance, "getInstance() must never return null");
            uniqueInstances.add(instance);
        }

        assertEquals(1, uniqueInstances.size(),
            "All threads must receive the exact same singleton instance");

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("XmlBindingTools uses Holder pattern (not synchronized method)")
    void xmlBindingToolsHolderPatternVerification() throws Exception {
        var method = XmlBindingTools.class.getDeclaredMethod("getInstance");
        assertFalse(Modifier.isSynchronized(method.getModifiers()),
            "getInstance() must NOT be synchronized (holder pattern is lock-free)");

        boolean holderFound = false;
        for (Class<?> inner : XmlBindingTools.class.getDeclaredClasses()) {
            if (inner.getSimpleName().equals("Holder")) {
                holderFound = true;
                Field instanceField = inner.getDeclaredField("INSTANCE");
                assertTrue(Modifier.isStatic(instanceField.getModifiers()),
                    "Holder.INSTANCE must be static");
                assertTrue(Modifier.isFinal(instanceField.getModifiers()),
                    "Holder.INSTANCE must be final");
            }
        }
        assertTrue(holderFound, "XmlBindingTools must have inner Holder class");
    }

    @Test
    @DisplayName("XmlBindingTools creates valid Marshaller and Unmarshaller")
    void xmlBindingToolsProducesValidMarshallers() {
        XmlBindingTools tools = XmlBindingTools.getInstance();
        assertNotNull(tools.createMarshaller(), "createMarshaller() must not return null");
        assertNotNull(tools.createUnmarshaller(), "createUnmarshaller() must not return null");
    }

    @Test
    @DisplayName("CommunicationBase mutex is final Object, not String literal")
    void communicationBaseMutexIsObject() throws Exception {
        Class<?> clazz = Class.forName("plugins.collaboration.socket.CommunicationBase");
        Field mutexField = clazz.getDeclaredField("mCurrentStateMutex");
        mutexField.setAccessible(true);

        assertEquals(Object.class, mutexField.getType(),
            "mCurrentStateMutex must be Object, not String");
        assertTrue(Modifier.isFinal(mutexField.getModifiers()),
            "mCurrentStateMutex must be final");
    }

    @Test
    @DisplayName("CalendarMarkingEvaluator handler map is eagerly initialized (static final)")
    void calendarMarkingEvaluatorEagerInit() throws Exception {
        Class<?> clazz = Class.forName("accessories.plugins.time.CalendarMarkingEvaluator");
        Field field = clazz.getDeclaredField("sHandlerMap");

        assertTrue(Modifier.isStatic(field.getModifiers()), "sHandlerMap must be static");
        assertTrue(Modifier.isFinal(field.getModifiers()), "sHandlerMap must be final (eager init)");
    }

    @Test
    @DisplayName("FreeMind.mFileHandler is volatile for double-checked locking")
    void freeMindFileHandlerIsVolatile() throws Exception {
        Class<?> clazz = Class.forName("freemind.main.FreeMind");
        Field field = clazz.getDeclaredField("mFileHandler");
        assertTrue(Modifier.isVolatile(field.getModifiers()),
            "mFileHandler must be volatile for double-checked locking");
    }

    @Test
    @DisplayName("FreeMind.sLogFileHandler is volatile for visibility")
    void freeMindLogFileHandlerIsVolatile() throws Exception {
        Class<?> clazz = Class.forName("freemind.main.FreeMind");
        Field field = clazz.getDeclaredField("sLogFileHandler");
        assertTrue(Modifier.isVolatile(field.getModifiers()),
            "sLogFileHandler must be volatile (read outside synchronized block)");
    }

    @Test
    @DisplayName("NodeAdapter.sSaveIdPropertyChangeListener is volatile for DCL")
    void nodeAdapterPropertyListenerIsVolatile() throws Exception {
        Class<?> clazz = Class.forName("freemind.modes.NodeAdapter");
        Field field = clazz.getDeclaredField("sSaveIdPropertyChangeListener");
        assertTrue(Modifier.isVolatile(field.getModifiers()),
            "sSaveIdPropertyChangeListener must be volatile for double-checked locking");
    }

    @Test
    @DisplayName("Concurrent XmlBindingTools marshal/unmarshal creates valid instances")
    void concurrentMarshalUnmarshal() throws Exception {
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                startGate.await();
                XmlBindingTools tools = XmlBindingTools.getInstance();
                return tools.createMarshaller() != null && tools.createUnmarshaller() != null;
            }));
        }

        startGate.countDown();

        for (Future<Boolean> future : futures) {
            assertTrue(future.get(5, TimeUnit.SECONDS),
                "Concurrent marshal/unmarshal must succeed");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }
}
