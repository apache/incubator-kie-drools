package org.drools.brms.client.modeldriven.brxml;

/**
 * This class is the parent for field setting or assertion actions.
 * 
 * Contains the list of fields and their values to be set.
 * 
 * @author Michael Neale
 *
 */
public abstract class ActionFieldList
    implements
    IAction {

    public ActionFieldValue[] fieldValues = new ActionFieldValue[0];

    public void removeField(int idx) {
        //Unfortunately, this is kinda duplicate code with other methods, 
        //but with typed arrays, and GWT, its not really possible to do anything "better" 
        //at this point in time. 
        ActionFieldValue[] newList = new ActionFieldValue[fieldValues.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < fieldValues.length; i++ ) {
            
            if (i != idx) {
                newList[newIdx] = fieldValues[i];
                newIdx++;
            }
            
        }
        this.fieldValues = newList;        
    }
    
    public void addFieldValue(ActionFieldValue val) {
        if (fieldValues == null) {
            fieldValues = new ActionFieldValue[1];            
            fieldValues[0] = val;            
        } else {
            ActionFieldValue[] newList = new ActionFieldValue[fieldValues.length + 1];
            for ( int i = 0; i < fieldValues.length; i++ ) {            
                newList[i] = fieldValues[i];
            }
            newList[fieldValues.length] = val;
            fieldValues = newList;
        }        
    }    
    
}
