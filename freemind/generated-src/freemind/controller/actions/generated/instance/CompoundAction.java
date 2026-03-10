
package freemind.controller.actions.generated.instance;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
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
 *     &lt;extension base="{}xml_action"&gt;
 *       &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *         &lt;element ref="{}compound_action"/&gt;
 *         &lt;element ref="{}select_node_action"/&gt;
 *         &lt;element ref="{}cut_node_action"/&gt;
 *         &lt;element ref="{}paste_node_action"/&gt;
 *         &lt;element ref="{}undo_paste_node_action"/&gt;
 *         &lt;element ref="{}revert_xml_action"/&gt;
 *         &lt;element ref="{}bold_node_action"/&gt;
 *         &lt;element ref="{}strikethrough_node_action"/&gt;
 *         &lt;element ref="{}italic_node_action"/&gt;
 *         &lt;element ref="{}underlined_node_action"/&gt;
 *         &lt;element ref="{}font_size_node_action"/&gt;
 *         &lt;element ref="{}font_node_action"/&gt;
 *         &lt;element ref="{}node_color_format_action"/&gt;
 *         &lt;element ref="{}node_background_color_format_action"/&gt;
 *         &lt;element ref="{}node_style_format_action"/&gt;
 *         &lt;element ref="{}edge_color_format_action"/&gt;
 *         &lt;element ref="{}edge_width_format_action"/&gt;
 *         &lt;element ref="{}edge_style_format_action"/&gt;
 *         &lt;element ref="{}delete_node_action"/&gt;
 *         &lt;element ref="{}edit_node_action"/&gt;
 *         &lt;element ref="{}new_node_action"/&gt;
 *         &lt;element ref="{}fold_action"/&gt;
 *         &lt;element ref="{}move_nodes_action"/&gt;
 *         &lt;element ref="{}hook_node_action"/&gt;
 *         &lt;element ref="{}add_icon_action"/&gt;
 *         &lt;element ref="{}remove_icon_xml_action"/&gt;
 *         &lt;element ref="{}remove_all_icons_xml_action"/&gt;
 *         &lt;element ref="{}move_node_xml_action"/&gt;
 *         &lt;element ref="{}add_cloud_xml_action"/&gt;
 *         &lt;element ref="{}cloud_color_xml_action"/&gt;
 *         &lt;element ref="{}add_arrow_link_xml_action"/&gt;
 *         &lt;element ref="{}add_link_xml_action"/&gt;
 *         &lt;element ref="{}remove_arrow_link_xml_action"/&gt;
 *         &lt;element ref="{}arrow_link_color_xml_action"/&gt;
 *         &lt;element ref="{}arrow_link_arrow_xml_action"/&gt;
 *         &lt;element ref="{}arrow_link_point_xml_action"/&gt;
 *         &lt;element ref="{}set_attribute_action"/&gt;
 *         &lt;element ref="{}insert_attribute_action"/&gt;
 *         &lt;element ref="{}add_attribute_action"/&gt;
 *         &lt;element ref="{}remove_attribute_action"/&gt;
 *         &lt;element ref="{}edit_note_to_node_action"/&gt;
 *         &lt;element ref="{}place_node_xml_action"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "compoundActionOrSelectNodeActionOrCutNodeAction"
})
@XmlRootElement(name = "compound_action")
public class CompoundAction
    extends XmlAction
{

    @XmlElements({
        @XmlElement(name = "compound_action", type = CompoundAction.class),
        @XmlElement(name = "select_node_action", type = SelectNodeAction.class),
        @XmlElement(name = "cut_node_action", type = CutNodeAction.class),
        @XmlElement(name = "paste_node_action", type = PasteNodeAction.class),
        @XmlElement(name = "undo_paste_node_action", type = UndoPasteNodeAction.class),
        @XmlElement(name = "revert_xml_action", type = RevertXmlAction.class),
        @XmlElement(name = "bold_node_action", type = BoldNodeAction.class),
        @XmlElement(name = "strikethrough_node_action", type = StrikethroughNodeAction.class),
        @XmlElement(name = "italic_node_action", type = ItalicNodeAction.class),
        @XmlElement(name = "underlined_node_action", type = UnderlinedNodeAction.class),
        @XmlElement(name = "font_size_node_action", type = FontSizeNodeAction.class),
        @XmlElement(name = "font_node_action", type = FontNodeAction.class),
        @XmlElement(name = "node_color_format_action", type = NodeColorFormatAction.class),
        @XmlElement(name = "node_background_color_format_action", type = NodeBackgroundColorFormatAction.class),
        @XmlElement(name = "node_style_format_action", type = NodeStyleFormatAction.class),
        @XmlElement(name = "edge_color_format_action", type = EdgeColorFormatAction.class),
        @XmlElement(name = "edge_width_format_action", type = EdgeWidthFormatAction.class),
        @XmlElement(name = "edge_style_format_action", type = EdgeStyleFormatAction.class),
        @XmlElement(name = "delete_node_action", type = DeleteNodeAction.class),
        @XmlElement(name = "edit_node_action", type = EditNodeAction.class),
        @XmlElement(name = "new_node_action", type = NewNodeAction.class),
        @XmlElement(name = "fold_action", type = FoldAction.class),
        @XmlElement(name = "move_nodes_action", type = MoveNodesAction.class),
        @XmlElement(name = "hook_node_action", type = HookNodeAction.class),
        @XmlElement(name = "add_icon_action", type = AddIconAction.class),
        @XmlElement(name = "remove_icon_xml_action", type = RemoveIconXmlAction.class),
        @XmlElement(name = "remove_all_icons_xml_action", type = RemoveAllIconsXmlAction.class),
        @XmlElement(name = "move_node_xml_action", type = MoveNodeXmlAction.class),
        @XmlElement(name = "add_cloud_xml_action", type = AddCloudXmlAction.class),
        @XmlElement(name = "cloud_color_xml_action", type = CloudColorXmlAction.class),
        @XmlElement(name = "add_arrow_link_xml_action", type = AddArrowLinkXmlAction.class),
        @XmlElement(name = "add_link_xml_action", type = AddLinkXmlAction.class),
        @XmlElement(name = "remove_arrow_link_xml_action", type = RemoveArrowLinkXmlAction.class),
        @XmlElement(name = "arrow_link_color_xml_action", type = ArrowLinkColorXmlAction.class),
        @XmlElement(name = "arrow_link_arrow_xml_action", type = ArrowLinkArrowXmlAction.class),
        @XmlElement(name = "arrow_link_point_xml_action", type = ArrowLinkPointXmlAction.class),
        @XmlElement(name = "set_attribute_action", type = SetAttributeAction.class),
        @XmlElement(name = "insert_attribute_action", type = InsertAttributeAction.class),
        @XmlElement(name = "add_attribute_action", type = AddAttributeAction.class),
        @XmlElement(name = "remove_attribute_action", type = RemoveAttributeAction.class),
        @XmlElement(name = "edit_note_to_node_action", type = EditNoteToNodeAction.class),
        @XmlElement(name = "place_node_xml_action", type = PlaceNodeXmlAction.class)
    })
    protected List<XmlAction> compoundActionOrSelectNodeActionOrCutNodeAction;

    /**
     * Gets the value of the compoundActionOrSelectNodeActionOrCutNodeAction property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the compoundActionOrSelectNodeActionOrCutNodeAction property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getCompoundActionOrSelectNodeActionOrCutNodeAction().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AddArrowLinkXmlAction }
     * {@link AddAttributeAction }
     * {@link AddCloudXmlAction }
     * {@link AddIconAction }
     * {@link AddLinkXmlAction }
     * {@link ArrowLinkArrowXmlAction }
     * {@link ArrowLinkColorXmlAction }
     * {@link ArrowLinkPointXmlAction }
     * {@link BoldNodeAction }
     * {@link CloudColorXmlAction }
     * {@link CompoundAction }
     * {@link CutNodeAction }
     * {@link DeleteNodeAction }
     * {@link EdgeColorFormatAction }
     * {@link EdgeStyleFormatAction }
     * {@link EdgeWidthFormatAction }
     * {@link EditNodeAction }
     * {@link EditNoteToNodeAction }
     * {@link FoldAction }
     * {@link FontNodeAction }
     * {@link FontSizeNodeAction }
     * {@link HookNodeAction }
     * {@link InsertAttributeAction }
     * {@link ItalicNodeAction }
     * {@link MoveNodeXmlAction }
     * {@link MoveNodesAction }
     * {@link NewNodeAction }
     * {@link NodeBackgroundColorFormatAction }
     * {@link NodeColorFormatAction }
     * {@link NodeStyleFormatAction }
     * {@link PasteNodeAction }
     * {@link PlaceNodeXmlAction }
     * {@link RemoveAllIconsXmlAction }
     * {@link RemoveArrowLinkXmlAction }
     * {@link RemoveAttributeAction }
     * {@link RemoveIconXmlAction }
     * {@link RevertXmlAction }
     * {@link SelectNodeAction }
     * {@link SetAttributeAction }
     * {@link StrikethroughNodeAction }
     * {@link UnderlinedNodeAction }
     * {@link UndoPasteNodeAction }
     * </p>
     *
     *
     * @return
     *     The value of the compoundActionOrSelectNodeActionOrCutNodeAction property.
     */
    public List<XmlAction> getCompoundActionOrSelectNodeActionOrCutNodeAction() {
        if (compoundActionOrSelectNodeActionOrCutNodeAction == null) {
            compoundActionOrSelectNodeActionOrCutNodeAction = new ArrayList<XmlAction>();
        }
        return this.compoundActionOrSelectNodeActionOrCutNodeAction;
    }

    // JiBX backward-compatibility methods (manual addition - preserve on regeneration)

    public List<XmlAction> getListChoiceList() {
        return getCompoundActionOrSelectNodeActionOrCutNodeAction();
    }

    public void addChoice(XmlAction action) {
        getCompoundActionOrSelectNodeActionOrCutNodeAction().add(action);
    }

    public int sizeChoiceList() {
        return getCompoundActionOrSelectNodeActionOrCutNodeAction().size();
    }

    public XmlAction getChoice(int index) {
        return getCompoundActionOrSelectNodeActionOrCutNodeAction().get(index);
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public void addAtChoice(int index, XmlAction action) {
        getCompoundActionOrSelectNodeActionOrCutNodeAction().add(index, action);
    }

    // JiBX backward-compatibility method (manual addition - preserve on regeneration)
    public void setAtChoice(int index, XmlAction action) {
        getCompoundActionOrSelectNodeActionOrCutNodeAction().set(index, action);
    }

}
