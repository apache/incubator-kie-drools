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

import java.util.ArrayList;
import java.util.List;

import org.dmg.pmml.pmml_4_2.descr.False;
import org.dmg.pmml.pmml_4_2.descr.True;

public class BooleanSegmentPredicate implements PredicateRuleProducer {
	private boolean alwaysTrue;
	private boolean alwaysFalse;
	
	BooleanSegmentPredicate(True t) {
		alwaysTrue = true;
		alwaysFalse = false;
	}
	
	BooleanSegmentPredicate(False f) {
		alwaysTrue = false;
		alwaysFalse = true;
	}
	
	@Override
	public String getPredicateRule() {
		return alwaysTrue ? "(1 == 1)" : "(1 == 0)";
	}

	@Override
	public List<String> getPredicateFieldNames() {
		return new ArrayList<>();
	}

	@Override
	public List<String> getFieldMissingFieldNames() {
		return new ArrayList<>();
	}

	public boolean isAlwaysTrue() {
		return alwaysTrue;
	}
	
	public boolean isAlwaysFalse() {
		return alwaysFalse;
	}
}
