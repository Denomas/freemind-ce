
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
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="node" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "node_list_member")
public class NodeListMember {

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
