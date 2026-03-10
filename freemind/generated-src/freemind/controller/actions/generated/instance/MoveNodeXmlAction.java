
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
 *       &lt;attribute name="v_gap" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="h_gap" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="shift_y" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "move_node_xml_action")
public class MoveNodeXmlAction
    extends NodeAction
{

    @XmlAttribute(name = "v_gap", required = true)
    protected int vGap;
    @XmlAttribute(name = "h_gap", required = true)
    protected int hGap;
    @XmlAttribute(name = "shift_y", required = true)
    protected int shiftY;

    /**
     * Gets the value of the vGap property.
     *
     */
    public int getVGap() {
        return vGap;
    }

    /**
     * Sets the value of the vGap property.
     *
     */
    public void setVGap(int value) {
        this.vGap = value;
    }

    /**
     * Gets the value of the hGap property.
     *
     */
    public int getHGap() {
        return hGap;
    }

    /**
     * Sets the value of the hGap property.
     *
     */
    public void setHGap(int value) {
        this.hGap = value;
    }

    /**
     * Gets the value of the shiftY property.
     *
     */
    public int getShiftY() {
        return shiftY;
    }

    /**
     * Sets the value of the shiftY property.
     *
     */
    public void setShiftY(int value) {
        this.shiftY = value;
    }

}
