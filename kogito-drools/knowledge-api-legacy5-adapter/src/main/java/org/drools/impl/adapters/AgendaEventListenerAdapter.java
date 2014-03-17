package org.drools.impl.adapters;

import org.drools.event.rule.ActivationCancelledCause;
import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.AgendaGroup;
import org.drools.runtime.rule.RuleFlowGroup;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;

public class AgendaEventListenerAdapter implements org.kie.api.event.rule.AgendaEventListener {

    private final AgendaEventListener delegate;

    public AgendaEventListenerAdapter(AgendaEventListener delegate) {
        this.delegate = delegate;
    }

    public void matchCreated(final MatchCreatedEvent event) {
        delegate.activationCreated(new ActivationCreatedEvent() {
            public Activation getActivation() {
                return new ActivationAdapter(event.getMatch());
            }

            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
        });
    }

    public void matchCancelled(final MatchCancelledEvent event) {
        delegate.activationCancelled(new ActivationCancelledEvent() {
            public ActivationCancelledCause getCause() {
                switch (event.getCause()) {
                    case CLEAR:
                        return ActivationCancelledCause.CLEAR;
                    case FILTER:
                        return ActivationCancelledCause.FILTER;
                    case WME_MODIFY:
                        return ActivationCancelledCause.WME_MODIFY;
                }
                return null;
            }

            public Activation getActivation() {
                return new ActivationAdapter(event.getMatch());
            }

            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
        });
    }

    public void beforeMatchFired(final BeforeMatchFiredEvent event) {
        delegate.beforeActivationFired(new BeforeActivationFiredEvent() {
            public Activation getActivation() {
                return new ActivationAdapter(event.getMatch());
            }

            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
        });
    }

    public void afterMatchFired(final AfterMatchFiredEvent event) {
        delegate.afterActivationFired(new AfterActivationFiredEvent() {
            public Activation getActivation() {
                return new ActivationAdapter(event.getMatch());
            }

            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
        });
    }

    public void agendaGroupPopped(final AgendaGroupPoppedEvent event) {
        delegate.agendaGroupPopped(new org.drools.event.rule.AgendaGroupPoppedEvent() {
            public AgendaGroup getAgendaGroup() {
                return new AgendaGroupAdapter(event.getAgendaGroup());
            }

            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
        });
    }

    public void agendaGroupPushed(final AgendaGroupPushedEvent event) {
        delegate.agendaGroupPushed(new org.drools.event.rule.AgendaGroupPushedEvent() {
            public AgendaGroup getAgendaGroup() {
                return new AgendaGroupAdapter(event.getAgendaGroup());
            }

            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
        });
    }

    public void beforeRuleFlowGroupActivated(final RuleFlowGroupActivatedEvent event) {
        delegate.beforeRuleFlowGroupActivated(new org.drools.event.rule.RuleFlowGroupActivatedEvent() {
            public RuleFlowGroup getRuleFlowGroup() {
                return new RuleFlowGroupAdapter(event.getRuleFlowGroup());
            }

            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
        });
    }

    public void afterRuleFlowGroupActivated(final RuleFlowGroupActivatedEvent event) {
        delegate.afterRuleFlowGroupActivated(new org.drools.event.rule.RuleFlowGroupActivatedEvent() {
            public RuleFlowGroup getRuleFlowGroup() {
                return new RuleFlowGroupAdapter(event.getRuleFlowGroup());
            }

            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
        });
    }

    public void beforeRuleFlowGroupDeactivated(final RuleFlowGroupDeactivatedEvent event) {
        delegate.beforeRuleFlowGroupDeactivated(new org.drools.event.rule.RuleFlowGroupDeactivatedEvent() {
            public RuleFlowGroup getRuleFlowGroup() {
                return new RuleFlowGroupAdapter(event.getRuleFlowGroup());
            }

            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
        });
    }

    public void afterRuleFlowGroupDeactivated(final RuleFlowGroupDeactivatedEvent event) {
        delegate.afterRuleFlowGroupDeactivated(new org.drools.event.rule.RuleFlowGroupDeactivatedEvent() {
            public RuleFlowGroup getRuleFlowGroup() {
                return new RuleFlowGroupAdapter(event.getRuleFlowGroup());
            }

            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
        });
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AgendaEventListenerAdapter && delegate.equals(((AgendaEventListenerAdapter)obj).delegate);
    }
}
