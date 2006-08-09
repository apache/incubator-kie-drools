package org.drools.repository;

import org.drools.repository.db.IVersionable;

/**
 * Application data contains a definition of objects that may be provided to the
 * rule engine to support the execution of rules.
 * 
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class ApplicationDataDef extends Asset
    implements
    IVersionable {

    private static final long serialVersionUID = 609004561319545529L;

    private String            versionComment;
    private long              versionNumber;
    private String            identifier;
    private String            type;
    

    /**
     * @param identifier
     *            the id by which the app data will be referenced in a rule.
     * @param type
     *            The type of the object that will be bound to the identifier
     *            for the ruleset.
     */
    public ApplicationDataDef(String identifier,
                              String type) {
        this.identifier = identifier;
        this.type = type;
    }

    ApplicationDataDef() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public IVersionable copy() {
        ApplicationDataDef copy = new ApplicationDataDef( this.getIdentifier(),
                                                           this.getType() );
        copy.setLastSavedByUser(this.getLastSavedByUser());
        copy.setLastSavedDate(this.getLastSavedDate());
        return copy;
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

}
