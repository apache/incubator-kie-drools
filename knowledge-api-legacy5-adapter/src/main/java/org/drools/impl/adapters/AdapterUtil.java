/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.impl.adapters;

import org.drools.builder.conf.AccumulateFunctionOption;
import org.drools.builder.conf.ClassLoaderCacheOption;
import org.drools.builder.conf.DefaultDialectOption;
import org.drools.builder.conf.DefaultPackageNameOption;
import org.drools.builder.conf.DumpDirOption;
import org.drools.builder.conf.EvaluatorOption;
import org.drools.builder.conf.KBuilderSeverityOption;
import org.drools.builder.conf.KnowledgeBuilderOption;
import org.drools.builder.conf.LanguageLevelOption;
import org.drools.builder.conf.MultiValueKnowledgeBuilderOption;
import org.drools.builder.conf.ProcessStringEscapesOption;
import org.drools.builder.conf.PropertySpecificOption;
import org.drools.builder.conf.SingleValueKnowledgeBuilderOption;
import org.drools.conf.AssertBehaviorOption;
import org.drools.conf.KnowledgeBaseOption;
import org.drools.conf.MultiValueKnowledgeBaseOption;
import org.drools.conf.SingleValueKnowledgeBaseOption;
import org.drools.definition.KnowledgeDefinition;
import org.drools.runtime.conf.KnowledgeSessionOption;
import org.drools.runtime.conf.MultiValueKnowledgeSessionOption;
import org.drools.runtime.conf.SingleValueKnowledgeSessionOption;
import org.kie.api.conf.DeclarativeAgendaOption;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.conf.MBeansOption;
import org.kie.api.conf.MultiValueKieBaseOption;
import org.kie.api.conf.RemoveIdentitiesOption;
import org.kie.api.conf.SingleValueKieBaseOption;
import org.kie.api.definition.KieDefinition;
import org.kie.api.runtime.conf.BeliefSystemTypeOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.KeepReferenceOption;
import org.kie.api.runtime.conf.KieSessionOption;
import org.kie.api.runtime.conf.MultiValueKieSessionOption;
import org.kie.api.runtime.conf.QueryListenerOption;
import org.kie.api.runtime.conf.SingleValueKieSessionOption;
import org.kie.api.runtime.conf.TimerJobFactoryOption;
import org.kie.api.runtime.conf.WorkItemHandlerOption;
import org.kie.api.runtime.rule.EvaluatorDefinition;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.conf.AlphaThresholdOption;
import org.kie.internal.conf.CompositeKeyDepthOption;
import org.kie.internal.conf.IndexLeftBetaMemoryOption;
import org.kie.internal.conf.IndexPrecedenceOption;
import org.kie.internal.conf.IndexRightBetaMemoryOption;
import org.kie.internal.conf.MaxThreadsOption;
import org.kie.internal.conf.PermGenThresholdOption;
import org.kie.internal.conf.SequentialAgendaOption;
import org.kie.internal.conf.SequentialOption;
import org.kie.internal.conf.ShareAlphaNodesOption;
import org.kie.internal.conf.ShareBetaNodesOption;

public class AdapterUtil {

    public static KnowledgeDefinition.KnowledgeType adaptKnowledgeType(KieDefinition.KnowledgeType type) {
        return KnowledgeDefinition.KnowledgeType.valueOf(type.name());
    }

    public static org.drools.builder.ResultSeverity adaptResultSeverity(ResultSeverity resultSeverity) {
        return org.drools.builder.ResultSeverity.valueOf(resultSeverity.name());
    }

    public static ResultSeverity adaptResultSeverity(org.drools.builder.ResultSeverity resultSeverity) {
        return ResultSeverity.valueOf(resultSeverity.name());
    }

    public static org.drools.builder.ResultSeverity[] adaptResultSeverity(ResultSeverity[] resultSeverities) {
        org.drools.builder.ResultSeverity[] result = new org.drools.builder.ResultSeverity[resultSeverities.length];
        for (int i = 0; i < resultSeverities.length; i++) {
            result[i] = adaptResultSeverity(resultSeverities[i]);
        }
        return result;
    }

    public static ResultSeverity[] adaptResultSeverity(org.drools.builder.ResultSeverity[] resultSeverities) {
        ResultSeverity[] result = new ResultSeverity[resultSeverities.length];
        for (int i = 0; i < resultSeverities.length; i++) {
            result[i] = adaptResultSeverity(resultSeverities[i]);
        }
        return result;
    }

