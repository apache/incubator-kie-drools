package org.drools.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.drools.repository.db.PersistentCase;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class LocalStoreTest extends PersistentCase {

    public void testXStream() {
        RuleSetDef def = getRuleSet();
        XStream xstream = new XStream(new DomDriver());
        String xml = xstream.toXML(def);
        //System.out.println(xml);
        
        def = (RuleSetDef) xstream.fromXML(xml);

        RepositoryManager repo = getRepo();
        repo.save(def);
        
        def = repo.loadRuleSet("xstream1", 1);
        xml = xstream.toXML(def);
        //System.out.println(xml);
        
        def = (RuleSetDef) xstream.fromXML(xml);
        def.addRule( new RuleDef("xstream2", "xxxx"));
        repo.save(def);
        
        assertNotNull(def);
        
    }
    
    /** 
     * The aim of this is to create a ruledef in one session, persist and then load
     * edit and save in a different session.
     */
    public void testMultiSession() {
        RuleSetDef def1 = new RuleSetDef("multiSess", new MetaData());
        def1.addApplicationData(new ApplicationDataDef("xx", "abc"));
        def1.addRule(new RuleDef("multis1", "abc"));
        
        RepositoryManager repo = RepositoryFactory.getStatefulRepository();
        repo.save(def1);
        repo.close();
        
        LocalStore store = new LocalStore();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        store.save(def1, out);
        
        def1 = (RuleSetDef) store.load(new ByteArrayInputStream(out.toByteArray()));
        def1.addRule(new RuleDef("multi12", "njkfdkj"));
        def1.addAttachment(new RuleSetAttachment("dsl", "nothing", "yeah".getBytes(), "nil"));
        def1.createNewVersion("new version");
        out = new ByteArrayOutputStream();
        store.save(def1, out);
        
        def1 = (RuleSetDef) store.load(new ByteArrayInputStream(out.toByteArray()));
        
        repo = RepositoryFactory.getStatefulRepository();
        repo.save(def1);
        repo.close();
        
        repo = RepositoryFactory.getStatefulRepository();
        RuleSetDef def2 = repo.loadRuleSet("multiSess", 2);
        assertEquals(1, def2.getAttachments().size());
        assertEquals(2, def2.getWorkingVersionNumber());
        repo.close();
        
        
    }
    
    
    public void testLocalStoreXstream() {
        runSaveLoad(new LocalStore());
    }
    
    public void testLocalStoreObjectSerialization() {
        runSaveLoad(new LocalStore(LocalStore.MODE_OBJECT_SER));
    }



    private void runSaveLoad(LocalStore local) {
        
        RuleSetDef set = getRuleSet();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        local.save(set, out);

        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Asset result = local.load(in);
        assertNotNull(result);
        
        RuleSetDef loaded = (RuleSetDef) result;
        assertEquals(set.getName(), loaded.getName());
        assertEquals(set.getRules().size(), loaded.getRules().size());
        assertTrue(System.identityHashCode(set) != System.identityHashCode(loaded));
    }
    

    private RuleSetDef getRuleSet() {
        RuleSetDef def = new RuleSetDef("xstream1", new MetaData());
        def.addRule(new RuleDef("rulex1", "ndklsanlkdsan"));
        return def;
    }
    
    
    
}
