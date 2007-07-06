package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.Address;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.compiler.PackageBuilder;

import junit.framework.TestCase;

/**
 * This is for testing possible PermSpace issues (leaking) when spawning lots of sessions in concurrent threads.
 * Normally this test will be XXX'ed out, as when running it will not terminate.
 * @author Michael Neale
 */
public class StatelessStressTest extends TestCase {

    public void testDummy() {
        
    }
    
    
    public void XXXtestLotsOfStateless() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "thread_class_test.drl" ) ) );
        assertFalse(builder.hasErrors());

        
        

        final RuleBase rb  = RuleBaseFactory.newRuleBase();
        rb.addPackage( builder.getPackage() );
        
      
        
        int numThreads = 100;
        Thread[] ts = new Thread[numThreads];
        
        
            for (int i=0; i<numThreads; i++) {
                Runnable run = new Runnable() {
    
                    public void run() {
                        
                        long start = 0;
                        long previous = 0;
                        
                        while (true) {
                            start = System.currentTimeMillis();
                            StatelessSession sess = rb.newStatelessSession();
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
