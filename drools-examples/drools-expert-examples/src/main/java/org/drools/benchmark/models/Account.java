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
 *
 * A simple test bean that represents a generic account. It could be
 * a bank account, shopping card account, or any type of membership
 * account with a nationwide company.
 */
public class Account
    implements
    Serializable {

    private static final long serialVersionUID = 510l;

    protected String    first       = null;
    protected String    middle      = null;
    protected String    last        = null;
    /**
     * mr, mrs, ms, junior, etc
     */
    protected String    title       = null;
    protected String    accountId   = null;
    protected String    accountType = null;
    protected String    status      = null;
    protected String    username    = null;
    /**
     * this would represent the region of the office the account
     * was opened at.
     */
    protected String    regionCode  = null;
    /**
     * the code for the office where the account was opened.
     */
    protected String    officeCode  = null;
    protected String    areaCode    = null;
    protected String    exchange    = null;
    protected String    number      = null;
    protected String    ext         = null;

    protected ArrayList<PropertyChangeListener> listeners   = new ArrayList<PropertyChangeListener>();

    public Account() {
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

    public void setFirst(String val) {
        if ( !val.equals( this.first ) ) {
            String old = this.first;
            this.first = val;
            notifyListener( "first",
                            old,
                            this.first );
        }
    }

    public String getFirst() {
        return this.first;
    }

    public void setLast(String val) {
        if ( !val.equals( this.last ) ) {
            String old = this.last;
            this.last = val;
            notifyListener( "last",
                            old,
                            this.last );
        }
    }

    public String getLast() {
        return this.last;
    }

    public void setMiddle(String val) {
        if ( !val.equals( this.middle ) ) {
            String old = this.middle;
            this.middle = val;
            notifyListener( "middle",
                            old,
                            this.middle );
        }
    }

    public String getMiddle() {
        return this.middle;
    }

    public void setOfficeCode(String val) {
        if ( !val.equals( this.officeCode ) ) {
            String old = this.officeCode;
            this.officeCode = val;
            notifyListener( "officeCode",
                            old,
                            this.officeCode );
        }
    }

    public String getOfficeCode() {
        return this.officeCode;
    }

    public void setRegionCode(String val) {
        if ( !val.equals( this.regionCode ) ) {
            String old = this.regionCode;
            this.regionCode = val;
            notifyListener( "regionCode",
                            old,
                            this.regionCode );
        }
    }

    public String getRegionCode() {
        return this.regionCode;
    }

    public void setStatus(String val) {
        if ( !val.equals( this.status ) ) {
            String old = this.status;
            this.status = val;
            notifyListener( "status",
                            old,
                            this.status );
        }
    }

    public String getStatus() {
        return this.status;
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

    public void setAccountType(String val) {
        if ( !val.equals( this.accountType ) ) {
            String old = this.accountType;
            this.accountType = val;
            notifyListener( "accountType",
                            old,
                            this.accountType );
        }
    }

    public String getAccountType() {
        return this.accountType;
    }

    public void setUsername(String val) {
        if ( !val.equals( this.username ) ) {
            String old = this.username;
            this.username = val;
            notifyListener( "username",
                            old,
                            this.username );
        }
    }

    public String getUsername() {
        return this.username;
    }

    public String getAreaCode() {
        return this.areaCode;
    }

    public void setAreaCode(String val) {
        if ( !val.equals( this.areaCode ) ) {
            String old = this.areaCode;
            this.areaCode = val;
            notifyListener( "areaCode",
                            old,
                            this.areaCode );
        }
    }

    public String getExchange() {
        return this.exchange;
    }

    public void setExchange(String val) {
        if ( !val.equals( this.exchange ) ) {
            String old = this.exchange;
            this.exchange = val;
            notifyListener( "exchange",
                            old,
                            this.exchange );
        }
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String val) {
        if ( !val.equals( this.number ) ) {
            String old = this.number;
            this.number = val;
            notifyListener( "number",
                            old,
                            this.number );
        }
    }

    public String getExt() {
        return this.ext;
    }

    public void setExt(String val) {
        if ( !val.equals( this.ext ) ) {
            String old = this.ext;
            this.ext = val;
            notifyListener( "ext",
                            old,
                            this.ext );
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
