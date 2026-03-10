
package freemind.controller.actions.generated.instance;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for attribute_table_properties complex type</p>.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType name="attribute_table_properties"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{}xml_action"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}table_column_order" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "attribute_table_properties", propOrder = {
    "tableColumnOrder"
})
@XmlRootElement(name = "attribute_table_properties")
public class AttributeTableProperties
    extends XmlAction
{

    @XmlElement(name = "table_column_order")
    protected List<TableColumnOrder> tableColumnOrder;

    /**
     * Gets the value of the tableColumnOrder property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tableColumnOrder property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getTableColumnOrder().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TableColumnOrder }
     * </p>
     *
     *
     * @return
     *     The value of the tableColumnOrder property.
     */
    public List<TableColumnOrder> getTableColumnOrder() {
        if (tableColumnOrder == null) {
            tableColumnOrder = new ArrayList<TableColumnOrder>();
        }
        return this.tableColumnOrder;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)

    public List<TableColumnOrder> getListTableColumnOrderList() {
        return getTableColumnOrder();
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public void addTableColumnOrder(TableColumnOrder o) {
        getTableColumnOrder().add(o);
    }

}
