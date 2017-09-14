/*
 * Copyright 2005 JBoss Inc
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

package org.drools.model.patterns;

import java.util.List;
import java.util.stream.Stream;

import org.drools.model.Condition;
import org.drools.model.OOPath;
import org.drools.model.Source;
import org.drools.model.Variable;
import org.drools.model.view.OOPathViewItem.OOPathChunk;

public class OOPathImpl implements OOPath {

    private final Source<?> source;
    private final List<OOPathChunk<?>> chunks;
    private Condition firstCondition;

    public OOPathImpl( Source<?> source, List<OOPathChunk<?>> chunks ) {
        this.source = source;
        this.chunks = chunks;
    }

    @Override
    public Type getType() {
        return Type.OOPATH;
    }

    @Override
    public Variable<?>[] getBoundVariables() {
        return chunks.stream()
                     .map(OOPathChunk::getExpr)
                     .flatMap( expr -> Stream.of( expr.getVariables() ) )
                     .distinct()
                     .toArray(Variable[]::new );
    }

    public void setFirstCondition( Condition condition ) {
        this.firstCondition = condition;
    }

    @Override
    public Condition getFirstCondition() {
        return firstCondition;
    }

    @Override
    public Source<?> getSource() {
        return source;
    }

    @Override
    public List<OOPathChunk<?>> getChunks() {
        return chunks;
    }
}
