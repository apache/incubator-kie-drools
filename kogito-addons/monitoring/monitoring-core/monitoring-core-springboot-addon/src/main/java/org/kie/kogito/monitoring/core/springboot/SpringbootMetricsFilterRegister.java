/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.monitoring.core.springboot;

import org.kie.kogito.monitoring.core.common.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringbootMetricsFilterRegister implements WebMvcConfigurer {

    @Value(value = "${" + Constants.HTTP_INTERCEPTOR_USE_DEFAULT + ":true}")
    boolean httpInterceptorUseDefault;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if(httpInterceptorUseDefault) {
            registry.addInterceptor(new SpringbootMetricsInterceptor());
        }
    }

    // for testing purpose
    void setHttpInterceptorUseDefault(boolean httpInterceptorUseDefault) {
        this.httpInterceptorUseDefault = httpInterceptorUseDefault;
    }
}