package org.drools.compiler.kie.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Map;
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

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMUnitConfig(loadDirectory="target/test-classes") // set "debug=true to see debug output
@SuppressWarnings("unchecked")
public class KieModuleRepoTest {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBuilderConfigurationImpl.class);

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

    private static Map<ComparableVersion, KieModule> getArtifactMapFromKieModuleRepoInstance(KieModuleRepo kieModuleRepo, String ga)
                throws Exception {
        Field kieModulesField = KieModuleRepo.class.getDeclaredField("kieModules");
        kieModulesField.setAccessible(true);
        Object kieModulesObj = kieModulesField.get(kieModuleRepo);

        Map<ComparableVersion, KieModule> artifactMap = (Map<ComparableVersion, KieModule>) ((Map) kieModulesObj).get(ga);
        return artifactMap;
    }

    private static Map<ReleaseId, KieModule> getOldKieModulesMapFromKieModuleRepoInstance(KieModuleRepo kieModuleRepo)
            throws Exception {
        Field oldKieModulesField = KieModuleRepo.class.getDeclaredField("oldKieModules");
        oldKieModulesField.setAccessible(true);
        Object kieModulesObj = oldKieModulesField.get(kieModuleRepo);

        return (Map<ReleaseId, KieModule>) kieModulesObj;
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

    private void continueTest() {
       // placeholder for byteman rule to signal other threads
    }

    private static void checkRules() {
       // placeholder for byteman rule to check test
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
        Map<ComparableVersion, KieModule> artifactMap = getArtifactMapFromKieModuleRepoInstance(kieModuleRepo, ga);

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
        Thread.currentThread().setName("test");

        final ReleaseIdImpl releaseId = new ReleaseIdImpl("org", "redeploy", "2.0");
        final KieModule redeployKieModule = mock(KieModule.class);
        when(redeployKieModule.getReleaseId()).thenReturn(releaseId);

        final CountDownLatch initialDeploy = new CountDownLatch(1);

        // 1. initial deploy ("long ago")
        kieModuleRepo.store(redeployKieModule);
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

//        continueTest(); // test development, because there are race conditions in the *test* :/

        redeployThread.setName("store");
        redeployThread.start();

        waitFor(storeAndRemoveThreadsToFinish);
        checkRules();

        String ga = releaseId.getGroupId() + ":" + releaseId.getArtifactId();
        Map<ComparableVersion, KieModule> artifactMap = getArtifactMapFromKieModuleRepoInstance(kieModuleRepo, ga);

        assertNotNull( "Artifact Map for GA '" + ga + "' not in KieModuleRepo!", artifactMap);

        // never gets this far, but this is a good check
        KieModule redployedKieModule = artifactMap.get(new ComparableVersion(releaseId.getVersion()));
        assertNotNull( "Redeployed module has disappeared from KieModuleRepo!", redployedKieModule);
    }

    // 2. duplicate deploy request
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

        Map<ReleaseId, KieModule> oldKieModules = getOldKieModulesMapFromKieModuleRepoInstance(kieModuleRepo);

        // never gets this far, but this is a good check
        KieModule oldKieModule = oldKieModules.get(releaseId);
        long oldKieModuleTimeStamp = ((InternalKieModule) oldKieModule).getCreationTimestamp();
        long originalKieModuleTimestamp = ((InternalKieModule) originalOldKieModule).getCreationTimestamp();
        assertEquals( "The old kie module in the repo is not the originally deployed module!",
                      originalKieModuleTimestamp, oldKieModuleTimeStamp);
    }

}