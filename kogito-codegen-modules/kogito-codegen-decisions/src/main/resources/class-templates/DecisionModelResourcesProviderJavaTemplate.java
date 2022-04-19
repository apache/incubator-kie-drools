/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package $Package$;

public class DecisionModelResourcesProvider implements org.kie.kogito.decision.DecisionModelResourcesProvider {

    // See https://issues.redhat.com/browse/KOGITO-3330
    private static java.io.InputStreamReader readResource(java.io.InputStream stream) {
        if (org.kie.kogito.internal.RuntimeEnvironment.isJdk()) {
            return new java.io.InputStreamReader(stream);
        }

        try {
            byte[] bytes = org.drools.util.IoUtils.readBytesFromInputStream(stream);
            java.io.ByteArrayInputStream byteArrayInputStream = new java.io.ByteArrayInputStream(bytes);
            return new java.io.InputStreamReader(byteArrayInputStream);
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    private final static java.util.List<org.kie.kogito.decision.DecisionModelResource> resources = getResources();

    @Override
    public java.util.List<org.kie.kogito.decision.DecisionModelResource> get() {
        return this.resources;
    }

    private final static java.util.List<org.kie.kogito.decision.DecisionModelResource> getResources() {
        java.util.List<org.kie.kogito.decision.DecisionModelResource> resourcePaths = new java.util.ArrayList<>();
        return resourcePaths;
    }

}
