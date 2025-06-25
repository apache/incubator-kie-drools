/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.impl.AbstractKieDMNModelInstrumentedBase;

public abstract class KieDMNModelInstrumentedBase extends AbstractKieDMNModelInstrumentedBase implements URIFEELed {

    public static final String URI_FEEL = "http://www.omg.org/spec/FEEL/20140401";
    public static final String URI_DMN = "http://www.omg.org/spec/DMN/20151101/dmn.xsd";
    public static final String URI_KIE = "http://www.drools.org/kie/dmn/1.1";
}
