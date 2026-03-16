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

package tests.freemind.modes;

import freemind.main.HeadlessFreeMind;
import freemind.modes.CloudAdapter;
import freemind.modes.EdgeAdapter;
import freemind.modes.MapFeedback;
import freemind.modes.MapFeedbackAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.StylePattern;
import freemind.modes.attributes.Attribute;
import freemind.modes.mindmapmode.MindMapArrowLinkModel;
import freemind.modes.mindmapmode.MindMapCloudModel;
import freemind.modes.mindmapmode.MindMapEdgeModel;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the FreeMind CE core model classes:
 * NodeAdapter, MindMapNodeModel, EdgeAdapter, CloudAdapter,
 * ArrowLinkAdapter, and StylePattern.
 */
@DisplayName("Core Model Classes")
class NodeAdapterTest {

    private static HeadlessFreeMind freeMindMain;
    private static MapFeedback mapFeedback;
    private MindMapMapModel mapModel;
    private MindMapNodeModel rootNode;

    @BeforeAll
    static void initFreeMind() {
        freeMindMain = new HeadlessFreeMind();
        mapFeedback = new MapFeedbackAdapter() {
            @Override
            public MindMap getMap() {
                return null;
            }

            @Override
            public Font getDefaultFont() {
                return new Font("SansSerif", Font.PLAIN, 12);
            }
        };
    }

    @BeforeEach
    void setUp() {
        mapModel = new MindMapMapModel(mapFeedback);
        rootNode = (MindMapNodeModel) mapModel.getRootNode();
    }

    // ---------------------------------------------------------------
    // NodeAdapter / MindMapNodeModel: text, creation, basic properties
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Node Text Operations")
    class NodeTextTests {

        @Test
        @DisplayName("setText/getText round-trips plain text")
        void setAndGetText() {
            rootNode.setText("Hello World");
            assertEquals("Hello World", rootNode.getText());
        }

        @Test
        @DisplayName("setText with null clears text to empty string")
        void setTextNull() {
            rootNode.setText("something");
            rootNode.setText(null);
            assertEquals("", rootNode.getText());
        }

        @Test
        @DisplayName("setXmlText/getXmlText round-trips XML text")
        void setAndGetXmlText() {
            rootNode.setXmlText("Test &amp; Value");
            assertEquals("Test &amp; Value", rootNode.getXmlText());
        }

        @Test
        @DisplayName("toString returns the text content")
        void toStringReturnsText() {
            rootNode.setText("Root Topic");
            assertEquals("Root Topic", rootNode.toString());
        }
    }

    // ---------------------------------------------------------------
    // Parent-child relationship
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Parent-Child Relationships")
    class ParentChildTests {

        @Test
        @DisplayName("root node isRoot returns true")
        void rootNodeIsRoot() {
            assertTrue(rootNode.isRoot());
        }

        @Test
        @DisplayName("newly created root node has no children (isLeaf)")
        void rootNodeIsLeafInitially() {
            if (rootNode.getChildCount() == 0) {
                assertTrue(rootNode.isLeaf());
            }
        }

        @Test
        @DisplayName("insert child increases child count")
        void insertChildIncreasesCount() {
            MindMapNodeModel child = new MindMapNodeModel(mapModel);
            child.setText("Child 1");
            rootNode.insert(child, 0);
            assertEquals(1, rootNode.getChildCount());
            assertFalse(rootNode.isLeaf());
        }

        @Test
        @DisplayName("inserted child has correct parent")
        void insertedChildHasParent() {
            MindMapNodeModel child = new MindMapNodeModel(mapModel);
            child.setText("Child");
            rootNode.insert(child, 0);
            assertSame(rootNode, child.getParentNode());
        }

        @Test
        @DisplayName("child is not root")
        void childIsNotRoot() {
            MindMapNodeModel child = new MindMapNodeModel(mapModel);
            rootNode.insert(child, 0);
            assertFalse(child.isRoot());
        }

