package tests.freemind.plugins;

import freemind.controller.actions.generated.instance.CollaborationActionBase;
import freemind.controller.actions.generated.instance.CollaborationGoodbye;
import freemind.controller.actions.generated.instance.CollaborationHello;
import freemind.controller.actions.generated.instance.CollaborationTransaction;
import freemind.controller.actions.generated.instance.CollaborationUserInformation;
import freemind.controller.actions.generated.instance.CollaborationWelcome;
import freemind.controller.actions.generated.instance.CollaborationWhoAreYou;
import freemind.controller.actions.generated.instance.CollaborationRequireLock;
import freemind.controller.actions.generated.instance.CollaborationReceiveLock;
import freemind.controller.actions.generated.instance.CollaborationUnableToLock;
import freemind.controller.actions.generated.instance.CollaborationWrongCredentials;
import freemind.controller.actions.generated.instance.CollaborationWrongMap;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.HeadlessFreeMind;
import freemind.main.Tools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import plugins.collaboration.socket.CommunicationBase;
import plugins.collaboration.socket.SocketBasics;
import plugins.collaboration.socket.TerminateableThread;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Structural and JAXB marshalling tests for the collaboration/socket plugin.
 */
@DisplayName("Collaboration Structural")
class CollaborationStructuralTest {

    @BeforeAll
    static void ensureResources() {
        new HeadlessFreeMind();
    }

    // ── CommunicationBase class hierarchy ──

    @Nested
    @DisplayName("CommunicationBase")
    class CommunicationBaseTests {

        @Test
        @DisplayName("extends TerminateableThread")
        void extendsTerminateableThread() {
            assertTrue(TerminateableThread.class.isAssignableFrom(CommunicationBase.class),
                "CommunicationBase must extend TerminateableThread");
        }

        @Test
        @DisplayName("is abstract")
        void isAbstract() {
            assertTrue(Modifier.isAbstract(CommunicationBase.class.getModifiers()),
                "CommunicationBase must be abstract");
        }

        @Test
        @DisplayName("has terminateSocket abstract method")
        void hasTerminateSocketMethod() {
            boolean found = Arrays.stream(CommunicationBase.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("terminateSocket")
                    && Modifier.isAbstract(m.getModifiers()));
            assertTrue(found, "CommunicationBase must have abstract terminateSocket method");
        }

        @Test
        @DisplayName("has processCommand abstract method")
        void hasProcessCommandMethod() {
            boolean found = Arrays.stream(CommunicationBase.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("processCommand")
                    && Modifier.isAbstract(m.getModifiers()));
            assertTrue(found, "CommunicationBase must have abstract processCommand method");
        }

        @Test
        @DisplayName("mCurrentStateMutex is final Object (not String)")
        void currentStateMutexIsFinalObject() throws Exception {
            Field field = CommunicationBase.class.getDeclaredField("mCurrentStateMutex");
            field.setAccessible(true);
            assertTrue(Modifier.isFinal(field.getModifiers()),
                "mCurrentStateMutex must be final");
            assertEquals(Object.class, field.getType(),
                "mCurrentStateMutex must be of type Object");
        }
    }

    // ── State constants ──

    @Nested
    @DisplayName("State Constants")
    class StateConstantsTests {

        @Test
        @DisplayName("STATE_IDLE is 0")
        void stateIdleIsZero() {
            assertEquals(0, CommunicationBase.STATE_IDLE);
        }

        @Test
        @DisplayName("STATE_WAIT_FOR_HELLO is 1")
        void stateWaitForHelloIsOne() {
            assertEquals(1, CommunicationBase.STATE_WAIT_FOR_HELLO);
        }

        @Test
        @DisplayName("STATE_WAIT_FOR_COMMAND is 2")
        void stateWaitForCommandIsTwo() {
            assertEquals(2, CommunicationBase.STATE_WAIT_FOR_COMMAND);
        }

        @Test
        @DisplayName("STATE_LOCK_RECEIVED is 8")
        void stateLockReceivedIsEight() {
            assertEquals(8, CommunicationBase.STATE_LOCK_RECEIVED);
        }

        @Test
        @DisplayName("all public STATE_ constants are unique")
        void allStateConstantsAreUnique() throws Exception {
            Set<Integer> values = new HashSet<>();
            for (Field field : CommunicationBase.class.getDeclaredFields()) {
                if (field.getName().startsWith("STATE_")
                        && Modifier.isPublic(field.getModifiers())
                        && Modifier.isStatic(field.getModifiers())
                        && field.getType() == int.class) {
                    field.setAccessible(true);
                    int value = field.getInt(null);
                    assertTrue(values.add(value),
                        "Duplicate state constant value: " + value + " for " + field.getName());
                }
            }
            assertTrue(values.size() >= 5,
                "Expected at least 5 public STATE_ constants, found " + values.size());
        }
    }

