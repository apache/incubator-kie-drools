package org.drools.compiler.integrationtests;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;

/**
 * Tests working with KieSession which is constructed either from a KieContainer,
 * or directly from a KieBase.
 */
public class KieSessionConstructionTest {

    private static final String KBASE_NAME = "defaultKieBase";
    private static final String PACKAGE = "org.drools.compiler.integrationtests";

    private KieBase kieBase;

    private KieSession kieSessionFromContainer;
    private KieSession kieSessionFromKieBase;

    @Before
    public void initSession() {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        final Resource drl = ks.getResources().newClassPathResource(
                "kie_session_construction.drl", this.getClass());
        kfs.write("src/main/resources/kie_session_construction.drl", drl);

        KieBuilder builder = ks.newKieBuilder(kfs).buildAll();
        assertEquals(0, builder.getResults().getMessages().size());

        this.kieBase = ks.newKieContainer(ks.getRepository()
                .getDefaultReleaseId()).getKieBase(KBASE_NAME);

        this.kieSessionFromContainer = ks.newKieContainer(ks.getRepository()
                .getDefaultReleaseId()).newKieSession();
        
        this.kieSessionFromKieBase = this.kieBase.newKieSession();        
    }

    @After
    public void cleanup() {
        if (this.kieSessionFromContainer != null) {
            this.kieSessionFromContainer.dispose();
        }
        if (this.kieSessionFromKieBase != null) {
            this.kieSessionFromKieBase.dispose();
        }        
    }

    /**
     * Tests querying the working memory for facts with KieSession created from 
     * a KieContainer.
     */
    @Test
    @Ignore
    public void testBasicQuerySessionFromContainer() throws Exception {
        this.testBasicQueryWithSession(kieSessionFromContainer);
    }

    /**
     * Tests querying the working memory for facts with KieSession created from 
     * a KieBase.
     */
    @Test
    public void testBasicQuerySessionFromKieBase() throws Exception {
        this.testBasicQueryWithSession(kieSessionFromKieBase);
    }
    
    private void testBasicQueryWithSession(KieSession kieSession) throws Exception {
        // working memory objects list
        List<Command<?>> commands = new ArrayList<Command<?>>();

        ListHolder listHolder = new ListHolder();
        kieSession.setGlobal("listHolder", listHolder);

        insertInstances(commands, kieBase);

        // we are in the office
        FactType hereType = kieBase.getFactType(PACKAGE, "Here");
        assertNotNull(hereType);
        Object here = hereType.newInstance();
        hereType.set(here, "place", "office");
        commands.add(getCommands().newInsert(here));

        // fire all rules
        commands.add(getCommands().newFireAllRules());
        kieSession.execute(getCommands().newBatchExecution(commands, "default-stateful"));

        // check thing list in the office
        List<String> things = listHolder.getThings();
        assertEquals(1, things.size());
        assertTrue(things.contains("desk"));        
    }

    private void insertInstances(List<Command<?>> commands, KieBase kbase) throws Exception {
        FactType locationType = kbase.getFactType(PACKAGE, "Location");

        // a desk is in the office
        Object desk = locationType.newInstance();
        locationType.set(desk, "thing", "desk");
        locationType.set(desk, "location", "office");
        commands.add(getCommands().newInsert(desk));
    }

    private KieCommands getCommands() {
        return KieServices.Factory.get().getCommands();
    }

    /**
     * Class storing results from the working memory.
     */
    public static class ListHolder implements Serializable {

        private static final long serialVersionUID = -3058814255413392428L;
        private List<String> things;

        public ListHolder() {
            this.things = new ArrayList<String>();
        }

        public void setThings(List<String> things) {
            this.things = things;
        }

        public List<String> getThings() {
            return things;
        }
    }    
}
