package org.drools.pmml.pmml_4_2.model.mining;

import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.pmml.pmml_4_2.PMML4Result;
import org.drools.pmml.pmml_4_2.model.PMMLRequestData;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieContext;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.runtime.KnowledgeContext;

@PropertyReactive
public class SegmentExecution {
	private String correlationId;
	private String segmentationId;
	private String segmentId;
	private int segmentIndex;
	private SegmentExecutionState state;
	private String agendaId;
	private PMMLRequestData requestData;
	private PMML4Result result;
	
	public SegmentExecution() {
	}
	
	

	public SegmentExecution(String correlationId, String segmentationId, String segmentId, int segmentIndex, String agendaId) {
		this.correlationId = correlationId;
		this.segmentationId = segmentationId;
		this.segmentId = segmentId;
		this.segmentIndex = segmentIndex;
		this.state = SegmentExecutionState.WAITING;
		this.agendaId = agendaId;
	}



	public SegmentExecution(String correlationId, String segmentationId, String segmentId, int segmentIndex, String state, String agendaId) {
		this.correlationId = correlationId;
		this.segmentationId = segmentationId;
		this.segmentId = segmentId;
		this.segmentIndex = segmentIndex;
		this.state = SegmentExecutionState.valueOf(state);
		this.agendaId = agendaId;
	}
	
	

	public String getCorrelationId() {
		return correlationId;
	}



	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}



	public String getSegmentationId() {
		return segmentationId;
	}

	public void setSegmentationId(String segmentationId) {
		this.segmentationId = segmentationId;
	}

	public String getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(String segmentId) {
		this.segmentId = segmentId;
	}

	public int getSegmentIndex() {
		return segmentIndex;
	}

	public void setSegmentIndex(int segmentIndex) {
		this.segmentIndex = segmentIndex;
	}

	public SegmentExecutionState getState() {
		return state;
	}

	public void setState(String state) {
		this.state = SegmentExecutionState.valueOf(state);
	}
	
	public void setState(SegmentExecutionState state) {
		this.state = state;
	}

	public String getAgendaId() {
		return agendaId;
	}

	public void setAgendaId(String agendaId) {
		this.agendaId = agendaId;
	}
	
	
	public PMMLRequestData getRequestData() {
		return requestData;
	}



	public void setRequestData(PMMLRequestData requestData) {
		this.requestData = requestData;
	}



	public PMML4Result getResult() {
		return result;
	}



	public void setResult(PMML4Result result) {
		this.result = result;
	}
	
	public void applySegmentModel(PMMLRequestData requestData) {
		KieServices services = KieServices.Factory.get();
		KieContainer container = services.getKieClasspathContainer();
		KieBase kbase = container.getKieBase(agendaId);
		KieSession session = kbase.newKieSession();
		session.insert(requestData);
		session.fireAllRules();
		System.out.println("Load session for the appropriate segment");
		PMML4Result res = new PMML4Result();
		res.setResultCode("OK");
		this.result = res; 
	}
	
	
	public void applyModel(KnowledgeHelper helper) {
//		helper.halt();
		helper.setFocus(this.agendaId);
//		helper.getWorkingMemory().getAgenda().getAgendaGroup("SampleMine_SampleMineSegmentation_SEGMENT_2").clear();
//		helper.getWorkingMemory().fireAllRules(new SegmentAgendaFilter());
//		applySegmentModel(this.requestData);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((correlationId == null) ? 0 : correlationId.hashCode());
		result = prime * result + ((segmentId == null) ? 0 : segmentId.hashCode());
		result = prime * result + segmentIndex;
		result = prime * result + ((segmentationId == null) ? 0 : segmentationId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SegmentExecution other = (SegmentExecution) obj;
		if (correlationId == null) {
			if (other.correlationId != null) {
				return false;
			}
		} else if (!correlationId.equals(other.correlationId)) {
			return false;
		}
		if (segmentId == null) {
			if (other.segmentId != null) {
				return false;
			}
		} else if (!segmentId.equals(other.segmentId)) {
			return false;
		}
		if (segmentIndex != other.segmentIndex) {
			return false;
		}
		if (segmentationId == null) {
			if (other.segmentationId != null) {
				return false;
			}
		} else if (!segmentationId.equals(other.segmentationId)) {
			return false;
		}
		return true;
	}
	
	private class SegmentAgendaFilter implements AgendaFilter {

		@Override
		public boolean accept(Match match) {
			RuleTerminalNodeLeftTuple rtnlt = (RuleTerminalNodeLeftTuple)match;
			String agendaName = rtnlt.getAgendaGroup().getName();
			System.out.println(agendaName);
			return agendaName.equals(agendaId);
		}
		
	}
	

}
