
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
 *     &lt;extension base="{}xml_action"&gt;
 *       &lt;attribute name="map" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="localFileName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="filePrefix" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "revert_xml_action")
public class RevertXmlAction
    extends XmlAction
{

    @XmlAttribute(name = "map", required = true)
    protected String map;
    @XmlAttribute(name = "localFileName")
    protected String localFileName;
    @XmlAttribute(name = "filePrefix")
    protected String filePrefix;

    /**
     * Gets the value of the map property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMap() {
        return map;
    }

    /**
     * Sets the value of the map property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMap(String value) {
        this.map = value;
    }

    /**
     * Gets the value of the localFileName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLocalFileName() {
        return localFileName;
    }

    /**
     * Sets the value of the localFileName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLocalFileName(String value) {
        this.localFileName = value;
    }

    /**
     * Gets the value of the filePrefix property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFilePrefix() {
        return filePrefix;
    }

    /**
     * Sets the value of the filePrefix property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFilePrefix(String value) {
        this.filePrefix = value;
    }

}
