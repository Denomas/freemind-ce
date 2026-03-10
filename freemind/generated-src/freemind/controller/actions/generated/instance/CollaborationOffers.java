
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
 *     &lt;extension base="{}collaboration_action_base"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}collaboration_map_offer" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="is_single_offer" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "collaborationMapOffer"
})
@XmlRootElement(name = "collaboration_offers")
public class CollaborationOffers
    extends CollaborationActionBase
{

    @XmlElement(name = "collaboration_map_offer")
    protected List<CollaborationMapOffer> collaborationMapOffer;
    @XmlAttribute(name = "is_single_offer")
    protected Boolean isSingleOffer;

    /**
     * Gets the value of the collaborationMapOffer property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the collaborationMapOffer property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getCollaborationMapOffer().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CollaborationMapOffer }
     * </p>
     *
     *
     * @return
     *     The value of the collaborationMapOffer property.
     */
    public List<CollaborationMapOffer> getCollaborationMapOffer() {
        if (collaborationMapOffer == null) {
            collaborationMapOffer = new ArrayList<CollaborationMapOffer>();
        }
        return this.collaborationMapOffer;
    }

    /**
     * Gets the value of the isSingleOffer property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isIsSingleOffer() {
        if (isSingleOffer == null) {
            return false;
        } else {
            return isSingleOffer;
        }
    }

    /**
     * Sets the value of the isSingleOffer property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsSingleOffer(Boolean value) {
        this.isSingleOffer = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public List<CollaborationMapOffer> getListCollaborationMapOfferList() {
        return getCollaborationMapOffer();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public void addCollaborationMapOffer(CollaborationMapOffer o) {
        getCollaborationMapOffer().add(o);
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public CollaborationMapOffer getCollaborationMapOffer(int index) {
        return getCollaborationMapOffer().get(index);
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getIsSingleOffer() {
        return isIsSingleOffer();
    }

}
