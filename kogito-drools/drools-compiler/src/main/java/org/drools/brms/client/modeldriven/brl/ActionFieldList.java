package org.drools.brms.client.modeldriven.brl;

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

    public void removeField(final int idx) {
        //Unfortunately, this is kinda duplicate code with other methods, 
        //but with typed arrays, and GWT, its not really possible to do anything "better" 
        //at this point in time. 
        final ActionFieldValue[] newList = new ActionFieldValue[this.fieldValues.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < this.fieldValues.length; i++ ) {

            if ( i != idx ) {
                newList[newIdx] = this.fieldValues[i];
                newIdx++;
            }

        }
        this.fieldValues = newList;
    }

    public void addFieldValue(final ActionFieldValue val) {
        if ( this.fieldValues == null ) {
            this.fieldValues = new ActionFieldValue[1];
            this.fieldValues[0] = val;
        } else {
            final ActionFieldValue[] newList = new ActionFieldValue[this.fieldValues.length + 1];
            for ( int i = 0; i < this.fieldValues.length; i++ ) {
                newList[i] = this.fieldValues[i];
            }
            newList[this.fieldValues.length] = val;
            this.fieldValues = newList;
        }
    }

}
