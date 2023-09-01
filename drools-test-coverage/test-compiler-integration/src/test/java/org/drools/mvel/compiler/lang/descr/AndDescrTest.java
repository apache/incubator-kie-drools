package org.drools.mvel.compiler.lang.descr;

import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AndDescrTest {

    @Test
    public void testAddUnboundPatternsEtc() {
        final AndDescr and = new AndDescr();
        and.addDescr( new NotDescr() );
        and.addDescr( new PatternDescr( "Foo" ) );
        and.addDescr( new NotDescr() );

        assertThat(and.getDescrs().size()).isEqualTo(3);
    }

}
