/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.commons.model.expressions;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Stream;

import org.kie.pmml.api.enums.AGGREGATE_FUNCTIONS;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

import static org.kie.pmml.commons.model.expressions.ExpressionsUtils.getFromPossibleSources;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Transformations.html#xsdElement_Aggregate>Aggregate</a>
 */
public class KiePMMLAggregate extends AbstractKiePMMLComponent implements KiePMMLExpression {

    private static final long serialVersionUID = -6975232157053159223L;
    private final AGGREGATE_FUNCTIONS function;
    private String groupField;
    private String sqlWhere;

    private KiePMMLAggregate(String name, List<KiePMMLExtension> extensions, AGGREGATE_FUNCTIONS function) {
        super(name, extensions);
        this.function = function;
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions, AGGREGATE_FUNCTIONS function) {
        return new Builder(name, extensions, function);
    }

    @Override
    public Object evaluate(final ProcessingDTO processingDTO) {
        Optional<Object> fieldValue =  getFromPossibleSources(name, processingDTO);
        if (!fieldValue.isPresent()) {
            return null;
        }
        if (!(fieldValue.get() instanceof String)) {
            throw new IllegalArgumentException("Can not evaluate aggregate on field " + name + " whose value is " + fieldValue.get());
        }
        String[] stringFieldValues = ((String) fieldValue.get()).split(" ");
        String[] groupBy = null;
        if (groupField != null) {
            Optional<Object> groupValue = getFromPossibleSources(name, processingDTO);
            if (groupValue.isPresent()) {
                if (!(groupValue.get() instanceof String)) {
                    throw new IllegalArgumentException("Can not evaluate aggregate on groupField " + groupField + " whose value is " + groupValue.get());
                } else {
                    groupBy = ((String) groupValue.get()).split(" ");
                }
            }
        }
        Object[] inputData = getInputData(stringFieldValues, function);
        return function.getValue(inputData, groupBy);
    }

    public AGGREGATE_FUNCTIONS getFunction() {
        return function;
    }

    public String getGroupField() {
        return groupField;
    }

    public String getSqlWhere() {
        return sqlWhere;
    }

    static Object[] getInputData(String[] stringFieldValues, AGGREGATE_FUNCTIONS function) {
        if (function.requiresNumbers()) {
            return Stream.of(stringFieldValues).map(Double::valueOf).toArray(Object[]::new);
        } else {
            return stringFieldValues;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KiePMMLAggregate that = (KiePMMLAggregate) o;
        return function == that.function && Objects.equals(groupField, that.groupField) && Objects.equals(sqlWhere,
                                                                                                          that.sqlWhere);
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, groupField, sqlWhere);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KiePMMLAggregate.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("id='" + id + "'")
                .add("parentId='" + parentId + "'")
                .add("function=" + function)
                .add("groupField='" + groupField + "'")
                .add("sqlWhere='" + sqlWhere + "'")
                .toString();
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLAggregate> {

        private Builder(String name, List<KiePMMLExtension> extensions, AGGREGATE_FUNCTIONS function) {
            super("Aggregate-", () -> new KiePMMLAggregate(name, extensions, function));
        }

        public Builder withGroupField(String groupField) {
            if (groupField != null) {
                toBuild.groupField = groupField;
            }
            return this;
        }

        public Builder withSqlWhere(String sqlWhere) {
            if (sqlWhere != null) {
                toBuild.sqlWhere = sqlWhere;
            }
            return this;
        }

    }
}
