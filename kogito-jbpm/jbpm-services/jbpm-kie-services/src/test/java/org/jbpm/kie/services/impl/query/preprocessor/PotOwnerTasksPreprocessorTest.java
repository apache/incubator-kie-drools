package org.jbpm.kie.services.impl.query.preprocessor;

import java.util.ArrayList;

import org.dashbuilder.dataset.DataSetLookup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.internal.identity.IdentityProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PotOwnerTasksPreprocessorTest {

    @Mock
    public IdentityProvider identityProvider;


    public DataSetLookup dataSetLookup;

    @InjectMocks
    public PotOwnerTasksPreprocessor potOwnerTasksPreprocessor;

    @Test
    public void testSetUser() {
        dataSetLookup = new DataSetLookup();
        String role1="role1";
        String role2="role2";
        String userId="userId";

        ArrayList<String> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);
        DataSetLookup dataSetLookup = new DataSetLookup();
        when(identityProvider.getRoles()).thenReturn(roles);
        when(identityProvider.getName()).thenReturn(userId);

        potOwnerTasksPreprocessor.preprocess(dataSetLookup);

        assertEquals("(((ID = " + role1 + ", " + role2 + " AND (ACTUALOWNER =  OR ACTUALOWNER is_null )) OR ACTUALOWNER = "+userId+"))",
                dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }


}
