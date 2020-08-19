package org.drools.workbench.models.guided.dtable.shared.util;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ColumnUtilitiesBaseTest {

    @Mock
    private GuidedDecisionTable52 model;

    private ColumnUtilitiesBase columnUtilities;

    @Before
    public void setUp() throws Exception {
        columnUtilities = new ColumnUtilitiesBase(model) {
            @Override
            /**
             * Simplified testing implementation returning same type as passed factType
             */
            protected String getTypeFromDataOracle(final String factType,
                                                   final String fieldName) {
                return factType;
            }
        };
    }

    @Test
    public void testConvertToTypeSafeType() {
        assertEquals(DataType.DataTypes.LOCAL_DATE, columnUtilities.convertToTypeSafeType("LocalDate"));
    }
}
