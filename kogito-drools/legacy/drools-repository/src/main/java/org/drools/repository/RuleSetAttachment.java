package org.drools.repository;

import java.util.HashSet;
import java.util.Set;

import org.drools.repository.db.ISaveHistory;
import org.drools.repository.db.IVersionable;
import org.drools.repository.security.ACLResource;


/**
 * A RuleSetAttachment may contain a ruleset that is stored in a non-normalised format.
 * An attachment may be a spreadsheet for instance. Or it may be a HTML document and a properties file.
 * It can even be a plain old DRL file.
 * 
 * Attachments can also be miscellanious files, such as test scripts or documentation. The deployer will 
 * use the typeOfAttachment property to work out what to do with it.
 * 
 * These are versioned along with the ruleset (with per save versioning as well).
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class RuleSetAttachment extends Asset implements ISaveHistory, ACLResource {


    private static final long serialVersionUID = 7474038734785975202L;    
    
    private byte[] content;  
    private String originalFileName;
    
    private String typeOfAttachment;
    
    private String name;
    private long versionNumber;
    private String versionComment;
    private Set               tags;
    private int              lockingVersion = 0;
    private boolean checkedOut;
    private String checkedOutBy; 
    private Long              historicalId;
    private boolean           historicalRecord = false;
    


    /**
     * 
     * @param typeOfAttachment The type of the content, eg XLS, CSV, HTML.
     * @param name The unique name of this attachment. Incorporate ruleset name if you like.
     * @param content The data in byte[] form. If it is text, use UTF-8 encoding.
     * @param originalFileName The original filename, if applicable. In some cases, people like
     * to think of things in terms of files. Feel free to include the pathname. 
     */
    public RuleSetAttachment(String typeOfAttachment,
                             String name, 
                             byte[] content, 
                             String originalFileName ) {
        super();       
        this.typeOfAttachment = typeOfAttachment;
        this.name = name;
        this.content = content;
        this.originalFileName = originalFileName;
        this.versionNumber = 1;
        this.versionComment = "new";
        this.tags = new HashSet();
        this.checkedOut = false;        
    }

    RuleSetAttachment() {
    }
    
    /**
     * Use tagging to aid with searching and sorting of large numbers of rules.
     */
    public RuleSetAttachment addTag(String tag) {
        this.tags.add( new Tag( tag ) );
        return this;
    }

    public RuleSetAttachment addTag(Tag tag) {
        this.tags.add( tag );
        return this;
    }
    
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }
    
    public void removeTag(String tagVal) {
        Tag.removeTagFromCollection(tagVal, this.tags);
    }    
    
    public String getName(){
        return name;
    }


    public void setName(String name){
        this.name = name;
    }


    public String getTypeOfAttachment(){
        return typeOfAttachment;
    }
    public void setTypeOfAttachment(String typeOfAttachment){
        this.typeOfAttachment = typeOfAttachment;
    }

    public long getVersionNumber(){
        return versionNumber;
    }

    public void setVersionNumber(long versionNumber){
        this.versionNumber = versionNumber;
    }

    public byte[] getContent(){
        return content;
    }

    public void setContent(byte[] content){
        this.content = content;
    }

    public String getOriginalFileName(){
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName){
        this.originalFileName = originalFileName;
    }
    
    public IVersionable copy() {
        RuleSetAttachment copy = new RuleSetAttachment(this.getTypeOfAttachment(), 
                                                       this.getName(), 
                                                       this.getContent(), 
                                                       this.getOriginalFileName());
        copy.setTags(Tag.copyTags(this.tags));
        copy.setLastSavedByUser(this.getLastSavedByUser());
        copy.setLastSavedDate(this.getLastSavedDate());
        return copy;
    }

    public void setVersionComment(String comment){
        this.versionComment = comment;        
    }
    
    public String getVersionComment() {
        return this.versionComment;
    }

    public Set getTags() {
        return tags;
    }

    private void setTags(Set tags) {
        this.tags = tags;
    }
    
    private int getLockingVersion() {
        return lockingVersion;
    }

    private void setLockingVersion(int lockingVersion) {
        this.lockingVersion = lockingVersion;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }

    void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }

    public String getCheckedOutBy() {
        return checkedOutBy;
    }

    void setCheckedOutBy(String checkedOutBy) {
        this.checkedOutBy = checkedOutBy;
    }    
    
    public Long getHistoricalId() {
        return historicalId;
    }

    public void setHistoricalId(Long historicalId) {
        this.historicalId = historicalId;
    }

    public boolean isHistoricalRecord() {
        return historicalRecord;
    }

    public void setHistoricalRecord(boolean historicalRecord) {
        this.historicalRecord = historicalRecord;
    }
    
    
}
