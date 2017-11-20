package org.drools.compiler.kie.builder.impl;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.appformer.maven.support.DependencyFilter;
import org.appformer.maven.support.PomModel;
import org.drools.compiler.kie.builder.impl.KieRepositoryImpl.KieModuleRepo;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.common.ResourceProvider;
import org.drools.core.impl.InternalKnowledgeBase;
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
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.ResourceChangeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
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
        setFinalField(maxSizeGaCacheField, null, maxSizeGaCacheOrig);
        setFinalField(maxSizeGaVersionsCacheField, null, maxSizeGaVersionsCacheOrig);
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

    private static KieContainerImpl createMockKieContainer(final ReleaseId projectReleaseId, final KieModuleRepo kieModuleRepo) throws Exception {

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

    private static void setFinalField(final Field field, final Object fieldObject, final Object newValue) throws Exception {
        // make accessible
        field.setAccessible(true);

        // make non-final
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL );

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
        firstThread.setName("normal");
        executor.submit(firstThread);
        secondThread.setName("newFeature");
        executor.submit(secondThread);

        try {
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

        assertNotNull( "Race condition occurred: normal KieModule disappeared from KieModuleRepo!", normalKieModule);
        assertNotNull( "Race condition occurred: new feature KieModule disappeared from KieModuleRepo!", newFeatureKieModule);
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
        // 2. remove and redploy
        executor.submit(removeRunnable);
        executor.submit(redeployRunnable);

        try {
            waitFor(threadsFinishedBarrier);
        } finally {
            executor.shutdownNow();
        }

        final String ga = releaseId.getGroupId() + ":" + releaseId.getArtifactId();
        final Map<ComparableVersion, KieModule> artifactMap = kieModuleRepo.kieModules.get(ga);

        assertNotNull( "Artifact Map for GA '" + ga + "' not in KieModuleRepo!", artifactMap);

        // never gets this far, but this is a good check
        final KieModule redeployedKieModule = artifactMap.get(new ComparableVersion(releaseId.getVersion()));
        assertNotNull( "Redeployed module has disappeared from KieModuleRepo!", redeployedKieModule);
        assertEquals( "Original module retrieved instead of redeployed module!",
                      1l, redeployKieModule.getCreationTimestamp() );
    }

    private static class InternalKieModuleStub implements InternalKieModule {
        @Override
        public void cacheKnowledgeBuilderForKieBase( String kieBaseName, KnowledgeBuilder kbuilder ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.cacheKnowledgeBuilderForKieBase -> TODO" );
        }

        @Override
        public KnowledgeBuilder getKnowledgeBuilderForKieBase( String kieBaseName ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getKnowledgeBuilderForKieBase -> TODO" );
        }

        @Override
        public Collection<KiePackage> getKnowledgePackagesForKieBase( String kieBaseName ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getKnowledgePackagesForKieBase -> TODO" );
        }

        @Override
        public void cacheResultsForKieBase( String kieBaseName, Results results ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.cacheResultsForKieBase -> TODO" );
        }

        @Override
        public Map<String, Results> getKnowledgeResultsCache() {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getKnowledgeResultsCache -> TODO" );
        }

        @Override
        public KieModuleModel getKieModuleModel() {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getKieModuleModel -> TODO" );
        }

        @Override
        public byte[] getBytes() {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getBytes -> TODO" );
        }

        @Override
        public boolean hasResource( String fileName ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.hasResource -> TODO" );
        }

        @Override
        public Resource getResource( String fileName ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getResource -> TODO" );
        }

        @Override
        public ResourceConfiguration getResourceConfiguration( String fileName ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getResourceConfiguration -> TODO" );
        }

        @Override
        public Map<ReleaseId, InternalKieModule> getKieDependencies() {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getKieDependencies -> TODO" );
        }

        @Override
        public void addKieDependency( InternalKieModule dependency ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.addKieDependency -> TODO" );
        }

        @Override
        public Collection<ReleaseId> getJarDependencies( DependencyFilter filter ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getJarDependencies -> TODO" );
        }

        @Override
        public Collection<ReleaseId> getUnresolvedDependencies() {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getUnresolvedDependencies -> TODO" );
        }

        @Override
        public void setUnresolvedDependencies( Collection<ReleaseId> unresolvedDependencies ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.setUnresolvedDependencies -> TODO" );
        }

        @Override
        public boolean isAvailable( String pResourceName ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.isAvailable -> TODO" );
        }

        @Override
        public byte[] getBytes( String pResourceName ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getBytes -> TODO" );
        }

        @Override
        public Collection<String> getFileNames() {
            return Collections.emptyList();
        }

        @Override
        public File getFile() {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getFile -> TODO" );
        }

        @Override
        public ResourceProvider createResourceProvider() {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.createResourceProvider -> TODO" );
        }

        @Override
        public Map<String, byte[]> getClassesMap( boolean includeTypeDeclarations ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getClassesMap -> TODO" );
        }

        @Override
        public boolean addResourceToCompiler( CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.addResourceToCompiler -> TODO" );
        }

        @Override
        public boolean addResourceToCompiler( CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName, ResourceChangeSet rcs ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.addResourceToCompiler -> TODO" );
        }

        @Override
        public long getCreationTimestamp() {
            return 0L;
        }

        @Override
        public InputStream getPomAsStream() {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getPomAsStream -> TODO" );
        }

        @Override
        public PomModel getPomModel() {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getPomModel -> TODO" );
        }

        @Override
        public KnowledgeBuilderConfiguration getBuilderConfiguration( KieBaseModel kBaseModel ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getBuilderConfiguration -> TODO" );
        }

        @Override
        public InternalKnowledgeBase createKieBase( KieBaseModelImpl kBaseModel, KieProject kieProject, ResultsImpl messages, KieBaseConfiguration conf ) {
            throw new UnsupportedOperationException( "org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.createKieBase -> TODO" );
        }

        @Override
        public ReleaseId getReleaseId() {
            return new ReleaseIdImpl("org", "deployTwiceAfterUpdateDependency", "1.0-SNAPSHOT");
        }
    }

    // 2. simultaneous deploy requests
    // - multitenant UI
    // - duplicate REST requests
    @Test(timeout=5000)
    public void newerVersionDeployOverwritesTest() throws Exception {

        // setup
        final ReleaseIdImpl releaseId = new ReleaseIdImpl("org", "deployTwiceAfterUpdateDependency", "1.0-SNAPSHOT");
        final InternalKieModule originalOldKieModule = new InternalKieModuleStub();

        final ReleaseId dependentReleaseid = new ReleaseIdImpl("org", "deployTwiceAfterUpdate", "1.0-SNAPSHOT");
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
        // 2. remove and redploy
        final Thread deployThread = new Thread(deployRunnable);
        deployThread.setName("one");
        executor.submit(deployThread);

        final Thread secondDeployThread = new Thread(deployRunnable);
        secondDeployThread.setName("two");
        executor.submit(secondDeployThread);

        try {
            waitFor(threadsFinishedBarrier);
        } finally {
            executor.shutdownNow();
        }

        // never gets this far, but this is a good check
        final KieModule oldKieModule = kieModuleRepo.oldKieModules.get(releaseId);
        final long oldKieModuleTimeStamp = ((InternalKieModule) oldKieModule).getCreationTimestamp();
        final long originalKieModuleTimestamp = originalOldKieModule.getCreationTimestamp();
        assertEquals( "The old kie module in the repo is not the originally deployed module!",
                      originalKieModuleTimestamp, oldKieModuleTimeStamp);
    }

    @Test
    public void storingNewProjectsCausesOldProjectEvictionFromKieModuleRepoTest() throws Exception {
        // setup
        setFinalField(maxSizeGaCacheField, null, 3);
        setFinalField(maxSizeGaVersionsCacheField, null, 2); // to test oldKieModules caching

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
        assertEquals( "KieModuleRepo cache should not grow past " + KieModuleRepo.MAX_SIZE_GA_CACHE + ": ",
                      KieModuleRepo.MAX_SIZE_GA_CACHE, numKieModules );

        final int oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        final int max = KieModuleRepo.MAX_SIZE_GA_CACHE * KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE;
        assertTrue( "KieModuleRepot old KieModules map is not limited in it's growth: " + oldKieModulesSize + " > " + max,
                     oldKieModulesSize <= max );
    }

    @Test
    public void storingNewProjectVersionsCausesOldVersionEvictionFromKieModuleRepoTest() throws Exception {
        // setup
        setFinalField(maxSizeGaCacheField, null, 2); // to test oldKieModules caching
        setFinalField(maxSizeGaVersionsCacheField, null, 3);

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
        assertEquals( "KieModuleRepo cache should not grow past " + KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE + ": ",
                      KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE, numKieModules );

        int oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        final int maxOldKieModules = KieModuleRepo.MAX_SIZE_GA_CACHE * KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE;
        assertTrue( "KieModuleRepo old KieModules map is not limited in it's growth: " + oldKieModulesSize + " > " + maxOldKieModules,
                     oldKieModulesSize <= maxOldKieModules );

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
        assertEquals( "KieModuleRepo cache should not grow past " + KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE + ": ",
                      KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE, numKieModules );

        oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        assertTrue( "KieModuleRepo old KieModules map is not limited in it's growth: " + oldKieModulesSize + " > " + maxOldKieModules,
                     oldKieModulesSize <= maxOldKieModules );
    }

    @Test
    public void testOldKieModulesLRUCache() throws Exception {
        // setup
        setFinalField(maxSizeGaCacheField, null, 2);
        setFinalField(maxSizeGaVersionsCacheField, null, 4);

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

        assertTrue( "The maximum of artifacts per GA should not grow past " + KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE + ": "
                    + KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE + " < " + maxSameGAModules,
                      KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE >= maxSameGAModules );
        assertTrue( "The number of GAs not grow past " + KieModuleRepo.MAX_SIZE_GA_CACHE + ": "
                    + KieModuleRepo.MAX_SIZE_GA_CACHE + " > " + maxGAs,
                      KieModuleRepo.MAX_SIZE_GA_CACHE >= maxGAs );

        final int oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        final int maxOldKieModules = KieModuleRepo.MAX_SIZE_GA_CACHE * KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE;
        assertTrue( "KieModuleRepo old KieModules map is not limited in it's growth: " + oldKieModulesSize + " > " + maxOldKieModules,
                     oldKieModulesSize <= maxOldKieModules );
    }

}