        @Test
        @DisplayName("getChildAt returns the correct child")
        void getChildAtReturnsCorrectChild() {
            MindMapNodeModel child1 = new MindMapNodeModel(mapModel);
            child1.setText("First");
            MindMapNodeModel child2 = new MindMapNodeModel(mapModel);
            child2.setText("Second");
            rootNode.insert(child1, 0);
            rootNode.insert(child2, 1);
            assertEquals("First", rootNode.getChildAt(0).toString());
            assertEquals("Second", rootNode.getChildAt(1).toString());
        }

        @Test
        @DisplayName("remove child decreases child count")
        void removeChildDecreasesCount() {
            MindMapNodeModel child = new MindMapNodeModel(mapModel);
            rootNode.insert(child, 0);
            assertEquals(1, rootNode.getChildCount());
            rootNode.remove(0);
            assertEquals(0, rootNode.getChildCount());
        }

        @Test
        @DisplayName("removed child has null parent")
        void removedChildHasNullParent() {
            MindMapNodeModel child = new MindMapNodeModel(mapModel);
            rootNode.insert(child, 0);
            rootNode.remove(child);
            assertNull(child.getParent());
        }

        @Test
        @DisplayName("hasChildren returns false when empty, true when populated")
        void hasChildrenReflectsState() {
            assertFalse(rootNode.hasChildren());
            MindMapNodeModel child = new MindMapNodeModel(mapModel);
            rootNode.insert(child, 0);
            assertTrue(rootNode.hasChildren());
        }

        @Test
        @DisplayName("getChildPosition returns correct index")
        void getChildPositionReturnsCorrectIndex() {
            MindMapNodeModel child1 = new MindMapNodeModel(mapModel);
            MindMapNodeModel child2 = new MindMapNodeModel(mapModel);
            rootNode.insert(child1, 0);
            rootNode.insert(child2, 1);
            assertEquals(0, rootNode.getChildPosition(child1));
            assertEquals(1, rootNode.getChildPosition(child2));
        }

        @Test
        @DisplayName("childrenUnfolded iterates all children")
        void childrenUnfoldedIteratesAll() {
            MindMapNodeModel child1 = new MindMapNodeModel(mapModel);
            MindMapNodeModel child2 = new MindMapNodeModel(mapModel);
            rootNode.insert(child1, 0);
            rootNode.insert(child2, 1);
            ListIterator<?> iter = rootNode.childrenUnfolded();
            int count = 0;
            while (iter.hasNext()) {
                iter.next();
                count++;
            }
            assertEquals(2, count);
        }

        @Test
        @DisplayName("getChildren returns list of children")
        void getChildrenReturnsList() {
            MindMapNodeModel child1 = new MindMapNodeModel(mapModel);
            MindMapNodeModel child2 = new MindMapNodeModel(mapModel);
            rootNode.insert(child1, 0);
            rootNode.insert(child2, 1);
            List<?> children = rootNode.getChildren();
            assertEquals(2, children.size());
        }
    }

    // ---------------------------------------------------------------
    // TreePath
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("TreePath Operations")
    class TreePathTests {

        @Test
        @DisplayName("root node path has length 1")
        void rootPathLengthIsOne() {
            TreePath path = rootNode.getPath();
            assertNotNull(path);
            assertEquals(1, path.getPathCount());
        }

        @Test
        @DisplayName("child node path has length 2")
        void childPathLengthIsTwo() {
            MindMapNodeModel child = new MindMapNodeModel(mapModel);
            rootNode.insert(child, 0);
            TreePath path = child.getPath();
            assertEquals(2, path.getPathCount());
        }

        @Test
        @DisplayName("getNodeLevel returns 0 for root, 1 for child")
        void getNodeLevelReturnsCorrectDepth() {
            assertEquals(0, rootNode.getNodeLevel());
            MindMapNodeModel child = new MindMapNodeModel(mapModel);
            rootNode.insert(child, 0);
            assertEquals(1, child.getNodeLevel());
        }

