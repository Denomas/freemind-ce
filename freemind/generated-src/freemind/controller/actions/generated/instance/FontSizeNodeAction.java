
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
 *       &lt;attribute name="size" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "font_size_node_action")
public class FontSizeNodeAction
    extends FormatNodeAction
{

    @XmlAttribute(name = "size", required = true)
    protected String size;

    /**
     * Gets the value of the size property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSize(String value) {
        this.size = value;
    }

}