    public static KieBaseOption adaptOption(KnowledgeBaseOption option) {
        if (option instanceof org.drools.conf.AlphaThresholdOption) {
            org.drools.conf.AlphaThresholdOption legacyOption = (org.drools.conf.AlphaThresholdOption)option;
            return AlphaThresholdOption.get(legacyOption.getThreshold());
        }
        if (option instanceof org.drools.conf.CompositeKeyDepthOption) {
            org.drools.conf.CompositeKeyDepthOption legacyOption = (org.drools.conf.CompositeKeyDepthOption)option;
            return CompositeKeyDepthOption.get(legacyOption.getDepth());
        }
        if (option instanceof org.drools.builder.conf.DeclarativeAgendaOption) {
            org.drools.builder.conf.DeclarativeAgendaOption legacyOption = (org.drools.builder.conf.DeclarativeAgendaOption)option;
            switch (legacyOption) {
                case ENABLED:
                    return DeclarativeAgendaOption.ENABLED;
                case DISABLED:
                    return DeclarativeAgendaOption.DISABLED;
            }
        }
        if (option instanceof AssertBehaviorOption) {
            AssertBehaviorOption legacyOption = (AssertBehaviorOption)option;
            switch (legacyOption) {
                case IDENTITY:
                    return EqualityBehaviorOption.IDENTITY;
                case EQUALITY:
                    return EqualityBehaviorOption.EQUALITY;
            }
        }
        if (option instanceof org.drools.conf.EventProcessingOption) {
            org.drools.conf.EventProcessingOption legacyOption = (org.drools.conf.EventProcessingOption)option;
            switch (legacyOption) {
                case CLOUD:
                    return EventProcessingOption.CLOUD;
                case STREAM:
                    return EventProcessingOption.STREAM;
            }
        }
        if (option instanceof org.drools.conf.IndexLeftBetaMemoryOption) {
            org.drools.conf.IndexLeftBetaMemoryOption legacyOption = (org.drools.conf.IndexLeftBetaMemoryOption)option;
            switch (legacyOption) {
                case YES:
                    return IndexLeftBetaMemoryOption.YES;
                case NO:
                    return IndexLeftBetaMemoryOption.NO;
            }
        }
        if (option instanceof org.drools.conf.IndexPrecedenceOption) {
            org.drools.conf.IndexPrecedenceOption legacyOption = (org.drools.conf.IndexPrecedenceOption)option;
            switch (legacyOption) {
                case PATTERN_ORDER:
                    return IndexPrecedenceOption.PATTERN_ORDER;
                case EQUALITY_PRIORITY:
                    return IndexPrecedenceOption.EQUALITY_PRIORITY;
            }
        }
        if (option instanceof org.drools.conf.IndexRightBetaMemoryOption) {
            org.drools.conf.IndexRightBetaMemoryOption legacyOption = (org.drools.conf.IndexRightBetaMemoryOption)option;
            switch (legacyOption) {
                case YES:
                    return IndexRightBetaMemoryOption.YES;
                case NO:
                    return IndexRightBetaMemoryOption.NO;
            }
        }
        if (option instanceof org.drools.conf.MBeansOption) {
            org.drools.conf.MBeansOption legacyOption = (org.drools.conf.MBeansOption)option;
            switch (legacyOption) {
                case ENABLED:
                    return MBeansOption.ENABLED;
                case DISABLED:
                    return MBeansOption.DISABLED;
            }
        }
        if (option instanceof org.drools.conf.MaxThreadsOption) {
            org.drools.conf.MaxThreadsOption legacyOption = (org.drools.conf.MaxThreadsOption)option;
            return MaxThreadsOption.get(legacyOption.getMaxThreads());
        }
        if (option instanceof org.drools.conf.PermGenThresholdOption) {
            org.drools.conf.PermGenThresholdOption legacyOption = (org.drools.conf.PermGenThresholdOption)option;
            return PermGenThresholdOption.get(legacyOption.getThreshold());
        }
        if (option instanceof org.drools.conf.RemoveIdentitiesOption) {
            org.drools.conf.RemoveIdentitiesOption legacyOption = (org.drools.conf.RemoveIdentitiesOption)option;
            switch (legacyOption) {
                case YES:
                    return RemoveIdentitiesOption.YES;
                case NO:
                    return RemoveIdentitiesOption.NO;
            }
        }
        if (option instanceof org.drools.conf.SequentialAgendaOption) {
            org.drools.conf.SequentialAgendaOption legacyOption = (org.drools.conf.SequentialAgendaOption)option;
            switch (legacyOption) {
                case SEQUENTIAL:
                    return SequentialAgendaOption.SEQUENTIAL;
                case DYNAMIC:
                    return SequentialAgendaOption.DYNAMIC;
            }
        }
        if (option instanceof org.drools.conf.SequentialOption) {
            org.drools.conf.SequentialOption legacyOption = (org.drools.conf.SequentialOption)option;
            switch (legacyOption) {
                case YES:
                    return SequentialOption.YES;
                case NO:
                    return SequentialOption.NO;
            }
        }
        if (option instanceof org.drools.conf.ShareAlphaNodesOption) {
            org.drools.conf.ShareAlphaNodesOption legacyOption = (org.drools.conf.ShareAlphaNodesOption)option;
            switch (legacyOption) {
                case YES:
                    return ShareAlphaNodesOption.YES;
                case NO:
                    return ShareAlphaNodesOption.NO;
            }
        }
        if (option instanceof org.drools.conf.ShareBetaNodesOption) {
            org.drools.conf.ShareBetaNodesOption legacyOption = (org.drools.conf.ShareBetaNodesOption)option;
            switch (legacyOption) {
                case YES:
                    return ShareBetaNodesOption.YES;
                case NO:
                    return ShareBetaNodesOption.NO;
            }
        }
        throw new UnsupportedOperationException("Unknown option " + option);
    }

