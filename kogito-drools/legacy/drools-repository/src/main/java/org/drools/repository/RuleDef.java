package org.drools.repository;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.repository.db.ISaveHistory;
import org.drools.repository.db.IVersionable;
import org.drools.repository.security.ACLResource;

public class RuleDef extends Asset
    implements
    ISaveHistory, ACLResource {

    private static final long serialVersionUID = -677781085801764266L;

    private String            name;
    private long              versionNumber;
    private String            content;
    private MetaData          metaData;
    private String            status;
    private boolean           checkedOut;
    private String            checkedOutBy;
    private String            versionComment;
    private Set               tags;
    private String            documentation;
    private Date              effectiveDate;
    private Date              expiryDate;
    private Long              historicalId;
    private boolean           historicalRecord = false;
    private int               lockingVersion = 0;
    private String            owningRuleSetName;


    /** 
     * If a rule belongs to a ruleset, this will have its name. 
     * Rulenames should be unique, but at a minimum unique in a ruleset.
     * */
    public String getOwningRuleSetName() {
        return owningRuleSetName;
    }

    void setOwningRuleSetName(String owningRuleSetName) {
        this.owningRuleSetName = owningRuleSetName;
    }

    /**
     * Use tagging to aid with searching and sorting of large numbers of rules.
     */
    public RuleDef addTag(String tag) {
        this.tags.add( new Tag( tag ) );
        return this;
    }

    public RuleDef addTag(Tag tag) {
        this.tags.add( tag );
        return this;
    }
    
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }
    
    public void removeTag(String tagVal) {
        Tag.removeTagFromCollection(tagVal, this.tags);
    }

    RuleDef() {
    }

    /**
     * This is for creating a brand new rule.
     * 
     * @param name
     *            Name of the MUST BE UNIQUE in the repository. The only time
     *            duplicate names exist are for different versions of rules.
     * @param content
     */
    public RuleDef(String name,
                   String content) {
        this.name = name;
        this.content = content;
        this.versionNumber = 1;
        this.tags = new HashSet();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    void setCheckedOutBy(String checkOutBy) {
        this.checkedOutBy = checkOutBy;
    }

    public String getVersionComment() {
        return versionComment;
    }

    public void setVersionComment(String versionComment) {
        this.versionComment = versionComment;
    }

    public long getVersionNumber() {
        return this.versionNumber;
    }

    public void setVersionNumber(long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public Set getTags() {
        return tags;
    }

    private void setTags(Set tags) {
        this.tags = tags;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    /**
     * Return a list of tags as Strings. Tags are stored as Tag objects, but are
     * essentially strings.
     */
    public String[] listTags() {
        String[] tagList = new String[tags.size()];
        int i = 0;
        for ( Iterator iter = tags.iterator(); iter.hasNext(); ) {
            Tag tag = (Tag) iter.next();
            tagList[i] = tag.getTag();
            i++;
        }
        return tagList;
    }

    private Set copyTags() {
        return Tag.copyTags(this.tags);
    }

    /**
     * This is used for versioning.
     */
    public IVersionable copy() {
        RuleDef newVersion = new RuleDef();
        newVersion.content = this.getContent();
        newVersion.documentation = this.getDocumentation();
        newVersion.effectiveDate = this.getEffectiveDate();
        newVersion.expiryDate = this.getExpiryDate();
        if ( this.metaData != null ) {
            newVersion.metaData = this.getMetaData().copy();
        }
        newVersion.name = this.getName();
        newVersion.status = this.getStatus();
        newVersion.tags = this.copyTags();
        newVersion.versionNumber = this.getVersionNumber();
        newVersion.versionComment = this.getVersionComment();
        newVersion.owningRuleSetName = this.getOwningRuleSetName();
        newVersion.setLastSavedByUser(this.getLastSavedByUser());
        newVersion.setLastSavedDate(this.getLastSavedDate());
        return newVersion;
    }

    public String toString() {
        return "{ id = " + this.getId() + " name = '" + this.name + "' version = " + this.getVersionNumber() + " }";
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

    private int getLockingVersion() {
        return lockingVersion;
    }

    private void setLockingVersion(int lockingVersion) {
        this.lockingVersion = lockingVersion;
    }

 

}
