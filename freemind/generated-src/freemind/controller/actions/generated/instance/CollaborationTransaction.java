
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
 *     &lt;extension base="{}collaboration_action_base"&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="do_action" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="undo_action" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "collaboration_transaction")
public class CollaborationTransaction
    extends CollaborationActionBase
{

    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "do_action", required = true)
    protected String doAction;
    @XmlAttribute(name = "undo_action", required = true)
    protected String undoAction;

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the doAction property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDoAction() {
        return doAction;
    }

    /**
     * Sets the value of the doAction property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDoAction(String value) {
        this.doAction = value;
    }

    /**
     * Gets the value of the undoAction property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUndoAction() {
        return undoAction;
    }

    /**
     * Sets the value of the undoAction property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUndoAction(String value) {
        this.undoAction = value;
    }

}
