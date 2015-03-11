/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.guided.scorecard.shared;

import java.util.ArrayList;
import java.util.List;

public class Characteristic {

    private String fact;
    private String field;
    private double baselineScore;
    private String reasonCode;
    private List<Attribute> attributes = new ArrayList<Attribute>();
    private String name;
    private String dataType;

    public Characteristic() {
    }

    public String getDataType() {
        return dataType;
    }

    public String getName() {
        return name;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes( final List<Attribute> attributes ) {
        this.attributes = attributes;
    }

    public String getFact() {
        return fact;
    }

    public void setFact( final String fact ) {
        this.fact = fact;
    }

    public String getField() {
        return field;
    }

    public void setField( final String field ) {
        this.field = field;
    }

    public double getBaselineScore() {
        return baselineScore;
    }

    public void setBaselineScore( final double baselineScore ) {
        this.baselineScore = baselineScore;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode( final String reasonCode ) {
        this.reasonCode = reasonCode;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public void setDataType( final String dataType ) {
        this.dataType = dataType;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Characteristic ) ) {
            return false;
        }

        Characteristic that = (Characteristic) o;

        if ( Double.compare( that.baselineScore, baselineScore ) != 0 ) {
            return false;
        }
        if ( attributes != null ? !attributes.equals( that.attributes ) : that.attributes != null ) {
            return false;
        }
        if ( dataType != null ? !dataType.equals( that.dataType ) : that.dataType != null ) {
            return false;
        }
        if ( fact != null ? !fact.equals( that.fact ) : that.fact != null ) {
            return false;
        }
        if ( field != null ? !field.equals( that.field ) : that.field != null ) {
            return false;
        }
        if ( name != null ? !name.equals( that.name ) : that.name != null ) {
            return false;
        }
        if ( reasonCode != null ? !reasonCode.equals( that.reasonCode ) : that.reasonCode != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = fact != null ? fact.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( field != null ? field.hashCode() : 0 );
        result = ~~result;
        temp = Double.doubleToLongBits( baselineScore );
        result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
        result = ~~result;
        result = 31 * result + ( reasonCode != null ? reasonCode.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( attributes != null ? attributes.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( dataType != null ? dataType.hashCode() : 0 );
        result = ~~result;
        return result;
    }

}
