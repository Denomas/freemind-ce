
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
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *           &lt;element ref="{}plugin_mode"/&gt;
 *           &lt;element ref="{}plugin_menu"/&gt;
 *           &lt;element ref="{}plugin_property"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="label" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="base" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="class_name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="documentation" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="icon_path" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="key_stroke" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="instanciation" default="Once"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="Once"/&gt;
 *             &lt;enumeration value="OnceForRoot"/&gt;
 *             &lt;enumeration value="OnceForAllNodes"/&gt;
 *             &lt;enumeration value="Other"/&gt;
 *             &lt;enumeration value="ApplyToRoot"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="isSelectable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "pluginModeOrPluginMenuOrPluginProperty"
})
@XmlRootElement(name = "plugin_action")
public class PluginAction {

    @XmlElements({
        @XmlElement(name = "plugin_mode", type = PluginMode.class),
        @XmlElement(name = "plugin_menu", type = PluginMenu.class),
        @XmlElement(name = "plugin_property", type = PluginProperty.class)
    })
    protected List<Object> pluginModeOrPluginMenuOrPluginProperty;
    @XmlAttribute(name = "label", required = true)
    protected String label;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "base", required = true)
    protected String base;
    @XmlAttribute(name = "class_name", required = true)
    protected String className;
    @XmlAttribute(name = "documentation")
    protected String documentation;
    @XmlAttribute(name = "icon_path")
    protected String iconPath;
    @XmlAttribute(name = "key_stroke")
    protected String keyStroke;
    @XmlAttribute(name = "instanciation")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String instanciation;
    @XmlAttribute(name = "isSelectable")
    protected Boolean isSelectable;

    /**
     * Gets the value of the pluginModeOrPluginMenuOrPluginProperty property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pluginModeOrPluginMenuOrPluginProperty property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getPluginModeOrPluginMenuOrPluginProperty().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PluginMenu }
     * {@link PluginMode }
     * {@link PluginProperty }
     * </p>
     *
     *
     * @return
     *     The value of the pluginModeOrPluginMenuOrPluginProperty property.
     */
    public List<Object> getPluginModeOrPluginMenuOrPluginProperty() {
        if (pluginModeOrPluginMenuOrPluginProperty == null) {
            pluginModeOrPluginMenuOrPluginProperty = new ArrayList<Object>();
        }
        return this.pluginModeOrPluginMenuOrPluginProperty;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)

    public List<Object> getListChoiceList() {
        return getPluginModeOrPluginMenuOrPluginProperty();
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

    /**
     * Gets the value of the base property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBase() {
        return base;
    }

    /**
     * Sets the value of the base property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBase(String value) {
        this.base = value;
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
     * Gets the value of the documentation property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Sets the value of the documentation property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDocumentation(String value) {
        this.documentation = value;
    }

    /**
     * Gets the value of the iconPath property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIconPath() {
        return iconPath;
    }

    /**
     * Sets the value of the iconPath property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIconPath(String value) {
        this.iconPath = value;
    }

    /**
     * Gets the value of the keyStroke property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKeyStroke() {
        return keyStroke;
    }

    /**
     * Sets the value of the keyStroke property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKeyStroke(String value) {
        this.keyStroke = value;
    }

    /**
     * Gets the value of the instanciation property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInstanciation() {
        if (instanciation == null) {
            return "Once";
        } else {
            return instanciation;
        }
    }

    /**
     * Sets the value of the instanciation property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInstanciation(String value) {
        this.instanciation = value;
    }

    /**
     * Gets the value of the isSelectable property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isIsSelectable() {
        if (isSelectable == null) {
            return false;
        } else {
            return isSelectable;
        }
    }

    /**
     * Sets the value of the isSelectable property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsSelectable(Boolean value) {
        this.isSelectable = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getIsSelectable() {
        return isIsSelectable();
    }

}
