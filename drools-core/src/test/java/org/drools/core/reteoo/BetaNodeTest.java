package org.drools.core.reteoo;

import java.util.Collections;

import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.reteoo.builder.BuildContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BetaNodeTest {

    @Test
    public void testEqualsObject() {
        InternalRuleBase kBase = RuleBaseFactory.newRuleBase();

        BuildContext buildContext = new BuildContext( kBase, Collections.emptyList() );

        final LeftTupleSource ts = new MockTupleSource( 1, buildContext );
        final ObjectSource os = new MockObjectSource( 2, buildContext );
        
        final BetaNode j1 = new JoinNode( 1,
                                          ts,
                                          os,
                                          EmptyBetaConstraints.getInstance(),
                                          buildContext );
        final BetaNode j2 = new JoinNode( 2,
                                          ts,
                                          os,
                                          EmptyBetaConstraints.getInstance(),
                                          buildContext );
        final BetaNode n1 = new NotNode( 3,
                                         ts,
                                         os,
                                         EmptyBetaConstraints.getInstance(),
                                         buildContext );
        final BetaNode n2 = new NotNode( 4,
                                         ts,
                                         os,
                                         EmptyBetaConstraints.getInstance(),
                                         buildContext );

        assertThat(j1).isEqualTo(j1);
        assertThat(j2).isEqualTo(j2);
        assertThat(j2).isEqualTo(j1);
        assertThat(n1).isEqualTo(n1);
        assertThat(n2).isEqualTo(n2);
        assertThat(n2).isEqualTo(n1);

        assertThat(j1.equals(n1)).isFalse();
        assertThat(j1.equals(n2)).isFalse();
        assertThat(n1.equals(j1)).isFalse();
        assertThat(n1.equals(j2)).isFalse();
    }

}
