
package freemind.controller.actions.generated.instance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *     &lt;extension base="{}format_node_action"&gt;
 *       &lt;attribute name="style" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "node_style_format_action")
public class NodeStyleFormatAction
    extends FormatNodeAction
{

    @XmlAttribute(name = "style")
    protected String style;

    /**
     * Gets the value of the style property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStyle() {
        return style;
    }

    /**
     * Sets the value of the style property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStyle(String value) {
        this.style = value;
    }

}
