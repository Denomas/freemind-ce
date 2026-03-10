
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
 *     &lt;extension base="{}menu_category_base"&gt;
 *       &lt;attribute name="name_ref" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "menu_submenu")
public class MenuSubmenu
    extends MenuCategoryBase
{

    @XmlAttribute(name = "name_ref", required = true)
    protected String nameRef;

    /**
     * Gets the value of the nameRef property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNameRef() {
        return nameRef;
    }

    /**
     * Sets the value of the nameRef property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNameRef(String value) {
        this.nameRef = value;
    }

}
