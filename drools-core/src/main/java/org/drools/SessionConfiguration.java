/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Properties;

import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.util.ChainedProperties;
import org.drools.util.StringUtils;

/**
 * SessionConfiguration
 *
 * A class to store Session related configuration. It must be used at session instantiation time
 * or not used at all.
 * This class will automatically load default values from system properties, so if you want to set
 * a default configuration value for all your new sessions, you can simply set the property as
 * a System property.
 *
 * After the Session is created, it makes the configuration immutable and there is no way to make it
 * mutable again. This is to avoid inconsistent behavior inside session.
 *
 * NOTE: This API is under review and may change in the future.
 * 
 * 
 * drools.keepReference = <true|false>
 * drools.clockType = <pseudo|realtime|heartbeat|implicit>
 */
public class SessionConfiguration
    implements
    KnowledgeSessionConfiguration,
    Externalizable {
    private static final long serialVersionUID = 500L;

    private ChainedProperties chainedProperties;

    private volatile boolean  immutable;

    private boolean           keepReference;

    private ClockType         clockType;

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( chainedProperties );
        out.writeBoolean( immutable );
        out.writeBoolean( keepReference );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        chainedProperties = (ChainedProperties) in.readObject();
        immutable = in.readBoolean();
        keepReference = in.readBoolean();
    }

    /**
     * Creates a new session configuration using the provided properties
     * as configuration options. 
     *
     * @param properties
     */
    public SessionConfiguration(Properties properties) {
        init( properties );
    }

    /**
     * Creates a new session configuration with default configuration options.
     */
    public SessionConfiguration() {
        init( null );
    }

    private void init(Properties properties) {
        this.immutable = false;

        this.chainedProperties = new ChainedProperties( "rulebase.conf" );

        if ( properties != null ) {
            this.chainedProperties.addProperties( properties );
        }

        setKeepReference( Boolean.valueOf( this.chainedProperties.getProperty( "drools.keepReference",
                                                                               "true" ) ).booleanValue() );

        setClockType( ClockType.resolveClockType( this.chainedProperties.getProperty( "drools.clockType",
                                                                                      "realtime" ) ) );
    }
    
    public void setProperty(String name,
                            String value) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return;
        }
        
        if ( name.equals( "drools.keepReference" ) ) {
            setKeepReference(  StringUtils.isEmpty( value ) ? true : Boolean.parseBoolean( value ) );
        } else if ( name.equals( "drools.clockType" ) ) {
            setClockType( ClockType.resolveClockType(  StringUtils.isEmpty( value ) ? "realtime" : value ) );
        }
    }   
    
    public String getProperty(String name) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return null;
        }
        
        if ( name.equals( "drools.keepReference" ) ) {
            return Boolean.toString( this.keepReference );
        } else if ( name.equals( "drools.clockType" ) ) {
            return this.clockType.toExternalForm();
        }
        
        return null;
    } 

    /**
     * Makes the configuration object immutable. Once it becomes immutable,
     * there is no way to make it mutable again.
     * This is done to keep consistency.
     */
    public void makeImmutable() {
        this.immutable = true;
    }

    /**
     * Returns true if this configuration object is immutable or false otherwise.
     * @return
     */
    public boolean isImmutable() {
        return this.immutable;
    }

    private void checkCanChange() {
        if ( this.immutable ) {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }

    public void setKeepReference(boolean keepReference) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.keepReference = keepReference;
    }

    public boolean isKeepReference() {
        return this.keepReference;
    }

    public ClockType getClockType() {
        return clockType;
    }

    public void setClockType(ClockType clockType) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.clockType = clockType;
    }

}
