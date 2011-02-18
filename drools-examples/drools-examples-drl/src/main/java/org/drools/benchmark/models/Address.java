/*
 * Copyright 2010 JBoss Inc
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

package org.drools.benchmark.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Peter Lin
 *
 * A simple address class that represents a house a person owns.
 */
public class Address
    implements
    Serializable {

	private static final long serialVersionUID = 510l;

	protected String    title      = null;
    protected String    accountId  = null;
    protected String    street     = null;
    protected String    street2    = null;
    protected String    status     = null;
    protected String    city       = null;
    protected String    state      = null;
    protected String    postalCode = null;
    protected String    houseType  = null;
    protected String    country    = null;

    protected ArrayList<PropertyChangeListener> listeners  = new ArrayList<PropertyChangeListener>();

    /**
     * 
     */
    public Address() {
        super();
    }

    public void setTitle(String val) {
        if ( !val.equals( this.title ) ) {
            String old = this.title;
            this.title = val;
            notifyListener( "title",
                            old,
                            this.title );
        }
    }

    public String getTitle() {
        return this.title;
    }

    public void setState(String val) {
        if ( !val.equals( this.state ) ) {
            String old = this.state;
            this.state = val;
            notifyListener( "officeCode",
                            old,
                            this.state );
        }
    }

    public String getState() {
        return this.state;
    }

    public void setCity(String val) {
        if ( !val.equals( this.city ) ) {
            String old = this.city;
            this.city = val;
            notifyListener( "regionCode",
                            old,
                            this.city );
        }
    }

    public String getCity() {
        return this.city;
    }

    public void setStreet2(String val) {
        if ( !val.equals( this.street2 ) ) {
            String old = this.street2;
            this.street2 = val;
            notifyListener( "status",
                            old,
                            this.street2 );
        }
    }

    public String getStreet2() {
        return this.street2;
    }

    public void setAccountId(String val) {
        if ( !val.equals( this.accountId ) ) {
            String old = this.accountId;
            this.accountId = val;
            notifyListener( "accountId",
                            old,
                            this.accountId );
        }
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setStreet(String val) {
        if ( !val.equals( this.street ) ) {
            String old = this.street;
            this.street = val;
            notifyListener( "accountType",
                            old,
                            this.street );
        }
    }

    public String getStreet() {
        return this.street;
    }

    public void setStatus(String val) {
        if ( !val.equals( this.status ) ) {
            String old = this.status;
            this.status = val;
            notifyListener( "username",
                            old,
                            this.status );
        }
    }

    public String getStatus() {
        return this.status;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String val) {
        if ( !val.equals( this.postalCode ) ) {
            String old = this.postalCode;
            this.postalCode = val;
            notifyListener( "areaCode",
                            old,
                            this.postalCode );
        }
    }

    public String getHouseType() {
        return this.houseType;
    }

    public void setHouseType(String val) {
        if ( !val.equals( this.houseType ) ) {
            String old = this.houseType;
            this.houseType = val;
            notifyListener( "exchange",
                            old,
                            this.houseType );
        }
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String val) {
        if ( !val.equals( this.country ) ) {
            String old = this.country;
            this.country = val;
            notifyListener( "number",
                            old,
                            this.country );
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.add( listener );
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.remove( listener );
    }

    protected void notifyListener(String field,
                                  Object oldValue,
                                  Object newValue) {
        if ( listeners == null || listeners.size() == 0 ) {
            return;
        } else {
            PropertyChangeEvent event = new PropertyChangeEvent( this,
                                                                 field,
                                                                 oldValue,
                                                                 newValue );

            for ( int i = 0; i < listeners.size(); i++ ) {
                ((java.beans.PropertyChangeListener) listeners.get( i )).propertyChange( event );
            }
        }

    }
}
