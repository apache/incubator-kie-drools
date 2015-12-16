/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.rule.builder.util;

import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.WindowReferenceDescr;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.rule.QueryElement;
import org.drools.core.rule.RuleConditionElement;

public class PackageBuilderUtil {

    /**
     * This method checks for the conditions when local declarations should be read from a tuple instead
     * of the right object when resolving declarations in an accumulate
     * 
     * @param accumDescr
     * @param source
     * @return
     */
    public static boolean isReadLocalsFromTuple(final RuleBuildContext context,
                                                final AccumulateDescr accumDescr,
                                                final RuleConditionElement source) {
        if (accumDescr.isMultiPattern()) {
            return true;
        }

        PatternDescr inputPattern = accumDescr.getInputPattern();
        if (inputPattern == null) {
            context.addError(new DescrBuildError(context.getParentDescr(),
                                                 accumDescr,
                                                 null,
                                                 "Invalid accumulate pattern in rule '" + context.getRule().getName() + "'."));
            return true;
        }

        return ( inputPattern.getSource() != null &&
                  !( inputPattern.getSource() instanceof WindowReferenceDescr ) &&
                  !( inputPattern.getSource() instanceof EntryPointDescr ) ) ||
                  source instanceof QueryElement ||
                  ( source.getNestedElements().size() == 1 && source.getNestedElements().get( 0 ) instanceof QueryElement );
    }
}
