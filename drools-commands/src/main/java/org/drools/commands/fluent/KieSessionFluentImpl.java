package org.drools.commands.fluent;

import org.drools.commands.SetActiveAgendaGroup;
import org.drools.commands.runtime.DisposeCommand;
import org.drools.commands.runtime.GetGlobalCommand;
import org.drools.commands.runtime.rule.FireAllRulesCommand;
import org.drools.commands.runtime.rule.InsertObjectCommand;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.fluent.ExecutableBuilder;
import org.kie.internal.builder.fluent.KieSessionFluent;

public class KieSessionFluentImpl extends BaseBatchWithProcessFluent<KieSessionFluent, ExecutableBuilder> implements KieSessionFluent {

    public KieSessionFluentImpl(ExecutableImpl fluentCtx) {
        super(fluentCtx);
    }

    @Override
    public KieSessionFluent fireAllRules() {
        fluentCtx.addCommand(new FireAllRulesCommand());
        return this;
    }

    @Override
    public KieSessionFluent setGlobal(String identifier, Object object) {
        return this;
    }

    @Override
    public KieSessionFluent getGlobal(String identifier) {
        fluentCtx.addCommand(new GetGlobalCommand(identifier));
        return this;
    }

    @Override
    public KieSessionFluent insert(Object object) {
        fluentCtx.addCommand(new InsertObjectCommand(object));
        return this;
    }

    @Override
    public KieSessionFluent update(FactHandle handle, Object object) {
        return this;
    }

    @Override
    public KieSessionFluent delete(FactHandle handle) {
        return this;
    }

    @Override
    public KieSessionFluent setActiveRuleFlowGroup(String ruleFlowGroup) {
        return setActiveAgendaGroup(ruleFlowGroup);
    }

    @Override
    public KieSessionFluent setActiveAgendaGroup(String agendaGroup) {
        fluentCtx.addCommand(new SetActiveAgendaGroup(agendaGroup));
        return this;
    }

    @Override
    public ExecutableBuilder dispose() {
        fluentCtx.addCommand(new DisposeCommand());
        return fluentCtx.getExecutableBuilder();
    }
}