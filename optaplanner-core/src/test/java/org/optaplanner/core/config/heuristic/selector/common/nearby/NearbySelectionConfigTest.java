package org.optaplanner.core.config.heuristic.selector.common.nearby;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType.JUST_IN_TIME;
import static org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType.STEP;
import static org.optaplanner.core.config.heuristic.selector.common.SelectionOrder.ORIGINAL;
import static org.optaplanner.core.config.heuristic.selector.common.SelectionOrder.SORTED;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.BetaDistributionNearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.BlockDistributionNearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.LinearDistributionNearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.ParabolicDistributionNearbyRandom;

public class NearbySelectionConfigTest {

    private static final String LINEAR = "linear";
    private static final String BLOCK = "block";
    private static final String BETA = "beta";
    private static final String PARABOLIC = "parabolic";
    private static final String ENTITY_SELECTOR_ID = "entitySelector";

    @Test
    public void withNoOriginEntitySelectorConfig() {
        NearbySelectionConfig nearbySelectionConfig = new NearbySelectionConfig();
        assertThatIllegalArgumentException().isThrownBy(() -> nearbySelectionConfig.validateNearby(JUST_IN_TIME, ORIGINAL))
                .withMessageContaining("originEntitySelectorConfig");
    }

    @Test
    public void withNoMimicSelector() {
        NearbySelectionConfig nearbySelectionConfig = new NearbySelectionConfig();
        nearbySelectionConfig.setOriginEntitySelectorConfig(new EntitySelectorConfig());
        assertThatIllegalArgumentException().isThrownBy(() -> nearbySelectionConfig.validateNearby(JUST_IN_TIME, ORIGINAL))
                .withMessageContaining("MimicSelectorRef");
    }

    @Test
    public void withNoDistanceMeter() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setId(ENTITY_SELECTOR_ID);

