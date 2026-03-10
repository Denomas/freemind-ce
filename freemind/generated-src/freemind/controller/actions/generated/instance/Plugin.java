
package freemind.controller.actions.generated.instance;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
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
 *           &lt;element ref="{}plugin_classpath"/&gt;
 *           &lt;element ref="{}plugin_registration"/&gt;
 *           &lt;element ref="{}plugin_action"/&gt;
 *           &lt;element ref="{}plugin_strings"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="label" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "pluginClasspathOrPluginRegistrationOrPluginAction"
})
@XmlRootElement(name = "plugin")
public class Plugin {

    @XmlElements({
        @XmlElement(name = "plugin_classpath", type = PluginClasspath.class),
        @XmlElement(name = "plugin_registration", type = PluginRegistration.class),
        @XmlElement(name = "plugin_action", type = PluginAction.class),
        @XmlElement(name = "plugin_strings", type = PluginStrings.class)
    })
    protected List<Object> pluginClasspathOrPluginRegistrationOrPluginAction;
    @XmlAttribute(name = "label", required = true)
    protected String label;

    /**
     * Gets the value of the pluginClasspathOrPluginRegistrationOrPluginAction property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pluginClasspathOrPluginRegistrationOrPluginAction property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getPluginClasspathOrPluginRegistrationOrPluginAction().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PluginAction }
     * {@link PluginClasspath }
     * {@link PluginRegistration }
     * {@link PluginStrings }
     * </p>
     *
     *
     * @return
     *     The value of the pluginClasspathOrPluginRegistrationOrPluginAction property.
     */
    public List<Object> getPluginClasspathOrPluginRegistrationOrPluginAction() {
        if (pluginClasspathOrPluginRegistrationOrPluginAction == null) {
            pluginClasspathOrPluginRegistrationOrPluginAction = new ArrayList<Object>();
        }
        return this.pluginClasspathOrPluginRegistrationOrPluginAction;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)

    public List<Object> getListChoiceList() {
        return getPluginClasspathOrPluginRegistrationOrPluginAction();
    }

    /**
     * Gets the value of the label property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLabel(String value) {
        this.label = value;
    }

}
