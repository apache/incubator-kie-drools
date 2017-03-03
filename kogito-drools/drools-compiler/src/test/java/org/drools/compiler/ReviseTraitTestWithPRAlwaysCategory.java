/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler;

/**
 * Serves as a JUnit's {@link org.junit.experimental.categories.Category} to mark Trait-related test
 * which since the enablement of DEFAULT_PROP_SPEC_OPT = PropertySpecificOption.ALWAYS in the KnowledgeBuilderConfiguration
 * failed to pass with the new default.
 * Therefore such marked Trait-related test work with the default being PropertySpecificOption.ALLOWED and shall be revised
 * in order to support all defaults type.
 */
public class ReviseTraitTestWithPRAlwaysCategory {
}
