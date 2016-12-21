package org.drools.testcoverage.regression;

import org.assertj.core.api.Assertions;
import org.drools.core.time.SessionPseudoClock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Reproducer for BZ 1181584, by Mike Wilson.
 */
public class DroolsGcCausesNPETest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsGcCausesNPETest.class);

    private static final String KIE_BASE_NAME = "defaultBase";
    private static final String DRL_FILE_NAME = "DroolsGcCausesNPE.drl";

    private static final KieServices services = KieServices.Factory.get();

    private static final ReleaseId releaseId = services.newReleaseId(
            "org.jboss.qa.brms.bre", "drools-gc-causes-npe-example", "1.0");

    private KieSession session;
    private SessionPseudoClock clock;
    private FactType eventFactType;

    @BeforeClass
    public static void beforeClass() throws Exception {

        KieModuleModel module = services.newKieModuleModel();
        KieBaseModel base = module.newKieBaseModel(KIE_BASE_NAME);
        base.setEventProcessingMode(EventProcessingOption.STREAM);

        KieFileSystem fs = services.newKieFileSystem();
        fs.generateAndWritePomXML(releaseId);
        fs.write(services.getResources()
                .newClassPathResource(DRL_FILE_NAME, DroolsGcCausesNPETest.class));
        fs.writeKModuleXML(module.toXML());

        KieBuilder builder = services.newKieBuilder(fs);
        List<Message> errors = builder.buildAll().getResults()
                .getMessages(Message.Level.ERROR);
        if (errors.size() > 0) {
            Assertions.fail("unexpected errors building drl: " + errors);
        }

        services.getRepository().addKieModule(builder.getKieModule());
    }

    @Before
    public void setUp() throws Exception {
        KieSessionConfiguration conf = services.newKieSessionConfiguration();
        conf.setOption(ClockTypeOption.get("pseudo"));
        conf.setProperty("type", "stateful");
        KieContainer container = services.newKieContainer(releaseId);
        session = container.getKieBase(KIE_BASE_NAME).newKieSession(conf,
                services.newEnvironment());
        clock = session.getSessionClock();
        eventFactType = session.getKieBase().getFactType(this.getClass().getPackage().getName(), "Event");
    }

    /**
     * The original test method reproducing NPE during event GC.
     */
    @Test
    public void testBZ1181584() throws Exception {
        Random r = new Random(1);

        int i = 0;

        try {
            for (; i < 1000000; i++) {
                insert(createEvent(1));
                advanceTime(r.nextInt(4000));
            }
        } catch (NullPointerException e) {
            LOGGER.warn("failed at i = " + i);
            LOGGER.warn("fact count: " + session.getFactCount());
            logActiveFacts();
            Assertions.fail("NPE thrown - consider reopening BZ 1181584", e);
        }
    }

    /**
     * Deterministic variant of the previous test method that reliably illustrates BZ 1274696.
     */
    @Test
    public void testBZ1274696() throws Exception {
        insert(createEvent(1));
        advanceTime(4000);
    }

    private Object createEvent(long id) throws InstantiationException,
            IllegalAccessException {
        Object event = eventFactType.newInstance();
        eventFactType.set(event, "id", id);
        return event;

    }

    private void advanceTime(long millis) {
        clock.advanceTime(millis, TimeUnit.MILLISECONDS);
        session.fireAllRules();
    }

    private void insert(Object event) {
        session.insert(event);
        session.fireAllRules();
    }

    private void logActiveFacts() {
        LOGGER.warn("facts: ");
        for (FactHandle handle : session.getFactHandles()) {
            LOGGER.warn(handle.toString());
        }
    }
}
