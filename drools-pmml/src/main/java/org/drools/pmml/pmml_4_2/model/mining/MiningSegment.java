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
package org.drools.pmml.pmml_4_2.model.mining;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.dmg.pmml.pmml_4_2.descr.Segment;
import org.drools.pmml.pmml_4_2.PMML4Compiler;
import org.drools.pmml.pmml_4_2.PMML4Model;
import org.drools.pmml.pmml_4_2.PMML4Result;
import org.drools.pmml.pmml_4_2.model.PMML4ModelFactory;
import org.drools.pmml.pmml_4_2.model.PMMLMiningField;
import org.drools.pmml.pmml_4_2.model.PMMLRequestData;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

@PropertyReactive
public class MiningSegment implements Comparable<MiningSegment> {
	private String segmentId;
	private MiningSegmentation owner;
	private PredicateRuleProducer predicateRuleProducer;
	private boolean alwaysTrue;
	private PMML4Model internalModel;
	private String segmentAgendaId;
	private int segmentIndex;
	private static CompiledTemplate launchTemplate;
	private static final String segmentPackageRootName = "org.drools.pmml.pmml_4_2";
	
	public MiningSegment(MiningSegmentation owner, Segment segment, int segmentIndex) {
		this.owner = owner;
		
		this.internalModel = PMML4ModelFactory.getInstance().getModel(segment,owner);
		this.segmentId = segment.getId();
		this.segmentIndex = segmentIndex;
		if (segment.getSimplePredicate() != null) {
			predicateRuleProducer = new SimpleSegmentPredicate(segment.getSimplePredicate());
		} else if (segment.getSimpleSetPredicate() != null) {
			predicateRuleProducer = new SimpleSetSegmentPredicate(segment.getSimpleSetPredicate());
		} else if (segment.getCompoundPredicate() != null) {
			predicateRuleProducer = new CompoundSegmentPredicate(segment.getCompoundPredicate());
		} else if (segment.getTrue() != null) {
			alwaysTrue = true;
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
	
	public String getSegmentId() {
		if (this.segmentId == null || this.segmentId.trim().isEmpty()) {
			StringBuilder bldr = new StringBuilder(owner.getSegmentationId());
			bldr.append("Segment").append(this.segmentIndex);
			this.segmentId = bldr.toString();
		}
		return this.segmentId;
	}
	
	private synchronized CompiledTemplate getLaunchTemplate() {
		if (launchTemplate == null) {
			Resource res = ResourceFactory.newClassPathResource("org/drools/pmml/pmml_4_2/templates/mvel/mining/selectFirstSegOnly.mvel");
			if (res != null) {
				try {
					launchTemplate = TemplateCompiler.compileTemplate(res.getInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return launchTemplate;
	}

	public String generateSegmentRules(String segmentationAgenda, int segmentIndex) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CompiledTemplate template = getLaunchTemplate();
		if (template != null) {
			Map<String, Object> vars = new HashMap<>();
			vars.put("segmentId", getSegmentId());
			vars.put("segmentationAgendaId", segmentationAgenda);
			vars.put("segmentSalience", new Integer(1000 - segmentIndex));
			if (predicateRuleProducer instanceof CompoundSegmentPredicate) {
				CompoundSegmentPredicate predProd = (CompoundSegmentPredicate)predicateRuleProducer;
				if (predProd.hasSurrogation()) {
					vars.put("segmentPredicate", getSurrogationPredicateText(predProd,-1));
				} else {
					vars.put("segmentPredicate", predProd.getPredicateRule());
				}
			} else {
				vars.put("segmentPredicate", predicateRuleProducer.getPredicateRule());
			}
			vars.put("miningPojoClass", getOwner().getOwner().getMiningPojoClassName());
			TemplateRuntime.execute(template, null, new MapVariableResolverFactory(vars),baos);
		}		
		PMML pmml = new PMML();
		pmml.setDataDictionary(this.internalModel.getDataDictionary());
		pmml.getAssociationModelsAndBaselineModelsAndClusteringModels().add(this.internalModel.getRawModel());
		PMML4Compiler compiler = new PMML4Compiler();
		String innerRules = compiler.generateTheory(pmml);
		return (new String(baos.toByteArray())).concat(innerRules);
	}
	
	private String getSurrogationPredicateText(CompoundSegmentPredicate predicate, int lastPredicate) {
		if (lastPredicate >= predicate.getSubpredicateCount()) return "";
		StringBuilder bldr = new StringBuilder();
		if (lastPredicate == -1) {
			bldr.append("(").append(predicate.getPrimaryPredicateRule()).append(")");
		} else {
			bldr.append(predicate.getNextPredicateRule(lastPredicate));
		}
		String subPredicate = getSurrogationPredicateText(predicate,lastPredicate+1);
		if (subPredicate != null && !subPredicate.trim().isEmpty()) {
			bldr.append(" || ").append(subPredicate);
		}
		return bldr.toString();
	}
	
	public MiningSegmentation getOwner() {
		return this.owner;
	}
	
	public PredicateRuleProducer getPredicateRuleProducer() {
		return this.predicateRuleProducer;
	}
	
	public String getPredicateText() {
		return this.alwaysTrue ? "" : this.predicateRuleProducer.getPredicateRule();
	}
	
	public String getSegmentPackageName() {
		StringBuilder builder = new StringBuilder(segmentPackageRootName);
		builder.append(".mining.segment_").append(this.getSegmentId());
		return builder.toString();
	}
	
	public String getSegmentAgendaId() {
		if (this.segmentAgendaId == null || this.segmentAgendaId.trim().isEmpty()) {
			this.segmentAgendaId = this.getOwner().getSegmentationAgendaId().concat("_SEGMENT_"+this.getSegmentId());
		}
		return this.segmentAgendaId;
	}
	
	public int getSegmentIndex() {
		return this.segmentIndex;
	}

	public boolean isAlwaysTrue() {
		return alwaysTrue;
	}

	public PMML4Model getInternalModel() {
		return internalModel;
	}

	@Override
	public int compareTo(MiningSegment ms) {
		if (ms.segmentIndex == this.segmentIndex) return 0;
		return (ms.segmentIndex > this.segmentIndex) ? 1:-1;
	}
	
}
