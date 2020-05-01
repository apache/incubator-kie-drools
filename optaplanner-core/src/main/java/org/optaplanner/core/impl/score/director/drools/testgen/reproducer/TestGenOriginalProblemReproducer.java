/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.score.director.drools.testgen.reproducer;

import org.optaplanner.core.impl.score.director.drools.testgen.TestGenKieSessionJournal;

public interface TestGenOriginalProblemReproducer {

    /**
     * Replay the journal and decide if the original problem is reproducible.
     *
     * @param journal journal tested for the original problem
     * @return true if replaying the journal leads to the original problem
     */
    boolean isReproducible(TestGenKieSessionJournal journal);

    /**
     * Throws exception if the original problem is not reproducible with the given journal.
     *
     * @param journal journal tested for the original problem
     * @param contextDescription describes the context in which the problem should be reproducible
     */
    void assertReproducible(TestGenKieSessionJournal journal, String contextDescription);

}
