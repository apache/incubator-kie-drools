package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import java.util.Objects;

import org.optaplanner.core.config.heuristic.selector.common.nearby.NearbySelectionConfig;
import org.optaplanner.core.config.heuristic.selector.common.nearby.NearbySelectionDistributionType;

public class NearbyRandomFactory {

    public static NearbyRandomFactory create(NearbySelectionConfig nearbySelectionConfig) {
        return new NearbyRandomFactory(nearbySelectionConfig);
    }

    private final NearbySelectionConfig nearbySelectionConfig;

    public NearbyRandomFactory(NearbySelectionConfig nearbySelectionConfig) {
        this.nearbySelectionConfig = nearbySelectionConfig;
    }

    public NearbyRandom buildNearbyRandom(boolean randomSelection) {
        boolean blockDistributionEnabled =
                nearbySelectionConfig.getNearbySelectionDistributionType() == NearbySelectionDistributionType.BLOCK_DISTRIBUTION
                        || nearbySelectionConfig.getBlockDistributionSizeMinimum() != null
                        || nearbySelectionConfig.getBlockDistributionSizeMaximum() != null
                        || nearbySelectionConfig.getBlockDistributionSizeRatio() != null
                        || nearbySelectionConfig.getBlockDistributionUniformDistributionProbability() != null;
        boolean linearDistributionEnabled = nearbySelectionConfig
                .getNearbySelectionDistributionType() == NearbySelectionDistributionType.LINEAR_DISTRIBUTION
                || nearbySelectionConfig.getLinearDistributionSizeMaximum() != null;
        boolean parabolicDistributionEnabled = nearbySelectionConfig
                .getNearbySelectionDistributionType() == NearbySelectionDistributionType.PARABOLIC_DISTRIBUTION
                || nearbySelectionConfig.getParabolicDistributionSizeMaximum() != null;
        boolean betaDistributionEnabled =
                nearbySelectionConfig.getNearbySelectionDistributionType() == NearbySelectionDistributionType.BETA_DISTRIBUTION
                        || nearbySelectionConfig.getBetaDistributionAlpha() != null
                        || nearbySelectionConfig.getBetaDistributionBeta() != null;
        if (!randomSelection) {
            if (blockDistributionEnabled || linearDistributionEnabled || parabolicDistributionEnabled
                    || betaDistributionEnabled) {
                throw new IllegalArgumentException("The nearbySelectorConfig (" + nearbySelectionConfig
                        + ") with randomSelection (" + randomSelection
                        + ") has distribution parameters.");
            }
            return null;
        }
        if (blockDistributionEnabled && linearDistributionEnabled) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + nearbySelectionConfig
                    + ") has both blockDistribution and linearDistribution parameters.");
        }
        if (blockDistributionEnabled && parabolicDistributionEnabled) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + nearbySelectionConfig
                    + ") has both blockDistribution and parabolicDistribution parameters.");
        }
        if (blockDistributionEnabled && betaDistributionEnabled) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + nearbySelectionConfig
                    + ") has both blockDistribution and betaDistribution parameters.");
        }
        if (linearDistributionEnabled && parabolicDistributionEnabled) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + nearbySelectionConfig
                    + ") has both linearDistribution and parabolicDistribution parameters.");
        }
        if (linearDistributionEnabled && betaDistributionEnabled) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + nearbySelectionConfig
                    + ") has both linearDistribution and betaDistribution parameters.");
        }
        if (parabolicDistributionEnabled && betaDistributionEnabled) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + nearbySelectionConfig
                    + ") has both parabolicDistribution and betaDistribution parameters.");
        }
        if (blockDistributionEnabled) {
            int sizeMinimum = Objects.requireNonNullElse(nearbySelectionConfig.getBlockDistributionSizeMinimum(), 1);
            int sizeMaximum =
                    Objects.requireNonNullElse(nearbySelectionConfig.getBlockDistributionSizeMaximum(), Integer.MAX_VALUE);
            double sizeRatio = Objects.requireNonNullElse(nearbySelectionConfig.getBlockDistributionSizeRatio(), 1.0);
            double uniformDistributionProbability =
                    Objects.requireNonNullElse(nearbySelectionConfig.getBlockDistributionUniformDistributionProbability(), 0.0);
            return new BlockDistributionNearbyRandom(sizeMinimum, sizeMaximum, sizeRatio, uniformDistributionProbability);
        } else if (linearDistributionEnabled) {
            int sizeMaximum =
                    Objects.requireNonNullElse(nearbySelectionConfig.getLinearDistributionSizeMaximum(), Integer.MAX_VALUE);
            return new LinearDistributionNearbyRandom(sizeMaximum);
        } else if (parabolicDistributionEnabled) {
            int sizeMaximum =
                    Objects.requireNonNullElse(nearbySelectionConfig.getParabolicDistributionSizeMaximum(), Integer.MAX_VALUE);
            return new ParabolicDistributionNearbyRandom(sizeMaximum);
        } else if (betaDistributionEnabled) {
            double alpha = Objects.requireNonNullElse(nearbySelectionConfig.getBetaDistributionAlpha(), 1.0);
            double beta = Objects.requireNonNullElse(nearbySelectionConfig.getBetaDistributionBeta(), 5.0);
            return new BetaDistributionNearbyRandom(alpha, beta);
        } else {
            return new LinearDistributionNearbyRandom(Integer.MAX_VALUE);
        }
    }
}
