/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBaseUpdateContext;
import org.drools.compiler.kie.builder.impl.KieProject;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.drools.compiler.kie.util.KieJarChangeSet;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.util.IoUtils;
import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.modelcompiler.builder.CanonicalKieBaseUpdater;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.ResourceChange;
import org.kie.internal.builder.ResourceChangeSet;

import static org.drools.model.impl.ModelComponent.areEqualInModel;

public class CanonicalKieModule extends ZipKieModule {

    public static final String MODEL_FILE = "META-INF/kie/drools-model";

    private final Collection<String> ruleClassesNames;

    private final Map<String, CanonicalKiePackages> pkgsInKbase = new HashMap<>();

    private ProjectClassLoader moduleClassLoader;

    public CanonicalKieModule( ReleaseId releaseId, KieModuleModel kieProject, File file ) {
        this( releaseId, kieProject, file, null );
    }

    public CanonicalKieModule( ReleaseId releaseId, KieModuleModel kieProject, File file, Collection<String> ruleClassesNames ) {
        super( releaseId, kieProject, file );
        this.ruleClassesNames = ruleClassesNames;
    }

    @Override
    public Map<String, byte[]> getClassesMap( boolean includeTypeDeclarations ) {
        return super.getClassesMap( true );
    }

    @Override
    public ResultsImpl build() {
        // TODO should this initialize the CanonicalKieModule in some way? (doesn't seem necessary so far)
        return new ResultsImpl();
    }

    @Override
    public InternalKnowledgeBase createKieBase( KieBaseModelImpl kBaseModel, KieProject kieProject, ResultsImpl messages, KieBaseConfiguration conf ) {
        ClassLoader kieProjectCL = kieProject.getClassLoader();
        KieBaseConfiguration kBaseConf = getKieBaseConfiguration( kBaseModel, kieProjectCL, conf );
        CanonicalKiePackages kpkgs = pkgsInKbase.computeIfAbsent( kBaseModel.getName(), k -> createKiePackages(kBaseConf, kieProjectCL) );
        return new KieBaseBuilder( kBaseModel, kieProjectCL, kBaseConf ).createKieBase(kpkgs);
    }

    private CanonicalKiePackages createKiePackages( KieBaseConfiguration conf, ClassLoader kieProjectCL ) {
        return new KiePackagesBuilder(conf, getModels(kieProjectCL).values()).build();
    }

    public CanonicalKiePackages getKiePackages( KieBaseModelImpl kBaseModel ) {
        return pkgsInKbase.computeIfAbsent( kBaseModel.getName(), k ->  {
            ProjectClassLoader kieProjectCL = getModuleClassLoader();
            return createKiePackages(getKnowledgeBaseConfiguration(kBaseModel, kieProjectCL), kieProjectCL);
        });
    }

    private ProjectClassLoader getModuleClassLoader() {
        if (moduleClassLoader == null) {
            moduleClassLoader = createModuleClassLoader( null );
            moduleClassLoader.storeClasses( getClassesMap( true ) );
        }
        return moduleClassLoader;
    }

    private Map<String, Model> getModels() {
        return getModels( getModuleClassLoader() );
    }

    private Map<String, Model> getModels(ClassLoader kieProjectCL) {
        Collection<String> rulesFiles = ruleClassesNames != null ? ruleClassesNames : findRuleClassesNames( kieProjectCL );
        Map<String, Model> models = new HashMap<>();
        for (String rulesFile : rulesFiles) {
            models.put( rulesFile, createInstance( kieProjectCL, rulesFile ) );
        }
        return models;
    }

