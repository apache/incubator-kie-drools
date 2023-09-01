package org.kie.pmml.compiler.commons.implementations;

import java.util.List;

import org.dmg.pmml.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.compiler.commons.mocks.TestingModelImplementationProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelImplementationProviderFinderImplTest {

    private ModelImplementationProviderFinderImpl modelImplementationProviderFinder;

    @BeforeEach
    public void setUp() throws Exception {
        modelImplementationProviderFinder = new ModelImplementationProviderFinderImpl();
    }

    @Test
 <T extends Model, E extends KiePMMLModel> void getImplementations() {
        final List<ModelImplementationProvider<T, E>> retrieved = modelImplementationProviderFinder.getImplementations(false);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
        assertThat(retrieved.get(0)).isInstanceOf(TestingModelImplementationProvider.class);
    }
}