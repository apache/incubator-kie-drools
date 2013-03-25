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

package org.drools.guvnor.models.guided.scorecard.shared;

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
}
