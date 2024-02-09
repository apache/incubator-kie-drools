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
package org.drools.mvel.compiler.kie.builder.impl;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.drools.compiler.kie.builder.impl.BuildContext;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.KieProject;
import org.drools.compiler.kie.builder.impl.KieRepositoryImpl;
import org.drools.compiler.kie.builder.impl.KieRepositoryImpl.KieModuleRepo;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.io.InternalResource;
import org.drools.wiring.api.ResourceProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.ReleaseIdComparator.ComparableVersion;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.ResourceChangeSet;
import org.kie.util.maven.support.DependencyFilter;
import org.kie.util.maven.support.PomModel;
import org.kie.util.maven.support.ReleaseIdImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * This test contains
 * - normal tests that test for concurrency issues and memory leaks (that the KieModuleRepo
 * functions as a LRU cache, and evicts old {@link KieModule} instances )
 */
public class KieModuleRepoTest {

    private KieModuleRepo kieModuleRepo;

    private int maxSizeGaCacheOrig;
    private int maxSizeGaVersionsCacheOrig;
    private Field maxSizeGaCacheField;
    private Field maxSizeGaVersionsCacheField;

    @Before
    public void before() throws Exception {
        kieModuleRepo = new KieModuleRepo();

        // store the original values as we need to restore them after the test
        maxSizeGaCacheOrig = KieModuleRepo.MAX_SIZE_GA_CACHE;
        maxSizeGaVersionsCacheOrig = KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE;
        maxSizeGaCacheField = KieModuleRepo.class.getDeclaredField("MAX_SIZE_GA_CACHE");
        maxSizeGaVersionsCacheField = KieModuleRepo.class.getDeclaredField("MAX_SIZE_GA_VERSIONS_CACHE");
    }

    @After
    public void after() throws Exception {
        setCacheSize(maxSizeGaCacheField, null, maxSizeGaCacheOrig);
        setCacheSize(maxSizeGaVersionsCacheField, null, maxSizeGaVersionsCacheOrig);
    }

    /**
     * HELPER METHODS -------------------------------------------------------------------------------------------------------------
     */

    protected static void waitFor(final CyclicBarrier barrier) {
        final String threadName = Thread.currentThread().getName();
        try {
            barrier.await();
        } catch( final InterruptedException e ) {
            fail( "Thread '" + threadName + "' was interrupted while waiting for the other threads!");
        } catch( final BrokenBarrierException e ) {
            fail( "Thread '" + threadName + "' barrier was broken while waiting for the other threads!");
        }
    }

    private static KieContainerImpl createMockKieContainer( final ReleaseId projectReleaseId, final KieModuleRepo kieModuleRepo) throws Exception {

        // kie module
        final InternalKieModule mockKieProjectKieModule = mock(InternalKieModule.class);
        final ResourceProvider mockKieProjectKieModuleResourceProvider = mock(ResourceProvider.class);
        when(mockKieProjectKieModule.createResourceProvider()).thenReturn(mockKieProjectKieModuleResourceProvider);

        // kie project
        final KieModuleKieProject kieProject = new KieModuleKieProject(mockKieProjectKieModule);
        final KieModuleKieProject mockKieProject = spy(kieProject);
        doNothing().when(mockKieProject).init();
        doReturn(projectReleaseId).when(mockKieProject).getGAV();
        doReturn( new HashMap<String, KieBaseModel>() ).when( mockKieProject ).updateToModule( any( InternalKieModule.class ) );

        // kie repository
        final KieRepository kieRepository = new KieRepositoryImpl();
        final Field kieModuleRepoField = KieRepositoryImpl.class.getDeclaredField("kieModuleRepo");
        kieModuleRepoField.setAccessible(true);
        kieModuleRepoField.set(kieRepository, kieModuleRepo);
        kieModuleRepoField.setAccessible(false);

        // kie container
        final KieContainerImpl kieContainerImpl = new KieContainerImpl(mockKieProject, kieRepository);
        return kieContainerImpl;
    }

    private static int countKieModules( final Map<String, NavigableMap<ComparableVersion, KieModule>> kieModulesCache ) {
       int numKieModules = 0;
       for( final NavigableMap<ComparableVersion, KieModule> map : kieModulesCache.values() ) {
           numKieModules += map.size();
       }
       return numKieModules;
    }

