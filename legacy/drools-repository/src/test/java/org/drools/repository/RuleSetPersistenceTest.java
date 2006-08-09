package org.drools.repository;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.drools.repository.db.PersistentCase;

/**
 * Some quasi unit tests, and some quasi integration tests including versioning.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 * Take deep breaths... its not really that scary...
 */
public class RuleSetPersistenceTest extends PersistentCase {

    public void testLoadSaveRuleSet() {
        MetaData meta = new MetaData();
        meta.setCreator("Michael Neale");
        meta.setRights("Unlimited");
        
        RuleSetDef def = new RuleSetDef("my ruleset", meta);
        def.addTag("ME");
        def.addRule(new RuleDef("simple", "x"));
        RepositoryManager repo = getRepo();
        repo.save(def);
        
        RuleSetDef def2 = repo.loadRuleSet("my ruleset", 1);
        assertEquals("my ruleset", def2.getName());
        assertEquals("Michael Neale", def2.getMetaData().getCreator());
        assertEquals(1, def2.getTags().size());
        
        //now modify the content of a rule, ensure it is saved
        RuleDef rule = (RuleDef) def2.getRules().iterator().next();
        rule.setContent("Something new");
        def2.modify(rule);
        
        repo.save(def2);
        
        def2 = repo.loadRuleSet("my ruleset", 1);
        RuleDef rule2 = (RuleDef) def2.getRules().iterator().next();
        assertEquals("Something new", rule2.getContent());
        
        //try adding a pre-saved rule
        RuleDef newRule = new RuleDef("pre-existing", "ABC");
        repo.save(newRule);
        newRule.addTag("a tag");
        repo.save(newRule);
        newRule = repo.loadRule("pre-existing", 1);
        def2.addRule(newRule);
        repo.save(def2);
        def2 = repo.loadRuleSet("my ruleset", 1);
        
        newRule = def2.findRuleByName("pre-existing");
        assertEquals("ABC", newRule.getContent());
        assertEquals(1, newRule.getTags().size());
    }
    
    public void testRuleSetWithRules() {
        MetaData meta = new MetaData();
        meta.setCreator("Michael Neale");
        RuleSetDef ruleSet = new RuleSetDef("Uber 1", meta);
        ruleSet.addRule(new RuleDef("UBER1Rule", "This is a rule"));
        RuleDef def = new RuleDef("UBER2Rule", "this is also a rule");
        def.addTag("HR").addTag("BUS");
        ruleSet.addRule(def);
        ruleSet.addTag("HR");
        
        RepositoryManager repo = getRepo();
        repo.save(ruleSet);
        
        RuleSetDef loaded = repo.loadRuleSet("Uber 1", 1);
        assertEquals(2, loaded.getRules().size());
        
    }
    
    public void testRuleSetWithAttachment() {
        MetaData meta = new MetaData();
        meta.setCreator("Michael Neale - Uber pimp");
        RuleSetDef ruleSet = new RuleSetDef("Attachmate", meta);
        
        RuleSetAttachment attachment = new RuleSetAttachment("decision-table", 
                                                             "my text file", 
                                                             "content".getBytes(), 
                                                             "file.txt");
        ruleSet.addAttachment(attachment);
        
        RepositoryManager repo = getRepo();
        repo.save(ruleSet);
        
        RuleSetDef result = repo.loadRuleSet("Attachmate", 1);             
        assertEquals(1, result.getAttachments().size());
        RuleSetAttachment at2 = (RuleSetAttachment) result.getAttachments().iterator().next();
        assertEquals("file.txt", at2.getOriginalFileName());
    }
    
    public void testRuleSetWithVersionHistory() {
        RuleSetDef def = new RuleSetDef("WithHistory", null);
        Set history = new HashSet();
        RuleSetVersionInfo info = new RuleSetVersionInfo(1, "blah");
        
        history.add(info);
        RuleSetVersionInfo info2 = new RuleSetVersionInfo(2, "woo");
        history.add(info2);
        
        def.setVersionHistory(history);
        RepositoryManager repo = getRepo();
        repo.save(def);
        
        RuleSetDef def2 = repo.loadRuleSet("WithHistory", 1);
        assertEquals(2, def2.getVersionHistory().size());
    }
    
    public void testRemoveAsset() {
        RuleSetDef def = new RuleSetDef("addRemove", null);
        RuleDef rule = new RuleDef("addRemove Rule", "xxx");
        def.addRule(rule);
                
        RepositoryManager repo = getRepo();
        
        //save and load it fresh
        repo.save(def);               
        def = repo.loadRuleSet("addRemove", 1);
        assertEquals(1, def.getRules().size());
        
        //create a new version
        def.createNewVersion("new version");        
        repo.save(def);
        
        //load it fresh, and the remove the only rule
        def = repo.loadRuleSet("addRemove", 2);
        RuleDef onlyRule = (RuleDef) def.getRules().iterator().next();
        def.removeRule(onlyRule);
        assertEquals(null, onlyRule.getOwningRuleSetName());
        //assertEquals(0, def.getRules().size());
        repo.save(def);
        
        //load the new one, check its not there
        def = repo.loadRuleSet("addRemove", 2);
        assertEquals(0, def.getRules().size());        
        
        //load the old version, check its there
        def = repo.loadRuleSet("addRemove", 1);
        assertEquals(1, def.getRules().size());
        

    }
    
