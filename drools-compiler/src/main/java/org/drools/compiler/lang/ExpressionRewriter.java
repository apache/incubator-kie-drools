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

package org.drools.compiler.lang;


import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConstraintConnectiveDescr;
import org.drools.compiler.lang.descr.OperatorDescr;

public interface ExpressionRewriter {

    String dump( BaseDescr base );

    String dump( BaseDescr base,
                 MVELDumper.MVELDumperContext context );

    String dump( BaseDescr base,
                 ConstraintConnectiveDescr parent,
                 MVELDumper.MVELDumperContext context );

    String dump( BaseDescr base,
                 int parentPrecedence );

    StringBuilder dump( StringBuilder sbuilder,
                        BaseDescr base,
                        int parentPriority,
                        boolean isInsideRelCons,
                        MVELDumper.MVELDumperContext context );

    StringBuilder dump( StringBuilder sbuilder,
                        BaseDescr base,
                        ConstraintConnectiveDescr parent,
                        int parentIndex,
                        int parentPriority,
                        boolean isInsideRelCons,
                        MVELDumper.MVELDumperContext context );

    String processRestriction( MVELDumper.MVELDumperContext context,
                               String left,
                               OperatorDescr operator,
                               String right );

    public Class<?> getEvaluatorWrapperClass();
}