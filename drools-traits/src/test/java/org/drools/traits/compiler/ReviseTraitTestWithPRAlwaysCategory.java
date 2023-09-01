package org.drools.traits.compiler;

/**
 * Serves as a JUnit's {@link org.junit.experimental.categories.Category} to mark Trait-related test
 * which since the enablement of DEFAULT_PROP_SPEC_OPT = PropertySpecificOption.ALWAYS in the KnowledgeBuilderConfiguration
 * failed to pass with the new default.
 * Therefore such marked Trait-related test work with the default being PropertySpecificOption.ALLOWED and shall be revised
 * in order to support all defaults type.
 */
public class ReviseTraitTestWithPRAlwaysCategory {
}
