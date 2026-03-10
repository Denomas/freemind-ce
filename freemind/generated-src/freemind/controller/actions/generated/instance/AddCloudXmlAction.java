
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
 *       &lt;attribute name="enabled" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="color" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "add_cloud_xml_action")
public class AddCloudXmlAction
    extends NodeAction
{

    @XmlAttribute(name = "enabled", required = true)
    protected boolean enabled;
    @XmlAttribute(name = "color")
    protected String color;

    /**
     * Gets the value of the enabled property.
     *
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the value of the enabled property.
     *
     */
    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getEnabled() {
        return isEnabled();
    }

    /**
     * Gets the value of the color property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the value of the color property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setColor(String value) {
        this.color = value;
    }

}
