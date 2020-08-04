/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.dmn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.kie.api.management.GAV;
import org.kie.internal.decision.DecisionModelResource;
import org.kie.kogito.decision.DecisionModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseDecisionModelResource implements DecisionModelResource {

    private static final Logger LOG = LoggerFactory.getLogger(BaseDecisionModelResource.class);

    protected final GAV gav;
    protected final String path;
    protected final String namespace;
    protected final String modelName;
    protected final DecisionModelType type;

    protected BaseDecisionModelResource(GAV gav,
                                        String path,
                                        String namespace,
                                        String modelName,
                                        DecisionModelType type) {
        this.gav = gav;
        this.path = path;
        this.namespace = namespace;
        this.modelName = modelName;
        this.type = type;
    }

    @Override
    public GAV getGav() {
        return gav;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public DecisionModelType getModelType() {
        return type;
    }

    @Override
    public String get() {
        try (InputStream is = getInputStream()) {
            return load(is);
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
            throw new RuntimeException(ioe);
        }
    }

    private String load(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader isr = new java.io.InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                sb.append(line).append("\n");
            }
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
            throw new RuntimeException(ioe);
        }
        return sb.toString();
    }
}