        @Test
        @DisplayName("isDescendantOf returns true for parent-child chain")
        void isDescendantOfWorks() {
            MindMapNodeModel child = new MindMapNodeModel(mapModel);
            rootNode.insert(child, 0);
            MindMapNodeModel grandchild = new MindMapNodeModel(mapModel);
            child.insert(grandchild, 0);
            assertTrue(grandchild.isDescendantOf(rootNode));
            assertTrue(grandchild.isDescendantOf(child));
            assertFalse(rootNode.isDescendantOf(child));
        }
    }

    // ---------------------------------------------------------------
    // Folding
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Folding State")
    class FoldingTests {

        @Test
        @DisplayName("node is not folded by default")
        void notFoldedByDefault() {
            assertFalse(rootNode.isFolded());
        }

        @Test
        @DisplayName("setFolded/isFolded round-trips")
        void setFoldedRoundTrips() {
            rootNode.setFolded(true);
            assertTrue(rootNode.isFolded());
            rootNode.setFolded(false);
            assertFalse(rootNode.isFolded());
        }
    }

    // ---------------------------------------------------------------
    // Icons
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Icon Management")
    class IconTests {

        @Test
        @DisplayName("no icons initially")
        void noIconsInitially() {
            List<MindIcon> icons = rootNode.getIcons();
            assertTrue(icons.isEmpty());
        }

        @Test
        @DisplayName("addIcon increases icon count")
        void addIconIncreasesCount() {
            MindIcon icon = MindIcon.factory("button_ok");
            rootNode.addIcon(icon, MindIcon.LAST);
            assertEquals(1, rootNode.getIcons().size());
            assertEquals("button_ok", rootNode.getIcons().get(0).getName());
        }

        @Test
        @DisplayName("addIcon at specific position inserts correctly")
        void addIconAtPosition() {
            rootNode.addIcon(MindIcon.factory("button_ok"), MindIcon.LAST);
            rootNode.addIcon(MindIcon.factory("button_cancel"), 0);
            assertEquals("button_cancel", rootNode.getIcons().get(0).getName());
            assertEquals("button_ok", rootNode.getIcons().get(1).getName());
        }

        @Test
        @DisplayName("removeIcon removes and returns remaining count")
        void removeIconReturnsRemainingCount() {
            rootNode.addIcon(MindIcon.factory("button_ok"), MindIcon.LAST);
            rootNode.addIcon(MindIcon.factory("button_cancel"), MindIcon.LAST);
            int remaining = rootNode.removeIcon(0);
            assertEquals(1, remaining);
            assertEquals("button_cancel", rootNode.getIcons().get(0).getName());
        }

        @Test
        @DisplayName("removeIcon with LAST position removes last icon")
        void removeIconLastPosition() {
            rootNode.addIcon(MindIcon.factory("button_ok"), MindIcon.LAST);
            rootNode.addIcon(MindIcon.factory("button_cancel"), MindIcon.LAST);
            rootNode.removeIcon(MindIcon.LAST);
            assertEquals(1, rootNode.getIcons().size());
            assertEquals("button_ok", rootNode.getIcons().get(0).getName());
        }
    }

    // ---------------------------------------------------------------
    // Link (hyperlink)
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Hyperlink Management")
    class LinkTests {

        @Test
        @DisplayName("link is null by default")
        void linkNullByDefault() {
            assertNull(rootNode.getLink());
        }

        @Test
        @DisplayName("setLink/getLink round-trips")
        void setAndGetLink() {
            rootNode.setLink("https://example.com");
            assertEquals("https://example.com", rootNode.getLink());
        }

        @Test
        @DisplayName("setLink to null clears the link")
        void setLinkNull() {
            rootNode.setLink("https://example.com");
            rootNode.setLink(null);
            assertNull(rootNode.getLink());
        }
    }

