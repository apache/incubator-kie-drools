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
package org.kie.pmml.pmml_4_2.model;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kie.dmg.pmml.pmml_4_2.descr.DATATYPE;
import org.kie.dmg.pmml.pmml_4_2.descr.FIELDUSAGETYPE;
import org.kie.dmg.pmml.pmml_4_2.descr.MiningField;
import org.kie.dmg.pmml.pmml_4_2.descr.MiningSchema;
import org.kie.dmg.pmml.pmml_4_2.descr.Output;
import org.kie.dmg.pmml.pmml_4_2.descr.OutputField;
import org.kie.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.kie.dmg.pmml.pmml_4_2.descr.TreeModel;
import org.kie.pmml.pmml_4_2.PMML4Model;
import org.kie.pmml.pmml_4_2.PMML4Unit;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.TemplateRuntimeError;

public class Treemodel extends AbstractModel<TreeModel> {
	private static final String MINING_POJO_TEMPLATE="/org/kie/pmml/pmml_4_2/templates/mvel/tree/treeMiningPojo.mvel";
	private static final String OUTPUT_POJO_TEMPLATE="/org/kie/pmml/pmml_4_2/templates/mvel/tree/treeOutputPojo.mvel";
	private static final String RULE_UNIT_TEMPLATE="/org/kie/pmml/pmml_4_2/templates/mvel/tree/treeRuleUnit.mvel";
	private static final String TREE_NODE_POJO_TEMPLATE="/org/kie/pmml/pmml_4_2/templates/mvel/tree/treeNodePojo.mvel";
	private static final String TREE_NODE_POJO_TEMPLATE_NAME = "TreemodelTreeNodePojo";

	public Treemodel( String modelId, TreeModel rawModel, PMML4Model parentModel, PMML4Unit owner) {
		super(modelId, PMML4ModelType.TREE, owner, parentModel, rawModel);
	}
	
	public Map.Entry<String, String> getTreeNodeJava() {
		Map<String,String> result = new HashMap<>();
		if (!templateRegistry.contains(TREE_NODE_POJO_TEMPLATE_NAME)) {
			InputStream istrm = Treemodel.class.getResourceAsStream(TREE_NODE_POJO_TEMPLATE);
			if (istrm != null) {
		        CompiledTemplate ct = TemplateCompiler.compileTemplate(istrm);
		        templateRegistry.addNamedTemplate(TREE_NODE_POJO_TEMPLATE_NAME,ct);
			}
		}
		Map<String,Object> vars = new HashMap<>();
		vars.put("pmmlPackageName", PMML_JAVA_PACKAGE_NAME);
		vars.put("outcomeType",getOutputFieldType());
		vars.put("context", this.getModelId());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            TemplateRuntime.execute( templateRegistry.getNamedTemplate(TREE_NODE_POJO_TEMPLATE_NAME),
                                     null,
                                     new MapVariableResolverFactory(vars),
                                     baos );
        } catch (TemplateRuntimeError tre) {
            // need to figure out logging here
            return null;
        }
        String fullName = PMML_JAVA_PACKAGE_NAME+"."+this.getModelId()+"TreeNode";
		result.put(fullName, new String(baos.toByteArray()));
		if (!result.isEmpty()) return result.entrySet().iterator().next();
		return null;
	}

	@Override
	public List<OutputField> getRawOutputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMiningPojoClassName() {
		return helper.compactAsJavaId(this.getModelId().concat("TreeToken"), true);
	}
	
	@Override
	public String getOutputPojoClassName() {
		return helper.compactAsJavaId(this.getModelId().concat("TreeOutput"), true);
	}
	
	@Override
	public String getRuleUnitClassName() {
		return helper.compactAsJavaId(this.getModelId().concat("TreeRuleUnit"),true);
	}

	@Override
	public MiningSchema getMiningSchema() {
		for (Serializable ser: rawModel.getExtensionsAndNodesAndMiningSchemas()) {
			if (ser instanceof MiningSchema) {
				return (MiningSchema)ser;
			}
		}
		return null;
	}

	@Override
	public Output getOutput() {
		for (Serializable ser: rawModel.getExtensionsAndNodesAndMiningSchemas()) {
			if (ser instanceof Output) {
				return (Output)ser;
			}
		}
		return null;
	}

	@Override
	protected void addMiningTemplateToRegistry(TemplateRegistry registry) {
        InputStream inputStream = Treemodel.class.getResourceAsStream(MINING_POJO_TEMPLATE);
        if (inputStream != null) {
	        CompiledTemplate ct = TemplateCompiler.compileTemplate(inputStream);
	        registry.addNamedTemplate(getMiningPojoTemplateName(),ct);
        }
	}
	
	@Override
	protected void addOutputTemplateToRegistry(TemplateRegistry registry) {
		InputStream inputStream = Scorecard.class.getResourceAsStream(OUTPUT_POJO_TEMPLATE);
		if (inputStream != null) {
			CompiledTemplate ct = TemplateCompiler.compileTemplate(inputStream);
			if (ct != null) {
				registry.addNamedTemplate(getOutputPojoTemplateName(), ct);
			}
		}
	}
	
	@Override
	protected void addRuleUnitTemplateToRegistry(TemplateRegistry registry) {
		InputStream inputStream = Scorecard.class.getResourceAsStream(RULE_UNIT_TEMPLATE);
		if (inputStream != null) {
			CompiledTemplate ct = TemplateCompiler.compileTemplate(inputStream);
			if (ct != null) {
				registry.addNamedTemplate(getRuleUnitTemplateName(), ct);
			}
		}
	}
	
	private String getOutputFieldType() {
		String fieldType = null;
		List<OutputField> outFields = getRawOutputFields();
		if (outFields != null && !outFields.isEmpty() ) {
			for (Iterator<OutputField> iter = outFields.iterator(); iter.hasNext() && fieldType == null;) {
				OutputField field = iter.next();
			    DATATYPE datatype = field.getDataType();
			    if (datatype == null) {
			    	String targetName = field.getTargetField();
			    	if (targetName != null) {
			    		PMMLMiningField mf = findMiningField(targetName);
			    		if (mf != null) {
			    			fieldType = mf.getType();
			    		}
			    	}
			    } else {
			    	fieldType = helper.mapDatatype(datatype);
			    }
			}
		} else {
			Map<String,MiningField> miningFields = 
					getFilteredMiningFieldMap(true, FIELDUSAGETYPE.PREDICTED, FIELDUSAGETYPE.TARGET);
			if (miningFields != null && !miningFields.isEmpty()) {
				for (Iterator<String> iter = miningFields.keySet().iterator(); iter.hasNext() && fieldType == null;) {
					String fldName = iter.next();
					PMMLMiningField mf = findMiningField(fldName);
					if (mf != null) {
						fieldType = mf.getType();
					}
				}
			}
		}
		return fieldType;
	}

}
