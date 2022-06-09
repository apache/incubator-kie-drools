package org.optaplanner.examples.investment.optional.score;

import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.domain.Region;
import org.optaplanner.examples.investment.domain.Sector;

public class InvestmentEasyScoreCalculator implements EasyScoreCalculator<InvestmentSolution, HardSoftLongScore> {

    @Override
    public HardSoftLongScore calculateScore(InvestmentSolution solution) {
        long hardScore = 0L;
        long softScore = 0L;
        long squaredFemtosMaximum = solution.getParametrization().calculateSquaredStandardDeviationFemtosMaximum();
        long squaredFemtos = solution.calculateStandardDeviationSquaredFemtos();
        // Standard deviation maximum
        if (squaredFemtos > squaredFemtosMaximum) {
            hardScore -= squaredFemtos - squaredFemtosMaximum;
        }
        // Region quantity maximum
        Map<Region, Long> regionQuantityTotalMap = solution.calculateRegionQuantityMillisTotalMap();
        for (Region region : solution.getRegionList()) {
            long available = region.getQuantityMillisMaximum() - regionQuantityTotalMap.get(region);
            if (available < 0) {
                hardScore += available;
            }
        }
        // Sector quantity maximum
        Map<Sector, Long> sectorQuantityTotalMap = solution.calculateSectorQuantityMillisTotalMap();
        for (Sector sector : solution.getSectorList()) {
            long available = sector.getQuantityMillisMaximum() - sectorQuantityTotalMap.get(sector);
            if (available < 0) {
                hardScore += available;
            }
        }
        // Maximize expected return
        softScore += solution.calculateExpectedReturnMicros();
        return HardSoftLongScore.of(hardScore, softScore);
    }

}
