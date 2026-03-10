
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
 *       &lt;attribute name="map_center_longitude" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="map_center_latitude" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="cursor_longitude" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="cursor_latitude" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="zoom" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="tile_source" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "place_node_xml_action")
public class PlaceNodeXmlAction
    extends NodeAction
{

    @XmlAttribute(name = "map_center_longitude")
    protected Double mapCenterLongitude;
    @XmlAttribute(name = "map_center_latitude")
    protected Double mapCenterLatitude;
    @XmlAttribute(name = "cursor_longitude")
    protected Double cursorLongitude;
    @XmlAttribute(name = "cursor_latitude")
    protected Double cursorLatitude;
    @XmlAttribute(name = "zoom")
    protected Integer zoom;
    @XmlAttribute(name = "tile_source")
    protected String tileSource;

    /**
     * Gets the value of the mapCenterLongitude property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getMapCenterLongitude() {
        return mapCenterLongitude;
    }

    /**
     * Sets the value of the mapCenterLongitude property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setMapCenterLongitude(Double value) {
        this.mapCenterLongitude = value;
    }

    /**
     * Gets the value of the mapCenterLatitude property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getMapCenterLatitude() {
        return mapCenterLatitude;
    }

    /**
     * Sets the value of the mapCenterLatitude property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setMapCenterLatitude(Double value) {
        this.mapCenterLatitude = value;
    }

    /**
     * Gets the value of the cursorLongitude property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getCursorLongitude() {
        return cursorLongitude;
    }

    /**
     * Sets the value of the cursorLongitude property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setCursorLongitude(Double value) {
        this.cursorLongitude = value;
    }

    /**
     * Gets the value of the cursorLatitude property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getCursorLatitude() {
        return cursorLatitude;
    }

    /**
     * Sets the value of the cursorLatitude property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setCursorLatitude(Double value) {
        this.cursorLatitude = value;
    }

    /**
     * Gets the value of the zoom property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getZoom() {
        return zoom;
    }

    /**
     * Sets the value of the zoom property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setZoom(Integer value) {
        this.zoom = value;
    }

    /**
     * Gets the value of the tileSource property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTileSource() {
        return tileSource;
    }

    /**
     * Sets the value of the tileSource property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTileSource(String value) {
        this.tileSource = value;
    }

}
