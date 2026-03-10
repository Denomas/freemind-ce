
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
 *     &lt;extension base="{}window_configuration_storage"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}table_column_setting" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{}map_location_storage" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="map_center_longitude" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="map_center_latitude" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="cursor_longitude" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="cursor_latitude" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="zoom" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="last_divider_position" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="tile_source" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="showMapMarker" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="tileGridVisible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="zoomControlsVisible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="searchControlVisible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="hideFoldedNodes" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="limitSearchToVisibleArea" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="map_location_storage_index" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "tableColumnSetting",
    "mapLocationStorage"
})
@XmlRootElement(name = "map_window_configuration_storage")
public class MapWindowConfigurationStorage
    extends WindowConfigurationStorage
{

    @XmlElement(name = "table_column_setting")
    protected List<TableColumnSettingType> tableColumnSetting;
    @XmlElement(name = "map_location_storage")
    protected List<MapLocationStorage> mapLocationStorage;
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
    @XmlAttribute(name = "last_divider_position")
    protected Integer lastDividerPosition;
    @XmlAttribute(name = "tile_source")
    protected String tileSource;
    @XmlAttribute(name = "showMapMarker")
    protected Boolean showMapMarker;
    @XmlAttribute(name = "tileGridVisible")
    protected Boolean tileGridVisible;
    @XmlAttribute(name = "zoomControlsVisible")
    protected Boolean zoomControlsVisible;
    @XmlAttribute(name = "searchControlVisible")
    protected Boolean searchControlVisible;
    @XmlAttribute(name = "hideFoldedNodes")
    protected Boolean hideFoldedNodes;
    @XmlAttribute(name = "limitSearchToVisibleArea")
    protected Boolean limitSearchToVisibleArea;
    @XmlAttribute(name = "map_location_storage_index")
    protected Integer mapLocationStorageIndex;

    /**
     * Gets the value of the tableColumnSetting property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tableColumnSetting property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getTableColumnSetting().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TableColumnSettingType }
     * </p>
     *
     *
     * @return
     *     The value of the tableColumnSetting property.
     */
    public List<TableColumnSettingType> getTableColumnSetting() {
        if (tableColumnSetting == null) {
            tableColumnSetting = new ArrayList<TableColumnSettingType>();
        }
        return this.tableColumnSetting;
    }

    /**
     * Gets the value of the mapLocationStorage property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mapLocationStorage property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getMapLocationStorage().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MapLocationStorage }
     * </p>
     *
     *
     * @return
     *     The value of the mapLocationStorage property.
     */
    public List<MapLocationStorage> getMapLocationStorage() {
        if (mapLocationStorage == null) {
            mapLocationStorage = new ArrayList<MapLocationStorage>();
        }
        return this.mapLocationStorage;
    }

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
     * Gets the value of the lastDividerPosition property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getLastDividerPosition() {
        return lastDividerPosition;
    }

    /**
     * Sets the value of the lastDividerPosition property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setLastDividerPosition(Integer value) {
        this.lastDividerPosition = value;
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

    /**
     * Gets the value of the showMapMarker property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isShowMapMarker() {
        if (showMapMarker == null) {
            return true;
        } else {
            return showMapMarker;
        }
    }

    /**
     * Sets the value of the showMapMarker property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setShowMapMarker(Boolean value) {
        this.showMapMarker = value;
    }

    /**
     * Gets the value of the tileGridVisible property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isTileGridVisible() {
        if (tileGridVisible == null) {
            return false;
        } else {
            return tileGridVisible;
        }
    }

    /**
     * Sets the value of the tileGridVisible property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setTileGridVisible(Boolean value) {
        this.tileGridVisible = value;
    }

    /**
     * Gets the value of the zoomControlsVisible property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isZoomControlsVisible() {
        if (zoomControlsVisible == null) {
            return true;
        } else {
            return zoomControlsVisible;
        }
    }

    /**
     * Sets the value of the zoomControlsVisible property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setZoomControlsVisible(Boolean value) {
        this.zoomControlsVisible = value;
    }

    /**
     * Gets the value of the searchControlVisible property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isSearchControlVisible() {
        if (searchControlVisible == null) {
            return true;
        } else {
            return searchControlVisible;
        }
    }

    /**
     * Sets the value of the searchControlVisible property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setSearchControlVisible(Boolean value) {
        this.searchControlVisible = value;
    }

    /**
     * Gets the value of the hideFoldedNodes property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isHideFoldedNodes() {
        if (hideFoldedNodes == null) {
            return true;
        } else {
            return hideFoldedNodes;
        }
    }

    /**
     * Sets the value of the hideFoldedNodes property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setHideFoldedNodes(Boolean value) {
        this.hideFoldedNodes = value;
    }

    /**
     * Gets the value of the limitSearchToVisibleArea property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isLimitSearchToVisibleArea() {
        if (limitSearchToVisibleArea == null) {
            return false;
        } else {
            return limitSearchToVisibleArea;
        }
    }

    /**
     * Sets the value of the limitSearchToVisibleArea property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setLimitSearchToVisibleArea(Boolean value) {
        this.limitSearchToVisibleArea = value;
    }

    /**
     * Gets the value of the mapLocationStorageIndex property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getMapLocationStorageIndex() {
        return mapLocationStorageIndex;
    }

    /**
     * Sets the value of the mapLocationStorageIndex property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setMapLocationStorageIndex(Integer value) {
        this.mapLocationStorageIndex = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getHideFoldedNodes() {
        return isHideFoldedNodes();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getZoomControlsVisible() {
        return isZoomControlsVisible();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getTileGridVisible() {
        return isTileGridVisible();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getShowMapMarker() {
        return isShowMapMarker();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getSearchControlVisible() {
        return isSearchControlVisible();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getLimitSearchToVisibleArea() {
        return isLimitSearchToVisibleArea();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)

    @SuppressWarnings("unchecked")
    public List<TableColumnSetting> getListTableColumnSettingList() {
        return (List<TableColumnSetting>)(List<?>)getTableColumnSetting();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public void addTableColumnSetting(TableColumnSettingType t) {
        getTableColumnSetting().add(t);
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public List<MapLocationStorage> getListMapLocationStorageList() {
        return getMapLocationStorage();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public void addMapLocationStorage(MapLocationStorage s) {
        getMapLocationStorage().add(s);
    }

}
