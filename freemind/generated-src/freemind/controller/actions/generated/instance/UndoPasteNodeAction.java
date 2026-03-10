
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
 *     &lt;extension base="{}node_action"&gt;
 *       &lt;attribute name="isLeft" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="asSibling" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="node_amount" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "undo_paste_node_action")
public class UndoPasteNodeAction
    extends NodeAction
{

    @XmlAttribute(name = "isLeft", required = true)
    protected boolean isLeft;
    @XmlAttribute(name = "asSibling", required = true)
    protected boolean asSibling;
    @XmlAttribute(name = "node_amount", required = true)
    protected int nodeAmount;

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

    /**
     * Gets the value of the nodeAmount property.
     *
     */
    public int getNodeAmount() {
        return nodeAmount;
    }

    /**
     * Sets the value of the nodeAmount property.
     *
     */
    public void setNodeAmount(int value) {
        this.nodeAmount = value;
    }

    // JiBX backward-compatibility methods (manual addition - preserve on regeneration)
    public boolean getAsSibling() {
        return isAsSibling();
    }

    public boolean getIsLeft() {
        return isIsLeft();
    }

}
