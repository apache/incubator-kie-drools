package org.drools.agent;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.drools.RuleBase;
import org.drools.rule.Package;

import junit.framework.TestCase;

public class RuleBaseAgentTest extends TestCase {

    public void testLists() {
        String s = "\tfoo.bar\n baz.bar\t whee ";
        List result = RuleBaseAgent.list( s );
        assertEquals(3, result.size());
        assertEquals("foo.bar", result.get( 0 ));
        assertEquals("baz.bar", result.get(1));
        assertEquals("whee", result.get(2));
        
        s = null;
        result = RuleBaseAgent.list( s );
        assertNotNull(result);
        assertEquals(0, result.size());
    }
    
    public void testFiles() throws Exception {        
        File dir = RuleBaseAssemblerTest.getTempDirectory();
        
        Package p1 = new Package("p1");
        File p1f = new File(dir, "p1.pkg");
        RuleBaseAssemblerTest.writePackage( p1, p1f );

        Package p2 = new Package("p2");
        File p2f = new File(dir, "p2.pkg");
        RuleBaseAssemblerTest.writePackage( p2, p2f );
        
        String path = dir.getPath() + "/" + "p1.pkg " + dir.getPath() + "/" + "p2.pkg";
        
        Properties props = new Properties();
        props.setProperty( "file", path );
        RuleBaseAgent ag = new RuleBaseAgent(props);
        RuleBase rb = ag.getRuleBase();
        assertNotNull(rb);
        assertEquals(2, rb.getPackages().length);
        
        assertFalse(ag.isPolling());
        
        props.setProperty( "poll", "1" );
        ag = new RuleBaseAgent(props);
        assertTrue(ag.isPolling());
        
        ag.stopPolling();
        assertFalse(ag.isPolling());
        
    }
    
    public void testPollingFilesRuleBaseUpdate() throws Exception {
        //RuleBaseAssemblerTest.clearTempDirectory();
        File dir = RuleBaseAssemblerTest.getTempDirectory();
        
        Package p1 = new Package("p1");
        File p1f = new File(dir, "p42_.pkg");
        RuleBaseAssemblerTest.writePackage( p1, p1f );
        
        String path = dir.getPath() + "/" + "p42_.pkg";
        
        Properties props = new Properties();
        props.setProperty( "file", path );
        props.setProperty( "poll", "1" );
        RuleBaseAgent ag = new RuleBaseAgent(props);
        
        assertTrue(ag.isPolling());
        RuleBase rb = ag.getRuleBase();
        assertEquals(1, rb.getPackages().length);        
        assertEquals(0, rb.getPackages()[0].getGlobals().size());
        
        p1.addGlobal( "goo", String.class );
        
        RuleBaseAssemblerTest.writePackage( p1, p1f );
        
        RuleBase rb_ = ag.getRuleBase();
        assertSame(rb, rb_);
        assertEquals(1, rb.getPackages().length);        
        assertEquals(0, rb.getPackages()[0].getGlobals().size());
        
        Thread.sleep( 2000 );
        
        RuleBase rb2 = ag.getRuleBase();
        assertSame(rb, rb2);
        
        assertEquals(1, rb2.getPackages().length);
        assertEquals(1, rb2.getPackages()[0].getGlobals().size());
        
        //now check subsequent changes
        p1.addGlobal( "goo2", String.class );
        System.err.println("-->WRITING CHANGE");
        RuleBaseAssemblerTest.writePackage( p1, p1f );
        System.err.println("-->WROTE CHANGE");
        RuleBase rb2_ = ag.getRuleBase(); 
        assertSame(rb2_, rb2);
        assertEquals(1, rb2_.getPackages().length);
        assertEquals(1, rb2_.getPackages()[0].getGlobals().size());
        
        Thread.sleep( 2000 );

        RuleBase rb3 = ag.getRuleBase();
        assertSame(rb3, rb2);        
        
        assertEquals(1, rb3.getPackages().length);
        assertEquals(2, rb3.getPackages()[0].getGlobals().size());
        
        
        ag.stopPolling();
    }
    
    public void testPollingFilesRuleBaseReplace() throws Exception {
        File dir = RuleBaseAssemblerTest.getTempDirectory();
        
        Package p1 = new Package("p1");
        File p1f = new File(dir, "p43_.pkg");
        RuleBaseAssemblerTest.writePackage( p1, p1f );
        
        String path = dir.getPath() + "/" + "p43_.pkg";
        
        Properties props = new Properties();
        props.setProperty( "file", path );
        props.setProperty( "poll", "1" );
        props.setProperty( "newInstance", "true" );
        RuleBaseAgent ag = new RuleBaseAgent(props);
        assertTrue(ag.isNewInstance());
        assertTrue(ag.isPolling());
        RuleBase rb = ag.getRuleBase();
        assertEquals(1, rb.getPackages().length);

        RuleBase rb_ = ag.getRuleBase();
        assertSame(rb, rb_);
        
        RuleBaseAssemblerTest.writePackage( p1, p1f );
        
        Thread.sleep( 2100 );
        
        rb_ = ag.getRuleBase();

        assertNotSame( rb, rb_ );
        
        ag.stopPolling();
        
         
    }
    
    
    public void testDirectory() throws Exception {
        File dir = RuleBaseAssemblerTest.getTempDirectory();
        
        Package p1 = new Package("p1");
        File p1f = new File(dir, "p43_.pkg");
        RuleBaseAssemblerTest.writePackage( p1, p1f );

        Properties props = new Properties();
        props.setProperty( RuleBaseAgent.DIRECTORY, dir.getPath() );
        RuleBaseAgent ag = new RuleBaseAgent(props);
        
        ag.refreshRuleBase();
        
        RuleBase rb = ag.getRuleBase();
        assertNotNull(rb);
        assertEquals(1, rb.getPackages().length);
        
    }
    
    public void testLoadSampleConfig() {
        RuleBaseAgent ag = new RuleBaseAgent();
        Properties props = ag.loadFromProperties( "/sample-agent-config.properties" );
        assertEquals("10", props.getProperty( RuleBaseAgent.POLL_INTERVAL ));
        assertEquals("/home/packages", props.getProperty( RuleBaseAgent.DIRECTORY ));
        assertEquals("true", props.getProperty( RuleBaseAgent.NEW_INSTANCE ));
        assertEqualsIgnoreWhitespace( "/foo/bar.pkg /wee/waa.pkg /wee/waa2.pkg", props.getProperty( RuleBaseAgent.FILES ));
    }
    
    private void assertEqualsIgnoreWhitespace(final String expected,
                                              final String actual) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }
    
}
