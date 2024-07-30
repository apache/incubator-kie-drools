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
package org.kie.kogito.serverless.workflow.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Optional;

import org.kie.kogito.serverless.workflow.parser.ParserContext;

import io.serverlessworkflow.api.Workflow;

import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.getBaseURI;

public class URIContentLoaderFactory {

    public static byte[] readAllBytes(URIContentLoader loader) {
        try (InputStream is = loader.getInputStream()) {
            return is.readAllBytes();
        } catch (IOException io) {
            throw new UncheckedIOException(io);
        }
    }

    public static byte[] readAllBytes(Builder builder) {
        return readAllBytes(builder.build());
    }

    public static String readString(URIContentLoader loader) {
        return new String(readAllBytes(loader));
    }

    public static String readString(Builder builder) {
        return readString(builder.build());
    }

    public static URIContentLoader buildLoader(String uri, Workflow workflow, Optional<ParserContext> context, String authRef) {
        Builder builder = new Builder(uri).withWorkflow(workflow).withAuthRef(authRef);
        context.map(c -> c.getContext().getClassLoader()).ifPresent(builder::withClassloader);
        getBaseURI(workflow).ifPresent(builder::withBaseURI);
        return builder.build();
    }

    public static byte[] readBytes(String uriStr, Workflow workflow, ParserContext parserContext) {
        return readBytes(uriStr, workflow, Optional.ofNullable(parserContext));
    }

    public static byte[] readBytes(String uriStr, Workflow workflow, Optional<ParserContext> parserContext) {
        return readAllBytes(buildLoader(uriStr, workflow, parserContext, null));
    }

    public static Builder builder(URI uri) {
        return new Builder(uri.toString());
    }

    public static Builder builder(String uri) {
        return new Builder(uri);
    }

    public static class Builder {
        private String uri;
        private ClassLoader cl;
        private Workflow workflow;
        private String authRef;
        private String baseURI;

        private Builder(URI uri) {
            this.uri = uri.toString();
        }

        private Builder(String uri) {
            this.uri = uri;
        }

        public Builder withClassloader(ClassLoader cl) {
            this.cl = cl;
            return this;
        }

        public Builder withWorkflow(Workflow workflow) {
            this.workflow = workflow;
            return this;
        }

        public Builder withAuthRef(String authRef) {
            this.authRef = authRef;
            return this;
        }

        public Builder withBaseURI(String baseURI) {
            this.baseURI = baseURI;
            return this;
        }

        public URIContentLoader build() {
            final String finalURI = baseURI != null ? compoundURI(baseURI, uri) : uri;
            switch (URIContentLoaderType.from(finalURI)) {
                default:
                case FILE:
                    return new FileContentLoader(finalURI, new ClassPathContentLoader(uri, Optional.ofNullable(cl)));
                case HTTP:
                    return new HttpContentLoader(finalURI, Optional.ofNullable(workflow), authRef, URIContentLoaderType.HTTP);
                case HTTPS:
                    return new HttpContentLoader(finalURI, Optional.ofNullable(workflow), authRef, URIContentLoaderType.HTTPS);
                case CLASSPATH:
                    Optional<ClassLoader> optionalCl = Optional.ofNullable(cl);
                    return finalURI == uri ? new ClassPathContentLoader(finalURI, optionalCl) : new ClassPathContentLoader(finalURI, optionalCl, new ClassPathContentLoader(uri, optionalCl));
            }
        }
    }

    public static String compoundURI(String baseURI, String uri) {
        return URIContentLoaderType.scheme(uri).isPresent() ? uri : URIContentLoaderType.from(baseURI).concat(baseURI, uri);
    }

    private URIContentLoaderFactory() {
    }
}
