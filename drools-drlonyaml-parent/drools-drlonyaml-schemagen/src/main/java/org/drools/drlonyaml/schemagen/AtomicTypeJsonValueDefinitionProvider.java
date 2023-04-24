package org.drools.drlonyaml.schemagen;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.members.ResolvedMember;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.CustomDefinition;
import com.github.victools.jsonschema.generator.CustomDefinitionProviderV2;
import com.github.victools.jsonschema.generator.SchemaGenerationContext;

// see https://github.com/victools/jsonschema-generator/issues/208#issuecomment-973416170
public class AtomicTypeJsonValueDefinitionProvider implements CustomDefinitionProviderV2 {

    @Override
    public CustomDefinition provideCustomSchemaDefinition(ResolvedType javaType, SchemaGenerationContext context) {
        ResolvedTypeWithMembers typeWithMembers = context.getTypeContext().resolveWithMembers(javaType);
        List<ResolvedMember<?>> jsonValueMembers = Stream
                .concat(Stream.of(typeWithMembers.getMemberFields()), Stream.of(typeWithMembers.getMemberMethods()))
                .filter(member -> Optional.ofNullable(member.get(JsonValue.class)).filter(JsonValue::value).isPresent())
                .collect(Collectors.toList());

        if (jsonValueMembers.size() != 1) {
            return null; // no unambiguous @JsonValue target could be detected
        }
        ResolvedType underlyingValueType = jsonValueMembers.get(0).getType();
        ObjectNode customNode = context.createStandardDefinitionReference(underlyingValueType, javaType.equals(underlyingValueType) ? this : null);
        return new CustomDefinition(customNode,
                CustomDefinition.DefinitionType.ALWAYS_REF, // used to make $ref instead of inlined as atomic (e.g.: string) in referencing type 
                CustomDefinition.AttributeInclusion.YES);
    }
}
