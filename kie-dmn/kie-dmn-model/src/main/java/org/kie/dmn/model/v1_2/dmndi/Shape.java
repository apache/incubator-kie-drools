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
package org.kie.dmn.model.v1_2.dmndi;

public abstract class Shape extends DiagramElement implements org.kie.dmn.model.api.dmndi.Shape {

    protected org.kie.dmn.model.api.dmndi.Bounds bounds;

    /**
     * the optional bounds of the shape relative to the origin of its nesting plane.
     * 
     * @return
     *     possible object is
     *     {@link Bounds }
     *     
     */
    public org.kie.dmn.model.api.dmndi.Bounds getBounds() {
        return bounds;
    }

    /**
     * Sets the value of the bounds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Bounds }
     *     
     */
    public void setBounds(org.kie.dmn.model.api.dmndi.Bounds value) {
        this.bounds = value;
    }

}
