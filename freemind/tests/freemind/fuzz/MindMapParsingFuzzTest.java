/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2026  Christian Foltin, Dimitry Polivaev and others.
 *
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

package tests.freemind.fuzz;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import freemind.common.XmlBindingTools;
import freemind.main.HeadlessFreeMind;
import freemind.main.XMLElement;
import freemind.main.XMLParseException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Fuzz tests for FreeMind CE's XML parsing and JAXB unmarshalling.
 *
 * <p>These tests use Jazzer (via JUnit integration) to find crashes,
 * hangs, and security issues in the .mm file parser and action XML
 * unmarshaller.</p>
 */
public class MindMapParsingFuzzTest {

    static {
        // Initialize HeadlessFreeMind so Resources.getInstance() works
        new HeadlessFreeMind();
    }

    /**
     * Fuzz the core XMLElement parser used to load .mm mind map files.
     * This is the lowest-level XML parser in FreeMind — all .mm files
     * pass through parseFromReader().
     */
    @FuzzTest(maxDuration = "5m")
    public void fuzzXmlElementParsing(FuzzedDataProvider data) {
        String input = data.consumeRemainingAsString();
        XMLElement element = new XMLElement();
        try {
            element.parseFromReader(new StringReader(input));
        } catch (XMLParseException | IOException | IllegalArgumentException e) {
            // Expected for malformed input
        }
    }

    /**
     * Fuzz the JAXB unmarshaller used for action/undo XML.
     * XmlBindingTools.unMarshall() processes action descriptors
     * that control undo/redo, patterns, and node modifications.
     */
    @FuzzTest(maxDuration = "5m")
    public void fuzzJaxbUnmarshalling(FuzzedDataProvider data) {
        String input = data.consumeRemainingAsString();
        try {
            XmlBindingTools.getInstance().unMarshall(input);
        } catch (Exception e) {
            // Expected for malformed input
        }
    }

    /**
     * Fuzz with structured .mm-like XML to exercise deeper parsing paths.
     * Generates semi-valid mind map XML with fuzzed attribute values.
     */
    @FuzzTest(maxDuration = "5m")
    public void fuzzStructuredMindMapXml(FuzzedDataProvider data) {
        String text = data.consumeString(200);
        String link = data.consumeString(100);
        String color = data.consumeString(20);
        String position = data.consumeBoolean() ? "right" : "left";
        boolean folded = data.consumeBoolean();
        int extraChildren = data.consumeInt(0, 5);

        StringBuilder sb = new StringBuilder();
        sb.append("<map version=\"1.0.1\">");
        sb.append("<node TEXT=\"").append(escapeXml(text)).append("\"");
        sb.append(" LINK=\"").append(escapeXml(link)).append("\"");
        sb.append(" COLOR=\"").append(escapeXml(color)).append("\"");
        sb.append(" POSITION=\"").append(position).append("\"");
        sb.append(" FOLDED=\"").append(folded).append("\">");

        for (int i = 0; i < extraChildren; i++) {
            String childText = data.consumeString(50);
            sb.append("<node TEXT=\"").append(escapeXml(childText)).append("\"/>");
        }

        sb.append("</node></map>");

        XMLElement element = new XMLElement();
        try {
            element.parseFromReader(new StringReader(sb.toString()));
        } catch (XMLParseException | IOException | IllegalArgumentException e) {
            // Expected for edge-case attribute values
        }
    }

    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