    private static Collection<String> findRuleClassesNames( ClassLoader kieProjectCL) {
        String modelFiles;
        try {
            modelFiles = new String( IoUtils.readBytesFromInputStream( kieProjectCL.getResourceAsStream( MODEL_FILE ) ) );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        return Arrays.asList( modelFiles.split( "\n" ) );
    }

    private static <T> T createInstance( ClassLoader cl, String className ) {
        try {
            return ( T ) cl.loadClass( className ).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public KieJarChangeSet getChanges( InternalKieModule newKieModule ) {
        KieJarChangeSet result = new KieJarChangeSet();

        Map<String, Model> oldModels = getModels();
        Map<String, Model> newModels = (( CanonicalKieModule ) newKieModule).getModels();

        for (Map.Entry<String, Model> entry : oldModels.entrySet()) {
            Model newModel = newModels.get( entry.getKey() );
            if ( newModel == null ) {
                // TODO all the resources from the old model have to be flagged as removed
                continue;
            }

            Model oldModel = entry.getValue();
            List<Rule> oldRules = oldModel.getRules();
            List<Rule> newRules = newModel.getRules();

            if ( oldRules.isEmpty() ) {
                if ( !newRules.isEmpty() ) {
                    ResourceChangeSet changeSet = new ResourceChangeSet( newRules.get( 0 ).getPackage(), ChangeType.UPDATED );
                    for (Rule newRule : newRules) {
                        changeSet.getChanges().add( new ResourceChange( ChangeType.ADDED, ResourceChange.Type.RULE, newRule.getName() ) );
                    }
                }
                continue;
            } else if ( newRules.isEmpty() ) {
                ResourceChangeSet changeSet = new ResourceChangeSet( oldRules.get( 0 ).getPackage(), ChangeType.UPDATED );
                for (Rule oldRule : oldRules) {
                    changeSet.getChanges().add( new ResourceChange( ChangeType.REMOVED, ResourceChange.Type.RULE, oldRule.getName() ) );
                }
            }

            oldRules.sort( Comparator.comparing( Rule::getName ) );
            newRules.sort( Comparator.comparing( Rule::getName ) );

            ResourceChangeSet changeSet = new ResourceChangeSet( oldRules.get( 0 ).getPackage(), ChangeType.UPDATED );

            Iterator<Rule> oldRulesIterator = oldRules.iterator();
            Iterator<Rule> newRulesIterator = newRules.iterator();

            Rule currentOld = oldRulesIterator.next();
            Rule currentNew = newRulesIterator.next();

            while (true) {
                int compare = currentOld.getName().compareTo( currentNew.getName() );
                if ( compare == 0 ) {
                    if ( !areEqualInModel( currentOld, currentNew ) ) {
                        changeSet.getChanges().add( new ResourceChange( ChangeType.UPDATED, ResourceChange.Type.RULE, currentOld.getName() ) );
                    }
                    if ( oldRulesIterator.hasNext() ) {
                        currentOld = oldRulesIterator.next();
                    } else {
                        break;
                    }
                    if ( newRulesIterator.hasNext() ) {
                        currentNew = newRulesIterator.next();
                    } else {
                        break;
                    }
                } else if ( compare < 0 ) {
                    changeSet.getChanges().add( new ResourceChange( ChangeType.REMOVED, ResourceChange.Type.RULE, currentOld.getName() ) );
                    if ( oldRulesIterator.hasNext() ) {
                        currentOld = oldRulesIterator.next();
                    } else {
                        break;
                    }
                } else {
                    changeSet.getChanges().add( new ResourceChange( ChangeType.ADDED, ResourceChange.Type.RULE, currentNew.getName() ) );
                    if ( newRulesIterator.hasNext() ) {
                        currentNew = newRulesIterator.next();
                    } else {
                        break;
                    }
                }
            }

            while (oldRulesIterator.hasNext()) {
                changeSet.getChanges().add( new ResourceChange( ChangeType.REMOVED, ResourceChange.Type.RULE, oldRulesIterator.next().getName() ) );
            }

            while (newRulesIterator.hasNext()) {
                changeSet.getChanges().add( new ResourceChange( ChangeType.ADDED, ResourceChange.Type.RULE, newRulesIterator.next().getName() ) );
            }

            result.registerChanges( entry.getKey(), changeSet );
        }

        return result;
    }

    @Override
    public boolean isFileInKBase( KieBaseModel kieBase, String fileName ) {
        // TODO
        return true;
    }

    @Override
    public Runnable createKieBaseUpdater(KieBaseUpdateContext context) {
        return new CanonicalKieBaseUpdater( context );
    }

    private static KieBaseConfiguration getKieBaseConfiguration( KieBaseModelImpl kBaseModel, ClassLoader cl, KieBaseConfiguration conf ) {
        if (conf == null) {
            conf = getKnowledgeBaseConfiguration(kBaseModel, cl);
        } else if (conf instanceof RuleBaseConfiguration ) {
            ((RuleBaseConfiguration)conf).setClassLoader(cl);
        }
        return conf;
    }

    private static KieBaseConfiguration getKnowledgeBaseConfiguration( KieBaseModelImpl kBaseModel, ClassLoader cl ) {
        KieBaseConfiguration kbConf = KieServices.get().newKieBaseConfiguration( null, cl );
        if (kBaseModel != null) {
            kbConf.setOption( kBaseModel.getEqualsBehavior() );
            kbConf.setOption( kBaseModel.getEventProcessingMode() );
            kbConf.setOption( kBaseModel.getDeclarativeAgenda() );
        }
        return kbConf;
    }
}