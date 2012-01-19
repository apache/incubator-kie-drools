/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.lang.api;

import org.drools.lang.descr.PatternDescr;

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

    public PatternDescrBuilder<P> id( String id, boolean isUnification );
    public PatternDescrBuilder<P> type( String type );
    public PatternDescrBuilder<P> isQuery( boolean query );
    public PatternDescrBuilder<P> constraint( String constraint );
    public PatternDescrBuilder<P> constraint( String constraint, boolean positional );
    public PatternDescrBuilder<P> bind( String var, String target, boolean isUnification );
    
    public SourceDescrBuilder<PatternDescrBuilder<P>> from();
    
    public BehaviorDescrBuilder<PatternDescrBuilder<P>> behavior();

}
