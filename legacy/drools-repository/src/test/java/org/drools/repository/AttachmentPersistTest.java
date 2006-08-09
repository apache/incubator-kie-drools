package org.drools.repository;

import java.util.List;

import org.drools.repository.db.PersistentCase;

public class AttachmentPersistTest extends PersistentCase {

    public void testLoadSave() {
        byte[] data = "test".getBytes();
        RuleSetAttachment at = new RuleSetAttachment("test","test", data, "blah.xml" );
        RepositoryManager repo = getRepo();
        at.addTag("RULESETAT");
        repo.save(at);
        RuleSetAttachment at2 = repo.loadAttachment("test", 1);
        assertEquals("test", at2.getTypeOfAttachment());
        assertEquals("RULESETAT", ((Tag)at2.getTags().iterator().next()).getTag());
        assertTrue((new String(data)).equals(new String(at2.getContent())) );
    }
    
    public void testHistory() {
        RuleSetAttachment at = new RuleSetAttachment("historical one", "histo", "test".getBytes(), "nothing.txt");
        RepositoryManager repo = getRepo();
        repo.save(at);
        
        assertEquals(0, repo.listSaveHistory(at).size());
        
        at.setContent("test2".getBytes());
        repo.save(at);
        
        List history = repo.listSaveHistory(at);
        assertEquals(1, history.size());
        
        
    }
    
}
