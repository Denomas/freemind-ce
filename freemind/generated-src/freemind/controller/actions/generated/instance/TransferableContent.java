
package freemind.controller.actions.generated.instance;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element ref="{}transferable_file" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Transferable" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TransferableAsPlainText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TransferableAsRTF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TransferableAsDrop" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TransferableAsHtml" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TransferableAsImage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "transferableFile",
    "transferable",
    "transferableAsPlainText",
    "transferableAsRTF",
    "transferableAsDrop",
    "transferableAsHtml",
    "transferableAsImage"
})
@XmlRootElement(name = "transferable_content")
public class TransferableContent {

    @XmlElement(name = "transferable_file")
    protected List<TransferableFile> transferableFile;
    @XmlElement(name = "Transferable")
    protected String transferable;
    @XmlElement(name = "TransferableAsPlainText")
    protected String transferableAsPlainText;
    @XmlElement(name = "TransferableAsRTF")
    protected String transferableAsRTF;
    @XmlElement(name = "TransferableAsDrop")
    protected String transferableAsDrop;
    @XmlElement(name = "TransferableAsHtml")
    protected String transferableAsHtml;
    @XmlElement(name = "TransferableAsImage")
    protected String transferableAsImage;

    /**
     * Gets the value of the transferableFile property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transferableFile property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getTransferableFile().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransferableFile }
     * </p>
     *
     *
     * @return
     *     The value of the transferableFile property.
     */
    public List<TransferableFile> getTransferableFile() {
        if (transferableFile == null) {
            transferableFile = new ArrayList<TransferableFile>();
        }
        return this.transferableFile;
    }

    /**
     * Gets the value of the transferable property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTransferable() {
        return transferable;
    }

    /**
     * Sets the value of the transferable property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTransferable(String value) {
        this.transferable = value;
    }

    /**
     * Gets the value of the transferableAsPlainText property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTransferableAsPlainText() {
        return transferableAsPlainText;
    }

    /**
     * Sets the value of the transferableAsPlainText property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTransferableAsPlainText(String value) {
        this.transferableAsPlainText = value;
    }

    /**
     * Gets the value of the transferableAsRTF property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTransferableAsRTF() {
        return transferableAsRTF;
    }

    /**
     * Sets the value of the transferableAsRTF property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTransferableAsRTF(String value) {
        this.transferableAsRTF = value;
    }

    /**
     * Gets the value of the transferableAsDrop property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTransferableAsDrop() {
        return transferableAsDrop;
    }

    /**
     * Sets the value of the transferableAsDrop property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTransferableAsDrop(String value) {
        this.transferableAsDrop = value;
    }

    /**
     * Gets the value of the transferableAsHtml property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTransferableAsHtml() {
        return transferableAsHtml;
    }

    /**
     * Sets the value of the transferableAsHtml property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTransferableAsHtml(String value) {
        this.transferableAsHtml = value;
    }

    /**
     * Gets the value of the transferableAsImage property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTransferableAsImage() {
        return transferableAsImage;
    }

    /**
     * Sets the value of the transferableAsImage property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTransferableAsImage(String value) {
        this.transferableAsImage = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)

    public List<TransferableFile> getListTransferableFileList() {
        return getTransferableFile();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public void addTransferableFile(TransferableFile f) {
        getTransferableFile().add(f);
    }

}
