package org.optaplanner.operator.impl.solver.model;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.fabric8.kubernetes.api.model.Condition;
import io.fabric8.kubernetes.api.model.ConditionBuilder;

/*
    For more details about the Status and Conditions, see
    https://github.com/kubernetes/community/blob/master/contributors/devel/sig-architecture/api-conventions.md#typical-status-properties
 */
public final class OptaPlannerSolverStatus {

    public static final String CONDITION_TYPE_READY = "Ready";
    private static final String CONDITION_REASON_IN_PROGRESS = "InProgress";

    private static final String CONDITION_REASON_SOLVER_READY = "SolverReady";

    /**
     * @return the current timestamp in ISO 8601 format, e.g. "2022-09-02T12:36:10.571Z".
     */
    private static String currentTimestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    public static OptaPlannerSolverStatus unknown(Long generation) {
        OptaPlannerSolverStatus optaPlannerSolverStatus = new OptaPlannerSolverStatus();
        Condition condition = new ConditionBuilder()
                .withLastTransitionTime(currentTimestamp())
                .withObservedGeneration(generation)
                .withReason(CONDITION_REASON_IN_PROGRESS)
                .withStatus(ConditionStatus.UNKNOWN.getName())
                .withType(CONDITION_TYPE_READY)
                .build();
        optaPlannerSolverStatus.setConditions(List.of(condition));
        return optaPlannerSolverStatus;
    }

    public static OptaPlannerSolverStatus ready(Long generation) {
        OptaPlannerSolverStatus optaPlannerSolverStatus = new OptaPlannerSolverStatus();
        Condition condition = new ConditionBuilder()
                .withLastTransitionTime(currentTimestamp())
                .withObservedGeneration(generation)
                .withStatus(ConditionStatus.TRUE.getName())
                .withReason(CONDITION_REASON_SOLVER_READY)
                .withType(CONDITION_TYPE_READY)
                .build();
        optaPlannerSolverStatus.setConditions(List.of(condition));
        return optaPlannerSolverStatus;
    }

    public static OptaPlannerSolverStatus error(Long generation, Throwable error) {
        OptaPlannerSolverStatus optaPlannerSolverStatus = new OptaPlannerSolverStatus();
        Condition condition = new ConditionBuilder()
                .withLastTransitionTime(currentTimestamp())
                .withObservedGeneration(generation)
                .withStatus(ConditionStatus.FALSE.getName())
                .withType(CONDITION_TYPE_READY)
                .withReason(error.getClass().getSimpleName())
                .withMessage(error.getMessage())
                .build();
        optaPlannerSolverStatus.setConditions(List.of(condition));
        return optaPlannerSolverStatus;
    }

    public enum ConditionStatus {
        UNKNOWN("Unknown"),
        TRUE("True"),
        FALSE("False");

        private final String name;

        ConditionStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private List<Condition> conditions;

    private String inputMessageAddress;
    private String outputMessageAddress;

    public OptaPlannerSolverStatus() {
        // required by Jackson
    }

    public String getInputMessageAddress() {
        return inputMessageAddress;
    }

    public void setInputMessageAddress(String inputMessageAddress) {
        this.inputMessageAddress = inputMessageAddress;
    }

    public String getOutputMessageAddress() {
        return outputMessageAddress;
    }

    public void setOutputMessageAddress(String outputMessageAddress) {
        this.outputMessageAddress = outputMessageAddress;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
