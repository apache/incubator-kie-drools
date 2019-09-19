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

package org.kie.kogito.index;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.query.ProcessInstanceFilter;
import org.kie.kogito.index.query.UserTaskInstanceFilter;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class GraphQLUtils {

    private static final Map<String, Function<Object, String>> VALUE_MAPPERS = new HashMap<>();
    private static final Map<Class, String> QUERY_FIELDS = new HashMap<>();

    static {
        VALUE_MAPPERS.put(ProcessInstanceFilter.class.getName() + ".state", states ->
                ((List<Integer>) states).stream().map(state -> ProcessInstanceState.fromStatus(state).name()).collect(joining(", "))
        );
        QUERY_FIELDS.put(UserTaskInstance.class, getAllFieldsList(UserTaskInstance.class).map(getFiledName()).collect(joining(", ")));
        QUERY_FIELDS.put(ProcessInstance.class, getAllFieldsList(ProcessInstance.class).map(getFiledName()).collect(joining(", ")));
    }

    public static String toGraphQLString(UserTaskInstanceFilter filter) {
        return toGraphQLString("UserTaskInstances", UserTaskInstance.class, UserTaskInstanceFilter.class, filter);
    }

    public static String toGraphQLString(ProcessInstanceFilter filter) {
        return toGraphQLString("ProcessInstances", ProcessInstance.class, ProcessInstanceFilter.class, filter);
    }

    private static String toGraphQLString(String root, Class targetClass, Class filterClass, Object filter) {
        StringBuilder query = new StringBuilder();
        query.append(format("{ \"query\" : \"{ %s", root));
        if (filter != null) {
            String filterString = getAllFieldsList(filterClass).filter(getFieldPredicate(filter)).map(getFieldStringFunction(filter)).collect(joining(", "));
            query.append(format("(filter: { %s } )", filterString));
        }
        query.append(format("{ %s } ", QUERY_FIELDS.get(targetClass)));
        query.append("}\" }");
        return query.toString();
    }

    private static Stream<Field> getAllFieldsList(Class clazz) {
        return FieldUtils.getAllFieldsList(clazz).stream().filter(f -> getJacocoPredicate().test(f));
    }

    private static Function<Field, String> getFiledName() {
        return field -> {
            if (field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                StringBuilder builder = new StringBuilder();
                builder.append(Arrays.stream(genericType.getActualTypeArguments()).filter(type -> type.getTypeName().startsWith("org.kie.kogito.index.model"))
                                       .flatMap(type -> {
                                           try {
                                               return getAllFieldsList(Class.forName(type.getTypeName()));
                                           } catch (Exception ex) {
                                               return Stream.empty();
                                           }
                                       }).map(f -> getFiledName().apply(f)).collect(joining(", ")));
                if (builder.length() > 0) {
                    return field.getName() + " { " + builder.toString() + " }";
                }
            }

            if (field.getType().getName().startsWith("org.kie.kogito.index.model")) {
                return "";
            }

            return field.getName();
        };
    }

    private static Function<Field, String> getFieldStringFunction(Object filter) {
        return field -> {
            try {
                Object object = field.get(filter);
                String fieldKey = field.getDeclaringClass().getTypeName() + "." + field.getName();
                return field.getName() + ":" + VALUE_MAPPERS.getOrDefault(fieldKey, getObjectString()).apply(object);
            } catch (Exception ex) {
                return "";
            }
        };
    }

    private static String getObjectString(Object object) {
        return object instanceof String ? "\\\"" + object + "\\\"" : object.toString();
    }

    private static Function<Object, String> getObjectString() {
        return object -> {
            if (object instanceof Collection) {
                return ((Collection) object).stream().map(o -> getObjectString(o)).collect(joining(", ")).toString();
            } else {
                return getObjectString(object);
            }
        };
    }

    //  See https://www.eclemma.org/jacoco/trunk/doc/faq.html    
    private static Predicate<Field> getJacocoPredicate() {
        return field -> !field.getName().equals("$jacocoData");
    }

    private static Predicate<Field> getFieldPredicate(Object target) {
        return field -> {
            try {
                Object param = FieldUtils.readField(field, target, true);
                return param != null;
            } catch (Exception ex) {
                return false;
            }
        };
    }
}
