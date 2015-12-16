/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.commons.backend.rule.context;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.IAction;

/**
 * Factory for Generator Contexts
 */
public class RHSGeneratorContextFactory {

    private List<RHSGeneratorContext> contexts = new ArrayList<RHSGeneratorContext>();

    public RHSGeneratorContext newGeneratorContext() {
        final RHSGeneratorContext gc = new RHSGeneratorContext();
        contexts.add( gc );
        return gc;
    }

    public RHSGeneratorContext newChildGeneratorContext( final RHSGeneratorContext parent,
                                                         final IAction action ) {
        final RHSGeneratorContext gc = new RHSGeneratorContext( parent,
                                                                action,
                                                                getMaximumDepth() + 1,
                                                                parent.getOffset() );
        contexts.add( gc );
        return gc;
    }

    public RHSGeneratorContext newChildGeneratorContext( final RHSGeneratorContext parent,
                                                         final ActionFieldValue afv ) {
        final RHSGeneratorContext gc = new RHSGeneratorContext( parent,
                                                                afv,
                                                                getMaximumDepth() + 1,
                                                                parent.getOffset() );
        contexts.add( gc );
        return gc;
    }

    public RHSGeneratorContext newPeerGeneratorContext( final RHSGeneratorContext peer,
                                                        final ActionFieldValue afv ) {
        final RHSGeneratorContext gc = new RHSGeneratorContext( peer.getParent(),
                                                                afv,
                                                                peer.getDepth(),
                                                                peer.getOffset() + 1 );
        contexts.add( gc );
        return gc;
    }

    public List<RHSGeneratorContext> getGeneratorContexts() {
        return contexts;
    }

    private int getMaximumDepth() {
        int depth = 0;
        for ( RHSGeneratorContext gctx : contexts ) {
            depth = Math.max( depth,
                              gctx.getDepth() );
        }
        return depth;
    }

    public List<RHSGeneratorContext> getPeers( final RHSGeneratorContext peer ) {
        final List<RHSGeneratorContext> peers = new ArrayList<RHSGeneratorContext>();
        for ( RHSGeneratorContext c : contexts ) {
            if ( c.getDepth() == peer.getDepth() ) {
                peers.add( c );
            }
        }
        peers.remove( peer );
        return peers;
    }

}
