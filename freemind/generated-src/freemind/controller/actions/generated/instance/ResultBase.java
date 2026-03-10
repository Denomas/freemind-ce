
package freemind.controller.actions.generated.instance;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for result_base complex type</p>.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType name="result_base"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="lat" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="lon" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="osm_id" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *       &lt;attribute name="osm_type" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="place_id" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *       &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "result_base", propOrder = {
    "content"
})
@XmlSeeAlso({
    Result.class
})
public class ResultBase {

    @XmlValue
    protected String content;
    @XmlAttribute(name = "lat", required = true)
    protected double lat;
    @XmlAttribute(name = "lon", required = true)
    protected double lon;
    @XmlAttribute(name = "osm_id", required = true)
    protected BigInteger osmId;
    @XmlAttribute(name = "osm_type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String osmType;
    @XmlAttribute(name = "place_id", required = true)
    protected BigInteger placeId;
    @XmlAttribute(name = "ref")
    @XmlSchemaType(name = "anySimpleType")
    protected String ref;

    /**
     * Gets the value of the content property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContent(String value) {
        this.content = value;
    }

    /**
     * Gets the value of the lat property.
     *
     */
    public double getLat() {
        return lat;
    }

    /**
     * Sets the value of the lat property.
     *
     */
    public void setLat(double value) {
        this.lat = value;
    }

    /**
     * Gets the value of the lon property.
     *
     */
    public double getLon() {
        return lon;
    }

    /**
     * Sets the value of the lon property.
     *
     */
    public void setLon(double value) {
        this.lon = value;
    }

    /**
     * Gets the value of the osmId property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getOsmId() {
        return osmId;
    }

    /**
     * Sets the value of the osmId property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setOsmId(BigInteger value) {
        this.osmId = value;
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
     * Gets the value of the placeId property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getPlaceId() {
        return placeId;
    }

    /**
     * Sets the value of the placeId property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setPlaceId(BigInteger value) {
        this.placeId = value;
    }

    /**
     * Gets the value of the ref property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRef(String value) {
        this.ref = value;
    }

}
