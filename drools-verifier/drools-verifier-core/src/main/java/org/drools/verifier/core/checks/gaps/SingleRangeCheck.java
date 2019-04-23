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

package org.drools.verifier.core.checks.gaps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.api.reporting.gaps.MissingRange;
import org.drools.verifier.api.reporting.gaps.PartitionCondition;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.cache.inspectors.condition.ConditionInspector;
import org.drools.verifier.core.checks.base.CheckBase;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.index.model.FieldRange;
import org.drools.verifier.core.index.model.ObjectField;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.drools.verifier.core.relations.Operator.resolve;

public class SingleRangeCheck extends CheckBase {

    private final Collection<RuleInspector> ruleInspectors;
    private List<RangeError> errors = new ArrayList<>();

    public SingleRangeCheck(final AnalyzerConfiguration configuration,
                            final Collection<RuleInspector> ruleInspectors) {
        super(configuration);
        this.ruleInspectors = ruleInspectors;
    }

    @Override
    public boolean check() {
        if (ruleInspectors == null || ruleInspectors.isEmpty()) {
            return hasIssues = false;
        }
        errors.clear();
        final int conditionNr = ruleInspectors.iterator().next().getConditionsInspectors().size();
        for (int i = 0; i < conditionNr; i++) {
            checkCondition(i);
        }
        return hasIssues = !errors.isEmpty();
    }

    private void checkCondition(final int conditionIndex) {
        final Map<OperatorType, Set<ObjectField>> fieldsByOperatorType = ruleInspectors.stream()
                .map(r -> r.getConditionsInspectors().get(conditionIndex))
                .flatMap(cond -> cond.keySet().stream())
                .collect(groupingBy(f -> getFieldOperatorType(f, conditionIndex), toSet()));

        final Set<ObjectField> rangeFields = fieldsByOperatorType.get(OperatorType.RANGE);
        if (rangeFields != null && !rangeFields.isEmpty()) {
            checkRanges(rangeFields,
                        getPartitions(fieldsByOperatorType.get(OperatorType.PARTITION),
                                      ruleInspectors,
                                      conditionIndex),
                        conditionIndex);
        }
    }

    private void checkRanges(final Collection<ObjectField> rangeFields,
                             final Map<PartitionKey, List<RuleInspector>> partitions,
                             final int conditionIndex) {
        for (Map.Entry<PartitionKey, List<RuleInspector>> partition : partitions.entrySet()) {
            final List<List<? extends Range<?>>> dimensions = new ArrayList<>();

            for (final ObjectField field : rangeFields) {
                if ("Integer".equals(field.getFieldType())) {
                    checkMonodimensionalRange(partition,
                                              dimensions,
                                              field,
                                              conditionIndex,
                                              IntegerRange::new,
                                              FieldRange.getIntegerMinValue(field),
                                              FieldRange.getIntegerMaxValue(field));
                } else {
                    checkMonodimensionalRange(partition,
                                              dimensions,
                                              field,
                                              conditionIndex,
                                              NumericRange::new,
                                              FieldRange.getDoubleMinValue(field),
                                              FieldRange.getDoubleMaxValue(field));
                }
            }

            checkBidimensionalRanges(partition,
                                     dimensions);
        }
    }

    private <T extends Comparable> void checkMonodimensionalRange(final Entry<PartitionKey, List<RuleInspector>> partition,
                                                                  final List<List<? extends Range<?>>> dimensions,
                                                                  final ObjectField field,
                                                                  final int conditionIndex,
                                                                  final Function<List<ConditionInspector>, Range<T>> rangeSupplier,
                                                                  final T min,
                                                                  final T max) {
        final List<Range<T>> ranges = partition.getValue().stream()
                .map(r -> r.getConditionsInspectors().get(conditionIndex))
                .map(c -> c.get(field))
                .map(rangeSupplier)
                .collect(toList());

        final T lower = getCoverageUpperBound(min, ranges);
        if (lower.equals(max)) {
            dimensions.add(ranges);
        } else {

            final ArrayList<MissingRange> missingRanges = findMissingRanges(field, max, ranges, lower);

            if (!missingRanges.isEmpty()) {
                errors.add(new RangeError(partition.getValue(),
                                          partition.getKey(),
                                          missingRanges));
            }
        }
    }

