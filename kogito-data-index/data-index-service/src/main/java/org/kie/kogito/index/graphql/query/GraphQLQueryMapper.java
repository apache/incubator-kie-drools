/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.graphql.query;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLTypeUtil;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.FilterCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.and;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.between;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.contains;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.containsAll;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.containsAny;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.equalTo;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.greaterThan;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.greaterThanEqual;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.in;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.isNull;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.lessThan;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.lessThanEqual;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.like;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.notNull;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.or;

public class GraphQLQueryMapper implements Function<GraphQLInputObjectType, GraphQLQueryParser> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLQueryMapper.class);

    @Override
    public GraphQLQueryParser apply(GraphQLInputObjectType type) {
        GraphQLQueryParser parser = new GraphQLQueryParser();

        type.getFields().forEach(
                field -> {
                    LOGGER.debug("Parser type: {}, field = {}:{}", type.getName(), field.getName(), GraphQLTypeUtil.simplePrint(field.getType()));
                    if (isEnumFilterType(field.getType())) {
                        parser.mapAttribute(field.getName(), mapEnumArgument(field.getName()));
                    } else if ((field.getType() instanceof GraphQLList) || ((GraphQLNamedType) field.getType()).getName().equals(type.getName())) {
                        parser.mapAttribute(field.getName(), mapRecursiveArgument(field.getName(), parser));
                    } else {
                        String name = ((GraphQLNamedType) field.getType()).getName();
                        switch (name) {
                            case "IdArgument":
                                parser.mapAttribute(field.getName(), mapIdArgument(field.getName()));
                                break;
                            case "StringArgument":
                                parser.mapAttribute(field.getName(), mapStringArgument(field.getName()));
                                break;
                            case "StringArrayArgument":
                                parser.mapAttribute(field.getName(), mapStringArrayArgument(field.getName()));
                                break;
                            case "BooleanArgument":
                                parser.mapAttribute(field.getName(), mapBooleanArgument(field.getName()));
                                break;
                            case "DateArgument":
                                parser.mapAttribute(field.getName(), mapDateArgument(field.getName()));
                                break;
                            case "NumericArgument":
                                parser.mapAttribute(field.getName(), mapNumericArgument(field.getName()));
                                break;
                            case "KogitoMetadataArgument":
                                parser.mapAttribute(field.getName(), mapSubEntityArgument(field.getName(), GraphQLQueryParserRegistry.get().getParser("KogitoMetadataArgument")));
                                break;
                            default:
                                parser.mapAttribute(field.getName(), mapSubEntityArgument(field.getName(), new GraphQLQueryMapper().apply((GraphQLInputObjectType) field.getType())));
                        }
                    }
                }
        );

        return parser;
    }

    //See ProcessInstanceStateArgument
    private boolean isEnumFilterType(GraphQLInputType inputType) {
        if (!(inputType instanceof GraphQLInputObjectType)) {
            return false;
        }

        GraphQLInputObjectType type = (GraphQLInputObjectType) inputType;
        if (type.getFields().isEmpty()) {
            return false;
        }
        return type.getFields().stream().filter(f -> {
            if (f.getType() instanceof GraphQLEnumType) {
                return true;
            } else if (f.getType() instanceof GraphQLList) {
                return (((GraphQLList) f.getType()).getWrappedType() instanceof GraphQLEnumType);
            } else {
                return false;
            }
        }).collect(Collectors.counting()) == type.getFields().size();
    }

    private Function<Object, Stream<AttributeFilter<?>>> mapRecursiveArgument(String joining, GraphQLQueryParser parser) {
        return argument -> {
            Stream<AttributeFilter<?>> stream = ((List) argument).stream().flatMap(args -> parser.apply(args).stream());
            List<AttributeFilter<?>> filters = stream.collect(toList());
            FilterCondition condition = FilterCondition.fromLabel(joining);
            switch (condition) {
                case AND:
                    return Stream.of(and(filters));
                case OR:
                    return Stream.of(or(filters));
                default:
                    return null;
            }
        };
    }

    private Function<Object, Stream<AttributeFilter<?>>> mapSubEntityArgument(String joining, GraphQLQueryParser parser) {
        return argument -> parser.apply((Map<String, Object>) argument).stream().map(f -> {
            f.setAttribute(joining + "." + f.getAttribute());
            return f;
        });
    }

    private Function<Object, Stream<AttributeFilter<?>>> mapIdArgument(String attribute) {
        return argument ->
                ((Map<String, Object>) argument).entrySet().stream().map(entry -> {
                    FilterCondition condition = FilterCondition.fromLabel(entry.getKey());
                    switch (condition) {
                        case IN:
                            return in(attribute, (List) entry.getValue());
                        case EQUAL:
                            return equalTo(attribute, entry.getValue().toString());
                        case IS_NULL:
                            return Boolean.TRUE.equals(entry.getValue()) ? isNull(attribute) : notNull(attribute);
                        default:
                            return null;
                    }
                });
    }

    private Function<Object, Stream<AttributeFilter<?>>> mapStringArgument(String attribute) {
        return argument ->
                ((Map<String, Object>) argument).entrySet().stream().map(entry -> {
                    FilterCondition condition = FilterCondition.fromLabel(entry.getKey());
                    switch (condition) {
                        case IN:
                            return in(attribute, (List) entry.getValue());
                        case LIKE:
                            return like(attribute, entry.getValue().toString());
                        case IS_NULL:
                            return Boolean.TRUE.equals(entry.getValue()) ? isNull(attribute) : notNull(attribute);
                        case EQUAL:
                            return equalTo(attribute, entry.getValue().toString());
                        default:
                            return null;
                    }
                });
    }

    private Function<Object, Stream<AttributeFilter<?>>> mapDateArgument(String attribute) {
        return argument ->
                ((Map<String, Object>) argument).entrySet().stream().map(entry -> {
                    FilterCondition condition = FilterCondition.fromLabel(entry.getKey());
                    switch (condition) {
                        case IS_NULL:
                            return Boolean.TRUE.equals(entry.getValue()) ? isNull(attribute) : notNull(attribute);
                        case EQUAL:
                            return equalTo(attribute, entry.getValue());
                        case GT:
                            return greaterThan(attribute, entry.getValue());
                        case GTE:
                            return greaterThanEqual(attribute, entry.getValue());
                        case LT:
                            return lessThan(attribute, entry.getValue());
                        case LTE:
                            return lessThanEqual(attribute, entry.getValue());
                        case BETWEEN:
                            Map<String, Object> value = (Map<String, Object>) entry.getValue();
                            return between(attribute, value.get("from"), value.get("to"));
                        default:
                            return null;
                    }
                });
    }

    private Function<Object, Stream<AttributeFilter<?>>> mapStringArrayArgument(String attribute) {
        return argument ->
                ((Map<String, Object>) argument).entrySet().stream().map(entry -> {
                    FilterCondition condition = FilterCondition.fromLabel(entry.getKey());
                    switch (condition) {
                        case CONTAINS:
                            return contains(attribute, entry.getValue().toString());
                        case CONTAINS_ALL:
                            return containsAll(attribute, (List) entry.getValue());
                        case CONTAINS_ANY:
                            return containsAny(attribute, (List) entry.getValue());
                        case IS_NULL:
                            return Boolean.TRUE.equals(entry.getValue()) ? isNull(attribute) : notNull(attribute);
                        default:
                            return null;
                    }
                });
    }

    private Function<Object, Stream<AttributeFilter<?>>> mapBooleanArgument(String attribute) {
        return argument -> ((Map<String, Object>) argument).entrySet().stream().map(entry -> {
            FilterCondition condition = FilterCondition.fromLabel(entry.getKey());
            switch (condition) {
                case IS_NULL:
                    return Boolean.TRUE.equals(entry.getValue()) ? isNull(attribute) : notNull(attribute);
                case EQUAL:
                    return equalTo(attribute, entry.getValue());
                default:
                    return null;
            }
        });
    }

    private Function<Object, Stream<AttributeFilter<?>>> mapNumericArgument(String attribute) {
        return argument -> ((Map<String, Object>) argument).entrySet().stream().map(entry -> {
            FilterCondition condition = FilterCondition.fromLabel(entry.getKey());
            switch (condition) {
                case IN:
                    return in(attribute, (List) entry.getValue());
                case IS_NULL:
                    return Boolean.TRUE.equals(entry.getValue()) ? isNull(attribute) : notNull(attribute);
                case EQUAL:
                    return equalTo(attribute, entry.getValue());
                case GT:
                    return greaterThan(attribute, entry.getValue());
                case GTE:
                    return greaterThanEqual(attribute, entry.getValue());
                case LT:
                    return lessThan(attribute, entry.getValue());
                case LTE:
                    return lessThanEqual(attribute, entry.getValue());
                case BETWEEN:
                    Map<String, Object> value = (Map<String, Object>) entry.getValue();
                    return between(attribute, value.get("from"), value.get("to"));
                default:
                    return null;
            }
        });
    }

    private Function<Object, Stream<AttributeFilter<?>>> mapEnumArgument(String attribute) {
        return argument -> ((Map<String, Object>) argument).entrySet().stream().map(entry -> {
            FilterCondition condition = FilterCondition.fromLabel(entry.getKey());
            switch (condition) {
                case IN:
                    return in(attribute, (List) entry.getValue());
                case EQUAL:
                    return equalTo(attribute, entry.getValue());
                default:
                    return null;
            }
        });
    }
}
