/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.constraint.streams.bavet;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.BavetAbstractUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.BavetForEachUniConstraintStream;
import org.optaplanner.constraint.streams.common.InnerConstraintFactory;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.domain.constraintweight.descriptor.ConstraintConfigurationDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

public final class BavetConstraintFactory<Solution_>
        extends InnerConstraintFactory<Solution_, BavetConstraint<Solution_>> {

    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private final String defaultConstraintPackage;

    private final Map<BavetAbstractConstraintStream<Solution_>, BavetAbstractConstraintStream<Solution_>> sharingStreamMap =
            new HashMap<>(256);

    public BavetConstraintFactory(SolutionDescriptor<Solution_> solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
        ConstraintConfigurationDescriptor<Solution_> configurationDescriptor = solutionDescriptor
                .getConstraintConfigurationDescriptor();
        if (configurationDescriptor == null) {
            Package pack = solutionDescriptor.getSolutionClass().getPackage();
            defaultConstraintPackage = (pack == null) ? "" : pack.getName();
        } else {
            defaultConstraintPackage = configurationDescriptor.getConstraintPackage();
        }
    }

    public <Stream_ extends BavetAbstractConstraintStream<Solution_>> Stream_ share(Stream_ stream) {
        return share(stream, t -> {
        });
    }

    /**
     * Enables node sharing.
     * If a constraint already exists in this factory, it replaces it by the old copy.
     * {@link BavetAbstractConstraintStream} implement equals/hashcode ignoring child streams.
     * <p>
     * {@link BavetConstraintSessionFactory#buildSession(boolean, Object)} relies on this occurring for all streams.
     * <p>
     * This must be called before the stream receives child streams.
     * 
     * @param stream never null
     * @param consumer never null
     * @param <Stream_> the {@link BavetAbstractConstraintStream} subclass
     * @return never null
     */
    public <Stream_ extends BavetAbstractConstraintStream<Solution_>> Stream_ share(Stream_ stream,
                                                                                    Consumer<Stream_> consumer) {
        return (Stream_) sharingStreamMap.computeIfAbsent(stream, k -> {
            consumer.accept(stream);
            return stream;
        });
    }

    // ************************************************************************
    // from
    // ************************************************************************

    @Override
    public <A> UniConstraintStream<A> forEachIncludingNullVars(Class<A> sourceClass) {
        assertValidFromType(sourceClass);
        return share(new BavetForEachUniConstraintStream<>(this, sourceClass, RetrievalSemantics.STANDARD));
    }

    @Override
    public <A> BavetAbstractUniConstraintStream<Solution_, A> fromUnfiltered(Class<A> fromClass) {
        assertValidFromType(fromClass);
        return share(new BavetForEachUniConstraintStream<>(this, fromClass, RetrievalSemantics.LEGACY));
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solutionDescriptor;
    }

    @Override
    public String getDefaultConstraintPackage() {
        return defaultConstraintPackage;
    }

}
