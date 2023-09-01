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
package org.drools.compiler.lang;


import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ConstraintConnectiveDescr;
import org.drools.drl.ast.descr.OperatorDescr;

public interface ExpressionRewriter {

    String dump( BaseDescr base );

    String dump( BaseDescr base,
                 DumperContext context );

    String dump( BaseDescr base,
                 ConstraintConnectiveDescr parent,
                 DumperContext context );

    String dump( BaseDescr base,
                 int parentPrecedence );

    StringBuilder dump( StringBuilder sbuilder,
                        BaseDescr base,
                        int parentPriority,
                        boolean isInsideRelCons,
                        DumperContext context );

    StringBuilder dump( StringBuilder sbuilder,
                        BaseDescr base,
                        ConstraintConnectiveDescr parent,
                        int parentIndex,
                        int parentPriority,
                        boolean isInsideRelCons,
                        DumperContext context );

    String processRestriction( DumperContext context,
                               String left,
                               OperatorDescr operator,
                               String right );
}