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
package org.kie.dmn.typesafe;


public class DMNStronglyCodeGenConfig {

    private boolean withJacksonAnnotation = false;
    private boolean withMPOpenApiAnnotation = false;
    private boolean withIOSwaggerOASv3Annotation = false;

    public boolean isWithJacksonAnnotation() {
        return withJacksonAnnotation;
    }

    public void setWithJacksonAnnotation(boolean withJacksonAnnotation) {
        this.withJacksonAnnotation = withJacksonAnnotation;
    }

    public boolean isWithMPOpenApiAnnotation() {
        return withMPOpenApiAnnotation;
    }

    public void setWithMPOpenApiAnnotation(boolean withMPOpenApiAnnotation) {
        this.withMPOpenApiAnnotation = withMPOpenApiAnnotation;
    }

    public boolean isWithIOSwaggerOASv3Annotation() {
        return withIOSwaggerOASv3Annotation;
    }

    public void setWithIOSwaggerOASv3Annotation(boolean withIOSwaggerOASV3Annotation) {
        this.withIOSwaggerOASv3Annotation = withIOSwaggerOASV3Annotation;
    }

}
