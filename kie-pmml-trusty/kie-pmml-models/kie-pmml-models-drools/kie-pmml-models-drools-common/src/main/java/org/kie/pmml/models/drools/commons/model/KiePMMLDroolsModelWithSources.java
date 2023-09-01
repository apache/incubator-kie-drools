package org.kie.pmml.models.drools.commons.model;

import java.util.List;
import java.util.Map;

import org.drools.drl.ast.descr.PackageDescr;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.pmml.api.identifiers.KiePmmlComponentRoot;
import org.kie.pmml.api.identifiers.LocalComponentIdPmml;
import org.kie.pmml.api.identifiers.PmmlIdFactory;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.api.models.TargetField;
import org.kie.pmml.commons.HasRedirectOutput;
import org.kie.pmml.commons.HasRule;
import org.kie.pmml.commons.model.IsDrools;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

/**
 * KIE representation of PMML model that use <b>Drools</b> for implementation
 */
public class KiePMMLDroolsModelWithSources extends KiePMMLModelWithSources implements IsDrools,
                                                                                      HasRule,
                                                                                      HasRedirectOutput<PackageDescr> {

    private static final long serialVersionUID = -168095076511604775L;
    private final String pkgUUID;

    private final PackageDescr packageDescr;

    private final EfestoRedirectOutputPMMLDrl redirectOutput;

    public KiePMMLDroolsModelWithSources(final String fileName,
                                         final String modelName,
                                         final String kmodulePackageName,
                                         final List<MiningField> miningFields,
                                         final List<OutputField> outputFields,
                                         final List<TargetField> targetFields,
                                         final Map<String, String> sourcesMap,
                                         final String pkgUUID,
                                         final PackageDescr packageDescr) {
        super(fileName, modelName, kmodulePackageName, miningFields, outputFields, targetFields, sourcesMap, false);
        this.pkgUUID = pkgUUID;
        this.packageDescr = packageDescr;
        LocalComponentIdPmml modelLocalUriId = new EfestoAppRoot()
                .get(KiePmmlComponentRoot.class)
                .get(PmmlIdFactory.class)
                .get(fileName, getSanitizedClassName(modelName));
        redirectOutput = new EfestoRedirectOutputPMMLDrl(modelLocalUriId, packageDescr);
    }

    @Override
    public String getPkgUUID() {
        return pkgUUID;
    }

    @Override
    public PackageDescr getPackageDescr() {
        return packageDescr;
    }

    @Override
    public EfestoRedirectOutputPMMLDrl getRedirectOutput() {
        return redirectOutput;
    }
}
