package org.jbpm.kie.services.impl.query.preprocessor;

import java.util.Arrays;
import java.util.Collections;

import org.dashbuilder.dataset.DataSetLookup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.internal.identity.IdentityProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PotOwnerTasksPreprocessorTest {

    @Mock
    IdentityProvider identityProvider;

    DataSetLookup dataSetLookup;

    @InjectMocks
    PotOwnerTasksPreprocessor potOwnerTasksPreprocessor;

    @Before
    public void init() {
        dataSetLookup = new DataSetLookup();
    }

    @Test
    public void testSetUser() {
        String role1 = "role1";
        String role2 = "role2";
        String userId = "userId";

        when(identityProvider.getRoles()).thenReturn(Arrays.asList(role1, role2));
        when(identityProvider.getName()).thenReturn(userId);

        potOwnerTasksPreprocessor.preprocess(dataSetLookup);

        assertEquals("((ID = " + role1 + ", " + role2 + ", " + userId + " AND (ACTUALOWNER =  OR ACTUALOWNER is_null )) OR ACTUALOWNER = " + userId + ")",
                dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }

    @Test
    public void testSetUserWithoutRoles() {
        String userId = "userId";

        when(identityProvider.getRoles()).thenReturn(Collections.emptyList());
        when(identityProvider.getName()).thenReturn(userId);

        potOwnerTasksPreprocessor.preprocess(dataSetLookup);

        assertEquals("((ID = " + userId + " AND (ACTUALOWNER =  OR ACTUALOWNER is_null )) OR ACTUALOWNER = " + userId + ")",
                dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }

}
