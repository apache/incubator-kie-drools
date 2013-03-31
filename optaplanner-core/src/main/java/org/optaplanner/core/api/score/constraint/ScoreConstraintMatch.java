/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.api.score.constraint;

import java.io.Serializable;
import java.util.List;

public abstract class ScoreConstraintMatch implements Serializable {

    protected final List<Object> justificationList;

    protected ScoreConstraintMatch(List<Object> justificationList) {
        this.justificationList = justificationList;
    }

    public abstract ScoreConstraintMatchTotal getScoreConstraintMatchTotal();

    public List<Object> getJustificationList() {
        return justificationList;
    }

    public abstract Number getWeightAsNumber();

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    private String getIdentificationString() {
        return getScoreConstraintMatchTotal().getIdentificationString() + "/" + justificationList;
    }

    public String toString() {
        return getIdentificationString()  + "=" + getWeightAsNumber();
    }

}
