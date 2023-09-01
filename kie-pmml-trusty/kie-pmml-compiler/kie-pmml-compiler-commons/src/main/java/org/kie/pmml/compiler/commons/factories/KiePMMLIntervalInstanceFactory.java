package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.dmg.pmml.Interval;
import org.kie.pmml.api.enums.CLOSURE;
import org.kie.pmml.commons.model.expressions.KiePMMLInterval;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLInterval</code> instance
 * <p>
 * out of <code>Interval</code>s
 */
public class KiePMMLIntervalInstanceFactory {

    private KiePMMLIntervalInstanceFactory() {
        // Avoid instantiation
    }

    static List<KiePMMLInterval> getKiePMMLIntervals(List<Interval> toConvert) {
        return toConvert != null ? toConvert.stream()
                .map(KiePMMLIntervalInstanceFactory::getKiePMMLInterval)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    static KiePMMLInterval getKiePMMLInterval(final Interval interval) {
        return new KiePMMLInterval(interval.getLeftMargin(), interval.getRightMargin(),
                                   CLOSURE.byName(interval.getClosure().value()));
    }
}
