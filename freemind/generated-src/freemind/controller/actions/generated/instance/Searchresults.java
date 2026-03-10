
package freemind.controller.actions.generated.instance;

import java.util.ArrayList;
import java.util.List;
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
 *     &lt;extension base="{}xml_action"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}place" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="timestamp" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="attribution" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="querystring" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="polygon" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="exclude_place_ids" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="more_url" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "place"
})
@XmlRootElement(name = "searchresults")
public class Searchresults
    extends XmlAction
{

    protected List<Place> place;
    @XmlAttribute(name = "timestamp")
    protected String timestamp;
    @XmlAttribute(name = "attribution")
    protected String attribution;
    @XmlAttribute(name = "querystring")
    protected String querystring;
    @XmlAttribute(name = "polygon")
    protected String polygon;
    @XmlAttribute(name = "exclude_place_ids")
    protected String excludePlaceIds;
    @XmlAttribute(name = "more_url")
    protected String moreUrl;

    /**
     * Gets the value of the place property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the place property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getPlace().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Place }
     * </p>
     *
     *
     * @return
     *     The value of the place property.
     */
    public List<Place> getPlace() {
        if (place == null) {
            place = new ArrayList<Place>();
        }
        return this.place;
    }

    /**
     * Gets the value of the timestamp property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTimestamp(String value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the attribution property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAttribution() {
        return attribution;
    }

    /**
     * Sets the value of the attribution property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAttribution(String value) {
        this.attribution = value;
    }

    /**
     * Gets the value of the querystring property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getQuerystring() {
        return querystring;
    }

    /**
     * Sets the value of the querystring property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setQuerystring(String value) {
        this.querystring = value;
    }

    /**
     * Gets the value of the polygon property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPolygon() {
        return polygon;
    }

    /**
     * Sets the value of the polygon property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPolygon(String value) {
        this.polygon = value;
    }

    /**
     * Gets the value of the excludePlaceIds property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExcludePlaceIds() {
        return excludePlaceIds;
    }

    /**
     * Sets the value of the excludePlaceIds property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExcludePlaceIds(String value) {
        this.excludePlaceIds = value;
    }

    /**
     * Gets the value of the moreUrl property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMoreUrl() {
        return moreUrl;
    }

    /**
     * Sets the value of the moreUrl property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMoreUrl(String value) {
        this.moreUrl = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public List<Place> getListPlaceList() {
        return getPlace();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public void addPlace(Place p) {
        getPlace().add(p);
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public int sizePlaceList() {
        return getPlace().size();
    }

}
