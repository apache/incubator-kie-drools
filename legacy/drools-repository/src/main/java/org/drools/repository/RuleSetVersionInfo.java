package org.drools.repository;

import java.util.Date;


/**
 * This records information about a particular version of a ruleset.
 * Rulesets themselves are just a collection of rules.
 * Rules themselves are versioned, and rulesets pull together particular a common set of rule versions.
 * 
 * Typically this co-incides with a release of a ruleset.
 * Once a new version is created, all the rules are cloned, and the new version number applied. 
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class RuleSetVersionInfo extends Asset implements Comparable {


    private static final long serialVersionUID = 53728327711138178L;
    
    private Date createdOn = new Date();
    private long versionNumber;
    private String versionComment;
    private String status;
    
    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public RuleSetVersionInfo(long versionNumber,
                              String versionComment){
        super();
        this.versionNumber = versionNumber;
        this.versionComment = versionComment;
    }
    
    RuleSetVersionInfo() {}
    
    public Date getCreatedOn(){
        return createdOn;
    }
    public void setCreatedOn(Date createdOn){
        this.createdOn = createdOn;
    }
    public String getVersionComment(){
        return versionComment;
    }
    public void setVersionComment(String versionComment){
        this.versionComment = versionComment;
    }
    public long getVersionNumber(){
        return versionNumber;
    }
    public void setVersionNumber(long versionNumber){
        this.versionNumber = versionNumber;
    }

    /** The version number provides the natural ordering */
    public int compareTo(Object arg){
        if (arg instanceof RuleSetVersionInfo) {
            RuleSetVersionInfo info = (RuleSetVersionInfo) arg;
            if (info.versionNumber == this.versionNumber ) {
                return 0; 
            } else if (info.versionNumber < this.versionNumber) {
                return -1;
            } else {
                return 1;
            }
        }
        return 0;
    }
    
    public String toString() {
        return "{ versionComment=" + this.versionComment + " versionNumber=" + this.versionNumber + " }";
    }
    
    
    
}
