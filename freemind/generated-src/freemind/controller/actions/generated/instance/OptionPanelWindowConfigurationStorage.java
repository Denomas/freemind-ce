
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
 *       &lt;attribute name="panel" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "option_panel_window_configuration_storage")
public class OptionPanelWindowConfigurationStorage
    extends WindowConfigurationStorage
{

    @XmlAttribute(name = "panel")
    protected String panel;

    /**
     * Gets the value of the panel property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPanel() {
        return panel;
    }

    /**
     * Sets the value of the panel property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPanel(String value) {
        this.panel = value;
    }

}
