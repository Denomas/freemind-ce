/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2024  FreeMind CE contributors
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package tests.freemind.actions;

import freemind.common.XmlBindingTools;
import freemind.controller.actions.generated.instance.*;
import freemind.main.HeadlessFreeMind;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActionRegistry;
import freemind.modes.mindmapmode.actions.xml.ActorXml;
import freemind.modes.mindmapmode.actions.xml.actors.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the FreeMind CE action framework: ActionPair, ActionRegistry,
 * XmlActorFactory, JAXB action serialization, and actor-based node operations.
 */
@DisplayName("Action Framework")
class ActionFrameworkTest {

    private static final String INITIAL_MAP = "<map>" + "<node TEXT='ROOT'>"
            + "<node TEXT='FormatMe'>"
            + "<node TEXT='Child1'/>"
            + "<node TEXT='Child2'/>"
            + "<node TEXT='Child3'/>"
            + "</node>"
            + "</node>" + "</map>";

    @BeforeAll
    static void initFreeMind() {
        new HeadlessFreeMind();
    }

    // -----------------------------------------------------------------------
    // ActionPair tests
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("ActionPair")
    class ActionPairTests {

        @Test
        @DisplayName("constructor stores doAction and undoAction")
        void constructorStoresActions() {
            BoldNodeAction doAction = new BoldNodeAction();
            doAction.setNode("ID_1");
            doAction.setBold(true);

            BoldNodeAction undoAction = new BoldNodeAction();
            undoAction.setNode("ID_1");
            undoAction.setBold(false);

            ActionPair pair = new ActionPair(doAction, undoAction);
            assertSame(doAction, pair.getDoAction());
            assertSame(undoAction, pair.getUndoAction());
        }

        @Test
        @DisplayName("setDoAction replaces the doAction")
        void setDoActionReplacesAction() {
            ActionPair pair = new ActionPair(new BoldNodeAction(), new BoldNodeAction());
            ItalicNodeAction replacement = new ItalicNodeAction();
            pair.setDoAction(replacement);
            assertSame(replacement, pair.getDoAction());
        }

        @Test
        @DisplayName("setUndoAction replaces the undoAction")
        void setUndoActionReplacesAction() {
            ActionPair pair = new ActionPair(new BoldNodeAction(), new BoldNodeAction());
            ItalicNodeAction replacement = new ItalicNodeAction();
            pair.setUndoAction(replacement);
            assertSame(replacement, pair.getUndoAction());
        }

        @Test
        @DisplayName("reverse swaps doAction and undoAction")
        void reverseSwapsActions() {
            BoldNodeAction doAction = new BoldNodeAction();
            ItalicNodeAction undoAction = new ItalicNodeAction();
            ActionPair pair = new ActionPair(doAction, undoAction);

            ActionPair reversed = pair.reverse();
            assertSame(undoAction, reversed.getDoAction());
            assertSame(doAction, reversed.getUndoAction());
        }

        @Test
        @DisplayName("reverse returns a new ActionPair instance")
        void reverseReturnsNewInstance() {
            ActionPair pair = new ActionPair(new BoldNodeAction(), new ItalicNodeAction());
            ActionPair reversed = pair.reverse();
            assertNotSame(pair, reversed);
        }

        @Test
        @DisplayName("reverse is self-inverse")
        void doubleReverseRestoresOriginal() {
            BoldNodeAction doAction = new BoldNodeAction();
            ItalicNodeAction undoAction = new ItalicNodeAction();
            ActionPair pair = new ActionPair(doAction, undoAction);

            ActionPair doubleReversed = pair.reverse().reverse();
            assertSame(doAction, doubleReversed.getDoAction());
            assertSame(undoAction, doubleReversed.getUndoAction());
        }

        @Test
        @DisplayName("allows null doAction and undoAction")
        void allowsNullActions() {
            ActionPair pair = new ActionPair(null, null);
            assertNull(pair.getDoAction());
            assertNull(pair.getUndoAction());
        }
    }

    // -----------------------------------------------------------------------
    // ActionRegistry tests
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("ActionRegistry")
    class ActionRegistryTests {

        private ActionRegistry registry;

        @BeforeEach
        void setUp() {
            registry = new ActionRegistry();
        }

