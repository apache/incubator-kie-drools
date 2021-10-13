/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.io;

import java.util.Properties;


/**
 * This interface is a marker interface and should be implemented by any class
 * that will provide configurations to the {@link org.kie.api.builder.KieBuilder} - currently this is
 * only used by decision tables.
 */
public interface ResourceConfiguration {

    public Properties toProperties();
    public ResourceConfiguration fromProperties( Properties prop );

}
