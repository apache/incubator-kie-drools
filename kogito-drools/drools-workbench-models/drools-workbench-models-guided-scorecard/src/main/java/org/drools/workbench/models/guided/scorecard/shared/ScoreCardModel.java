/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import org.drools.workbench.models.datamodel.imports.HasImports;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.packages.HasPackageName;

public class ScoreCardModel implements HasImports,
                                       HasPackageName {

    private String name;
    private double baselineScore;
    private double initialScore;
    private boolean useReasonCodes;
    private String factName = "";
    private String fieldName = "";
    private String reasonCodesAlgorithm = "";
    private String reasonCodeField = "";

    private List<Characteristic> characteristics = new ArrayList<Characteristic>();
    private String packageName;

    private Imports imports = new Imports();

    private String agendaGroup = "";
    private String ruleFlowGroup = "";

    public ScoreCardModel() {
    }

    public String getReasonCodeField() {
        return reasonCodeField;
    }

    public void setReasonCodeField( final String reasonCodeField ) {
        this.reasonCodeField = reasonCodeField;
    }

    public String getFactName() {
        return factName;
    }

    public void setFactName( final String factName ) {
        this.factName = factName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName( final String fieldName ) {
        this.fieldName = fieldName;
    }

    public double getInitialScore() {
        return initialScore;
    }

    public void setInitialScore( final double initialScore ) {
        this.initialScore = initialScore;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getReasonCodesAlgorithm() {
        return reasonCodesAlgorithm;
    }

    public void setReasonCodesAlgorithm( final String reasonCodesAlgorithm ) {
        this.reasonCodesAlgorithm = reasonCodesAlgorithm;
    }

    public double getBaselineScore() {
        return baselineScore;
    }

    public void setBaselineScore( final double baselineScore ) {
        this.baselineScore = baselineScore;
    }

    public boolean isUseReasonCodes() {
        return useReasonCodes;
    }

    public void setUseReasonCodes( final boolean useReasonCodes ) {
        this.useReasonCodes = useReasonCodes;
    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics( final List<Characteristic> characteristics ) {
        this.characteristics = characteristics;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void setPackageName( final String packageName ) {
        this.packageName = packageName;
    }

    @Override
    public Imports getImports() {
        return imports;
    }

    @Override
    public void setImports( final Imports imports ) {
        this.imports = imports;
    }

    public String getRuleFlowGroup() {
        return ruleFlowGroup;
    }

    public void setRuleFlowGroup( String ruleFlowGroup ) {
        this.ruleFlowGroup = ruleFlowGroup;
    }

    public String getAgendaGroup() {
        return agendaGroup;
    }

    public void setAgendaGroup( String agendaGroup ) {
        this.agendaGroup = agendaGroup;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ScoreCardModel ) ) {
            return false;
        }

        ScoreCardModel that = (ScoreCardModel) o;

        if ( Double.compare( that.baselineScore, baselineScore ) != 0 ) {
            return false;
        }
        if ( Double.compare( that.initialScore, initialScore ) != 0 ) {
            return false;
        }
        if ( useReasonCodes != that.useReasonCodes ) {
            return false;
        }
        if ( agendaGroup != null ? !agendaGroup.equals( that.agendaGroup ) : that.agendaGroup != null ) {
            return false;
        }
        if ( characteristics != null ? !characteristics.equals( that.characteristics ) : that.characteristics != null ) {
            return false;
        }
        if ( factName != null ? !factName.equals( that.factName ) : that.factName != null ) {
            return false;
        }
        if ( fieldName != null ? !fieldName.equals( that.fieldName ) : that.fieldName != null ) {
            return false;
        }
        if ( imports != null ? !imports.equals( that.imports ) : that.imports != null ) {
            return false;
        }
        if ( name != null ? !name.equals( that.name ) : that.name != null ) {
            return false;
        }
        if ( packageName != null ? !packageName.equals( that.packageName ) : that.packageName != null ) {
            return false;
        }
        if ( reasonCodeField != null ? !reasonCodeField.equals( that.reasonCodeField ) : that.reasonCodeField != null ) {
            return false;
        }
        if ( reasonCodesAlgorithm != null ? !reasonCodesAlgorithm.equals( that.reasonCodesAlgorithm ) : that.reasonCodesAlgorithm != null ) {
            return false;
        }
        if ( ruleFlowGroup != null ? !ruleFlowGroup.equals( that.ruleFlowGroup ) : that.ruleFlowGroup != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( reasonCodesAlgorithm != null ? reasonCodesAlgorithm.hashCode() : 0 );
        result = ~~result;
        temp = Double.doubleToLongBits( baselineScore );
        result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
        result = ~~result;
        temp = Double.doubleToLongBits( initialScore );
        result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
        result = ~~result;
        result = 31 * result + ( useReasonCodes ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( factName != null ? factName.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( fieldName != null ? fieldName.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( reasonCodeField != null ? reasonCodeField.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( characteristics != null ? characteristics.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( packageName != null ? packageName.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( imports != null ? imports.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( agendaGroup != null ? agendaGroup.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( ruleFlowGroup != null ? ruleFlowGroup.hashCode() : 0 );
        result = ~~result;
        return result;
    }

}
