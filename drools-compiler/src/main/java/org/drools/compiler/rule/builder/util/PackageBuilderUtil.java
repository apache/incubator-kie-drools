/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.rule.builder.util;

import org.drools.base.rule.EntryPointId;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.QueryElement;
import org.drools.base.rule.RuleConditionElement;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.EntryPointDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.WindowReferenceDescr;

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

        if (source instanceof Pattern) {
            if ( ((Pattern) source).hasXPath() ) {
                return true;
            }
            if ( ((Pattern) source).getSource() instanceof EntryPointId ) {
                return false;
            }
        }

        if ( inputPattern.getSource() != null &&
                !( inputPattern.getSource() instanceof WindowReferenceDescr ) &&
                !( inputPattern.getSource() instanceof EntryPointDescr ) ) {
            return true;
        }

        return source instanceof QueryElement ||
               ( source.getNestedElements().size() == 1 && source.getNestedElements().get( 0 ) instanceof QueryElement );
    }
}
