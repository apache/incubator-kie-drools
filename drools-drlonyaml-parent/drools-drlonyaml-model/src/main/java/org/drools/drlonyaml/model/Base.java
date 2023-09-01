package org.drools.drlonyaml.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
@JsonSubTypes({
    @JsonSubTypes.Type(value=Pattern.class),
    @JsonSubTypes.Type(value=Exists.class),
    @JsonSubTypes.Type(value=All.class),
    @JsonSubTypes.Type(value=Not.class)
})
@JsonDeserialize(using=BaseDeserializer.class)
// BaseDescr analogous.
public interface Base {
}

