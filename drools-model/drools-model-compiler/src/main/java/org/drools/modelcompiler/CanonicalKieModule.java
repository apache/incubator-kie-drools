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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.appformer.maven.support.DependencyFilter;
import org.appformer.maven.support.PomModel;
import org.drools.compiler.kie.builder.impl.FileKieModule;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBaseUpdateContext;
import org.drools.compiler.kie.builder.impl.KieProject;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.drools.compiler.kie.util.KieJarChangeSet;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.common.ResourceProvider;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.util.IoUtils;
import org.drools.model.Model;
import org.drools.model.NamedModelItem;
import org.drools.modelcompiler.builder.CanonicalKieBaseUpdater;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.ResourceChange;
import org.kie.internal.builder.ResourceChangeSet;

import static org.drools.model.impl.ModelComponent.areEqualInModel;
import static org.drools.modelcompiler.util.StringUtil.fileNameToClass;

public class CanonicalKieModule implements InternalKieModule {

    public static final String MODEL_FILE = "META-INF/kie/drools-model";

    private final InternalKieModule internalKieModule;

    private Collection<String> ruleClassesNames;

    private final Map<String, CanonicalKiePackages> pkgsInKbase = new HashMap<>();

    private final Map<String, Model> models = new HashMap<>();

    private ProjectClassLoader moduleClassLoader;

    public CanonicalKieModule( ReleaseId releaseId, KieModuleModel kieProject, File file ) {
        this( releaseId, kieProject, file, null );
    }

    public CanonicalKieModule( ReleaseId releaseId, KieModuleModel kieProject, File file, Collection<String> ruleClassesNames ) {
        this( file.isDirectory() ? new FileKieModule( releaseId, kieProject, file ) : new ZipKieModule( releaseId, kieProject, file ), ruleClassesNames );
    }

    public CanonicalKieModule( InternalKieModule internalKieModule ) {
        this( internalKieModule, null );
    }

    public CanonicalKieModule( InternalKieModule internalKieModule, Collection<String> ruleClassesNames ) {
        this.internalKieModule = internalKieModule;
        this.ruleClassesNames = ruleClassesNames;
    }

    @Override
    public Map<String, byte[]> getClassesMap( boolean includeTypeDeclarations ) {
        return internalKieModule.getClassesMap( true );
    }

    @Override
    public ResultsImpl build() {
        // TODO should this initialize the CanonicalKieModule in some way? (doesn't seem necessary so far)
        return new ResultsImpl();
    }

    @Override
    public InternalKnowledgeBase createKieBase( KieBaseModelImpl kBaseModel, KieProject kieProject, ResultsImpl messages, KieBaseConfiguration conf ) {
        this.moduleClassLoader = (( ProjectClassLoader ) kieProject.getClassLoader());
        KieBaseConfiguration kBaseConf = getKieBaseConfiguration( kBaseModel, moduleClassLoader, conf );
        CanonicalKiePackages kpkgs = pkgsInKbase.computeIfAbsent( kBaseModel.getName(), k -> createKiePackages(kBaseModel, kBaseConf) );
        return new KieBaseBuilder( kBaseModel, kBaseConf ).createKieBase(kpkgs);
    }

    private CanonicalKiePackages createKiePackages( KieBaseModelImpl kBaseModel, KieBaseConfiguration conf ) {
        return new KiePackagesBuilder(conf, getModelForKBase(kBaseModel), this.moduleClassLoader).build();
    }

    public CanonicalKiePackages getKiePackages( KieBaseModelImpl kBaseModel ) {
        return pkgsInKbase.computeIfAbsent( kBaseModel.getName(), k -> createKiePackages(kBaseModel, getKnowledgeBaseConfiguration(kBaseModel, getModuleClassLoader())) );
    }

    public ProjectClassLoader getModuleClassLoader() {
        if (moduleClassLoader == null) {
            moduleClassLoader = createModuleClassLoader( null );
            moduleClassLoader.storeClasses( getClassesMap( true ) );
        }
        return moduleClassLoader;
    }

    public void setModuleClassLoader(ProjectClassLoader moduleClassLoader) {
        pkgsInKbase.clear();
        models.clear();
        this.moduleClassLoader = moduleClassLoader;
    }

    private Map<String, Model> getModels() {
        if ( models.isEmpty() ) {
            for (String rulesFile : getRuleClassNames()) {
                Model model = createInstance( getModuleClassLoader(), rulesFile );
                models.put( model.getName(), model );
            }
        }
        return models;
    }

    private Collection<String> getRuleClassNames() {
        if ( ruleClassesNames == null ) {
            ruleClassesNames = findRuleClassesNames( getModuleClassLoader() );
        }
        return ruleClassesNames;
    }

