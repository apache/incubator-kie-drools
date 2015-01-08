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

public class Attribute {

    private String value;
    private double partialScore;
    private String reasonCode;
    private String operator;

    public Attribute() {
        this.reasonCode = "";
        this.operator = "";
        this.partialScore = 0.0;
        this.value = "";
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator( final String operator ) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue( final String value ) {
        this.value = value;
    }

    public double getPartialScore() {
        return partialScore;
    }

    public void setPartialScore( final double partialScore ) {
        this.partialScore = partialScore;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode( final String reasonCode ) {
        this.reasonCode = reasonCode;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Attribute ) ) {
            return false;
        }

        Attribute attribute = (Attribute) o;

        if ( Double.compare( attribute.partialScore, partialScore ) != 0 ) {
            return false;
        }
        if ( operator != null ? !operator.equals( attribute.operator ) : attribute.operator != null ) {
            return false;
        }
        if ( reasonCode != null ? !reasonCode.equals( attribute.reasonCode ) : attribute.reasonCode != null ) {
            return false;
        }
        if ( value != null ? !value.equals( attribute.value ) : attribute.value != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = value != null ? value.hashCode() : 0;
        temp = Double.doubleToLongBits( partialScore );
        result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
        result = 31 * result + ( reasonCode != null ? reasonCode.hashCode() : 0 );
        result = 31 * result + ( operator != null ? operator.hashCode() : 0 );
        return result;
    }
}
