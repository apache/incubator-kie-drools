package org.drools.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;



/**
 * This represents a users tag for a rule, ruleset.
 * This aids with classification of rules in an ad-hoc fashion.
 * 
 * A tag is its own entity as tags should be shared as much as possible.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class Tag implements Serializable {


    private static final long serialVersionUID = 3830081066595663015L;

    private Long id;
    
    private String tag;

    public Tag(String tag) {
        this.tag = tag;
    }
    
    private Tag() {}

    public String getTag(){
        return tag;
    }

    public void setTag(String tag){
        this.tag = tag;
    }

    public String toString(){
        return tag;
    }

    /** 
     * Wrangles the tag out of the collection.
     * TODO: move all tags to maps rather then sets. Probably better.
     */
    static void removeTagFromCollection(String tagValue, Collection tags) {
        for ( Iterator iter = tags.iterator(); iter.hasNext(); ) {
            Tag tag = (Tag) iter.next();
            if (tag.getTag().equals(tagValue)) {
                iter.remove();
                return;
            }            
        }           
    }
    
    /**
     * Copy the tags as new instances.
     */
    static Set copyTags(Set originalSet) {
        Set newTags = new HashSet();
        for ( Iterator iter = originalSet.iterator(); iter.hasNext(); ) {
            Tag tag = (Tag) iter.next();
            newTags.add( new Tag( tag.getTag() ) );                        
        }
        return newTags;        
    }

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }
    
    
    
}