    public static KieSessionOption adaptOption(KnowledgeSessionOption option) {
        if (option instanceof org.drools.runtime.conf.BeliefSystemTypeOption) {
            org.drools.runtime.conf.BeliefSystemTypeOption legacyOption = (org.drools.runtime.conf.BeliefSystemTypeOption)option;
            return BeliefSystemTypeOption.get(legacyOption.getBeliefSystemType());
        }
        if (option instanceof org.drools.runtime.conf.ClockTypeOption) {
            org.drools.runtime.conf.ClockTypeOption legacyOption = (org.drools.runtime.conf.ClockTypeOption)option;
            return ClockTypeOption.get(legacyOption.getClockType());
        }
        if (option instanceof org.drools.runtime.conf.KeepReferenceOption) {
            org.drools.runtime.conf.KeepReferenceOption legacyOption = (org.drools.runtime.conf.KeepReferenceOption)option;
            switch (legacyOption) {
                case YES:
                    return KeepReferenceOption.YES;
                case NO:
                    return KeepReferenceOption.NO;
            }
        }
        if (option instanceof org.drools.runtime.conf.QueryListenerOption) {
            org.drools.runtime.conf.QueryListenerOption legacyOption = (org.drools.runtime.conf.QueryListenerOption)option;
            switch (legacyOption) {
                case STANDARD:
                    return QueryListenerOption.STANDARD;
                case LIGHTWEIGHT:
                    return QueryListenerOption.LIGHTWEIGHT;
            }
        }
        if (option instanceof org.drools.runtime.conf.TimerJobFactoryOption) {
            org.drools.runtime.conf.TimerJobFactoryOption legacyOption = (org.drools.runtime.conf.TimerJobFactoryOption)option;
            return TimerJobFactoryOption.get(legacyOption.getTimerJobType());
        }
        if (option instanceof org.drools.runtime.conf.WorkItemHandlerOption) {
            org.drools.runtime.conf.WorkItemHandlerOption legacyOption = (org.drools.runtime.conf.WorkItemHandlerOption)option;
            return WorkItemHandlerOption.get(legacyOption.getName(),
                                             new WorkItemHandlerAdapter(legacyOption.getHandler()));
        }
        throw new UnsupportedOperationException("Unknown option " + option);
    }

