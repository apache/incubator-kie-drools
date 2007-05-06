/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0.5</a>, using an XML
 * Schema.
 * $Id$
 */

package org.drools.testing.core.beans;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class TestSuite.
 * 
 * @version $Revision$ $Date$
 */
public class TestSuite extends org.drools.rule.Package 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _description
     */
    private java.lang.String _description;

    /**
     * Field _fileName
     */
    private java.lang.String _fileName;

    /**
     * Field _scenarioList
     */
    private java.util.List _scenarioList;


      //----------------/
     //- Constructors -/
    //----------------/

    public TestSuite() 
     {
        super();
        this._scenarioList = new java.util.ArrayList();
    } //-- org.drools.testing.core.beans.TestSuite()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vScenario
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addScenario(org.drools.testing.core.beans.Scenario vScenario)
        throws java.lang.IndexOutOfBoundsException
    {
        this._scenarioList.add(vScenario);
    } //-- void addScenario(org.drools.testing.core.beans.Scenario) 

    /**
     * 
     * 
     * @param index
     * @param vScenario
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addScenario(int index, org.drools.testing.core.beans.Scenario vScenario)
        throws java.lang.IndexOutOfBoundsException
    {
        this._scenarioList.add(index, vScenario);
    } //-- void addScenario(int, org.drools.testing.core.beans.Scenario) 

    /**
     * Method enumerateScenario
     * 
     * 
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration enumerateScenario()
    {
        return java.util.Collections.enumeration(this._scenarioList);
    } //-- java.util.Enumeration enumerateScenario() 

    /**
     * Returns the value of field 'description'.
     * 
     * @return the value of field 'Description'.
     */
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
     * Returns the value of field 'fileName'.
     * 
     * @return the value of field 'FileName'.
     */
    public java.lang.String getFileName()
    {
        return this._fileName;
    } //-- java.lang.String getFileName() 

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
     * Method getScenario
     * 
     * 
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.drools.testing.core.beans.Scenario at the given index
     */
    public org.drools.testing.core.beans.Scenario getScenario(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._scenarioList.size()) {
            throw new IndexOutOfBoundsException("getScenario: Index value '" + index + "' not in range [0.." + (this._scenarioList.size() - 1) + "]");
        }
        
        return (org.drools.testing.core.beans.Scenario) _scenarioList.get(index);
    } //-- org.drools.testing.core.beans.Scenario getScenario(int) 

    /**
     * Method getScenario
     * 
     * 
     * 
     * @return this collection as an Array
     */
    public org.drools.testing.core.beans.Scenario[] getScenario()
    {
        int size = this._scenarioList.size();
        org.drools.testing.core.beans.Scenario[] array = new org.drools.testing.core.beans.Scenario[size];
        for (int index = 0; index < size; index++){
            array[index] = (org.drools.testing.core.beans.Scenario) _scenarioList.get(index);
        }
        
        return array;
    } //-- org.drools.testing.core.beans.Scenario[] getScenario() 

    /**
     * Method getScenarioCount
     * 
     * 
     * 
     * @return the size of this collection
     */
    public int getScenarioCount()
    {
        return this._scenarioList.size();
    } //-- int getScenarioCount() 

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
     * Method iterateScenario
     * 
     * 
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator iterateScenario()
    {
        return this._scenarioList.iterator();
    } //-- java.util.Iterator iterateScenario() 

    /**
     * 
     * 
     * @param out
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * 
     * @param handler
     * @throws java.io.IOException if an IOException occurs during
     * marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     */
    public void removeAllScenario()
    {
        this._scenarioList.clear();
    } //-- void removeAllScenario() 

    /**
     * Method removeScenario
     * 
     * 
     * 
     * @param vScenario
     * @return true if the object was removed from the collection.
     */
    public boolean removeScenario(org.drools.testing.core.beans.Scenario vScenario)
    {
        boolean removed = _scenarioList.remove(vScenario);
        return removed;
    } //-- boolean removeScenario(org.drools.testing.core.beans.Scenario) 

    /**
     * Method removeScenarioAt
     * 
     * 
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.drools.testing.core.beans.Scenario removeScenarioAt(int index)
    {
        Object obj = this._scenarioList.remove(index);
        return (org.drools.testing.core.beans.Scenario) obj;
    } //-- org.drools.testing.core.beans.Scenario removeScenarioAt(int) 

    /**
     * Sets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * Sets the value of field 'fileName'.
     * 
     * @param fileName the value of field 'fileName'.
     */
    public void setFileName(java.lang.String fileName)
    {
        this._fileName = fileName;
    } //-- void setFileName(java.lang.String) 

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
     * @param vScenario
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setScenario(int index, org.drools.testing.core.beans.Scenario vScenario)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._scenarioList.size()) {
            throw new IndexOutOfBoundsException("setScenario: Index value '" + index + "' not in range [0.." + (this._scenarioList.size() - 1) + "]");
        }
        
        this._scenarioList.set(index, vScenario);
    } //-- void setScenario(int, org.drools.testing.core.beans.Scenario) 

    /**
     * 
     * 
     * @param vScenarioArray
     */
    public void setScenario(org.drools.testing.core.beans.Scenario[] vScenarioArray)
    {
        //-- copy array
        _scenarioList.clear();
        
        for (int i = 0; i < vScenarioArray.length; i++) {
                this._scenarioList.add(vScenarioArray[i]);
        }
    } //-- void setScenario(org.drools.testing.core.beans.Scenario) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled
     * org.drools.testing.core.beans.TestSuite
     */
    public static org.drools.testing.core.beans.TestSuite unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.drools.testing.core.beans.TestSuite) Unmarshaller.unmarshal(org.drools.testing.core.beans.TestSuite.class, reader);
    } //-- org.drools.testing.core.beans.TestSuite unmarshal(java.io.Reader) 

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