    public void testIntegrationNewVersioning() {
        RuleSetDef set = new RuleSetDef("InMemory", null);
        RuleDef def1 = new RuleDef("Rule1", "blah");
        RuleDef def2 = new RuleDef("Rule2", "blah2");
        
        def1.addTag("S").addTag("A");
        set.addRule(def1);
        set.addRule(def2);
        set.addAttachment(new RuleSetAttachment("x", "x", "x".getBytes(), "x"));
        
        assertEquals(2, set.getRules().size());
        assertEquals(1, def1.getVersionNumber());
        assertEquals(1, def2.getVersionNumber());
        assertEquals(1, set.getWorkingVersionNumber());
        
        //once we create a new version, we double the asset counts, one for each version
        set.createNewVersion("New version");
        assertEquals(4, set.getRules().size());
        assertEquals(2, set.getAttachments().size());
        assertEquals(1, def1.getVersionNumber());
        
        //now check that the new version rules are kosher (tag wise)
        for ( Iterator iter = set.getRules().iterator(); iter.hasNext(); ) {
            RuleDef rule = (RuleDef) iter.next();
            if (rule.getVersionNumber() == 2) {
                assertEquals("New version", rule.getVersionComment());
                if (rule.getName().equals("Rule1")) {
                    assertEquals(2, rule.getTags().size());
                }
            }            
        }
        
        
        RepositoryManager repo = getRepo();
        repo.save(set);
        
        //now when we load it, the filter only loads the workingVersion that we specify
        RuleSetDef loaded = repo.loadRuleSet("InMemory", 2);
        
        //now should have half as many as before, as old versions are not loaded.
        assertEquals(2, loaded.getRules().size());        
        assertEquals(1, loaded.getAttachments().size());
        
        //now check the version numbers and comment, use attachment as there is only one in set so easy..
        RuleSetAttachment att = (RuleSetAttachment) loaded.getAttachments().iterator().next();
        assertEquals(2, att.getVersionNumber());
        assertEquals("New version", att.getVersionComment());
        
        //now run it again, with OLD VERSION...
        loaded = repo.loadRuleSet("InMemory", 1);
        assertEquals(2, loaded.getRules().size());        
        assertEquals(1, loaded.getAttachments().size());

        //now the version number should be one
        att = (RuleSetAttachment) loaded.getAttachments().iterator().next();
        assertEquals(1, att.getVersionNumber());
        assertFalse("New version".equals(att.getVersionComment()));
        
        RuleDef newRule = new RuleDef("blah42", "blah42"); 
        loaded.addRule(newRule);
        //add some other assets to it.
        loaded
            .addApplicationData(new ApplicationDataDef("x", "XX"))
            .addFunction(new FunctionDef("My func", "yeah"))
            .addImport(new ImportDef("com.allenparsons.project"));
        repo.save(loaded);
        assertEquals(loaded.getWorkingVersionNumber(), newRule.getVersionNumber());
        assertNotNull(newRule.getId());
        
        loaded = repo.loadRuleSet("InMemory", 1);
        assertEquals(1, loaded.getApplicationData().size());
        assertEquals(1, loaded.getFunctions().size());
        assertEquals(1, loaded.getImports().size());
        
        loaded = repo.loadRuleSet("InMemory", 2);
        assertEquals(0, loaded.getApplicationData().size());
        assertEquals(0, loaded.getFunctions().size());
        assertEquals(0, loaded.getImports().size());
    }
    
    public void testParallelVersions() {
        RuleSetDef def = new RuleSetDef("para", null);
        def.addRule(new RuleDef("para1","sss"));
        
        RepositoryManager repo = getRepo();
        repo.save(def);
        
        //create a new version
        def = repo.loadRuleSet("para", 1);
        def.createNewVersion("yeah");        
        repo.save(def);
        
        //load em up
        RuleSetDef old = repo.loadRuleSet("para", 1);
        RuleSetDef newRs = repo.loadRuleSet("para", 2);
        assertEquals(1, old.getRules().size());
        assertEquals(1, newRs.getRules().size());
        
        //add a rule to new one
        newRs.addRule(new RuleDef("para2", "xxx"));
        newRs.addTag("HR");
        repo.save(newRs);

        
        newRs = repo.loadRuleSet("para", 2);
        old = repo.loadRuleSet("para", 1);
        assertEquals(2, newRs.getRules().size());
        assertEquals(1, old.getRules().size());
        
        RuleDef originalRule = (RuleDef) old.getRules().iterator().next();
        //check that the original one still has the right version number
        assertEquals(1, originalRule.getVersionNumber());                
        
        assertEquals(1, old.getRules().size());
        repo.delete(originalRule);
        old = repo.loadRuleSet("para", 1);
        assertEquals(0, old.getRules().size());
        
        
    }
    
