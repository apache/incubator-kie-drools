package org.optaplanner.core.api.domain.variable;

public enum PlanningVariableGraphType {
    /**
     * This is the default.
     */
    NONE,
    /**
     * Changes to this variable need to trigger chain correction.
     * <p>
     * In some use cases, such as Vehicle Routing, planning entities are chained.
     * A chained variable recursively points to a problem fact, which is called the anchor.
     * So either it points directly to the anchor (that problem fact)
     * or it points to another planning entity which recursively points to the anchor.
     * Chains always have exactly 1 anchor, thus they never loop and the tail is always open.
     * Chains never split into a tree: a anchor or planning entity has at most 1 trailing planning entity.
     * <p>
     * When a chained planning entity changes position, then chain correction must happen:
     * <ul>
     * <li>divert the chain link at the new position to go through the modified planning entity</li>
     * <li>close the missing chain link at the old position</li>
     * </ul>
     * For example: Given {@code A <- B <- C <- D <- X <- Y}, when B moves between X and Y, pointing to X,
     * then Y is also changed to point to B
     * and C is also changed to point to A,
     * giving the result {@code A <- C <- D <- X <- B <- Y}.
     * <p>
     * {@link PlanningVariable#nullable()} true is not compatible with this.
     */
    CHAINED;

    // TODO TREE (DIRECTED_GRAPH)

}
