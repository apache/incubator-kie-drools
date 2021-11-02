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
package org.kie.kogito.expr.jq;

import org.kie.kogito.process.workitems.impl.expr.ParsedExpression;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Output;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class JqParsedExpression implements ParsedExpression {

    private Scope scope;
    private JsonQuery query;

    public JqParsedExpression(Scope scope, String expr) {
        this.scope = scope;
        try {
            this.query = JsonQuery.compile(expr, Versions.JQ_1_6);
        } catch (JsonQueryException e) {
            throw new IllegalArgumentException("Unable to compile expression " + expr, e);
        }
    }

    private static class HolderOutput implements Output {
        private JsonNode out;
        private boolean arrayCreated;

        @Override
        public void emit(JsonNode out) throws JsonQueryException {
            if (this.out == null) {
                this.out = out;
            } else if (!arrayCreated) {
                ArrayNode newNode = ObjectMapperFactory.get().createArrayNode();
                newNode.add(this.out).add(out);
                this.out = newNode;
                arrayCreated = true;
            } else {
                ((ArrayNode) this.out).add(out);
            }
        }

        public JsonNode getResult() {
            return out;
        }
    }

    @Override
    public <T> T eval(Object context, Class<T> returnClass) {
        try {
            HolderOutput out = new HolderOutput();
            query.apply(this.scope, (JsonNode) context, out);
            if (Boolean.class.isAssignableFrom(returnClass)) {
                return (T) (Boolean) out.getResult().asBoolean();
            } else if (String.class.isAssignableFrom(returnClass)) {
                return (T) out.getResult().asText();
            } else {
                return (T) out.getResult();
            }
        } catch (JsonQueryException e) {
            throw new IllegalArgumentException("Unable to evaluate content " + context, e);
        }
    }

    private static class ObjectMapperFactory {
        private static ObjectMapper mapper = new ObjectMapper();

        public static ObjectMapper get() {
            return mapper;
        }
    }

}
