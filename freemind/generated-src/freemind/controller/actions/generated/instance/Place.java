
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
 *     &lt;extension base="{}xml_action"&gt;
 *       &lt;attribute name="place_id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="osm_type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="osm_id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="place_rank" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="boundingbox" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="lat" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="lon" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="display_name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="class" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="icon" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "place")
public class Place
    extends XmlAction
{

    @XmlAttribute(name = "place_id")
    protected String placeId;
    @XmlAttribute(name = "osm_type")
    protected String osmType;
    @XmlAttribute(name = "osm_id")
    protected String osmId;
    @XmlAttribute(name = "place_rank")
    protected String placeRank;
    @XmlAttribute(name = "boundingbox")
    protected String boundingbox;
    @XmlAttribute(name = "lat")
    protected Double lat;
    @XmlAttribute(name = "lon")
    protected Double lon;
    @XmlAttribute(name = "display_name")
    protected String displayName;
    @XmlAttribute(name = "class")
    protected String clazz;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "icon")
    protected String icon;

    /**
     * Gets the value of the placeId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPlaceId() {
        return placeId;
    }

    /**
     * Sets the value of the placeId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPlaceId(String value) {
        this.placeId = value;
    }

    /**
     * Gets the value of the osmType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOsmType() {
        return osmType;
    }

    /**
     * Sets the value of the osmType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOsmType(String value) {
        this.osmType = value;
    }

    /**
     * Gets the value of the osmId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOsmId() {
        return osmId;
    }

    /**
     * Sets the value of the osmId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOsmId(String value) {
        this.osmId = value;
    }

    /**
     * Gets the value of the placeRank property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPlaceRank() {
        return placeRank;
    }

    /**
     * Sets the value of the placeRank property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPlaceRank(String value) {
        this.placeRank = value;
    }

    /**
     * Gets the value of the boundingbox property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBoundingbox() {
        return boundingbox;
    }

    /**
     * Sets the value of the boundingbox property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBoundingbox(String value) {
        this.boundingbox = value;
    }

    /**
     * Gets the value of the lat property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getLat() {
        return lat;
    }

    /**
     * Sets the value of the lat property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setLat(Double value) {
        this.lat = value;
    }

    /**
     * Gets the value of the lon property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getLon() {
        return lon;
    }

    /**
     * Sets the value of the lon property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setLon(Double value) {
        this.lon = value;
    }

    /**
     * Gets the value of the displayName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    /**
     * Gets the value of the clazz property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setClazz(String value) {
        this.clazz = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the icon property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Sets the value of the icon property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIcon(String value) {
        this.icon = value;
    }

}
