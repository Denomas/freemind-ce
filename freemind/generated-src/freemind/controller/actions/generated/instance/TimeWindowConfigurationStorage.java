
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
 *         &lt;element ref="{}time_window_column_setting" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="view_folded_nodes" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "timeWindowColumnSetting"
})
@XmlRootElement(name = "time_window_configuration_storage")
public class TimeWindowConfigurationStorage
    extends WindowConfigurationStorage
{

    @XmlElement(name = "time_window_column_setting")
    protected List<TimeWindowColumnSetting> timeWindowColumnSetting;
    @XmlAttribute(name = "view_folded_nodes")
    protected Boolean viewFoldedNodes;

    /**
     * Gets the value of the timeWindowColumnSetting property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the timeWindowColumnSetting property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getTimeWindowColumnSetting().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TimeWindowColumnSetting }
     * </p>
     *
     *
     * @return
     *     The value of the timeWindowColumnSetting property.
     */
    public List<TimeWindowColumnSetting> getTimeWindowColumnSetting() {
        if (timeWindowColumnSetting == null) {
            timeWindowColumnSetting = new ArrayList<TimeWindowColumnSetting>();
        }
        return this.timeWindowColumnSetting;
    }

    /**
     * Gets the value of the viewFoldedNodes property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isViewFoldedNodes() {
        if (viewFoldedNodes == null) {
            return true;
        } else {
            return viewFoldedNodes;
        }
    }

    /**
     * Sets the value of the viewFoldedNodes property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setViewFoldedNodes(Boolean value) {
        this.viewFoldedNodes = value;
    }

    // JiBX backward-compatibility methods (manual addition - preserve on regeneration)

    public List<TimeWindowColumnSetting> getListTimeWindowColumnSettingList() {
        return getTimeWindowColumnSetting();
    }

    public void addTimeWindowColumnSetting(TimeWindowColumnSetting setting) {
        getTimeWindowColumnSetting().add(setting);
    }

    public boolean getViewFoldedNodes() {
        return isViewFoldedNodes();
    }

}
