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

package org.drools.planner.examples.trailerrouting.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("TrailerRoutingDriver")
public class TrailerRoutingDriver extends TrailerRoutingResource {

    private boolean availableBdLicense;
    private boolean availableSidelifterLicense;

    public boolean isAvailableBdLicense() {
        return availableBdLicense;
    }

    public void setAvailableBdLicense(boolean availableBdLicense) {
        this.availableBdLicense = availableBdLicense;
    }

    public boolean isAvailableSidelifterLicense() {
        return availableSidelifterLicense;
    }

    public void setAvailableSidelifterLicense(boolean availableSidelifterLicense) {
        this.availableSidelifterLicense = availableSidelifterLicense;
    }

}
