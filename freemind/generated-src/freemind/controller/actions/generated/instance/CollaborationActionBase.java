
package freemind.controller.actions.generated.instance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for collaboration_action_base complex type</p>.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType name="collaboration_action_base"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{}xml_action"&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "collaboration_action_base")
@XmlSeeAlso({
    CollaborationTransaction.class,
    CollaborationReceiveLock.class,
    CollaborationWrongMap.class,
    CollaborationWrongCredentials.class,
    CollaborationUnableToLock.class,
    CollaborationGoodbye.class,
    CollaborationWelcome.class,
    CollaborationOffers.class,
    CollaborationHello.class,
    CollaborationGetOffers.class,
    CollaborationPublishNewMap.class,
    CollaborationWhoAreYou.class,
    CollaborationUserInformation.class,
    CollaborationRequireLock.class
})
public class CollaborationActionBase
    extends XmlAction
{


}
