
package freemind.controller.actions.generated.instance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for format_node_action complex type</p>.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType name="format_node_action"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{}node_action"&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "format_node_action")
@XmlSeeAlso({
    EdgeStyleFormatAction.class,
    EdgeWidthFormatAction.class,
    EdgeColorFormatAction.class,
    NodeStyleFormatAction.class,
    NodeBackgroundColorFormatAction.class,
    NodeColorFormatAction.class,
    FontNodeAction.class,
    FontSizeNodeAction.class,
    UnderlinedNodeAction.class,
    ItalicNodeAction.class,
    StrikethroughNodeAction.class,
    BoldNodeAction.class
})
public class FormatNodeAction
    extends NodeAction
{


}