    // ── TerminateableThread class hierarchy ──

    @Nested
    @DisplayName("TerminateableThread")
    class TerminateableThreadTests {

        @Test
        @DisplayName("extends Thread")
        void extendsThread() {
            assertTrue(Thread.class.isAssignableFrom(TerminateableThread.class),
                "TerminateableThread must extend Thread");
        }

        @Test
        @DisplayName("is abstract")
        void isAbstract() {
            assertTrue(Modifier.isAbstract(TerminateableThread.class.getModifiers()),
                "TerminateableThread must be abstract");
        }

        @Test
        @DisplayName("has processAction abstract method")
        void hasProcessActionMethod() {
            boolean found = Arrays.stream(TerminateableThread.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("processAction")
                    && Modifier.isAbstract(m.getModifiers()));
            assertTrue(found, "TerminateableThread must have abstract processAction method");
        }

        @Test
        @DisplayName("has commitSuicide method")
        void hasCommitSuicideMethod() {
            boolean found = Arrays.stream(TerminateableThread.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("commitSuicide"));
            assertTrue(found, "TerminateableThread must have commitSuicide method");
        }

        @Test
        @DisplayName("has mShouldTerminate field")
        void hasShouldTerminateField() {
            assertDoesNotThrow(
                () -> TerminateableThread.class.getDeclaredField("mShouldTerminate"),
                "TerminateableThread must have mShouldTerminate field");
        }
    }

    // ── SocketBasics ──

    @Nested
    @DisplayName("SocketBasics")
    class SocketBasicsTests {

        @Test
        @DisplayName("is abstract")
        void isAbstract() {
            assertTrue(Modifier.isAbstract(SocketBasics.class.getModifiers()),
                "SocketBasics must be abstract");
        }

