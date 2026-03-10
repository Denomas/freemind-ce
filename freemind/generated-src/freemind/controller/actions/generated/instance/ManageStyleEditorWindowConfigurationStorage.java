
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
 *     &lt;extension base="{}window_configuration_storage"&gt;
 *       &lt;attribute name="divider_position" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "manage_style_editor_window_configuration_storage")
public class ManageStyleEditorWindowConfigurationStorage
    extends WindowConfigurationStorage
{

    @XmlAttribute(name = "divider_position")
    protected Integer dividerPosition;

    /**
     * Gets the value of the dividerPosition property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getDividerPosition() {
        return dividerPosition;
    }

    /**
     * Sets the value of the dividerPosition property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setDividerPosition(Integer value) {
        this.dividerPosition = value;
    }

}