        @Test
        @DisplayName("registerActor and getActor by class")
        void registerAndGetActorByClass() {
            ExtendedMapFeedbackImpl feedback = createFeedback();
            BoldNodeActor boldActor = new BoldNodeActor(feedback);
            registry.registerActor(boldActor, BoldNodeAction.class);

            ActorXml retrieved = registry.getActor(BoldNodeAction.class);
            assertSame(boldActor, retrieved);
        }

        @Test
        @DisplayName("registerActor and getActor by action instance")
        void registerAndGetActorByInstance() {
            ExtendedMapFeedbackImpl feedback = createFeedback();
            BoldNodeActor boldActor = new BoldNodeActor(feedback);
            registry.registerActor(boldActor, BoldNodeAction.class);

            BoldNodeAction action = new BoldNodeAction();
            ActorXml retrieved = registry.getActor(action);
            assertSame(boldActor, retrieved);
        }

        @Test
        @DisplayName("getActor throws for unregistered action class")
        void getActorThrowsForUnregisteredClass() {
            assertThrows(IllegalArgumentException.class,
                    () -> registry.getActor(BoldNodeAction.class));
        }

        @Test
        @DisplayName("getActor throws for unregistered action instance")
        void getActorThrowsForUnregisteredInstance() {
            assertThrows(IllegalArgumentException.class,
                    () -> registry.getActor(new BoldNodeAction()));
        }

        @Test
        @DisplayName("deregisterActor removes the actor")
        void deregisterActorRemovesIt() {
            ExtendedMapFeedbackImpl feedback = createFeedback();
            BoldNodeActor boldActor = new BoldNodeActor(feedback);
            registry.registerActor(boldActor, BoldNodeAction.class);
            registry.deregisterActor(BoldNodeAction.class);

            assertThrows(IllegalArgumentException.class,
                    () -> registry.getActor(BoldNodeAction.class));
        }

        @Test
        @DisplayName("multiple actors can be registered for different actions")
        void multipleActorsRegistered() {
            ExtendedMapFeedbackImpl feedback = createFeedback();
            BoldNodeActor boldActor = new BoldNodeActor(feedback);
            ItalicNodeActor italicActor = new ItalicNodeActor(feedback);
            registry.registerActor(boldActor, BoldNodeAction.class);
            registry.registerActor(italicActor, ItalicNodeAction.class);

            assertSame(boldActor, registry.getActor(BoldNodeAction.class));
            assertSame(italicActor, registry.getActor(ItalicNodeAction.class));
        }

        @Test
        @DisplayName("doTransaction returns false for null pair")
        void doTransactionReturnsFalseForNull() {
            boolean result = registry.doTransaction("test", null);
            assertFalse(result);
        }
    }

    // -----------------------------------------------------------------------
    // XmlActorFactory tests
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("XmlActorFactory")
    class XmlActorFactoryTests {

        private XmlActorFactory factory;

        @BeforeEach
        void setUp() {
            ExtendedMapFeedbackImpl feedback = createFeedback();
            factory = new XmlActorFactory(feedback);
        }

        @Test
        @DisplayName("creates BoldNodeActor")
        void createsBoldActor() {
            assertNotNull(factory.getBoldActor());
            assertTrue(factory.getBoldActor() instanceof BoldNodeActor);
        }

        @Test
        @DisplayName("creates ItalicNodeActor")
        void createsItalicActor() {
            assertNotNull(factory.getItalicActor());
            assertTrue(factory.getItalicActor() instanceof ItalicNodeActor);
        }

        @Test
        @DisplayName("creates StrikethroughNodeActor")
        void createsStrikethroughActor() {
            assertNotNull(factory.getStrikethroughActor());
            assertTrue(factory.getStrikethroughActor() instanceof StrikethroughNodeActor);
        }

        @Test
        @DisplayName("creates FontSizeActor")
        void createsFontSizeActor() {
            assertNotNull(factory.getFontSizeActor());
            assertTrue(factory.getFontSizeActor() instanceof FontSizeActor);
        }

        @Test
        @DisplayName("creates FontFamilyActor")
        void createsFontFamilyActor() {
            assertNotNull(factory.getFontFamilyActor());
            assertTrue(factory.getFontFamilyActor() instanceof FontFamilyActor);
        }

        @Test
        @DisplayName("creates EditActor")
        void createsEditActor() {
            assertNotNull(factory.getEditActor());
            assertTrue(factory.getEditActor() instanceof EditActor);
        }

