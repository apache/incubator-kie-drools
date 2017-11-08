/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.drools.model.impl.NamesGenerator.generateName;

public class DRLIdGenerator {
    private Map<PatternTypeDRLConstraint, String> generatedExprIds = new HashMap<>();
    private Map<PatternTypeDRLConstraint, String> generatedCondIds = new HashMap<>();
    private Map<PatternTypeDRLConstraint, String> generateOOPathId = new HashMap<>();
    private Map<PatternTypeDRLConstraint, String> generateUnificationVariableId = new HashMap<>();

    public String getExprId(Class<?> patternType, String drlConstraint) {
        PatternTypeDRLConstraint key = PatternTypeDRLConstraint.of(patternType, drlConstraint);
        return generatedExprIds.computeIfAbsent(key, k -> generateNewId());
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        generatedExprIds.forEach((k, v) -> sb.append(v+": "+k+"\n"));
        return sb.toString();
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
            return "" + patternType.getName() + "( " + drlConstraint + " )";
        }
        
    }
}
