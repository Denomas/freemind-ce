
package freemind.controller.actions.generated.instance;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *       &lt;sequence&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element ref="{}pattern"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "pattern"
})
@XmlRootElement(name = "patterns")
public class Patterns
    extends XmlAction
{

    protected List<Pattern> pattern;

    /**
     * Gets the value of the pattern property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pattern property.</p>
     *
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getPattern().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Pattern }
     * </p>
     *
     *
     * @return
     *     The value of the pattern property.
     */
    public List<Pattern> getPattern() {
        if (pattern == null) {
            pattern = new ArrayList<Pattern>();
        }
        return this.pattern;
    }

    // JiBX backward-compatibility methods (manual addition - preserve on regeneration)

    public List<Pattern> getListChoiceList() {
        return getPattern();
    }

    public void addChoice(Pattern p) {
        getPattern().add(p);
    }

    public int sizeChoiceList() {
        return getPattern().size();
    }

    public Pattern getChoice(int index) {
        return getPattern().get(index);
    }

}
