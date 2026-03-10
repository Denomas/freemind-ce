
package freemind.controller.actions.generated.instance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type</p>.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{}xml_action"&gt;
 *       &lt;choice&gt;
 *         &lt;element ref="{}pattern_node_background_color" minOccurs="0"/&gt;
 *         &lt;element ref="{}pattern_node_color" minOccurs="0"/&gt;
 *         &lt;element ref="{}pattern_node_style" minOccurs="0"/&gt;
 *         &lt;element ref="{}pattern_node_text" minOccurs="0"/&gt;
 *         &lt;element ref="{}pattern_node_font_name" minOccurs="0"/&gt;
 *         &lt;element ref="{}pattern_node_font_bold" minOccurs="0"/&gt;
 *         &lt;element ref="{}pattern_node_font_strikethrough" minOccurs="0"/&gt;
 *         &lt;element ref="{}pattern_node_font_italic" minOccurs="0"/&gt;
 *         &lt;element ref="{}pattern_node_font_size" minOccurs="0"/&gt;
 *         &lt;element ref="{}pattern_icon" minOccurs="0"/&gt;
 *         &lt;element ref="{}pattern_edge_color" minOccurs="0"/&gt;
 *         &lt;element ref="{}pattern_edge_style" minOccurs="0"/&gt;
 *         &lt;element ref="{}pattern_edge_width" minOccurs="0"/&gt;
 *         &lt;element ref="{}pattern_child" minOccurs="0"/&gt;
 *         &lt;element ref="{}pattern_script" minOccurs="0"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="original_name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "patternNodeBackgroundColor",
    "patternNodeColor",
    "patternNodeStyle",
    "patternNodeText",
    "patternNodeFontName",
    "patternNodeFontBold",
    "patternNodeFontStrikethrough",
    "patternNodeFontItalic",
    "patternNodeFontSize",
    "patternIcon",
    "patternEdgeColor",
    "patternEdgeStyle",
    "patternEdgeWidth",
    "patternChild",
    "patternScript"
})
@XmlRootElement(name = "pattern")
public class Pattern
    extends XmlAction
{

    @XmlElement(name = "pattern_node_background_color")
    protected PatternNodeBackgroundColor patternNodeBackgroundColor;
    @XmlElement(name = "pattern_node_color")
    protected PatternNodeColor patternNodeColor;
    @XmlElement(name = "pattern_node_style")
    protected PatternNodeStyle patternNodeStyle;
    @XmlElement(name = "pattern_node_text")
    protected PatternNodeText patternNodeText;
    @XmlElement(name = "pattern_node_font_name")
    protected PatternNodeFontName patternNodeFontName;
    @XmlElement(name = "pattern_node_font_bold")
    protected PatternNodeFontBold patternNodeFontBold;
    @XmlElement(name = "pattern_node_font_strikethrough")
    protected PatternNodeFontStrikethrough patternNodeFontStrikethrough;
    @XmlElement(name = "pattern_node_font_italic")
    protected PatternNodeFontItalic patternNodeFontItalic;
    @XmlElement(name = "pattern_node_font_size")
    protected PatternNodeFontSize patternNodeFontSize;
    @XmlElement(name = "pattern_icon")
    protected PatternIcon patternIcon;
    @XmlElement(name = "pattern_edge_color")
    protected PatternEdgeColor patternEdgeColor;
    @XmlElement(name = "pattern_edge_style")
    protected PatternEdgeStyle patternEdgeStyle;
    @XmlElement(name = "pattern_edge_width")
    protected PatternEdgeWidth patternEdgeWidth;
    @XmlElement(name = "pattern_child")
    protected PatternChild patternChild;
    @XmlElement(name = "pattern_script")
    protected PatternScript patternScript;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "original_name")
    protected String originalName;

    /**
     * Gets the value of the patternNodeBackgroundColor property.
     *
     * @return
     *     possible object is
     *     {@link PatternNodeBackgroundColor }
     *
     */
    public PatternNodeBackgroundColor getPatternNodeBackgroundColor() {
        return patternNodeBackgroundColor;
    }

    /**
     * Sets the value of the patternNodeBackgroundColor property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternNodeBackgroundColor }
     *
     */
    public void setPatternNodeBackgroundColor(PatternNodeBackgroundColor value) {
        this.patternNodeBackgroundColor = value;
    }

    /**
     * Gets the value of the patternNodeColor property.
     *
     * @return
     *     possible object is
     *     {@link PatternNodeColor }
     *
     */
    public PatternNodeColor getPatternNodeColor() {
        return patternNodeColor;
    }

    /**
     * Sets the value of the patternNodeColor property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternNodeColor }
     *
     */
    public void setPatternNodeColor(PatternNodeColor value) {
        this.patternNodeColor = value;
    }

    /**
     * Gets the value of the patternNodeStyle property.
     *
     * @return
     *     possible object is
     *     {@link PatternNodeStyle }
     *
     */
    public PatternNodeStyle getPatternNodeStyle() {
        return patternNodeStyle;
    }

    /**
     * Sets the value of the patternNodeStyle property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternNodeStyle }
     *
     */
    public void setPatternNodeStyle(PatternNodeStyle value) {
        this.patternNodeStyle = value;
    }

    /**
     * Gets the value of the patternNodeText property.
     *
     * @return
     *     possible object is
     *     {@link PatternNodeText }
     *
     */
    public PatternNodeText getPatternNodeText() {
        return patternNodeText;
    }

    /**
     * Sets the value of the patternNodeText property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternNodeText }
     *
     */
    public void setPatternNodeText(PatternNodeText value) {
        this.patternNodeText = value;
    }

    /**
     * Gets the value of the patternNodeFontName property.
     *
     * @return
     *     possible object is
     *     {@link PatternNodeFontName }
     *
     */
    public PatternNodeFontName getPatternNodeFontName() {
        return patternNodeFontName;
    }

    /**
     * Sets the value of the patternNodeFontName property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternNodeFontName }
     *
     */
    public void setPatternNodeFontName(PatternNodeFontName value) {
        this.patternNodeFontName = value;
    }

    /**
     * Gets the value of the patternNodeFontBold property.
     *
     * @return
     *     possible object is
     *     {@link PatternNodeFontBold }
     *
     */
    public PatternNodeFontBold getPatternNodeFontBold() {
        return patternNodeFontBold;
    }

    /**
     * Sets the value of the patternNodeFontBold property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternNodeFontBold }
     *
     */
    public void setPatternNodeFontBold(PatternNodeFontBold value) {
        this.patternNodeFontBold = value;
    }

    /**
     * Gets the value of the patternNodeFontStrikethrough property.
     *
     * @return
     *     possible object is
     *     {@link PatternNodeFontStrikethrough }
     *
     */
    public PatternNodeFontStrikethrough getPatternNodeFontStrikethrough() {
        return patternNodeFontStrikethrough;
    }

    /**
     * Sets the value of the patternNodeFontStrikethrough property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternNodeFontStrikethrough }
     *
     */
    public void setPatternNodeFontStrikethrough(PatternNodeFontStrikethrough value) {
        this.patternNodeFontStrikethrough = value;
    }

    /**
     * Gets the value of the patternNodeFontItalic property.
     *
     * @return
     *     possible object is
     *     {@link PatternNodeFontItalic }
     *
     */
    public PatternNodeFontItalic getPatternNodeFontItalic() {
        return patternNodeFontItalic;
    }

    /**
     * Sets the value of the patternNodeFontItalic property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternNodeFontItalic }
     *
     */
    public void setPatternNodeFontItalic(PatternNodeFontItalic value) {
        this.patternNodeFontItalic = value;
    }

    /**
     * Gets the value of the patternNodeFontSize property.
     *
     * @return
     *     possible object is
     *     {@link PatternNodeFontSize }
     *
     */
    public PatternNodeFontSize getPatternNodeFontSize() {
        return patternNodeFontSize;
    }

    /**
     * Sets the value of the patternNodeFontSize property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternNodeFontSize }
     *
     */
    public void setPatternNodeFontSize(PatternNodeFontSize value) {
        this.patternNodeFontSize = value;
    }

    /**
     * Gets the value of the patternIcon property.
     *
     * @return
     *     possible object is
     *     {@link PatternIcon }
     *
     */
    public PatternIcon getPatternIcon() {
        return patternIcon;
    }

    /**
     * Sets the value of the patternIcon property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternIcon }
     *
     */
    public void setPatternIcon(PatternIcon value) {
        this.patternIcon = value;
    }

    /**
     * Gets the value of the patternEdgeColor property.
     *
     * @return
     *     possible object is
     *     {@link PatternEdgeColor }
     *
     */
    public PatternEdgeColor getPatternEdgeColor() {
        return patternEdgeColor;
    }

    /**
     * Sets the value of the patternEdgeColor property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternEdgeColor }
     *
     */
    public void setPatternEdgeColor(PatternEdgeColor value) {
        this.patternEdgeColor = value;
    }

    /**
     * Gets the value of the patternEdgeStyle property.
     *
     * @return
     *     possible object is
     *     {@link PatternEdgeStyle }
     *
     */
    public PatternEdgeStyle getPatternEdgeStyle() {
        return patternEdgeStyle;
    }

    /**
     * Sets the value of the patternEdgeStyle property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternEdgeStyle }
     *
     */
    public void setPatternEdgeStyle(PatternEdgeStyle value) {
        this.patternEdgeStyle = value;
    }

    /**
     * Gets the value of the patternEdgeWidth property.
     *
     * @return
     *     possible object is
     *     {@link PatternEdgeWidth }
     *
     */
    public PatternEdgeWidth getPatternEdgeWidth() {
        return patternEdgeWidth;
    }

    /**
     * Sets the value of the patternEdgeWidth property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternEdgeWidth }
     *
     */
    public void setPatternEdgeWidth(PatternEdgeWidth value) {
        this.patternEdgeWidth = value;
    }

    /**
     * Gets the value of the patternChild property.
     *
     * @return
     *     possible object is
     *     {@link PatternChild }
     *
     */
    public PatternChild getPatternChild() {
        return patternChild;
    }

    /**
     * Sets the value of the patternChild property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternChild }
     *
     */
    public void setPatternChild(PatternChild value) {
        this.patternChild = value;
    }

    /**
     * Gets the value of the patternScript property.
     *
     * @return
     *     possible object is
     *     {@link PatternScript }
     *
     */
    public PatternScript getPatternScript() {
        return patternScript;
    }

    /**
     * Sets the value of the patternScript property.
     *
     * @param value
     *     allowed object is
     *     {@link PatternScript }
     *
     */
    public void setPatternScript(PatternScript value) {
        this.patternScript = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the originalName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * Sets the value of the originalName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOriginalName(String value) {
        this.originalName = value;
    }

}