        @Test
        @DisplayName("creates NewChildActor")
        void createsNewChildActor() {
            assertNotNull(factory.getNewChildActor());
            assertTrue(factory.getNewChildActor() instanceof NewChildActor);
        }

        @Test
        @DisplayName("creates DeleteChildActor")
        void createsDeleteChildActor() {
            assertNotNull(factory.getDeleteChildActor());
            assertTrue(factory.getDeleteChildActor() instanceof DeleteChildActor);
        }

        @Test
        @DisplayName("creates CloudActor")
        void createsCloudActor() {
            assertNotNull(factory.getCloudActor());
            assertTrue(factory.getCloudActor() instanceof CloudActor);
        }

        @Test
        @DisplayName("creates EdgeStyleActor")
        void createsEdgeStyleActor() {
            assertNotNull(factory.getEdgeStyleActor());
            assertTrue(factory.getEdgeStyleActor() instanceof EdgeStyleActor);
        }

        @Test
        @DisplayName("creates EdgeWidthActor")
        void createsEdgeWidthActor() {
            assertNotNull(factory.getEdgeWidthActor());
            assertTrue(factory.getEdgeWidthActor() instanceof EdgeWidthActor);
        }

        @Test
        @DisplayName("creates EdgeColorActor")
        void createsEdgeColorActor() {
            assertNotNull(factory.getEdgeColorActor());
            assertTrue(factory.getEdgeColorActor() instanceof EdgeColorActor);
        }

        @Test
        @DisplayName("creates NodeColorActor")
        void createsNodeColorActor() {
            assertNotNull(factory.getNodeColorActor());
            assertTrue(factory.getNodeColorActor() instanceof NodeColorActor);
        }

        @Test
        @DisplayName("creates NodeBackgroundColorActor")
        void createsNodeBackgroundColorActor() {
            assertNotNull(factory.getNodeBackgroundColorActor());
            assertTrue(factory.getNodeBackgroundColorActor() instanceof NodeBackgroundColorActor);
        }

        @Test
        @DisplayName("creates NodeStyleActor")
        void createsNodeStyleActor() {
            assertNotNull(factory.getNodeStyleActor());
            assertTrue(factory.getNodeStyleActor() instanceof NodeStyleActor);
        }

        @Test
        @DisplayName("creates CompoundActor")
        void createsCompoundActor() {
            assertNotNull(factory.getCompoundActor());
            assertTrue(factory.getCompoundActor() instanceof CompoundActor);
        }

        @Test
        @DisplayName("creates ToggleFoldedActor")
        void createsToggleFoldedActor() {
            assertNotNull(factory.getToggleFoldedActor());
            assertTrue(factory.getToggleFoldedActor() instanceof ToggleFoldedActor);
        }

        @Test
        @DisplayName("creates SetLinkActor")
        void createsSetLinkActor() {
            assertNotNull(factory.getSetLinkActor());
            assertTrue(factory.getSetLinkActor() instanceof SetLinkActor);
        }

        @Test
        @DisplayName("creates ChangeNoteTextActor")
        void createsChangeNoteTextActor() {
            assertNotNull(factory.getChangeNoteTextActor());
            assertTrue(factory.getChangeNoteTextActor() instanceof ChangeNoteTextActor);
        }
    }

    // -----------------------------------------------------------------------
    // Actor getDoActionClass tests
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("Actor getDoActionClass returns correct JAXB type")
    class ActorDoActionClassTests {

        private ExtendedMapFeedbackImpl feedback;

        @BeforeEach
        void setUp() {
            feedback = createFeedback();
        }

        @Test
        @DisplayName("BoldNodeActor maps to BoldNodeAction")
        void boldActorDoActionClass() {
            assertEquals(BoldNodeAction.class, new BoldNodeActor(feedback).getDoActionClass());
        }

        @Test
        @DisplayName("ItalicNodeActor maps to ItalicNodeAction")
        void italicActorDoActionClass() {
            assertEquals(ItalicNodeAction.class, new ItalicNodeActor(feedback).getDoActionClass());
        }

        @Test
        @DisplayName("StrikethroughNodeActor maps to StrikethroughNodeAction")
        void strikethroughActorDoActionClass() {
            assertEquals(StrikethroughNodeAction.class,
                    new StrikethroughNodeActor(feedback).getDoActionClass());
        }