    public void testSaveHistoryFromCascade() {
        
        RuleSetDef old = new RuleSetDef("something old", null);
        RepositoryManager repo = getRepo();
        RuleDef newRule = new RuleDef("save history 2", "ABC");        
        old.addRule(newRule);
        
        repo.save(old);
        
        assertEquals(0, repo.listSaveHistory(newRule).size());
        old.addTag("yeah");
        repo.save(old);
        assertEquals(0, repo.listSaveHistory(newRule).size());
        newRule.setContent("CHANGED CONTENT");
        old.modify(newRule);
        repo.save(old);
        assertEquals(1, repo.listSaveHistory(newRule).size());
        
    }
    
    /** just make sure it works for at least one other asset type */
    public void testRuleSetWithFunction() {
        RuleSetDef def = new RuleSetDef("with functions", null);
        def.addFunction(new FunctionDef("abc", "123"));
        RepositoryManager repo = getRepo();
        repo.save(def);
        
        def = repo.loadRuleSet("with functions", 1);
        FunctionDef func = (FunctionDef) def.getFunctions().iterator().next();
        assertEquals("abc", func.getFunctionContent());
        
        //just make sure it preserves content, and same ID (now new versions).
        func.setFunctionContent("xyz");
        def.modify(func);
        Long id = func.getId();
        repo.save(def);
        def = repo.loadRuleSet("with functions", 1);
        func = (FunctionDef) def.getFunctions().iterator().next();
        assertEquals(id, func.getId());
        assertEquals("xyz", func.getFunctionContent());
        
        
    }
    
    public void testRuleSetNameList() {
        List list = getRepo().listRuleSets();
        assertTrue(list.size() > 0);        
    }
    
    public void testRuleSetSearchRuleTags() {
        RepositoryManager repo = getRepo();
        RuleSetDef def = new RuleSetDef("rules with tags", null);
        RuleDef rule = new RuleDef("search rule tags in set", "fdsfdsfds");
        rule.addTag("HR");
        def.addRule(rule);
        repo.save(def);
        List list = repo.searchRulesByTag("rules with tags", "HR");
        assertEquals(1, list.size());
        assertEquals("search rule tags in set", ((RuleDef) list.get(0)).getName());
    }
    
    public void testFindWorkingVersionInfo() {
        RuleSetDef ruleset = new RuleSetDef("nothing", null);
        RuleSetVersionInfo info = ruleset.getVersionInfoWorking();
        assertNotNull(info);
        assertEquals(1, info.getVersionNumber());
    }
    
    public void testAddRemoveCopyRules() {
        RuleSetDef ruleset = new RuleSetDef("another one", null);
        RepositoryManager repo = getRepo();
        
        RuleDef preExist = new RuleDef("preexist", "yeah");
        repo.save(preExist);
        repo.save(ruleset);
        
        ruleset.addRule(preExist);
        
        
        repo.save(ruleset);
        
        RuleSetDef newruleset = new RuleSetDef("yao", null);
        repo.save(ruleset);
        
        RuleDef copied = newruleset.addRule(preExist);
        RuleDef other = newruleset.findRuleByName(preExist.getName());
        assertEquals(null, other.getId()); //so we know it is a copy
        assertEquals(newruleset.getName(), other.getOwningRuleSetName());
        assertEquals(null, preExist.getOwningRuleSetName());
        
        repo.save(newruleset);
        
        assertFalse(other == preExist);
        assertEquals(other, copied);
        
        newruleset = repo.loadRuleSet("yao", 1);
        assertEquals(1, newruleset.getRules().size());
        other = (RuleDef) newruleset.getRules().iterator().next();
        assertEquals(newruleset.getName(), other.getOwningRuleSetName());
        
        newruleset.removeRule(other);
        repo.save(newruleset);
        
        newruleset = repo.loadRuleSet("yao", 1);
        ruleset = repo.loadRuleSet("another one", 1);
        assertEquals(1, ruleset.getRules().size());
        assertEquals(0, newruleset.getRules().size());
        
    }
    
    
//    
//    public void testLargeNumbers() {
//        RuleSetDef large = new RuleSetDef("Large1", null);
//        
//        System.out.println("Starting " + System.currentTimeMillis());
//        
//        for (int i = 0; i < 4000; i++) {
//            RuleDef def = new RuleDef("RuleNumber " + i, "Content");
//            def.addTag("HR" + i);
//            large.addRule(def);
//        }
//        RepositoryManager repo = RepositoryFactory.getStatefulRepository();
//        repo.save(large);
//        repo.close();
//        
//        repo = RepositoryFactory.getStatefulRepository();
//        System.out.println("Saved " + System.currentTimeMillis());
//        
//        large = repo.loadRuleSet("Large1", 1);
//        assertEquals(4000, large.getRules().size());
//        System.out.println("Loaded " + System.currentTimeMillis());
//        
//        List list = repo.findRulesByTag("HR1024");
//        assertEquals(1, list.size());
//        System.out.println("Searched " + System.currentTimeMillis());
//        
//        large.addTag("blah");
//        repo.save(large);
//        System.out.println("Change saved " + System.currentTimeMillis());
//        repo.close();
//        
//    }
    
}