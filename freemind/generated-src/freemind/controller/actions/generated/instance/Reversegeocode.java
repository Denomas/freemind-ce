
package freemind.controller.actions.generated.instance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element ref="{}result"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="attribution" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="querystring" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="timestamp" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "result"
})
@XmlRootElement(name = "reversegeocode")
public class Reversegeocode
    extends XmlAction
{

    @XmlElement(required = true)
    protected Result result;
    @XmlAttribute(name = "attribution", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String attribution;
    @XmlAttribute(name = "querystring", required = true)
    protected String querystring;
    @XmlAttribute(name = "timestamp", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String timestamp;

    /**
     * Gets the value of the result property.
     *
     * @return
     *     possible object is
     *     {@link Result }
     *
     */
    public Result getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     *
     * @param value
     *     allowed object is
     *     {@link Result }
     *
     */
    public void setResult(Result value) {
        this.result = value;
    }

    /**
     * Gets the value of the attribution property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAttribution() {
        return attribution;
    }

    /**
     * Sets the value of the attribution property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAttribution(String value) {
        this.attribution = value;
    }

    /**
     * Gets the value of the querystring property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getQuerystring() {
        return querystring;
    }

    /**
     * Sets the value of the querystring property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setQuerystring(String value) {
        this.querystring = value;
    }

    /**
     * Gets the value of the timestamp property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTimestamp(String value) {
        this.timestamp = value;
    }

    // JiBX backward-compatibility methods (manual addition - preserve on regeneration)

    public java.util.List<Result> getListResultList() {
        java.util.List<Result> list = new java.util.ArrayList<>();
        if (result != null) {
            list.add(result);
        }
        return list;
    }

    public Result getResult(int index) {
        if (index == 0 && result != null) {
            return result;
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }

}
