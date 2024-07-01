package org.drools.mvel.integrationtests.phreak.sequencing;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.ClockType;
import org.drools.core.SessionConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.MockLeftTupleSink;
import org.drools.core.reteoo.MultiInputNode;
import org.drools.core.reteoo.MultiInputNode.DynamicFilterProto;
import org.drools.core.reteoo.MultiInputNode.MultiInputNodeMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.sequencing.Sequence;
import org.drools.core.reteoo.sequencing.Sequencer.SequencerMemory;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.kiesession.rulebase.SessionsAwareKnowledgeBase;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.integrationtests.phreak.A;
import org.kie.api.runtime.conf.ThreadSafeOption;
import org.kie.internal.conf.CompositeBaseConfiguration;

import java.util.Collections;

public class AbstractPhreakSequencerSubsequenceTest {
    StatefulKnowledgeSessionImpl session;
    MultiInputNodeMemory nodeMemory;
    SequencerMemory  sequencerMemory;
    BuildContext   buildContext;
    Sequence       seq0;
    Sequence       seq1;
    Sequence       seq2;
    MultiInputNode mnode;

    DynamicFilterProto bfilter;
    DynamicFilterProto cfilter;
    DynamicFilterProto dfilter;
    DynamicFilterProto efilter;

    public static BuildContext createContext() {

        CompositeBaseConfiguration conf = (CompositeBaseConfiguration) RuleBaseFactory.newKnowledgeBaseConfiguration();

        KnowledgeBaseImpl rbase = new KnowledgeBaseImpl("ID",
                                                        conf );
        BuildContext buildContext = new BuildContext(rbase, Collections.emptyList() );

        RuleImpl                 rule = new RuleImpl("rule1").setPackage("org.pkg1");
        InternalKnowledgePackage pkg  = CoreComponentFactory.get().createKnowledgePackage("org.pkg1");
        pkg.getDialectRuntimeRegistry().setDialectData( "java", new JavaDialectRuntimeData());

        pkg.addRule( rule );
        buildContext.setRule( rule );

        return buildContext;
    }

    void createSession() {
        SessionsAwareKnowledgeBase kbase       = new SessionsAwareKnowledgeBase(buildContext.getRuleBase());
        SessionConfiguration       sessionConf = kbase.getSessionConfiguration();
        sessionConf.setOption(ThreadSafeOption.YES);
        sessionConf.setClockType(ClockType.PSEUDO_CLOCK);

        if (session != null) {
            nodeMemory      = null;
            sequencerMemory = null;
            session.dispose();
            session = null;
        }

        session = (StatefulKnowledgeSessionImpl) kbase.newKieSession(sessionConf, null);

        InternalFactHandle fhA0 = (InternalFactHandle) session.insert(new A(0));
        nodeMemory = session.getNodeMemory(mnode);
        LeftTuple lt = new LeftTuple(fhA0, mnode, true);
        lt.setContextObject(mnode.createSequencerMemory(lt, new MockLeftTupleSink(buildContext.getNextNodeId(), buildContext), nodeMemory));
        nodeMemory.getLeftTupleMemory().add(lt);
        sequencerMemory = (SequencerMemory) lt.getContextObject();
    }
}
