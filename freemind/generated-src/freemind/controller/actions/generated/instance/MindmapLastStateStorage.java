
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
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}node_list_member" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="last_changed" use="required" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="tab_index" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="restorable_name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="last_zoom" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="x" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="y" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="last_selected" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
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
@XmlRootElement(name = "mindmap_last_state_storage")
public class MindmapLastStateStorage {

    @XmlElement(name = "node_list_member")
    protected List<NodeListMember> nodeListMember;
    @XmlAttribute(name = "last_changed", required = true)
    protected long lastChanged;
    @XmlAttribute(name = "tab_index", required = true)
    protected int tabIndex;
    @XmlAttribute(name = "restorable_name", required = true)
    protected String restorableName;
    @XmlAttribute(name = "last_zoom", required = true)
    protected float lastZoom;
    @XmlAttribute(name = "x", required = true)
    protected int x;
    @XmlAttribute(name = "y", required = true)
    protected int y;
    @XmlAttribute(name = "last_selected", required = true)
    protected String lastSelected;

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
     * Gets the value of the lastChanged property.
     *
     */
    public long getLastChanged() {
        return lastChanged;
    }

    /**
     * Sets the value of the lastChanged property.
     *
     */
    public void setLastChanged(long value) {
        this.lastChanged = value;
    }

    /**
     * Gets the value of the tabIndex property.
     *
     */
    public int getTabIndex() {
        return tabIndex;
    }

    /**
     * Sets the value of the tabIndex property.
     *
     */
    public void setTabIndex(int value) {
        this.tabIndex = value;
    }

    /**
     * Gets the value of the restorableName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRestorableName() {
        return restorableName;
    }

    /**
     * Sets the value of the restorableName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRestorableName(String value) {
        this.restorableName = value;
    }

    /**
     * Gets the value of the lastZoom property.
     *
     */
    public float getLastZoom() {
        return lastZoom;
    }

    /**
     * Sets the value of the lastZoom property.
     *
     */
    public void setLastZoom(float value) {
        this.lastZoom = value;
    }

    /**
     * Gets the value of the x property.
     *
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     *
     */
    public void setX(int value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     *
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     *
     */
    public void setY(int value) {
        this.y = value;
    }

    /**
     * Gets the value of the lastSelected property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLastSelected() {
        return lastSelected;
    }

    /**
     * Sets the value of the lastSelected property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLastSelected(String value) {
        this.lastSelected = value;
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
