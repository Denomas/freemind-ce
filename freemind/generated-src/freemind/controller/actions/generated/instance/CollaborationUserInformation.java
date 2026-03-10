
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
 *       &lt;attribute name="user_ids" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="master_ip" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="master_port" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="master_hostname" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "collaboration_user_information")
public class CollaborationUserInformation
    extends CollaborationActionBase
{

    @XmlAttribute(name = "user_ids", required = true)
    protected String userIds;
    @XmlAttribute(name = "master_ip", required = true)
    protected String masterIp;
    @XmlAttribute(name = "master_port", required = true)
    protected int masterPort;
    @XmlAttribute(name = "master_hostname", required = true)
    protected String masterHostname;

    /**
     * Gets the value of the userIds property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserIds() {
        return userIds;
    }

    /**
     * Sets the value of the userIds property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserIds(String value) {
        this.userIds = value;
    }

    /**
     * Gets the value of the masterIp property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMasterIp() {
        return masterIp;
    }

    /**
     * Sets the value of the masterIp property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMasterIp(String value) {
        this.masterIp = value;
    }

    /**
     * Gets the value of the masterPort property.
     *
     */
    public int getMasterPort() {
        return masterPort;
    }

    /**
     * Sets the value of the masterPort property.
     *
     */
    public void setMasterPort(int value) {
        this.masterPort = value;
    }

    /**
     * Gets the value of the masterHostname property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMasterHostname() {
        return masterHostname;
    }

    /**
     * Sets the value of the masterHostname property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMasterHostname(String value) {
        this.masterHostname = value;
    }

}
