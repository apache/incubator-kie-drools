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
package org.drools.pmml.pmml_4_2.model.scorecard;

public class InitialScore extends Score {
	public InitialScore() {
		super();
	}
	
	public InitialScore(String scoreCard, Double value) {
		super(scoreCard,value);
	}
	
	@Override
	public String toString() {
		StringBuilder bldr = new StringBuilder("InitialScore( ");
		bldr.append("scoreCard=").append(getScoreCard()).append(", ");
		bldr.append("value=").append(getValue()).append(" )");
		return bldr.toString();
	}
}
