
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
 *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "edge_width_format_action")
public class EdgeWidthFormatAction
    extends FormatNodeAction
{

    @XmlAttribute(name = "width")
    protected Integer width;

    /**
     * Gets the value of the width property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setWidth(Integer value) {
        this.width = value;
    }

}
