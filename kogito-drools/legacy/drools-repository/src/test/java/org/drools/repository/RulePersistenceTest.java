package org.drools.repository;

import java.security.Principal;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.drools.repository.MetaData;
import org.drools.repository.RuleDef;
import org.drools.repository.db.PersistentCase;
import org.drools.repository.security.AssetPermission;
import org.drools.repository.security.RepositorySecurityManager;
import org.drools.repository.security.PermissionGroup;

public class RulePersistenceTest extends PersistentCase {

    public void testStoreNewRuleDef() throws Exception {
        RepositoryManager repo = getRepo();
        RuleDef def = new RuleDef("myRule", "A rule"); 
            repo.save(def);
        assertNotNull(def.getId());        
        repo.save(new RuleDef("myRule2", "A rule2"));               
        
        def = new RuleDef("myRule3", "A rule3");
        def.addTag("tag1").addTag("tag2").addTag("HR");
        repo.save(def);        
        
        assertNotNull(def.getId());
        Long id = def.getId();
        
        def.setContent("new content");
        repo.save(def);
        
        def = repo.loadRule("myRule3", 1);
        assertEquals(id, def.getId());
        
        Properties q = new Properties();
        q.setProperty("name", "myRule3");
        List list = repo.query("select name from RuleDef where name = :name", q);
        
        assertEquals("new content", def.getContent());
        assertEquals(3, def.getTags().size());
        def.removeTag("tag1");
        repo.save(def);
        
        def = repo.loadRule("myRule3", 1);
        assertEquals(null, def.getOwningRuleSetName());        
        assertEquals(2, def.getTags().size());
        
        
        RepositorySecurityManager mgr = new RepositorySecurityManager();
        mgr.enableSecurity(true);
        //setup a group for security
        PermissionGroup group = new PermissionGroup("michaelhome");
        group.addUserIdentity(getUserPrincipal().getName());
        
        
        mgr.saveGroup(group);
        String clazz = def.getClass().getName();
        mgr.addPermissionToGroup("michaelhome", new AssetPermission(clazz, def.getId(), AssetPermission.WRITE));
        mgr.addPermissionToGroup("michaelhome", new AssetPermission(clazz, AssetPermission.ALL_INSTANCES, AssetPermission.READ));
        repo = RepositoryFactory.getRepository(getUserPrincipal(), true);
        
        def = repo.loadRule("myRule3", 1);
        def.setContent("something else");
        repo.save(def);
        mgr.enableSecurity(false);
        
        assertNotNull(def.getLastSavedDate());
        assertEquals("michael", def.getLastSavedByUser());
        
        
        repo.close();
        mgr.commitAndClose();
    }

    private Principal getUserPrincipal() {
        return new Principal() {

                public String getName() {
                    return "michael";
                } 
                
                
            };
    }
        
    public void testRetreieveRuleWithTags() {
        RepositoryManager repo = getRepo();
        RuleDef newRule = new RuleDef("my rule RWT", "content");
        newRule.addTag("RWT").addTag("RWT2");
        repo.save(newRule);
        
        RuleDef rule = repo.loadRule("my rule RWT", 1);
        assertNotNull(rule);
        assertEquals("my rule RWT", rule.getName());

        Set tags = rule.getTags();
        assertEquals(2, tags.size());

        Tag firstTag = (Tag) rule.getTags().iterator().next();
        assertTrue(firstTag.getTag().equals("RWT") || firstTag.getTag().equals("RWT2"));
        
        List rules = repo.findRulesByTag("RWT");
        assertEquals(1, rules.size());
        rule = (RuleDef) rules.get(0);
        assertEquals("my rule RWT", rule.getName());
    }
    
    public void testRuleCopy() {
        RepositoryManager repo = getRepo();
        
        RuleDef rule1 = new RuleDef("newVersionTest", "XXX");
        rule1.addTag("HR").addTag("BOO");
        rule1.setLastSavedByUser("blah");
        MetaData meta = new MetaData();
        meta.setCreator("Peter Jackson");
        rule1.setMetaData(meta);
        
        repo.save(rule1);
        RuleDef ruleCopy  = (RuleDef) rule1.copy();
        assertEquals(null, ruleCopy.getId());
        assertEquals(2, ruleCopy.getTags().size());
        assertEquals("Peter Jackson", ruleCopy.getMetaData().getCreator());
        assertEquals("blah", ruleCopy.getLastSavedByUser());
    }
    
    public void testRuleRuleSetHistory() {
        RuleSetDef rs = new RuleSetDef("rule history", null);
        RuleDef first = rs.addRule(new RuleDef("rh1", "xxxxx"));
        rs.addRule(new RuleDef("rh2", "xxxxx"));
        rs.addRule(new RuleDef("rh3", "xxxxx"));
        
        RepositoryManager repo = getRepo();
        repo.save(rs);
        
        rs = repo.loadRuleSet("rule history", 1);
        rs.createNewVersion("yeah");
        repo.save(rs);
        
        
        List list = repo.listRuleVersions("rh1");
        assertEquals(2, list.size());
        assertTrue(list.get(0) instanceof RuleDef);
        
        RuleDef rule = (RuleDef) list.get(0);
        rule.addTag("XYZ");
        repo.save(rule);

        list = repo.listRuleVersions("rh1");
        assertEquals(2, list.size());
        
        rule.setContent("NEW CONTENT");
        repo.save(rule);
        
        rule.setContent("MORE NEW");
        repo.save(rule);
        
        list = repo.listRuleVersions("rh1");
        assertEquals(2, list.size());
        
        list = repo.listSaveHistory(rule); 
        assertEquals(2, list.size());

        rs = repo.loadRuleSet("rule history", 1);
        RuleDef firstLoaded = rs.findRuleByName("rh1");
        firstLoaded.setContent("new again");
        rs.modify(firstLoaded);
        repo.save(rs);
        
        rs = repo.loadRuleSet("rule history", 1);
        RuleDef loadedAgain = rs.findRuleByName("rh1");
        
        assertEquals(firstLoaded.getContent(), loadedAgain.getContent());
        
    }
    
    public void testCheckinOut() {
        RuleDef rule = new RuleDef("checkin", "some rule");
        
        RepositoryManager repo = RepositoryFactory.getRepository(new MockUser("michael"), false);
        repo.save(rule);
        
        repo.checkOutRule(rule);
        rule = repo.loadRule("checkin", 1);
        
        assertEquals(true, rule.isCheckedOut());
        assertEquals("michael", rule.getCheckedOutBy());
        
        repo = RepositoryFactory.getRepository(new MockUser("rohit"), false);
        
        try {
            //whoops we cant check it in
            repo.checkInRule(rule);
        } catch (RepositoryException e) {
            assertNotNull(e.getMessage());
        }
        
        //now we can check it in
        repo = RepositoryFactory.getRepository(new MockUser("michael"), false);
        
        repo.checkInRule(rule);
        assertEquals(false, rule.isCheckedOut());
        
    }
    
    
    


    
    

    
}