        @Test
        @DisplayName("FontSizeActor maps to FontSizeNodeAction")
        void fontSizeActorDoActionClass() {
            assertEquals(FontSizeNodeAction.class, new FontSizeActor(feedback).getDoActionClass());
        }

        @Test
        @DisplayName("EditActor maps to EditNodeAction")
        void editActorDoActionClass() {
            assertEquals(EditNodeAction.class, new EditActor(feedback).getDoActionClass());
        }

        @Test
        @DisplayName("CloudActor maps to AddCloudXmlAction")
        void cloudActorDoActionClass() {
            assertEquals(AddCloudXmlAction.class, new CloudActor(feedback).getDoActionClass());
        }

        @Test
        @DisplayName("CompoundActor maps to CompoundAction")
        void compoundActorDoActionClass() {
            assertEquals(CompoundAction.class, new CompoundActor(feedback).getDoActionClass());
        }
    }

    // -----------------------------------------------------------------------
    // JAXB action serialization tests
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("JAXB Action Serialization")
    class JaxbSerializationTests {

        @Test
        @DisplayName("BoldNodeAction marshalls to XML")
        void boldNodeActionMarshallsToXml() {
            BoldNodeAction action = new BoldNodeAction();
            action.setNode("ID_123");
            action.setBold(true);

            String xml = XmlBindingTools.getInstance().marshall(action);
            assertNotNull(xml);
            assertTrue(xml.contains("bold_node_action"));
            assertTrue(xml.contains("ID_123"));
        }

        @Test
        @DisplayName("ItalicNodeAction marshalls to XML")
        void italicNodeActionMarshallsToXml() {
            ItalicNodeAction action = new ItalicNodeAction();
            action.setNode("ID_456");
            action.setItalic(true);

            String xml = XmlBindingTools.getInstance().marshall(action);
            assertNotNull(xml);
            assertTrue(xml.contains("italic_node_action"));
        }

        @Test
        @DisplayName("FontSizeNodeAction marshalls to XML")
        void fontSizeNodeActionMarshallsToXml() {
            FontSizeNodeAction action = new FontSizeNodeAction();
            action.setNode("ID_789");
            action.setSize("24");

            String xml = XmlBindingTools.getInstance().marshall(action);
            assertNotNull(xml);
            assertTrue(xml.contains("font_size_node_action"));
            assertTrue(xml.contains("24"));
        }

        @Test
        @DisplayName("EditNodeAction marshalls to XML")
        void editNodeActionMarshallsToXml() {
            EditNodeAction action = new EditNodeAction();
            action.setNode("ID_100");
            action.setText("Hello World");

            String xml = XmlBindingTools.getInstance().marshall(action);
            assertNotNull(xml);
            assertTrue(xml.contains("edit_node_action"));
        }

        @Test
        @DisplayName("CompoundAction with children marshalls to XML")
        void compoundActionWithChildrenMarshallsToXml() {
            CompoundAction compound = new CompoundAction();
            BoldNodeAction bold = new BoldNodeAction();
            bold.setNode("ID_1");
            bold.setBold(true);
            ItalicNodeAction italic = new ItalicNodeAction();
            italic.setNode("ID_1");
            italic.setItalic(true);
            compound.addChoice(bold);
            compound.addChoice(italic);

            String xml = XmlBindingTools.getInstance().marshall(compound);
            assertNotNull(xml);
            assertTrue(xml.contains("compound_action"));
            assertTrue(xml.contains("bold_node_action"));
            assertTrue(xml.contains("italic_node_action"));
        }

        @Test
        @DisplayName("BoldNodeAction round-trips through marshall/unmarshall")
        void boldNodeActionRoundTrip() {
            BoldNodeAction original = new BoldNodeAction();
            original.setNode("ID_RT");
            original.setBold(true);

            String xml = XmlBindingTools.getInstance().marshall(original);
            XmlAction unmarshalled = XmlBindingTools.getInstance().unMarshall(xml);

            assertNotNull(unmarshalled);
            assertInstanceOf(BoldNodeAction.class, unmarshalled);
            BoldNodeAction restored = (BoldNodeAction) unmarshalled;
            assertEquals("ID_RT", restored.getNode());
            assertTrue(restored.getBold());
        }

