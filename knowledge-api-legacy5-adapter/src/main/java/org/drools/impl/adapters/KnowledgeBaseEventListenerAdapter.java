package org.drools.impl.adapters;

import org.drools.KnowledgeBase;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.process.*;
import org.drools.definition.rule.Rule;
import org.drools.event.knowledgebase.AfterKnowledgeBaseLockedEvent;
import org.drools.event.knowledgebase.AfterKnowledgeBaseUnlockedEvent;
import org.drools.event.knowledgebase.AfterKnowledgePackageAddedEvent;
import org.drools.event.knowledgebase.AfterKnowledgePackageRemovedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgeBaseLockedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgeBaseUnlockedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgePackageAddedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgePackageRemovedEvent;
import org.drools.event.knowledgebase.KnowledgeBaseEventListener;
import org.kie.api.event.kiebase.AfterFunctionRemovedEvent;
import org.kie.api.event.kiebase.AfterKieBaseLockedEvent;
import org.kie.api.event.kiebase.AfterKieBaseUnlockedEvent;
import org.kie.api.event.kiebase.AfterKiePackageAddedEvent;
import org.kie.api.event.kiebase.AfterKiePackageRemovedEvent;
import org.kie.api.event.kiebase.AfterProcessAddedEvent;
import org.kie.api.event.kiebase.AfterProcessRemovedEvent;
import org.kie.api.event.kiebase.AfterRuleAddedEvent;
import org.kie.api.event.kiebase.AfterRuleRemovedEvent;
import org.kie.api.event.kiebase.BeforeFunctionRemovedEvent;
import org.kie.api.event.kiebase.BeforeKieBaseLockedEvent;
import org.kie.api.event.kiebase.BeforeKieBaseUnlockedEvent;
import org.kie.api.event.kiebase.BeforeKiePackageAddedEvent;
import org.kie.api.event.kiebase.BeforeKiePackageRemovedEvent;
import org.kie.api.event.kiebase.BeforeProcessAddedEvent;
import org.kie.api.event.kiebase.BeforeProcessRemovedEvent;
import org.kie.api.event.kiebase.BeforeRuleAddedEvent;
import org.kie.api.event.kiebase.BeforeRuleRemovedEvent;
import org.kie.api.event.kiebase.KieBaseEventListener;

public class KnowledgeBaseEventListenerAdapter implements KieBaseEventListener {

    private final KnowledgeBaseEventListener delegate;

    public KnowledgeBaseEventListenerAdapter(KnowledgeBaseEventListener delegate) {
        this.delegate = delegate;
    }

