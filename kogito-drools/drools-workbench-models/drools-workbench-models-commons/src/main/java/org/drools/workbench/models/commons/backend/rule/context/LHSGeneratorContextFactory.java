/*
 * Copyright 2013 JBoss Inc
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

import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.IPattern;

/**
 * Factory for Generator Contexts
 */
public class LHSGeneratorContextFactory {

    private List<LHSGeneratorContext> contexts = new ArrayList<LHSGeneratorContext>();

    public LHSGeneratorContext newGeneratorContext() {
        final LHSGeneratorContext gc = new LHSGeneratorContext();
        contexts.add( gc );
        return gc;
    }

    public LHSGeneratorContext newChildGeneratorContext( final LHSGeneratorContext parent,
                                                         final IPattern pattern ) {
        final LHSGeneratorContext gc = new LHSGeneratorContext( parent,
                                                                pattern,
                                                                getMaximumDepth() + 1,
                                                                parent.getOffset() );
        contexts.add( gc );
        return gc;
    }

    public LHSGeneratorContext newChildGeneratorContext( final LHSGeneratorContext parent,
                                                         final FieldConstraint fieldConstraint ) {
        final LHSGeneratorContext gc = new LHSGeneratorContext( parent,
                                                                fieldConstraint,
                                                                getMaximumDepth() + 1,
                                                                parent.getOffset() );
        contexts.add( gc );
        return gc;
    }

    public LHSGeneratorContext newPeerGeneratorContext( final LHSGeneratorContext peer,
                                                        final FieldConstraint fieldConstraint ) {
        final LHSGeneratorContext gc = new LHSGeneratorContext( peer.getParent(),
                                                                fieldConstraint,
                                                                peer.getDepth(),
                                                                peer.getOffset() + 1 );
        contexts.add( gc );
        return gc;
    }

    public List<LHSGeneratorContext> getGeneratorContexts() {
        return contexts;
    }

    private int getMaximumDepth() {
        int depth = 0;
        for ( LHSGeneratorContext gctx : contexts ) {
            depth = Math.max( depth,
                              gctx.getDepth() );
        }
        return depth;
    }

    public List<LHSGeneratorContext> getPeers( final LHSGeneratorContext peer ) {
        final List<LHSGeneratorContext> peers = new ArrayList<LHSGeneratorContext>();
        for ( LHSGeneratorContext c : contexts ) {
            if ( c.getDepth() == peer.getDepth() ) {
                peers.add( c );
            }
        }
        peers.remove( peer );
        return peers;
    }

}
