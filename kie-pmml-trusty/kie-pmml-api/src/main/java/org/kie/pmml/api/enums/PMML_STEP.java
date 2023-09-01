package org.kie.pmml.api.enums;

/**
 * Values common to all models, i.e. to overall execution
 * This should not contain any reference to a specific implementation, to keep modules decoupling
 */
public enum PMML_STEP {

    START,
    PRE_EVALUATION,
    POST_EVALUATION,
    END
}
