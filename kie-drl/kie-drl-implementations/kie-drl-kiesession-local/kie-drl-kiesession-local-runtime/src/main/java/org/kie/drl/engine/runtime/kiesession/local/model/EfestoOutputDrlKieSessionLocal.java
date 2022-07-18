/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.drl.engine.runtime.kiesession.local.model;

import org.kie.api.runtime.KieSession;
import org.kie.efesto.common.api.model.FRI;
import org.kie.drl.engine.runtime.model.EfestoOutputDrl;

/**
 * <code>EfestoOutputDrl</code> specific for local kiesession usage.
 * Its only scope it is to return a <code>KieSession</code> instance.
 *
 * The returned <code>FRI</code> will contain the session id as last element of the path
 */
public class EfestoOutputDrlKieSessionLocal extends EfestoOutputDrl<KieSession> {

    public EfestoOutputDrlKieSessionLocal(FRI fri, KieSession kieSession) {
        super(fri, kieSession);
    }
}
