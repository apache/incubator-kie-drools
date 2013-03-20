/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.impl.heuristic.selector.move.factory;

import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.solution.Solution;

/**
 * A simple interface to generate a {@link List} of custom {@link Move}s.
 * <p/>
 * For a more powerful version, see {@link MoveIteratorFactory}.
 */
public interface MoveListFactory {

    /**
     * When it is called depends on the configured {@link SelectionCacheType}.
     * <p/>
     * It can never support {@link SelectionCacheType#JUST_IN_TIME},
     * because it returns a {@link List}, not an {@link Iterator}.
     * @param solution never null, the {@link Solution} of which the {@link Move}s need to be generated
     * @return never null
     */
    List<Move> createMoveList(Solution solution);

}
