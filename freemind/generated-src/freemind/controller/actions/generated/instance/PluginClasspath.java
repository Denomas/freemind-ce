
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
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="jar" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "plugin_classpath")
public class PluginClasspath {

    @XmlAttribute(name = "jar", required = true)
    protected String jar;

    /**
     * Gets the value of the jar property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getJar() {
        return jar;
    }

    /**
     * Sets the value of the jar property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setJar(String value) {
        this.jar = value;
    }

}
