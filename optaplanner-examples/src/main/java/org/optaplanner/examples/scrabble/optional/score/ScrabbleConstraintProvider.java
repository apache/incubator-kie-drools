/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.scrabble.optional.score;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.examples.scrabble.domain.ScrabbleCell;
import org.optaplanner.examples.scrabble.domain.ScrabbleWordAssignment;
import org.optaplanner.examples.scrabble.domain.ScrabbleWordDirection;

public class ScrabbleConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                characterConflict(constraintFactory),
                noParallelHorizontalNeighbours(constraintFactory),
                noParallelVerticalNeighbours(constraintFactory),
                outOfGrid(constraintFactory),
                maximizeMergesPerWord(constraintFactory),
                pullToCenter(constraintFactory)
        };
    }

    private Constraint characterConflict(ConstraintFactory cf) {
        return cf.from(ScrabbleCell.class)
                .filter(sc -> sc.getCharacterSet().size() >= 2)
                .penalize("Character conflict", HardMediumSoftScore.ONE_HARD, sc -> sc.getCharacterSet().size() - 1);
    }

    private Constraint noParallelHorizontalNeighbours(ConstraintFactory cf) {
        return cf.from(ScrabbleCell.class).filter(sc -> sc.hasWordSet(ScrabbleWordDirection.HORIZONTAL))
                .ifExists(ScrabbleCell.class,
                        Joiners.equal(ScrabbleCell::getX), Joiners.equal(ScrabbleCell::getY, c -> c.getY() + 1),
                        Joiners.filtering((first, second) -> second.hasWordSet(ScrabbleWordDirection.HORIZONTAL)))
                .penalize("No parallel horizontal neighbours", HardMediumSoftScore.ONE_HARD);
    }

    private Constraint noParallelVerticalNeighbours(ConstraintFactory cf) {
        return cf.from(ScrabbleCell.class).filter(sc -> sc.hasWordSet(ScrabbleWordDirection.VERTICAL))
                .ifExists(ScrabbleCell.class,
                        Joiners.equal(ScrabbleCell::getY), Joiners.equal(ScrabbleCell::getX, c -> c.getX() + 1),
                        Joiners.filtering((first, second) -> second.hasWordSet(ScrabbleWordDirection.VERTICAL)))
                .penalize("No parallel vertical neighbours", HardMediumSoftScore.ONE_HARD);
    }

    private Constraint outOfGrid(ConstraintFactory cf) {
        return cf.from(ScrabbleWordAssignment.class)
                .filter(ScrabbleWordAssignment::isOutOfGrid)
                .penalize("Out of grid", HardMediumSoftScore.ONE_HARD, swa -> swa.getWord().length());
    }

    private Constraint maximizeMergesPerWord(ConstraintFactory cf) {
        return cf.from(ScrabbleWordAssignment.class)
                .join(ScrabbleCell.class, Joiners.filtering((swa, sc) -> sc.getWordSet().contains(swa) && sc.hasMerge()))
                .groupBy((swa, sc) -> swa.getId(), ConstraintCollectors.countBi())
                .reward("Maximize merges per word", HardMediumSoftScore.ONE_MEDIUM, (id, count) -> count * count);
    }

    private Constraint pullToCenter(ConstraintFactory cf) {
        return cf.from(ScrabbleWordAssignment.class)
                .penalize("Pull to the center", HardMediumSoftScore.ONE_SOFT, ScrabbleWordAssignment::getDistanceToCenter);
    }
}
