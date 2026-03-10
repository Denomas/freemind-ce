
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
 * <p>Java class for mindmap_last_state_map_storage complex type</p>.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType name="mindmap_last_state_map_storage"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{}xml_action"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}mindmap_last_state_storage" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="last_focused_tab" type="{http://www.w3.org/2001/XMLSchema}int" default="-1" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mindmap_last_state_map_storage", propOrder = {
    "mindmapLastStateStorage"
})
@XmlRootElement(name = "mindmap_last_state_map_storage")
public class MindmapLastStateMapStorage
    extends XmlAction
{

    @XmlElement(name = "mindmap_last_state_storage")
    protected List<MindmapLastStateStorage> mindmapLastStateStorage;
    @XmlAttribute(name = "last_focused_tab")
    protected Integer lastFocusedTab;

    /**
     * Gets the value of the mindmapLastStateStorage property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mindmapLastStateStorage property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getMindmapLastStateStorage().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MindmapLastStateStorage }
     * </p>
     *
     *
     * @return
     *     The value of the mindmapLastStateStorage property.
     */
    public List<MindmapLastStateStorage> getMindmapLastStateStorage() {
        if (mindmapLastStateStorage == null) {
            mindmapLastStateStorage = new ArrayList<MindmapLastStateStorage>();
        }
        return this.mindmapLastStateStorage;
    }

    /**
     * Gets the value of the lastFocusedTab property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public int getLastFocusedTab() {
        if (lastFocusedTab == null) {
            return -1;
        } else {
            return lastFocusedTab;
        }
    }

    /**
     * Sets the value of the lastFocusedTab property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setLastFocusedTab(Integer value) {
        this.lastFocusedTab = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)

    public List<MindmapLastStateStorage> getListMindmapLastStateStorageList() {
        return getMindmapLastStateStorage();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public void addMindmapLastStateStorage(MindmapLastStateStorage s) {
        getMindmapLastStateStorage().add(s);
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public void clearMindmapLastStateStorageList() {
        getMindmapLastStateStorage().clear();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public int sizeMindmapLastStateStorageList() {
        return getMindmapLastStateStorage().size();
    }

}
