/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.query.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kie.internal.jaxb.StringKeyObjectValueMapXmlAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated // see org.jbpm.query.jpa.data.QueryWhere and org.jbpm.query.jpa.data.QueryCriteria
public class QueryParameters {

    @XmlTransient
    private boolean union = true;
    
    @XmlTransient
    private boolean like = false;
    
    @XmlTransient
    private boolean range = false;
   
    @XmlElement
    @XmlJavaTypeAdapter(StringKeyObjectValueMapXmlAdapter.class)
    private Map<String, List<? extends Object>> unionParameters = null;
    
    @XmlElement
    @XmlJavaTypeAdapter(StringKeyObjectValueMapXmlAdapter.class)
    private Map<String, List<? extends Object>> intersectParameters = null;
    
    @XmlElement
    @XmlJavaTypeAdapter(StringKeyObjectValueMapXmlAdapter.class)
    private Map<String, List<String>> unionRegexParameters = null;
    
    @XmlElement
    @XmlJavaTypeAdapter(StringKeyObjectValueMapXmlAdapter.class)
    private Map<String, List<String>> intersectRegexParameters = null;
    
    @XmlElement
    @XmlJavaTypeAdapter(StringKeyObjectValueMapXmlAdapter.class)
    private Map<String, List<? extends Object>> unionRangeParameters = null;
    
    @XmlElement
    @XmlJavaTypeAdapter(StringKeyObjectValueMapXmlAdapter.class)
    private Map<String, List<? extends Object>> intersectRangeParameters = null;

    public QueryParameters() { 
        // JAXB constructor
    }
    
    // getters 
    
    public Map<String, List<? extends Object>> getUnionParameters() {
        if( unionParameters == null ) {
            unionParameters = new HashMap<String, List<? extends Object>>();
        }
        return unionParameters;
    }
    
    public boolean unionParametersAreEmpty() {
        return unionParameters == null ? true : unionParameters.isEmpty();
                
    }

    public Map<String, List<? extends Object>> getIntersectParameters() {
        if( intersectParameters == null ) {
            intersectParameters = new HashMap<String, List<? extends Object>>();
        }
        return intersectParameters;
    }

    public boolean intersectParametersAreEmpty() {
        return intersectParameters == null ? true : intersectParameters.isEmpty();
    }

    public Map<String, List<String>> getUnionRegexParameters() {
        if( unionRegexParameters == null ) { 
           unionRegexParameters = new HashMap<String, List<String>>();
        }
        return unionRegexParameters;
    }

    public boolean unionRegexParametersAreEmpty() {
        return unionRegexParameters == null ? true : unionRegexParameters.isEmpty();
    }

    public Map<String, List<String>> getIntersectRegexParameters() {
        if( intersectRegexParameters == null ) {
           intersectRegexParameters = new HashMap<String, List<String>>();
        }
        return intersectRegexParameters;
    }

    public boolean intersectRegexParametersAreEmpty() {
        return intersectRegexParameters == null ? true : intersectRegexParameters.isEmpty();
    }

    public Map<String, List<? extends Object>> getUnionRangeParameters() {
        if( unionRangeParameters == null ) { 
           unionRangeParameters = new HashMap<String, List<? extends Object>>();
        }
        return unionRangeParameters;
    }

    public boolean unionRangeParametersAreEmpty() {
        return unionRangeParameters == null ? true : unionRangeParameters.isEmpty();
    }

    public Map<String, List<? extends Object>> getIntersectRangeParameters() {
        if( intersectRangeParameters == null ) {
           intersectRangeParameters = new HashMap<String, List<? extends Object>>();
        }
        return intersectRangeParameters;
    }

    public boolean intersectRangeParametersAreEmpty() {
        return intersectRangeParameters == null ? true : intersectRangeParameters.isEmpty();
    }

    // other logic
    
    public <T> void addAppropriateParam( String listId, T... param ) {
        if( param.length == 0 ) {
            return;
        }
        List<T> listParams = getAppropriateParamList(listId, param[0], param.length);
        listParams.addAll(Arrays.asList(param));
    }

