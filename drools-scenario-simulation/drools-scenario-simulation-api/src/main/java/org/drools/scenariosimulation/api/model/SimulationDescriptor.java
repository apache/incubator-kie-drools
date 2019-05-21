/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.scenariosimulation.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * SimulationDescriptor describes a template of a simulation
 */
public class SimulationDescriptor {

    private final List<FactMapping> factMappings = new ArrayList<>();

    private String dmoSession;

    private String dmnFilePath;

    private ScenarioSimulationModel.Type type;

    private String fileName;

    private String kieSession;

    private String kieBase;

    private String ruleFlowGroup;

    private String dmnNamespace;

    private String dmnName;

    private boolean skipFromBuild = false;

    /**
     * Returns an <b>unmodifiable</b> list wrapping the backed one
     * @return
     */
    public List<FactMapping> getUnmodifiableFactMappings() {
        return Collections.unmodifiableList(factMappings);
    }

    public Set<FactIdentifier> getFactIdentifiers() {
        return factMappings.stream().map(FactMapping::getFactIdentifier).collect(Collectors.toSet());
    }

    public String getDmoSession() {
        return dmoSession;
    }

    public void setDmoSession(String ruleSession) {
        this.dmoSession = ruleSession;
    }

    public String getDmnFilePath() {
        return dmnFilePath;
    }

    public void setDmnFilePath(String dmnFilePath) {
        this.dmnFilePath = dmnFilePath;
    }

    public ScenarioSimulationModel.Type getType() {
        return type;
    }

    public void setType(ScenarioSimulationModel.Type type) {
        this.type = type;
    }

    public List<FactMapping> getFactMappings() {
        return factMappings;
    }

    @Deprecated
    public String getFileName() {
        return fileName;
    }

    @Deprecated
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Deprecated
    public String getKieSession() {
        return kieSession;
    }

    @Deprecated
    public void setKieSession(String kieSession) {
        this.kieSession = kieSession;
    }

    @Deprecated
    public String getKieBase() {
        return kieBase;
    }

    @Deprecated
    public void setKieBase(String kieBase) {
        this.kieBase = kieBase;
    }

    public String getRuleFlowGroup() {
        return ruleFlowGroup;
    }

    public void setRuleFlowGroup(String ruleFlowGroup) {
        this.ruleFlowGroup = ruleFlowGroup;
    }

    public String getDmnNamespace() {
        return dmnNamespace;
    }

    public void setDmnNamespace(String dmnNamespace) {
        this.dmnNamespace = dmnNamespace;
    }

    public String getDmnName() {
        return dmnName;
    }

    public void setDmnName(String dmnName) {
        this.dmnName = dmnName;
    }

    public boolean isSkipFromBuild() {
        return skipFromBuild;
    }

    public void setSkipFromBuild(boolean skipFromBuild) {
        this.skipFromBuild = skipFromBuild;
    }

    public void moveFactMapping(int oldIndex, int newIndex) {
        if (oldIndex < 0 || oldIndex >= factMappings.size()) {
            throw new IllegalArgumentException(new StringBuilder().append("Index ").append(oldIndex)
                                                       .append(" not found in the list").toString());
        }
        if (newIndex < 0 || newIndex >= factMappings.size()) {
            throw new IllegalArgumentException(new StringBuilder().append("Index ").append(newIndex)
                                                       .append(" out of range").toString());
        }
        FactMapping factMapping = factMappings.get(oldIndex);
        factMappings.remove(oldIndex);
        factMappings.add(newIndex, factMapping);
    }

    public FactMapping getFactMappingByIndex(int index) {
        return factMappings.get(index);
    }

    void removeFactMappingByIndex(int index) {
        factMappings.remove(index);
    }

    void removeFactMapping(FactMapping toRemove) {
        factMappings.remove(toRemove);
    }

    public int getIndexByIdentifier(FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier) {
        return IntStream.range(0, factMappings.size()).filter(index -> {
            FactMapping factMapping = factMappings.get(index);
            return factMapping.getExpressionIdentifier().equals(expressionIdentifier) &&
                    factMapping.getFactIdentifier().equals(factIdentifier);
        }).findFirst().orElseThrow(() -> new IllegalArgumentException(
                new StringBuilder().append("Impossible to find a FactMapping with factIdentifier '").append(factIdentifier.getName())
                        .append("' and expressionIdentifier '").append(expressionIdentifier.getName()).append("'").toString()));
    }

    public List<FactMapping> getFactMappingsByFactName(String factName) {
        return internalFilter(e -> e.getFactIdentifier().getName().equalsIgnoreCase(factName));
    }

    public Optional<FactMapping> getFactMapping(FactIdentifier factIdentifier, ExpressionIdentifier ei) {
        List<FactMapping> factMappings = internalFilter(e -> e.getExpressionIdentifier().equals(ei) &&
                e.getFactIdentifier().equals(factIdentifier));
        return factMappings.stream().findFirst();
    }

    /**
     * This method clone the given <code>FactMapping</code> and insert the cloned instance at the specified index
     * @param index
     * @param toClone
     * @return the <b>cloned</b> <code>FactMapping</code>
     */
    public FactMapping addFactMapping(int index, FactMapping toClone) {
        FactMapping toReturn = toClone.cloneFactMapping();
        factMappings.add(index, toReturn);
        return toReturn;
    }

    public FactMapping addFactMapping(FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier) {
        return addFactMapping(factMappings.size(), factIdentifier, expressionIdentifier);
    }

    public FactMapping addFactMapping(String factAlias, FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier) {
        return addFactMapping(factMappings.size(), factAlias, factIdentifier, expressionIdentifier);
    }

    public FactMapping addFactMapping(int index, FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier) {
        return addFactMapping(index, factIdentifier.getName(), factIdentifier, expressionIdentifier);
    }

    public FactMapping addFactMapping(int index, String factAlias, FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier) {
        if (getFactMapping(factIdentifier, expressionIdentifier).isPresent()) {
            throw new IllegalArgumentException(
                    new StringBuilder().append("An expression with name '").append(expressionIdentifier.getName())
                            .append("' already exists for the fact '").append(factIdentifier.getName()).append("'").toString());
        }
        if (index > factMappings.size()) {
            throw new IllegalArgumentException(
                    new StringBuilder().append("Impossible to add an element at position ").append(index)
                            .append(" because there are only ").append(factMappings.size()).append(" elements").toString());
        }
        FactMapping factMapping = new FactMapping(factAlias, factIdentifier, expressionIdentifier);
        factMappings.add(index, factMapping);
        return factMapping;
    }

    public void clear() {
        factMappings.clear();
    }

    private List<FactMapping> internalFilter(Predicate<FactMapping> predicate) {
        return factMappings.stream().filter(predicate).collect(Collectors.toList());
    }
}
