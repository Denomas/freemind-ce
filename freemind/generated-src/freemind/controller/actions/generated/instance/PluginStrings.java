
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
 *         &lt;element ref="{}plugin_string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="language" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "pluginString"
})
@XmlRootElement(name = "plugin_strings")
public class PluginStrings {

    @XmlElement(name = "plugin_string")
    protected List<PluginString> pluginString;
    @XmlAttribute(name = "language", required = true)
    protected String language;

    /**
     * Gets the value of the pluginString property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pluginString property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getPluginString().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PluginString }
     * </p>
     *
     *
     * @return
     *     The value of the pluginString property.
     */
    public List<PluginString> getPluginString() {
        if (pluginString == null) {
            pluginString = new ArrayList<PluginString>();
        }
        return this.pluginString;
    }

    /**
     * Gets the value of the language property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLanguage(String value) {
        this.language = value;
    }

}
