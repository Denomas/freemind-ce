
package freemind.controller.actions.generated.instance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for table_column_setting_type complex type</p>.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType name="table_column_setting_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="column_width" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="column_sorting" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "table_column_setting_type")
@XmlSeeAlso({
    TimeWindowColumnSetting.class
})
public class TableColumnSettingType {

    @XmlAttribute(name = "column_width", required = true)
    protected int columnWidth;
    @XmlAttribute(name = "column_sorting")
    protected Integer columnSorting;

    /**
     * Gets the value of the columnWidth property.
     *
     */
    public int getColumnWidth() {
        return columnWidth;
    }

    /**
     * Sets the value of the columnWidth property.
     *
     */
    public void setColumnWidth(int value) {
        this.columnWidth = value;
    }

    /**
     * Gets the value of the columnSorting property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getColumnSorting() {
        return columnSorting;
    }

    /**
     * Sets the value of the columnSorting property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setColumnSorting(Integer value) {
        this.columnSorting = value;
    }

}
