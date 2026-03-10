
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
 *     &lt;extension base="{}menu_action_base"&gt;
 *       &lt;attribute name="selected" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "menu_radio_action")
public class MenuRadioAction
    extends MenuActionBase
{

    @XmlAttribute(name = "selected")
    protected Boolean selected;

    /**
     * Gets the value of the selected property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isSelected() {
        if (selected == null) {
            return false;
        } else {
            return selected;
        }
    }

    /**
     * Sets the value of the selected property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setSelected(Boolean value) {
        this.selected = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getSelected() {
        return isSelected();
    }

}
