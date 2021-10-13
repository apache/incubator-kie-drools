/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.internal.runtime.manager.deploy;

import javax.xml.bind.annotation.XmlTransient;

import org.kie.internal.runtime.conf.ObjectModel;

@XmlTransient
public class TransientObjectModel extends ObjectModel {
    private static final long serialVersionUID = -8210248739969022897L;

    public TransientObjectModel() {
        super();
    }

    public TransientObjectModel(String identifier, Object... parameters) {
        super(identifier, parameters);
    }

    public TransientObjectModel(String resolver, String identifier, Object... parameters) {
        super(resolver, identifier, parameters);
    }


}
