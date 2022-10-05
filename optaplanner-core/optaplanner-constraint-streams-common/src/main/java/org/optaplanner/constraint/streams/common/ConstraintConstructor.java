package org.optaplanner.constraint.streams.common;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;

@FunctionalInterface
public interface ConstraintConstructor<Score_ extends Score<Score_>, JustificationMapping_, IndictedObjectsMapping_> {

    Constraint apply(String constraintPackage, String constraintName, Score_ constraintWeight,
            ScoreImpactType impactType, JustificationMapping_ justificationMapping,
            IndictedObjectsMapping_ indictedObjectsMapping);

}
