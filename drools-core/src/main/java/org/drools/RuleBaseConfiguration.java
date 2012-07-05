/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools;

import org.drools.builder.conf.ClassLoaderCacheOption;
import org.drools.builder.conf.DeclarativeAgendaOption;
import org.drools.builder.conf.LRUnlinkingOption;
import org.drools.common.AgendaGroupFactory;
import org.drools.common.ArrayAgendaGroupFactory;
import org.drools.common.PriorityQueueAgendaGroupFactory;
import org.drools.concurrent.DefaultExecutorService;
import org.drools.conf.AlphaThresholdOption;
import org.drools.conf.AssertBehaviorOption;
import org.drools.conf.CompositeKeyDepthOption;
import org.drools.conf.ConsequenceExceptionHandlerOption;
import org.drools.conf.EventProcessingOption;
import org.drools.conf.IndexLeftBetaMemoryOption;
import org.drools.conf.IndexRightBetaMemoryOption;
import org.drools.conf.KnowledgeBaseOption;
import org.drools.conf.LogicalOverrideOption;
import org.drools.conf.MBeansOption;
import org.drools.conf.MaintainTMSOption;
import org.drools.conf.MaxThreadsOption;
import org.drools.conf.MultiValueKnowledgeBaseOption;
import org.drools.conf.MultithreadEvaluationOption;
import org.drools.conf.PermGenThresholdOption;
import org.drools.conf.RemoveIdentitiesOption;
import org.drools.conf.SequentialAgendaOption;
import org.drools.conf.SequentialOption;
import org.drools.conf.ShareAlphaNodesOption;
import org.drools.conf.ShareBetaNodesOption;
import org.drools.conf.SingleValueKnowledgeBaseOption;
import org.drools.conflict.DepthConflictResolver;
import org.drools.core.util.ConfFileUtils;
import org.drools.core.util.StringUtils;
import org.drools.runtime.rule.ConsequenceExceptionHandler;
import org.drools.runtime.rule.impl.DefaultConsequenceExceptionHandler;
import org.drools.spi.ConflictResolver;
import org.drools.util.ChainedProperties;
import org.drools.util.ClassLoaderUtil;
import org.drools.util.CompositeClassLoader;
import org.mvel2.MVEL;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
 * drools.compositeKeyDepth  =&lt;1..3&gt;
 * drools.indexLeftBetaMemory = &lt;true/false&gt;
 * drools.indexRightBetaMemory = &lt;true/false&gt;
 * drools.assertBehaviour = &lt;identity|equality&gt;
 * drools.logicalOverride = &lt;discard|preserve&gt;
 * drools.executorService = &lt;qualified class name&gt;
 * drools.conflictResolver = &lt;qualified class name&gt;
 * drools.consequenceExceptionHandler = &lt;qualified class name&gt;
 * drools.ruleBaseUpdateHandler = &lt;qualified class name&gt;
 * drools.sessionClock = &lt;qualified class name&gt;
 * drools.mbeans = &lt;enabled|disabled&gt;
 * drools.classLoaderCacheEnabled = &lt;true|false&gt;
 * drools.lrUnlinkingEnabled = &lt;true|false&gt; 
 * drools.declarativeAgendaEnabled =  &lt;true|false&gt; 
 * </pre>
 */
