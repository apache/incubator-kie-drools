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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.pmml_4_2.descr.MULTIPLEMODELMETHOD;
import org.dmg.pmml.pmml_4_2.descr.Segment;
import org.dmg.pmml.pmml_4_2.descr.Segmentation;
import org.drools.pmml.pmml_4_2.model.Miningmodel;
import org.drools.pmml.pmml_4_2.model.PMMLMiningField;
import org.drools.pmml.pmml_4_2.model.PMMLOutputField;
import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

public class MiningSegmentation {
	private Miningmodel owner;
	private String segmentationId;
	private MULTIPLEMODELMETHOD multipleModelMethod;
	private List<MiningSegment> miningSegments;
	
	private static TemplateRegistry templates;
	private static Map<String,String> templateNameToFile;
	private static final String segmentActivationSelectFirst = "org/drools/pmml/pmml_4_2/templates/mvel/mining/selectFirstSegOnly.mvel";
	private static final String segmentSelectAll = "org/drools/pmml/pmml_4_2/templates/mvel/mining/selectAllSegments.mvel";
	private static final String segmentModelChain = "org/drools/pmml/pmml_4_2/templates/mvel/mining/modelChain.mvel";
	
	public MiningSegmentation(Miningmodel owner, Segmentation segmentation) {
		this.owner = owner;
		this.multipleModelMethod = segmentation.getMultipleModelMethod();
		this.miningSegments = new ArrayList<>();
		initSegments(segmentation.getSegments());
		initTemplates();
	}
	
	private void initSegments(List<Segment> segments) {
		if (segments != null && !segments.isEmpty()) {
			for (int index = 0; index < segments.size(); index++) {
				Segment seg = segments.get(index);
				MiningSegment ms = new MiningSegment(this,seg,index);
				miningSegments.add(ms);
			}
		}
	}
	
	private synchronized static void initTemplates() {
		if (templates == null) {
			templates = new SimpleTemplateRegistry();
		}
		if (templateNameToFile == null) {
			templateNameToFile = new HashMap<>();
			templateNameToFile.put(MULTIPLEMODELMETHOD.SELECT_FIRST.name(), segmentActivationSelectFirst);
			templateNameToFile.put(MULTIPLEMODELMETHOD.SELECT_ALL.name(), segmentSelectAll);
			templateNameToFile.put(MULTIPLEMODELMETHOD.MODEL_CHAIN.name(), segmentModelChain);
		}
	}
	
	public Miningmodel getOwner() {
		return this.owner;
	}
	
	public List<PMMLMiningField> getMiningFields() {
		return this.owner.getMiningFields();
	}
	
	public String getMiningPojoClassName() {
		return this.owner.getMiningPojoClassName();
	}

	public MULTIPLEMODELMETHOD getMultipleModelMethod() {
		return this.multipleModelMethod;
	}
	
	public String getMultipleModelHandling() {
		return this.multipleModelMethod.name();
	}
	
	public void setMultipleModelMethod(MULTIPLEMODELMETHOD multipleModelMethod) {
		this.multipleModelMethod = multipleModelMethod;
	}
	
	public List<MiningSegment> getMiningSegments() {
		return this.miningSegments;
	}
	
	public String getSegmentationId() {
		if (segmentationId == null || segmentationId.trim().length() < 1) {
			segmentationId = owner.getModelId().concat("Segmentation");
		}
		return segmentationId;
	}

	public void setSegmentationId(String segmentationId) {
		this.segmentationId = segmentationId;
	}
	
	private void loadTemplates(MULTIPLEMODELMETHOD mmm) {
		if (!templates.contains(mmm.name())) {
			Resource res = ResourceFactory.newClassPathResource(templateNameToFile.get(mmm.name()), MiningSegmentation.class);
			if (res != null) {
				try {
					InputStream strm = res.getInputStream();
					templates.addNamedTemplate(mmm.name(), TemplateCompiler.compileTemplate(strm));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public MiningSegmentTransfer getSegmentTransfer(MiningSegment targetSegment, String targetFieldName) {
		MiningSegmentTransfer xfer = null;
		int lastIndex = targetSegment.getSegmentIndex() - 1;
		if (lastIndex < 0) {
			throw new IndexOutOfBoundsException("Cannot have an undefined mining field in the first segment");
		}
		for (int idx = lastIndex; idx >= 0; idx--) {
			MiningSegment seg = miningSegments.get(idx);
			if (seg != null && seg.getInternalModel() != null) {
				List<PMMLOutputField> outputs = seg.getInternalModel().getOutputFields();
				if (outputs != null) {
					for (PMMLOutputField field: outputs) {
						if (field.getName().equals(targetFieldName)) {
							xfer = new MiningSegmentTransfer(this.segmentationId, seg.getSegmentId(), targetSegment.getSegmentId());
							xfer.addResultToRequestMapping(targetFieldName, targetFieldName);
							return xfer;
						}
					}
				}
			}
		}
		return xfer;
	}

	public String generateSegmentationRules() {
		StringBuilder builder = new StringBuilder();
		loadTemplates(this.multipleModelMethod);
		Map<String, Object> templateVars = new HashMap<>();
		String pkgName = "org.drools.pmml.pmml_4_2."+this.getSegmentationId();
		CompiledTemplate ct = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		switch (this.multipleModelMethod) {
			case AVERAGE:
				break;
			case MAJORITY_VOTE:
				break;
			case MAX:
				break;
			case MEDIAN:
				break;
			case MODEL_CHAIN:
				List<MiningSegmentTransfer> segmentTransfers = new ArrayList<>();
				MiningSegmentTransfer mst = new MiningSegmentTransfer(this.getSegmentationId(), "1","2");
				mst.addResultToRequestMapping("calculatedScore","calculatedScore");
				segmentTransfers.add(mst);
				templateVars.put("miningModel", this.getOwner());
				templateVars.put("childSegments", this.getMiningSegments());
				templateVars.put("packageName", pkgName);
				templateVars.put("resultMappings", segmentTransfers);
				ct = templates.getNamedTemplate(this.multipleModelMethod.name());
				TemplateRuntime.execute(ct,null,new MapVariableResolverFactory(templateVars),baos);
				builder.append(new String(baos.toByteArray()));
				break;
			case SELECT_ALL:
				templateVars.put("miningModel", this.getOwner());
				templateVars.put("childSegments", this.getMiningSegments());
				templateVars.put("packageName", pkgName);
				ct = templates.getNamedTemplate(this.multipleModelMethod.name());
				TemplateRuntime.execute(ct,null,new MapVariableResolverFactory(templateVars),baos);
				builder.append(new String(baos.toByteArray()));
				break;
			case SELECT_FIRST:
				templateVars.put("miningModel", this.getOwner());
				templateVars.put("childSegments", this.getMiningSegments());
				templateVars.put("packageName", pkgName);
				ct = templates.getNamedTemplate(this.multipleModelMethod.name());
				TemplateRuntime.execute(ct,null,new MapVariableResolverFactory(templateVars),baos);
				builder.append(new String(baos.toByteArray()));
				break;
			case SUM:
				break;
			case WEIGHTED_AVERAGE:
				break;
			case WEIGHTED_MAJORITY_VOTE:
				break;
		}
		
		return builder.toString();
	}
	
 
	
	public String generateRulesForSegment(int index) {
		StringBuilder builder = new StringBuilder();
		String segRules = miningSegments.get(index).generateSegmentRules(getSegmentationAgendaId(), index);
		return segRules;
	}
	
	public String getSegmentationAgendaId() {
		return getOwner().getModelId()+"_"+getSegmentationId();
	}
}
