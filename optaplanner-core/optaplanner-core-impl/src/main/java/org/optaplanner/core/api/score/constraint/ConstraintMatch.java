package org.optaplanner.core.api.score.constraint;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.List;

import org.optaplanner.core.api.domain.common.DomainAccessType;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessorFactory;
import org.optaplanner.core.impl.domain.lookup.ClassAndPlanningIdComparator;

/**
 * Retrievable from {@link ConstraintMatchTotal#getConstraintMatchSet()}
 * and {@link Indictment#getConstraintMatchSet()}.
 *
 * <p>
 * This class implements {@link Comparable} for consistent ordering of constraint matches in visualizations.
 * The details of this ordering are unspecified and are subject to change.
 * 
 * @param <Score_> the actual score type
 */
public final class ConstraintMatch<Score_ extends Score<Score_>> implements Comparable<ConstraintMatch<Score_>> {

    private final String constraintPackage;
    private final String constraintName;
    private final String constraintId;

    private final List<Object> justificationList;
    private final Score_ score;

    private Comparator<Object> classAndIdPlanningComparator;

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
        this.constraintId = ConstraintMatchTotal.composeConstraintId(constraintPackage, constraintName);
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
        if (!constraintId.equals(other.constraintId)) {
            return constraintId.compareTo(other.constraintId);
        } else if (!score.equals(other.score)) {
            return score.compareTo(other.score);
        } else if (justificationList.size() != other.justificationList.size()) {
            return Integer.compare(justificationList.size(), other.justificationList.size());
        } else {
            Comparator<Object> comparator = getClassAndIdPlanningComparator(other);
            for (int i = 0; i < justificationList.size(); i++) {
                Object left = justificationList.get(i);
                Object right = other.justificationList.get(i);
                int comparison = comparator.compare(left, right);
                if (comparison != 0) {
                    return comparison;
                }
            }
            return Integer.compare(System.identityHashCode(this),
                    System.identityHashCode(other));
        }
    }

    private Comparator<Object> getClassAndIdPlanningComparator(ConstraintMatch<Score_> other) {
        /*
         * The comparator performs some expensive operations, which can be cached.
         * For optimal performance, this cache (MemberAccessFactory) needs to be shared between comparators.
         * In order to prevent the comparator from being shared in a static field creating a de-facto memory leak,
         * we cache the comparator inside this class, and we minimize the number of instances that will be created
         * by creating the comparator when none of the constraint matches already carry it,
         * and we store it in both.
         */
        if (classAndIdPlanningComparator != null) {
            return classAndIdPlanningComparator;
        } else if (other.classAndIdPlanningComparator != null) {
            return other.classAndIdPlanningComparator;
        } else {
            /*
             * FIXME Using reflection will break Quarkus once we don't open up classes for reflection any more.
             * Use cases which need to operate safely within Quarkus should use SolutionDescriptor's MemberAccessorFactory.
             */
            classAndIdPlanningComparator =
                    new ClassAndPlanningIdComparator(new MemberAccessorFactory(), DomainAccessType.REFLECTION, false);
            other.classAndIdPlanningComparator = classAndIdPlanningComparator;
            return classAndIdPlanningComparator;
        }
    }

    @Override
    public String toString() {
        return getIdentificationString() + "=" + score;
    }

}
