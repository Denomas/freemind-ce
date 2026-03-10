
package freemind.controller.actions.generated.instance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pattern_property_base complex type</p>.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType name="pattern_property_base"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pattern_property_base")
@XmlSeeAlso({
    PatternScript.class,
    PatternChild.class,
    PatternEdgeWidth.class,
    PatternEdgeStyle.class,
    PatternEdgeColor.class,
    PatternIcon.class,
    PatternNodeFontSize.class,
    PatternNodeFontItalic.class,
    PatternNodeFontStrikethrough.class,
    PatternNodeFontBold.class,
    PatternNodeFontName.class,
    PatternNodeText.class,
    PatternNodeStyle.class,
    PatternNodeColor.class,
    PatternNodeBackgroundColor.class
})
public class PatternPropertyBase {

    @XmlAttribute(name = "value")
    protected String value;

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

}