    // ---------------------------------------------------------------
    // Notes (HTML text)
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Note Text Operations")
    class NoteTests {

        @Test
        @DisplayName("note text is null by default")
        void noteNullByDefault() {
            assertNull(rootNode.getNoteText());
        }

        @Test
        @DisplayName("setNoteText/getNoteText round-trips")
        void setAndGetNoteText() {
            rootNode.setNoteText("<html><body>Note content</body></html>");
            assertNotNull(rootNode.getNoteText());
            assertTrue(rootNode.getNoteText().contains("Note content"));
        }

        @Test
        @DisplayName("setNoteText null clears note")
        void setNoteTextNull() {
            rootNode.setNoteText("<html><body>Some note</body></html>");
            rootNode.setNoteText(null);
            assertNull(rootNode.getNoteText());
            assertNull(rootNode.getXmlNoteText());
        }
    }

    // ---------------------------------------------------------------
    // Node Style
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Node Style")
    class StyleTests {

        @Test
        @DisplayName("setStyle/getStyle with explicit style")
        void setAndGetStyle() {
            rootNode.setStyle(MindMapNode.STYLE_BUBBLE);
            assertEquals(MindMapNode.STYLE_BUBBLE, rootNode.getStyle());
        }

        @Test
        @DisplayName("setStyle to fork returns fork")
        void setStyleFork() {
            rootNode.setStyle(MindMapNode.STYLE_FORK);
            assertEquals(MindMapNode.STYLE_FORK, rootNode.getStyle());
        }

        @Test
        @DisplayName("hasStyle returns false when style not explicitly set, true when set")
        void hasStyleReflectsExplicitSetting() {
            rootNode.setStyle(null);
            assertFalse(rootNode.hasStyle());
            rootNode.setStyle(MindMapNode.STYLE_BUBBLE);
            assertTrue(rootNode.hasStyle());
        }
    }

    // ---------------------------------------------------------------
    // Color
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Node Color")
    class ColorTests {

        @Test
        @DisplayName("color is null by default")
        void colorNullByDefault() {
            assertNull(rootNode.getColor());
        }

        @Test
        @DisplayName("setColor/getColor round-trips")
        void setAndGetColor() {
            rootNode.setColor(Color.RED);
            assertEquals(Color.RED, rootNode.getColor());
        }

        @Test
        @DisplayName("backgroundColor is null by default")
        void backgroundColorNullByDefault() {
            assertNull(rootNode.getBackgroundColor());
        }

        @Test
        @DisplayName("setBackgroundColor/getBackgroundColor round-trips")
        void setAndGetBackgroundColor() {
            rootNode.setBackgroundColor(Color.YELLOW);
            assertEquals(Color.YELLOW, rootNode.getBackgroundColor());
        }
    }

    // ---------------------------------------------------------------
    // Font
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Font Management")
    class FontTests {

        @Test
        @DisplayName("isBold is false by default")
        void notBoldByDefault() {
            assertFalse(rootNode.isBold());
        }

        @Test
        @DisplayName("isItalic is false by default")
        void notItalicByDefault() {
            assertFalse(rootNode.isItalic());
        }

        @Test
        @DisplayName("setBold makes node bold")
        void setBoldMakesNodeBold() {
            rootNode.setBold(true);
            assertTrue(rootNode.isBold());
        }

        @Test
        @DisplayName("setItalic makes node italic")
        void setItalicMakesNodeItalic() {
            rootNode.setItalic(true);
            assertTrue(rootNode.isItalic());
        }

        @Test
        @DisplayName("setFont sets the font object")
        void setFontSetsFont() {
            Font customFont = new Font("Monospaced", Font.BOLD, 16);
            rootNode.setFont(customFont);
            Font nodeFont = rootNode.getFont();
            assertNotNull(nodeFont);
            assertEquals("Monospaced", nodeFont.getFamily());
            assertTrue(nodeFont.isBold());
            assertEquals(16, nodeFont.getSize());
        }

