package org.drools.compiler.integrationtests;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.Address;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.StatelessKieSession;

/**
 * This is for testing possible PermSpace issues (leaking) when spawning lots of sessions in concurrent threads.
 * Normally this test will be XXX'ed out, as when running it will not terminate.
 */
public class StatelessStressTest extends CommonTestMethodBase {

    @Test @Ignore
    public void testLotsOfStateless() throws Exception {
        final KieBase kbase = loadKnowledgeBase("thread_class_test.drl");

        int numThreads = 100;
        Thread[] ts = new Thread[numThreads];
        
        
            for (int i=0; i<numThreads; i++) {
                Runnable run = new Runnable() {
    
                    public void run() {
                        
                        long start = 0;
                        long previous = 0;
                        
                        while (true) {
                            start = System.currentTimeMillis();
                            StatelessKieSession sess = kbase.newStatelessKieSession();
                            try {
                                sess    = SerializationHelper.serializeObject(sess);
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                            Person p = new Person();
                            p.setName( "Michael" );
                            Address add1 = new Address();
                            add1.setStreet( "High" );
                            Address add2 = new Address();
                            add2.setStreet( "Low" );
                            List l = new ArrayList();
                            l.add( add1 ); l.add( add2 );
                            p.setAddresses( l );
                            sess.execute( p );
                            
                            long current = System.currentTimeMillis() - start;
                            if (previous != 0) {
                                float f = current/previous;
                                if (f > 1) {
                                    System.err.println("SLOWDOWN");
                                }
                            }
                            
                            previous = current;
                        }
                    }
                    
                };
                
                Thread t = new Thread(run);
                t.start();
                ts[i] = t;
            }
            
            for ( int i = 0; i < ts.length; i++ ) {
                ts[i].join();
            }
        
        
        
        
    }
    
    
}
