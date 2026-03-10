
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
 *     &lt;extension base="{}xml_action"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}node_list_member" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "nodeListMember"
})
@XmlRootElement(name = "node_list")
public class NodeList
    extends XmlAction
{

    @XmlElement(name = "node_list_member", required = true)
    protected List<NodeListMember> nodeListMember;

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

}
