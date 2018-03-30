package org.kie.dmn.core.compiler;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.kie.dmn.feel.util.Either;
import org.kie.dmn.model.v1_1.Import;

public class ImportDMNResolverUtil {

    private ImportDMNResolverUtil() {
        // No constructor for util class.
    }

    public static <T> Either<String, T> resolveImportDMN(Import _import, Collection<T> all, Function<T, QName> idExtractor) {
        final String iNamespace = _import.getNamespace();
        final String iName = _import.getAdditionalAttributes().get(Import.NAME_QNAME);
        final String iModelName = _import.getAdditionalAttributes().get(Import.MODELNAME_QNAME);
        List<T> allInNS = all.stream()
                             .filter(m -> idExtractor.apply(m).getNamespaceURI().equals(iNamespace))
                             .collect(Collectors.toList());
        if (allInNS.size() == 1) {
            T located = allInNS.get(0);
            // Check if the located DMN Model in the NS, correspond for the import `drools:modelName`. 
            if (iModelName == null || idExtractor.apply(located).getLocalPart().equals(iModelName)) {
                return Either.ofRight(located);
            } else {
                return Either.ofLeft(String.format("While importing DMN for namespace: %s, name: %s, modelName: %s, located within namespace only %s but does not match for the actual name",
                                                   iNamespace, iName, iModelName,
                                                   idExtractor.apply(located)));
            }
        } else {
            List<T> usingNSandName = allInNS.stream()
                                            .filter(m -> idExtractor.apply(m).getLocalPart().equals(iModelName))
                                            .collect(Collectors.toList());
            if (usingNSandName.size() == 1) {
                return Either.ofRight(usingNSandName.get(0));
            } else if (usingNSandName.size() == 0) {
                return Either.ofLeft(String.format("Could not locate required dependency while importing DMN for namespace: %s, name: %s, modelName: %s.",
                                                   iNamespace, iName, iModelName));
            } else {
                return Either.ofLeft(String.format("While importing DMN for namespace: %s, name: %s, modelName: %s, could not locate required dependency within: %s.",
                                                   iNamespace, iName, iModelName,
                                                   allInNS.stream().map(idExtractor).collect(Collectors.toList())));
            }
        }
    }

    public static enum ImportType {
        UNKNOWN,
        DMN;
    }

    public static ImportType whichImportType(Import _import) {
        switch (_import.getImportType()) {
            case "http://www.omg.org/spec/DMN/20151101/dmn.xsd":
            case "http://www.omg.org/spec/DMN1-2Alpha/20160929/MODEL":
                return ImportType.DMN;
            default:
                return ImportType.UNKNOWN;
        }
    }
}
