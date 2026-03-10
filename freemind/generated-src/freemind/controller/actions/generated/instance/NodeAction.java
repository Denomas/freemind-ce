
package freemind.controller.actions.generated.instance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for node_action complex type</p>.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType name="node_action"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{}xml_action"&gt;
 *       &lt;attribute name="node" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "node_action")
@XmlSeeAlso({
    ChangeRootNodeAction.class,
    PlaceNodeXmlAction.class,
    RemoveAttributeAction.class,
    AddAttributeAction.class,
    InsertAttributeAction.class,
    SetAttributeAction.class,
    AddLinkXmlAction.class,
    AddArrowLinkXmlAction.class,
    CloudColorXmlAction.class,
    AddCloudXmlAction.class,
    MoveNodeXmlAction.class,
    RemoveAllIconsXmlAction.class,
    RemoveIconXmlAction.class,
    AddIconAction.class,
    HookNodeAction.class,
    MoveNodesAction.class,
    FoldAction.class,
    NewNodeAction.class,
    TextNodeAction.class,
    DeleteNodeAction.class,
    FormatNodeAction.class,
    UndoPasteNodeAction.class,
    PasteNodeAction.class,
    CutNodeAction.class,
    SelectNodeAction.class
})
public class NodeAction
    extends XmlAction
{

    @XmlAttribute(name = "node", required = true)
    protected String node;

    /**
     * Gets the value of the node property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNode() {
        return node;
    }

    /**
     * Sets the value of the node property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNode(String value) {
        this.node = value;
    }

}
