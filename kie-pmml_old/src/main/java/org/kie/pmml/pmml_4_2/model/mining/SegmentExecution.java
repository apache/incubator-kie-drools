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


import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;

@PropertyReactive
public class SegmentExecution implements Comparable<SegmentExecution> {
	private String correlationId;
	private String segmentationId;
	private String segmentId;
	private int segmentIndex;
	private SegmentExecutionState state;
	private String ruleUnitClassName;
	private PMMLRequestData requestData;
	private PMML4Result result;
	
	
	public SegmentExecution() {
	}
	
	

	public SegmentExecution(String correlationId, 
			String segmentationId, 
			String segmentId, 
			int segmentIndex, 
			String ruleUnitClassName) {
		this.correlationId = correlationId;
		this.segmentationId = segmentationId;
		this.segmentId = segmentId;
		this.segmentIndex = segmentIndex;
		this.state = SegmentExecutionState.WAITING;
		this.ruleUnitClassName = ruleUnitClassName;
	}



	public SegmentExecution(String correlationId, 
			String segmentationId, 
			String segmentId, 
			int segmentIndex, 
			String state, 
			String ruleUnitClassName) {
		this.correlationId = correlationId;
		this.segmentationId = segmentationId;
		this.segmentId = segmentId;
		this.segmentIndex = segmentIndex;
		this.state = SegmentExecutionState.valueOf(state);
		this.ruleUnitClassName = ruleUnitClassName;
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
	
	public void setState( SegmentExecutionState state) {
		this.state = state;
	}

	public String getRuleUnitClassName() {
		return ruleUnitClassName;
	}

	public void setRuleUnitClassName(String ruleUnitClassName) {
		this.ruleUnitClassName = ruleUnitClassName;
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
