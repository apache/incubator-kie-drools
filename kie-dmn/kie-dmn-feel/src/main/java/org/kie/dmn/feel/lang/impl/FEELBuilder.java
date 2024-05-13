/**
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
package org.kie.dmn.feel.lang.impl;

import java.util.Collections;
import java.util.List;

import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.util.ClassLoaderUtil;

/**
 * <code>FEELImpl</code> builder
 */
public class FEELBuilder {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ClassLoader classLoader;
        private List<FEELProfile> profiles;
        private FEELDialect feelDialect;

        private Builder() {
        }

        public Builder withClassloader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public Builder withProfiles(List<FEELProfile> profiles) {
            this.profiles = profiles;
            return this;
        }

        public Builder withFEELDialect(FEELDialect feelDialect) {
            this.feelDialect = feelDialect;
            return this;
        }

        public FEEL build() {
            ClassLoader classLoaderToUse = classLoader != null ? classLoader : ClassLoaderUtil.findDefaultClassLoader();
            List<FEELProfile> profilesToUse = profiles != null ? profiles : Collections.emptyList();
            FEELDialect feelDialectToUse = feelDialect != null ? feelDialect : FEELDialect.FEEL;
            return new FEELImpl(classLoaderToUse, profilesToUse, feelDialectToUse);
        }
    }
}
