package org.optaplanner.core.api.score.constraint;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.List;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.lookup.ClassAndPlanningIdComparator;

/**
 * Retrievable from {@link ConstraintMatchTotal#getConstraintMatchSet()}
 * and {@link Indictment#getConstraintMatchSet()}.
 *
 * <p>
 * This class has a {@link #compareTo(ConstraintMatch)} method which is inconsistent with equals.
 * (See {@link Comparable}.)
 * Two different {@link ConstraintMatch} instances with the same justification list aren't
 * {@link Object#equals(Object) equal} because some ConstraintStream API methods can result in duplicate facts,
 * which are treated as independent matches.
 * Yet two instances may {@link #compareTo(ConstraintMatch)} equal in case they come from the same constraint and their
 * justifications are equal.
 * This is for consistent ordering of constraint matches in visualizations.
 * 
 * @param <Score_> the actual score type
 */
public final class ConstraintMatch<Score_ extends Score<Score_>> implements Comparable<ConstraintMatch<Score_>> {

    private final String constraintPackage;
    private final String constraintName;

    private final List<Object> justificationList;
    private final Score_ score;

    /**
     * @param constraintPackage never null
     * @param constraintName never null
     * @param justificationList never null, sometimes empty
     * @param score never null
     */
    public ConstraintMatch(String constraintPackage, String constraintName, List<Object> justificationList,
            Score_ score) {
        this.constraintPackage = requireNonNull(constraintPackage);
        this.constraintName = requireNonNull(constraintName);
        this.justificationList = requireNonNull(justificationList);
        this.score = requireNonNull(score);
    }

    public String getConstraintPackage() {
        return constraintPackage;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public List<Object> getJustificationList() {
        return justificationList;
    }

    public Score_ getScore() {
        return score;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public String getConstraintId() {
        return ConstraintMatchTotal.composeConstraintId(constraintPackage, constraintName);
    }

    public String getIdentificationString() {
        return getConstraintId() + "/" + justificationList;
    }

    @Override
    public int compareTo(ConstraintMatch<Score_> other) {
        if (!constraintPackage.equals(other.constraintPackage)) {
            return constraintPackage.compareTo(other.constraintPackage);
        } else if (!constraintName.equals(other.constraintName)) {
            return constraintName.compareTo(other.constraintName);
        } else {
            /*
             * TODO Come up with a better cache.
             *
             * Reuse the comparator to internally caches reflection for performance benefits.
             * However, there are possibly thousands of instances of this class, and each gets its own comparator.
             * Therefore, the caching is only partially effective.
             */
            Comparator<Object> comparator = new ClassAndPlanningIdComparator(false);
            for (int i = 0; i < justificationList.size() && i < other.justificationList.size(); i++) {
                Object left = justificationList.get(i);
                Object right = other.justificationList.get(i);
                int comparison = comparator.compare(left, right);
                if (comparison != 0) {
                    return comparison;
                }
            }
            if (justificationList.size() != other.justificationList.size()) {
                return justificationList.size() < other.justificationList.size() ? -1 : 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public String toString() {
        return getIdentificationString() + "=" + score;
    }

}
