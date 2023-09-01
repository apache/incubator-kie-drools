package org.drools.scenariosimulation.backend.util;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNModel;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNSimulationUtilsTest {

    @Test
    public void findDMNModel() {
        List<String> pathToFind = List.of(new StringBuilder("to/find").reverse().toString().split("/"));

        List<DMNModel> models = List.of(createDMNModelMock("this/should/not/match"), createDMNModelMock("find"), 
        		createDMNModelMock("something/to/find"));

        DMNSimulationUtils.findDMNModel(models, pathToFind, 1);

        List<String> impossibleToFind = List.of(new StringBuilder("not/find").reverse().toString().split("/"));

        assertThatThrownBy(() -> DMNSimulationUtils.findDMNModel(models, impossibleToFind, 1))
                .isInstanceOf(ImpossibleToFindDMNException.class)
                .hasMessage("Retrieving the DMNModel has failed. Make sure the used DMN asset does not " +
                                    "produce any compilation errors and that the project does not " +
                                    "contain multiple DMN assets with the same name and namespace. " +
                                    "In addition, check if the reference to the DMN file is correct " +
                                    "in the Settings panel. " +
                                    "After addressing the issues, build the project again.");
    }

    private DMNModel createDMNModelMock(String path) {
        DMNModel modelMock = mock(DMNModel.class);
        Resource resourceMock = mock(Resource.class);
        when(resourceMock.getSourcePath()).thenReturn(path);
        when(modelMock.getResource()).thenReturn(resourceMock);
        return modelMock;
    }
}