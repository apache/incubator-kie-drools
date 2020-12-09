package org.kie.internal.pmml;

import org.junit.Test;
import org.kie.api.pmml.PMMLConstants;

import static org.junit.Assert.assertEquals;
import static org.kie.api.pmml.PMMLConstants.KIE_PMML_IMPLEMENTATION;
import static org.kie.api.pmml.PMMLConstants.LEGACY;
import static org.kie.api.pmml.PMMLConstants.NEW;

public class PMMLImplementationsUtilTest {


    @Test
    public void testGetPMMLConstantsBothPresentWithSysProp() {
        System.setProperty(KIE_PMML_IMPLEMENTATION.getName(), NEW.getName());
        PMMLConstants retrieved = PMMLImplementationsUtil.getPMMLConstants(true, true);
        assertEquals(NEW, retrieved);
        System.setProperty(KIE_PMML_IMPLEMENTATION.getName(), LEGACY.getName());
        retrieved = PMMLImplementationsUtil.getPMMLConstants(true, true);
        assertEquals(LEGACY, retrieved);
    }

    @Test
    public void testGetPMMLConstantsPathLegacyPresentWithSysProp() {
        System.setProperty(KIE_PMML_IMPLEMENTATION.getName(), LEGACY.getName());
        PMMLConstants retrieved = PMMLImplementationsUtil.getPMMLConstants(true, false);
        assertEquals(LEGACY, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPMMLConstantsPathLegacyPresentWithSysPropWrong() {
        System.setProperty(KIE_PMML_IMPLEMENTATION.getName(), NEW.getName());
         PMMLImplementationsUtil.getPMMLConstants(true, false);
    }

    @Test
    public void testGetPMMLConstantsNewPresentWithSysProp() {
        System.setProperty(KIE_PMML_IMPLEMENTATION.getName(), NEW.getName());
        PMMLConstants retrieved = PMMLImplementationsUtil.getPMMLConstants(false, true);
        assertEquals(NEW, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPMMLConstantsPathNewPresentWithSysPropWrong() {
        System.setProperty(KIE_PMML_IMPLEMENTATION.getName(), LEGACY.getName());
        PMMLImplementationsUtil.getPMMLConstants(false, true);
    }

    @Test
    public void testGetFromClassPathBothPresent() {
        PMMLConstants retrieved = PMMLImplementationsUtil.getFromClassPath(true, true);
        assertEquals(NEW, retrieved);
    }

    @Test
    public void testGetFromClassPathLegacyPresent() {
        PMMLConstants retrieved = PMMLImplementationsUtil.getFromClassPath(true, false);
        assertEquals(LEGACY, retrieved);
    }

    @Test
    public void testGetFromClassPathNewPresent() {
        PMMLConstants retrieved = PMMLImplementationsUtil.getFromClassPath(false, true);
        assertEquals(NEW, retrieved);
    }

    @Test
    public void testReturnImplementationLegacyPresent() {
        PMMLConstants retrieved = PMMLImplementationsUtil.returnImplementation(LEGACY, true);
        assertEquals(LEGACY, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReturnImplementationLegacyNotPresent() {
        PMMLImplementationsUtil.returnImplementation(LEGACY, false);
    }

    @Test
    public void testReturnImplementationNewPresent() {
        PMMLConstants retrieved = PMMLImplementationsUtil.returnImplementation(NEW, true);
        assertEquals(NEW, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReturnImplementationNewNotPresent() {
        PMMLImplementationsUtil.returnImplementation(NEW, false);
    }



}