/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.pmml_4_2.model.mining;

import static org.drools.core.command.runtime.pmml.PmmlConstants.DEFAULT_ROOT_PACKAGE;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.api.definition.type.PropertyReactive;
import org.dmg.pmml.pmml_4_2.descr.FIELDUSAGETYPE;
import org.dmg.pmml.pmml_4_2.descr.Segment;
import org.kie.pmml.pmml_4_2.PMML4Helper;
import org.kie.pmml.pmml_4_2.PMML4Model;
import org.kie.pmml.pmml_4_2.model.PMML4ModelFactory;
import org.kie.pmml.pmml_4_2.model.PMMLMiningField;

@PropertyReactive
public class MiningSegment implements Comparable<MiningSegment> {
    private static PMML4Helper pmmlHelper = new PMML4Helper();
    private String segmentId;
    private MiningSegmentation owner;
    private PredicateRuleProducer predicateRuleProducer;
    private PMML4Model internalModel;
    private String segmentRuleUnit;
    private int segmentIndex;
    private Double weight;
    
    public MiningSegment( MiningSegmentation owner, Segment segment, int segmentIndex) {
        this.owner = owner;
        
        this.internalModel = PMML4ModelFactory.getInstance().getModel(segment,owner);
        this.segmentId = segment.getId();
        this.segmentIndex = segmentIndex;
        this.weight = segment.getWeight();
        if (segment.getSimplePredicate() != null) {
            predicateRuleProducer = new SimpleSegmentPredicate(segment.getSimplePredicate());
        } else if (segment.getSimpleSetPredicate() != null) {
            predicateRuleProducer = new SimpleSetSegmentPredicate(segment.getSimpleSetPredicate());
        } else if (segment.getCompoundPredicate() != null) {
            predicateRuleProducer = new CompoundSegmentPredicate(segment.getCompoundPredicate());
        } else if (segment.getTrue() != null) {
            predicateRuleProducer = new BooleanSegmentPredicate(segment.getTrue());
        } else if (segment.getFalse() != null) {
            predicateRuleProducer = new BooleanSegmentPredicate(segment.getFalse());
        }
    }
    
    public PMML4Model getModel() {
        return this.internalModel;
    }
    
    public boolean checkForMiningFieldMapping() {
        List<PMMLMiningField> miningFields = this.internalModel.getMiningFields();
        for (PMMLMiningField field : miningFields) {
            if (!field.isInDictionary()) {
                System.out.println("must search for output named: "+field.getName());
            }
        }
        return false;
    }
    
    public List<String> getTargetsForWeighting() {
        return this.internalModel.getMiningFields().stream()
        .filter(mf -> mf.getFieldUsageType() == FIELDUSAGETYPE.TARGET || mf.getFieldUsageType() == FIELDUSAGETYPE.PREDICTED)
        .map(mf -> { return pmmlHelper.compactAsJavaId(mf.getName(), true); })
        .collect(Collectors.toList());
    }
    
    public String getTargetForWeighting() {
        List<String> targets = this.getTargetsForWeighting();
        return (targets != null && !targets.isEmpty()) ? targets.get(0):null;
    }
    
    public String getSegmentId() {
        if (this.segmentId == null || this.segmentId.trim().isEmpty()) {
            StringBuilder bldr = new StringBuilder(owner.getSegmentationId());
            bldr.append("Segment").append(this.segmentIndex);
            this.segmentId = bldr.toString();
        }
        return this.segmentId;
    }

    public MiningSegmentation getOwner() {
        return this.owner;
    }
    
    public PredicateRuleProducer getPredicateRuleProducer() {
        return this.predicateRuleProducer;
    }
    
    public String getPredicateText() {
        return this.predicateRuleProducer.getPredicateRule();
    }
    
    public String getSegmentPackageName() {
        StringBuilder builder = new StringBuilder(DEFAULT_ROOT_PACKAGE);
        builder.append(".mining.segment_").append(this.getSegmentId());
        return builder.toString();
    }
    
    public String getSegmentRuleUnit() {
        if (this.segmentRuleUnit == null || this.segmentRuleUnit.trim().isEmpty()) {
            this.segmentRuleUnit = this.getModel().getModelPackageName()+"."+this.getModel().getRuleUnitClassName();
        }
        return this.segmentRuleUnit;
    }
    
    public int getSegmentIndex() {
        return this.segmentIndex;
    }

    public boolean isAlwaysTrue() {
        return predicateRuleProducer.isAlwaysTrue();
    }
    
    public boolean isAlwaysFalse() {
        return predicateRuleProducer.isAlwaysFalse();
    }

    public PMML4Model getInternalModel() {
        return internalModel;
    }
    

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(MiningSegment ms) {
        if (ms.segmentIndex == this.segmentIndex) return 0;
        return (ms.segmentIndex > this.segmentIndex) ? 1:-1;
    }
    
}
