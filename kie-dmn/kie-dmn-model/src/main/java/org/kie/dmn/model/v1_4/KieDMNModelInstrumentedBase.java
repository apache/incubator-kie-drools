/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.model.v1_4;

import org.kie.dmn.model.impl.AbstractKieDMNModelInstrumentedBase;

public abstract class KieDMNModelInstrumentedBase extends AbstractKieDMNModelInstrumentedBase implements URIFEELed {

    public static final String URI_DMN = "https://www.omg.org/spec/DMN/20211108/MODEL/";
    public static final String URI_FEEL = "https://www.omg.org/spec/DMN/20211108/FEEL/";
    public static final String URI_KIE = "https://www.drools.org/kie/dmn/1.4";
    public static final String URI_DMNDI = "https://www.omg.org/spec/DMN/20191111/DMNDI/";
    public static final String URI_DI = "http://www.omg.org/spec/DMN/20180521/DI/";
    public static final String URI_DC = "http://www.omg.org/spec/DMN/20180521/DC/";
}