        @Test
        @DisplayName("CompoundAction round-trips preserving children")
        void compoundActionRoundTrip() {
            CompoundAction original = new CompoundAction();
            BoldNodeAction bold = new BoldNodeAction();
            bold.setNode("ID_C1");
            bold.setBold(true);
            original.addChoice(bold);

            String xml = XmlBindingTools.getInstance().marshall(original);
            XmlAction unmarshalled = XmlBindingTools.getInstance().unMarshall(xml);

            assertNotNull(unmarshalled);
            assertInstanceOf(CompoundAction.class, unmarshalled);
            CompoundAction restored = (CompoundAction) unmarshalled;
            assertEquals(1, restored.sizeChoiceList());
            assertInstanceOf(BoldNodeAction.class, restored.getChoice(0));
        }

        @Test
        @DisplayName("empty CompoundAction marshalls to XML")
        void emptyCompoundActionMarshalls() {
            CompoundAction compound = new CompoundAction();
            String xml = XmlBindingTools.getInstance().marshall(compound);
            assertNotNull(xml);
            assertTrue(xml.contains("compound_action"));
        }

        @Test
        @DisplayName("AddCloudXmlAction marshalls to XML")
        void cloudActionMarshallsToXml() {
            AddCloudXmlAction action = new AddCloudXmlAction();
            action.setNode("ID_CLD");
            action.setEnabled(true);
            action.setColor(Tools.colorToXml(Color.CYAN));

            String xml = XmlBindingTools.getInstance().marshall(action);
            assertNotNull(xml);
            assertTrue(xml.contains("add_cloud_xml_action"));
        }

        @Test
        @DisplayName("FoldAction marshalls to XML")
        void foldActionMarshallsToXml() {
            FoldAction action = new FoldAction();
            action.setNode("ID_FLD");
            action.setFolded(true);

            String xml = XmlBindingTools.getInstance().marshall(action);
            assertNotNull(xml);
            assertTrue(xml.contains("fold_action"));
        }

        @Test
        @DisplayName("StrikethroughNodeAction round-trips correctly")
        void strikethroughRoundTrip() {
            StrikethroughNodeAction original = new StrikethroughNodeAction();
            original.setNode("ID_ST");
            original.setStrikethrough(true);

            String xml = XmlBindingTools.getInstance().marshall(original);
            XmlAction unmarshalled = XmlBindingTools.getInstance().unMarshall(xml);
            assertInstanceOf(StrikethroughNodeAction.class, unmarshalled);
            StrikethroughNodeAction restored = (StrikethroughNodeAction) unmarshalled;
            assertEquals("ID_ST", restored.getNode());
            assertTrue(restored.getStrikethrough());
        }
    }

    // -----------------------------------------------------------------------
    // CompoundAction structure tests
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("CompoundAction structure")
    class CompoundActionStructureTests {

        @Test
        @DisplayName("addChoice appends to child list")
        void addChoiceAppendsChildren() {
            CompoundAction compound = new CompoundAction();
            assertEquals(0, compound.sizeChoiceList());

            compound.addChoice(new BoldNodeAction());
            assertEquals(1, compound.sizeChoiceList());

            compound.addChoice(new ItalicNodeAction());
            assertEquals(2, compound.sizeChoiceList());
        }

        @Test
        @DisplayName("getChoice returns child by index")
        void getChoiceReturnsByIndex() {
            CompoundAction compound = new CompoundAction();
            BoldNodeAction bold = new BoldNodeAction();
            ItalicNodeAction italic = new ItalicNodeAction();
            compound.addChoice(bold);
            compound.addChoice(italic);

            assertSame(bold, compound.getChoice(0));
            assertSame(italic, compound.getChoice(1));
        }

        @Test
        @DisplayName("addAtChoice inserts at specified index")
        void addAtChoiceInsertsAtIndex() {
            CompoundAction compound = new CompoundAction();
            BoldNodeAction bold = new BoldNodeAction();
            ItalicNodeAction italic = new ItalicNodeAction();
            FontSizeNodeAction fontSize = new FontSizeNodeAction();

            compound.addChoice(bold);
            compound.addChoice(italic);
            compound.addAtChoice(1, fontSize);

            assertEquals(3, compound.sizeChoiceList());
            assertSame(bold, compound.getChoice(0));
            assertSame(fontSize, compound.getChoice(1));
            assertSame(italic, compound.getChoice(2));
        }