    private <T extends Comparable> ArrayList<MissingRange> findMissingRanges(final ObjectField field,
                                                                             final T max,
                                                                             final List<Range<T>> ranges,
                                                                             final T lowest) {
        final ArrayList<MissingRange> result = new ArrayList<>();

        T lower = lowest;

        while (lower.compareTo(max) < 0) {
            final T upper = getCoverageNextBound(lower, max, ranges.stream());
            result.add(new MissingRange(field.getFactType(),
                                        field.getName(),
                                        lower,
                                        upper));
            lower = getCoverageUpperBound(upper, ranges);
        }

        return result;
    }

    private void checkBidimensionalRanges(final Entry<PartitionKey, List<RuleInspector>> partition,
                                          final List<List<? extends Range<?>>> dimensions) {
        if (errors.isEmpty() && dimensions.size() >= 2) {
            for (int i = 0; i < dimensions.size() - 1; i++) {
                for (int j = i + 1; j < dimensions.size(); j++) {
                    if (!checkBidimensionalRanges(dimensions.get(i),
                                                  dimensions.get(j))) {
                        errors.add(new RangeError(partition.getValue(),
                                                  partition.getKey(),
                                                  null));
                    }
                }
            }
        }
    }

    private <H extends Comparable, V extends Comparable> boolean checkBidimensionalRanges(final List<? extends Range<? extends H>> hRanges,
                                                                                          final List<? extends Range<? extends V>> vRanges) {
        final List<BidimensionalRange<H, V>> bidiRanges = IntStream.range(0, hRanges.size())
                .mapToObj(i -> new BidimensionalRange<H, V>(hRanges.get(i), vRanges.get(i)))
                .sorted(Comparator.comparing(r -> r.getHorizontal()))
                .collect(toList());

        final SortedSet<H> hBreakPoints = new TreeSet<>();
        final Map<H, List<BidimensionalRange<H, V>>> lowerHBounds = new HashMap<>();
        final Map<H, List<BidimensionalRange<H, V>>> upperHBounds = new HashMap<>();
        for (BidimensionalRange<H, V> bidiRange : bidiRanges) {
            Range<? extends H> hRange = bidiRange.getHorizontal();
            hBreakPoints.add(hRange.lowerBound);
            lowerHBounds.computeIfAbsent(hRange.lowerBound, x -> new ArrayList<>()).add(bidiRange);
            if (!hRange.upperBound.equals(hRange.maxValue())) {
                hBreakPoints.add(hRange.upperBound);
                upperHBounds.computeIfAbsent(hRange.upperBound, x -> new ArrayList<>()).add(bidiRange);
            }
        }

        final V minV = vRanges.get(0).minValue();
        final V maxV = vRanges.get(0).maxValue();

        boolean first = true;
        final List<BidimensionalRange<H, V>> parsedRanges = new ArrayList<>();
        for (H sweep : hBreakPoints) {
            final List<BidimensionalRange<H, V>> enteringRanges = lowerHBounds.get(sweep);
            if (enteringRanges != null) {
                parsedRanges.addAll(enteringRanges);
            }

            final List<BidimensionalRange<H, V>> exitingRanges = upperHBounds.get(sweep);
            if (exitingRanges != null) {
                parsedRanges.removeAll(exitingRanges);
            }

            if ((first || exitingRanges != null) &&
                    !maxV.equals(getCoverageUpperBound(minV,
                                                       parsedRanges.stream().map(br -> br.getVertical())))) {
                return false;
            }
            first = false;
        }

        return true;
    }

    private OperatorType getFieldOperatorType(final ObjectField field,
                                              final int conditionIndex) {
        return ruleInspectors.stream()
                .flatMap(r -> getConditionStream(r, field, conditionIndex))
                .map(c -> resolve(((FieldCondition) c.getCondition()).getOperator()))
                .map(OperatorType::decode)
                .reduce(OperatorType.PARTITION, OperatorType::combine);
    }

