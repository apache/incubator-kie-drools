package org.drools.pmml.pmml_4_2.model.mining;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.dmg.pmml.pmml_4_2.descr.Segment;
import org.drools.pmml.pmml_4_2.PMML4Compiler;
import org.drools.pmml.pmml_4_2.PMML4Model;
import org.drools.pmml.pmml_4_2.model.PMML4ModelFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

public class MiningSegment {
	private String segmentId;
	private MiningSegmentation owner;
	private PredicateRuleProducer predicateRuleProducer;
	private boolean alwaysTrue;
	private PMML4Model internalModel;
	private static CompiledTemplate launchTemplate;
	private static int SEGMENT_COUNTER = 1;
	private static final String SEGMENT_CAN_LAUNCH_TEMPLATE = ""
			+ "rule 'Segment Can Launch - Segment @{segmentId}' \n"
			+ "agenda-group '@{segmentAgendaId}' \n"
			+ "salience @{segmentSalience} \n"
			+ "when \n"
			+ "   @{miningPojoClass}(@{segmentPredicate}) \n"
			+ "then \n"
			+ "   \n"
			+ "end \n\n";
	
	public MiningSegment(MiningSegmentation owner, Segment segment) {
		this.owner = owner;
		this.internalModel = PMML4ModelFactory.getInstance().getModel(segment,owner);
		this.segmentId = segment.getId();
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
	
	public String getSegmentId() {
		if (this.segmentId == null || this.segmentId.trim().length() < 1) {
			StringBuilder bldr = new StringBuilder(owner.getSegmentationId());
			bldr.append("Segment").append(SEGMENT_COUNTER++);
			this.segmentId = bldr.toString();
		}
		return this.segmentId;
	}
	
	private synchronized CompiledTemplate getLaunchTemplate() {
		if (launchTemplate == null) {
			launchTemplate = TemplateCompiler.compileTemplate(SEGMENT_CAN_LAUNCH_TEMPLATE);
		}
		return launchTemplate;
	}

	public String generateSegmentRules(String segmentationAgenda, int segmentIndex) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CompiledTemplate template = getLaunchTemplate();
		if (template != null) {
			Map<String, Object> vars = new HashMap<>();
			vars.put("segmentId", getSegmentId());
			vars.put("segmentAgendaId", segmentationAgenda);
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
		System.out.println(innerRules);
		return new String(baos.toByteArray());
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
		return this.predicateRuleProducer.getPredicateRule();
	}
}
