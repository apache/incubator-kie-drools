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
package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.PatternDescr;

/**
 *  A descriptor builder for Patterns
 *  
 *  rule.name("Xyz")
 *    .attribute("ruleflow-grou","bla")
 *  .lhs()
 *    .and()
 *      .pattern("Foo").id("$foo").constraint("bar==baz").constraint("x>y").end()
 *      .not().pattern("Bar").constraint("a+b==c").end()
 *    .end()
 *  .rhs( "System.out.println();" )
 *  .end()
 */
public interface PatternDescrBuilder<P extends DescrBuilder<?, ?>>
    extends
    AnnotatedDescrBuilder<PatternDescrBuilder<P>>,
    DescrBuilder<P, PatternDescr> {

    PatternDescrBuilder<P> id( String id, boolean isUnification );
    PatternDescrBuilder<P> type( String type );
    PatternDescrBuilder<P> isQuery( boolean query );
    PatternDescrBuilder<P> constraint( String constraint );
    PatternDescrBuilder<P> constraint( String constraint, boolean positional );
    PatternDescrBuilder<P> bind( String var, String target, boolean isUnification );
    
    SourceDescrBuilder<PatternDescrBuilder<P>> from();
    
    BehaviorDescrBuilder<PatternDescrBuilder<P>> behavior();

}
