/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import org.drools.base.rule.consequence.ConflictResolver;
import org.drools.core.runtime.rule.impl.DefaultConsequenceExceptionHandler;
import org.drools.util.StringUtils;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.BetaRangeIndexOption;
import org.kie.api.conf.ConfigurationKey;
import org.kie.api.conf.DeclarativeAgendaOption;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.conf.MultiValueKieBaseOption;
import org.kie.api.conf.OptionKey;
import org.kie.api.conf.PrototypesOption;
import org.kie.api.conf.RemoveIdentitiesOption;
import org.kie.api.conf.SequentialOption;
import org.kie.api.conf.SessionsPoolOption;
import org.kie.api.conf.SingleValueKieBaseOption;
import org.kie.api.runtime.rule.ConsequenceExceptionHandler;
import org.kie.internal.conf.AlphaRangeIndexThresholdOption;
import org.kie.internal.conf.AlphaThresholdOption;
import org.kie.internal.conf.CompositeConfiguration;
import org.kie.internal.conf.CompositeKeyDepthOption;
import org.kie.internal.conf.ConsequenceExceptionHandlerOption;
import org.kie.internal.conf.ConstraintJittingThresholdOption;
import org.kie.internal.conf.IndexLeftBetaMemoryOption;
import org.kie.internal.conf.IndexPrecedenceOption;
import org.kie.internal.conf.IndexRightBetaMemoryOption;
import org.kie.internal.conf.MaxThreadsOption;
import org.kie.internal.conf.ParallelExecutionOption;
import org.kie.internal.conf.SequentialAgendaOption;
import org.kie.internal.conf.ShareAlphaNodesOption;
import org.kie.internal.conf.ShareBetaNodesOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RuleBaseConfiguration
 *
 * A class to store RuleBase related configuration. It must be used at rule base instantiation time
 * or not used at all.
 * This class will automatically load default values from system properties, so if you want to set
 * a default configuration value for all your new rule bases, you can simply set the property as
 * a System property.
 *
 * After RuleBase is created, it makes the configuration immutable and there is no way to make it
 * mutable again. This is to avoid inconsistent behavior inside rulebase.
 *
 * NOTE: This API is under review and may change in the future.
 */

/**
 * Available configuration options:
 * <pre>
 * drools.maintainTms = &lt;true|false&gt;
 * drools.sequential = &lt;true|false&gt;
 * drools.sequential.agenda = &lt;sequential|dynamic&gt;
 * drools.removeIdentities = &lt;true|false&gt;
 * drools.shareAlphaNodes  = &lt;true|false&gt;
 * drools.shareBetaNodes = &lt;true|false&gt;
 * drools.alphaNodeHashingThreshold = &lt;1...n&gt;
 * drools.alphaNodeRangeIndexThreshold = &lt;1...n&gt;
 * drools.betaNodeRangeIndexEnabled = &lt;true|false&gt;
 * drools.sessionPool = &lt;1...n&gt;
 * drools.compositeKeyDepth = &lt;1..3&gt;
 * drools.indexLeftBetaMemory = &lt;true/false&gt;
 * drools.indexRightBetaMemory = &lt;true/false&gt;
 * drools.equalityBehavior = &lt;identity|equality&gt;
 * drools.conflictResolver = &lt;qualified class name&gt;
 * drools.consequenceExceptionHandler = &lt;qualified class name&gt;
 * drools.ruleBaseUpdateHandler = &lt;qualified class name&gt;
 * drools.sessionClock = &lt;qualified class name&gt;
 * drools.mbeans = &lt;enabled|disabled&gt;
 * drools.classLoaderCacheEnabled = &lt;true|false&gt;
 * drools.declarativeAgendaEnabled =  &lt;true|false&gt;
 * drools.permgenThreshold = &lt;1...n&gt;
 * drools.jittingThreshold = &lt;1...n&gt;
 * </pre>
 */
