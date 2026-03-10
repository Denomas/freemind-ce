
package freemind.controller.actions.generated.instance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type</p>.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="color" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="start_date" use="required" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="end_date" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="repeat_type" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="never"/&gt;
 *             &lt;enumeration value="yearly"/&gt;
 *             &lt;enumeration value="yearly_every_nth_day"/&gt;
 *             &lt;enumeration value="yearly_every_nth_week"/&gt;
 *             &lt;enumeration value="yearly_every_nth_month"/&gt;
 *             &lt;enumeration value="monthly"/&gt;
 *             &lt;enumeration value="monthly_every_nth_day"/&gt;
 *             &lt;enumeration value="monthly_every_nth_week"/&gt;
 *             &lt;enumeration value="weekly"/&gt;
 *             &lt;enumeration value="weekly_every_nth_day"/&gt;
 *             &lt;enumeration value="DAILY"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="repeat_each_n_occurence" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="first_occurence" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "calendar_marking")
public class CalendarMarking {

    // JiBX backward-compatibility constants (manual addition - preserve on regeneration)
    public static final String NEVER = "never";
    public static final String YEARLY = "yearly";
    public static final String YEARLY_EVERY_NTH_DAY = "yearly_every_nth_day";
    public static final String YEARLY_EVERY_NTH_WEEK = "yearly_every_nth_week";
    public static final String YEARLY_EVERY_NTH_MONTH = "yearly_every_nth_month";
    public static final String MONTHLY = "monthly";
    public static final String MONTHLY_EVERY_NTH_DAY = "monthly_every_nth_day";
    public static final String MONTHLY_EVERY_NTH_WEEK = "monthly_every_nth_week";
    public static final String WEEKLY = "weekly";
    public static final String WEEKLY_EVERY_NTH_DAY = "weekly_every_nth_day";
    public static final String DAILY = "daily";

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "color", required = true)
    protected String color;
    @XmlAttribute(name = "start_date", required = true)
    protected long startDate;
    @XmlAttribute(name = "end_date")
    protected Long endDate;
    @XmlAttribute(name = "repeat_type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String repeatType;
    @XmlAttribute(name = "repeat_each_n_occurence")
    protected Integer repeatEachNOccurence;
    @XmlAttribute(name = "first_occurence")
    protected Integer firstOccurence;

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the color property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the value of the color property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setColor(String value) {
        this.color = value;
    }

    /**
     * Gets the value of the startDate property.
     *
     */
    public long getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     *
     */
    public void setStartDate(long value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     *
     * @return
     *     possible object is
     *     {@link Long }
     *
     */
    public Long getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     *
     * @param value
     *     allowed object is
     *     {@link Long }
     *
     */
    public void setEndDate(Long value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the repeatType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRepeatType() {
        return repeatType;
    }

    /**
     * Sets the value of the repeatType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRepeatType(String value) {
        this.repeatType = value;
    }

    /**
     * Gets the value of the repeatEachNOccurence property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getRepeatEachNOccurence() {
        return repeatEachNOccurence;
    }

    /**
     * Sets the value of the repeatEachNOccurence property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setRepeatEachNOccurence(Integer value) {
        this.repeatEachNOccurence = value;
    }

    /**
     * Gets the value of the firstOccurence property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getFirstOccurence() {
        return firstOccurence;
    }

    /**
     * Sets the value of the firstOccurence property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setFirstOccurence(Integer value) {
        this.firstOccurence = value;
    }

}