    private static void setCacheSize( final Field field, final Object fieldObject, final Object newValue ) throws Exception {
        // make accessible
        field.setAccessible(true);
        field.set(null, newValue);
        field.set(fieldObject, newValue);
    }

    /**
     * TESTS ----------------------------------------------------------------------------------------------------------------------
     */

    // simultaneous requests to deploy two new deployments (different versions)
    // for an empty/new GA artifactMap
    @Test(timeout=5000)
    public void testDeployTwoArtifactVersionsSameTime() throws Exception {
        final String groupId = "org";
        final String artifactId = "one";
        final String firstVersion = "1.0";
        final String secondVersion = "1.0-NEW-FEATURE";

        final CyclicBarrier storeOperationBarrier = new CyclicBarrier(2);
        final CyclicBarrier threadsFinishedBarrier = new CyclicBarrier(3);

        final Thread firstThread = new Thread(
                getStoreArtifactRunnable(kieModuleRepo, groupId, artifactId, firstVersion,
                        storeOperationBarrier, threadsFinishedBarrier));

        final Thread secondThread = new Thread(
                getStoreArtifactRunnable(kieModuleRepo, groupId, artifactId, secondVersion,
                        storeOperationBarrier, threadsFinishedBarrier));

        final ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            firstThread.setName("normal");
            executor.submit(firstThread);
            secondThread.setName("newFeature");
            executor.submit(secondThread);
            waitFor(threadsFinishedBarrier);
        } finally {
            executor.shutdownNow();
        }

        final String ga = groupId + ":" + artifactId;
        final Map<ComparableVersion, KieModule> artifactMap = kieModuleRepo.kieModules.get(ga);

        final ComparableVersion normalVersion = new ComparableVersion(firstVersion);
        final KieModule normalKieModule = artifactMap.get(normalVersion);
        final ComparableVersion newFeatureVersion = new ComparableVersion(secondVersion);
        final KieModule newFeatureKieModule = artifactMap.get(newFeatureVersion);

