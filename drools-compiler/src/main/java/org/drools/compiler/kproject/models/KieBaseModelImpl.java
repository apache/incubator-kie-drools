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
package org.drools.compiler.kproject.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.kie.api.KieServices;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.RuleTemplateModel;
import org.kie.api.conf.BetaRangeIndexOption;
import org.kie.api.conf.DeclarativeAgendaOption;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseMutabilityOption;
import org.kie.api.conf.PrototypesOption;
import org.kie.api.conf.SequentialOption;
import org.kie.api.conf.SessionsPoolOption;
import org.kie.api.io.ResourceType;

import static org.drools.util.IoUtils.recursiveListFile;

public class KieBaseModelImpl
        implements
        KieBaseModel {

    private String                       name;

    private Set<String>                  includes= new HashSet<>();

    private List<String>                 packages;

    private EqualityBehaviorOption       equalsBehavior = EqualityBehaviorOption.IDENTITY;

    private PrototypesOption             prototypes = PrototypesOption.DISABLED;

    private KieBaseMutabilityOption      mutability = KieBaseMutabilityOption.ALLOWED;

    private EventProcessingOption        eventProcessingMode = EventProcessingOption.CLOUD;

    private DeclarativeAgendaOption      declarativeAgenda = DeclarativeAgendaOption.DISABLED;

    private SequentialOption             sequential = SequentialOption.NO;

    private SessionsPoolOption sessionsPool = SessionsPoolOption.NO;

    private BetaRangeIndexOption         betaRangeIndexOption = BetaRangeIndexOption.DISABLED;

    private Map<String, KieSessionModel> kSessions = new HashMap<>();

    private KieModuleModel               kModule;
    
    private String                       scope = "jakarta.enterprise.context.ApplicationScoped";

    private List<RuleTemplateModel>      ruleTemplates = new ArrayList<>();

    private boolean                      isDefault = false;

    public KieBaseModelImpl() {
    }

    public KieBaseModelImpl(String name) {
        this(null, name);
    }

    public KieBaseModelImpl(KieModuleModel kModule, String name) {
        this.kModule = kModule;
        this.includes = new HashSet<>();
        this.name = name;
        this.kSessions = Collections.emptyMap();
    }

    public static KieBaseModel defaultKieBaseModel() {
        return KieServices.get().newKieModuleModel().newKieBaseModel( "defaultKieBase" ).addPackage( "*" ).setDefault( true );
    }

    public boolean isDefault() {
        return isDefault;
    }

    public KieBaseModel setDefault(boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    public List<String> getPackages() {
        return packages != null ? packages : Collections.emptyList();
    }

    public KieBaseModel addPackage(String pkg) {
        if ( packages == null ) {
            packages = new ArrayList<>();
        }
        packages.add( pkg );
        return this;
    }

    public KieBaseModel removePackage(String pkg) {
        if ( packages != null ) {
            packages.remove( pkg );
        }
        return this;
    }

    public KieModuleModel getKModule() {
        return kModule;
    }

    public void setKModule(KieModuleModel kieProject) {
        this.kModule = kieProject;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseModel#getKieSessionModels()
     */
    public Map<String, KieSessionModel> getKieSessionModels() {
        return Collections.unmodifiableMap( kSessions );
    }

    public Map<String, KieSessionModel> getRawKieSessionModels() {
        return kSessions;
    }    
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseModel#setKSessions(java.util.Map)
     */
    private void setKSessions(Map<String, KieSessionModel> kSessions) {
        this.kSessions = kSessions;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseModel#addKSession(org.kie.kproject.KieSessionModelImpl)
     */
    public KieSessionModel newKieSessionModel(String name) {
        KieSessionModel kieSessionModel = new KieSessionModelImpl( this, name );
        Map<String, KieSessionModel> newMap = new HashMap<>();
        newMap.putAll( this.kSessions );
        newMap.put( kieSessionModel.getName(), kieSessionModel );
        setKSessions( newMap );

        return kieSessionModel;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseModel#removeKieSessionModel(org.kie.kproject.KieSessionModelImpl)
     */
    public KieBaseModel removeKieSessionModel(String qName) {
        Map<String, KieSessionModel> newMap = new HashMap<>();
        newMap.putAll( this.kSessions );
        newMap.remove(qName);
        setKSessions(newMap);
        return this;
    }

    public List<RuleTemplateModel> getRuleTemplates() {
        return Collections.unmodifiableList( ruleTemplates );
    }

    public List<RuleTemplateModel> getRawRuleTemplates() {
        return ruleTemplates;
    }

    public KieBaseModel addRuleTemplate(String dtable, String template, int row, int col) {
        ruleTemplates.add( new RuleTemplateModelImpl( this, dtable, template, row, col ) );
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseModel#getName()
     */
    public String getName() {
        return name;
    }

    public KieBaseModel setNameForUnmarshalling(String name) {
        this.name = name;
        return this;
    }

    void changeKSessionName(KieSessionModel kieSession, String oldName, String newName) {
        kSessions.remove(oldName);
        kSessions.put(newName, kieSession);
    }

    public Set<String> getIncludes() {
        return Collections.unmodifiableSet( includes );
    }

    public KieBaseModel addInclude(String kBaseQName) {
        this.includes.add( kBaseQName );
        return this;
    }

    public KieBaseModel removeInclude(String kBaseQName) {
        this.includes.remove( kBaseQName );
        return this;
    }

    public SessionsPoolOption getSessionsPool() {
        return sessionsPool;
    }

    public KieBaseModel setSessionsPool( SessionsPoolOption sessionsPool ) {
        this.sessionsPool = sessionsPool;
        return this;
    }

    public EqualityBehaviorOption getEqualsBehavior() {
        return equalsBehavior;
    }

    public KieBaseModel setEqualsBehavior(EqualityBehaviorOption equalsBehaviour) {
        this.equalsBehavior = equalsBehaviour;
        return this;
    }

    public PrototypesOption getPrototypes() {
        return prototypes;
    }

    public KieBaseModel setPrototypes(PrototypesOption prototypes) {
        this.prototypes = prototypes;
        return this;
    }

    public KieBaseMutabilityOption getMutability() {
        return mutability;
    }

    public KieBaseModel setMutability( KieBaseMutabilityOption mutability ) {
        this.mutability = mutability;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseModel#getEventProcessingMode()
     */
    public EventProcessingOption getEventProcessingMode() {
        return eventProcessingMode;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseModel#setEventProcessingMode(org.kie.api.conf.EventProcessingOption)
     */
    public KieBaseModel setEventProcessingMode(EventProcessingOption eventProcessingMode) {
        this.eventProcessingMode = eventProcessingMode;
        return this;
    }

    public DeclarativeAgendaOption getDeclarativeAgenda() {
        return declarativeAgenda;
    }

    public KieBaseModel setDeclarativeAgenda(DeclarativeAgendaOption declarativeAgenda) {
        this.declarativeAgenda = declarativeAgenda;
        return this;
    }

    @Override
    public BetaRangeIndexOption getBetaRangeIndexOption() {
        return betaRangeIndexOption;
    }

    @Override
    public KieBaseModel setBetaRangeIndexOption(BetaRangeIndexOption betaRangeIndexOption) {
        this.betaRangeIndexOption = betaRangeIndexOption;
        return this;
    }

    @Override
    public SequentialOption getSequential() {
        return sequential;
    }

    @Override
    public KieBaseModel setSequential(SequentialOption sequential) {
        this.sequential = sequential;
        return this;
    }

    @Override
    public KieBaseModel setScope(String scope) {
        this.scope = scope;
        return this;
    }

    @Override
    public String getScope() {
        return this.scope;
    }
    

    public static List<String> getFiles(String kBaseName,
                                        ZipFile zipFile) {
        List<String> files = new ArrayList<>();
        Enumeration< ? extends ZipEntry> zipEntries = zipFile.entries();
        while ( zipEntries.hasMoreElements() ) {
            ZipEntry zipEntry = zipEntries.nextElement();
            String fileName = zipEntry.getName();
            if ( filterFileInKBase( kBaseName.replace( '.', '/' ), fileName ) ) {
                files.add( fileName );
            }
        }
        return files;
    }
    
    public static List<String> getFiles(java.io.File rootFolder) {
        return recursiveListFile( rootFolder, "", file -> {
                String fileName = file.getName();
                return fileName.endsWith( ResourceType.DRL.getDefaultExtension() ) ||
                       fileName.endsWith( ResourceType.GDRL.getDefaultExtension() ) ||
                       fileName.endsWith( ResourceType.RDRL.getDefaultExtension() ) ||
                       fileName.endsWith( ResourceType.BPMN2.getDefaultExtension() ) ||
                       fileName.endsWith( ResourceType.TDRL.getDefaultExtension() );
        } );
    }

    private static boolean filterFileInKBase(String rootPath,
                                             String fileName) {
        return fileName.startsWith( rootPath ) &&
                (fileName.endsWith( ResourceType.DRL.getDefaultExtension() ) ||
                        fileName.endsWith( ResourceType.GDRL.getDefaultExtension() ) ||
                        fileName.endsWith( ResourceType.RDRL.getDefaultExtension() ) ||
                        fileName.endsWith( ResourceType.BPMN2.getDefaultExtension() ) ||
                        fileName.endsWith( ResourceType.TDRL.getDefaultExtension() ) );
    }

    @Override
    public String toString() {
        return "KieBaseModelImpl [name=" + name + ", includes=" + includes + ", packages=" + getPackages() + ", equalsBehavior=" + equalsBehavior + ", prototypes=" + prototypes + ", mutability=" + mutability + ", eventProcessingMode=" + eventProcessingMode + ", kSessions=" + kSessions + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KieBaseModelImpl that = (KieBaseModelImpl) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
