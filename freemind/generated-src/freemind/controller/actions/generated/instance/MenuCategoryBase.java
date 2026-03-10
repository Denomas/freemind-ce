
package freemind.controller.actions.generated.instance;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for menu_category_base complex type</p>.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType name="menu_category_base"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;group ref="{}base"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "menu_category_base", propOrder = {
    "menuCategoryOrMenuSubmenuOrMenuAction"
})
@XmlSeeAlso({
    MenuCategory.class,
    MenuSubmenu.class
})
public class MenuCategoryBase {

    @XmlElements({
        @XmlElement(name = "menu_category", type = MenuCategory.class),
        @XmlElement(name = "menu_submenu", type = MenuSubmenu.class),
        @XmlElement(name = "menu_action", type = MenuAction.class),
        @XmlElement(name = "menu_checked_action", type = MenuCheckedAction.class),
        @XmlElement(name = "menu_radio_action", type = MenuRadioAction.class),
        @XmlElement(name = "menu_separator", type = MenuSeparator.class)
    })
    protected List<Object> menuCategoryOrMenuSubmenuOrMenuAction;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the menuCategoryOrMenuSubmenuOrMenuAction property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the menuCategoryOrMenuSubmenuOrMenuAction property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getMenuCategoryOrMenuSubmenuOrMenuAction().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MenuAction }
     * {@link MenuCategory }
     * {@link MenuCheckedAction }
     * {@link MenuRadioAction }
     * {@link MenuSeparator }
     * {@link MenuSubmenu }
     * </p>
     *
     *
     * @return
     *     The value of the menuCategoryOrMenuSubmenuOrMenuAction property.
     */
    public List<Object> getMenuCategoryOrMenuSubmenuOrMenuAction() {
        if (menuCategoryOrMenuSubmenuOrMenuAction == null) {
            menuCategoryOrMenuSubmenuOrMenuAction = new ArrayList<Object>();
        }
        return this.menuCategoryOrMenuSubmenuOrMenuAction;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)

    public List<Object> getListChoiceList() {
        return getMenuCategoryOrMenuSubmenuOrMenuAction();
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
