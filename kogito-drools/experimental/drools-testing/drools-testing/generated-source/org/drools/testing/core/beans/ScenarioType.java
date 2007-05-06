/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0.5</a>, using an XML
 * Schema.
 * $Id$
 */

package org.drools.testing.core.beans;

/**
 * Class ScenarioType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class ScenarioType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _factList
     */
    private java.util.List _factList;

    /**
     * Field _ruleList
     */
    private java.util.List _ruleList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ScenarioType() 
     {
        super();
        this._factList = new java.util.ArrayList();
        this._ruleList = new java.util.ArrayList();
    } //-- org.drools.testing.core.beans.ScenarioType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vFact
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addFact(org.drools.testing.core.beans.Fact vFact)
        throws java.lang.IndexOutOfBoundsException
    {
        this._factList.add(vFact);
    } //-- void addFact(org.drools.testing.core.beans.Fact) 

    /**
     * 
     * 
     * @param index
     * @param vFact
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addFact(int index, org.drools.testing.core.beans.Fact vFact)
        throws java.lang.IndexOutOfBoundsException
    {
        this._factList.add(index, vFact);
    } //-- void addFact(int, org.drools.testing.core.beans.Fact) 

    /**
     * 
     * 
     * @param vRule
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addRule(org.drools.testing.core.beans.Rule vRule)
        throws java.lang.IndexOutOfBoundsException
    {
        this._ruleList.add(vRule);
    } //-- void addRule(org.drools.testing.core.beans.Rule) 

    /**
     * 
     * 
     * @param index
     * @param vRule
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addRule(int index, org.drools.testing.core.beans.Rule vRule)
        throws java.lang.IndexOutOfBoundsException
    {
        this._ruleList.add(index, vRule);
    } //-- void addRule(int, org.drools.testing.core.beans.Rule) 

    /**
     * Method enumerateFact
     * 
     * 
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration enumerateFact()
    {
        return java.util.Collections.enumeration(this._factList);
    } //-- java.util.Enumeration enumerateFact() 

    /**
     * Method enumerateRule
     * 
     * 
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration enumerateRule()
    {
        return java.util.Collections.enumeration(this._ruleList);
    } //-- java.util.Enumeration enumerateRule() 

    /**
     * Method getFact
     * 
     * 
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the org.drools.testing.core.beans.Fact
     * at the given index
     */
    public org.drools.testing.core.beans.Fact getFact(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._factList.size()) {
            throw new IndexOutOfBoundsException("getFact: Index value '" + index + "' not in range [0.." + (this._factList.size() - 1) + "]");
        }
        
        return (org.drools.testing.core.beans.Fact) _factList.get(index);
    } //-- org.drools.testing.core.beans.Fact getFact(int) 

    /**
     * Method getFact
     * 
     * 
     * 
     * @return this collection as an Array
     */
    public org.drools.testing.core.beans.Fact[] getFact()
    {
        int size = this._factList.size();
        org.drools.testing.core.beans.Fact[] array = new org.drools.testing.core.beans.Fact[size];
        for (int index = 0; index < size; index++){
            array[index] = (org.drools.testing.core.beans.Fact) _factList.get(index);
        }
        
        return array;
    } //-- org.drools.testing.core.beans.Fact[] getFact() 

    /**
     * Method getFactCount
     * 
     * 
     * 
     * @return the size of this collection
     */
    public int getFactCount()
    {
        return this._factList.size();
    } //-- int getFactCount() 

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
     * Method getRule
     * 
     * 
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the org.drools.testing.core.beans.Rule
     * at the given index
     */
    public org.drools.testing.core.beans.Rule getRule(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._ruleList.size()) {
            throw new IndexOutOfBoundsException("getRule: Index value '" + index + "' not in range [0.." + (this._ruleList.size() - 1) + "]");
        }
        
        return (org.drools.testing.core.beans.Rule) _ruleList.get(index);
    } //-- org.drools.testing.core.beans.Rule getRule(int) 

    /**
     * Method getRule
     * 
     * 
     * 
     * @return this collection as an Array
     */
    public org.drools.testing.core.beans.Rule[] getRule()
    {
        int size = this._ruleList.size();
        org.drools.testing.core.beans.Rule[] array = new org.drools.testing.core.beans.Rule[size];
        for (int index = 0; index < size; index++){
            array[index] = (org.drools.testing.core.beans.Rule) _ruleList.get(index);
        }
        
        return array;
    } //-- org.drools.testing.core.beans.Rule[] getRule() 

    /**
     * Method getRuleCount
     * 
     * 
     * 
     * @return the size of this collection
     */
    public int getRuleCount()
    {
        return this._ruleList.size();
    } //-- int getRuleCount() 

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
     * Method iterateFact
     * 
     * 
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator iterateFact()
    {
        return this._factList.iterator();
    } //-- java.util.Iterator iterateFact() 

    /**
     * Method iterateRule
     * 
     * 
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator iterateRule()
    {
        return this._ruleList.iterator();
    } //-- java.util.Iterator iterateRule() 

    /**
     */
    public void removeAllFact()
    {
        this._factList.clear();
    } //-- void removeAllFact() 

    /**
     */
    public void removeAllRule()
    {
        this._ruleList.clear();
    } //-- void removeAllRule() 

    /**
     * Method removeFact
     * 
     * 
     * 
     * @param vFact
     * @return true if the object was removed from the collection.
     */
    public boolean removeFact(org.drools.testing.core.beans.Fact vFact)
    {
        boolean removed = _factList.remove(vFact);
        return removed;
    } //-- boolean removeFact(org.drools.testing.core.beans.Fact) 

    /**
     * Method removeFactAt
     * 
     * 
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.drools.testing.core.beans.Fact removeFactAt(int index)
    {
        Object obj = this._factList.remove(index);
        return (org.drools.testing.core.beans.Fact) obj;
    } //-- org.drools.testing.core.beans.Fact removeFactAt(int) 

    /**
     * Method removeRule
     * 
     * 
     * 
     * @param vRule
     * @return true if the object was removed from the collection.
     */
    public boolean removeRule(org.drools.testing.core.beans.Rule vRule)
    {
        boolean removed = _ruleList.remove(vRule);
        return removed;
    } //-- boolean removeRule(org.drools.testing.core.beans.Rule) 

    /**
     * Method removeRuleAt
     * 
     * 
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.drools.testing.core.beans.Rule removeRuleAt(int index)
    {
        Object obj = this._ruleList.remove(index);
        return (org.drools.testing.core.beans.Rule) obj;
    } //-- org.drools.testing.core.beans.Rule removeRuleAt(int) 

    /**
     * 
     * 
     * @param index
     * @param vFact
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setFact(int index, org.drools.testing.core.beans.Fact vFact)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._factList.size()) {
            throw new IndexOutOfBoundsException("setFact: Index value '" + index + "' not in range [0.." + (this._factList.size() - 1) + "]");
        }
        
        this._factList.set(index, vFact);
    } //-- void setFact(int, org.drools.testing.core.beans.Fact) 

    /**
     * 
     * 
     * @param vFactArray
     */
    public void setFact(org.drools.testing.core.beans.Fact[] vFactArray)
    {
        //-- copy array
        _factList.clear();
        
        for (int i = 0; i < vFactArray.length; i++) {
                this._factList.add(vFactArray[i]);
        }
    } //-- void setFact(org.drools.testing.core.beans.Fact) 

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
     * @param vRule
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setRule(int index, org.drools.testing.core.beans.Rule vRule)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._ruleList.size()) {
            throw new IndexOutOfBoundsException("setRule: Index value '" + index + "' not in range [0.." + (this._ruleList.size() - 1) + "]");
        }
        
        this._ruleList.set(index, vRule);
    } //-- void setRule(int, org.drools.testing.core.beans.Rule) 

    /**
     * 
     * 
     * @param vRuleArray
     */
    public void setRule(org.drools.testing.core.beans.Rule[] vRuleArray)
    {
        //-- copy array
        _ruleList.clear();
        
        for (int i = 0; i < vRuleArray.length; i++) {
                this._ruleList.add(vRuleArray[i]);
        }
    } //-- void setRule(org.drools.testing.core.beans.Rule) 

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
