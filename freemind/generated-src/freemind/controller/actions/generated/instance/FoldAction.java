
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
 *       &lt;attribute name="folded" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "fold_action")
public class FoldAction
    extends NodeAction
{

    @XmlAttribute(name = "folded", required = true)
    protected boolean folded;

    /**
     * Gets the value of the folded property.
     *
     */
    public boolean isFolded() {
        return folded;
    }

    /**
     * Sets the value of the folded property.
     *
     */
    public void setFolded(boolean value) {
        this.folded = value;
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public boolean getFolded() {
        return isFolded();
    }

}
