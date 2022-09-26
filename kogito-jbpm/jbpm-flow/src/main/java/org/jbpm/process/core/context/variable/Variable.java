/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.core.context.variable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.lang.model.SourceVersion;

import org.drools.util.StringUtils;
import org.jbpm.process.core.TypeObject;
import org.jbpm.process.core.ValueObject;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.impl.type.UndefinedDataType;

/**
 * Default implementation of a variable.
 * 
 */
public class Variable implements TypeObject, ValueObject, Serializable {

    private static final long serialVersionUID = 510l;

    public static final String VARIABLE_TAGS = "customTags";

    public static final String READONLY_TAG = "readonly";
    public static final String REQUIRED_TAG = "required";
    public static final String INTERNAL_TAG = "internal";
    public static final String INPUT_TAG = "input";
    public static final String OUTPUT_TAG = "output";
    public static final String BUSINESS_RELEVANT = "business-relevant";
    public static final String TRACKED = "tracked";
    public static final Set<String> KOGITO_RESERVED = Set.of("id");

    private String id;
    private String name;
    private String sanitizedName;
    private DataType type;
    private Object value;
    private Map<String, Object> metaData = new HashMap<>();

    private List<String> tags = new ArrayList<>();

    public Variable() {
        this.type = UndefinedDataType.getInstance();
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
        this.sanitizedName = sanitizeIdentifier(name);
    }

    public String getSanitizedName() {
        return sanitizedName;
    }

    /**
     * Return a valid unique Java identifier based on the given @param name. It considers valid characters and
     * reserved words.
     * In case the input is valid it is returned itself otherwise a valid identifier is generated prefixing v$ based
     * on the @param input excluding invalid characters.
     *
     * @param name the input
     * @return the output valid Java identifier
     */
    private static String sanitizeIdentifier(String name) {
        return Optional.ofNullable(name)
                .filter(SourceVersion::isName)
                .orElseGet(() -> {
                    String identifier = StringUtils.extractFirstIdentifier(name, 0);
                    return Optional.ofNullable(identifier)
                            .filter(s -> !StringUtils.isEmpty(s))
                            .filter(SourceVersion::isName)
                            // prepend v$ in front of the variable name to prevent clashing with reserved keywords
                            .orElseGet(() -> String.format("v$%s", identifier));
                });
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public DataType getType() {
        return this.type;
    }

    @Override
    public void setType(final DataType type) {
        if (type == null) {
            throw new IllegalArgumentException("type is null");
        }
        this.type = type;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public void setValue(final Object value) {
        if (this.type.verifyDataType(value)) {
            this.value = value;
        } else {
            final StringBuilder sb = new StringBuilder();
            sb.append("Value <");
            sb.append(value);
            sb.append("> is not valid for datatype: ");
            sb.append(this.type);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public void setMetaData(String name, Object value) {
        this.metaData.put(name, value);

        if (VARIABLE_TAGS.equals(name) && value != null) {
            tags = Arrays.asList(value.toString().split(","));
        }
    }

    public Object getMetaData(String name) {
        return this.metaData.get(name);
    }

    public Map<String, Object> getMetaData() {
        return this.metaData;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public List<String> getTags() {
        if (tags.isEmpty() && this.metaData.containsKey(VARIABLE_TAGS)) {
            tags = Arrays.asList(metaData.get(VARIABLE_TAGS).toString().split(","));

        }
        return tags;
    }

    public Object cloneValue() {
        return type.clone(getValue());
    }

    public boolean hasTag(String tagName) {
        return getTags().contains(tagName);
    }

    public boolean matchByIdOrName(String nameOrId) {
        return (id.equals(nameOrId) || name.equals(nameOrId));
    }
}
