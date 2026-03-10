
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
 *       &lt;attribute name="destination" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="new_id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="color" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="startInclination" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="endInclination" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="startArrow" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="endArrow" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "add_arrow_link_xml_action")
public class AddArrowLinkXmlAction
    extends NodeAction
{

    @XmlAttribute(name = "destination", required = true)
    protected String destination;
    @XmlAttribute(name = "new_id")
    protected String newId;
    @XmlAttribute(name = "color")
    protected String color;
    @XmlAttribute(name = "startInclination")
    protected String startInclination;
    @XmlAttribute(name = "endInclination")
    protected String endInclination;
    @XmlAttribute(name = "startArrow")
    protected String startArrow;
    @XmlAttribute(name = "endArrow")
    protected String endArrow;

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

    /**
     * Gets the value of the startInclination property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStartInclination() {
        return startInclination;
    }

    /**
     * Sets the value of the startInclination property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStartInclination(String value) {
        this.startInclination = value;
    }

    /**
     * Gets the value of the endInclination property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEndInclination() {
        return endInclination;
    }

    /**
     * Sets the value of the endInclination property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEndInclination(String value) {
        this.endInclination = value;
    }

    /**
     * Gets the value of the startArrow property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStartArrow() {
        return startArrow;
    }

    /**
     * Sets the value of the startArrow property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStartArrow(String value) {
        this.startArrow = value;
    }

    /**
     * Gets the value of the endArrow property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEndArrow() {
        return endArrow;
    }

    /**
     * Sets the value of the endArrow property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEndArrow(String value) {
        this.endArrow = value;
    }

}