    private Collection<Model> getModelForKBase(KieBaseModelImpl kBaseModel) {
        Map<String, Model> modelsMap = getModels();
        if (kBaseModel.getPackages().isEmpty()) {
            return modelsMap.values();
        }
        Collection<Model> models = new ArrayList<>();
        for (String pkg : kBaseModel.getPackages()) {
            Model model = modelsMap.get(pkg);
            if ( model != null ) {
                models.add( model );
            }
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
        findChanges(result, newKieModule);

        Map<String, Model> oldModels = getModels();
        Map<String, Model> newModels = (( CanonicalKieModule ) newKieModule).getModels();

        for (Map.Entry<String, Model> entry : oldModels.entrySet()) {
            Model newModel = newModels.get( entry.getKey() );
            if ( newModel == null ) {
                // TODO all the resources from the old model have to be flagged as removed
                continue;
            }

            Model oldModel = entry.getValue();
            ResourceChangeSet changeSet = calculateResourceChangeSet( oldModel, newModel );
            if ( !changeSet.getChanges().isEmpty() ) {
                result.registerChanges( entry.getKey(), changeSet );
            }
        }

        return result;
    }

    private void findChanges(KieJarChangeSet result, InternalKieModule newKieModule) {
        Collection<String> oldFiles = getFileNames();
        Collection<String> newFiles = newKieModule.getFileNames();

        ArrayList<String> removedFiles = new ArrayList<>( oldFiles );
        removedFiles.removeAll( newFiles );
        if( ! removedFiles.isEmpty() ) {
            for( String file : removedFiles ) {
                if ( isChange( file, this ) ) {
                    result.removeFile( file );
                }
            }
        }

        for ( String file : newFiles ) {
            if ( oldFiles.contains( file ) && isChange( file, this ) ) {
                // check for modification
                byte[] oldBytes = getBytes( file );
                byte[] newBytes = newKieModule.getBytes( file );
                if( ! Arrays.equals( oldBytes, newBytes ) ) {
                    // parse the file to figure out the difference
                    result.registerChanges( file, new ResourceChangeSet( file, ChangeType.UPDATED ) );
                }
            } else if (isChange( file, (( CanonicalKieModule ) newKieModule) )) {
                // file was added
                result.addFile( file );
            }
        }
    }

    private boolean isChange(String fileName, CanonicalKieModule module) {
        return fileName.endsWith( ".class" ) && !module.getRuleClassNames().contains( fileNameToClass(fileName) );
    }

    private ResourceChangeSet calculateResourceChangeSet( Model oldModel, Model newModel ) {
        ResourceChangeSet changeSet = new ResourceChangeSet( oldModel.getName(), ChangeType.UPDATED );
        addModifiedItemsToChangeSet( changeSet, ResourceChange.Type.RULE, oldModel.getRules(), newModel.getRules() );
        addModifiedItemsToChangeSet( changeSet, ResourceChange.Type.RULE, oldModel.getQueries(), newModel.getQueries() );
        addModifiedItemsToChangeSet( changeSet, ResourceChange.Type.GLOBAL, oldModel.getGlobals(), newModel.getGlobals() );
        return changeSet;
    }

    private void addModifiedItemsToChangeSet( ResourceChangeSet changeSet, ResourceChange.Type type, List<? extends NamedModelItem> oldItems, List<? extends NamedModelItem> newItems ) {
        if ( oldItems.isEmpty() ) {
            if ( !newItems.isEmpty() ) {
                for (NamedModelItem newItem : newItems) {
                    changeSet.getChanges().add( new ResourceChange( ChangeType.ADDED, type, newItem.getName() ) );
                }
            }
            return;
        } else if ( newItems.isEmpty() ) {
            for (NamedModelItem oldItem : oldItems) {
                changeSet.getChanges().add( new ResourceChange( ChangeType.REMOVED, type, oldItem.getName() ) );
            }
            return;
        }

        oldItems.sort( Comparator.comparing( NamedModelItem::getName ) );
        newItems.sort( Comparator.comparing( NamedModelItem::getName ) );

        Iterator<? extends NamedModelItem> oldRulesIterator = oldItems.iterator();
        Iterator<? extends NamedModelItem> newRulesIterator = newItems.iterator();

        NamedModelItem currentOld = oldRulesIterator.next();
        NamedModelItem currentNew = newRulesIterator.next();

        while (true) {
            int compare = currentOld.getName().compareTo( currentNew.getName() );
            if ( compare == 0 ) {
                if ( !areEqualInModel( currentOld, currentNew ) ) {
                    changeSet.getChanges().add( new ResourceChange( ChangeType.UPDATED, type, currentOld.getName() ) );
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
                changeSet.getChanges().add( new ResourceChange( ChangeType.REMOVED, type, currentOld.getName() ) );
                if ( oldRulesIterator.hasNext() ) {
                    currentOld = oldRulesIterator.next();
                } else {
                    break;
                }
            } else {
                changeSet.getChanges().add( new ResourceChange( ChangeType.ADDED, type, currentNew.getName() ) );
                if ( newRulesIterator.hasNext() ) {
                    currentNew = newRulesIterator.next();
                } else {
                    break;
                }
            }
        }

        while (oldRulesIterator.hasNext()) {
            changeSet.getChanges().add( new ResourceChange( ChangeType.REMOVED, type, oldRulesIterator.next().getName() ) );
        }

        while (newRulesIterator.hasNext()) {
            changeSet.getChanges().add( new ResourceChange( ChangeType.ADDED, type, newRulesIterator.next().getName() ) );
        }
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


    // Delegate methods

    @Override
    public void cacheKnowledgeBuilderForKieBase( String kieBaseName, KnowledgeBuilder kbuilder ) {
        internalKieModule.cacheKnowledgeBuilderForKieBase( kieBaseName, kbuilder );
    }

    @Override
    public KnowledgeBuilder getKnowledgeBuilderForKieBase( String kieBaseName ) {
        return internalKieModule.getKnowledgeBuilderForKieBase( kieBaseName );
    }

    @Override
    public Collection<KiePackage> getKnowledgePackagesForKieBase( String kieBaseName ) {
        return internalKieModule.getKnowledgePackagesForKieBase( kieBaseName );
    }

    @Override
    public void cacheResultsForKieBase( String kieBaseName, Results results ) {
        internalKieModule.cacheResultsForKieBase( kieBaseName, results );
    }

    @Override
    public Map<String, Results> getKnowledgeResultsCache() {
        return internalKieModule.getKnowledgeResultsCache();
    }

    @Override
    public KieModuleModel getKieModuleModel() {
        return internalKieModule.getKieModuleModel();
    }

    @Override
    public byte[] getBytes() {
        return internalKieModule.getBytes();
    }

    @Override
    public boolean hasResource( String fileName ) {
        return internalKieModule.hasResource( fileName );
    }

    @Override
    public Resource getResource( String fileName ) {
        return internalKieModule.getResource( fileName );
    }

    @Override
    public ResourceConfiguration getResourceConfiguration( String fileName ) {
        return internalKieModule.getResourceConfiguration( fileName );
    }

    @Override
    public Map<ReleaseId, InternalKieModule> getKieDependencies() {
        return internalKieModule.getKieDependencies();
    }

    @Override
    public void addKieDependency( InternalKieModule dependency ) {
        internalKieModule.addKieDependency( dependency );
    }

    @Override
    public Collection<ReleaseId> getJarDependencies( DependencyFilter filter ) {
        return internalKieModule.getJarDependencies( filter );
    }

    @Override
    public Collection<ReleaseId> getUnresolvedDependencies() {
        return internalKieModule.getUnresolvedDependencies();
    }

    @Override
    public void setUnresolvedDependencies( Collection<ReleaseId> unresolvedDependencies ) {
        internalKieModule.setUnresolvedDependencies( unresolvedDependencies );
    }

    @Override
    public boolean isAvailable( String pResourceName ) {
        return internalKieModule.isAvailable( pResourceName );
    }

    @Override
    public byte[] getBytes( String pResourceName ) {
        return internalKieModule.getBytes( pResourceName );
    }

    @Override
    public Collection<String> getFileNames() {
        return internalKieModule.getFileNames();
    }

    @Override
    public File getFile() {
        return internalKieModule.getFile();
    }

    @Override
    public ResourceProvider createResourceProvider() {
        return internalKieModule.createResourceProvider();
    }

    @Override
    public boolean addResourceToCompiler( CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName ) {
        return internalKieModule.addResourceToCompiler( ckbuilder, kieBaseModel, fileName );
    }

    @Override
    public boolean addResourceToCompiler( CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName, ResourceChangeSet rcs ) {
        return internalKieModule.addResourceToCompiler( ckbuilder, kieBaseModel, fileName, rcs );
    }

    @Override
    public long getCreationTimestamp() {
        return internalKieModule.getCreationTimestamp();
    }

    @Override
    public InputStream getPomAsStream() {
        return internalKieModule.getPomAsStream();
    }

    @Override
    public PomModel getPomModel() {
        return internalKieModule.getPomModel();
    }

    @Override
    public KnowledgeBuilderConfiguration getBuilderConfiguration( KieBaseModel kBaseModel ) {
        return internalKieModule.getBuilderConfiguration( kBaseModel );
    }

    @Override
    public ReleaseId getReleaseId() {
        return internalKieModule.getReleaseId();
    }
}