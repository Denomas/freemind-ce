
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
 *       &lt;attribute name="icon_position" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "remove_icon_xml_action")
public class RemoveIconXmlAction
    extends NodeAction
{

    @XmlAttribute(name = "icon_position", required = true)
    protected int iconPosition;

    /**
     * Gets the value of the iconPosition property.
     *
     */
    public int getIconPosition() {
        return iconPosition;
    }

    /**
     * Sets the value of the iconPosition property.
     *
     */
    public void setIconPosition(int value) {
        this.iconPosition = value;
    }

}