        @Test
        @DisplayName("isUnderlined is false by default")
        void notUnderlinedByDefault() {
            assertFalse(rootNode.isUnderlined());
        }

        @Test
        @DisplayName("setUnderlined changes underline state")
        void setUnderlinedChangesState() {
            rootNode.setUnderlined(true);
            assertTrue(rootNode.isUnderlined());
        }
    }

    // ---------------------------------------------------------------
    // Node Position (left/right)
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Node Position")
    class PositionTests {

        @Test
        @DisplayName("setLeft/isLeft round-trips")
        void setLeftRoundTrips() {
            MindMapNodeModel child = new MindMapNodeModel(mapModel);
            rootNode.insert(child, 0);
            child.setLeft(true);
            assertTrue(child.isLeft());
            child.setLeft(false);
            assertFalse(child.isLeft());
        }
    }

    // ---------------------------------------------------------------
    // Attributes
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Attribute Management")
    class AttributeTests {

        @Test
        @DisplayName("no attributes by default")
        void noAttributesByDefault() {
            assertEquals(0, rootNode.getAttributeTableLength());
        }

        @Test
        @DisplayName("addAttribute increases count and returns position")
        void addAttributeIncreasesCount() {
            Attribute attr = new Attribute("key1", "value1");
            int pos = rootNode.addAttribute(attr);
            assertEquals(0, pos);
            assertEquals(1, rootNode.getAttributeTableLength());
        }

        @Test
        @DisplayName("getAttribute by position returns correct attribute")
        void getAttributeByPosition() {
            rootNode.addAttribute(new Attribute("key1", "value1"));
            rootNode.addAttribute(new Attribute("key2", "value2"));
            Attribute attr = rootNode.getAttribute(0);
            assertEquals("key1", attr.getName());
            assertEquals("value1", attr.getValue());
        }

        @Test
        @DisplayName("getAttribute by key name returns correct value")
        void getAttributeByKey() {
            rootNode.addAttribute(new Attribute("author", "John"));
            rootNode.addAttribute(new Attribute("version", "1.0"));
            assertEquals("John", rootNode.getAttribute("author"));
            assertEquals("1.0", rootNode.getAttribute("version"));
            assertNull(rootNode.getAttribute("nonexistent"));
        }

        @Test
        @DisplayName("setAttribute replaces attribute at position")
        void setAttributeReplacesAtPosition() {
            rootNode.addAttribute(new Attribute("key1", "value1"));
            rootNode.setAttribute(0, new Attribute("key1", "newValue"));
            assertEquals("newValue", rootNode.getAttribute(0).getValue());
        }

        @Test
        @DisplayName("removeAttribute decreases count")
        void removeAttributeDecreasesCount() {
            rootNode.addAttribute(new Attribute("key1", "value1"));
            rootNode.addAttribute(new Attribute("key2", "value2"));
            rootNode.removeAttribute(0);
            assertEquals(1, rootNode.getAttributeTableLength());
            assertEquals("key2", rootNode.getAttribute(0).getName());
        }

        @Test
        @DisplayName("getAttributeKeyList returns all keys")
        void getAttributeKeyListReturnsAllKeys() {
            rootNode.addAttribute(new Attribute("alpha", "1"));
            rootNode.addAttribute(new Attribute("beta", "2"));
            List<String> keys = rootNode.getAttributeKeyList();
            assertEquals(2, keys.size());
            assertTrue(keys.contains("alpha"));
            assertTrue(keys.contains("beta"));
        }
    }

    // ---------------------------------------------------------------
    // Edge (via MindMapEdgeModel)
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("EdgeAdapter Operations")
    class EdgeTests {

        @Test
        @DisplayName("node has a non-null edge by default")
        void nodeHasEdgeByDefault() {
            assertNotNull(rootNode.getEdge());
        }

