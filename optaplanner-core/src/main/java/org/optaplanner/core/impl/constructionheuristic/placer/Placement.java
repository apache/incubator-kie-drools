/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.constructionheuristic.placer;

import java.io.Serializable;
import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.move.Move;

public class Placement implements Iterable<Move>, Serializable {

    private final Iterator<Move> moveIterator;

    public Placement(Iterator<Move> moveIterator) {
        this.moveIterator = moveIterator;
    }

    @Override
    public Iterator<Move> iterator() {
        return moveIterator;
    }

    @Override
    public String toString() {
        return "Placement (" + moveIterator + ")";
    }

}
