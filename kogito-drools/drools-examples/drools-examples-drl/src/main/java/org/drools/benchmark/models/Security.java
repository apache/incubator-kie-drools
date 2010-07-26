/**
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
 */
public class Security
    implements
    Serializable {

	private static final long serialVersionUID = 1L;
	
	protected String    countryCode = null;
    protected double    currentPrice;
    protected int       cusip;
    protected String    exchange    = null;
    protected int       industryGroupID;
    protected int       industryID;
    protected String    issuer      = null;
    protected double    lastPrice;
    protected int       sectorID;
    protected int       subIndustryID;

    protected ArrayList<PropertyChangeListener> listeners   = new ArrayList<PropertyChangeListener>();

    public Security() {
        super();
    }

    public void setCountryCode(String code) {
        if ( !code.equals( this.countryCode ) ) {
            String old = this.countryCode;
            this.countryCode = code;
            this.notifyListener( "countryCode",
                                 old,
                                 this.countryCode );
        }
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCurrentPrice(double price) {
        if ( price != this.currentPrice ) {
            Double old = new Double( this.currentPrice );
            this.currentPrice = price;
            this.notifyListener( "currentPrice",
                                 old,
                                 new Double( this.currentPrice ) );
        }
    }

    public double getCurrentPrice() {
        return this.currentPrice;
    }

    public void setCusip(int value) {
        if ( value != this.cusip ) {
            Integer old = new Integer( this.cusip );
            this.cusip = value;
            this.notifyListener( "cusip",
                                 old,
                                 new Integer( this.cusip ) );
        }
    }

    public int getCusip() {
        return this.cusip;
    }

    public void setExchange(String exc) {
        if ( !exc.equals( this.exchange ) ) {
            String old = this.exchange;
            this.exchange = exc;
            this.notifyListener( "exchange",
                                 old,
                                 this.exchange );
        }
    }

    public String getExchange() {
        return this.exchange;
    }

    public void setIndustryGroupID(int id) {
        if ( id != this.industryGroupID ) {
            int old = this.industryGroupID;
            this.industryGroupID = id;
            this.notifyListener( "industryGroupID",
                                 new Integer( old ),
                                 new Integer( this.industryGroupID ) );
        }
    }

    public int getIndustryGroupID() {
        return this.industryGroupID;
    }

    public void setIndustryID(int id) {
        if ( id != this.industryID ) {
            int old = this.industryID;
            this.industryID = id;
            this.notifyListener( "industryID",
                                 new Integer( old ),
                                 new Integer( this.industryID ) );
        }
    }

    public int getIndustryID() {
        return this.industryID;
    }

    public void setIssuer(String name) {
        if ( !name.equals( this.issuer ) ) {
            String old = this.issuer;
            this.issuer = name;
            this.notifyListener( "issuer",
                                 old,
                                 this.issuer );
        }
    }

    public String getIssuer() {
        return this.issuer;
    }

    public void setLastPrice(double price) {
        if ( price != this.lastPrice ) {
            Double old = new Double( this.lastPrice );
            this.lastPrice = price;
            this.notifyListener( "lastPrice",
                                 old,
                                 new Double( this.lastPrice ) );
        }
    }

    public double getLastPrice() {
        return this.lastPrice;
    }

    public void setSectorID(int id) {
        if ( id != this.sectorID ) {
            int old = this.sectorID;
            this.sectorID = id;
            this.notifyListener( "sectorID",
                                 new Integer( old ),
                                 new Integer( this.sectorID ) );
        }
    }

    public int getSectorID() {
        return this.sectorID;
    }

    public void setSubIndustryID(int id) {
        if ( id != this.subIndustryID ) {
            int old = this.subIndustryID;
            this.subIndustryID = id;
            this.notifyListener( "subIndustryID",
                                 new Integer( old ),
                                 new Integer( this.subIndustryID ) );
        }
    }

    public int getSubIndustryID() {
        return this.subIndustryID;
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