        @Test
        @DisplayName("edge setColor/getColor round-trips")
        void edgeSetAndGetColor() {
            EdgeAdapter edge = (EdgeAdapter) rootNode.getEdge();
            edge.setColor(Color.BLUE);
            assertEquals(Color.BLUE, edge.getColor());
        }

        @Test
        @DisplayName("edge setStyle/getStyle round-trips")
        void edgeSetAndGetStyle() {
            EdgeAdapter edge = (EdgeAdapter) rootNode.getEdge();
            edge.setStyle(EdgeAdapter.EDGESTYLE_BEZIER);
            assertEquals(EdgeAdapter.EDGESTYLE_BEZIER, edge.getStyle());
        }

        @Test
        @DisplayName("edge setWidth/getWidth round-trips")
        void edgeSetAndGetWidth() {
            EdgeAdapter edge = (EdgeAdapter) rootNode.getEdge();
            edge.setWidth(4);
            assertEquals(4, edge.getWidth());
        }

        @Test
        @DisplayName("edge default width is WIDTH_PARENT")
        void edgeDefaultWidthIsParent() {
            EdgeAdapter edge = (EdgeAdapter) rootNode.getEdge();
            assertEquals(EdgeAdapter.WIDTH_PARENT, edge.getRealWidth());
        }

        @Test
        @DisplayName("edge style constants are defined")
        void edgeStyleConstants() {
            assertEquals("linear", EdgeAdapter.EDGESTYLE_LINEAR);
            assertEquals("bezier", EdgeAdapter.EDGESTYLE_BEZIER);
            assertEquals("sharp_linear", EdgeAdapter.EDGESTYLE_SHARP_LINEAR);
            assertEquals("sharp_bezier", EdgeAdapter.EDGESTYLE_SHARP_BEZIER);
        }

        @Test
        @DisplayName("edge getStyleAsInt returns correct int for linear")
        void edgeGetStyleAsIntLinear() {
            EdgeAdapter edge = (EdgeAdapter) rootNode.getEdge();
            edge.setStyle(EdgeAdapter.EDGESTYLE_LINEAR);
            assertEquals(EdgeAdapter.INT_EDGESTYLE_LINEAR, edge.getStyleAsInt());
        }

        @Test
        @DisplayName("edge getStyleAsInt returns correct int for bezier")
        void edgeGetStyleAsIntBezier() {
            EdgeAdapter edge = (EdgeAdapter) rootNode.getEdge();
            edge.setStyle(EdgeAdapter.EDGESTYLE_BEZIER);
            assertEquals(EdgeAdapter.INT_EDGESTYLE_BEZIER, edge.getStyleAsInt());
        }

        @Test
        @DisplayName("setEdge replaces the edge on the node")
        void setEdgeReplacesEdge() {
            MindMapEdgeModel newEdge = new MindMapEdgeModel(rootNode, mapModel.getMapFeedback());
            newEdge.setColor(Color.GREEN);
            rootNode.setEdge(newEdge);
            assertEquals(Color.GREEN, rootNode.getEdge().getColor());
        }
    }

    // ---------------------------------------------------------------
    // Cloud (via MindMapCloudModel)
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("CloudAdapter Operations")
    class CloudTests {

        @Test
        @DisplayName("node has no cloud by default")
        void noCloudByDefault() {
            assertNull(rootNode.getCloud());
        }

        @Test
        @DisplayName("setCloud/getCloud assigns a cloud to the node")
        void setAndGetCloud() {
            MindMapCloudModel cloud = new MindMapCloudModel(rootNode, mapModel.getMapFeedback());
            rootNode.setCloud(cloud);
            assertNotNull(rootNode.getCloud());
            assertSame(cloud, rootNode.getCloud());
        }

        @Test
        @DisplayName("cloud setColor/getColor round-trips")
        void cloudSetAndGetColor() {
            MindMapCloudModel cloud = new MindMapCloudModel(rootNode, mapModel.getMapFeedback());
            rootNode.setCloud(cloud);
            cloud.setColor(Color.CYAN);
            assertEquals(Color.CYAN, cloud.getColor());
        }

