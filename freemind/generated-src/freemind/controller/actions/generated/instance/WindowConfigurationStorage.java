
package freemind.controller.actions.generated.instance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for window_configuration_storage complex type</p>.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType name="window_configuration_storage"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{}xml_action"&gt;
 *       &lt;attribute name="x" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="y" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="width" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="height" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "window_configuration_storage")
@XmlSeeAlso({
    ManageStyleEditorWindowConfigurationStorage.class,
    ScriptEditorWindowConfigurationStorage.class,
    LogFileViewerConfigurationStorage.class,
    MapWindowConfigurationStorage.class,
    TimeWindowConfigurationStorage.class,
    OptionPanelWindowConfigurationStorage.class,
    NormalWindowConfigurationStorage.class
})
public class WindowConfigurationStorage
    extends XmlAction
{

    @XmlAttribute(name = "x", required = true)
    protected int x;
    @XmlAttribute(name = "y", required = true)
    protected int y;
    @XmlAttribute(name = "width", required = true)
    protected int width;
    @XmlAttribute(name = "height", required = true)
    protected int height;

    /**
     * Gets the value of the x property.
     *
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     *
     */
    public void setX(int value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     *
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     *
     */
    public void setY(int value) {
        this.y = value;
    }

    /**
     * Gets the value of the width property.
     *
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     *
     */
    public void setWidth(int value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     *
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     *
     */
    public void setHeight(int value) {
        this.height = value;
    }

}
