
package freemind.controller.actions.generated.instance;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "calendarMarking"
})
@XmlRootElement(name = "calendar_markings")
public class CalendarMarkings
    extends XmlAction
{

    @XmlElement(name = "calendar_marking")
    protected List<CalendarMarking> calendarMarking;

    public List<CalendarMarking> getCalendarMarking() {
        if (calendarMarking == null) {
            calendarMarking = new ArrayList<CalendarMarking>();
        }
        return this.calendarMarking;
    }

    // JiBX backward-compatibility methods (manual addition - preserve on regeneration)

    public int sizeCalendarMarkingList() {
        return getCalendarMarking().size();
    }

    public CalendarMarking getCalendarMarking(int index) {
        return getCalendarMarking().get(index);
    }

    public void addCalendarMarking(CalendarMarking marking) {
        getCalendarMarking().add(marking);
    }

    public void removeFromCalendarMarkingElementAt(int index) {
        getCalendarMarking().remove(index);
    }

}
