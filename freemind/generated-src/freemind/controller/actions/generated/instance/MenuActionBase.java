
package freemind.controller.actions.generated.instance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for menu_action_base complex type</p>.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType name="menu_action_base"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="field" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="key_ref" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "menu_action_base")
@XmlSeeAlso({
    MenuRadioAction.class,
    MenuCheckedAction.class,
    MenuAction.class
})
public class MenuActionBase {

    @XmlAttribute(name = "field", required = true)
    protected String field;
    @XmlAttribute(name = "key_ref")
    protected String keyRef;
    @XmlAttribute(name = "name")
    protected String name;

    /**
     * Gets the value of the field property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getField() {
        return field;
    }

    /**
     * Sets the value of the field property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setField(String value) {
        this.field = value;
    }

    /**
     * Gets the value of the keyRef property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKeyRef() {
        return keyRef;
    }

    /**
     * Sets the value of the keyRef property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKeyRef(String value) {
        this.keyRef = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

}
