/**
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
package org.kie.dmn.model.api.dmndi;

import javax.xml.namespace.QName;

public interface DMNShape extends Shape {

    public DMNLabel getDMNLabel();

    public void setDMNLabel(DMNLabel value);

    public DMNDecisionServiceDividerLine getDMNDecisionServiceDividerLine();

    public void setDMNDecisionServiceDividerLine(DMNDecisionServiceDividerLine value);

    public QName getDmnElementRef();

    public void setDmnElementRef(QName value);

    public Boolean isIsListedInputData();

    public void setIsListedInputData(Boolean value);

    public boolean isIsCollapsed();

    public void setIsCollapsed(Boolean value);

}
