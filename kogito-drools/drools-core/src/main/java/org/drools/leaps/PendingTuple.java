package org.drools.leaps;

/*
 * Copyright 2006 Alexander Bagerman
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;

import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;

/**
 * This is wrapper for drools agendaItem to keep track of activations that were
 * revoked from the agenda due to the fact retraction
 * 
 * @author Alexander Bagerman
 * 
 */
class PendingTuple {
	boolean valid;

	private final boolean existsCountRelevant;

	private int existsCount;

	private final boolean notCountRelevant;

	private int notCount;

	private final LeapsTuple tuple;

	private final List existsQualifiers;

	private final PropagationContext context;

	private final Rule rule;

	private boolean submited = false;

	public PendingTuple(LeapsTuple tuple, List existsQualifiers,
			PropagationContext context, Rule rule, boolean existsCountRelevant,
			int existsCount, boolean notCountRelevant, int notCount) {
		this.valid = true;
		this.existsCountRelevant = existsCountRelevant;
		this.existsCount = existsCount;
		this.notCountRelevant = notCountRelevant;
		this.notCount = notCount;
		this.tuple = tuple;
		this.context = context;
		this.rule = rule;
		this.existsQualifiers = existsQualifiers;
	}

	protected LeapsTuple getTuple() {
		return this.tuple;
	}

	protected boolean isValid() {
		boolean ret = this.valid && !this.submited;
		if (ret && this.notCountRelevant) {
			ret = (this.notCount == 0);
		}
		if (ret && this.existsCountRelevant) {
			ret = (this.existsCount > 0);
		}
		return ret;
	}

	protected void invalidate() {
		this.valid = false;
	}

	protected void decrementExistsCount() {
		if (this.existsCountRelevant) {
			this.existsCount--;
		}
	}

	protected void decrementNotCount() {
		if (this.notCountRelevant) {
			this.notCount--;
		}
	}

	protected List getExistsQualifiers() {
		return this.existsQualifiers;
	}

	protected void setSubmited() {
		this.submited = true;
	}

	protected PropagationContext getContext() {
		return this.context;
	}

	protected Rule getRule() {
		return this.rule;
	}
}
