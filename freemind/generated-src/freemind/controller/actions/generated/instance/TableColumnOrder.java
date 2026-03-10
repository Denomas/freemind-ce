
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
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="column_index" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="column_sorting" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "table_column_order")
public class TableColumnOrder {

    @XmlAttribute(name = "column_index", required = true)
    protected int columnIndex;
    @XmlAttribute(name = "column_sorting", required = true)
    protected String columnSorting;

    /**
     * Gets the value of the columnIndex property.
     *
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * Sets the value of the columnIndex property.
     *
     */
    public void setColumnIndex(int value) {
        this.columnIndex = value;
    }

    /**
     * Gets the value of the columnSorting property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getColumnSorting() {
        return columnSorting;
    }

    /**
     * Sets the value of the columnSorting property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setColumnSorting(String value) {
        this.columnSorting = value;
    }

}