        @Test
        @DisplayName("setAtChoice replaces at specified index")
        void setAtChoiceReplacesAtIndex() {
            CompoundAction compound = new CompoundAction();
            BoldNodeAction bold = new BoldNodeAction();
            ItalicNodeAction italic = new ItalicNodeAction();
            compound.addChoice(bold);

            compound.setAtChoice(0, italic);
            assertEquals(1, compound.sizeChoiceList());
            assertSame(italic, compound.getChoice(0));
        }

        @Test
        @DisplayName("getListChoiceList returns same list as underlying accessor")
        void getListChoiceListSameAsUnderlying() {
            CompoundAction compound = new CompoundAction();
            compound.addChoice(new BoldNodeAction());
            assertSame(compound.getCompoundActionOrSelectNodeActionOrCutNodeAction(),
                    compound.getListChoiceList());
        }
    }

    // -----------------------------------------------------------------------
    // Actor operations via ExtendedMapFeedbackImpl tests
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("Actor operations via ExtendedMapFeedbackImpl")
    class ActorOperationsTests {

        private ExtendedMapFeedbackImpl mapFeedback;
        private MindMapNode root;
        private MindMapNode firstChild;

        @BeforeEach
        void setUp() throws Exception {
            mapFeedback = new ExtendedMapFeedbackImpl();
            MindMapMapModel mMap = new MindMapMapModel(mapFeedback);
            mapFeedback.setMap(mMap);
            Tools.StringReaderCreator readerCreator = new Tools.StringReaderCreator(INITIAL_MAP);
            root = mMap.loadTree(readerCreator, MapAdapter.sDontAskInstance);
            mMap.setRoot(root);
            firstChild = (MindMapNode) root.getChildAt(0);
        }

        @Test
        @DisplayName("setBold changes node bold state")
        void setBoldChangesState() {
            assertFalse(root.isBold());
            mapFeedback.setBold(root, true);
            assertTrue(root.isBold());
        }

        @Test
        @DisplayName("setBold false reverts bold state")
        void setBoldFalseReverts() {
            mapFeedback.setBold(root, true);
            mapFeedback.setBold(root, false);
            assertFalse(root.isBold());
        }

        @Test
        @DisplayName("setItalic changes node italic state")
        void setItalicChangesState() {
            assertFalse(root.isItalic());
            mapFeedback.setItalic(root, true);
            assertTrue(root.isItalic());
        }

        @Test
        @DisplayName("setStrikethrough changes node strikethrough state")
        void setStrikethroughChangesState() {
            assertFalse(root.isStrikethrough());
            mapFeedback.setStrikethrough(root, true);
            assertTrue(root.isStrikethrough());
        }

        @Test
        @DisplayName("setFontSize changes node font size")
        void setFontSizeChangesSize() {
            mapFeedback.setFontSize(root, "24");
            assertEquals("24", root.getFontSize());
        }

        @Test
        @DisplayName("setNodeText changes node text")
        void setNodeTextChangesText() {
            String newText = "Updated Text";
            mapFeedback.setNodeText(firstChild, newText);
            assertEquals(newText, firstChild.getText());
        }

        @Test
        @DisplayName("setNodeColor changes node color")
        void setNodeColorChangesColor() {
            mapFeedback.setNodeColor(root, Color.RED);
            assertEquals(Color.RED, root.getColor());
        }

        @Test
        @DisplayName("setNodeBackgroundColor changes background color")
        void setNodeBackgroundColorChanges() {
            mapFeedback.setNodeBackgroundColor(firstChild, Color.YELLOW);
            assertEquals(Color.YELLOW, firstChild.getBackgroundColor());
        }

        @Test
        @DisplayName("setCloud enables cloud on non-root node")
        void setCloudEnables() {
            assertNull(firstChild.getCloud());
            mapFeedback.setCloud(firstChild, true);
            assertNotNull(firstChild.getCloud());
        }

        @Test
        @DisplayName("setCloud disables cloud")
        void setCloudDisables() {
            mapFeedback.setCloud(firstChild, true);
            mapFeedback.setCloud(firstChild, false);
            assertNull(firstChild.getCloud());
        }

