/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0.5</a>, using an XML
 * Schema.
 * $Id$
 */

package org.drools.testing.core.beans;

/**
 * Class FactType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class FactType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id
     */
    private int _id;

    /**
     * keeps track of state for field: _id
     */
    private boolean _has_id;

    /**
     * Field _type
     */
    private java.lang.String _type;

    /**
     * Field _fieldList
     */
    private java.util.List _fieldList;


      //----------------/
     //- Constructors -/
    //----------------/

    public FactType() 
     {
        super();
        this._fieldList = new java.util.ArrayList();
    } //-- org.drools.testing.core.beans.FactType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vField
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addField(org.drools.testing.core.beans.Field vField)
        throws java.lang.IndexOutOfBoundsException
    {
        this._fieldList.add(vField);
    } //-- void addField(org.drools.testing.core.beans.Field) 

    /**
     * 
     * 
     * @param index
     * @param vField
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addField(int index, org.drools.testing.core.beans.Field vField)
        throws java.lang.IndexOutOfBoundsException
    {
        this._fieldList.add(index, vField);
    } //-- void addField(int, org.drools.testing.core.beans.Field) 

    /**
     */
    public void deleteId()
    {
        this._has_id= false;
    } //-- void deleteId() 

    /**
     * Method enumerateField
     * 
     * 
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration enumerateField()
    {
        return java.util.Collections.enumeration(this._fieldList);
    } //-- java.util.Enumeration enumerateField() 

    /**
     * Method getField
     * 
     * 
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the org.drools.testing.core.beans.Field
     * at the given index
     */
    public org.drools.testing.core.beans.Field getField(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._fieldList.size()) {
            throw new IndexOutOfBoundsException("getField: Index value '" + index + "' not in range [0.." + (this._fieldList.size() - 1) + "]");
        }
        
        return (org.drools.testing.core.beans.Field) _fieldList.get(index);
    } //-- org.drools.testing.core.beans.Field getField(int) 

    /**
     * Method getField
     * 
     * 
     * 
     * @return this collection as an Array
     */
    public org.drools.testing.core.beans.Field[] getField()
    {
        int size = this._fieldList.size();
        org.drools.testing.core.beans.Field[] array = new org.drools.testing.core.beans.Field[size];
        for (int index = 0; index < size; index++){
            array[index] = (org.drools.testing.core.beans.Field) _fieldList.get(index);
        }
        
        return array;
    } //-- org.drools.testing.core.beans.Field[] getField() 

    /**
     * Method getFieldCount
     * 
     * 
     * 
     * @return the size of this collection
     */
    public int getFieldCount()
    {
        return this._fieldList.size();
    } //-- int getFieldCount() 

    /**
     * Returns the value of field 'id'.
     * 
     * @return the value of field 'Id'.
     */
    public int getId()
    {
        return this._id;
    } //-- int getId() 

    /**
     * Returns the value of field 'type'.
     * 
     * @return the value of field 'Type'.
     */
    public java.lang.String getType()
    {
        return this._type;
    } //-- java.lang.String getType() 

    /**
     * Method hasId
     * 
     * 
     * 
     * @return true if at least one Id has been added
     */
    public boolean hasId()
    {
        return this._has_id;
    } //-- boolean hasId() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return true if this object is valid according to the schema
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method iterateField
     * 
     * 
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator iterateField()
    {
        return this._fieldList.iterator();
    } //-- java.util.Iterator iterateField() 

    /**
     */
    public void removeAllField()
    {
        this._fieldList.clear();
    } //-- void removeAllField() 

    /**
     * Method removeField
     * 
     * 
     * 
     * @param vField
     * @return true if the object was removed from the collection.
     */
    public boolean removeField(org.drools.testing.core.beans.Field vField)
    {
        boolean removed = _fieldList.remove(vField);
        return removed;
    } //-- boolean removeField(org.drools.testing.core.beans.Field) 

    /**
     * Method removeFieldAt
     * 
     * 
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.drools.testing.core.beans.Field removeFieldAt(int index)
    {
        Object obj = this._fieldList.remove(index);
        return (org.drools.testing.core.beans.Field) obj;
    } //-- org.drools.testing.core.beans.Field removeFieldAt(int) 

    /**
     * 
     * 
     * @param index
     * @param vField
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setField(int index, org.drools.testing.core.beans.Field vField)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._fieldList.size()) {
            throw new IndexOutOfBoundsException("setField: Index value '" + index + "' not in range [0.." + (this._fieldList.size() - 1) + "]");
        }
        
        this._fieldList.set(index, vField);
    } //-- void setField(int, org.drools.testing.core.beans.Field) 

    /**
     * 
     * 
     * @param vFieldArray
     */
    public void setField(org.drools.testing.core.beans.Field[] vFieldArray)
    {
        //-- copy array
        _fieldList.clear();
        
        for (int i = 0; i < vFieldArray.length; i++) {
                this._fieldList.add(vFieldArray[i]);
        }
    } //-- void setField(org.drools.testing.core.beans.Field) 

    /**
     * Sets the value of field 'id'.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(int id)
    {
        this._id = id;
        this._has_id = true;
    } //-- void setId(int) 

    /**
     * Sets the value of field 'type'.
     * 
     * @param type the value of field 'type'.
     */
    public void setType(java.lang.String type)
    {
        this._type = type;
    } //-- void setType(java.lang.String) 

    /**
     * 
     * 
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
