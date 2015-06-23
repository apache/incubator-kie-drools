/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.query.jpa.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.jbpm.query.jpa.data.QueryWhere.ParameterType;
import org.kie.internal.query.QueryParameterIdentifiers;

/**
 * This object contains the following information: 
 * <ol> 
 * <li>The listId, which refers to the field that this criteria applies to<ul>
 *   <li>See {@link QueryParameterIdentifiers}</li></ul>
 * </li>
 * <li>The values of the criteria, which will be applied to the listId field<ul>
 *   <li>For example, it could be a list of numbers "1, 22, 3"</li></ul>
 * </li>
 * <li>Whether this is a union ("OR") or intersection ("AND") critieria</li>
 * <li>The type of criteria: normal, like (JPQL regex) or range</li>
 * <li>The grouping information of the phrase (see below)</li>
 * </ol>
 * </p>
 * With regard to the grouping information in this class, we treat JPQL/SQL as a "prefix" language here, which means that
 * this class represents the following regular expression/BNF string:
 * <pre>
 *   [)]{0,} [OR|AND] [(]{0,} &lt;CRITERIA&gt;
 * </pre> 
 * This structure is then represented by the following fields:
 * <pre>
 *   [endGroups] [union] [startGroupos] [values]
 * </pre>
 * 
 * The main reason to include the grouping status in this object is that other data structures (nested lists, etc) 
 * are much harder to de/serialize correctly. 
 */
@XmlRootElement
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(value="parameters")
public class QueryCriteria {

    @XmlAttribute
    private String listId;
    
    @XmlAttribute
    private Boolean union = null;
    
    @XmlAttribute
    private ParameterType type = ParameterType.NORMAL;
    
    @XmlElement(name="parameter")
    @JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
    // @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private List<Object> values;
   
    @XmlElement
    private List<QueryCriteria> criteria;
    
    public QueryCriteria() { 
        // default constructor
    }
    public QueryCriteria(String listId, ParameterType type) { 
        this.listId = listId;
        this.type = type;
    }

    public QueryCriteria(String listId, boolean union, ParameterType type) { 
        this(listId, type);
        this.union = union;
    }

    public QueryCriteria(String listId, boolean union, ParameterType type, int valueListSize) { 
        this(listId, union, type);
        this.values = new ArrayList<Object>(valueListSize);
    }
    
    public String getListId() {
        return listId;
    }

    public void setListId( String listId ) {
        this.listId = listId;
    }

    public Boolean isUnion() {
        return union;
    }

    public void setUnion( Boolean union ) {
        this.union = union;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType( ParameterType type ) {
        this.type = type;
    }

    public List<Object> getValues() {
        if( this.values == null ) { 
            this.values = new ArrayList<Object>();
        }
        return values;
    }
    
    public void setValues( List<Object> values ) {
        this.values = values;
    }
    
    // other methods
  
    public List<QueryCriteria> getCriteria() {
        if( this.criteria == null ) { 
            this.criteria = new ArrayList<QueryCriteria>();
        }
        return criteria;
    }
    public void setCriteria( List<QueryCriteria> criteria ) {
        this.criteria = criteria;
    }

    private static DatatypeFactory datatypeFactory;;
    static { 
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch( DatatypeConfigurationException e ) {
            System.out.println("Unable to instantiate a " + DatatypeFactory.class.getName() );
        }
    }
    
    /**
     * This method returns a list that should only be read
     */
    public List<Object> getParameters() {
        List<Object> values = getValues();
        if( values.isEmpty() ) { 
            return values;
        }
        List<Object> parameters = new ArrayList<Object>(values.size());
        for( Object obj : this.values ) { 
           parameters.add(convertSerializableVariantToObject(obj));
        }
        return parameters;
    }
    
    void addParameter( Object value ) { 
        Object xmlValue = convertObjectToSerializableVariant(value);
        getValues().add(xmlValue);
    }

    void setParameter( int index, Object value ) { 
        Object xmlValue = convertObjectToSerializableVariant(value);
        List<Object> values = getValues();
        while( values.size() <= index ) { 
           values.add(null); 
        }
        getValues().set(index, xmlValue);
    }

    void addCriteria( QueryCriteria criteria ) { 
       getCriteria().add(criteria);
    }

    private static Object convertObjectToSerializableVariant(Object obj) { 
        if( obj instanceof Date ) { 
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime((Date) obj);
            return datatypeFactory.newXMLGregorianCalendar(cal);
         } 
        return obj;
    }
  
    private static Object convertSerializableVariantToObject(Object obj) { 
       if( obj instanceof XMLGregorianCalendar ) { 
          return ((XMLGregorianCalendar) obj).toGregorianCalendar().getTime();
       }
       return obj;
    }
    
    public QueryCriteria(QueryCriteria queryCriteria) { 
        this.listId = queryCriteria.listId;
        this.union = queryCriteria.union;
        this.type = queryCriteria.type;
        this.values = new ArrayList<Object>(queryCriteria.getValues());
    }
   
    @Override
    public String toString() { 
       StringBuilder out = new StringBuilder();
       if( union != null ) { 
           out.append(union ? "OR" : "AND");
           out.append(" ");
       }
       out.append(listId);
       if( this.values != null && ! this.values.isEmpty() ) { 
           out.append(" =");
           if( type.equals(ParameterType.REGEXP) ) { 
               out.append("~");
           } 
           out.append(" ");
           if ( type.equals(ParameterType.RANGE) ) { 
               out.append("[");
           }
           out.append(this.values.get(0));
           for( int i = 1; i < this.values.size(); ++i ) { 
               out.append(", ") .append(this.values.get(i));
           }
           if ( type.equals(ParameterType.RANGE) ) { 
               out.append("]");
           }
       } 
       return out.toString();
    }
}
