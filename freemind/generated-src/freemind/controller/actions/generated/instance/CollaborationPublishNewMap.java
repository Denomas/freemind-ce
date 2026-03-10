
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
 *       &lt;attribute name="user_id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="password" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="map" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="map_name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "collaboration_publish_new_map")
public class CollaborationPublishNewMap
    extends CollaborationActionBase
{

    @XmlAttribute(name = "user_id", required = true)
    protected String userId;
    @XmlAttribute(name = "password", required = true)
    protected String password;
    @XmlAttribute(name = "map", required = true)
    protected String map;
    @XmlAttribute(name = "map_name", required = true)
    protected String mapName;

    /**
     * Gets the value of the userId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserId(String value) {
        this.userId = value;
    }

    /**
     * Gets the value of the password property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPassword(String value) {
        this.password = value;
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
     * Gets the value of the mapName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * Sets the value of the mapName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMapName(String value) {
        this.mapName = value;
    }

}
