package org.kie.efesto.common.api.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "step-type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GeneratedRedirectResource.class, name = "redirect"),
        @JsonSubTypes.Type(value = GeneratedClassResource.class, name = "class"),
        @JsonSubTypes.Type(value = GeneratedExecutableResource.class, name = "executable")
})
public interface GeneratedResource extends Serializable {

}
