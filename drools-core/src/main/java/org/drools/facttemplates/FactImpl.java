/*
 * Copyright 2002-2006 Peter Lin & RuleML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.drools.facttemplates;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


//import woolfel.engine.rule.Rule;

/**
 * @author Peter Lin
 * 
 * Deffact is a concrete implementation of Fact interface. It is
 * equivalent to deffact in CLIPS.
 */
public class FactImpl implements Fact, Serializable {

    private static int hashCode(Object[] array) {
        final int PRIME = 31;
        if ( array == null ) return 0;
        int result = 1;
        for ( int index = 0; index < array.length; index++ ) {
            result = PRIME * result + (array[index] == null ? 0 : array[index].hashCode());
        }
        return result;
    }

    private FactTemplate factTemplate = null;
    private Object[] values = null;
    private int hashCode;
    
    /**
     * the Fact id must be unique, since we use it for the indexes
     */
    private long id;
    
    /**
     * this is the default constructor
     * @param instance
     * @param values
     */
    public FactImpl(FactTemplate template, Object[] values, long id){
        this.factTemplate = template;
        this.values= values;
        this.id = id;
    }

    public FactImpl(FactTemplate template, long id){
        this.factTemplate = template;
        this.values= new Object[ template.getNumberOfFields() ];
        this.id = id;
    }    
    
    /**
     * Method returns the value of the given slot at the
     * id.
     * @param id
     * @return
     */
    public Object getFieldValue(int index){
        return this.values[ index ];
    }
    
     

    /**
     * Return the long factId
     */
    public long getFactId(){
        return this.id;
    }

    /**
     * this is used to reset the id, in the event an user tries to
     * assert the same fact again, we reset the id to the existing one.
     * @param fact
     */
    protected void resetId(Fact fact) {
    	this.id = fact.getFactId();
    }
        
    /**
     * Return the deftemplate for the fact
     */
    public FactTemplate getFactTemplate(){
        return this.factTemplate;
    }       
    
    public int hashCode() {
        if ( hashCode == 0 )  {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + this.factTemplate.hashCode();
            result = PRIME * result + FactImpl.hashCode( this.values );
            result = PRIME * result + (int) (this.id ^ (this.id >>> 32));
            hashCode = result;
            
        }
        return this.hashCode;
    }

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }
        
        if ( object == null || FactImpl.class != object.getClass() ) {
            return false;
        }
        
        final FactImpl other = ( FactImpl ) object;

        if ( !this.factTemplate.equals( other.factTemplate ) ) {
            return false;
        }
        
        if ( !Arrays.equals( this.values,
                             other.values ) ) {
            return false;
        }
        
        return true;
    }    
}
