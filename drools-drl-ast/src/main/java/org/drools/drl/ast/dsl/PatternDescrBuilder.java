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
