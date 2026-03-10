
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
 *       &lt;attribute name="position" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="index" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="newId" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "new_node_action")
public class NewNodeAction
    extends NodeAction
{

    @XmlAttribute(name = "position")
    protected String position;
    @XmlAttribute(name = "index")
    protected Integer index;
    @XmlAttribute(name = "newId")
    protected String newId;

    /**
     * Gets the value of the position property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPosition(String value) {
        this.position = value;
    }

    /**
     * Gets the value of the index property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * Sets the value of the index property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setIndex(Integer value) {
        this.index = value;
    }

    /**
     * Gets the value of the newId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNewId() {
        return newId;
    }

    /**
     * Sets the value of the newId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNewId(String value) {
        this.newId = value;
    }

}
