package org.optaplanner.core.impl.score;

import static java.util.Comparator.comparing;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.score.stream.ConstraintJustification;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public final class DefaultScoreExplanation<Solution_, Score_ extends Score<Score_>>
        implements ScoreExplanation<Solution_, Score_> {

    private static final int DEFAULT_SCORE_EXPLANATION_INDICTMENT_LIMIT = 5;
    private static final int DEFAULT_SCORE_EXPLANATION_CONSTRAINT_MATCH_LIMIT = 2;

    private final Solution_ solution;
    private final Score_ score;
    private final Map<String, ConstraintMatchTotal<Score_>> constraintMatchTotalMap;
    private final List<ConstraintJustification> constraintJustificationList;
    private final Map<Object, Indictment<Score_>> indictmentMap;
    private final AtomicReference<String> summary = new AtomicReference<>(); // Will be calculated lazily.

    public static <Score_ extends Score<Score_>> String explainScore(Score_ workingScore,
            Collection<ConstraintMatchTotal<Score_>> constraintMatchTotalCollection,
            Collection<Indictment<Score_>> indictmentCollection) {
        return explainScore(workingScore, constraintMatchTotalCollection, indictmentCollection,
                DEFAULT_SCORE_EXPLANATION_INDICTMENT_LIMIT, DEFAULT_SCORE_EXPLANATION_CONSTRAINT_MATCH_LIMIT);
    }

    public static <Score_ extends Score<Score_>> String explainScore(Score_ workingScore,
            Collection<ConstraintMatchTotal<Score_>> constraintMatchTotalCollection,
            Collection<Indictment<Score_>> indictmentCollection, int indictmentLimit, int constraintMatchLimit) {
        StringBuilder scoreExplanation =
                new StringBuilder((constraintMatchTotalCollection.size() + 4 + 2 * indictmentLimit) * 80);
        scoreExplanation.append("Explanation of score (").append(workingScore).append("):\n");
        scoreExplanation.append("    Constraint match totals:\n");
        Comparator<ConstraintMatchTotal<Score_>> constraintMatchTotalComparator = comparing(ConstraintMatchTotal::getScore);
        Comparator<ConstraintMatch<Score_>> constraintMatchComparator = comparing(ConstraintMatch::getScore);
        constraintMatchTotalCollection.stream()
                .sorted(constraintMatchTotalComparator)
                .forEach(constraintMatchTotal -> {
                    Set<ConstraintMatch<Score_>> constraintMatchSet = constraintMatchTotal.getConstraintMatchSet();
                    scoreExplanation
                            .append("        ").append(constraintMatchTotal.getScore().toShortString())
                            .append(": constraint (").append(constraintMatchTotal.getConstraintName())
                            .append(") has ").append(constraintMatchSet.size()).append(" matches:\n");
                    constraintMatchSet.stream()
                            .sorted(constraintMatchComparator)
                            .limit(constraintMatchLimit)
                            .forEach(constraintMatch -> scoreExplanation
                                    .append("            ")
                                    .append(constraintMatch.getScore().toShortString())
                                    .append(": justifications (")
                                    .append(constraintMatch.getJustification().toString())
                                    .append(")\n"));
                    if (constraintMatchSet.size() > constraintMatchLimit) {
                        scoreExplanation.append("            ...\n");
                    }
                });

        int indictmentCount = indictmentCollection.size();
        if (indictmentLimit < indictmentCount) {
            scoreExplanation.append("    Indictments (top ").append(indictmentLimit)
                    .append(" of ").append(indictmentCount).append("):\n");
        } else {
            scoreExplanation.append("    Indictments:\n");
        }
        Comparator<Indictment<Score_>> indictmentComparator = comparing(Indictment::getScore);
        Comparator<ConstraintMatch<Score_>> constraintMatchScoreComparator = comparing(ConstraintMatch::getScore);
        indictmentCollection.stream()
                .sorted(indictmentComparator)
                .limit(indictmentLimit)
                .forEach(indictment -> {
                    Set<ConstraintMatch<Score_>> constraintMatchSet = indictment.getConstraintMatchSet();
                    scoreExplanation
                            .append("        ").append(indictment.getScore().toShortString())
                            .append(": indicted object (").append(indictment.getIndictedObject().toString())
                            .append(") has ").append(constraintMatchSet.size()).append(" matches:\n");
                    constraintMatchSet.stream()
                            .sorted(constraintMatchScoreComparator)
                            .limit(constraintMatchLimit)
                            .forEach(constraintMatch -> scoreExplanation
                                    .append("            ").append(constraintMatch.getScore().toShortString())
                                    .append(": constraint (").append(constraintMatch.getConstraintName())
                                    .append(")\n"));
                    if (constraintMatchSet.size() > constraintMatchLimit) {
                        scoreExplanation.append("            ...\n");
                    }
                });
        if (indictmentCount > indictmentLimit) {
            scoreExplanation.append("        ...\n");
        }
        return scoreExplanation.toString();
    }

    DefaultScoreExplanation(InnerScoreDirector<Solution_, Score_> scoreDirector) {
        this.solution = scoreDirector.getWorkingSolution();
        this.score = scoreDirector.calculateScore();
        this.constraintMatchTotalMap = scoreDirector.getConstraintMatchTotalMap();
        this.constraintJustificationList = constraintMatchTotalMap.values()
                .stream()
                .flatMap(constraintMatchTotal -> constraintMatchTotal.getConstraintMatchSet().stream())
                .map(constraintMatch -> (ConstraintJustification) constraintMatch.getJustification())
                .collect(Collectors.toList());
        this.indictmentMap = scoreDirector.getIndictmentMap();
    }

    @Override
    public Solution_ getSolution() {
        return solution;
    }

    @Override
    public Score_ getScore() {
        return score;
    }

    @Override
    public Map<String, ConstraintMatchTotal<Score_>> getConstraintMatchTotalMap() {
        return constraintMatchTotalMap;
    }

    @Override
    public List<ConstraintJustification> getJustificationList() {
        return constraintJustificationList;
    }

    @Override
    public Map<Object, Indictment<Score_>> getIndictmentMap() {
        return indictmentMap;
    }

    @Override
    public String getSummary() {
        return summary.updateAndGet(currentSummary -> {
            if (currentSummary != null) {
                return currentSummary;
            }
            return explainScore(score, constraintMatchTotalMap.values(), indictmentMap.values());
        });
    }

    @Override
    public String toString() {
        return getSummary(); // So that this class can be used in strings directly.
    }
}