    private Map<PartitionKey, List<RuleInspector>> getPartitions(final Collection<ObjectField> partitionFields,
                                                                 final Collection<RuleInspector> rules,
                                                                 final int conditionIndex) {
        final List<PartitionKey> keysWithNull = new ArrayList<>();

        final Map<PartitionKey, List<RuleInspector>> partitions = new HashMap<>();
        for (RuleInspector rule : rules) {
            final PartitionKey key = getPartitionKey(partitionFields,
                                                     rule,
                                                     conditionIndex);
            partitions.computeIfAbsent(key, k -> {
                if (k.hasNulls()) {
                    keysWithNull.add(k);
                }
                return new ArrayList<>();
            }).add(rule);
        }

        for (PartitionKey key : keysWithNull) {
            for (Map.Entry<PartitionKey, List<RuleInspector>> partition : partitions.entrySet()) {
                if (key.subsumes(partition.getKey())) {
                    partition.getValue().addAll(partitions.get(key));
                }
            }
        }

        keysWithNull.forEach(partitions::remove);
        return partitions;
    }

    private PartitionKey getPartitionKey(final Collection<ObjectField> partitionFields,
                                         final RuleInspector rule,
                                         final int conditionIndex) {
        return partitionFields == null || partitionFields.isEmpty() ?
                PartitionKey.EMPTY_KEY :
                makePartitionKey(partitionFields,
                                 rule,
                                 conditionIndex);
    }

    private PartitionKey makePartitionKey(final Collection<ObjectField> partitionFields,
                                          final RuleInspector rule,
                                          final int conditionIndex) {

        final List<Object> list = new ArrayList<>();
        final List<PartitionCondition> conditions = new ArrayList<>();
        for (final ObjectField field : partitionFields) {
            final Object value = getValue(rule,
                                          field,
                                          conditionIndex);
            list.add(value);
            conditions.add(new PartitionCondition(field.getFactType(),
                                                  field.getName(),
                                                  value));
        }
        return new PartitionKey(list.toArray(),
                                conditions);
    }

    private Object getValue(final RuleInspector rule,
                            final ObjectField field,
                            final int conditionIndex) {
        final List<ConditionInspector> conditions = getConditions(rule, field, conditionIndex);
        return conditions != null ? conditions.get(0).getCondition().getValues().iterator().next() : null;
    }

    private Stream<ConditionInspector> getConditionStream(final RuleInspector rule,
                                                          final ObjectField field,
                                                          final int conditionIndex) {
        final List<ConditionInspector> conditionInspectors = getConditions(rule, field, conditionIndex);
        return conditionInspectors != null ? conditionInspectors.stream() : Stream.empty();
    }

    private List<ConditionInspector> getConditions(final RuleInspector rule,
                                                   final ObjectField field,
                                                   final int conditionIndex) {
        return rule.getConditionsInspectors() != null ? rule.getConditionsInspectors().get(conditionIndex).get(field) : null;
    }

    private <T extends Comparable> T getCoverageUpperBound(T lowerBound, List<? extends Range<? extends T>> ranges) {
        return getCoverageUpperBound(lowerBound, ranges.stream());
    }

    private <T extends Comparable> T getCoverageUpperBound(final T lowerBound,
                                                           final Stream<? extends Range<? extends T>> ranges) {
        T limit = lowerBound;
        Iterator<? extends Range<? extends T>> i = ranges.sorted().iterator();
        while (i.hasNext()) {
            final Range<? extends T> range = i.next();
            if (range.lowerBound.compareTo(limit) > 0) {
                return limit;
            }
            limit = range.upperBound.compareTo(limit) > 0 ? range.upperBound : limit;
        }
        return limit;
    }

    private <T extends Comparable> T getCoverageNextBound(final T bound,
                                                          final T max,
                                                          final Stream<? extends Range<? extends T>> ranges) {
        final Iterator<? extends Range<? extends T>> i = ranges.sorted().iterator();
        while (i.hasNext()) {
            final Range<? extends T> range = i.next();

            if (range.lowerBound.compareTo(bound) > 0) {
                return range.lowerBound;
            }
        }
        return max;
    }

    @Override
    protected List<Issue> makeIssues(final Severity severity,
                                     final CheckType checkType) {
        final ArrayList<Issue> result = new ArrayList<Issue>();
        for (final RangeError error : errors) {
            result.add(error.toIssue(severity, checkType));
        }
        return result;
    }

    @Override
    protected CheckType getCheckType() {
        return CheckType.MISSING_RANGE;
    }

    @Override
    protected Severity getDefaultSeverity() {
        return Severity.NOTE;
    }
}
