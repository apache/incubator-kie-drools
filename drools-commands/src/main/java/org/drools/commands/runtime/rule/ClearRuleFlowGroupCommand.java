package org.drools.commands.runtime.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.common.ReteEvaluator;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ClearRuleFlowGroupCommand implements ExecutableCommand<Void> {

    @XmlAttribute(required=true)
    private String name;

    public ClearRuleFlowGroupCommand() {
    }

    public ClearRuleFlowGroupCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        ((ReteEvaluator)ksession).getActivationsManager().getAgendaGroupsManager().getAgendaGroup( this.name ).clear();
        return null;
    }

    public String toString() {
        return "session.getAgenda().getRuleFlowGroup(" + name + ").clear();";
    }

}
