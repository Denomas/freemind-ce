
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
 *     &lt;extension base="{}node_action"&gt;
 *       &lt;attribute name="destination" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "add_link_xml_action")
public class AddLinkXmlAction
    extends NodeAction
{

    @XmlAttribute(name = "destination")
    protected String destination;

    /**
     * Gets the value of the destination property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Sets the value of the destination property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDestination(String value) {
        this.destination = value;
    }

}