        NearbySelectionConfig nearbySelectionConfig = new NearbySelectionConfig();
        nearbySelectionConfig
                .setOriginEntitySelectorConfig(EntitySelectorConfig.newMimicSelectorConfig(entitySelectorConfig.getId()));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> nearbySelectionConfig.validateNearby(entitySelectorConfig.getCacheType(),
                        entitySelectorConfig.getSelectionOrder()))
                .withMessageContaining("nearbyDistanceMeterClass");
    }

    @Test
    public void withWrongSelectionOrder() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setId(ENTITY_SELECTOR_ID);
        entitySelectorConfig.setSelectionOrder(SORTED);

        NearbySelectionConfig nearbySelectionConfig = new NearbySelectionConfig();
        nearbySelectionConfig
                .setOriginEntitySelectorConfig(EntitySelectorConfig.newMimicSelectorConfig(entitySelectorConfig.getId()));
        nearbySelectionConfig.setNearbyDistanceMeterClass(Mockito.mock(NearbyDistanceMeter.class).getClass());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> nearbySelectionConfig.validateNearby(entitySelectorConfig.getCacheType(),
                        entitySelectorConfig.getSelectionOrder()))
                .withMessageContaining("resolvedSelectionOrder");
    }

    @Test
    public void withCachedResolvedCachedType() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setId(ENTITY_SELECTOR_ID);
        entitySelectorConfig.setSelectionOrder(ORIGINAL);
        entitySelectorConfig.setCacheType(STEP);

        NearbySelectionConfig nearbySelectionConfig = new NearbySelectionConfig();
        nearbySelectionConfig
                .setOriginEntitySelectorConfig(EntitySelectorConfig.newMimicSelectorConfig(entitySelectorConfig.getId()));
        nearbySelectionConfig.setNearbyDistanceMeterClass(Mockito.mock(NearbyDistanceMeter.class).getClass());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> nearbySelectionConfig.validateNearby(entitySelectorConfig.getCacheType(),
                        entitySelectorConfig.getSelectionOrder()))
                .withMessageContaining("cached");
    }

    @Test
    public void buildNearbyRandomWithNoRandomSelection() {
        NearbySelectionConfig nearbySelectionConfig = buildNearbySelectionConfig();

        Assertions.assertThat(nearbySelectionConfig.buildNearbyRandom(false)).isNull();
    }

    @Test
    public void buildNearbyRandomWithNoRandomSelectionAndWithDistribution() {
        NearbySelectionConfig nearbySelectionConfig = buildNearbySelectionConfig();
        nearbySelectionConfig.setBlockDistributionSizeMinimum(1);

        assertThatIllegalArgumentException().isThrownBy(() -> nearbySelectionConfig.buildNearbyRandom(false))
                .withMessageContaining("randomSelection").withMessageContaining("distribution");
    }

    @Test
    public void buildNearbyRandomWithBlockAndLinear() {
        NearbySelectionConfig nearbySelectionConfig = buildNearbySelectionConfig();
        nearbySelectionConfig.setBlockDistributionSizeMinimum(1);
        nearbySelectionConfig.setLinearDistributionSizeMaximum(1);

        assertThatIllegalArgumentException().isThrownBy(() -> nearbySelectionConfig.buildNearbyRandom(true))
                .withMessageContaining(BLOCK).withMessageContaining(LINEAR);
    }

    @Test
    public void buildNearbyRandomWithBlockAndParabolic() {
        NearbySelectionConfig nearbySelectionConfig = buildNearbySelectionConfig();
        nearbySelectionConfig.setBlockDistributionSizeMinimum(1);
        nearbySelectionConfig.setParabolicDistributionSizeMaximum(1);

        assertThatIllegalArgumentException().isThrownBy(() -> nearbySelectionConfig.buildNearbyRandom(true))
                .withMessageContaining(BLOCK).withMessageContaining(PARABOLIC);
    }

    @Test
    public void buildNearbyRandomWithBlockAndBeta() {
        NearbySelectionConfig nearbySelectionConfig = buildNearbySelectionConfig();
        nearbySelectionConfig.setBlockDistributionSizeMinimum(1);
        nearbySelectionConfig.setBetaDistributionAlpha(0.0);

        assertThatIllegalArgumentException().isThrownBy(() -> nearbySelectionConfig.buildNearbyRandom(true))
                .withMessageContaining(BLOCK).withMessageContaining(BETA);
    }

    @Test
    public void buildNearbyRandomWithLinearAndParabolic() {
        NearbySelectionConfig nearbySelectionConfig = buildNearbySelectionConfig();
        nearbySelectionConfig.setLinearDistributionSizeMaximum(1);
        nearbySelectionConfig.setParabolicDistributionSizeMaximum(1);

        assertThatIllegalArgumentException().isThrownBy(() -> nearbySelectionConfig.buildNearbyRandom(true))
                .withMessageContaining(LINEAR).withMessageContaining(PARABOLIC);
    }

    @Test
    public void buildNearbyRandomWithLinearAndBeta() {
        NearbySelectionConfig nearbySelectionConfig = buildNearbySelectionConfig();
        nearbySelectionConfig.setLinearDistributionSizeMaximum(1);
        nearbySelectionConfig.setBetaDistributionAlpha(1.0);

        assertThatIllegalArgumentException().isThrownBy(() -> nearbySelectionConfig.buildNearbyRandom(true))
                .withMessageContaining(LINEAR).withMessageContaining(BETA);
    }

    @Test
    public void buildNearbyRandomWithParabolicAndBeta() {
        NearbySelectionConfig nearbySelectionConfig = buildNearbySelectionConfig();
        nearbySelectionConfig.setParabolicDistributionSizeMaximum(1);
        nearbySelectionConfig.setBetaDistributionAlpha(1.0);

        assertThatIllegalArgumentException().isThrownBy(() -> nearbySelectionConfig.buildNearbyRandom(true))
                .withMessageContaining(PARABOLIC).withMessageContaining(BETA);
    }

    @Test
    public void buildNearbyRandomWithBlockDistribution() {
        NearbySelectionConfig nearbySelectionConfig = buildNearbySelectionConfig();
        int minimum = 2;
        nearbySelectionConfig.setBlockDistributionSizeMinimum(minimum);
        int maximum = 3;
        nearbySelectionConfig.setBlockDistributionSizeMaximum(maximum);
        double sizeRatio = 0.2;
        nearbySelectionConfig.setBlockDistributionSizeRatio(sizeRatio);
        double probability = 0.1;
        nearbySelectionConfig.setBlockDistributionUniformDistributionProbability(probability);

        assertThat(nearbySelectionConfig.buildNearbyRandom(true))
                .isEqualToComparingFieldByField(new BlockDistributionNearbyRandom(minimum, maximum, sizeRatio, probability));
    }

    @Test
    public void buildNearbyRandomWithLinearDistribution() {
        NearbySelectionConfig nearbySelectionConfig = buildNearbySelectionConfig();
        int maximum = 2;
        nearbySelectionConfig.setLinearDistributionSizeMaximum(maximum);

        assertThat(nearbySelectionConfig.buildNearbyRandom(true))
                .isEqualToComparingFieldByField(new LinearDistributionNearbyRandom(maximum));
    }

    @Test
    public void buildNearbyRandomWithParabolicDistribution() {
        NearbySelectionConfig nearbySelectionConfig = buildNearbySelectionConfig();
        int maximum = 2;
        nearbySelectionConfig.setParabolicDistributionSizeMaximum(maximum);

        assertThat(nearbySelectionConfig.buildNearbyRandom(true))
                .isEqualToComparingFieldByField(new ParabolicDistributionNearbyRandom(maximum));
    }

    @Test
    public void buildNearbyRandomWithBetaDistribution() {
        NearbySelectionConfig nearbySelectionConfig = buildNearbySelectionConfig();
        double alpha = 0.1;
        nearbySelectionConfig.setBetaDistributionAlpha(alpha);
        double beta = 0.2;
        nearbySelectionConfig.setBetaDistributionBeta(beta);

        // A RandomGenerator in BetaDistribution is not easily accessible through BetaDistributionNearbyRandom
        // and messes up equals, therefore only a class check is done, but is sufficient.
        assertThat(nearbySelectionConfig.buildNearbyRandom(true).getClass())
                .isEqualTo(BetaDistributionNearbyRandom.class);
    }

    @Test
    public void buildNearbyRandomWithDefaultDistribution() {
        NearbySelectionConfig nearbySelectionConfig = buildNearbySelectionConfig();

        assertThat(nearbySelectionConfig.buildNearbyRandom(true))
                .isEqualToComparingFieldByField(new LinearDistributionNearbyRandom(Integer.MAX_VALUE));
    }

    private NearbySelectionConfig buildNearbySelectionConfig() {
        NearbySelectionConfig nearbySelectionConfig = new NearbySelectionConfig();
        nearbySelectionConfig
                .setOriginEntitySelectorConfig(EntitySelectorConfig.newMimicSelectorConfig(new EntitySelectorConfig().getId()));
        nearbySelectionConfig.setNearbyDistanceMeterClass(Mockito.mock(NearbyDistanceMeter.class).getClass());
        return nearbySelectionConfig;
    }
}
