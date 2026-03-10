
package freemind.controller.actions.generated.instance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type</p>.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{}xml_action"&gt;
 *       &lt;attribute name="user" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="timestamp" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="cmd" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="request_map_sharing"/&gt;
 *             &lt;enumeration value="accept_map_sharing"/&gt;
 *             &lt;enumeration value="stop_map_sharing"/&gt;
 *             &lt;enumeration value="decline_map_sharing"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="map" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="filename" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "collaboration_action")
public class CollaborationAction
    extends XmlAction
{

    @XmlAttribute(name = "user")
    protected String user;
    @XmlAttribute(name = "timestamp")
    protected String timestamp;
    @XmlAttribute(name = "cmd", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String cmd;
    @XmlAttribute(name = "map")
    protected String map;
    @XmlAttribute(name = "filename")
    protected String filename;

    /**
     * Gets the value of the user property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Gets the value of the timestamp property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTimestamp(String value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the cmd property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCmd() {
        return cmd;
    }

    /**
     * Sets the value of the cmd property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCmd(String value) {
        this.cmd = value;
    }

    /**
     * Gets the value of the map property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMap() {
        return map;
    }

    /**
     * Sets the value of the map property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMap(String value) {
        this.map = value;
    }

    /**
     * Gets the value of the filename property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFilename(String value) {
        this.filename = value;
    }

}
