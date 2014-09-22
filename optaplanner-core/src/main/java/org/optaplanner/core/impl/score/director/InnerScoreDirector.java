/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.impl.score.director;

import java.util.List;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

public interface InnerScoreDirector extends ScoreDirector {

    /**
     * @return used to check {@link #isWorkingEntityListDirty(long)} later on
     */
    long getWorkingEntityListRevision();

    /**
     * @param expectedWorkingEntityListRevision an
     * @return true if the entityList might have a different set of instances now
     */
    boolean isWorkingEntityListDirty(long expectedWorkingEntityListRevision);

    /**
     * @return never null
     */
    InnerScoreDirectorFactory getScoreDirectorFactory();

    /**
     * @return never null
     */
    SolutionDescriptor getSolutionDescriptor();

    /**
     * @return never null
     */
    ScoreDefinition getScoreDefinition();

    Solution cloneWorkingSolution();

    /**
     * @return >= 0
     */
    int getWorkingEntityCount();

    /**
     * @return never null: an empty list if there are none
     */
    List<Object> getWorkingEntityList();

    /**
     * @return >= 0
     */
    int getWorkingValueCount();

    int countWorkingSolutionUninitializedVariables();

    /**
     * @return true if the {@link Solution workingSolution} is initialized
     */
    boolean isWorkingSolutionInitialized();

    /**
     * @return at least 0L
     */
    long getCalculateCount();

    /**
     * Clones this {@link ScoreDirector} and its {@link Solution workingSolution}.
     * Use {@link #getWorkingSolution()} to retrieve the {@link Solution workingSolution} of that clone.
     * <p/>
     * This is heavy method, because it usually breaks incremental score calculation. Use it sparingly.
     * Therefore it's best to clone lazily by delaying the clone call as long as possible.
     * @return never null
     */
    ScoreDirector clone();

    /**
     * @param chainedVariableDescriptor never null, must be {@link GenuineVariableDescriptor#isChained()} true
     * and known to the {@link SolutionDescriptor}
     * @param planningValue sometimes null
     * @return never null
     */
    Object getTrailingEntity(GenuineVariableDescriptor chainedVariableDescriptor, Object planningValue);

    /**
     * Do not waste performance by propagating changes to step (or higher) mechanisms.
     * @param allChangesWillBeUndoneBeforeStepEnds true if all changes will be undone
     */
    void setAllChangesWillBeUndoneBeforeStepEnds(boolean allChangesWillBeUndoneBeforeStepEnds);

    /**
     * Asserts that if the {@link Score} is calculated for the current {@link Solution workingSolution}
     * in the current {@link ScoreDirector} (with possibly incremental calculation residue),
     * it is equal to the parameter {@link Score expectedWorkingScore}.
     * <p/>
     * Used to assert that skipping {@link #calculateScore()} (when the score is otherwise determined) is correct,
     * @param expectedWorkingScore never null
     * @param completedAction sometimes null, when assertion fails then the completedAction's {@link Object#toString()}
     * is included in the exception message
     */
    void assertExpectedWorkingScore(Score expectedWorkingScore, Object completedAction);

    /**
     * Asserts that if the {@link Score} is calculated for the current {@link Solution workingSolution}
     * in a fresh {@link ScoreDirector} (with no incremental calculation residue),
     * it is equal to the parameter {@link Score workingScore}.
     * <p/>
     * Furthermore, if the assert fails, a score corruption analysis might be included in the exception message.
     * @param workingScore never null
     * @param completedAction sometimes null, when assertion fails then the completedAction's {@link Object#toString()}
     * is included* in the exception message
     * @see InnerScoreDirectorFactory#assertScoreFromScratch(Solution)
     */
    void assertWorkingScoreFromScratch(Score workingScore, Object completedAction);

}
