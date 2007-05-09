/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0.5</a>, using an XML
 * Schema.
 * $Id$
 */

package org.drools.testing.core.beans;

/**
 * Class RuleType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class RuleType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _fire
     */
    private boolean _fire;

    /**
     * keeps track of state for field: _fire
     */
    private boolean _has_fire;

    /**
     * Field _resultList
     */
    private java.util.List _resultList;


      //----------------/
     //- Constructors -/
    //----------------/

    public RuleType() 
     {
        super();
        this._resultList = new java.util.ArrayList();
    } //-- org.drools.testing.core.beans.RuleType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addResult(org.drools.testing.core.beans.Result vResult)
        throws java.lang.IndexOutOfBoundsException
    {
        this._resultList.add(vResult);
    } //-- void addResult(org.drools.testing.core.beans.Result) 

    /**
     * 
     * 
     * @param index
     * @param vResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addResult(int index, org.drools.testing.core.beans.Result vResult)
        throws java.lang.IndexOutOfBoundsException
    {
        this._resultList.add(index, vResult);
    } //-- void addResult(int, org.drools.testing.core.beans.Result) 

    /**
     */
    public void deleteFire()
    {
        this._has_fire= false;
    } //-- void deleteFire() 

    /**
     * Method enumerateResult
     * 
     * 
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration enumerateResult()
    {
        return java.util.Collections.enumeration(this._resultList);
    } //-- java.util.Enumeration enumerateResult() 

    /**
     * Returns the value of field 'fire'.
     * 
     * @return the value of field 'Fire'.
     */
    public boolean getFire()
    {
        return this._fire;
    } //-- boolean getFire() 

    /**
     * Returns the value of field 'name'.
     * 
     * @return the value of field 'Name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Method getResult
     * 
     * 
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.drools.testing.core.beans.Result at the given index
     */
    public org.drools.testing.core.beans.Result getResult(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._resultList.size()) {
            throw new IndexOutOfBoundsException("getResult: Index value '" + index + "' not in range [0.." + (this._resultList.size() - 1) + "]");
        }
        
        return (org.drools.testing.core.beans.Result) _resultList.get(index);
    } //-- org.drools.testing.core.beans.Result getResult(int) 

    /**
     * Method getResult
     * 
     * 
     * 
     * @return this collection as an Array
     */
    public org.drools.testing.core.beans.Result[] getResult()
    {
        int size = this._resultList.size();
        org.drools.testing.core.beans.Result[] array = new org.drools.testing.core.beans.Result[size];
        for (int index = 0; index < size; index++){
            array[index] = (org.drools.testing.core.beans.Result) _resultList.get(index);
        }
        
        return array;
    } //-- org.drools.testing.core.beans.Result[] getResult() 

    /**
     * Method getResultCount
     * 
     * 
     * 
     * @return the size of this collection
     */
    public int getResultCount()
    {
        return this._resultList.size();
    } //-- int getResultCount() 

    /**
     * Method hasFire
     * 
     * 
     * 
     * @return true if at least one Fire has been added
     */
    public boolean hasFire()
    {
        return this._has_fire;
    } //-- boolean hasFire() 

    /**
     * Returns the value of field 'fire'.
     * 
     * @return the value of field 'Fire'.
     */
    public boolean isFire()
    {
        return this._fire;
    } //-- boolean isFire() 

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
     * Method iterateResult
     * 
     * 
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator iterateResult()
    {
        return this._resultList.iterator();
    } //-- java.util.Iterator iterateResult() 

    /**
     */
    public void removeAllResult()
    {
        this._resultList.clear();
    } //-- void removeAllResult() 

    /**
     * Method removeResult
     * 
     * 
     * 
     * @param vResult
     * @return true if the object was removed from the collection.
     */
    public boolean removeResult(org.drools.testing.core.beans.Result vResult)
    {
        boolean removed = _resultList.remove(vResult);
        return removed;
    } //-- boolean removeResult(org.drools.testing.core.beans.Result) 

    /**
     * Method removeResultAt
     * 
     * 
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.drools.testing.core.beans.Result removeResultAt(int index)
    {
        Object obj = this._resultList.remove(index);
        return (org.drools.testing.core.beans.Result) obj;
    } //-- org.drools.testing.core.beans.Result removeResultAt(int) 

    /**
     * Sets the value of field 'fire'.
     * 
     * @param fire the value of field 'fire'.
     */
    public void setFire(boolean fire)
    {
        this._fire = fire;
        this._has_fire = true;
    } //-- void setFire(boolean) 

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * 
     * 
     * @param index
     * @param vResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setResult(int index, org.drools.testing.core.beans.Result vResult)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._resultList.size()) {
            throw new IndexOutOfBoundsException("setResult: Index value '" + index + "' not in range [0.." + (this._resultList.size() - 1) + "]");
        }
        
        this._resultList.set(index, vResult);
    } //-- void setResult(int, org.drools.testing.core.beans.Result) 

    /**
     * 
     * 
     * @param vResultArray
     */
    public void setResult(org.drools.testing.core.beans.Result[] vResultArray)
    {
        //-- copy array
        _resultList.clear();
        
        for (int i = 0; i < vResultArray.length; i++) {
                this._resultList.add(vResultArray[i]);
        }
    } //-- void setResult(org.drools.testing.core.beans.Result) 

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