    public <T> void addRangeParameter( String listId, T param, boolean start ) {
        this.range = true;
        List<T> listParams = getAppropriateParamList(listId, param, 2);
        int index = start ? 0 : 1;
        listParams.set(index, param);
        this.range = false;
    }
    
    @SuppressWarnings("unchecked")
    public <T> List<T> getAppropriateParamList(String listId, T param, int size) { 
        List<T> listParams = null;
        if( like ) { 
            if( ! (param instanceof String) ) { 
               throw new IllegalArgumentException("Only String parameters may be used in regular expressions.");
            }
            List<String> stringListParams = null;
            if( union ) {
                listParams = (List<T>) getUnionRegexParameters().get(listId);
                if( listParams == null ) {
                    stringListParams = new ArrayList<String>(size);
                    getUnionRegexParameters().put(listId, stringListParams);
                }
            } else {
                listParams = (List<T>) getIntersectParameters().get(listId);
                if( listParams == null ) {
                    stringListParams = new ArrayList<String>(size);
                    getIntersectRegexParameters().put(listId, stringListParams);
                }
            } 
            if( listParams == null ) { 
               return (List<T>) stringListParams; 
            } else { 
                return listParams;
            }
        } else if( range ) { 
            if( union ) {
                listParams = (List<T>) getUnionRangeParameters().get(listId);
                if( listParams == null ) {
                    listParams = Arrays.asList(null, null);
                    getUnionRangeParameters().put(listId, listParams);
                }
            } else {
                listParams = (List<T>) getIntersectRangeParameters().get(listId);
                if( listParams == null ) {
                    listParams = Arrays.asList(null, null);
                    getIntersectRangeParameters().put(listId, listParams);
                }
            } 
        } else { 
            if( union ) {
                listParams = (List<T>) getUnionParameters().get(listId);
                if( listParams == null ) {
                    listParams = new ArrayList<T>(size);
                    getUnionParameters().put(listId, listParams);
                }
            } else {
                listParams = (List<T>) getIntersectParameters().get(listId);
                if( listParams == null ) {
                    listParams = new ArrayList<T>(size);
                    getIntersectParameters().put(listId, listParams);
                }
            }
        }
        return listParams;
    }
    
    public void setToUnion() {
        this.union = true;
    }
    
    public void setToIntersection() {
        this.union = false;
    }

    public boolean isUnion() { 
       return this.union;
    }

    public void setToLike() {
        this.like = true;
    }

    public boolean isLike() {
        return this.like;
    }

    public void setToEquals() {
        this.like = false;
    }

    public void setToRange() {
        this.range = true;
    }

    public void setToPrecise() {
        this.range = false;
    }
    
    public boolean isRange() {
        return this.range;
    }

    public void clear() { 
        union = true;
        like = false;
        range = false;
       
        Map [] maps = { 
                unionParameters, 
                intersectParameters,
                unionRegexParameters,
                intersectRegexParameters,
                unionRangeParameters,
                intersectRangeParameters 
        };
        for( Map paramMap : maps ) { 
            if( paramMap != null ) { 
                paramMap.clear();
            }
        }
    }
    
    public QueryParameters(QueryParameters queryParameters) { 
       this.union = queryParameters.union;
       this.like = queryParameters.like;
       this.range = queryParameters.range;
       this.intersectParameters = queryParameters.intersectParameters == null ? null : 
               new HashMap<String, List<? extends Object>>(queryParameters.intersectParameters);
       this.unionParameters = queryParameters.unionParameters == null ? null : 
               new HashMap<String, List<? extends Object>>(queryParameters.unionParameters);
       this.intersectRangeParameters = queryParameters.intersectRangeParameters == null ? null : 
               new HashMap<String, List<? extends Object>>(queryParameters.intersectRangeParameters);
       this.unionRangeParameters = queryParameters.unionRangeParameters == null ? null : 
               new HashMap<String, List<? extends Object>>(queryParameters.unionRangeParameters);
       this.intersectRegexParameters = queryParameters.intersectRegexParameters == null ? null : 
               new HashMap<String, List<String>>(queryParameters.intersectRegexParameters);
       this.unionRegexParameters = queryParameters.unionRegexParameters == null ? null : 
               new HashMap<String, List<String>>(queryParameters.unionRegexParameters);
    }

    
}