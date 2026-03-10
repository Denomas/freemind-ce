
package freemind.controller.actions.generated.instance;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element ref="{}menu_category"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "menuCategory"
})
@XmlRootElement(name = "menu_structure")
public class MenuStructure {

    @XmlElement(name = "menu_category")
    protected List<MenuCategory> menuCategory;

    /**
     * Gets the value of the menuCategory property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the menuCategory property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getMenuCategory().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MenuCategory }
     * </p>
     *
     *
     * @return
     *     The value of the menuCategory property.
     */
    public List<MenuCategory> getMenuCategory() {
        if (menuCategory == null) {
            menuCategory = new ArrayList<MenuCategory>();
        }
        return this.menuCategory;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)

    @SuppressWarnings("unchecked")
    public List<Object> getListChoiceList() {
        return (List<Object>)(List<?>)getMenuCategory();
    }

}
