/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.machinereassignment.domain.solver;

import org.optaplanner.examples.machinereassignment.domain.MrService;

public class MrServiceDependency {

    private MrService fromService;
    private MrService toService;

    public MrServiceDependency() {
    }

    public MrServiceDependency(MrService fromService, MrService toService) {
        this.fromService = fromService;
        this.toService = toService;
    }

    public MrService getFromService() {
        return fromService;
    }

    public void setFromService(MrService fromService) {
        this.fromService = fromService;
    }

    public MrService getToService() {
        return toService;
    }

    public void setToService(MrService toService) {
        this.toService = toService;
    }

}
