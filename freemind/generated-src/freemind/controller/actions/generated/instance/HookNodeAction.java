
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
 *     &lt;extension base="{}node_action"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}node_list_member" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{}node_child_parameter" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="hook_name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "nodeListMember",
    "nodeChildParameter"
})
@XmlRootElement(name = "hook_node_action")
public class HookNodeAction
    extends NodeAction
{

    @XmlElement(name = "node_list_member", required = true)
    protected List<NodeListMember> nodeListMember;
    @XmlElement(name = "node_child_parameter")
    protected List<NodeChildParameter> nodeChildParameter;
    @XmlAttribute(name = "hook_name", required = true)
    protected String hookName;

    /**
     * Gets the value of the nodeListMember property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodeListMember property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getNodeListMember().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NodeListMember }
     * </p>
     *
     *
     * @return
     *     The value of the nodeListMember property.
     */
    public List<NodeListMember> getNodeListMember() {
        if (nodeListMember == null) {
            nodeListMember = new ArrayList<NodeListMember>();
        }
        return this.nodeListMember;
    }

    /**
     * Gets the value of the nodeChildParameter property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodeChildParameter property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getNodeChildParameter().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NodeChildParameter }
     * </p>
     *
     *
     * @return
     *     The value of the nodeChildParameter property.
     */
    public List<NodeChildParameter> getNodeChildParameter() {
        if (nodeChildParameter == null) {
            nodeChildParameter = new ArrayList<NodeChildParameter>();
        }
        return this.nodeChildParameter;
    }

    /**
     * Gets the value of the hookName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHookName() {
        return hookName;
    }

    /**
     * Sets the value of the hookName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHookName(String value) {
        this.hookName = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)

    public List<NodeListMember> getListNodeListMemberList() {
        return getNodeListMember();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public void addNodeListMember(NodeListMember m) {
        getNodeListMember().add(m);
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public void clearNodeListMemberList() {
        getNodeListMember().clear();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public NodeListMember getNodeListMember(int index) {
        return getNodeListMember().get(index);
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public List<NodeChildParameter> getListNodeChildParameterList() {
        return getNodeChildParameter();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public void addNodeChildParameter(NodeChildParameter p) {
        getNodeChildParameter().add(p);
    }

}