        @Test
        @DisplayName("has MASTER_HOOK_LABEL constant")
        void hasMasterHookLabel() throws Exception {
            Field field = SocketBasics.class.getDeclaredField("MASTER_HOOK_LABEL");
            assertTrue(Modifier.isStatic(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
            String value = (String) field.get(null);
            assertTrue(value.contains("socket_master_plugin"),
                "MASTER_HOOK_LABEL must contain 'socket_master_plugin'");
        }

        @Test
        @DisplayName("has SLAVE_HOOK_LABEL constant")
        void hasSlaveHookLabel() throws Exception {
            Field field = SocketBasics.class.getDeclaredField("SLAVE_HOOK_LABEL");
            assertTrue(Modifier.isStatic(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
            String value = (String) field.get(null);
            assertTrue(value.contains("socket_slave_plugin"),
                "SLAVE_HOOK_LABEL must contain 'socket_slave_plugin'");
        }

        @Test
        @DisplayName("SOCKET_TIMEOUT_IN_MILLIES is 500")
        void socketTimeoutValue() {
            assertEquals(500, SocketBasics.SOCKET_TIMEOUT_IN_MILLIES);
        }

        @Test
        @DisplayName("has getRole abstract method")
        void hasGetRoleMethod() {
            boolean found = Arrays.stream(SocketBasics.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("getRole")
                    && Modifier.isAbstract(m.getModifiers()));
            assertTrue(found, "SocketBasics must have abstract getRole method");
        }
    }

    // ── JAXB marshalling round-trip for collaboration action classes ──

    @Nested
    @DisplayName("Collaboration JAXB Marshalling")
    class CollaborationJaxbTests {

        @Test
        @DisplayName("CollaborationHello marshall/unmarshall round-trip")
        void helloRoundTrip() {
            CollaborationHello hello = new CollaborationHello();
            hello.setMap("<map><node TEXT=\"Root\"/></map>");

            String xml = Tools.marshall(hello);
            assertNotNull(xml, "Marshalled CollaborationHello must not be null");
            assertTrue(xml.contains("collaboration_hello"), "XML must contain element name");

            XmlAction unmarshalled = Tools.unMarshall(xml);
            assertInstanceOf(CollaborationHello.class, unmarshalled);
            assertEquals("<map><node TEXT=\"Root\"/></map>",
                ((CollaborationHello) unmarshalled).getMap());
        }

        @Test
        @DisplayName("CollaborationGoodbye marshall/unmarshall round-trip")
        void goodbyeRoundTrip() {
            CollaborationGoodbye goodbye = new CollaborationGoodbye();
            goodbye.setUserId("user42");

            String xml = Tools.marshall(goodbye);
            assertNotNull(xml);
            assertTrue(xml.contains("collaboration_goodbye"));

            XmlAction unmarshalled = Tools.unMarshall(xml);
            assertInstanceOf(CollaborationGoodbye.class, unmarshalled);
            assertEquals("user42", ((CollaborationGoodbye) unmarshalled).getUserId());
        }

        @Test
        @DisplayName("CollaborationTransaction marshall/unmarshall round-trip")
        void transactionRoundTrip() {
            CollaborationTransaction trans = new CollaborationTransaction();
            trans.setId("lock-123");
            trans.setDoAction("<compound_action/>");
            trans.setUndoAction("<compound_action/>");

            String xml = Tools.marshall(trans);
            assertNotNull(xml);
            assertTrue(xml.contains("collaboration_transaction"));

            XmlAction unmarshalled = Tools.unMarshall(xml);
            assertInstanceOf(CollaborationTransaction.class, unmarshalled);
            CollaborationTransaction result = (CollaborationTransaction) unmarshalled;
            assertEquals("lock-123", result.getId());
            assertEquals("<compound_action/>", result.getDoAction());
            assertEquals("<compound_action/>", result.getUndoAction());
        }

        @Test
        @DisplayName("CollaborationWelcome marshall/unmarshall round-trip")
        void welcomeRoundTrip() {
            CollaborationWelcome welcome = new CollaborationWelcome();
            welcome.setMap("<map><node TEXT=\"Shared\"/></map>");
            welcome.setFilename("shared.mm");

            String xml = Tools.marshall(welcome);
            assertNotNull(xml);

            XmlAction unmarshalled = Tools.unMarshall(xml);
            assertInstanceOf(CollaborationWelcome.class, unmarshalled);
            CollaborationWelcome result = (CollaborationWelcome) unmarshalled;
            assertEquals("shared.mm", result.getFilename());
        }

        @Test
        @DisplayName("CollaborationWhoAreYou marshall/unmarshall round-trip")
        void whoAreYouRoundTrip() {
            CollaborationWhoAreYou whoAreYou = new CollaborationWhoAreYou();
            whoAreYou.setServerVersion("1.1.0");

            String xml = Tools.marshall(whoAreYou);
            assertNotNull(xml);

            XmlAction unmarshalled = Tools.unMarshall(xml);
            assertInstanceOf(CollaborationWhoAreYou.class, unmarshalled);
            assertEquals("1.1.0", ((CollaborationWhoAreYou) unmarshalled).getServerVersion());
        }

        @Test
        @DisplayName("CollaborationUserInformation marshall/unmarshall round-trip")
        void userInformationRoundTrip() {
            CollaborationUserInformation info = new CollaborationUserInformation();
            info.setUserIds("alice,bob");
            info.setMasterIp("192.168.1.1");
            info.setMasterPort(9001);
            info.setMasterHostname("mindmap-server");

            String xml = Tools.marshall(info);
            assertNotNull(xml);

            XmlAction unmarshalled = Tools.unMarshall(xml);
            assertInstanceOf(CollaborationUserInformation.class, unmarshalled);
            CollaborationUserInformation result = (CollaborationUserInformation) unmarshalled;
            assertEquals("alice,bob", result.getUserIds());
            assertEquals("192.168.1.1", result.getMasterIp());
            assertEquals(9001, result.getMasterPort());
            assertEquals("mindmap-server", result.getMasterHostname());
        }

        @Test
        @DisplayName("all collaboration classes extend CollaborationActionBase")
        void allCollaborationClassesExtendBase() {
            Class<?>[] collabClasses = {
                CollaborationHello.class,
                CollaborationGoodbye.class,
                CollaborationTransaction.class,
                CollaborationWelcome.class,
                CollaborationWhoAreYou.class,
                CollaborationUserInformation.class,
                CollaborationRequireLock.class,
                CollaborationReceiveLock.class,
                CollaborationUnableToLock.class,
                CollaborationWrongCredentials.class,
                CollaborationWrongMap.class,
            };
            for (Class<?> clazz : collabClasses) {
                assertTrue(CollaborationActionBase.class.isAssignableFrom(clazz),
                    clazz.getSimpleName() + " must extend CollaborationActionBase");
            }
        }

        @Test
        @DisplayName("CollaborationActionBase extends XmlAction")
        void collaborationActionBaseExtendsXmlAction() {
            assertTrue(XmlAction.class.isAssignableFrom(CollaborationActionBase.class),
                "CollaborationActionBase must extend XmlAction");
        }
    }
}
