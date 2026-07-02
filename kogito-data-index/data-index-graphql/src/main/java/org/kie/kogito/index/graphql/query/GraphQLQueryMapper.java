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
package org.kie.kogito.index.graphql.query;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.FilterCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLType;

import static graphql.schema.GraphQLTypeUtil.isList;
import static graphql.schema.GraphQLTypeUtil.simplePrint;
import static graphql.schema.GraphQLTypeUtil.unwrapNonNull;
import static graphql.schema.GraphQLTypeUtil.unwrapOne;
import static java.util.stream.Collectors.toList;
import static org.kie.kogito.index.graphql.query.GraphQLInputObjectTypeMapper.ARRAY_ARGUMENT;
import static org.kie.kogito.index.json.JsonUtils.jsonFilter;
import static org.kie.kogito.persistence.api.query.FilterCondition.NOT;
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
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.not;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.notNull;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.or;

public class GraphQLQueryMapper implements Function<GraphQLInputObjectType, GraphQLQueryParser> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLQueryMapper.class);

    private static AttributeFilter filterValueList(Object value, Function<List, AttributeFilter> filter) {
        return (value instanceof List && !((List) value).isEmpty()) ? filter.apply((List) value) : null;
    }

    private static AttributeFilter filterValueMap(Object value, Function<Map<String, Object>, AttributeFilter> filter) {
        return (value instanceof Map && !((Map) value).isEmpty()) ? filter.apply((Map) value) : null;
    }

    @Override
    public GraphQLQueryParser apply(GraphQLInputObjectType type) {
        GraphQLQueryParser parser = new GraphQLQueryParser();

        type.getFields().forEach(
                field -> {
                    LOGGER.debug("Parser type: {}, field = {}:{}", type.getName(), field.getName(), simplePrint(field.getType()));
                    if (isEnumFilterType(field.getType())) {
                        parser.mapAttribute(field.getName(), mapEnumArgument(field.getName()));
                    } else if (isListOfType(field.getType(), type.getName())) {
                        parser.mapAttribute(field.getName(), mapRecursiveListArgument(field.getName(), parser));
                    } else if (field.getType() instanceof GraphQLNamedType namedType && namedType.getName().equals(type.getName())) {
                        parser.mapAttribute(field.getName(), mapRecursiveArgument(field.getName(), parser));
                    } else if (field.getType() instanceof GraphQLNamedType namedType) {
                        String name = namedType.getName();
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
                            case "FloatArgument":
                            case "BigDecimalArgument":
                            case "LongArgument":
                                parser.mapAttribute(field.getName(), mapNumericArgument(field.getName()));
                                break;
                            case "KogitoMetadataArgument":
                                parser.mapAttribute(field.getName(), mapSubEntityArgument(field.getName(), GraphQLQueryParserRegistry.get().getParser("KogitoMetadataArgument")));
                                break;
                            case "JSON":
                                parser.mapAttribute(field.getName(), mapJsonArgument(field.getName()));
                                break;
                            default:
                                if (field.getType() instanceof GraphQLInputObjectType inputType) {
                                    // Check if this is an array argument type (ends with "ArrayArgument")
                                    if (inputType.getName().endsWith(ARRAY_ARGUMENT)) {
                                        parser.mapAttribute(field.getName(), mapArrayArgument(field.getName(), inputType));
                                    } else {
                                        parser.mapAttribute(field.getName(), mapSubEntityArgument(field.getName(), new GraphQLQueryMapper().apply(inputType)));
                                    }
                                }
                        }
                    }
                });
        return parser;
    }

    Function<Object, Stream<AttributeFilter<?>>> mapJsonArgument(String attribute) {
        return argument -> ((Map<String, Object>) argument).entrySet().stream().map(e -> mapJsonArgument(attribute, e.getKey(), e.getValue()));
    }

    private AttributeFilter<?> mapJsonArgument(String attribute, String key, Object value) {
        StringBuilder sb = new StringBuilder(attribute);
        FilterCondition condition = FilterCondition.fromLabel(key);
        while (condition == null && value instanceof Map) {
            sb.append('.').append(key);
            Map.Entry<String, Object> entry = ((Map<String, Object>) value).entrySet().iterator().next();
            key = entry.getKey();
            value = entry.getValue();
            condition = FilterCondition.fromLabel(key);
        }
        if (condition != null) {
            switch (condition) {
                case GT:
                    return jsonFilter(greaterThan(sb.toString(), value));
                case GTE:
                    return jsonFilter(greaterThanEqual(sb.toString(), value));
                case LT:
                    return jsonFilter(lessThan(sb.toString(), value));
                case LTE:
                    return jsonFilter(lessThanEqual(sb.toString(), value));
                case BETWEEN:
                    return jsonFilter(filterValueMap(value, val -> between(sb.toString(), val.get("from"), val.get("to"))));
                case IN:
                    return jsonFilter(filterValueList(value, val -> in(sb.toString(), val)));
                case IS_NULL:
                    return jsonFilter(Boolean.TRUE.equals(value) ? isNull(sb.toString()) : notNull(sb.toString()));
                case CONTAINS:
                    return jsonFilter(contains(sb.toString(), value));
                case LIKE:
                    return jsonFilter(like(sb.toString(), value.toString()));
                case CONTAINS_ALL:
                    return jsonFilter(filterValueList(value, val -> containsAll(sb.toString(), val)));
                case CONTAINS_ANY:
                    return jsonFilter(filterValueList(value, val -> containsAny(sb.toString(), val)));
                case EQUAL:
                default:
                    return jsonFilter(equalTo(sb.toString(), value));
            }
        }
        return null;
    }

    private boolean isListOfType(GraphQLInputType source, String type) {
        if (isList(source)) {
            return ((GraphQLNamedType) unwrapNonNull(unwrapOne(source))).getName().equals(type);
        } else {
            return false;
        }
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

    private Function<Object, Stream<AttributeFilter<?>>> mapRecursiveListArgument(String joining, GraphQLQueryParser parser) {
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

    private Function<Object, Stream<AttributeFilter<?>>> mapRecursiveArgument(String joining, GraphQLQueryParser parser) {
        return argument -> parser.apply(argument).stream().map(f -> {
            FilterCondition condition = FilterCondition.fromLabel(joining);
            return condition == NOT ? not(f) : null;
        });
    }

    private Function<Object, Stream<AttributeFilter<?>>> mapSubEntityArgument(String joining, GraphQLQueryParser parser) {
        return argument -> parser.apply(argument).stream().map(f -> {
            f.setAttribute(joining + "." + f.getAttribute());
            return f;
        });
    }

    private Function<Object, Stream<AttributeFilter<?>>> mapArrayArgument(String attribute, GraphQLInputObjectType arrayArgType) {
        return argument -> {
            Map<String, Object> argMap = (Map<String, Object>) argument;

            // Separate array operations from backward-compatible fields
            Map<String, Object> backwardCompatFields = new java.util.HashMap<>();
            List<Stream<AttributeFilter<?>>> arrayOpStreams = new java.util.ArrayList<>();

            // Lazy initialization of element parser (only when needed)
            GraphQLQueryParser elementParser = null;

            // Helper to get element parser (lazy initialization)
            final GraphQLQueryParser[] parserHolder = new GraphQLQueryParser[1];
            java.util.function.Supplier<GraphQLQueryParser> getElementParser = () -> {
                if (parserHolder[0] == null) {
                    GraphQLInputType containsFieldType = arrayArgType.getField("contains") != null ? arrayArgType.getField("contains").getType() : null;
                    if (containsFieldType == null) {
                        LOGGER.warn("Array argument type {} does not have a 'contains' field required for element operations", arrayArgType.getName());
                        return null;
                    }
                    GraphQLType unwrappedType = unwrapNonNull(containsFieldType);
                    if (!(unwrappedType instanceof GraphQLInputObjectType elementType)) {
                        LOGGER.warn("Contains field type is not an input object type: {}", simplePrint(unwrappedType));
                        return null;
                    }
                    parserHolder[0] = new GraphQLQueryMapper().apply(elementType);
                }
                return parserHolder[0];
            };

            for (Map.Entry<String, Object> entry : argMap.entrySet()) {
                FilterCondition condition = FilterCondition.fromLabel(entry.getKey());
                Object value = entry.getValue();

                if (condition == null) {
                    // Backward compatibility: treat as element property (like 'contains')
                    backwardCompatFields.put(entry.getKey(), value);
                    continue;
                }

                switch (condition) {
                    case CONTAINS:
                        // Single element filter: nodes.name = 'X'
                        if (value instanceof Map) {
                            GraphQLQueryParser parser = getElementParser.get();
                            if (parser != null) {
                                arrayOpStreams.add(parser.apply(value).stream().map(f -> {
                                    f.setAttribute(attribute + "." + f.getAttribute());
                                    return f;
                                }));
                            }
                        }
                        break;

                    case CONTAINS_ALL:
                        // All elements must match: AND(nodes.name = 'X', nodes.name = 'Y')
                        if (value instanceof List) {
                            GraphQLQueryParser parser = getElementParser.get();
                            if (parser != null) {
                                List<AttributeFilter<?>> allFilters = ((List<?>) value).stream()
                                        .flatMap(elem -> {
                                            if (elem instanceof Map) {
                                                return parser.apply(elem).stream().map(f -> {
                                                    f.setAttribute(attribute + "." + f.getAttribute());
                                                    return f;
                                                });
                                            }
                                            return Stream.empty();
                                        })
                                        .collect(toList());
                                if (!allFilters.isEmpty()) {
                                    arrayOpStreams.add(Stream.of(and(allFilters)));
                                }
                            }
                        }
                        break;

                    case CONTAINS_ANY:
                        // Any element matches: OR(nodes.name = 'X', nodes.name = 'Y')
                        if (value instanceof List) {
                            GraphQLQueryParser parser = getElementParser.get();
                            if (parser != null) {
                                List<AttributeFilter<?>> anyFilters = ((List<?>) value).stream()
                                        .flatMap(elem -> {
                                            if (elem instanceof Map) {
                                                return parser.apply(elem).stream().map(f -> {
                                                    f.setAttribute(attribute + "." + f.getAttribute());
                                                    return f;
                                                });
                                            }
                                            return Stream.empty();
                                        })
                                        .collect(toList());
                                if (!anyFilters.isEmpty()) {
                                    arrayOpStreams.add(Stream.of(or(anyFilters)));
                                }
                            }
                        }
                        break;

                    case IS_NULL:
                        // Check if array is null or empty
                        if (value instanceof Boolean) {
                            arrayOpStreams.add(Stream.of(Boolean.TRUE.equals(value) ? isNull(attribute) : notNull(attribute)));
                        }
                        break;

                    case AND:
                        // Recursively process AND conditions
                        if (value instanceof List) {
                            List<AttributeFilter<?>> andFilters = ((List<?>) value).stream()
                                    .flatMap(elem -> {
                                        if (elem instanceof Map) {
                                            return mapArrayArgument(attribute, arrayArgType).apply(elem);
                                        }
                                        return Stream.empty();
                                    })
                                    .collect(toList());
                            if (!andFilters.isEmpty()) {
                                arrayOpStreams.add(Stream.of(and(andFilters)));
                            }
                        }
                        break;

                    case OR:
                        // Recursively process OR conditions
                        if (value instanceof List) {
                            List<AttributeFilter<?>> orFilters = ((List<?>) value).stream()
                                    .flatMap(elem -> {
                                        if (elem instanceof Map) {
                                            return mapArrayArgument(attribute, arrayArgType).apply(elem);
                                        }
                                        return Stream.empty();
                                    })
                                    .collect(toList());
                            if (!orFilters.isEmpty()) {
                                arrayOpStreams.add(Stream.of(or(orFilters)));
                            }
                        }
                        break;

                    case NOT:
                        // Recursively process NOT condition
                        if (value instanceof Map) {
                            List<AttributeFilter<?>> notFilters = mapArrayArgument(attribute, arrayArgType).apply(value).collect(toList());
                            if (notFilters.size() == 1) {
                                // Single filter: apply NOT directly
                                arrayOpStreams.add(Stream.of(not(notFilters.get(0))));
                            } else if (notFilters.size() > 1) {
                                // Multiple filters: combine with AND, then apply NOT
                                arrayOpStreams.add(Stream.of(not(and(notFilters))));
                            }
                        }
                        break;

                    default:
                        // Unknown condition - ignore
                        break;
                }
            }

            // Process backward-compatible fields as 'contains'
            if (!backwardCompatFields.isEmpty()) {
                GraphQLQueryParser parser = getElementParser.get();
                if (parser != null) {
                    arrayOpStreams.add(parser.apply(backwardCompatFields).stream().map(f -> {
                        f.setAttribute(attribute + "." + f.getAttribute());
                        return f;
                    }));
                }
            }

            // Combine all streams
            return arrayOpStreams.stream().flatMap(s -> s);
        };
    }

    private Function<Object, Stream<AttributeFilter<?>>> mapIdArgument(String attribute) {
        return argument -> ((Map<String, Object>) argument).entrySet().stream().map(entry -> {
            FilterCondition condition = FilterCondition.fromLabel(entry.getKey());
            if (entry.getValue() == null) {
                return null;
            }
            switch (condition) {
                case IN:
                    return filterValueList(entry.getValue(), value -> in(attribute, value));
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
        return argument -> ((Map<String, Object>) argument).entrySet().stream().map(entry -> {
            FilterCondition condition = FilterCondition.fromLabel(entry.getKey());
            if (entry.getValue() == null) {
                return null;
            }
            switch (condition) {
                case IN:
                    return filterValueList(entry.getValue(), value -> in(attribute, value));
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
        return argument -> ((Map<String, Object>) argument).entrySet().stream().map(entry -> {
            FilterCondition condition = FilterCondition.fromLabel(entry.getKey());
            if (entry.getValue() == null) {
                return null;
            }
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
                    return filterValueMap(entry.getValue(), value -> between(attribute, value.get("from"), value.get("to")));
                default:
                    return null;
            }
        });
    }

    private Function<Object, Stream<AttributeFilter<?>>> mapStringArrayArgument(String attribute) {
        return argument -> ((Map<String, Object>) argument).entrySet().stream().map(entry -> {
            FilterCondition condition = FilterCondition.fromLabel(entry.getKey());
            if (entry.getValue() == null) {
                return null;
            }
            switch (condition) {
                case CONTAINS:
                    return contains(attribute, entry.getValue().toString());
                case CONTAINS_ALL:
                    return filterValueList(entry.getValue(), value -> containsAll(attribute, value));
                case CONTAINS_ANY:
                    return filterValueList(entry.getValue(), value -> containsAny(attribute, value));
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
            if (entry.getValue() == null) {
                return null;
            }
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
            if (entry.getValue() == null) {
                return null;
            }
            switch (condition) {
                case IN:
                    return filterValueList(entry.getValue(), value -> in(attribute, value));
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
                    return filterValueMap(entry.getValue(), value -> between(attribute, value.get("from"), value.get("to")));
                default:
                    return null;
            }
        });
    }

    private Function<Object, Stream<AttributeFilter<?>>> mapEnumArgument(String attribute) {
        return argument -> ((Map<String, Object>) argument).entrySet().stream().map(entry -> {
            FilterCondition condition = FilterCondition.fromLabel(entry.getKey());
            if (entry.getValue() == null) {
                return null;
            }
            switch (condition) {
                case IN:
                    return filterValueList(entry.getValue(), value -> in(attribute, value));
                case EQUAL:
                    return equalTo(attribute, entry.getValue());
                default:
                    return null;
            }
        });
    }
}
