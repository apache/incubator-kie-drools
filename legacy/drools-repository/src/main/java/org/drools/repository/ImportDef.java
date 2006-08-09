package org.drools.repository;

import org.drools.repository.db.IVersionable;

/**
 * This holds a type import for a ruleset. 
 * TODO: This probably does not need to be versioned this granular.
 * A simple Import collection object, which is versionable, would do fine
 * (same argument for ApplicationDataDef). 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class ImportDef extends Asset
    implements
    IVersionable {
    
    private static final long serialVersionUID = -5509795356918241886L;
    
    private long versionNumber;
    private String versionComment;
    private String type;
    
    /**
     * @param type The type to import into the ruleset.
     */
    public ImportDef(String type) {
        this.type = type;
    }
    
    ImportDef() {}
    
    /** The type to import into the ruleset */
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getVersionComment() {
        return versionComment;
    }
    public void setVersionComment(String versionComment) {
        this.versionComment = versionComment;
    }
    public long getVersionNumber() {
        return versionNumber;
    }
    public void setVersionNumber(long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public IVersionable copy() {
        ImportDef copy = new ImportDef(this.getType());
        copy.setLastSavedByUser(this.getLastSavedByUser());
        copy.setLastSavedDate(this.getLastSavedDate());
        return copy;
    }
    
    


}