        assertThat(normalKieModule).as("Race condition occurred: normal KieModule disappeared from KieModuleRepo!").isNotNull();
        assertThat(newFeatureKieModule).as("Race condition occurred: new feature KieModule disappeared from KieModuleRepo!").isNotNull();
    }

    public Runnable getStoreArtifactRunnable(final KieModuleRepo kieModuleRepo, final String groupId, final String artifactId,
            final String version, final CyclicBarrier storeOperationBarrier, final CyclicBarrier threadsFinishedBarrier) {
        return () -> {
            final ReleaseIdImpl firstReleaseId = new ReleaseIdImpl(groupId, artifactId, version);
            final KieModule firstKieModule = mock(KieModule.class);
            when(firstKieModule.getReleaseId()).thenReturn(firstReleaseId);
            waitFor(storeOperationBarrier);
            kieModuleRepo.store(firstKieModule);
            waitFor(threadsFinishedBarrier);
        };
    }

    // remove request followed by a store request on a high load system
    // * remove does not completely finish before store starts
    @Test(timeout=5000)
    public void removeStoreArtifactMapTest() throws Exception {
        // actual test
        final ReleaseIdImpl releaseId = new ReleaseIdImpl("org", "redeploy", "2.0");
        final InternalKieModule originalKieModule = mock(InternalKieModule.class);
        when(originalKieModule.getReleaseId()).thenReturn(releaseId);
        when(originalKieModule.getCreationTimestamp()).thenReturn(0l);

        final InternalKieModule redeployKieModule = mock(InternalKieModule.class);
        when(redeployKieModule.getReleaseId()).thenReturn(releaseId);
        when(redeployKieModule.getCreationTimestamp()).thenReturn(1l);

        // 1. initial deploy ("long ago")
        kieModuleRepo.store(originalKieModule);

        final CyclicBarrier storeRemoveOperationBarrier = new CyclicBarrier(2);
        final CyclicBarrier operationsSerializationBarrier = new CyclicBarrier(2);
        final CyclicBarrier threadsFinishedBarrier = new CyclicBarrier(3);

        final Runnable removeRunnable = () -> {
            waitFor(storeRemoveOperationBarrier);
            kieModuleRepo.remove(releaseId);
            waitFor(operationsSerializationBarrier);
            waitFor(threadsFinishedBarrier);
        };

        final Runnable redeployRunnable = () -> {
            waitFor(storeRemoveOperationBarrier);
            waitFor(operationsSerializationBarrier);
            kieModuleRepo.store(redeployKieModule);
            waitFor(threadsFinishedBarrier);
        };

        final ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            // 2. remove and redploy
            executor.submit(removeRunnable);
            executor.submit(redeployRunnable);
            waitFor(threadsFinishedBarrier);
        } finally {
            executor.shutdownNow();
        }

        final String ga = releaseId.getGroupId() + ":" + releaseId.getArtifactId();
        final Map<ComparableVersion, KieModule> artifactMap = kieModuleRepo.kieModules.get(ga);

        assertThat(artifactMap).as("Artifact Map for GA '" + ga + "' not in KieModuleRepo!").isNotNull();

        // never gets this far, but this is a good check
        final KieModule redeployedKieModule = artifactMap.get(new ComparableVersion(releaseId.getVersion()));
        assertThat(redeployedKieModule).as("Redeployed module has disappeared from KieModuleRepo!").isNotNull();
        assertThat(redeployKieModule.getCreationTimestamp()).as("Original module retrieved instead of redeployed module!").isEqualTo(1l);
    }

    private static class InternalKieModuleStub implements InternalKieModule {
        @Override
        public void cacheKnowledgeBuilderForKieBase( String kieBaseName, KnowledgeBuilder kbuilder ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public KnowledgeBuilder getKnowledgeBuilderForKieBase( String kieBaseName ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public InternalKnowledgePackage getPackage( String packageName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<KiePackage> getKnowledgePackagesForKieBase( String kieBaseName ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void cacheResultsForKieBase( String kieBaseName, Results results ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<String, Results> getKnowledgeResultsCache() {
            throw new UnsupportedOperationException();
        }

        @Override
        public KieModuleModel getKieModuleModel() {
            return null;
        }

        @Override
        public byte[] getBytes() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasResource( String fileName ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public InternalResource getResource( String fileName ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ResourceConfiguration getResourceConfiguration( String fileName ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<ReleaseId, InternalKieModule> getKieDependencies() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addKieDependency( InternalKieModule dependency ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<ReleaseId> getJarDependencies( DependencyFilter filter ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<ReleaseId> getUnresolvedDependencies() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setUnresolvedDependencies( Collection<ReleaseId> unresolvedDependencies ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAvailable( String pResourceName ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] getBytes( String pResourceName ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<String> getFileNames() {
            return Collections.emptyList();
        }

        @Override
        public File getFile() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ResourceProvider createResourceProvider() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<String, byte[]> getClassesMap() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addResourceToCompiler( CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addResourceToCompiler( CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName, ResourceChangeSet rcs ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getCreationTimestamp() {
            return 0L;
        }

        @Override
        public InputStream getPomAsStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PomModel getPomModel() {
            throw new UnsupportedOperationException();
        }

        @Override
        public KnowledgeBuilderConfiguration createBuilderConfiguration( KieBaseModel kBaseModel, ClassLoader classLoader ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public InternalKnowledgeBase createKieBase(KieBaseModelImpl kBaseModel, KieProject kieProject, BuildContext buildContext, KieBaseConfiguration conf) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ClassLoader getModuleClassLoader() {
            return null;
        }

        @Override
        public ReleaseId getReleaseId() {
            return new ReleaseIdImpl("org", "deployTwiceAfterUpdateDependency", "1.0");
        }
    }

    // 2. simultaneous deploy requests
    // - multitenant UI
    // - duplicate REST requests
    @Test(timeout=5000)
    public void newerVersionDeployOverwritesTest() throws Exception {

        // setup
        final ReleaseIdImpl releaseId = new ReleaseIdImpl("org", "deployTwiceAfterUpdateDependency", "1.0");
        final InternalKieModule originalOldKieModule = new InternalKieModuleStub();

        final ReleaseId dependentReleaseid = new ReleaseIdImpl("org", "deployTwiceAfterUpdate", "1.0");
        final KieContainerImpl kieContainer = createMockKieContainer(dependentReleaseid, kieModuleRepo);

        // 1. deploy
        kieModuleRepo.store(originalOldKieModule);

        // 2. another project is dependent on this project
        kieContainer.updateDependencyToVersion(releaseId, releaseId);

        final InternalKieModule newKieModule = mock(InternalKieModule.class);
        when(newKieModule.getReleaseId()).thenReturn(releaseId);
        when(newKieModule.getCreationTimestamp()).thenReturn(10l);

        final CyclicBarrier storeOperationBarrier = new CyclicBarrier(2);
        final CyclicBarrier storeSerializationBarrier = new CyclicBarrier(2);
        final CyclicBarrier threadsFinishedBarrier = new CyclicBarrier(3);

        final Runnable deployRunnable = () -> {
            waitFor(storeOperationBarrier);
            // Second thread waits with store until the first one finishes with it.
            if (Thread.currentThread().getName().equals("two")) {
                waitFor(storeSerializationBarrier);
            }
            kieModuleRepo.store(newKieModule);
            if (Thread.currentThread().getName().equals("one")) {
                waitFor(storeSerializationBarrier);
            }
            waitFor(threadsFinishedBarrier);
        };

        final ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            // 2. remove and redploy
            final Thread deployThread = new Thread(deployRunnable);
            deployThread.setName("one");
            executor.submit(deployThread);

            final Thread secondDeployThread = new Thread(deployRunnable);
            secondDeployThread.setName("two");
            executor.submit(secondDeployThread);

            waitFor(threadsFinishedBarrier);
        } finally {
            executor.shutdownNow();
        }

        // never gets this far, but this is a good check
        final KieModule oldKieModule = kieModuleRepo.oldKieModules.get(releaseId);
        final long oldKieModuleTimeStamp = ((InternalKieModule) oldKieModule).getCreationTimestamp();
        final long originalKieModuleTimestamp = originalOldKieModule.getCreationTimestamp();
        assertThat(oldKieModuleTimeStamp).as("The old kie module in the repo is not the originally deployed module!").isEqualTo(originalKieModuleTimestamp);
    }

    @Test
    public void storingNewProjectsCausesOldProjectEvictionFromKieModuleRepoTest() throws Exception {
        // setup
        setCacheSize(maxSizeGaCacheField, null, 3);
        setCacheSize(maxSizeGaVersionsCacheField, null, 2); // to test oldKieModules caching

        final ReleaseIdImpl [] releaseIds = new ReleaseIdImpl[7];
        for( int i = 0; i < releaseIds.length; ++i ) {
            final String artifactId = Character.toString((char)('A'+i));
            releaseIds[i] =  new ReleaseIdImpl("org", artifactId, "1.0");
        }

        // store
        for( int i = 0; i < releaseIds.length; ++i ) {
            final InternalKieModule kieModule = mock(InternalKieModule.class);
            when(kieModule.getReleaseId()).thenReturn(releaseIds[i]);
            when(kieModule.getCreationTimestamp()).thenReturn(10l);
            kieModuleRepo.store(kieModule);
            kieModuleRepo.store(kieModule); // store module 2 times to trigger storage to oldKieModules
        }

        final int numKieModules = countKieModules(kieModuleRepo.kieModules);
        assertThat(numKieModules).as("KieModuleRepo cache should not grow past " + KieModuleRepo.MAX_SIZE_GA_CACHE + ": ").isEqualTo(KieModuleRepo.MAX_SIZE_GA_CACHE);

        final int oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        final int max = KieModuleRepo.MAX_SIZE_GA_CACHE * KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE;
        assertThat(oldKieModulesSize <= max).as("KieModuleRepot old KieModules map is not limited in it's growth: " + oldKieModulesSize + " > " + max).isTrue();
    }

    @Test
    public void storingNewProjectVersionsCausesOldVersionEvictionFromKieModuleRepoTest() throws Exception {
        // setup
        setCacheSize(maxSizeGaCacheField, null, 2); // to test oldKieModules caching
        setCacheSize(maxSizeGaVersionsCacheField, null, 3);

        final ReleaseIdImpl [] releaseIds = new ReleaseIdImpl[7];
        for( int i = 0; i < releaseIds.length; ++i ) {
            releaseIds[i] =  new ReleaseIdImpl("org", "test", "1." + i);
        }

        // store
        for( int i = 0; i < releaseIds.length; ++i ) {
            final InternalKieModule kieModule = mock(InternalKieModule.class);
            when(kieModule.getReleaseId()).thenReturn(releaseIds[i]);
            when(kieModule.getCreationTimestamp()).thenReturn(10l);
            kieModuleRepo.store(kieModule);
            kieModuleRepo.store(kieModule); // in order to trigger storage to oldKieModules
        }

        int numKieModules = countKieModules(kieModuleRepo.kieModules);
        assertThat(numKieModules).as("KieModuleRepo cache should not grow past " + KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE + ": ").isEqualTo(KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE);

        int oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        final int maxOldKieModules = KieModuleRepo.MAX_SIZE_GA_CACHE * KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE;
        assertThat(oldKieModulesSize <= maxOldKieModules).as("KieModuleRepo old KieModules map is not limited in it's growth: " + oldKieModulesSize + " > " + maxOldKieModules).isTrue();

        // store
        for( int o = 0; o < 2; ++o ) {
            // loop 2 times in order to trigger storage to oldKieModules
            for( int i = 0; i < releaseIds.length; ++i ) {
                final InternalKieModule kieModule = mock(InternalKieModule.class);
                when(kieModule.getReleaseId()).thenReturn(releaseIds[i]);
                when(kieModule.getCreationTimestamp()).thenReturn(10l);
                kieModuleRepo.store(kieModule);
            }
        }

        numKieModules = countKieModules(kieModuleRepo.kieModules);
        assertThat(numKieModules).as("KieModuleRepo cache should not grow past " + KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE + ": ").isEqualTo(KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE);

        oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        assertThat(oldKieModulesSize <= maxOldKieModules).as("KieModuleRepo old KieModules map is not limited in it's growth: " + oldKieModulesSize + " > " + maxOldKieModules).isTrue();
    }

    @Test
    public void testOldKieModulesLRUCache() throws Exception {
        // setup
        setCacheSize(maxSizeGaCacheField, null, 2);
        setCacheSize(maxSizeGaVersionsCacheField, null, 4);

        final ReleaseIdImpl [] releaseIds = new ReleaseIdImpl[9];
        for( int i = 0; i < releaseIds.length; ++i ) {
            final String artifactId = Character.toString((char)('A'+i/2));
            releaseIds[i] =  new ReleaseIdImpl("org", artifactId, "1." + i);
        }

        // store
        for( int i = 0; i < releaseIds.length; ++i ) {
            final InternalKieModule kieModule = mock(InternalKieModule.class);
            when(kieModule.getReleaseId()).thenReturn(releaseIds[i]);
            when(kieModule.getCreationTimestamp()).thenReturn(10l);
            kieModuleRepo.store(kieModule);
            kieModuleRepo.store(kieModule); // in order to trigger storage to oldKieModules
        }

        int maxSameGAModules = 0;
        int maxGAs = 0;
        for( final Map<ComparableVersion, KieModule> artifactMap : kieModuleRepo.kieModules.values() ) {
            maxGAs++;
            if( artifactMap.size() > maxSameGAModules ) {
                maxSameGAModules = artifactMap.size();
            }
        }

        assertThat(KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE >= maxSameGAModules).as("The maximum of artifacts per GA should not grow past " + KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE + ": "
                + KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE + " < " + maxSameGAModules).isTrue();
        assertThat(KieModuleRepo.MAX_SIZE_GA_CACHE >= maxGAs).as("The number of GAs not grow past " + KieModuleRepo.MAX_SIZE_GA_CACHE + ": "
                + KieModuleRepo.MAX_SIZE_GA_CACHE + " > " + maxGAs).isTrue();

        final int oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        final int maxOldKieModules = KieModuleRepo.MAX_SIZE_GA_CACHE * KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE;
        assertThat(oldKieModulesSize <= maxOldKieModules).as("KieModuleRepo old KieModules map is not limited in it's growth: " + oldKieModulesSize + " > " + maxOldKieModules).isTrue();
    }

}