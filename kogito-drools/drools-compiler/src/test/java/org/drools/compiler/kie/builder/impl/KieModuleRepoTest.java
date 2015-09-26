package org.drools.compiler.kie.builder.impl;

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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.kie.builder.impl.KieRepositoryImpl.ComparableVersion;
import org.drools.compiler.kie.builder.impl.KieRepositoryImpl.KieModuleRepo;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.core.common.ResourceProvider;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test contains
 * - byteman tests (for concurrency related issues)
 * - normal tests that test for memory leaks (that the KieModuleRepo
 * functions as a LRU cache, and evicts old {@link KieModule} instances )
 */
@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMUnitConfig(loadDirectory="target/test-classes") // set "debug=true to see debug output
@SuppressWarnings("unchecked")
public class KieModuleRepoTest {

    private static final Logger logger = LoggerFactory.getLogger(KieModuleRepoTest.class);

    private KieModuleRepo kieModuleRepo;

    @Before
    public void before() {

        InternalKieScanner mockKieScanner = mock(InternalKieScanner.class);
        kieModuleRepo = new KieModuleRepo(mockKieScanner);
    }

    /**
     * HELPER METHODS -------------------------------------------------------------------------------------------------------------
     */

    protected static void waitFor(CyclicBarrier barrier) {
        String threadName = Thread.currentThread().getName();
        try {
            barrier.await();
        } catch( InterruptedException e ) {
            fail( "Thread '" + threadName + "' was interrupted while waiting for the other threads!");
        } catch( BrokenBarrierException e ) {
            fail( "Thread '" + threadName + "' barrier was broken while waiting for the other threads!");
        }
    }

    protected static void waitFor(CountDownLatch countDownLatch) {
        String threadName = Thread.currentThread().getName();
        try {
            countDownLatch.await();
        } catch( InterruptedException e ) {
            fail( "Thread '" + threadName + "' was interrupted while waiting for the count down!");
        }
    }

    private static KieContainerImpl createMockKieContainer(ReleaseId projectReleaseId, KieModuleRepo kieModuleRepo) throws Exception {

        // kie module
        InternalKieModule mockKieProjectKieModule = mock(InternalKieModule.class);
        ResourceProvider mockKieProjectKieModuleResourceProvider = mock(ResourceProvider.class);
        when(mockKieProjectKieModule.createResourceProvider()).thenReturn(mockKieProjectKieModuleResourceProvider);

        // kie project
        KieModuleKieProject kieProject = new KieModuleKieProject(mockKieProjectKieModule);
        KieModuleKieProject mockKieProject = spy(kieProject);
        doNothing().when(mockKieProject).init();
        doReturn(projectReleaseId).when(mockKieProject).getGAV();
        doNothing().when(mockKieProject).updateToModule(any(InternalKieModule.class));

        // kie repository
        KieRepository kieRepository = new KieRepositoryImpl();
        Field kieModuleRepoField = KieRepositoryImpl.class.getDeclaredField("kieModuleRepo");
        kieModuleRepoField.setAccessible(true);
        kieModuleRepoField.set(kieRepository, kieModuleRepo);
        kieModuleRepoField.setAccessible(false);

        // kie container
        KieContainerImpl kieContainerImpl = new KieContainerImpl(mockKieProject, kieRepository);
        return kieContainerImpl;
    }

    private static void checkRules() {
       // placeholder for byteman rule to check test
    }

    private static int countKieModules( Map<String, NavigableMap<ComparableVersion, KieModule>> kieModulesCache ) {
       int numKieModules = 0;
       for( NavigableMap<ComparableVersion, KieModule> map : kieModulesCache.values() ) {
           numKieModules += map.size();
       }
       return numKieModules;
    }

