package org.drools.bpel.instance;

import org.drools.bpel.core.BPELAssign;
import org.drools.bpel.core.BPELFlow;
import org.drools.bpel.core.BPELInvoke;
import org.drools.bpel.core.BPELReceive;
import org.drools.bpel.core.BPELReply;
import org.drools.bpel.core.BPELScope;
import org.drools.bpel.core.BPELSequence;
import org.drools.workflow.instance.impl.NodeInstanceFactoryRegistry;
import org.drools.workflow.instance.impl.factory.CreateNewNodeFactory;

public class BPELNodeInstanceFactoryRegistry extends NodeInstanceFactoryRegistry {
    
    public BPELNodeInstanceFactoryRegistry() {
        register( BPELAssign.class,
                  new CreateNewNodeFactory( BPELAssignInstance.class ) );
        register( BPELFlow.class,
                  new CreateNewNodeFactory( BPELFlowInstance.class ) );
        register( BPELInvoke.class,
                  new CreateNewNodeFactory( BPELInvokeInstance.class ) );
        register( BPELReceive.class,
                  new CreateNewNodeFactory( BPELReceiveInstance.class ) );
        register( BPELReply.class,
                  new CreateNewNodeFactory( BPELReplyInstance.class ) );
        register( BPELScope.class,
                  new CreateNewNodeFactory( BPELScopeInstance.class ) );
        register( BPELSequence.class,
                  new CreateNewNodeFactory( BPELSequenceInstance.class ) );
    }

}
