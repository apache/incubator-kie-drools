package org.optaplanner.core.impl.domain.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.domain.common.DomainAccessType;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;

public class DescriptorPolicy {
    private Map<String, MemberAccessor> generatedMemberAccessorMap = new LinkedHashMap<>();
    private Map<String, SolutionCloner> generatedSolutionClonerMap = new LinkedHashMap<>();
    private Map<String, MemberAccessor> fromSolutionValueRangeProviderMap = new LinkedHashMap<>();
    private Map<String, MemberAccessor> fromEntityValueRangeProviderMap = new LinkedHashMap<>();
    private DomainAccessType domainAccessType = DomainAccessType.REFLECTION;

    public void addFromSolutionValueRangeProvider(MemberAccessor memberAccessor) {
        String id = extractValueRangeProviderId(memberAccessor);
        fromSolutionValueRangeProviderMap.put(id, memberAccessor);
    }

    public boolean hasFromSolutionValueRangeProvider(String id) {
        return fromSolutionValueRangeProviderMap.containsKey(id);
    }

    public MemberAccessor getFromSolutionValueRangeProvider(String id) {
        return fromSolutionValueRangeProviderMap.get(id);
    }

    public void addFromEntityValueRangeProvider(MemberAccessor memberAccessor) {
        String id = extractValueRangeProviderId(memberAccessor);
        fromEntityValueRangeProviderMap.put(id, memberAccessor);
    }

    public boolean hasFromEntityValueRangeProvider(String id) {
        return fromEntityValueRangeProviderMap.containsKey(id);
    }

    /**
     * @return never null
     */
    public DomainAccessType getDomainAccessType() {
        return domainAccessType;
    }

    public void setDomainAccessType(DomainAccessType domainAccessType) {
        this.domainAccessType = domainAccessType;
    }

    /**
     * @return never null
     */
    public Map<String, MemberAccessor> getGeneratedMemberAccessorMap() {
        return generatedMemberAccessorMap;
    }

    public void setGeneratedMemberAccessorMap(Map<String, MemberAccessor> generatedMemberAccessorMap) {
        this.generatedMemberAccessorMap = generatedMemberAccessorMap;
    }

    /**
     * @return never null
     */
    public Map<String, SolutionCloner> getGeneratedSolutionClonerMap() {
        return generatedSolutionClonerMap;
    }

    public void setGeneratedSolutionClonerMap(Map<String, SolutionCloner> generatedSolutionClonerMap) {
        this.generatedSolutionClonerMap = generatedSolutionClonerMap;
    }

    public MemberAccessor getFromEntityValueRangeProvider(String id) {
        return fromEntityValueRangeProviderMap.get(id);
    }

    private String extractValueRangeProviderId(MemberAccessor memberAccessor) {
        ValueRangeProvider annotation = memberAccessor.getAnnotation(ValueRangeProvider.class);
        String id = annotation.id();
        if (id == null || id.isEmpty()) {
            throw new IllegalStateException("The @" + ValueRangeProvider.class.getSimpleName()
                    + " annotated member (" + memberAccessor + ")'s id (" + id + ") must not be empty.");
        }
        validateUniqueValueRangeProviderId(id, memberAccessor);
        return id;
    }

    private void validateUniqueValueRangeProviderId(String id, MemberAccessor memberAccessor) {
        MemberAccessor duplicate = fromSolutionValueRangeProviderMap.get(id);
        if (duplicate != null) {
            throw new IllegalStateException("2 members (" + duplicate + ", " + memberAccessor
                    + ") with a @" + ValueRangeProvider.class.getSimpleName()
                    + " annotation must not have the same id (" + id + ").");
        }
        duplicate = fromEntityValueRangeProviderMap.get(id);
        if (duplicate != null) {
            throw new IllegalStateException("2 members (" + duplicate + ", " + memberAccessor
                    + ") with a @" + ValueRangeProvider.class.getSimpleName()
                    + " annotation must not have the same id (" + id + ").");
        }
    }

    public Collection<String> getValueRangeProviderIds() {
        List<String> valueRangeProviderIds = new ArrayList<>(
                fromSolutionValueRangeProviderMap.size() + fromEntityValueRangeProviderMap.size());
        valueRangeProviderIds.addAll(fromSolutionValueRangeProviderMap.keySet());
        valueRangeProviderIds.addAll(fromEntityValueRangeProviderMap.keySet());
        return valueRangeProviderIds;
    }

}
