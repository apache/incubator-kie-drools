/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.informer.util;

import org.drools.common.EqualityKey;
import org.drools.common.InternalFactHandle;
import org.drools.informer.Answer;
import org.drools.informer.Question;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;


import java.util.*;

/**
 * TODO need to decide what goes in here and whether this is the right place for it
 *
 * TODO javadoc
 *
 * @author Damon Horrell
 */
public class QueryHelper {

	StatefulKnowledgeSession knowledgeSession;

	public QueryHelper(StatefulKnowledgeSession knowledgeSession) {
		this.knowledgeSession = knowledgeSession;
	}

	/**
	 * Returns a map of question ids and answers sorted by question id.
	 *
	 * @return
	 */
	public SortedMap<String, Object> getAnswers() {
		SortedMap<String, Object> answers = new TreeMap<String, Object>();
		QueryResults queryResults = knowledgeSession.getQueryResults("answeredQuestions");
		for (Iterator<QueryResultsRow> iterator = queryResults.iterator(); iterator.hasNext();) {
			QueryResultsRow row = iterator.next();
			Question question = (Question) row.get("question");
			answers.put(question.getId(), question.getAnswer());
		}
		return answers;
	}

	/**
	 * Returns a collection of everything in the working memory which should be persisted.
	 *
	 * It excludes any objects which were created with insertLogical since they would lose their truth maintenance if reloaded. An
	 * Answer object is returned for each Question which was logically inserted. Other Items shouldn't need to be persisted as the
	 * rules should recreate them again.
	 *
	 * To use this correctly you need to ensure that your rules use "insert" for everything that needs to be persisted other than
	 * answers to questions. You should use "insertLogical" for anything that doesn't need to be persisted, or in fact must not be
	 * persisted. Think about what is data and what are surrounding controls that may need to be extended later. If you persist
	 * more than just data then if your app changes you won't be able to reload old persisted state. e.g. if you add a new Note to
	 * a Group, it won't appear for old data if you had persisted that Group.
	 *
	 * @return
	 */
	public Collection<Object> getPersistentObjects() {
		Collection<Object> objects = new ArrayList<Object>();
		for (FactHandle factHandle : knowledgeSession.getFactHandles()) {
			InternalFactHandle internalFactHandle = (InternalFactHandle) factHandle;
			Object object = internalFactHandle.getObject();
			boolean insertedLogically = internalFactHandle.getEqualityKey().getStatus() == EqualityKey.JUSTIFIED;
			if (!insertedLogically) {
				objects.add(object);
			} else if (object instanceof Question) {
				Question question = (Question) object;
				Object answer = question.getAnswer();
				objects.add(new Answer(question.getId(), answer == null ? null : answer.toString()));
			}
		}
		return objects;
	}
}