    public static SingleValueKnowledgeBaseOption adaptOption(SingleValueKieBaseOption option) {
        if (option instanceof AlphaThresholdOption) {
            AlphaThresholdOption kieOption = (AlphaThresholdOption)option;
            return org.drools.conf.AlphaThresholdOption.get(kieOption.getThreshold());
        }
        if (option instanceof CompositeKeyDepthOption) {
            CompositeKeyDepthOption kieOption = (CompositeKeyDepthOption)option;
            return org.drools.conf.CompositeKeyDepthOption.get(kieOption.getDepth());
        }
        if (option instanceof DeclarativeAgendaOption) {
            DeclarativeAgendaOption kieOption = (DeclarativeAgendaOption)option;
            switch (kieOption) {
                case ENABLED:
                    return org.drools.builder.conf.DeclarativeAgendaOption.ENABLED;
                case DISABLED:
                    return org.drools.builder.conf.DeclarativeAgendaOption.DISABLED;
            }
        }
        if (option instanceof EqualityBehaviorOption) {
            EqualityBehaviorOption kieOption = (EqualityBehaviorOption)option;
            switch (kieOption) {
                case IDENTITY:
                    return AssertBehaviorOption.IDENTITY;
                case EQUALITY:
                    return AssertBehaviorOption.EQUALITY;
            }
        }
        if (option instanceof EventProcessingOption) {
            EventProcessingOption kieOption = (EventProcessingOption)option;
            switch (kieOption) {
                case CLOUD:
                    return org.drools.conf.EventProcessingOption.CLOUD;
                case STREAM:
                    return org.drools.conf.EventProcessingOption.STREAM;
            }
        }
        if (option instanceof IndexLeftBetaMemoryOption) {
            IndexLeftBetaMemoryOption kieOption = (IndexLeftBetaMemoryOption)option;
            switch (kieOption) {
                case YES:
                    return org.drools.conf.IndexLeftBetaMemoryOption.YES;
                case NO:
                    return org.drools.conf.IndexLeftBetaMemoryOption.NO;
            }
        }
        if (option instanceof IndexPrecedenceOption) {
            IndexPrecedenceOption kieOption = (IndexPrecedenceOption)option;
            switch (kieOption) {
                case PATTERN_ORDER:
                    return org.drools.conf.IndexPrecedenceOption.PATTERN_ORDER;
                case EQUALITY_PRIORITY:
                    return org.drools.conf.IndexPrecedenceOption.EQUALITY_PRIORITY;
            }
        }
        if (option instanceof IndexRightBetaMemoryOption) {
            IndexRightBetaMemoryOption kieOption = (IndexRightBetaMemoryOption)option;
            switch (kieOption) {
                case YES:
                    return org.drools.conf.IndexRightBetaMemoryOption.YES;
                case NO:
                    return org.drools.conf.IndexRightBetaMemoryOption.NO;
            }
        }
        if (option instanceof MBeansOption) {
            MBeansOption kieOption = (MBeansOption)option;
            switch (kieOption) {
                case ENABLED:
                    return org.drools.conf.MBeansOption.ENABLED;
                case DISABLED:
                    return org.drools.conf.MBeansOption.DISABLED;
            }
        }
        if (option instanceof MaxThreadsOption) {
            MaxThreadsOption kieOption = (MaxThreadsOption)option;
            return org.drools.conf.MaxThreadsOption.get(kieOption.getMaxThreads());
        }
        if (option instanceof PermGenThresholdOption) {
            PermGenThresholdOption kieOption = (PermGenThresholdOption)option;
            return org.drools.conf.PermGenThresholdOption.get(kieOption.getThreshold());
        }
        if (option instanceof RemoveIdentitiesOption) {
            RemoveIdentitiesOption kieOption = (RemoveIdentitiesOption)option;
            switch (kieOption) {
                case YES:
                    return org.drools.conf.RemoveIdentitiesOption.YES;
                case NO:
                    return org.drools.conf.RemoveIdentitiesOption.NO;
            }
        }
        if (option instanceof SequentialAgendaOption) {
            SequentialAgendaOption kieOption = (SequentialAgendaOption)option;
            switch (kieOption) {
                case SEQUENTIAL:
                    return org.drools.conf.SequentialAgendaOption.SEQUENTIAL;
                case DYNAMIC:
                    return org.drools.conf.SequentialAgendaOption.DYNAMIC;
            }
        }
        if (option instanceof SequentialOption) {
            SequentialOption kieOption = (SequentialOption)option;
            switch (kieOption) {
                case YES:
                    return org.drools.conf.SequentialOption.YES;
                case NO:
                    return org.drools.conf.SequentialOption.NO;
            }
        }
        if (option instanceof ShareAlphaNodesOption) {
            ShareAlphaNodesOption kieOption = (ShareAlphaNodesOption)option;
            switch (kieOption) {
                case YES:
                    return org.drools.conf.ShareAlphaNodesOption.YES;
                case NO:
                    return org.drools.conf.ShareAlphaNodesOption.NO;
            }
        }
        if (option instanceof ShareBetaNodesOption) {
            ShareBetaNodesOption kieOption = (ShareBetaNodesOption)option;
            switch (kieOption) {
                case YES:
                    return org.drools.conf.ShareBetaNodesOption.YES;
                case NO:
                    return org.drools.conf.ShareBetaNodesOption.NO;
            }
        }
        throw new UnsupportedOperationException("Unknown option " + option);
    }

