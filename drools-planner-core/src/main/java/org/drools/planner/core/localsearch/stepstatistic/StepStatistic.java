/**
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.core.localsearch.stepstatistic;

/**
 * @author Geoffrey De Smet
 */
public interface StepStatistic { // TODO This isn't used anywhere

    /**
     * How much of all the selectable moves should be evaluated for the current step.
     * @return a number > 0 and <= 1.0
     */
    double getSelectorThoroughness(); // TODO this is a new feature to implement somewhere 

}
