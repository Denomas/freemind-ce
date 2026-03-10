
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
 *       &lt;attribute name="strikethrough" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "strikethrough_node_action")
public class StrikethroughNodeAction
    extends FormatNodeAction
{

    @XmlAttribute(name = "strikethrough", required = true)
    protected boolean strikethrough;

    /**
     * Gets the value of the strikethrough property.
     *
     */
    public boolean isStrikethrough() {
        return strikethrough;
    }

    /**
     * Sets the value of the strikethrough property.
     *
     */
    public void setStrikethrough(boolean value) {
        this.strikethrough = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getStrikethrough() {
        return isStrikethrough();
    }

}
