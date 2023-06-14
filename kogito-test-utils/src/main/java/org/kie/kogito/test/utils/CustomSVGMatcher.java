/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.test.utils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.xmlunit.diff.*;

public final class CustomSVGMatcher extends BaseMatcher<Object> {

    private static final ComparisonFormatter FORMATTER = new DefaultComparisonFormatter();

    private CustomSVGDiffer diffBuilder;
    private Diff diffResult;

    CustomSVGMatcher(final CustomSVGDiffer diffBuilder) {
        this.diffBuilder = diffBuilder;
    }

    public static CustomSVGMatcher isSimilarTo(final String content) {
        return new CustomSVGMatcher(new CustomSVGDiffer(content));
    }

    @Override
    public boolean matches(Object item) {
        diffResult = diffBuilder.withTest(item);

        return !diffResult.hasDifferences();
    }

    @Override
    public void describeTo(Description description) {
        if (diffResult == null || !diffResult.hasDifferences()) {
            description.appendText(" is similar to the control document");
        } else {
            final Comparison difference = diffResult.getDifferences().iterator().next().getComparison();
            final String reason = createReasonPrefix(diffResult.getControlSource().getSystemId(), difference);
            final String testString = FORMATTER.getDetails(difference.getControlDetails(), difference.getType(), true);

            description.appendText(String.format("%s:%n%s", reason, testString));
        }
    }

    private String createReasonPrefix(final String systemId, final Comparison difference) {
        final String description = FORMATTER.getDescription(difference);
        final String reason;
        if (systemId == null) {
            reason = description;
        } else {
            reason = String.format("In Source '%s' %s", systemId, description);
        }
        return reason;
    }

}