
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
 *     &lt;extension base="{}collaboration_action_base"&gt;
 *       &lt;attribute name="server_version" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "collaboration_who_are_you")
public class CollaborationWhoAreYou
    extends CollaborationActionBase
{

    @XmlAttribute(name = "server_version")
    protected String serverVersion;

    /**
     * Gets the value of the serverVersion property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getServerVersion() {
        return serverVersion;
    }

    /**
     * Sets the value of the serverVersion property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setServerVersion(String value) {
        this.serverVersion = value;
    }

}
