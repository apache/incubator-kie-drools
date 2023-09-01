package org.drools.drlonyaml.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
@JsonSubTypes({
    @JsonSubTypes.Type(StringThen.class)
})
@JsonDeserialize(using = AbstractThenDeserializer.class)
public abstract class AbstractThen {
    // intentionally empty.
}