    public static MultiValueKnowledgeBaseOption adaptOption(MultiValueKieBaseOption option) {
        throw new UnsupportedOperationException("Unknown option " + option);
    }

    public static Class<? extends SingleValueKieBaseOption> adaptSingleValueBaseOption(Class<? extends SingleValueKnowledgeBaseOption> option) {
        if (option == AssertBehaviorOption.class) {
            return EqualityBehaviorOption.class;
        }
        try {
            return (Class<? extends SingleValueKieBaseOption>)Class.forName("org.kie.api.conf." + option.getSimpleName());
        } catch (ClassNotFoundException e1) {
            try {
                return (Class<? extends SingleValueKieBaseOption>)Class.forName("org.kie.internal.conf." + option.getSimpleName());
            } catch (ClassNotFoundException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    public static Class<? extends MultiValueKieBaseOption> adaptMultiValueBaseOption(Class<? extends MultiValueKnowledgeBaseOption> option) {
        try {
            return (Class<? extends MultiValueKieBaseOption>)Class.forName("org.kie.api.conf." + option.getSimpleName());
        } catch (ClassNotFoundException e1) {
            try {
                return (Class<? extends MultiValueKieBaseOption>)Class.forName("org.kie.internal.conf." + option.getSimpleName());
            } catch (ClassNotFoundException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    public static SingleValueKnowledgeSessionOption adaptOption(SingleValueKieSessionOption option) {
        if (option instanceof BeliefSystemTypeOption) {
            BeliefSystemTypeOption kieOption = (BeliefSystemTypeOption)option;
            return org.drools.runtime.conf.BeliefSystemTypeOption.get(kieOption.getBeliefSystemType());
        }
        if (option instanceof ClockTypeOption) {
            ClockTypeOption kieOption = (ClockTypeOption)option;
            return org.drools.runtime.conf.ClockTypeOption.get(kieOption.getClockType());
        }
        if (option instanceof KeepReferenceOption) {
            KeepReferenceOption kieOption = (KeepReferenceOption)option;
            switch (kieOption) {
                case YES:
                    return org.drools.runtime.conf.KeepReferenceOption.YES;
                case NO:
                    return org.drools.runtime.conf.KeepReferenceOption.NO;
            }
        }
        if (option instanceof QueryListenerOption) {
            QueryListenerOption kieOption = (QueryListenerOption)option;
            switch (kieOption) {
                case STANDARD:
                    return org.drools.runtime.conf.QueryListenerOption.STANDARD;
                case LIGHTWEIGHT:
                    return org.drools.runtime.conf.QueryListenerOption.LIGHTWEIGHT;
            }
        }
        if (option instanceof TimerJobFactoryOption) {
            TimerJobFactoryOption kieOption = (TimerJobFactoryOption)option;
            return org.drools.runtime.conf.TimerJobFactoryOption.get(kieOption.getTimerJobType());
        }
        throw new UnsupportedOperationException("Unknown option " + option);
    }

    public static MultiValueKnowledgeSessionOption adaptOption(MultiValueKieSessionOption option) {
        if (option instanceof WorkItemHandlerOption) {
            WorkItemHandlerOption kieOption = (WorkItemHandlerOption)option;
            return org.drools.runtime.conf.WorkItemHandlerOption.get(kieOption.getName(),
                                                                     new WorkItemHandlerKieAdapter(kieOption.getHandler()));
        }
        throw new UnsupportedOperationException("Unknown option " + option);
    }

    public static Class<? extends SingleValueKieSessionOption> adaptSingleValueSessionOption(Class<? extends SingleValueKnowledgeSessionOption> option) {
        try {
            return (Class<? extends SingleValueKieSessionOption>)Class.forName("org.kie.api.runtime.conf." + option.getSimpleName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<? extends MultiValueKieSessionOption> adaptMultiValueSessionOption(Class<? extends MultiValueKnowledgeSessionOption> option) {
        try {
            return (Class<? extends MultiValueKieSessionOption>)Class.forName("org.kie.api.runtime.conf." + option.getSimpleName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static org.kie.internal.builder.conf.KnowledgeBuilderOption adaptOption(KnowledgeBuilderOption option) {
        if (option instanceof AccumulateFunctionOption) {
            AccumulateFunctionOption legacyOption = (AccumulateFunctionOption)option;
            return org.kie.internal.builder.conf.AccumulateFunctionOption.get(legacyOption.getName(),
                                                                              new AccumulateFunctionAdapter(legacyOption.getFunction()));
        }
        if (option instanceof ClassLoaderCacheOption) {
            ClassLoaderCacheOption legacyOption = (ClassLoaderCacheOption)option;
            switch (legacyOption) {
                case DISABLED:
                    return org.kie.internal.builder.conf.ClassLoaderCacheOption.DISABLED;
                case ENABLED:
                    return org.kie.internal.builder.conf.ClassLoaderCacheOption.ENABLED;
            }
        }
        if (option instanceof DefaultDialectOption) {
            DefaultDialectOption legacyOption = (DefaultDialectOption)option;
            return org.kie.internal.builder.conf.DefaultDialectOption.get(legacyOption.getName());
        }
        if (option instanceof DefaultPackageNameOption) {
            DefaultPackageNameOption legacyOption = (DefaultPackageNameOption)option;
            return org.kie.internal.builder.conf.DefaultPackageNameOption.get(legacyOption.getPackageName());
        }
        if (option instanceof DumpDirOption) {
            DumpDirOption legacyOption = (DumpDirOption)option;
            return org.kie.internal.builder.conf.DumpDirOption.get(legacyOption.getDirectory());
        }
        if (option instanceof EvaluatorOption) {
            EvaluatorOption legacyOption = (EvaluatorOption)option;
            return org.kie.internal.builder.conf.EvaluatorOption.get(legacyOption.getName(),
                                                                     (EvaluatorDefinition)legacyOption.getEvaluatorDefinition());
        }
        if (option instanceof KBuilderSeverityOption) {
            KBuilderSeverityOption legacyOption = (KBuilderSeverityOption)option;
            return org.kie.internal.builder.conf.KBuilderSeverityOption.get(legacyOption.getName(),
                                                                            legacyOption.getSeverity().toString());
        }
        if (option instanceof LanguageLevelOption) {
            LanguageLevelOption legacyOption = (LanguageLevelOption)option;
            switch (legacyOption) {
                case DRL5:
                    return org.kie.internal.builder.conf.LanguageLevelOption.DRL5;
                case DRL6:
                    return org.kie.internal.builder.conf.LanguageLevelOption.DRL6;
            }
        }
        if (option instanceof ProcessStringEscapesOption) {
            ProcessStringEscapesOption legacyOption = (ProcessStringEscapesOption)option;
            switch (legacyOption) {
                case YES:
                    return org.kie.internal.builder.conf.ProcessStringEscapesOption.YES;
                case NO:
                    return org.kie.internal.builder.conf.ProcessStringEscapesOption.NO;
            }

        }
        if (option instanceof PropertySpecificOption) {
            PropertySpecificOption legacyOption = (PropertySpecificOption)option;
            switch (legacyOption) {
                case ALLOWED:
                    return org.kie.internal.builder.conf.PropertySpecificOption.ALLOWED;
                case ALWAYS:
                    return org.kie.internal.builder.conf.PropertySpecificOption.ALWAYS;
                case DISABLED:
                    return org.kie.internal.builder.conf.PropertySpecificOption.DISABLED;
            }
        }
        throw new UnsupportedOperationException("Unknown option " + option);
    }

    public static Class<? extends org.kie.internal.builder.conf.SingleValueKnowledgeBuilderOption> adaptSingleValueBuilderOption(Class<? extends SingleValueKnowledgeBuilderOption> option) {
        try {
            return (Class<? extends org.kie.internal.builder.conf.SingleValueKnowledgeBuilderOption>)Class.forName("org.kie.internal.builder.conf." + option.getSimpleName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<? extends org.kie.internal.builder.conf.MultiValueKnowledgeBuilderOption> adaptMultiValueBuilderOption(Class<? extends MultiValueKnowledgeBuilderOption> option) {
        try {
            return (Class<? extends org.kie.internal.builder.conf.MultiValueKnowledgeBuilderOption>)Class.forName("org.kie.internal.builder.conf." + option.getSimpleName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
