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
package org.drools.model.codegen.execmodel.generator;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.drools.util.StringUtils.md5Hash;
import static org.drools.model.impl.NamesGenerator.generateName;
import static org.drools.model.impl.VariableImpl.GENERATED_VARIABLE_PREFIX;

public class DRLIdGenerator {

    private Map<PatternTypeDRLConstraint, String> generatedCondIds = new ConcurrentHashMap<>();
    private Map<PatternTypeDRLConstraint, String> generateOOPathId = new ConcurrentHashMap<>();
    private Map<PatternTypeDRLConstraint, String> generateUnificationVariableId = new ConcurrentHashMap<>();
    private Map<PatternTypeDRLConstraint, String> generateAccumulateBindingId = new ConcurrentHashMap<>();

    public String getExprId(Class<?> patternType, String drlConstraint) {
        return GENERATED_VARIABLE_PREFIX + md5Hash(patternType + drlConstraint);
    }

    public boolean isGenerated(String id) {
        return id.startsWith( GENERATED_VARIABLE_PREFIX );
    }

    public String getCondId(Class<?> patternType, String drlConstraint) {
        PatternTypeDRLConstraint key = PatternTypeDRLConstraint.of(patternType, drlConstraint);
        return generatedCondIds.computeIfAbsent(key, k -> generateNewCond());
    }

    public String getOOPathId(Class<?> patternType, String drlConstraint) {
        PatternTypeDRLConstraint key = PatternTypeDRLConstraint.of(patternType, drlConstraint);
        return generateOOPathId.computeIfAbsent(key, k -> generateOOPathExpr());
    }

    public String getOrCreateUnificationVariable(String drlConstraint) {
        PatternTypeDRLConstraint key = PatternTypeDRLConstraint.of(Object.class, drlConstraint);
        return generateUnificationVariableId.computeIfAbsent(key, k -> generateUnificationExpr());
    }

    public Optional<String> getUnificationVariable(String drlConstraint) {
        PatternTypeDRLConstraint key = PatternTypeDRLConstraint.of(Object.class, drlConstraint);
        return Optional.ofNullable(generateUnificationVariableId.get(key));
    }

    public String getOrCreateAccumulateBindingId(String drlConstraint) {
        PatternTypeDRLConstraint key = PatternTypeDRLConstraint.of(Object.class, drlConstraint);
        return generateAccumulateBindingId.computeIfAbsent(key, k -> generateAccumulatorBindingId());
    }

    private String generateNewId() {
        return generateName("expr");
    }

    private String generateNewCond() {
        return generateName("cond");
    }

    private String generateOOPathExpr() {
        return generateName("ooChunk");
    }

    private String generateUnificationExpr() {
        return generateName("unificationExpr");
    }

    private String generateAccumulatorBindingId() {
        return generateName("accBindingId");
    }

    private static class PatternTypeDRLConstraint {
        public final Class<?> patternType;
        public final String drlConstraint;
        public PatternTypeDRLConstraint(Class<?> patternType, String drlConstraint) {
            this.patternType = patternType;
            this.drlConstraint = drlConstraint;
        }
        public static PatternTypeDRLConstraint of(Class<?> patternType, String drlConstraint) {
            return new PatternTypeDRLConstraint(patternType, drlConstraint);
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((drlConstraint == null) ? 0 : drlConstraint.hashCode());
            result = prime * result + ((patternType == null) ? 0 : patternType.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PatternTypeDRLConstraint other = (PatternTypeDRLConstraint) obj;
            if (drlConstraint == null) {
                if (other.drlConstraint != null)
                    return false;
            } else if (!drlConstraint.equals(other.drlConstraint))
                return false;
            if (patternType == null) {
                if (other.patternType != null)
                    return false;
            } else if (!patternType.equals(other.patternType))
                return false;
            return true;
        }
        @Override
        public String toString() {
            return "" + ((patternType != null) ? patternType.getName() : "<no patternType>") + "( " + drlConstraint + " )";
        }
    }
}
