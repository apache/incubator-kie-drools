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

import org.drools.workbench.models.datamodel.imports.HasImports;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.packages.HasPackageName;

public class ScoreCardModel implements HasImports,
                                       HasPackageName {

    private String name;
    private String reasonCodesAlgorithm;
    private double baselineScore;
    private double initialScore;
    private boolean useReasonCodes;
    private String factName = "";
    private String fieldName = "";
    private String reasonCodeField = "";

    private List<Characteristic> characteristics = new ArrayList<Characteristic>();
    private String packageName;

    private Imports imports = new Imports();

    private String agendaGroup="";
    private String ruleFlowGroup="";

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

    public void setRuleFlowGroup(String ruleFlowGroup) {
        this.ruleFlowGroup = ruleFlowGroup;
    }

    public String getAgendaGroup() {
        return agendaGroup;
    }

    public void setAgendaGroup(String agendaGroup) {
        this.agendaGroup = agendaGroup;
    }
}
