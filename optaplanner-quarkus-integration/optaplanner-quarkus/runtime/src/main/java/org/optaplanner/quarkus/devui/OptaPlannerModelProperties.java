package org.optaplanner.quarkus.devui;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OptaPlannerModelProperties {
    String solutionClass;
    List<String> entityClassList;
    Map<String, List<String>> entityClassToGenuineVariableListMap;
    Map<String, List<String>> entityClassToShadowVariableListMap;

    public OptaPlannerModelProperties() {
        solutionClass = "null";
        entityClassList = Collections.emptyList();
        entityClassToGenuineVariableListMap = Collections.emptyMap();
        entityClassToShadowVariableListMap = Collections.emptyMap();
    }

    public String getSolutionClass() {
        return solutionClass;
    }

    public void setSolutionClass(String solutionClass) {
        this.solutionClass = solutionClass;
    }

    public List<String> getEntityClassList() {
        return entityClassList;
    }

    public void setEntityClassList(List<String> entityClassList) {
        this.entityClassList = entityClassList;
    }

    public Map<String, List<String>> getEntityClassToGenuineVariableListMap() {
        return entityClassToGenuineVariableListMap;
    }

    public void setEntityClassToGenuineVariableListMap(
            Map<String, List<String>> entityClassToGenuineVariableListMap) {
        this.entityClassToGenuineVariableListMap = entityClassToGenuineVariableListMap;
    }

    public Map<String, List<String>> getEntityClassToShadowVariableListMap() {
        return entityClassToShadowVariableListMap;
    }

    public void setEntityClassToShadowVariableListMap(
            Map<String, List<String>> entityClassToShadowVariableListMap) {
        this.entityClassToShadowVariableListMap = entityClassToShadowVariableListMap;
    }
}
