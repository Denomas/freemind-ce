
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
 *     &lt;extension base="{}xml_action"&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="end_arrow" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="start_arrow" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "arrow_link_arrow_xml_action")
public class ArrowLinkArrowXmlAction
    extends XmlAction
{

    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "end_arrow")
    protected String endArrow;
    @XmlAttribute(name = "start_arrow")
    protected String startArrow;

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
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

}
