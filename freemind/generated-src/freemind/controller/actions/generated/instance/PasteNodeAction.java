
package freemind.controller.actions.generated.instance;

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
 *     &lt;extension base="{}node_action"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}transferable_content"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="isLeft" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="asSibling" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "transferableContent"
})
@XmlRootElement(name = "paste_node_action")
public class PasteNodeAction
    extends NodeAction
{

    @XmlElement(name = "transferable_content", required = true)
    protected TransferableContent transferableContent;
    @XmlAttribute(name = "isLeft", required = true)
    protected boolean isLeft;
    @XmlAttribute(name = "asSibling", required = true)
    protected boolean asSibling;

    /**
     * Gets the value of the transferableContent property.
     *
     * @return
     *     possible object is
     *     {@link TransferableContent }
     *
     */
    public TransferableContent getTransferableContent() {
        return transferableContent;
    }

    /**
     * Sets the value of the transferableContent property.
     *
     * @param value
     *     allowed object is
     *     {@link TransferableContent }
     *
     */
    public void setTransferableContent(TransferableContent value) {
        this.transferableContent = value;
    }

    /**
     * Gets the value of the isLeft property.
     *
     */
    public boolean isIsLeft() {
        return isLeft;
    }

    /**
     * Sets the value of the isLeft property.
     *
     */
    public void setIsLeft(boolean value) {
        this.isLeft = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getIsLeft() {
        return isIsLeft();
    }

    /**
     * Gets the value of the asSibling property.
     *
     */
    public boolean isAsSibling() {
        return asSibling;
    }

    /**
     * Sets the value of the asSibling property.
     *
     */
    public void setAsSibling(boolean value) {
        this.asSibling = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getAsSibling() {
        return isAsSibling();
    }

}
