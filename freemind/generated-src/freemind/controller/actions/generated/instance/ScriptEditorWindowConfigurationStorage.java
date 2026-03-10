
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
 *       &lt;attribute name="left_ratio" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="top_ratio" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "script_editor_window_configuration_storage")
public class ScriptEditorWindowConfigurationStorage
    extends WindowConfigurationStorage
{

    @XmlAttribute(name = "left_ratio")
    protected Integer leftRatio;
    @XmlAttribute(name = "top_ratio")
    protected Integer topRatio;

    /**
     * Gets the value of the leftRatio property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getLeftRatio() {
        return leftRatio;
    }

    /**
     * Sets the value of the leftRatio property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setLeftRatio(Integer value) {
        this.leftRatio = value;
    }

    /**
     * Gets the value of the topRatio property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getTopRatio() {
        return topRatio;
    }

    /**
     * Sets the value of the topRatio property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setTopRatio(Integer value) {
        this.topRatio = value;
    }

}
