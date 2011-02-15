/**
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.core.localsearch.decider.forager;

import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.core.localsearch.decider.MoveScope;
import org.drools.planner.core.score.Score;

/**
 * @author Geoffrey De Smet
 */
public class AcceptedMoveScopeComparator implements Comparator<MoveScope> {

    private final Comparator<? extends Score> deciderScoreComparator;

    public AcceptedMoveScopeComparator(Comparator<? extends Score> deciderScoreComparator) {
        this.deciderScoreComparator = deciderScoreComparator;
    }
    
    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public int compare(MoveScope a, MoveScope b) {
        CompareToBuilder compareToBuilder = new CompareToBuilder();
        compareToBuilder.append(a.getScore(), b.getScore(), deciderScoreComparator);
        compareToBuilder.append(a.getAcceptChance(), b.getAcceptChance());
        // moves are not compared
        return compareToBuilder.toComparison();
    }

}