        @Test
        @DisplayName("setFolded toggles folded state")
        void setFoldedToggles() {
            assertFalse(firstChild.isFolded());
            mapFeedback.setFolded(firstChild, true);
            assertTrue(firstChild.isFolded());
        }

        @Test
        @DisplayName("setLink sets hyperlink on node")
        void setLinkSetsHyperlink() {
            MindMapNode child1 = (MindMapNode) firstChild.getChildAt(0);
            String url = "https://example.com";
            mapFeedback.setLink(child1, url);
            assertEquals(url, child1.getLink());
        }

        @Test
        @DisplayName("multiple format operations compose correctly")
        void multipleFormatOperationsCompose() {
            mapFeedback.setBold(root, true);
            mapFeedback.setItalic(root, true);
            mapFeedback.setStrikethrough(root, true);
            mapFeedback.setFontSize(root, "32");

            assertTrue(root.isBold());
            assertTrue(root.isItalic());
            assertTrue(root.isStrikethrough());
            assertEquals("32", root.getFontSize());
        }

        @Test
        @DisplayName("setNoteText sets HTML note on node")
        void setNoteTextSetsNote() {
            MindMapNode child1 = (MindMapNode) firstChild.getChildAt(0);
            String noteHtml = "<html><body>Test note</body></html>";
            mapFeedback.setNoteText(child1, noteHtml);
            assertEquals(noteHtml, child1.getNoteText());
        }
    }

    // -----------------------------------------------------------------------
    // Format toggle tests (setBold, setItalic, setFontSize)
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("Format toggle via ExtendedMapFeedbackImpl")
    class FormatToggleTests {

        private ExtendedMapFeedbackImpl mapFeedback;
        private MindMapNode root;
        private MindMapNode targetNode;

        @BeforeEach
        void setUp() throws Exception {
            mapFeedback = new ExtendedMapFeedbackImpl();
            MindMapMapModel mMap = new MindMapMapModel(mapFeedback);
            mapFeedback.setMap(mMap);
            Tools.StringReaderCreator readerCreator = new Tools.StringReaderCreator(INITIAL_MAP);
            root = mMap.loadTree(readerCreator, MapAdapter.sDontAskInstance);
            mMap.setRoot(root);
            targetNode = (MindMapNode) root.getChildAt(0);
        }

        @Test
        @DisplayName("setBold toggles bold state on node")
        void setBoldTogglesState() {
            assertFalse(targetNode.isBold(), "Node should not be bold initially");

            mapFeedback.setBold(targetNode, true);
            assertTrue(targetNode.isBold(), "Node should be bold after setBold(true)");

            mapFeedback.setBold(targetNode, false);
            assertFalse(targetNode.isBold(), "Node should not be bold after setBold(false)");
        }

        @Test
        @DisplayName("setItalic toggles italic state on node")
        void setItalicTogglesState() {
            assertFalse(targetNode.isItalic(), "Node should not be italic initially");

            mapFeedback.setItalic(targetNode, true);
            assertTrue(targetNode.isItalic(), "Node should be italic after setItalic(true)");

            mapFeedback.setItalic(targetNode, false);
            assertFalse(targetNode.isItalic(), "Node should not be italic after setItalic(false)");
        }

        @Test
        @DisplayName("setFontSize changes and reverts font size on node")
        void setFontSizeChangesAndReverts() {
            String originalSize = targetNode.getFontSize();

            mapFeedback.setFontSize(targetNode, "24");
            assertEquals("24", targetNode.getFontSize(), "Font size should be 24 after set");

            mapFeedback.setFontSize(targetNode, "18");
            assertEquals("18", targetNode.getFontSize(), "Font size should be 18 after second set");

            // Revert to original
            if (originalSize != null) {
                mapFeedback.setFontSize(targetNode, originalSize);
                assertEquals(originalSize, targetNode.getFontSize(),
                        "Font size should revert to original value");
            }
        }
    }

    // -----------------------------------------------------------------------
    // Helper methods
    // -----------------------------------------------------------------------

    /**
     * Creates a minimal ExtendedMapFeedbackImpl with a map loaded.
     */
    private static ExtendedMapFeedbackImpl createFeedback() {
        ExtendedMapFeedbackImpl feedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(feedback);
        feedback.setMap(map);
        return feedback;
    }
}