    private static void setFinalField(Field field, Object fieldObject, Object newValue) throws Exception {
        // make accessible
        field.setAccessible(true);

        // make non-final
        Field modifiersField = Field.class.getDeclaredField("modifiers");
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
    @BMScript(value="byteman/setArtifactMapTest.btm")
    public void setArtifactMapTest() throws Exception {
        Thread.currentThread().setName("test");

        final String groupId = "org";
        final String artifactId = "one";
        final String firstVersion = "1.0";
        final String secondVersion = "1.0-NEW-FEATURE";

        final CyclicBarrier threadsFinishedBarrier = new CyclicBarrier(3);

        Thread firstThread = new Thread(new Runnable() {

            private final KieModuleRepo kieModuleRepo = KieModuleRepoTest.this.kieModuleRepo;

            @Override
            public void run() {
                ReleaseIdImpl firstReleaseId = new ReleaseIdImpl(groupId, artifactId, firstVersion);
                KieModule firstKieModule = mock(KieModule.class);
                when(firstKieModule.getReleaseId()).thenReturn(firstReleaseId);
                kieModuleRepo.store(firstKieModule);

                waitFor(threadsFinishedBarrier);
            }
        });

        Thread secondThread = new Thread(new Runnable() {

            private final KieModuleRepo kieModuleRepo = KieModuleRepoTest.this.kieModuleRepo;

            @Override
            public void run() {
                ReleaseIdImpl secondReleaseId = new ReleaseIdImpl(groupId, artifactId, secondVersion);
                KieModule secondKieModule = mock(KieModule.class);
                when(secondKieModule.getReleaseId()).thenReturn(secondReleaseId);
                kieModuleRepo.store(secondKieModule);

                waitFor(threadsFinishedBarrier);
            }
        });

        firstThread.setName("normal");
        firstThread.start();
        secondThread.setName("newFeature");
        secondThread.start();

        waitFor(threadsFinishedBarrier);
        checkRules();

        String ga = groupId + ":" + artifactId;
        Map<ComparableVersion, KieModule> artifactMap = kieModuleRepo.kieModules.get(ga);

        ComparableVersion normalVersion = new ComparableVersion(firstVersion);
        KieModule normalKieModule = artifactMap.get(normalVersion);
        ComparableVersion newFeatureVersion = new ComparableVersion(secondVersion);
        KieModule newFeatureKieModule = artifactMap.get(newFeatureVersion);

        assertNotNull( "Race condition occurred: normal KieModule disappeared from KieModuleRepo!", normalKieModule);
        assertNotNull( "Race condition occurred: new feature KieModule disappeared from KieModuleRepo!", newFeatureKieModule);
    }

    // remove request followed by a store request on a high load system
    // * remove does not completely finish before store starts
    @Test(timeout=5000)
    @BMScript(value="byteman/removeStoreArtifactMapTest.btm")
    public void removeStoreArtifactMapTest() throws Exception {
        // actual test
        Thread.currentThread().setName("test");

        final ReleaseIdImpl releaseId = new ReleaseIdImpl("org", "redeploy", "2.0");
        final InternalKieModule originalKieModule = mock(InternalKieModule.class);
        when(originalKieModule.getReleaseId()).thenReturn(releaseId);
        when(originalKieModule.getCreationTimestamp()).thenReturn(0l);

        final InternalKieModule redeployKieModule = mock(InternalKieModule.class);
        when(redeployKieModule.getReleaseId()).thenReturn(releaseId);
        when(redeployKieModule.getCreationTimestamp()).thenReturn(1l);

        final CountDownLatch initialDeploy = new CountDownLatch(1);

        // 1. initial deploy ("long ago")
        kieModuleRepo.store(originalKieModule);
        initialDeploy.countDown();

        final CyclicBarrier storeAndRemoveThreadsToFinish = new CyclicBarrier(3);

        Thread removeThread = new Thread(new Runnable() {

            private final KieModuleRepo kieModuleRepo = KieModuleRepoTest.this.kieModuleRepo;

            @Override
            public void run() {
                waitFor(initialDeploy);

                kieModuleRepo.remove(releaseId);

                waitFor(storeAndRemoveThreadsToFinish);
            }
        });

        Thread redeployThread = new Thread(new Runnable() {

            private final KieModuleRepo kieModuleRepo = KieModuleRepoTest.this.kieModuleRepo;

            @Override
            public void run() {
                waitFor(initialDeploy);

                kieModuleRepo.store(redeployKieModule);

                waitFor(storeAndRemoveThreadsToFinish);
            }
        });

        // 2. remove and redploy
        removeThread.setName("remove");
        removeThread.start();

        redeployThread.setName("store");
        redeployThread.start();

        waitFor(storeAndRemoveThreadsToFinish);
        checkRules();

        String ga = releaseId.getGroupId() + ":" + releaseId.getArtifactId();
        Map<ComparableVersion, KieModule> artifactMap = kieModuleRepo.kieModules.get(ga);

        assertNotNull( "Artifact Map for GA '" + ga + "' not in KieModuleRepo!", artifactMap);

        // never gets this far, but this is a good check
        KieModule redployedKieModule = artifactMap.get(new ComparableVersion(releaseId.getVersion()));
        assertNotNull( "Redeployed module has disappeared from KieModuleRepo!", redployedKieModule);
        assertEquals( "Original module retrieved instead of redeployed module!",
                      1l, ((InternalKieModule) redeployKieModule).getCreationTimestamp() );
    }

    // 2. simultaneous deploy requests
    // - multitenant UI
    // - duplicate REST requests
    @Test(timeout=5000)
    @BMScript(value="byteman/newerVersionDeployOverwritesTest.btm")
    public void newerVersionDeployOverwritesTest() throws Exception {
        Thread.currentThread().setName("test");

        // setup
        ReleaseIdImpl releaseId = new ReleaseIdImpl("org", "deployTwiceAfterUpdateDependency", "1.0-SNAPSHOT");
        InternalKieModule originalOldKieModule = mock(InternalKieModule.class);
        when(originalOldKieModule.getReleaseId()).thenReturn(releaseId);
        when(originalOldKieModule.getCreationTimestamp()).thenReturn(0l);

        ReleaseId dependentReleaseid = new ReleaseIdImpl("org", "deployTwiceAfterUpdate", "1.0-SNAPSHOT");
        KieContainerImpl kieContainer = createMockKieContainer(dependentReleaseid, kieModuleRepo);

        // test
        final CountDownLatch initialDeployAndUpdateDependencyToFinish = new CountDownLatch(1);

        // 1. deploy
        kieModuleRepo.store(originalOldKieModule);

        // 2. another project is dependent on this project
        kieContainer.updateDependencyToVersion(releaseId, releaseId);

        // once above is done, let the threads below proceed
        initialDeployAndUpdateDependencyToFinish.countDown();

        final CyclicBarrier bothStoreThreadsToFinish = new CyclicBarrier(3);

        final InternalKieModule newKieModule = mock(InternalKieModule.class);
        when(newKieModule.getReleaseId()).thenReturn(releaseId);
        when(newKieModule.getCreationTimestamp()).thenReturn(10l);

        Runnable deployRunnable = new Runnable() {

            private final KieModuleRepo kieModuleRepo = KieModuleRepoTest.this.kieModuleRepo;

            @Override
            public void run() {
                waitFor(initialDeployAndUpdateDependencyToFinish);

                kieModuleRepo.store(newKieModule);

                waitFor(bothStoreThreadsToFinish);
            }
        };

        Thread deployThread = new Thread(deployRunnable);
        Thread secondDeployThread = new Thread(deployRunnable);

        // 2. remove and redploy
        deployThread.setName("one");
        deployThread.start();

        secondDeployThread.setName("two");
        secondDeployThread.start();

        waitFor(bothStoreThreadsToFinish);
        checkRules();

        // never gets this far, but this is a good check
        KieModule oldKieModule = kieModuleRepo.oldKieModules.get(releaseId);
        long oldKieModuleTimeStamp = ((InternalKieModule) oldKieModule).getCreationTimestamp();
        long originalKieModuleTimestamp = ((InternalKieModule) originalOldKieModule).getCreationTimestamp();
        assertEquals( "The old kie module in the repo is not the originally deployed module!",
                      originalKieModuleTimestamp, oldKieModuleTimeStamp);
    }

    @Test
    public void storingNewProjectsCausesOldProjectEvictionFromKieModuleRepoTest() throws Exception {
        // setup
        Field maxSizeGaCacheField = KieModuleRepo.class.getDeclaredField("MAX_SIZE_GA_CACHE");
        setFinalField(maxSizeGaCacheField, null, 3);
        Field maxSizeGaVersionsCacheField = KieModuleRepo.class.getDeclaredField("MAX_SIZE_GA_VERSIONS_CACHE");
        setFinalField(maxSizeGaVersionsCacheField, null, 2); // to test oldKieModules caching

        ReleaseIdImpl [] releaseIds = new ReleaseIdImpl[7];
        for( int i = 0; i < releaseIds.length; ++i ) {
            String artifactId = Character.toString((char)('A'+i));
            releaseIds[i] =  new ReleaseIdImpl("org", artifactId, "1.0");
        }

        // store
        for( int i = 0; i < releaseIds.length; ++i ) {
            InternalKieModule kieModule = mock(InternalKieModule.class);
            when(kieModule.getReleaseId()).thenReturn(releaseIds[i]);
            when(kieModule.getCreationTimestamp()).thenReturn(10l);
            kieModuleRepo.store(kieModule);
            kieModuleRepo.store(kieModule); // store module 2 times to trigger storage to oldKieModules
        }

        int numKieModules = countKieModules(kieModuleRepo.kieModules);
        assertEquals( "KieModuleRepo cache should not grow past " + KieModuleRepo.MAX_SIZE_GA_CACHE + ": ",
                      KieModuleRepo.MAX_SIZE_GA_CACHE, numKieModules );

        int oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        int max = KieModuleRepo.MAX_SIZE_GA_CACHE * KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE;
        assertTrue( "KieModuleRepot old KieModules map is not limited in it's growth: " + oldKieModulesSize + " > " + max,
                     oldKieModulesSize <= max );
    }

    @Test
    public void storingNewProjectVersionsCausesOldVersionEvictionFromKieModuleRepoTest() throws Exception {
        // setup
        Field maxSizeGaCacheField = KieModuleRepo.class.getDeclaredField("MAX_SIZE_GA_CACHE");
        setFinalField(maxSizeGaCacheField, null, 2); // to test oldKieModules caching
        Field maxSizeGaVersionsCacheField = KieModuleRepo.class.getDeclaredField("MAX_SIZE_GA_VERSIONS_CACHE");
        setFinalField(maxSizeGaVersionsCacheField, null, 3);;

        ReleaseIdImpl [] releaseIds = new ReleaseIdImpl[7];
        for( int i = 0; i < releaseIds.length; ++i ) {
            releaseIds[i] =  new ReleaseIdImpl("org", "test", "1." + i);
        }

        // store
        for( int i = 0; i < releaseIds.length; ++i ) {
            InternalKieModule kieModule = mock(InternalKieModule.class);
            when(kieModule.getReleaseId()).thenReturn(releaseIds[i]);
            when(kieModule.getCreationTimestamp()).thenReturn(10l);
            kieModuleRepo.store(kieModule);
            kieModuleRepo.store(kieModule); // in order to trigger storage to oldKieModules
        }

        int numKieModules = countKieModules(kieModuleRepo.kieModules);
        assertEquals( "KieModuleRepo cache should not grow past " + KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE + ": ",
                      KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE, numKieModules );

        int oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        int maxOldKieModules = KieModuleRepo.MAX_SIZE_GA_CACHE * KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE;
        assertTrue( "KieModuleRepo old KieModules map is not limited in it's growth: " + oldKieModulesSize + " > " + maxOldKieModules,
                     oldKieModulesSize <= maxOldKieModules );

        // store
        for( int o = 0; o < 2; ++o ) {
            // loop 2 times in order to trigger storage to oldKieModules
            for( int i = 0; i < releaseIds.length; ++i ) {
                InternalKieModule kieModule = mock(InternalKieModule.class);
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
        Field maxSizeGaCacheField = KieModuleRepo.class.getDeclaredField("MAX_SIZE_GA_CACHE");
        setFinalField(maxSizeGaCacheField, null, 2);
        Field maxSizeGaVersionsCacheField = KieModuleRepo.class.getDeclaredField("MAX_SIZE_GA_VERSIONS_CACHE");
        setFinalField(maxSizeGaVersionsCacheField, null, 4);

        ReleaseIdImpl [] releaseIds = new ReleaseIdImpl[9];
        for( int i = 0; i < releaseIds.length; ++i ) {
            String artifactId = Character.toString((char)('A'+i/2));
            releaseIds[i] =  new ReleaseIdImpl("org", artifactId, "1." + i);
        }

        // store
        for( int i = 0; i < releaseIds.length; ++i ) {
            InternalKieModule kieModule = mock(InternalKieModule.class);
            when(kieModule.getReleaseId()).thenReturn(releaseIds[i]);
            when(kieModule.getCreationTimestamp()).thenReturn(10l);
            kieModuleRepo.store(kieModule);
            kieModuleRepo.store(kieModule); // in order to trigger storage to oldKieModules
        }

        int maxSameGAModules = 0;
        int maxGAs = 0;
        for( Map<ComparableVersion, KieModule> artifactMap : kieModuleRepo.kieModules.values() ) {
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

        int oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        int maxOldKieModules = KieModuleRepo.MAX_SIZE_GA_CACHE * KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE;
        assertTrue( "KieModuleRepo old KieModules map is not limited in it's growth: " + oldKieModulesSize + " > " + maxOldKieModules,
                     oldKieModulesSize <= maxOldKieModules );
    }

}