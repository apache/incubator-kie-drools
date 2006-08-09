package org.drools.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.drools.repository.security.AssetPermission;
import org.drools.repository.security.RepositorySecurityException;
import org.drools.repository.security.RepositorySecurityManager;
import org.hibernate.StaleObjectStateException;

import junit.framework.TestCase;

/**
 * This integration test aims to do a full suite of scenarios.
 * 
 * It also may take a lot longer to run then the other "unit" tests.
 * 
 * Including concurrent editing of rules, simulating multiple rules in parallel.
 * 
 * Note that I am using stateful repository instances as it makes it easier to
 * test. I can control when the session is closed etc in a single threaded test
 * run.
 * 
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class IntegrationTest extends TestCase {

    /**
     * This will all execute as one JUnit test. Any failure will cause the test
     * to stop, but this is not a unit test, so that is acceptable.
     */
    public void testBootstrap() throws Exception {
        runVersioningTests();
        runAttachmentTests();
        runLocalOfflineTests();
        runConcurrentTests();
        runACLTests();

    }

    private void runACLTests() {
        RuleSetDef setA = new RuleSetDef( "aclSet",
                                          null );
        setA.addRule( new RuleDef( "offline1",
                                   "blah" ) );
        setA.addRule( new RuleDef( "offline2",
                                   "blah" ) );

        RepositoryManager repo = RepositoryFactory.getStatefulRepository();
        repo.save( setA );
        repo.close();

        //we enable security
        RepositorySecurityManager mgr = new RepositorySecurityManager();
        mgr.enableSecurity( true );


        repo = RepositoryFactory.getRepository( getAclTestUser(),
                                                true );

        // should fail, as we don't have ruleset permission
        try {
            setA = repo.loadRuleSet( "aclSet",
                                     1 );
            fail( "Should not be allowed to load" );
        }
        catch ( RepositorySecurityException e ) {

        }

        mgr.addUserToGroup( "aclTester",
                            "aclTesters" );
        mgr.addPermissionToGroup( "aclTesters",
                                  new AssetPermission( setA,
                                                       AssetPermission.READ ) );
        //we have to close the connection, and open another one to get new ACL
        repo.close();
        repo = RepositoryFactory.getRepository( getAclTestUser(),
                                                true );
        
        // still should not be allowed, as we don't have rule permissions !
        try {
            setA = repo.loadRuleSet( "aclSet",
                                     1 );
            fail( "Should not be allowed to load" );
        }
        catch ( RepositorySecurityException e ) {

        }

        repo.close();
        
        //add permissions to read all ruledefs
        mgr.addPermissionToGroup( "aclTesters",
                                  new AssetPermission( RuleDef.class.getName(),
                                                       AssetPermission.ALL_INSTANCES,
                                                       AssetPermission.READ ) );

        //and load it up fresh
        repo = RepositoryFactory.getRepository( getAclTestUser(),
                                                true );
        //should be ok now
        setA = repo.loadRuleSet( "aclSet",
                                      1 );
        assertNotNull(setA);

        repo.close();
        mgr.enableSecurity( false );
        mgr.commitAndClose();

    }

    private Principal getAclTestUser() {
        return new Principal() {
                                                    public String getName() {
                                                        return "aclTester";
                                                    }
                                                };
    }

    /**
     * Test local store features, simulating 2 users running offline.
     */
    private void runLocalOfflineTests() throws Exception {

        RuleSetDef setA = new RuleSetDef( "offline1",
                                          null );
        setA.addRule( new RuleDef( "offline1",
                                   "blah" ) );
        setA.addRule( new RuleDef( "offline2",
                                   "blah" ) );

        RepositoryManager repo = RepositoryFactory.getStatefulRepository();
        repo.save( setA );
        repo.close();

        repo = RepositoryFactory.getStatefulRepository();
        // load up copy A
        setA = repo.loadRuleSet( "offline1",
                                 1 );
        repo.close();

        // now load up copy B
        repo = RepositoryFactory.getStatefulRepository();
        RuleSetDef setB = repo.loadRuleSet( "offline1",
                                            1 );
        repo.close();

        // now store them locally
        LocalStore store = new LocalStore();
        ByteArrayOutputStream outA = new ByteArrayOutputStream();
        ByteArrayOutputStream outB = new ByteArrayOutputStream();
        store.save( setA,
                    outA );
        store.save( setB,
                    outB );

        setA = (RuleSetDef) store.load( new ByteArrayInputStream( outA.toByteArray() ) );
        setB = (RuleSetDef) store.load( new ByteArrayInputStream( outB.toByteArray() ) );
        // setA.addRule(new RuleDef("yeah", "boo"));
        // setB.addRule(new RuleDef("goo", "ya"));
        RuleDef rule1 = setA.findRuleByName( "offline1" );
        RuleDef rule2 = setB.findRuleByName( "offline2" );
        rule1.setContent( "something" );
        setA.addApplicationData(new ApplicationDataDef("dsadsa", "dfsads"));
        setB.setMetaData(new MetaData());

        // no problems expected, as they were editing different rules
        repo = RepositoryFactory.getStatefulRepository();
        repo.save( setA );
        repo.close();

        repo = RepositoryFactory.getStatefulRepository();
        repo.save( setB );
        repo.close();

    }

    /**
     * The purpose of this test is to simulate 2 different users editing rules
     * in different session. Detached and all.
     */
    private void runConcurrentTests() {
        // 2 repos, representing different users.
        RepositoryManager repoA = RepositoryFactory.getStatefulRepository();
        RepositoryManager repoB = RepositoryFactory.getStatefulRepository();

        // Lets try a simple rule, which will stay stand alone
        RuleDef ruleA = new RuleDef( "Concurrent 1",
                                     "content1" );
        repoA.save( ruleA );
        repoA.close();
        repoA = RepositoryFactory.getStatefulRepository();

        // Bob loads it up, decides he wants to edit it.
        RuleDef ruleB = repoB.loadRule( "Concurrent 1",
                                        1 );
        assertEquals( "content1",
                      ruleB.getContent() );
        ruleB.setContent( "bobs version" );

        // Michael also is editing his version.
        ruleA.setContent( "michaels version" );

        // oh dear, we can't both be write can we?
        // but Bob is just a bit quicker ...
        repoB.save( ruleB );
        try {
            repoA.save( ruleA );
            fail();
        }
        catch ( StaleObjectStateException e ) {
            assertNotNull( e.getMessage() );
        }

        repoA = RepositoryFactory.getStatefulRepository();

        // now try some rulesets.
        RuleSetDef ruleSet = new RuleSetDef( "Integration concurrent 1",
                                             null );
        ruleSet.addRule( new RuleDef( "Concurrent 2",
                                      "abc" ).addTag( "yeah" ) );
        repoA.save( ruleSet );
        repoA.close();

        // we will add a rule to each one, concurrently
        repoA = RepositoryFactory.getStatefulRepository();
        repoB = RepositoryFactory.getStatefulRepository();

        RuleSetDef ruleSetA = repoA.loadRuleSet( "Integration concurrent 1",
                                                 1 );
        RuleSetDef ruleSetB = repoB.loadRuleSet( "Integration concurrent 1",
                                                 1 );

        ruleA = new RuleDef( "Concurrent 3",
                             "content" );
        ruleSetA.addRule( ruleA );
        ruleSetB.addRule( new RuleDef( "Concurrent 4",
                                       "content" ) );

        // should have no problems, as we are not touching the same rules.
        repoA.save( ruleSetA );
        repoB.save( ruleSetB );

        ruleSetA.addFunction( new FunctionDef( "abc",
                                               "yeah" ) );
        repoA.save( ruleSetA );

        ruleA.setContent( "new content" );
        repoA.save( ruleSetA );

        repoA.close();
        repoB.close();

        repoA = RepositoryFactory.getStatefulRepository();
        ruleSetA = repoA.loadRuleSet( "Integration concurrent 1",
                                      1 );
        assertEquals( 3,
                      ruleSetA.getRules().size() );
        ruleA = ruleSetA.findRuleByName( "Concurrent 3" );
        assertEquals( "new content",
                      ruleA.getContent() );

        repoA.close();

        // now lets have bob and michael both edit to different rules at once
        repoA = RepositoryFactory.getStatefulRepository();
        repoB = RepositoryFactory.getStatefulRepository();
        ruleSetA = repoA.loadRuleSet( "Integration concurrent 1",
                                      1 );
        ruleSetB = repoB.loadRuleSet( "Integration concurrent 1",
                                      1 );

        ruleA = ruleSetA.findRuleByName( "Concurrent 3" );
        ruleB = ruleSetB.findRuleByName( "Concurrent 4" );

        ruleA.setContent( "something wild" );
        ruleB.setContent( "something simple" );

        // should be fine, as different rules.
        repoA.save( ruleSetA );
        repoB.save( ruleSetB );

        repoA.close();
        repoB.close();

    }

    private void runAttachmentTests() {
        RepositoryManager repo = RepositoryFactory.getStatefulRepository();

        byte[] data = new byte[4096];
        Random rand = new Random( System.currentTimeMillis() );
        rand.nextBytes( data );

        RuleSetAttachment attachment = new RuleSetAttachment( "spreadsheet",
                                                              "something",
                                                              data,
                                                              "something.xls" );
        attachment.addTag( "BLAH" );
        repo.save( attachment );

        repo.close();
        repo = RepositoryFactory.getStatefulRepository();

        RuleSetDef ruleSet = new RuleSetDef( "Integration attachments 1",
                                             null );
        ruleSet.addAttachment( attachment );
        ruleSet.addAttachment( new RuleSetAttachment( "nothing",
                                                      "boo",
                                                      data,
                                                      "nothing.txt" ) );
        ruleSet.getVersionInfoWorking().setStatus( "draft" );
        repo.save( ruleSet );
        repo.close();

        repo = RepositoryFactory.getStatefulRepository();
        ruleSet = repo.loadRuleSet( "Integration attachments 1",
                                    1 );
        assertEquals( 2,
                      ruleSet.getAttachments().size() );

        // lets create a new version, must reload it after doing this
        ruleSet.createNewVersion( "going to remove" );
        ruleSet.getVersionInfoWorking().setStatus( "latest" );
        repo.save( ruleSet );
        assertEquals( 2,
                      ruleSet.getVersionInfoWorking().getVersionNumber() );
        assertEquals( "going to remove",
                      ruleSet.getVersionInfoWorking().getVersionComment() );
        assertEquals( "latest",
                      ruleSet.getVersionInfoWorking().getStatus() );

        // now lets load the old one.
        ruleSet = repo.loadRuleSet( "Integration attachments 1",
                                    1 );
        assertEquals( "draft",
                      ruleSet.getVersionInfoWorking().getStatus() );
        // and remove an attachment
        RuleSetAttachment at = (RuleSetAttachment) ruleSet.getAttachments().iterator().next();
        ruleSet.removeAttachment( at ); // don't forget to reload it after save
        // !!
        repo.save( ruleSet );
        ruleSet = repo.loadRuleSet( "Integration attachments 1",
                                    1 );
        assertEquals( 1,
                      ruleSet.getAttachments().size() );

        // load up the new version, check that it has 2 attachments
        ruleSet = repo.loadRuleSet( "Integration attachments 1",
                                    2 );
        assertEquals( 2,
                      ruleSet.getAttachments().size() );

        repo.close();
        repo = RepositoryFactory.getRepository( new MockUser( "Michael" ),
                                                false );

        // now with a new session, lets load up the latest, and add an
        // attachment
        ruleSet = repo.loadRuleSet( "Integration attachments 1",
                                    2 );
        assertEquals( 2,
                      ruleSet.getAttachments().size() );
        ruleSet.addAttachment( new RuleSetAttachment( "fdsfds",
                                                      "blah2",
                                                      data,
                                                      "nothing.xyz" ) );
        repo.save( ruleSet );

        // load up the old one
        ruleSet = repo.loadRuleSet( "Integration attachments 1",
                                    1 );
        assertEquals( 1,
                      ruleSet.getAttachments().size() );

        // and the new one, just to be sure
        ruleSet = repo.loadRuleSet( "Integration attachments 1",
                                    2 );
        assertEquals( 3,
                      ruleSet.getAttachments().size() );

        RuleSetAttachment att = (RuleSetAttachment) ruleSet.getAttachments().iterator().next();
        repo.checkOutAttachment( att );
        assertEquals( true,
                      att.isCheckedOut() );
        repo.checkInAttachment( att );
        assertEquals( false,
                      att.isCheckedOut() );

        repo.close();
    }

    /**
     * These tests show how it all hangs together regarding versioning. You can
     * see versioning in action, save history etc.
     */
    private void runVersioningTests() {
        RepositoryManager repo = RepositoryFactory.getStatefulRepository();

        // In the beginning there was a rule
        RuleDef rule = new RuleDef( "Integration rule 1",
                                    "rule { when bob likes cheese then print 'yeah' }" );
        rule.setDocumentation( "This is an initial rule" );
        rule.addTag( "CHEESE RULE" );
        rule.setMetaData( new MetaData( "a rule",
                                        "michael",
                                        "cheese",
                                        "something",
                                        "drl" ) );
        repo.save( rule );

        // now lets load that rule back
        List list = repo.findRulesByTag( "CHEESE RULE" );
        assertEquals( 1,
                      list.size() );
        rule = (RuleDef) list.get( 0 );
        assertEquals( "This is an initial rule",
                      rule.getDocumentation() );

        // lets create a ruleset
        MetaData meta = new MetaData();
        meta.setContributor( "Michael" );
        meta.setDescription( "A test ruleset" );

        RuleSetDef ruleSet = new RuleSetDef( "Integration ruleset 1",
                                             meta );
        RuleSetVersionInfo info = ruleSet.getVersionInfoWorking();
        info.setStatus( "draft" );

        // time to save it
        repo.save( ruleSet );

        // now lets finish this session
        repo.close();

        // check we can load it up
        repo = RepositoryFactory.getStatefulRepository();
        assertTrue( repo.listRuleSets().size() > 0 );
        ruleSet = repo.loadRuleSet( "Integration ruleset 1",
                                    1 );
        assertEquals( "draft",
                      ruleSet.getVersionInfoWorking().getStatus() );

        // now lets work "disconnected" for a while
        repo.close();

        // lets add the rule to it that we loaded before, and some other goodies
        ruleSet.addRule( rule );
        ruleSet.addRule( new RuleDef( "Integration rule 2",
                                      "Some content" ) );
        ruleSet.addFunction( new FunctionDef( "this is a function",
                                              "blah blah blah" ) );
        ruleSet.addApplicationData( new ApplicationDataDef( "blah",
                                                            "String" ) );
        ruleSet.addImport( new ImportDef( "java.lang.String" ) );

        repo = RepositoryFactory.getStatefulRepository();
        repo.save( ruleSet );
        repo.close();

        // now lets load it again, and work on it disconnected
        repo = RepositoryFactory.getStatefulRepository();
        ruleSet = repo.loadRuleSet( "Integration ruleset 1",
                                    1 );
        repo.close();

        // check its OK first
        assertEquals( "this is a function",
                      ((FunctionDef) ruleSet.getFunctions().iterator().next()).getFunctionContent() );
        assertEquals( 2,
                      ruleSet.getRules().size() );

        // now lets go and modify some rules
        for ( Iterator iter = ruleSet.getRules().iterator(); iter.hasNext(); ) {
            RuleDef myRule = (RuleDef) iter.next();
            myRule.setContent( "CHANGED RULE TEXT" );
            ruleSet.modify( myRule );
            // this should cause us to have some rule history saved.
            // this has nothing to do with ruleset versioning.
        }

        // connect and save
        repo = RepositoryFactory.getStatefulRepository();
        repo.save( ruleSet );
        ruleSet = repo.loadRuleSet( "Integration ruleset 1",
                                    1 );

        // just to prove the save history, lets check the history of a rule
        rule = ruleSet.findRuleByName( "Integration rule 2" );
        List history = repo.listSaveHistory( rule );
        assertEquals( 1,
                      history.size() );
        RuleDef historicalRule = (RuleDef) history.get( 0 );
        String oldContent = historicalRule.getContent();
        assertTrue( !oldContent.equals( rule.getContent() ) );

        // lets also check that the correct number of rules are on the ruleset,
        // history is seperate
        assertEquals( 2,
                      ruleSet.getRules().size() );

        // now lets create a new major version of the ruleset.
        ruleSet.createNewVersion( "New version" );
        ruleSet.getVersionInfoWorking().setStatus( "pending" );
        repo.save( ruleSet );
        repo.close();

        // now we will have 2 versions of the ruleset, lets load up the latest.
        repo = RepositoryFactory.getStatefulRepository();
        ruleSet = repo.loadRuleSet( "Integration ruleset 1",
                                    2 );
        assertEquals( 2,
                      ruleSet.getVersionHistory().size() );
        assertEquals( "New version",
                      ruleSet.getVersionInfoWorking().getVersionComment() );
        assertEquals( "pending",
                      ruleSet.getVersionInfoWorking().getStatus() );

        // lets add a rule to it (making 3 rules in total)
        ruleSet.addRule( new RuleDef( "Integration rule 3",
                                      "content" ) );

        repo.save( ruleSet );

        // lets load up the old version, check that there is still only 2 rules
        ruleSet = repo.loadRuleSet( "Integration ruleset 1",
                                    1 );
        assertEquals( 2,
                      ruleSet.getRules().size() );
        assertEquals( "draft",
                      ruleSet.getVersionInfoWorking().getStatus() );

        // ooh look, the new one has 3, and they are all different rules !
        assertEquals( 3,
                      repo.loadRuleSet( "Integration ruleset 1",
                                        2 ).getRules().size() );

        runTestQueryAPI( repo );

        repo.close();
    }

    private void runTestQueryAPI(RepositoryManager repo) {
        Map params = new HashMap();
        params.put( "fragment",
                    "%Integration%" );
        params.put( "version",
                    new Long( 1 ) );
        List query = repo.query( "from RuleDef where name like :fragment and versionNumber = :version",
                                 params );
        assertNotNull( query );
        assertTrue( query.size() > 0 );
    }

    /**
     * Lets create 3 rulesets, each with 500 rules. Each RuleSet will have
     * several functions, applicationdata, imports and so on. Rules will also
     * have tags, a
     */
    public void initialDataSetup() {
        create500CheeseRules();

    }

    private void create500CheeseRules() {
        RepositoryManager repo = RepositoryFactory.getStatefulRepository();

        // lets build some rules, stand alone
        for ( int i = 0; i < 500; i++ ) {
            RuleDef rule = new RuleDef( "cheese-rule-" + i,
                                        "The rule body here " + i );
            rule.addTag( "FIRST" ).addTag( "NUM" + i );
            repo.save( rule );
        }

        repo.close();
    }

}
