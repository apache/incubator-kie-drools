/*
 * Copyright 2015 JBoss Inc
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

    public String dump( BaseDescr base );

    public String dump( BaseDescr base,
                        MVELDumper.MVELDumperContext context );

    public String dump( BaseDescr base,
                        ConstraintConnectiveDescr parent,
                        MVELDumper.MVELDumperContext context );

    public String dump( BaseDescr base,
                        int parentPrecedence );

    public StringBuilder dump( StringBuilder sbuilder,
                               BaseDescr base,
                               int parentPriority,
                               boolean isInsideRelCons,
                               MVELDumper.MVELDumperContext context );

    public StringBuilder dump( StringBuilder sbuilder,
                               BaseDescr base,
                               ConstraintConnectiveDescr parent,
                               int parentIndex,
                               int parentPriority,
                               boolean isInsideRelCons,
                               MVELDumper.MVELDumperContext context );

    public void processRestriction( MVELDumper.MVELDumperContext context,
                                    StringBuilder sbuilder,
                                    String left,
                                    OperatorDescr operator,
                                    String right );

    public Class<?> getEvaluatorWrapperClass();
}