public class RuleBaseConfiguration
    implements
    KnowledgeBaseConfiguration,
    Externalizable {
    private static final long              serialVersionUID = 510l;
    
    public static final String          DEFAULT_SIGN_ON_SERIALIZATION = "false";

    private ChainedProperties              chainedProperties;

    private boolean                        immutable;

    private boolean                        sequential;
    private SequentialAgenda               sequentialAgenda;

    private boolean                        maintainTms;
    private boolean                        removeIdentities;
    private boolean                        shareAlphaNodes;
    private boolean                        shareBetaNodes;
    private int                            permGenThreshold;
    private int                            alphaNodeHashingThreshold;
    private int                            compositeKeyDepth;
    private boolean                        indexLeftBetaMemory;
    private boolean                        indexRightBetaMemory;
    private AssertBehaviour                assertBehaviour;
    private LogicalOverride                logicalOverride;
    private String                         executorService;
    private String                         consequenceExceptionHandler;
    private String                         ruleBaseUpdateHandler;
    private boolean                        classLoaderCacheEnabled;
    private boolean                        lrUnlinkingEnabled;

    private boolean                        declarativeAgenda;
    
    private EventProcessingOption          eventProcessingMode;

    // if "true", rulebase builder will try to split 
    // the rulebase into multiple partitions that can be evaluated
    // in parallel by using multiple internal threads
    private boolean                        multithread;
    private int                            maxThreads;

    // this property activates MBean monitoring and management
    private boolean                        mbeansEnabled;

    private ConflictResolver               conflictResolver;
    
    private Map<String, ActivationListenerFactory> activationListeners;

    private List<Map<String, Object>>      workDefinitions;
    private boolean                        advancedProcessRuleIntegration;

    private transient CompositeClassLoader classLoader;
    
    private static final RuleBaseConfiguration defaultConf = new RuleBaseConfiguration();
    
    public static RuleBaseConfiguration getDefaultInstance() {
        return defaultConf;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( chainedProperties );
        out.writeBoolean( immutable );
        out.writeBoolean( sequential );
        out.writeObject( sequentialAgenda );
        out.writeBoolean( maintainTms );
        out.writeBoolean( removeIdentities );
        out.writeBoolean( shareAlphaNodes );
        out.writeBoolean( shareBetaNodes );
        out.writeInt( permGenThreshold );
        out.writeInt( alphaNodeHashingThreshold );
        out.writeInt( compositeKeyDepth );
        out.writeBoolean( indexLeftBetaMemory );
        out.writeBoolean( indexRightBetaMemory );
        out.writeObject( assertBehaviour );
        out.writeObject( logicalOverride );
        out.writeObject( executorService );
        out.writeObject( consequenceExceptionHandler );
        out.writeObject( ruleBaseUpdateHandler );
        out.writeObject( conflictResolver );
        out.writeBoolean( advancedProcessRuleIntegration );
        out.writeBoolean( multithread );
        out.writeInt( maxThreads );
        out.writeObject( eventProcessingMode );
        out.writeBoolean( classLoaderCacheEnabled );
        out.writeBoolean( lrUnlinkingEnabled );
        out.writeBoolean(  declarativeAgenda );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        chainedProperties = (ChainedProperties) in.readObject();
        immutable = in.readBoolean();
        sequential = in.readBoolean();
        sequentialAgenda = (SequentialAgenda) in.readObject();
        maintainTms = in.readBoolean();
        removeIdentities = in.readBoolean();
        shareAlphaNodes = in.readBoolean();
        shareBetaNodes = in.readBoolean();
        permGenThreshold = in.readInt();
        alphaNodeHashingThreshold = in.readInt();
        compositeKeyDepth = in.readInt();
        indexLeftBetaMemory = in.readBoolean();
        indexRightBetaMemory = in.readBoolean();
        assertBehaviour = (AssertBehaviour) in.readObject();
        logicalOverride = (LogicalOverride) in.readObject();
        executorService = (String) in.readObject();
        consequenceExceptionHandler = (String) in.readObject();
        ruleBaseUpdateHandler = (String) in.readObject();
        conflictResolver = (ConflictResolver) in.readObject();
        advancedProcessRuleIntegration = in.readBoolean();
        multithread = in.readBoolean();
        maxThreads = in.readInt();
        eventProcessingMode = (EventProcessingOption) in.readObject();
        classLoaderCacheEnabled = in.readBoolean();
        lrUnlinkingEnabled = in.readBoolean();
        declarativeAgenda = in.readBoolean();
    }

    /**
     * Creates a new rulebase configuration using the provided properties
     * as configuration options. Also, if a Thread.currentThread().getContextClassLoader()
     * returns a non-null class loader, it will be used as the parent classloader
     * for this rulebase class loaders, otherwise, the RuleBaseConfiguration.class.getClassLoader()
     * class loader will be used.
     *
     * @param properties
     */
    public RuleBaseConfiguration(Properties properties) {
        init( properties,
              null );
    }

    /**
     * Creates a new rulebase with a default parent class loader set according
     * to the following algorithm:
     *
     * If a Thread.currentThread().getContextClassLoader() returns a non-null class loader,
     * it will be used as the parent class loader for this rulebase class loaders, otherwise,
     * the RuleBaseConfiguration.class.getClassLoader() class loader will be used.
     */
    public RuleBaseConfiguration() {
        init( null,
              null );
    }

    /**
     * A constructor that sets the parent classloader to be used
     * while dealing with this rule base
     *
     * @param classLoader
     */
    public RuleBaseConfiguration(ClassLoader... classLoaders) {
        init( null,
              classLoaders );
    }

    public void setProperty(String name,
                            String value) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return;
        }

        if ( name.equals( SequentialAgendaOption.PROPERTY_NAME ) ) {
            setSequentialAgenda( SequentialAgenda.determineSequentialAgenda( StringUtils.isEmpty( value ) ? "sequential" : value ) );
        } else if ( name.equals( SequentialOption.PROPERTY_NAME ) ) {
            setSequential( StringUtils.isEmpty( value ) ? false : Boolean.valueOf( value ) );
        } else if ( name.equals( MaintainTMSOption.PROPERTY_NAME ) ) {
            setMaintainTms( StringUtils.isEmpty( value ) ? false : Boolean.valueOf( value ) );
        } else if ( name.equals( RemoveIdentitiesOption.PROPERTY_NAME ) ) {
            setRemoveIdentities( StringUtils.isEmpty( value ) ? false : Boolean.valueOf( value ) );
        } else if ( name.equals( ShareAlphaNodesOption.PROPERTY_NAME ) ) {
            setShareAlphaNodes( StringUtils.isEmpty( value ) ? false : Boolean.valueOf( value ) );
        } else if ( name.equals( ShareBetaNodesOption.PROPERTY_NAME ) ) {
            setShareBetaNodes( StringUtils.isEmpty( value ) ? false : Boolean.valueOf( value ) );
        } else if ( name.equals( PermGenThresholdOption.PROPERTY_NAME ) ) {
            setPermGenThreshold(StringUtils.isEmpty(value) ? PermGenThresholdOption.DEFAULT_VALUE : Integer.parseInt(value));
        } else if ( name.equals( AlphaThresholdOption.PROPERTY_NAME ) ) {
            setAlphaNodeHashingThreshold(StringUtils.isEmpty(value) ? 3 : Integer.parseInt(value));
        } else if ( name.equals( CompositeKeyDepthOption.PROPERTY_NAME ) ) {
            setCompositeKeyDepth(StringUtils.isEmpty(value) ? 3 : Integer.parseInt(value));
        } else if ( name.equals( IndexLeftBetaMemoryOption.PROPERTY_NAME ) ) {
            setIndexLeftBetaMemory(StringUtils.isEmpty(value) ? true : Boolean.valueOf(value));
        } else if ( name.equals( IndexRightBetaMemoryOption.PROPERTY_NAME ) ) {
            setIndexRightBetaMemory(StringUtils.isEmpty(value) ? true : Boolean.valueOf(value));
        } else if ( name.equals( AssertBehaviorOption.PROPERTY_NAME ) ) {
            setAssertBehaviour(AssertBehaviour.determineAssertBehaviour(StringUtils.isEmpty(value) ? "identity" : value));
        } else if ( name.equals( LogicalOverrideOption.PROPERTY_NAME ) ) {
            setLogicalOverride(LogicalOverride.determineLogicalOverride(StringUtils.isEmpty(value) ? "discard" : value));
        } else if ( name.equals( "drools.executorService" ) ) {
            setExecutorService(StringUtils.isEmpty(value) ? DefaultExecutorService.class.getName() : value);
        } else if ( name.equals( ConsequenceExceptionHandlerOption.PROPERTY_NAME ) ) {
            setConsequenceExceptionHandler(StringUtils.isEmpty(value) ? DefaultConsequenceExceptionHandler.class.getName() : value);
        } else if ( name.equals( "drools.ruleBaseUpdateHandler" ) ) {
            setRuleBaseUpdateHandler(StringUtils.isEmpty(value) ? "" : value);
        } else if ( name.equals( "drools.conflictResolver" ) ) {
            setConflictResolver(determineConflictResolver(StringUtils.isEmpty(value) ? DepthConflictResolver.class.getName() : value));
        } else if ( name.equals( "drools.advancedProcessRuleIntegration" ) ) {
            setAdvancedProcessRuleIntegration(StringUtils.isEmpty(value) ? false : Boolean.valueOf(value));
        } else if ( name.equals( MultithreadEvaluationOption.PROPERTY_NAME ) ) {
            setMultithreadEvaluation(StringUtils.isEmpty(value) ? false : Boolean.valueOf(value));
        } else if ( name.equals( MaxThreadsOption.PROPERTY_NAME ) ) {
            setMaxThreads(StringUtils.isEmpty(value) ? 3 : Integer.parseInt(value));
        } else if ( name.equals( EventProcessingOption.PROPERTY_NAME ) ) {
            setEventProcessingMode(EventProcessingOption.determineEventProcessingMode(StringUtils.isEmpty(value) ? "cloud" : value));
        } else if ( name.equals( MBeansOption.PROPERTY_NAME ) ) {
            setMBeansEnabled(MBeansOption.isEnabled(value));
        } else if ( name.equals( ClassLoaderCacheOption.PROPERTY_NAME ) ) {
            setClassLoaderCacheEnabled(StringUtils.isEmpty(value) ? true : Boolean.valueOf(value));
        } else if ( name.equals( LRUnlinkingOption.PROPERTY_NAME ) ) {
            setLRUnlinkingEnabled( StringUtils.isEmpty( value ) ? false : Boolean.valueOf( value ) );
        }
    }

    public String getProperty(String name) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return null;
        }

        if ( name.equals( SequentialAgendaOption.PROPERTY_NAME ) ) {
            return getSequentialAgenda().toExternalForm();
        } else if ( name.equals( SequentialOption.PROPERTY_NAME ) ) {
            return Boolean.toString( isSequential() );
        } else if ( name.equals( MaintainTMSOption.PROPERTY_NAME ) ) {
            return Boolean.toString( isMaintainTms() );
        } else if ( name.equals( RemoveIdentitiesOption.PROPERTY_NAME ) ) {
            return Boolean.toString( isRemoveIdentities() );
        } else if ( name.equals( ShareAlphaNodesOption.PROPERTY_NAME ) ) {
            return Boolean.toString( isShareAlphaNodes() );
        } else if ( name.equals( ShareBetaNodesOption.PROPERTY_NAME ) ) {
            return Boolean.toString( isShareBetaNodes() );
        } else if ( name.equals( PermGenThresholdOption.PROPERTY_NAME ) ) {
            return Integer.toString( getPermGenThreshold() );
        } else if ( name.equals( AlphaThresholdOption.PROPERTY_NAME ) ) {
            return Integer.toString( getAlphaNodeHashingThreshold() );
        } else if ( name.equals( CompositeKeyDepthOption.PROPERTY_NAME ) ) {
            return Integer.toString( getCompositeKeyDepth() );
        } else if ( name.equals( IndexLeftBetaMemoryOption.PROPERTY_NAME ) ) {
            return Boolean.toString( isIndexLeftBetaMemory() );
        } else if ( name.equals( IndexRightBetaMemoryOption.PROPERTY_NAME ) ) {
            return Boolean.toString(isIndexRightBetaMemory());
        } else if ( name.equals( AssertBehaviorOption.PROPERTY_NAME ) ) {
            return getAssertBehaviour().toExternalForm();
        } else if ( name.equals( "drools.logicalOverride" ) ) {
            return getLogicalOverride().toExternalForm();
        } else if ( name.equals( "drools.executorService" ) ) {
            return getExecutorService();
        } else if ( name.equals( ConsequenceExceptionHandlerOption.PROPERTY_NAME ) ) {
            return getConsequenceExceptionHandler();
        } else if ( name.equals( "drools.ruleBaseUpdateHandler" ) ) {
            return getRuleBaseUpdateHandler();
        } else if ( name.equals( "drools.conflictResolver" ) ) {
            return getConflictResolver().getClass().getName();
        } else if ( name.equals( "drools.advancedProcessRuleIntegration" ) ) {
            return Boolean.toString( isAdvancedProcessRuleIntegration() );
        } else if ( name.equals( MultithreadEvaluationOption.PROPERTY_NAME ) ) {
            return Boolean.toString( isMultithreadEvaluation() );
        } else if ( name.equals( MaxThreadsOption.PROPERTY_NAME ) ) {
            return Integer.toString(getMaxThreads());
        } else if ( name.equals( EventProcessingOption.PROPERTY_NAME ) ) {
            return getEventProcessingMode().getMode();
        } else if ( name.equals( MBeansOption.PROPERTY_NAME ) ) {
            return isMBeansEnabled() ? "enabled" : "disabled";
        } else if ( name.equals( ClassLoaderCacheOption.PROPERTY_NAME ) ) {
            return Boolean.toString( isClassLoaderCacheEnabled() );
        } else if ( name.equals( LRUnlinkingOption.PROPERTY_NAME ) ) {
            return Boolean.toString( isLRUnlinkingEnabled() );
        }

        return null;
    }

    /**
     * A constructor that sets the classloader to be used as the parent classloader
     * of this rule base classloaders, and the properties to be used
     * as base configuration options
     *
     * @param classLoder
     * @param properties
     */
    public RuleBaseConfiguration(Properties properties,
                                 ClassLoader... classLoaders) {
        init( properties,
              classLoaders );
    }

    private void init(Properties properties,
                      ClassLoader... classLoaders) {
        this.immutable = false;

        setClassLoader( classLoaders );

        this.chainedProperties = new ChainedProperties( "rulebase.conf",
                                                        this.classLoader,
                                                        true );

        if ( properties != null ) {
            this.chainedProperties.addProperties( properties );
        }

        setSequentialAgenda( SequentialAgenda.determineSequentialAgenda( this.chainedProperties.getProperty( SequentialAgendaOption.PROPERTY_NAME,
                                                                                                             "sequential" ) ) );

        setSequential(Boolean.valueOf(this.chainedProperties.getProperty(SequentialOption.PROPERTY_NAME,
                                                                         "false")).booleanValue());

        setMaintainTms(Boolean.valueOf(this.chainedProperties.getProperty(MaintainTMSOption.PROPERTY_NAME,
                                                                          "true")).booleanValue());

        setRemoveIdentities(Boolean.valueOf(this.chainedProperties.getProperty("drools.removeIdentities",
                                                                               "false")).booleanValue());

        setShareAlphaNodes(Boolean.valueOf(this.chainedProperties.getProperty(ShareAlphaNodesOption.PROPERTY_NAME,
                                                                              "true")).booleanValue());

        setShareBetaNodes(Boolean.valueOf(this.chainedProperties.getProperty(ShareBetaNodesOption.PROPERTY_NAME,
                                                                             "true")).booleanValue());

        setPermGenThreshold(Integer.parseInt(this.chainedProperties.getProperty(PermGenThresholdOption.PROPERTY_NAME,
                                                                                "" + PermGenThresholdOption.DEFAULT_VALUE)));

        setAlphaNodeHashingThreshold(Integer.parseInt(this.chainedProperties.getProperty(AlphaThresholdOption.PROPERTY_NAME,
                                                                                         "3")));

        setCompositeKeyDepth( Integer.parseInt( this.chainedProperties.getProperty( CompositeKeyDepthOption.PROPERTY_NAME,
                                                                                    "3" ) ) );

        setIndexLeftBetaMemory( Boolean.valueOf( this.chainedProperties.getProperty( IndexLeftBetaMemoryOption.PROPERTY_NAME,
                                                                                     "true" ) ).booleanValue() );
        setIndexRightBetaMemory( Boolean.valueOf( this.chainedProperties.getProperty( IndexRightBetaMemoryOption.PROPERTY_NAME,
                                                                                      "true" ) ).booleanValue() );

        setAssertBehaviour( AssertBehaviour.determineAssertBehaviour( this.chainedProperties.getProperty( AssertBehaviorOption.PROPERTY_NAME,
                                                                                                          "identity" ) ) );
        setLogicalOverride( LogicalOverride.determineLogicalOverride( this.chainedProperties.getProperty( "drools.logicalOverride",
                                                                                                          "discard" ) ) );

        setExecutorService( this.chainedProperties.getProperty( "drools.executorService",
                                                                "org.drools.concurrent.DefaultExecutorService" ) );

        setConsequenceExceptionHandler( this.chainedProperties.getProperty( ConsequenceExceptionHandlerOption.PROPERTY_NAME,
                                                                            "org.drools.runtime.rule.impl.DefaultConsequenceExceptionHandler" ) );

        setRuleBaseUpdateHandler( this.chainedProperties.getProperty( "drools.ruleBaseUpdateHandler",
                                                                      "" ) );

        setConflictResolver( determineConflictResolver( this.chainedProperties.getProperty( "drools.conflictResolver",
                                                        "org.drools.conflict.DepthConflictResolver" ) ) );

        setAdvancedProcessRuleIntegration( Boolean.valueOf( this.chainedProperties.getProperty( "drools.advancedProcessRuleIntegration",
                                                                                                "false" ) ).booleanValue() );

        setMultithreadEvaluation( Boolean.valueOf( this.chainedProperties.getProperty( MultithreadEvaluationOption.PROPERTY_NAME,
                                                                                       "false" ) ).booleanValue() );

        setMaxThreads( Integer.parseInt( this.chainedProperties.getProperty( MaxThreadsOption.PROPERTY_NAME,
                                                                             "3" ) ) );

        setEventProcessingMode( EventProcessingOption.determineEventProcessingMode( this.chainedProperties.getProperty( EventProcessingOption.PROPERTY_NAME,
                                                                                                                        "cloud" ) ) );

        setMBeansEnabled( MBeansOption.isEnabled( this.chainedProperties.getProperty( MBeansOption.PROPERTY_NAME,
                                                                                      "disabled" ) ) );

        setClassLoaderCacheEnabled( Boolean.valueOf( this.chainedProperties.getProperty( ClassLoaderCacheOption.PROPERTY_NAME,
                                                                                         "true" ) ) );
        
        setLRUnlinkingEnabled( Boolean.valueOf( this.chainedProperties.getProperty( LRUnlinkingOption.PROPERTY_NAME,
                                                                                    "false" ) ) );
        setDeclarativeAgendaEnabled( Boolean.valueOf( this.chainedProperties.getProperty( DeclarativeAgendaOption.PROPERTY_NAME,
                                                                                          "false" ) ) );        

    }

    /**
     * Makes the configuration object immutable. Once it becomes immutable,
     * there is no way to make it mutable again.
     * This is done to keep consistency.
     */
    public void makeImmutable() {
        this.immutable = true;
    }

    /**
     * Returns true if this configuration object is immutable or false otherwise.
     * @return
     */
    public boolean isImmutable() {
        return this.immutable;
    }

    private void checkCanChange() {
        if ( this.immutable ) {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }

    public void setSequential(boolean sequential) {
        this.sequential = sequential;
        if (sequential && isLRUnlinkingEnabled()) {
            throw new IllegalArgumentException( "Sequential mode cannot be used when Left & Right unlinking is enabled." );
        }
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

    public int getPermGenThreshold() {
        return this.permGenThreshold;
    }

    public void setPermGenThreshold(final int permGenThreshold) {
        checkCanChange(); // throws an exception if a change isn't possible;
        if (permGenThreshold < 0 || permGenThreshold > 100) {
            throw new UnsupportedOperationException( "The PermGen threshold should be a number between 0 and 100" );
        }
        this.permGenThreshold = permGenThreshold;
    }

    public int getAlphaNodeHashingThreshold() {
        return this.alphaNodeHashingThreshold;
    }

    public void setAlphaNodeHashingThreshold(final int alphaNodeHashingThreshold) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.alphaNodeHashingThreshold = alphaNodeHashingThreshold;
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

    public int getCompositeKeyDepth() {
        return this.compositeKeyDepth;
    }

    public void setCompositeKeyDepth(final int compositeKeyDepth) {
        if ( !this.immutable ) {
            if ( compositeKeyDepth > 3 ) {
                throw new UnsupportedOperationException( "compositeKeyDepth cannot be greater than 3" );
            }
            this.compositeKeyDepth = compositeKeyDepth;
        } else {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
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

    public LogicalOverride getLogicalOverride() {
        return this.logicalOverride;
    }

    public void setLogicalOverride(final LogicalOverride logicalOverride) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.logicalOverride = logicalOverride;
    }

    public String getExecutorService() {
        return executorService;
    }

    public void setExecutorService(String executorService) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.executorService = executorService;
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

    public AgendaGroupFactory getAgendaGroupFactory() {
        if ( isSequential() ) {
            if ( this.sequentialAgenda == SequentialAgenda.SEQUENTIAL ) {
                return ArrayAgendaGroupFactory.getInstance();
            } else {
                return PriorityQueueAgendaGroupFactory.getInstance();
            }
        } else {
            return PriorityQueueAgendaGroupFactory.getInstance();
        }
    }

    public SequentialAgenda getSequentialAgenda() {
        return this.sequentialAgenda;
    }

    public void setSequentialAgenda(final SequentialAgenda sequentialAgenda) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.sequentialAgenda = sequentialAgenda;
    }

    /**
     * Defines if the RuleBase should be executed using a pool of
     * threads for evaluating the rules ("true"), or if the rulebase 
     * should work in classic single thread mode ("false").
     * 
     * @param enableMultithread true for multi-thread or 
     *                     false for single-thread. Default is false.
     */
    public void setMultithreadEvaluation(boolean enableMultithread) {
        checkCanChange();
        if( enableMultithread ) {
            throw new IllegalArgumentException( "Multithread mode is currently not supported. Please disable it." );
        }
        this.multithread = enableMultithread;
        if (multithread && isLRUnlinkingEnabled()) {
            throw new IllegalArgumentException( "Multithread evaluation cannot be used when Left & Right Unlinking is enabled." );
        }
    }

    /**
     * Returns true if the partitioning of the rulebase is enabled
     * and false otherwise. Default is false.
     * 
     * @return
     */
    public boolean isMultithreadEvaluation() {
        return this.multithread;
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

    public boolean isClassLoaderCacheEnabled() {
        return this.classLoaderCacheEnabled;
    }

    public void setClassLoaderCacheEnabled(final boolean classLoaderCacheEnabled) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.classLoaderCacheEnabled = classLoaderCacheEnabled;
        this.classLoader.setCachingEnabled( this.classLoaderCacheEnabled );
    }
    
    /**
     * @return whether or not Left & Right Unlinking is enabled.
     */
    public boolean isLRUnlinkingEnabled() {
        return this.lrUnlinkingEnabled;
    }
    
    /**
     * Enable Left & Right Unlinking. It will also disable sequential mode 
     * and multithread evaluation as these are incompatible with L&R unlinking.
     * @param enabled
     */
    public void setLRUnlinkingEnabled(boolean enabled) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.lrUnlinkingEnabled = enabled;

        if ( enabled && isSequential() ) {
            throw new IllegalArgumentException( "Sequential mode cannot be used when Left & Right Unlinking is enabled." );
        }
        
        if ( enabled && isMultithreadEvaluation() ) {
            throw new IllegalArgumentException( "Multithread evaluation cannot be used when Left & Right Unlinking is enabled." );
        }
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

    public List<Map<String, Object>> getWorkDefinitions() {
        if ( this.workDefinitions == null ) {
            initWorkDefinitions();
        }
        return this.workDefinitions;

    }

    private void initWorkDefinitions() {
        this.workDefinitions = new ArrayList<Map<String, Object>>();

        // split on each space
        String locations[] = this.chainedProperties.getProperty( "drools.workDefinitions",
                                                                 "" ).split( "\\s" );

        // load each SemanticModule
        for ( String factoryLocation : locations ) {
            // trim leading/trailing spaces and quotes
            factoryLocation = factoryLocation.trim();
            if ( factoryLocation.startsWith( "\"" ) ) {
                factoryLocation = factoryLocation.substring( 1 );
            }
            if ( factoryLocation.endsWith( "\"" ) ) {
                factoryLocation = factoryLocation.substring( 0,
                                                             factoryLocation.length() - 1 );
            }
            if ( !factoryLocation.equals( "" ) ) {
                loadWorkItems( factoryLocation );
            }
        }
    }

    private void loadWorkItems(String location) {
        String content = ConfFileUtils.URLContentsToString( ConfFileUtils.getURL( location,
                                                                                  null,
                                                                                  RuleBaseConfiguration.class ) );
        try {
            this.workDefinitions.addAll(
                (List<Map<String, Object>>) MVEL.eval( content, new HashMap() ) );
        } catch ( Throwable t ) {
            System.err.println( "Error occured while loading work definitions " + location );
            System.err.println( "Continuing without reading these work definitions" );
            t.printStackTrace();
            throw new RuntimeException( "Could not parse work definitions " + location + ": " + t.getMessage() );
        }
    }

    public boolean isAdvancedProcessRuleIntegration() {
        return advancedProcessRuleIntegration;
    }

    public void setAdvancedProcessRuleIntegration(boolean advancedProcessRuleIntegration) {
        this.advancedProcessRuleIntegration = advancedProcessRuleIntegration;
    }
    
    public void addActivationListener(String name, ActivationListenerFactory factory) {
        if ( this.activationListeners == null ) {
            this.activationListeners = new HashMap<String, ActivationListenerFactory>();
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

    private boolean determineShadowProxy(String userValue) {
        if ( this.isSequential() ) {
            // sequential never needs shadowing, so always override
            return false;
        }

        if ( userValue != null ) {
            return Boolean.valueOf( userValue ).booleanValue();
        } else {
            return true;
        }
    }

    private ConflictResolver determineConflictResolver(String className) {
        Class clazz = null;
        try {
            clazz = this.classLoader.loadClass( className );
        } catch ( ClassNotFoundException e ) {
            throw new IllegalArgumentException( "conflict Resolver '" + className + "' not found" );
        }


        try {
            return (ConflictResolver) clazz.getMethod( "getInstance",
                                                       null ).invoke( null,
                                                                      null );
        } catch ( Exception e ) {
            throw new IllegalArgumentException( "Unable to set Conflict Resolver '" + className + "'" );
        }
    }

    public void setConflictResolver(ConflictResolver conflictResolver) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.conflictResolver = conflictResolver;
    }

    public ConflictResolver getConflictResolver() {
        return this.conflictResolver;
    }

    public CompositeClassLoader getClassLoader() {
        return this.classLoader.clone();
    }

    public void setClassLoader(ClassLoader... classLoaders) {
        this.classLoader = ClassLoaderUtil.getClassLoader( classLoaders,
                                                           getClass(),
                                                           isClassLoaderCacheEnabled() );
    }

    /**
     * Defines if the RuleBase should expose management and monitoring MBeans
     * 
     * @param enableMultithread true for multi-thread or 
     *                     false for single-thread. Default is false.
     */
    public void setMBeansEnabled(boolean mbeansEnabled) {
        checkCanChange();
        this.mbeansEnabled = mbeansEnabled;
    }

    /**
     * Returns true if the management and monitoring through MBeans is active 
     * 
     * @return
     */
    public boolean isMBeansEnabled() {
        return this.mbeansEnabled;
    }

    public static class AssertBehaviour
        implements
        Externalizable {
        private static final long           serialVersionUID = 510l;

        public static final AssertBehaviour IDENTITY         = new AssertBehaviour( 0 );
        public static final AssertBehaviour EQUALITY         = new AssertBehaviour( 1 );

        private int                         value;

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            value = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt( value );
        }

        public AssertBehaviour() {

        }

        private AssertBehaviour(final int value) {
            this.value = value;
        }

        public boolean equals(Object obj) {
            if ( obj == this ) return true;
            else if ( obj instanceof AssertBehaviour ) {
                AssertBehaviour that = (AssertBehaviour) obj;

                return value == that.value;
            }
            return false;
        }

        public static AssertBehaviour determineAssertBehaviour(final String value) {
            if ( "IDENTITY".equalsIgnoreCase( value ) ) {
                return IDENTITY;
            } else if ( "EQUALITY".equalsIgnoreCase( value ) ) {
                return EQUALITY;
            } else {
                throw new IllegalArgumentException( "Illegal enum value '" + value + "' for AssertBehaviour" );
            }
        }

        private Object readResolve() throws java.io.ObjectStreamException {
            switch ( this.value ) {
                case 0 :
                    return IDENTITY;
                case 1 :
                    return EQUALITY;
                default :
                    throw new IllegalArgumentException( "Illegal enum value '" + this.value + "' for AssertBehaviour" );
            }
        }

        public String toExternalForm() {
            return (this.value == 0) ? "identity" : "equality";
        }

        public String toString() {
            return "AssertBehaviour : " + ((this.value == 0) ? "identity" : "equality");
        }
    }

    public static class LogicalOverride
        implements
        Externalizable {
        private static final long           serialVersionUID = 510l;

        public static final LogicalOverride PRESERVE         = new LogicalOverride( 0 );
        public static final LogicalOverride DISCARD          = new LogicalOverride( 1 );

        private int                         value;

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            value = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt( value );
        }

        public LogicalOverride() {

        }

        private LogicalOverride(final int value) {
            this.value = value;
        }

        public static LogicalOverride determineLogicalOverride(final String value) {
            if ( "PRESERVE".equalsIgnoreCase( value ) ) {
                return PRESERVE;
            } else if ( "DISCARD".equalsIgnoreCase( value ) ) {
                return DISCARD;
            } else {
                throw new IllegalArgumentException( "Illegal enum value '" + value + "' for LogicalOverride" );
            }
        }

        private Object readResolve() throws java.io.ObjectStreamException {
            switch ( this.value ) {
                case 0 :
                    return PRESERVE;
                case 1 :
                    return DISCARD;
                default :
                    throw new IllegalArgumentException( "Illegal enum value '" + this.value + "' for LogicalOverride" );
            }
        }

        public boolean equals(Object obj) {
            if ( obj == this ) {
                return true;
            } else if ( obj instanceof LogicalOverride ) {
                return value == ((LogicalOverride) obj).value;
            }
            return false;
        }

        public String toExternalForm() {
            return (this.value == 0) ? "preserve" : "discard";
        }

        public String toString() {
            return "LogicalOverride : " + ((this.value == 0) ? "preserve" : "discard");
        }
    }

    public static class SequentialAgenda
        implements
        Externalizable {
        private static final long            serialVersionUID = 510l;

        public static final SequentialAgenda SEQUENTIAL       = new SequentialAgenda( 0 );
        public static final SequentialAgenda DYNAMIC          = new SequentialAgenda( 1 );

        private int                          value;

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            value = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt( value );
        }

        public SequentialAgenda() {

        }

        private SequentialAgenda(final int value) {
            this.value = value;
        }

        public static SequentialAgenda determineSequentialAgenda(final String value) {
            if ( "sequential".equalsIgnoreCase( value ) ) {
                return SEQUENTIAL;
            } else if ( "dynamic".equalsIgnoreCase( value ) ) {
                return DYNAMIC;
            } else {
                throw new IllegalArgumentException( "Illegal enum value '" + value + "' for SequentialAgenda" );
            }
        }

        private Object readResolve() throws java.io.ObjectStreamException {
            switch ( this.value ) {
                case 0 :
                    return SEQUENTIAL;
                case 1 :
                    return DYNAMIC;
                default :
                    throw new IllegalArgumentException( "Illegal enum value '" + this.value + "' for SequentialAgenda" );
            }
        }

        public String toExternalForm() {
            return (this.value == 0) ? "sequential" : "dynamic";
        }

        public String toString() {
            return "SequentialAgenda : " + ((this.value == 0) ? "sequential" : "dynamic");
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends SingleValueKnowledgeBaseOption> T getOption(Class<T> option) {
        if ( MaintainTMSOption.class.equals( option ) ) {
            return (T) (this.maintainTms ? MaintainTMSOption.YES : MaintainTMSOption.NO);
        } else if ( SequentialOption.class.equals( option ) ) {
            return (T) (this.sequential ? SequentialOption.YES : SequentialOption.NO);
        } else if ( RemoveIdentitiesOption.class.equals( option ) ) {
            return (T) (this.removeIdentities ? RemoveIdentitiesOption.YES : RemoveIdentitiesOption.NO);
        } else if ( ShareAlphaNodesOption.class.equals( option ) ) {
            return (T) (this.shareAlphaNodes ? ShareAlphaNodesOption.YES : ShareAlphaNodesOption.NO);
        } else if ( ShareBetaNodesOption.class.equals( option ) ) {
            return (T) (this.shareBetaNodes ? ShareBetaNodesOption.YES : ShareBetaNodesOption.NO);
        } else if ( IndexLeftBetaMemoryOption.class.equals( option ) ) {
            return (T) (this.indexLeftBetaMemory ? IndexLeftBetaMemoryOption.YES : IndexLeftBetaMemoryOption.NO);
        } else if ( IndexRightBetaMemoryOption.class.equals( option ) ) {
            return (T) (this.indexRightBetaMemory ? IndexRightBetaMemoryOption.YES : IndexRightBetaMemoryOption.NO);
        } else if ( AssertBehaviorOption.class.equals( option ) ) {
            return (T) ((this.assertBehaviour == AssertBehaviour.IDENTITY) ? AssertBehaviorOption.IDENTITY : AssertBehaviorOption.EQUALITY);
        } else if ( LogicalOverrideOption.class.equals( option ) ) {
            return (T) ((this.logicalOverride == LogicalOverride.DISCARD) ? LogicalOverrideOption.DISCARD : LogicalOverrideOption.PRESERVE);
        } else if ( SequentialAgendaOption.class.equals( option ) ) {
            return (T) ((this.sequentialAgenda == SequentialAgenda.SEQUENTIAL) ? SequentialAgendaOption.SEQUENTIAL : SequentialAgendaOption.DYNAMIC);
        } else if ( PermGenThresholdOption.class.equals( option ) ) {
            return (T) PermGenThresholdOption.get( permGenThreshold );
        } else if ( AlphaThresholdOption.class.equals( option ) ) {
            return (T) AlphaThresholdOption.get( alphaNodeHashingThreshold );
        } else if ( CompositeKeyDepthOption.class.equals( option ) ) {
            return (T) CompositeKeyDepthOption.get( compositeKeyDepth );
        } else if ( ConsequenceExceptionHandlerOption.class.equals( option ) ) {
            Class< ? extends ConsequenceExceptionHandler> handler;
            try {
                handler = (Class< ? extends ConsequenceExceptionHandler>) Class.forName( consequenceExceptionHandler );
            } catch ( ClassNotFoundException e ) {
                throw new RuntimeDroolsException( "Unable to resolve ConsequenceExceptionHandler class: " + consequenceExceptionHandler,
                                                  e );
            }
            return (T) ConsequenceExceptionHandlerOption.get(handler);
        } else if ( EventProcessingOption.class.equals( option ) ) {
            return (T) getEventProcessingMode();
        } else if ( MaxThreadsOption.class.equals( option ) ) {
            return (T) MaxThreadsOption.get( getMaxThreads() );
        } else if ( MultithreadEvaluationOption.class.equals( option ) ) {
            return (T) (this.multithread ? MultithreadEvaluationOption.YES : MultithreadEvaluationOption.NO);
        } else if ( MBeansOption.class.equals( option ) ) {
            return (T) (this.isMBeansEnabled() ? MBeansOption.ENABLED : MBeansOption.DISABLED);
        } else if ( ClassLoaderCacheOption.class.equals( option ) ) {
            return (T) (this.isClassLoaderCacheEnabled() ? ClassLoaderCacheOption.ENABLED : ClassLoaderCacheOption.DISABLED);
        } else if ( LRUnlinkingOption.class.equals( option ) ) {
            return (T) (this.isLRUnlinkingEnabled() ? LRUnlinkingOption.ENABLED : LRUnlinkingOption.DISABLED);
        } else if ( DeclarativeAgendaOption.class.equals( option )  ) {
            return (T) (this.isDeclarativeAgenda() ? DeclarativeAgendaOption.ENABLED : DeclarativeAgendaOption.DISABLED);
        }
        return null;

    }

    public <T extends KnowledgeBaseOption> void setOption(T option) {
        if ( option instanceof MaintainTMSOption ) {
            setMaintainTms( ((MaintainTMSOption) option).isMaintainTMS() );
        } else if ( option instanceof SequentialOption ) {
            setSequential( ((SequentialOption) option).isSequential() );
        } else if ( option instanceof RemoveIdentitiesOption ) {
            setRemoveIdentities( ((RemoveIdentitiesOption) option).isRemoveIdentities() );
        } else if ( option instanceof ShareAlphaNodesOption ) {
            setShareAlphaNodes( ((ShareAlphaNodesOption) option).isShareAlphaNodes() );
        } else if ( option instanceof ShareBetaNodesOption ) {
            setShareBetaNodes( ((ShareBetaNodesOption) option).isShareBetaNodes() );
        } else if ( option instanceof IndexLeftBetaMemoryOption ) {
            setIndexLeftBetaMemory( ((IndexLeftBetaMemoryOption) option).isIndexLeftBetaMemory() );
        } else if ( option instanceof IndexRightBetaMemoryOption ) {
            setIndexRightBetaMemory( ((IndexRightBetaMemoryOption) option).isIndexRightBetaMemory() );
        } else if ( option instanceof AssertBehaviorOption ) {
            setAssertBehaviour( (option == AssertBehaviorOption.IDENTITY) ? AssertBehaviour.IDENTITY : AssertBehaviour.EQUALITY );
        } else if ( option instanceof LogicalOverrideOption ) {
            setLogicalOverride( (option == LogicalOverrideOption.DISCARD) ? LogicalOverride.DISCARD : LogicalOverride.PRESERVE );
        } else if ( option instanceof SequentialAgendaOption ) {
            setSequentialAgenda( (option == SequentialAgendaOption.SEQUENTIAL) ? SequentialAgenda.SEQUENTIAL : SequentialAgenda.DYNAMIC );
        } else if ( option instanceof PermGenThresholdOption ) {
            setPermGenThreshold(((PermGenThresholdOption) option).getThreshold());
        } else if ( option instanceof AlphaThresholdOption ) {
            setAlphaNodeHashingThreshold(((AlphaThresholdOption) option).getThreshold());
        } else if ( option instanceof CompositeKeyDepthOption ) {
            setCompositeKeyDepth(((CompositeKeyDepthOption) option).getDepth());
        } else if ( option instanceof ConsequenceExceptionHandlerOption ) {
            setConsequenceExceptionHandler(((ConsequenceExceptionHandlerOption) option).getHandler().getName());
        } else if ( option instanceof EventProcessingOption ) {
            setEventProcessingMode((EventProcessingOption) option);
        } else if ( option instanceof MaxThreadsOption ) {
            setMaxThreads(((MaxThreadsOption) option).getMaxThreads());
        } else if ( option instanceof MultithreadEvaluationOption ) {
            setMultithreadEvaluation(((MultithreadEvaluationOption) option).isMultithreadEvaluation());
        } else if ( option instanceof MBeansOption ) {
            setMBeansEnabled(((MBeansOption) option).isEnabled());
        } else if ( option instanceof ClassLoaderCacheOption ) {
            setClassLoaderCacheEnabled(((ClassLoaderCacheOption) option).isClassLoaderCacheEnabled());
        } else if ( option instanceof LRUnlinkingOption ) {
            setLRUnlinkingEnabled(((LRUnlinkingOption) option).isLRUnlinkingEnabled());
        } else if ( option instanceof DeclarativeAgendaOption ) {
            setDeclarativeAgendaEnabled( ((DeclarativeAgendaOption) option).isDeclarativeAgendaEnabled() );
        }

    }

    public <T extends MultiValueKnowledgeBaseOption> T getOption(Class<T> option,
                                                                 String key) {
        return null;
    }

}
