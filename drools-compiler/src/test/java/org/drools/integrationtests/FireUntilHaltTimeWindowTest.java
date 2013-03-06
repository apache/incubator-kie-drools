package org.drools.integrationtests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.drools.ClockType;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.type.FactType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests behavior of Drools Fusion when several events are being
 * fed into the engine by thread A while the engine had been started by
 * fireUntilHalt by thread B.
 */
public class FireUntilHaltTimeWindowTest {

    private static final String DRL_FILE_NAME = "test_CEP_fireUntilHaltTimeWindow.drl";
            
    private StatefulKnowledgeSession statefulSession;
    
    private StockFactory stockFactory;

    @Before
    public void setUp() {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(DRL_FILE_NAME, this.getClass()),
                ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            System.err.println(kbuilder.getErrors().toString());
        }
        final KnowledgeBaseConfiguration config =
                KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(config);       
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        
        final KnowledgeSessionConfiguration sessionConfig =
                KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption(ClockTypeOption.get(ClockType.PSEUDO_CLOCK.getId()));

        this.statefulSession = kbase.newStatefulKnowledgeSession(sessionConfig, null);
        this.stockFactory = new StockFactory(kbase);
    }

    @After
    public void cleanUp() {
        if (this.statefulSession != null) {
            this.statefulSession.dispose();
        }
    }
    
    /**
     * Inserts several events into a time window with advancing pseudo-clock time
     * before each insertion of an event.
     * <p/>
     * The engine runs in fireUntilHalt mode started in a separate thread.
     */
    @Test
    public void testFireUntilHaltWithTimeWindow() throws Exception {
        // thread for firing until halt
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future sessionFuture = executor.submit(new Runnable() {

            @Override
            public void run() {
                statefulSession.fireUntilHalt();
            }
        });
        
        try {
            for (int iteration = 0; iteration < 10000; iteration++) {
                this.populateSessionWithStocks();
            }
            // let the engine finish its job
            Thread.sleep(5000);
            
        } finally {
            statefulSession.halt();
            // not to swallow the exception
            sessionFuture.get();
        }       
    }
    
    private void populateSessionWithStocks() {
        final SessionPseudoClock clock = statefulSession.getSessionClock();

        clock.advanceTime(1, TimeUnit.SECONDS);
        statefulSession.insert(stockFactory.createStock("ST1", 0d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        statefulSession.insert(stockFactory.createStock("ST2", 1d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        statefulSession.insert(stockFactory.createStock("ST3", 0d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        statefulSession.insert(stockFactory.createStock("ST4", 0d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        statefulSession.insert(stockFactory.createStock("ST5", 0d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        statefulSession.insert(stockFactory.createStock("ST6", 1d));
    }
    
    /**
     * Factory creating events used in the test.
     */
    private static class StockFactory {

        private static final String DRL_PACKAGE_NAME = "org.drools.integrationtests";

        private static final String DRL_FACT_NAME = "Stock";
        
        private final KnowledgeBase kbase;
        
        public StockFactory(final KnowledgeBase kbase) {
            this.kbase = kbase;
        }
        
        public Object createStock(final String name, final Double value) {
            try {
                return this.createDRLStock(name, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to create Stock instance defined in DRL", e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Unable to create Stock instance defined in DRL", e);
            }
        }

        private Object createDRLStock(final String name, final Double value)
                throws IllegalAccessException, InstantiationException {

            final FactType stockType = kbase.getFactType(DRL_PACKAGE_NAME, DRL_FACT_NAME);

            final Object stock = stockType.newInstance();
            stockType.set(stock, "name", name);
            stockType.set(stock, "value", value);

            return stock;
        }
    }    
}
