/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.core.score.buildin.hardandsoftlong;

import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.holder.AbstractScoreHolder;

public class HardAndSoftLongScoreHolder extends AbstractScoreHolder {

    protected long hardConstraintsBroken;
    protected long softConstraintsBroken;

    public long getHardConstraintsBroken() {
        return hardConstraintsBroken;
    }

    public void setHardConstraintsBroken(long hardConstraintsBroken) {
        this.hardConstraintsBroken = hardConstraintsBroken;
    }

    public long getSoftConstraintsBroken() {
        return softConstraintsBroken;
    }

    public void setSoftConstraintsBroken(long softConstraintsBroken) {
        this.softConstraintsBroken = softConstraintsBroken;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public Score calculateScore() {
        return DefaultHardAndSoftLongScore.valueOf(-hardConstraintsBroken, -softConstraintsBroken);
    }

}
