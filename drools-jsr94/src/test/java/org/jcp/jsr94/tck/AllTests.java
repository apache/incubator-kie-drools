/*
 * J A V A  C O M M U N I T Y  P R O C E S S
 *
 * J S R  9 4
 *
 * Test Compatability Kit
 *
 */
package org.jcp.jsr94.tck;

// java imports
import junit.framework.Test;
import junit.framework.TestSuite;

import org.jcp.jsr94.tck.admin.LocalRuleExecutionSetProviderTest;
import org.jcp.jsr94.tck.admin.RuleAdministrationExceptionTest;
import org.jcp.jsr94.tck.admin.RuleAdministratorTest;
import org.jcp.jsr94.tck.admin.RuleExecutionSetCreateExceptionTest;
import org.jcp.jsr94.tck.admin.RuleExecutionSetDeregistrationExceptionTest;
import org.jcp.jsr94.tck.admin.RuleExecutionSetProviderTest;
import org.jcp.jsr94.tck.admin.RuleExecutionSetRegisterExceptionTest;
import org.jcp.jsr94.tck.admin.RuleExecutionSetTest;
import org.jcp.jsr94.tck.admin.RuleTest;

/**
 * Run all the test suites in the Test Compatability Kit.
 * Output is directed to System.out (textui).
 */
public class AllTests extends TestSuite {

    public static Test suite() {
        System.setProperty( "jsr94.tck.configuration",
                            "src/test/resources/org/drools/jsr94/tck" );

        TestSuite suite = new TestSuite( "JSR 94 Test Compatability Kit" );
        suite.addTestSuite( ApiSignatureTest.class );
        suite.addTestSuite( ClassLoaderTest.class );
        suite.addTestSuite( ConfigurationExceptionTest.class );
        suite.addTestSuite( HandleTest.class );
        suite.addTestSuite( InvalidHandleExceptionTest.class );
        suite.addTestSuite( InvalidRuleSessionExceptionTest.class );
        suite.addTestSuite( ObjectFilterTest.class );
        suite.addTestSuite( RuleExceptionTest.class );
        suite.addTestSuite( RuleExecutionExceptionTest.class );
        suite.addTestSuite( RuleExecutionSetMetadataTest.class );
        suite.addTestSuite( RuleExecutionSetNotFoundExceptionTest.class );
        suite.addTestSuite( RuleRuntimeTest.class );
        suite.addTestSuite( RuleServiceProviderManagerTest.class );
        suite.addTestSuite( RuleServiceProviderTest.class );
        suite.addTestSuite( RuleSessionCreateExceptionTest.class );
        suite.addTestSuite( RuleSessionTest.class );
        suite.addTestSuite( RuleSessionTypeUnsupportedExceptionTest.class );
        suite.addTestSuite( StatefulRuleSessionTest.class );
        suite.addTestSuite( StatelessRuleSessionTest.class );
        suite.addTestSuite( LocalRuleExecutionSetProviderTest.class );
        suite.addTestSuite( RuleAdministrationExceptionTest.class );
        suite.addTestSuite( RuleAdministratorTest.class );
        suite.addTestSuite( RuleExecutionSetCreateExceptionTest.class );
        //     suite.addTestSuite(RuleExecutionSetProviderTest.class);
        suite.addTestSuite( RuleExecutionSetRegisterExceptionTest.class );
        suite.addTestSuite( RuleExecutionSetTest.class );
        suite.addTestSuite( RuleExecutionSetDeregistrationExceptionTest.class );
        suite.addTestSuite( RuleTest.class );
        return suite;
    }
    //   public static Test suite()
    //   {
    //       
    //
    //       TestSuite suite = new TestSuite( "JSR 94 Test Compatability Kit" );
    //       System.out.println(System.getProperty("jsr94.tck.configuration"));
    //	   suite.addTestSuite(ApiSignatureTest.class);
    //	   suite.addTestSuite(ClassLoaderTest.class);
    //	   suite.addTestSuite(ConfigurationExceptionTest.class);
    //	   suite.addTestSuite(HandleTest.class);
    //	   suite.addTestSuite(InvalidHandleExceptionTest.class);
    //	   suite.addTestSuite(InvalidRuleSessionExceptionTest.class);
    //	   suite.addTestSuite(ObjectFilterTest.class);
    //	   suite.addTestSuite(RuleExceptionTest.class);
    //	   suite.addTestSuite(RuleExecutionExceptionTest.class);
    //	   suite.addTestSuite(RuleExecutionSetMetadataTest.class);
    //	   suite.addTestSuite(RuleExecutionSetNotFoundExceptionTest.class);
    //	   suite.addTestSuite(RuleRuntimeTest.class);
    //	   suite.addTestSuite(RuleServiceProviderManagerTest.class);
    //	   suite.addTestSuite(RuleServiceProviderTest.class);
    //	   suite.addTestSuite(RuleSessionCreateExceptionTest.class);
    //	   suite.addTestSuite(RuleSessionTest.class);
    //	   suite.addTestSuite(RuleSessionTypeUnsupportedExceptionTest.class);
    //	   suite.addTestSuite(StatefulRuleSessionTest.class);
    //	   suite.addTestSuite(StatelessRuleSessionTest.class);
    //	   suite.addTestSuite(LocalRuleExecutionSetProviderTest.class);
    //	   suite.addTestSuite(RuleAdministrationExceptionTest.class);
    //	   suite.addTestSuite(RuleAdministratorTest.class);
    //	   suite.addTestSuite(RuleExecutionSetCreateExceptionTest.class);
    ////	   suite.addTestSuite(RuleExecutionSetProviderTest.class);
    //	   suite.addTestSuite(RuleExecutionSetRegisterExceptionTest.class);
    //	   suite.addTestSuite(RuleExecutionSetTest.class);
    //	   suite.addTestSuite(RuleExecutionSetDeregistrationExceptionTest.class);
    //	   suite.addTestSuite(RuleTest.class);
    //	   return suite;
    //   }
}
