package org.drools.pmml.pmml_4_2.model.mining;

import java.util.Collection;
import java.util.Iterator;

import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.pmml.pmml_4_2.PMML4Result;
import org.drools.pmml.pmml_4_2.model.PMMLRequestData;
import org.kie.api.KieBase;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieContext;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;

@PropertyReactive
public class SegmentExecution implements Comparable<SegmentExecution> {
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
	
	public void applySegmentModel(PMMLRequestData requestData, KieContext ctx) {
		KieBase kb = ctx.getKieRuntime().getKieBase();
		KnowledgeBaseImpl kbi = (KnowledgeBaseImpl)kb;
		KieContainer container = kbi.getKieContainer();
		
		KieSession segmentSession = container.newKieSession("SEGMENT_"+this.segmentId);

		// Update the state and let the Mining session know
		this.state = SegmentExecutionState.EXECUTING;
		FactHandle handle = ctx.getKieRuntime().getFactHandle(this);
		ctx.getKieRuntime().update(handle, this);
		
		// Insert the request and a target result holder
		// into the segment session and fire the rules
		segmentSession.insert(requestData);
		PMML4Result result = new PMML4Result(this);
		segmentSession.insert(result);
		segmentSession.fireAllRules();
		
		// Update my result and insert the result
		// into the Mining session
		this.result = result;
		segmentSession.dispose();
		
		ctx.getKieRuntime().insert(result);
	}
	
	public class SegmentEventListener extends DefaultAgendaEventListener {
		@Override
		public void matchCancelled(MatchCancelledEvent event) {
	        System.out.println("Match cancelled - "+
		            event.getCause().name()+" - "+event.getMatch().getRule().getPackageName()+" : "+event.getMatch().getRule().getName());
		}

		@Override
		public void matchCreated(MatchCreatedEvent event) {
	        System.out.println("Match created - "+event.getMatch().getRule().getPackageName()+" : "+event.getMatch().getRule().getName());
		}

		@Override
		public void afterMatchFired(AfterMatchFiredEvent event) {
	        System.out.println("After match fired - "+event.getMatch().getRule().getPackageName()+" : "+event.getMatch().getRule().getName());
		}

		@Override
		public void beforeMatchFired(BeforeMatchFiredEvent event) {
	        System.out.println("Before match fired - "+event.getMatch().getRule().getPackageName()+" : "+event.getMatch().getRule().getName());
		}
		
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



	@Override
	public int compareTo(SegmentExecution segEx) {
		if (this.segmentIndex == segEx.segmentIndex) return 0;
		return (this.segmentIndex > segEx.segmentIndex) ? 1:-1;
	}
	
}
