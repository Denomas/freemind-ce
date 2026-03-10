
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
 *     &lt;extension base="{}format_node_action"&gt;
 *       &lt;attribute name="bold" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "bold_node_action")
public class BoldNodeAction
    extends FormatNodeAction
{

    @XmlAttribute(name = "bold", required = true)
    protected boolean bold;

    /**
     * Gets the value of the bold property.
     *
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * Sets the value of the bold property.
     *
     */
    public void setBold(boolean value) {
        this.bold = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getBold() {
        return isBold();
    }

}