public class RuleBaseConfiguration  extends BaseConfiguration<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption>
    implements
    KieBaseConfiguration,
    Externalizable {

    public static final ConfigurationKey<RuleBaseConfiguration> KEY = new ConfigurationKey<>("Rule");

    private static final long serialVersionUID = 510l;

    public static final boolean DEFAULT_PHREAK = true;
    public static final boolean DEFAULT_SESSION_CACHE = true;

    public static final String DEFAULT_SIGN_ON_SERIALIZATION = "false";

    protected static final transient Logger logger = LoggerFactory.getLogger(RuleBaseConfiguration.class);

    private boolean          sequential;
    private SequentialAgenda sequentialAgenda;

    private boolean         maintainTms;
    private boolean         removeIdentities;
    private boolean         shareAlphaNodes;
    private boolean         shareBetaNodes;
    private int             permGenThreshold;
    private int             jittingThreshold;
    private int             alphaNodeHashingThreshold;
    private int             alphaNodeRangeIndexThreshold;
    private boolean         betaNodeRangeIndexEnabled;
    private int             compositeKeyDepth;
    private boolean         indexLeftBetaMemory;
    private boolean         indexRightBetaMemory;
    private AssertBehaviour assertBehaviour;
    private String          consequenceExceptionHandler;
    private String          ruleBaseUpdateHandler;

    private boolean declarativeAgenda;

    private EventProcessingOption eventProcessingMode;

    private PrototypesOption prototypesOption;

    private IndexPrecedenceOption indexPrecedenceOption;

    // if parallelism is enabled, rulebase builder will try to split
    // the rulebase into multiple partitions that can be evaluated
    // in parallel by using multiple internal threads
    private ParallelExecutionOption parallelExecution;
    private int     maxThreads;

    private ConflictResolver conflictResolver;

    private Map<String, ActivationListenerFactory> activationListeners;

    private int sessionPoolSize;

    /**
     * A constructor that sets the classloader to be used as the parent classloader
     * of this rule base classloaders, and the properties to be used
     * as base configuration options
     *
     */
    public RuleBaseConfiguration(CompositeConfiguration<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption> compConfig) {
        super(compConfig);
        init();
    }
    
    private void init() {

        setRemoveIdentities(Boolean.parseBoolean(getPropertyValue("drools.removeIdentities", "false")));

        setShareAlphaNodes(Boolean.parseBoolean(getPropertyValue(ShareAlphaNodesOption.PROPERTY_NAME, "true")));

        setShareBetaNodes(Boolean.parseBoolean(getPropertyValue(ShareBetaNodesOption.PROPERTY_NAME, "true")));

        setJittingThreshold( Integer.parseInt( getPropertyValue( ConstraintJittingThresholdOption.PROPERTY_NAME, "" + ConstraintJittingThresholdOption.DEFAULT_VALUE)));

        setAlphaNodeHashingThreshold(Integer.parseInt(getPropertyValue(AlphaThresholdOption.PROPERTY_NAME, "3")));

        setAlphaNodeRangeIndexThreshold(Integer.parseInt(getPropertyValue(AlphaRangeIndexThresholdOption.PROPERTY_NAME, "" + AlphaRangeIndexThresholdOption.DEFAULT_VALUE)));

        setBetaNodeRangeIndexEnabled(Boolean.parseBoolean(getPropertyValue(BetaRangeIndexOption.PROPERTY_NAME, "false")));

        setSessionPoolSize(Integer.parseInt(getPropertyValue( SessionsPoolOption.PROPERTY_NAME, "-1")));

        setCompositeKeyDepth(Integer.parseInt(getPropertyValue(CompositeKeyDepthOption.PROPERTY_NAME, "3")));

        setIndexLeftBetaMemory(Boolean.parseBoolean(getPropertyValue(IndexLeftBetaMemoryOption.PROPERTY_NAME, "true")));

        setIndexRightBetaMemory(Boolean.parseBoolean(getPropertyValue(IndexRightBetaMemoryOption.PROPERTY_NAME, "true")));

        setIndexPrecedenceOption(IndexPrecedenceOption.determineIndexPrecedence(getPropertyValue(IndexPrecedenceOption.PROPERTY_NAME, "equality")));

        setAssertBehaviour(AssertBehaviour.determineAssertBehaviour(getPropertyValue(EqualityBehaviorOption.PROPERTY_NAME, "identity")));

        setConsequenceExceptionHandler(getPropertyValue(ConsequenceExceptionHandlerOption.PROPERTY_NAME, "org.drools.core.runtime.rule.impl.DefaultConsequenceExceptionHandler"));

        setRuleBaseUpdateHandler(getPropertyValue("drools.ruleBaseUpdateHandler", ""));

        setSequentialAgenda(SequentialAgenda.determineSequentialAgenda(getPropertyValue(SequentialAgendaOption.PROPERTY_NAME, "sequential")));

        setSequential(Boolean.parseBoolean(getPropertyValue(SequentialOption.PROPERTY_NAME, "false")));

        setParallelExecution(ParallelExecutionOption.determineParallelExecution(getPropertyValue(ParallelExecutionOption.PROPERTY_NAME, "sequential")));

        setMaxThreads( Integer.parseInt( getPropertyValue( MaxThreadsOption.PROPERTY_NAME, "3" ) ) );

        setEventProcessingMode( EventProcessingOption.determineEventProcessingMode( getPropertyValue( EventProcessingOption.PROPERTY_NAME, "cloud" ) ) );

        setPrototypesOption( PrototypesOption.determinePrototypesOption( getPropertyValue( PrototypesOption.PROPERTY_NAME, "disabled" ) ) );

        setDeclarativeAgendaEnabled( Boolean.parseBoolean(getPropertyValue(DeclarativeAgendaOption.PROPERTY_NAME, "false")) );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeBoolean(sequential);
        out.writeObject(sequentialAgenda);
        out.writeBoolean(maintainTms);
        out.writeBoolean(removeIdentities);
        out.writeBoolean(shareAlphaNodes);
        out.writeBoolean(shareBetaNodes);
        out.writeInt(permGenThreshold);
        out.writeInt(jittingThreshold);
        out.writeInt(alphaNodeHashingThreshold);
        out.writeInt(alphaNodeRangeIndexThreshold);
        out.writeBoolean(betaNodeRangeIndexEnabled);
        out.writeInt(compositeKeyDepth);
        out.writeBoolean(indexLeftBetaMemory);
        out.writeBoolean(indexRightBetaMemory);
        out.writeObject(indexPrecedenceOption);
        out.writeObject(assertBehaviour);
        out.writeObject(consequenceExceptionHandler);
        out.writeObject(ruleBaseUpdateHandler);
        out.writeObject(conflictResolver);
        out.writeObject(parallelExecution);
        out.writeInt(maxThreads);
        out.writeObject(eventProcessingMode);
        out.writeBoolean(declarativeAgenda);
        out.writeInt(sessionPoolSize);
    }

    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        super.readExternal(in);
        sequential = in.readBoolean();
        sequentialAgenda = (SequentialAgenda) in.readObject();
        maintainTms = in.readBoolean();
        removeIdentities = in.readBoolean();
        shareAlphaNodes = in.readBoolean();
        shareBetaNodes = in.readBoolean();
        permGenThreshold = in.readInt();
        jittingThreshold = in.readInt();
        alphaNodeHashingThreshold = in.readInt();
        alphaNodeRangeIndexThreshold = in.readInt();
        betaNodeRangeIndexEnabled = in.readBoolean();
        compositeKeyDepth = in.readInt();
        indexLeftBetaMemory = in.readBoolean();
        indexRightBetaMemory = in.readBoolean();
        indexPrecedenceOption = (IndexPrecedenceOption) in.readObject();
        assertBehaviour = (AssertBehaviour) in.readObject();
        consequenceExceptionHandler = (String) in.readObject();
        ruleBaseUpdateHandler = (String) in.readObject();
        conflictResolver = (ConflictResolver) in.readObject();
        parallelExecution = (ParallelExecutionOption) in.readObject();
        maxThreads = in.readInt();
        eventProcessingMode = (EventProcessingOption) in.readObject();
        declarativeAgenda = in.readBoolean();
        sessionPoolSize = in.readInt();
    }

    @SuppressWarnings("unchecked")
    public <T extends SingleValueKieBaseOption> T getOption(OptionKey<T> option) {
        switch(option.name()) {
            case SequentialOption.PROPERTY_NAME: {
                return (T) (this.sequential ? SequentialOption.YES : SequentialOption.NO);
            }
            case RemoveIdentitiesOption.PROPERTY_NAME: {
                return (T) (this.removeIdentities ? RemoveIdentitiesOption.YES : RemoveIdentitiesOption.NO);
            }
            case ShareAlphaNodesOption.PROPERTY_NAME: {
                return (T) (this.shareAlphaNodes ? ShareAlphaNodesOption.YES : ShareAlphaNodesOption.NO);
            }
            case ShareBetaNodesOption.PROPERTY_NAME: {
                return (T) (this.shareBetaNodes ? ShareBetaNodesOption.YES : ShareBetaNodesOption.NO);
            }
            case IndexRightBetaMemoryOption.PROPERTY_NAME: {
                return (T) (this.indexRightBetaMemory ? IndexRightBetaMemoryOption.YES : IndexRightBetaMemoryOption.NO);
            }
            case IndexLeftBetaMemoryOption.PROPERTY_NAME: {
                return (T) (this.indexLeftBetaMemory ? IndexLeftBetaMemoryOption.YES : IndexLeftBetaMemoryOption.NO);
            }
            case IndexPrecedenceOption.PROPERTY_NAME: {
                return (T) getIndexPrecedenceOption();
            }
            case EqualityBehaviorOption.PROPERTY_NAME: {
                return (T) ((this.assertBehaviour == AssertBehaviour.IDENTITY) ? EqualityBehaviorOption.IDENTITY : EqualityBehaviorOption.EQUALITY);
            }
            case SequentialAgendaOption.PROPERTY_NAME: {
                return (T) ((this.sequentialAgenda == SequentialAgenda.SEQUENTIAL) ? SequentialAgendaOption.SEQUENTIAL : SequentialAgendaOption.DYNAMIC);
            }
            case ConstraintJittingThresholdOption.PROPERTY_NAME: {
                return (T) ConstraintJittingThresholdOption.get(jittingThreshold);
            }
            case AlphaThresholdOption.PROPERTY_NAME: {
                return (T) AlphaThresholdOption.get(alphaNodeHashingThreshold);
            }
            case AlphaRangeIndexThresholdOption.PROPERTY_NAME: {
                return (T) AlphaRangeIndexThresholdOption.get(alphaNodeRangeIndexThreshold);
            }
            case BetaRangeIndexOption.PROPERTY_NAME: {
                return (T) (this.betaNodeRangeIndexEnabled ? BetaRangeIndexOption.ENABLED : BetaRangeIndexOption.DISABLED);
            }
            case SessionsPoolOption.PROPERTY_NAME: {
                return (T) SessionsPoolOption.get(sessionPoolSize);
            }
            case CompositeKeyDepthOption.PROPERTY_NAME: {
                return (T) CompositeKeyDepthOption.get(compositeKeyDepth);
            }
            case ConsequenceExceptionHandlerOption.PROPERTY_NAME: {
                Class<? extends ConsequenceExceptionHandler> handler;
                try {
                    handler = (Class<? extends ConsequenceExceptionHandler>) Class.forName(consequenceExceptionHandler);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Unable to resolve ConsequenceExceptionHandler class: " + consequenceExceptionHandler,
                                               e);
                }
                return (T) ConsequenceExceptionHandlerOption.get(handler);
            }
            case EventProcessingOption.PROPERTY_NAME: {
                return (T) getEventProcessingMode();
            }
            case PrototypesOption.PROPERTY_NAME: {
                return (T) getPrototypesOption();
            }
            case MaxThreadsOption.PROPERTY_NAME: {
                return (T) MaxThreadsOption.get(getMaxThreads());
            }
            case ParallelExecutionOption.PROPERTY_NAME: {
                return (T) parallelExecution;
            }
            case DeclarativeAgendaOption.PROPERTY_NAME: {
                return (T) (this.isDeclarativeAgenda() ? DeclarativeAgendaOption.ENABLED : DeclarativeAgendaOption.DISABLED);
            }
            default:
                return compConfig.getOption(option);
        }
    }

    public void setOption(KieBaseOption option) {
        switch (option.propertyName()) {
            case SequentialOption.PROPERTY_NAME : {
                setSequential(((SequentialOption) option).isSequential());
                break;
            }
            case RemoveIdentitiesOption.PROPERTY_NAME: {
                setRemoveIdentities(((RemoveIdentitiesOption) option).isRemoveIdentities());
                break;
            }
            case ShareAlphaNodesOption.PROPERTY_NAME: {
                setShareAlphaNodes(((ShareAlphaNodesOption) option).isShareAlphaNodes());
                break;
            }
            case ShareBetaNodesOption.PROPERTY_NAME: {
                setShareBetaNodes(((ShareBetaNodesOption) option).isShareBetaNodes());
                break;
            }
            case IndexLeftBetaMemoryOption.PROPERTY_NAME: {
                setIndexLeftBetaMemory(((IndexLeftBetaMemoryOption) option).isIndexLeftBetaMemory());
                break;
            }
            case IndexRightBetaMemoryOption.PROPERTY_NAME: {
                setIndexRightBetaMemory(((IndexRightBetaMemoryOption) option).isIndexRightBetaMemory());
                break;
            }
            case IndexPrecedenceOption.PROPERTY_NAME: {
                setIndexPrecedenceOption((IndexPrecedenceOption) option);
                break;
            }
            case EqualityBehaviorOption.PROPERTY_NAME: {
                setAssertBehaviour((option == EqualityBehaviorOption.IDENTITY) ? AssertBehaviour.IDENTITY : AssertBehaviour.EQUALITY);
                break;
            }
            case SequentialAgendaOption.PROPERTY_NAME: {
                setSequentialAgenda((option == SequentialAgendaOption.SEQUENTIAL) ? SequentialAgenda.SEQUENTIAL : SequentialAgenda.DYNAMIC);
                break;
            }
            case ConstraintJittingThresholdOption.PROPERTY_NAME: {
                setJittingThreshold( ( (ConstraintJittingThresholdOption) option ).getThreshold());
                break;
            }
            case AlphaThresholdOption.PROPERTY_NAME: {
                setAlphaNodeHashingThreshold( ( (AlphaThresholdOption) option ).getThreshold());
                break;
            }
            case AlphaRangeIndexThresholdOption.PROPERTY_NAME: {
                setAlphaNodeRangeIndexThreshold( ( (AlphaRangeIndexThresholdOption) option ).getThreshold());
                break;
            }
            case BetaRangeIndexOption.PROPERTY_NAME: {
                setBetaNodeRangeIndexEnabled( ( (BetaRangeIndexOption) option ).isBetaRangeIndexEnabled());
                break;
            }
            case SessionsPoolOption.PROPERTY_NAME: {
                setSessionPoolSize( ( ( SessionsPoolOption ) option ).getSize());
                break;
            }
            case CompositeKeyDepthOption.PROPERTY_NAME: {
                setCompositeKeyDepth( ( (CompositeKeyDepthOption) option ).getDepth());
                break;
            }
            case ConsequenceExceptionHandlerOption.PROPERTY_NAME: {
                setConsequenceExceptionHandler( ( (ConsequenceExceptionHandlerOption) option ).getHandler().getName());
                break;
            }
            case EventProcessingOption.PROPERTY_NAME: {
                setEventProcessingMode( (EventProcessingOption) option);
                break;
            }
            case PrototypesOption.PROPERTY_NAME: {
                setPrototypesOption( (PrototypesOption) option);
                break;
            }
            case MaxThreadsOption.PROPERTY_NAME: {
                setMaxThreads( ( (MaxThreadsOption) option ).getMaxThreads());
                break;
            }
            case ParallelExecutionOption.PROPERTY_NAME: {
                setParallelExecution( (ParallelExecutionOption) option );
                break;
            }
            case DeclarativeAgendaOption.PROPERTY_NAME: {
                setDeclarativeAgendaEnabled(((DeclarativeAgendaOption) option).isDeclarativeAgendaEnabled());
                break;
            }
            default:
                compConfig.setOption(option);
        }
    }

    public boolean setInternalProperty(String name, String value) {
        switch(name) {
            case SequentialAgendaOption.PROPERTY_NAME: {
                setSequentialAgenda(SequentialAgenda.determineSequentialAgenda(StringUtils.isEmpty(value) ? "sequential" : value));
                break;
            }
            case SequentialOption.PROPERTY_NAME: {
                setSequential(StringUtils.isEmpty(value) ? false : Boolean.valueOf(value));
                break;
            }
            case RemoveIdentitiesOption.PROPERTY_NAME: {
                setRemoveIdentities(StringUtils.isEmpty(value) ? false : Boolean.valueOf(value));
                break;
            }
            case ShareAlphaNodesOption.PROPERTY_NAME: {
                setShareAlphaNodes(StringUtils.isEmpty(value) ? false : Boolean.valueOf(value));
                break;
            }
            case ShareBetaNodesOption.PROPERTY_NAME: {
                setShareBetaNodes(StringUtils.isEmpty(value) ? false : Boolean.valueOf(value));
                break;
            }
            case ConstraintJittingThresholdOption.PROPERTY_NAME: {
                setJittingThreshold(StringUtils.isEmpty(value) ? ConstraintJittingThresholdOption.DEFAULT_VALUE : Integer.parseInt(value));
                break;
            }
            case AlphaThresholdOption.PROPERTY_NAME: {
                setAlphaNodeHashingThreshold(StringUtils.isEmpty(value) ? 3 : Integer.parseInt(value));
                break;
            }
            case AlphaRangeIndexThresholdOption.PROPERTY_NAME: {
                setAlphaNodeRangeIndexThreshold(StringUtils.isEmpty(value) ? AlphaRangeIndexThresholdOption.DEFAULT_VALUE : Integer.parseInt(value));
                break;
            }
            case BetaRangeIndexOption.PROPERTY_NAME: {
                setBetaNodeRangeIndexEnabled(StringUtils.isEmpty(value) ? false : Boolean.valueOf(value));
                break;
            }
            case SessionsPoolOption.PROPERTY_NAME: {
                setSessionPoolSize(StringUtils.isEmpty(value) ? -1 : Integer.parseInt(value));
                break;
            }
            case CompositeKeyDepthOption.PROPERTY_NAME: {
                setCompositeKeyDepth(StringUtils.isEmpty(value) ? 3 : Integer.parseInt(value));
                break;
            }
            case IndexLeftBetaMemoryOption.PROPERTY_NAME: {
                setIndexLeftBetaMemory(StringUtils.isEmpty(value) ? true : Boolean.valueOf(value));
                break;
            }
            case IndexRightBetaMemoryOption.PROPERTY_NAME: {
                setIndexRightBetaMemory(StringUtils.isEmpty(value) ? true : Boolean.valueOf(value));
                break;
            }
            case IndexPrecedenceOption.PROPERTY_NAME: {
                setIndexPrecedenceOption(StringUtils.isEmpty(value) ? IndexPrecedenceOption.EQUALITY_PRIORITY : IndexPrecedenceOption.determineIndexPrecedence(value));
                break;
            }
            case EqualityBehaviorOption.PROPERTY_NAME: {
                setAssertBehaviour(AssertBehaviour.determineAssertBehaviour(StringUtils.isEmpty(value) ? "identity" : value));
                break;
            }
            case ConsequenceExceptionHandlerOption.PROPERTY_NAME: {
                setConsequenceExceptionHandler(StringUtils.isEmpty(value) ? DefaultConsequenceExceptionHandler.class.getName() : value);
                break;
            }
            case "drools.ruleBaseUpdateHandler": {
                setRuleBaseUpdateHandler(StringUtils.isEmpty(value) ? "" : value);
                break;
            }
            case ParallelExecutionOption.PROPERTY_NAME: {
                setParallelExecution(ParallelExecutionOption.determineParallelExecution(StringUtils.isEmpty(value) ? "sequential" : value));
                break;
            }
            case MaxThreadsOption.PROPERTY_NAME: {
                setMaxThreads(StringUtils.isEmpty(value) ? 3 : Integer.parseInt(value));
                break;
            }
            case EventProcessingOption.PROPERTY_NAME: {
                setEventProcessingMode(EventProcessingOption.determineEventProcessingMode(StringUtils.isEmpty(value) ? "cloud" : value));
                break;
            }
            case PrototypesOption.PROPERTY_NAME: {
                setPrototypesOption(PrototypesOption.determinePrototypesOption(StringUtils.isEmpty(value) ? "disabled" : value));
                break;
            }
            default : {
                return false;
            }
        }

        return true;
    }

    public String getInternalProperty(String name) {
        switch (name) {
            case SequentialAgendaOption.PROPERTY_NAME: {
                return getSequentialAgenda().toExternalForm();
            }
            case SequentialOption.PROPERTY_NAME: {
                return Boolean.toString(isSequential());
            }
            case RemoveIdentitiesOption.PROPERTY_NAME: {
                return Boolean.toString(isRemoveIdentities());
            }
            case ShareAlphaNodesOption.PROPERTY_NAME: {
                return Boolean.toString(isShareAlphaNodes());
            }
            case ShareBetaNodesOption.PROPERTY_NAME: {
                return Boolean.toString(isShareBetaNodes());
            }
            case ConstraintJittingThresholdOption.PROPERTY_NAME: {
                return Integer.toString(getJittingThreshold());
            }
            case AlphaThresholdOption.PROPERTY_NAME: {
                return Integer.toString(getAlphaNodeHashingThreshold());
            }
            case AlphaRangeIndexThresholdOption.PROPERTY_NAME: {
                return Integer.toString(getAlphaNodeRangeIndexThreshold());
            }
            case BetaRangeIndexOption.PROPERTY_NAME: {
                return Boolean.toString(isBetaNodeRangeIndexEnabled());
            }
            case SessionsPoolOption.PROPERTY_NAME: {
                return Integer.toString(getSessionPoolSize());
            }
            case CompositeKeyDepthOption.PROPERTY_NAME: {
                return Integer.toString(getCompositeKeyDepth());
            }
            case IndexLeftBetaMemoryOption.PROPERTY_NAME: {
                return Boolean.toString(isIndexLeftBetaMemory());
            }
            case IndexRightBetaMemoryOption.PROPERTY_NAME: {
                return Boolean.toString(isIndexRightBetaMemory());
            }
            case IndexPrecedenceOption.PROPERTY_NAME: {
                return getIndexPrecedenceOption().getValue();
            }
            case EqualityBehaviorOption.PROPERTY_NAME: {
                return getAssertBehaviour().toExternalForm();
            }
            case ConsequenceExceptionHandlerOption.PROPERTY_NAME: {
                return getConsequenceExceptionHandler();
            }
            case "drools.ruleBaseUpdateHandler": {
                return getRuleBaseUpdateHandler();
            }
            case ParallelExecutionOption.PROPERTY_NAME: {
                return parallelExecution.toExternalForm();
            }
            case MaxThreadsOption.PROPERTY_NAME: {
                return Integer.toString(getMaxThreads());
            }
            case EventProcessingOption.PROPERTY_NAME: {
                return getEventProcessingMode().getMode();
            }
            case PrototypesOption.PROPERTY_NAME: {
                return getPrototypesOption().toString().toLowerCase();
            }
        }

        return null;
    }

    public void setSequential(boolean sequential) {
        this.sequential = sequential;
    }

    public boolean isSequential() {
        return this.sequential;
    }

    public boolean isMaintainTms() {
        return this.maintainTms;
    }

    public void setMaintainTms(final boolean maintainTms) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.maintainTms = maintainTms;
    }

    public boolean isRemoveIdentities() {
        return this.removeIdentities;
    }

    public void setRemoveIdentities(final boolean removeIdentities) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.removeIdentities = removeIdentities;
    }

    public boolean isShareAlphaNodes() {
        return this.shareAlphaNodes;
    }

    public void setShareAlphaNodes(final boolean shareAlphaNodes) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.shareAlphaNodes = shareAlphaNodes;
    }

    public boolean isShareBetaNodes() {
        return this.shareBetaNodes;
    }

    public void setShareBetaNodes(final boolean shareBetaNodes) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.shareBetaNodes = shareBetaNodes;
    }

    public int getJittingThreshold() {
        return jittingThreshold;
    }

    public void setJittingThreshold( int jittingThreshold ) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.jittingThreshold = jittingThreshold;
    }

    public int getAlphaNodeHashingThreshold() {
        return this.alphaNodeHashingThreshold;
    }

    public void setAlphaNodeHashingThreshold(final int alphaNodeHashingThreshold) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.alphaNodeHashingThreshold = alphaNodeHashingThreshold;
    }

    public int getAlphaNodeRangeIndexThreshold() {
        return this.alphaNodeRangeIndexThreshold;
    }

    public void setAlphaNodeRangeIndexThreshold(final int alphaNodeRangeIndexThreshold) {
        checkCanChange();
        this.alphaNodeRangeIndexThreshold = alphaNodeRangeIndexThreshold;
    }

    public boolean isBetaNodeRangeIndexEnabled() {
        return this.betaNodeRangeIndexEnabled;
    }

    public void setBetaNodeRangeIndexEnabled(final boolean betaNodeRangeIndexEnabled) {
        checkCanChange();
        this.betaNodeRangeIndexEnabled = betaNodeRangeIndexEnabled;
    }

    public int getSessionPoolSize() {
        return this.sessionPoolSize;
    }

    public void setSessionPoolSize(final int sessionPoolSize) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.sessionPoolSize = sessionPoolSize;
    }

    public AssertBehaviour getAssertBehaviour() {
        return this.assertBehaviour;
    }

    public void setAssertBehaviour(final AssertBehaviour assertBehaviour) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.assertBehaviour = assertBehaviour;
    }

    public EventProcessingOption getEventProcessingMode() {
        return this.eventProcessingMode;
    }

    public void setEventProcessingMode(final EventProcessingOption mode) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.eventProcessingMode = mode;
    }

    public PrototypesOption getPrototypesOption() {
        return this.prototypesOption;
    }

    public void setPrototypesOption(final PrototypesOption prototypesOption) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.prototypesOption = prototypesOption;
    }

    public int getCompositeKeyDepth() {
        return this.compositeKeyDepth;
    }

    public void setCompositeKeyDepth(final int compositeKeyDepth) {
        checkCanChange();
        if ( compositeKeyDepth > 3 ) {
            throw new UnsupportedOperationException( "compositeKeyDepth cannot be greater than 3" );
        }
        this.compositeKeyDepth = compositeKeyDepth;
    }

    public boolean isIndexLeftBetaMemory() {
        return this.indexLeftBetaMemory;
    }

    public void setIndexLeftBetaMemory(final boolean indexLeftBetaMemory) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.indexLeftBetaMemory = indexLeftBetaMemory;
    }

    public boolean isIndexRightBetaMemory() {
        return this.indexRightBetaMemory;
    }

    public void setIndexRightBetaMemory(final boolean indexRightBetaMemory) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.indexRightBetaMemory = indexRightBetaMemory;
    }

    public IndexPrecedenceOption getIndexPrecedenceOption() {
        return this.indexPrecedenceOption;
    }

    public void setIndexPrecedenceOption(final IndexPrecedenceOption precedence) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.indexPrecedenceOption = precedence;
    }

    public String getConsequenceExceptionHandler() {
        return consequenceExceptionHandler;
    }

    public void setConsequenceExceptionHandler(String consequenceExceptionHandler) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.consequenceExceptionHandler = consequenceExceptionHandler;
    }

    public String getRuleBaseUpdateHandler() {
        return ruleBaseUpdateHandler;
    }

    public void setRuleBaseUpdateHandler(String ruleBaseUpdateHandler) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.ruleBaseUpdateHandler = ruleBaseUpdateHandler;
    }

    public SequentialAgenda getSequentialAgenda() {
        return this.sequentialAgenda;
    }

    public void setSequentialAgenda(final SequentialAgenda sequentialAgenda) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.sequentialAgenda = sequentialAgenda;
    }

    public void setParallelExecution(ParallelExecutionOption parallelExecutionOption) {
        checkCanChange();
        this.parallelExecution = parallelExecutionOption;
    }

    public void enforceSingleThreadEvaluation() {
        this.parallelExecution = ParallelExecutionOption.SEQUENTIAL;
    }

    /**
     * Returns true if the partitioning of the rulebase is enabled
     * and false otherwise. Default is false.
     * 
     * @return
     */
    public boolean isParallelEvaluation() {
        return this.parallelExecution.isParallel();
    }

    public boolean isParallelExecution() {
        return this.parallelExecution == ParallelExecutionOption.FULLY_PARALLEL;
    }

    /**
     * If multi-thread evaluation is enabled, this parameter configures the 
     * maximum number of threads each session can use for concurrent Rete
     * propagation. 
     * 
     * @param maxThreads the maximum number of threads to use. If 0 or a 
     *                   negative number is set, the engine will use number
     *                   of threads equal to the number of partitions in the
     *                   rule base. Default number of threads is 0. 
     */
    public void setMaxThreads(final int maxThreads) {
        this.maxThreads = maxThreads;
    }

    /**
     * Returns the configured number of maximum threads to use for concurrent
     * propagation when multi-thread evaluation is enabled. Default is zero.
     * 
     * @return
     */
    public int getMaxThreads() {
        return this.maxThreads;
    }

    public boolean isDeclarativeAgenda() {
        return this.declarativeAgenda;
    }
    
    /**
     * Enable declarative agenda
     * @param enabled
     */
    public void setDeclarativeAgendaEnabled(boolean enabled) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.declarativeAgenda = enabled;
    }
    
    public void addActivationListener(String name, ActivationListenerFactory factory) {
        if ( this.activationListeners == null ) {
            this.activationListeners = new HashMap<>();
        }
        this.activationListeners.put( name, factory );
    }
    
    public ActivationListenerFactory getActivationListenerFactory(String name) {
        ActivationListenerFactory factory = null;
        if ( this.activationListeners != null ) {
            factory = this.activationListeners.get( name );
        }
        
        if ( factory != null ) {
            return factory;
        } else {
            if ( "query".equals( name )) {
                return QueryActivationListenerFactory.INSTANCE;
            } else  if ( "agenda".equals( name ) || "direct".equals( name ) ) {
                return RuleActivationListenerFactory.INSTANCE;
            } 
        } 
        
        throw new IllegalArgumentException( "ActivationListenerFactory not found for '" + name + "'" );
    }

    public static class AssertBehaviour
            implements
            Externalizable {
        private static final long serialVersionUID = 510l;

        public static final AssertBehaviour IDENTITY = new AssertBehaviour(0);
        public static final AssertBehaviour EQUALITY = new AssertBehaviour(1);

        private int value;

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            value = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(value);
        }

        public AssertBehaviour() {

        }

        private AssertBehaviour(final int value) {
            this.value = value;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            else if (obj instanceof AssertBehaviour) {
                AssertBehaviour that = (AssertBehaviour) obj;

                return value == that.value;
            }
            return false;
        }

        public static AssertBehaviour determineAssertBehaviour(final String value) {
            if ("IDENTITY".equalsIgnoreCase(value)) {
                return IDENTITY;
            } else if ("EQUALITY".equalsIgnoreCase(value)) {
                return EQUALITY;
            } else {
                throw new IllegalArgumentException("Illegal enum value '" + value + "' for AssertBehaviour");
            }
        }

        private Object readResolve() throws java.io.ObjectStreamException {
            switch (this.value) {
                case 0:
                    return IDENTITY;
                case 1:
                    return EQUALITY;
                default:
                    throw new IllegalArgumentException("Illegal enum value '" + this.value + "' for AssertBehaviour");
            }
        }

        public String toExternalForm() {
            return (this.value == 0) ? "identity" : "equality";
        }

        public String toString() {
            return "AssertBehaviour : " + ((this.value == 0) ? "identity" : "equality");
        }
    }

    public static class SequentialAgenda
            implements
            Externalizable {
        private static final long serialVersionUID = 510l;

        public static final SequentialAgenda SEQUENTIAL = new SequentialAgenda(0);
        public static final SequentialAgenda DYNAMIC    = new SequentialAgenda(1);

        private int value;

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            value = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(value);
        }

        public SequentialAgenda() {

        }

        private SequentialAgenda(final int value) {
            this.value = value;
        }

        public static SequentialAgenda determineSequentialAgenda(final String value) {
            if ("sequential".equalsIgnoreCase(value)) {
                return SEQUENTIAL;
            } else if ("dynamic".equalsIgnoreCase(value)) {
                return DYNAMIC;
            } else {
                throw new IllegalArgumentException("Illegal enum value '" + value + "' for SequentialAgenda");
            }
        }

        private Object readResolve() throws java.io.ObjectStreamException {
            switch (this.value) {
                case 0:
                    return SEQUENTIAL;
                case 1:
                    return DYNAMIC;
                default:
                    throw new IllegalArgumentException("Illegal enum value '" + this.value + "' for SequentialAgenda");
            }
        }

        public String toExternalForm() {
            return (this.value == 0) ? "sequential" : "dynamic";
        }

        public String toString() {
            return "SequentialAgenda : " + ((this.value == 0) ? "sequential" : "dynamic");
        }
    }
}
