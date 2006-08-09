package org.drools.repository.db;

/** 
 * Assets that implement this will have their history saved.
 * Is partly a marker interface for persistence. 
 */
public interface ISaveHistory extends IVersionable {

    /** 
     * This indicates the ID the the original asset was saved with. Allows 
     * a history list to be queried.
     */
    Long getHistoricalId();    
    void setHistoricalId(Long id);
    
    /** 
     * @return True is the object is actually a save history record.
     */
    boolean isHistoricalRecord();
    void setHistoricalRecord(boolean b);
    

    
}
