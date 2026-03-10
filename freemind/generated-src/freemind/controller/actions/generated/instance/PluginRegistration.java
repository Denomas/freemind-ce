
package freemind.controller.actions.generated.instance;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element ref="{}plugin_mode" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="class_name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="isPluginBase" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "pluginMode"
})
@XmlRootElement(name = "plugin_registration")
public class PluginRegistration {

    @XmlElement(name = "plugin_mode", required = true)
    protected List<PluginMode> pluginMode;
    @XmlAttribute(name = "class_name", required = true)
    protected String className;
    @XmlAttribute(name = "isPluginBase")
    protected Boolean isPluginBase;

    /**
     * Gets the value of the pluginMode property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pluginMode property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getPluginMode().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PluginMode }
     * </p>
     *
     *
     * @return
     *     The value of the pluginMode property.
     */
    public List<PluginMode> getPluginMode() {
        if (pluginMode == null) {
            pluginMode = new ArrayList<PluginMode>();
        }
        return this.pluginMode;
    }

    /**
     * Gets the value of the className property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the value of the className property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setClassName(String value) {
        this.className = value;
    }

    /**
     * Gets the value of the isPluginBase property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isIsPluginBase() {
        if (isPluginBase == null) {
            return false;
        } else {
            return isPluginBase;
        }
    }

    /**
     * Sets the value of the isPluginBase property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsPluginBase(Boolean value) {
        this.isPluginBase = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)

    public List<PluginMode> getListPluginModeList() {
        return getPluginMode();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getIsPluginBase() {
        return isIsPluginBase();
    }

}