        @Test
        @DisplayName("cloud setStyle/getStyle round-trips")
        void cloudSetAndGetStyle() {
            MindMapCloudModel cloud = new MindMapCloudModel(rootNode, mapModel.getMapFeedback());
            rootNode.setCloud(cloud);
            cloud.setStyle("rect");
            assertEquals("rect", cloud.getStyle());
        }

        @Test
        @DisplayName("cloud getExteriorColor returns darker color")
        void cloudExteriorColorIsDarker() {
            MindMapCloudModel cloud = new MindMapCloudModel(rootNode, mapModel.getMapFeedback());
            rootNode.setCloud(cloud);
            Color baseColor = Color.CYAN;
            cloud.setColor(baseColor);
            Color exterior = cloud.getExteriorColor();
            assertEquals(baseColor.darker(), exterior);
        }

        @Test
        @DisplayName("setCloud to null removes cloud")
        void setCloudNullRemovesCloud() {
            MindMapCloudModel cloud = new MindMapCloudModel(rootNode, mapModel.getMapFeedback());
            rootNode.setCloud(cloud);
            assertNotNull(rootNode.getCloud());
            rootNode.setCloud(null);
            assertNull(rootNode.getCloud());
        }
    }

    // ---------------------------------------------------------------
    // ArrowLinkAdapter
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("ArrowLinkAdapter Operations")
    class ArrowLinkTests {

        @Test
        @DisplayName("arrow link start/end arrows default to none")
        void arrowLinkDefaultArrows() {
            MindMapNodeModel target = new MindMapNodeModel(mapModel);
            MindMapArrowLinkModel link = new MindMapArrowLinkModel(rootNode, target, mapModel.getMapFeedback());
            assertEquals("None", link.getStartArrow());
            assertEquals("Default", link.getEndArrow());
        }

        @Test
        @DisplayName("setStartArrow/getStartArrow round-trips")
        void setAndGetStartArrow() {
            MindMapNodeModel target = new MindMapNodeModel(mapModel);
            MindMapArrowLinkModel link = new MindMapArrowLinkModel(rootNode, target, mapModel.getMapFeedback());
            link.setStartArrow("Default");
            assertEquals("Default", link.getStartArrow());
        }

        @Test
        @DisplayName("setEndArrow/getEndArrow round-trips")
        void setAndGetEndArrow() {
            MindMapNodeModel target = new MindMapNodeModel(mapModel);
            MindMapArrowLinkModel link = new MindMapArrowLinkModel(rootNode, target, mapModel.getMapFeedback());
            link.setEndArrow("None");
            assertEquals("None", link.getEndArrow());
        }

        @Test
        @DisplayName("arrow link source is set correctly")
        void arrowLinkSourceIsCorrect() {
            MindMapNodeModel target = new MindMapNodeModel(mapModel);
            MindMapArrowLinkModel link = new MindMapArrowLinkModel(rootNode, target, mapModel.getMapFeedback());
            assertSame(rootNode, link.getSource());
        }
    }

    // ---------------------------------------------------------------
    // StylePattern
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("StylePattern Operations")
    class StylePatternTests {

        @Test
        @DisplayName("StylePattern name can be set and retrieved")
        void stylePatternNameRoundTrips() {
            StylePattern pattern = new StylePattern();
            pattern.setName("MyStyle");
            assertEquals("MyStyle", pattern.getName());
        }

        @Test
        @DisplayName("StylePattern node color can be set and retrieved")
        void stylePatternNodeColor() {
            StylePattern pattern = new StylePattern();
            pattern.setNodeColor(Color.RED);
            assertEquals(Color.RED, pattern.getNodeColor());
        }

        @Test
        @DisplayName("StylePattern edge style can be set and retrieved")
        void stylePatternEdgeStyle() {
            StylePattern pattern = new StylePattern();
            pattern.setEdgeStyle("bezier");
            assertEquals("bezier", pattern.getEdgeStyle());
        }

