/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workflow.core.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.workflow.core.node.Transformation;

public class DataAssociation implements Serializable {

    private static final long serialVersionUID = 5L;

    private List<DataDefinition> sources;
    private DataDefinition target;
    private List<Assignment> assignments;
    private Transformation transformation;

    public DataAssociation(Assignment assignment) {
        this(Collections.emptyList(), null, Collections.singletonList(assignment), null);
    }

    public DataAssociation(List<DataDefinition> sources, DataDefinition target,
            List<Assignment> assignments, Transformation transformation) {
        this.sources = new ArrayList<>(sources);
        this.target = target;
        this.transformation = transformation;
        this.assignments = assignments == null ? new ArrayList<>() : assignments;
        this.assignments.forEach(this::buildInterpretedAssignment);
    }

    private Assignment buildInterpretedAssignment(Assignment assignment) {
        if (assignment.getDialect() != null) {
            return assignment;
        }
        if (isExpr(assignment.getFrom().getExpression())) {
            assignment.setMetaData("Action", new InputExpressionAssignment(assignment.getFrom(), assignment.getTo()));
        } else if (isExpr(assignment.getTo().getExpression())) {
            assignment.setMetaData("Action", new OutputExpressionAssignment(assignment.getFrom(), assignment.getTo()));
        } else if (assignment.getFrom().hasExpression()) {
            // constants can only be in the source of the expression
            String source = assignment.getFrom().getExpression();
            source = isConstant(source) ? source.substring(1, source.length() - 1) : source;
            assignment.setMetaData("Action", new StaticAssignment(source, assignment.getTo()));
        } else {
            assignment.setMetaData("Action", new SimpleExpressionAssignment(assignment.getFrom(), assignment.getTo()));
        }
        return assignment;
    }

    private boolean isConstant(String expression) {
        return Pattern.matches("\".*\"", expression);
    }

    private boolean isExpr(String mvelExpression) {
        return mvelExpression != null && mvelExpression.contains("#{");
    }

    public DataAssociation(final DataDefinition source, DataDefinition target,
            List<Assignment> assignments, Transformation transformation) {
        this(Collections.singletonList(source), target, assignments, transformation);
    }

    public List<DataDefinition> getSources() {
        return sources != null ? sources : Collections.emptyList();
    }

    public void setSources(List<DataDefinition> sources) {
        this.sources = sources;
    }

    public DataDefinition getTarget() {
        return target;
    }

    public void setTarget(DataDefinition target) {
        this.target = target;
    }

    public List<Assignment> getAssignments() {
        return assignments != null ? assignments : Collections.emptyList();
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public void setTransformation(Transformation transformation) {
        if (transformation != null) {
            throw new UnsupportedOperationException("Transformations are not supported");
        }
    }

    @Override
    public String toString() {
        return "DataAssociation{" +
                "sources=" + sources +
                ", target='" + target + '\'' +
                ", assignments=" + assignments +
                ", transformation=" + transformation +
                '}';
    }
}