    public void beforeKiePackageAdded(final BeforeKiePackageAddedEvent event) {
        delegate.beforeKnowledgePackageAdded(new BeforeKnowledgePackageAddedEvent() {
            public KnowledgePackage getKnowledgePackage() {
                return new KnowledgePackageAdapter((org.kie.internal.definition.KnowledgePackage)event.getKiePackage());
            }

            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase)event.getKieBase());
            }
        });
    }

    public void afterKiePackageAdded(final AfterKiePackageAddedEvent event) {
        delegate.afterKnowledgePackageAdded(new AfterKnowledgePackageAddedEvent() {
            public KnowledgePackage getKnowledgePackage() {
                return new KnowledgePackageAdapter((org.kie.internal.definition.KnowledgePackage) event.getKiePackage());
            }

            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void beforeKiePackageRemoved(final BeforeKiePackageRemovedEvent event) {
        delegate.beforeKnowledgePackageRemoved(new BeforeKnowledgePackageRemovedEvent() {
            public KnowledgePackage getKnowledgePackage() {
                return new KnowledgePackageAdapter((org.kie.internal.definition.KnowledgePackage) event.getKiePackage());
            }

            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void afterKiePackageRemoved(final AfterKiePackageRemovedEvent event) {
        delegate.afterKnowledgePackageRemoved(new AfterKnowledgePackageRemovedEvent() {
            public KnowledgePackage getKnowledgePackage() {
                return new KnowledgePackageAdapter((org.kie.internal.definition.KnowledgePackage) event.getKiePackage());
            }

            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void beforeKieBaseLocked(final BeforeKieBaseLockedEvent event) {
        delegate.beforeKnowledgeBaseLocked(new BeforeKnowledgeBaseLockedEvent() {
            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void afterKieBaseLocked(final AfterKieBaseLockedEvent event) {
        delegate.afterKnowledgeBaseLocked(new AfterKnowledgeBaseLockedEvent() {
            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }

            public Rule getRule() {
                return new RuleAdapter(event.getRule());
            }
        });
    }

    public void beforeKieBaseUnlocked(final BeforeKieBaseUnlockedEvent event) {
        delegate.beforeKnowledgeBaseUnlocked(new BeforeKnowledgeBaseUnlockedEvent() {
            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void afterKieBaseUnlocked(final AfterKieBaseUnlockedEvent event) {
        delegate.afterKnowledgeBaseUnlocked(new AfterKnowledgeBaseUnlockedEvent() {
            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void beforeRuleAdded(final BeforeRuleAddedEvent event) {
        delegate.beforeRuleAdded(new org.drools.event.knowledgebase.BeforeRuleAddedEvent() {
            public Rule getRule() {
                return new RuleAdapter(event.getRule());
            }

            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void afterRuleAdded(final AfterRuleAddedEvent event) {
        delegate.afterRuleAdded(new org.drools.event.knowledgebase.AfterRuleAddedEvent() {
            public Rule getRule() {
                return new RuleAdapter(event.getRule());
            }

            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void beforeRuleRemoved(final BeforeRuleRemovedEvent event) {
        delegate.beforeRuleRemoved(new org.drools.event.knowledgebase.BeforeRuleRemovedEvent() {
            public Rule getRule() {
                return new RuleAdapter(event.getRule());
            }

            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void afterRuleRemoved(final AfterRuleRemovedEvent event) {
        delegate.afterRuleRemoved(new org.drools.event.knowledgebase.AfterRuleRemovedEvent() {
            public Rule getRule() {
                return new RuleAdapter(event.getRule());
            }

            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void beforeFunctionRemoved(final BeforeFunctionRemovedEvent event) {
        delegate.beforeFunctionRemoved(new org.drools.event.knowledgebase.BeforeFunctionRemovedEvent() {
            public String getFunction() {
                return event.getFunction();
            }

            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void afterFunctionRemoved(final AfterFunctionRemovedEvent event) {
        delegate.afterFunctionRemoved(new org.drools.event.knowledgebase.AfterFunctionRemovedEvent() {
            public String getFunction() {
                return event.getFunction();
            }

            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void beforeProcessAdded(final BeforeProcessAddedEvent event) {
        delegate.beforeProcessAdded(new org.drools.event.knowledgebase.BeforeProcessAddedEvent() {
            public org.drools.definition.process.Process getProcess() {
                return new ProcessAdapter(event.getProcess());
            }

            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void afterProcessAdded(final AfterProcessAddedEvent event) {
        delegate.afterProcessAdded(new org.drools.event.knowledgebase.AfterProcessAddedEvent() {
            public org.drools.definition.process.Process getProcess() {
                return new ProcessAdapter(event.getProcess());
            }

            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void beforeProcessRemoved(final BeforeProcessRemovedEvent event) {
        delegate.beforeProcessRemoved(new org.drools.event.knowledgebase.BeforeProcessRemovedEvent() {
            public org.drools.definition.process.Process getProcess() {
                return new ProcessAdapter(event.getProcess());
            }

            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    public void afterProcessRemoved(final AfterProcessRemovedEvent event) {
        delegate.afterProcessRemoved(new org.drools.event.knowledgebase.AfterProcessRemovedEvent() {
            public org.drools.definition.process.Process getProcess() {
                return new ProcessAdapter(event.getProcess());
            }

            public KnowledgeBase getKnowledgeBase() {
                return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase) event.getKieBase());
            }
        });
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof KnowledgeBaseEventListenerAdapter && delegate.equals(((KnowledgeBaseEventListenerAdapter)obj).delegate);
    }
}
