/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.testcontainers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.ImageNameSubstitutor;

public class KogitoImageNameSubstitutor extends ImageNameSubstitutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoImageNameSubstitutor.class);

    @Override
    public DockerImageName apply(DockerImageName original) {
        LOGGER.debug("Original Docker image used by TestContainers: {}", original);
        String canonicalName = original.asCanonicalNameString();

        if (canonicalName.startsWith("mongo:")) {
            return getMongoImageSubstitute(canonicalName);
        } else {
            return original;
        }
    }

    private DockerImageName getMongoImageSubstitute(String canonicalName) {
        return DockerImageName.parse("library/" + canonicalName).asCompatibleSubstituteFor("mongo");
    }

    @Override
    protected String getDescription() {
        return "Kogito Image Name Substitutor";
    }
}
