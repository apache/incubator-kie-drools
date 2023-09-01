package org.kie.drl.engine.runtime.mapinput.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.drl.engine.mapinput.compilation.model.test.Applicant;
import org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication;
import org.kie.drl.engine.runtime.mapinput.model.EfestoInputDrlMap;
import org.kie.drl.engine.runtime.mapinput.model.EfestoOutputDrlMap;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoMapInputDTO;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;

import static org.assertj.core.api.Assertions.assertThat;

class DrlRuntimeHelperTest {

    private static final String basePath = "LoanApplication";

    @Test
    void canManage() {
        ModelLocalUriId modelLocalUriId = new ModelLocalUriId(LocalUri.parse("/drl/" + basePath));
        EfestoRuntimeContext context = EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        BaseEfestoInput darInputDrlMap = new EfestoInputDrlMap(modelLocalUriId, new EfestoMapInputDTO(null, null,
                                                                                                      null, null,
                                                                                                      null, null));
        assertThat(DrlRuntimeHelper.canManage(darInputDrlMap, context)).isTrue();
        modelLocalUriId = new ModelLocalUriId(LocalUri.parse("/drl/notexisting"));
        darInputDrlMap = new EfestoInputDrlMap(modelLocalUriId, null);
        assertThat(DrlRuntimeHelper.canManage(darInputDrlMap, context)).isFalse();
    }

    @Test
    void execute() {
        List<Object> inserts = new ArrayList<>();

        inserts.add(new LoanApplication("ABC10001", new Applicant("John", 45), 2000, 1000));
        inserts.add(new LoanApplication("ABC10002", new Applicant("Paul", 25), 5000, 100));
        inserts.add(new LoanApplication("ABC10015", new Applicant("George", 12), 1000, 100));

        List<LoanApplication> approvedApplications = new ArrayList<>();
        final Map<String, Object> globals = new HashMap<>();
        globals.put("approvedApplications", approvedApplications);
        globals.put("maxAmount", 5000);

        EfestoMapInputDTO darMapInputDTO = new EfestoMapInputDTO(inserts, globals, Collections.emptyMap(), Collections.emptyMap(), "modelname", "packageName");
        EfestoInputDrlMap darInputDrlMap =
                new EfestoInputDrlMap(new ModelLocalUriId(LocalUri.parse("/drl/" + basePath)), darMapInputDTO);

        EfestoRuntimeContext context = EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        Optional<EfestoOutputDrlMap> retrieved = DrlRuntimeHelper.execute(darInputDrlMap, context);
        assertThat(retrieved).isNotNull().isPresent();
        assertThat(approvedApplications).hasSize(1);
        LoanApplication approvedApplication = approvedApplications.get(0);
        assertThat(approvedApplication).isEqualTo(inserts.get(0));
    }
}