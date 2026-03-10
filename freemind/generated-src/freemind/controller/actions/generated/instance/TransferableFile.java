
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
 *       &lt;attribute name="file_name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "transferable_file")
public class TransferableFile {

    @XmlAttribute(name = "file_name", required = true)
    protected String fileName;

    /**
     * Gets the value of the fileName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the value of the fileName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

}