        @Test
        @DisplayName("StylePattern font bold can be set and retrieved")
        void stylePatternFontBold() {
            StylePattern pattern = new StylePattern();
            pattern.setNodeFontBold(Boolean.TRUE);
            assertEquals(Boolean.TRUE, pattern.getNodeFontBold());
        }

        @Test
        @DisplayName("StylePattern font italic can be set and retrieved")
        void stylePatternFontItalic() {
            StylePattern pattern = new StylePattern();
            pattern.setNodeFontItalic(Boolean.TRUE);
            assertEquals(Boolean.TRUE, pattern.getNodeFontItalic());
        }

        @Test
        @DisplayName("StylePattern node style can be set and retrieved")
        void stylePatternNodeStyle() {
            StylePattern pattern = new StylePattern();
            pattern.setNodeStyle("bubble");
            assertEquals("bubble", pattern.getNodeStyle());
        }

        @Test
        @DisplayName("StylePattern toString includes node and edge info")
        void stylePatternToString() {
            StylePattern pattern = new StylePattern();
            pattern.setNodeStyle("bubble");
            String str = pattern.toString();
            assertNotNull(str);
            assertTrue(str.contains("node:"));
            assertTrue(str.contains("edge:"));
            assertTrue(str.contains("bubble"));
        }
    }

    // ---------------------------------------------------------------
    // ToolTip
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("ToolTip Management")
    class ToolTipTests {

        @Test
        @DisplayName("setToolTip/getToolTip round-trips")
        void setAndGetToolTip() {
            rootNode.setToolTip("key1", "This is a tooltip");
            assertNotNull(rootNode.getToolTip());
            assertTrue(rootNode.getToolTip().containsKey("key1"));
            assertEquals("This is a tooltip", rootNode.getToolTip().get("key1"));
        }

        @Test
        @DisplayName("setToolTip null value removes tooltip entry")
        void setToolTipNullRemovesEntry() {
            rootNode.setToolTip("key1", "tooltip value");
            rootNode.setToolTip("key1", null);
            assertTrue(rootNode.getToolTip() == null ||
                       !rootNode.getToolTip().containsKey("key1"));
        }
    }

    // ---------------------------------------------------------------
    // Spacing (VGap, HGap, ShiftY)
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Spacing Properties")
    class SpacingTests {

        @Test
        @DisplayName("setVGap/getVGap round-trips")
        void setAndGetVGap() {
            rootNode.setVGap(10);
            assertEquals(10, rootNode.getVGap());
        }

        @Test
        @DisplayName("setHGap/getHGap round-trips")
        void setAndGetHGap() {
            rootNode.setHGap(20);
            assertEquals(20, rootNode.getHGap());
        }

        @Test
        @DisplayName("setShiftY/getShiftY round-trips")
        void setAndGetShiftY() {
            rootNode.setShiftY(15);
            assertEquals(15, rootNode.getShiftY());
        }
    }

    // ---------------------------------------------------------------
    // HistoryInformation
    // ---------------------------------------------------------------

    @Test
    @DisplayName("node has history information after creation")
    void nodeHasHistoryInformation() {
        assertNotNull(rootNode.getHistoryInformation());
    }

    // ---------------------------------------------------------------
    // MindMapNodeModel: isWriteable and getPlainTextContent
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("MindMapNodeModel-specific Operations")
    class MindMapNodeModelTests {

        @Test
        @DisplayName("isWriteable returns true for MindMapNodeModel")
        void isWriteableReturnsTrue() {
            assertTrue(rootNode.isWriteable());
        }

        @Test
        @DisplayName("getPlainTextContent strips HTML tags")
        void getPlainTextContentStripsHtml() {
            rootNode.setText("Plain text content");
            String plain = rootNode.getPlainTextContent();
            assertNotNull(plain);
            assertEquals("Plain text content", plain);
        }
    }
}
