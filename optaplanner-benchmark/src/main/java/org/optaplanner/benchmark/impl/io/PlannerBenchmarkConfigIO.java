package org.optaplanner.benchmark.impl.io;

import java.io.Reader;
import java.io.Writer;

import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.io.jaxb.ElementNamespaceOverride;
import org.optaplanner.core.impl.io.jaxb.GenericJaxbIO;
import org.optaplanner.core.impl.io.jaxb.JaxbIO;
import org.w3c.dom.Document;

public class PlannerBenchmarkConfigIO implements JaxbIO<PlannerBenchmarkConfig> {

    private static final String BENCHMARK_XSD_RESOURCE = "/benchmark.xsd";
    private final GenericJaxbIO<PlannerBenchmarkConfig> genericJaxbIO = new GenericJaxbIO<>(PlannerBenchmarkConfig.class);

    @Override
    public PlannerBenchmarkConfig read(Reader reader) {
        Document document = genericJaxbIO.parseXml(reader);
        String rootElementNamespace = document.getDocumentElement().getNamespaceURI();
        if (PlannerBenchmarkConfig.XML_NAMESPACE.equals(rootElementNamespace)) { // If there is the correct namespace, validate.
            genericJaxbIO.validate(document, BENCHMARK_XSD_RESOURCE);
            /*
             * In JAXB annotations the SolverConfig belongs to a different namespace than the PlannerBenchmarkConfig.
             * However, benchmark.xsd merges both namespaces into a single one. As a result, JAXB is incapable of matching
             * the solver element in benchmark configuration and thus the solver element's namespace needs to be overridden.
             */
            return genericJaxbIO.readOverridingNamespace(document,
                    ElementNamespaceOverride.of(SolverConfig.XML_ELEMENT_NAME, SolverConfig.XML_NAMESPACE));
        } else if (rootElementNamespace == null || rootElementNamespace.isEmpty()) {
            // If not, add the missing namespace to maintain backward compatibility.
            return genericJaxbIO.readOverridingNamespace(document,
                    ElementNamespaceOverride.of(PlannerBenchmarkConfig.XML_ELEMENT_NAME, PlannerBenchmarkConfig.XML_NAMESPACE),
                    ElementNamespaceOverride.of(SolverConfig.XML_ELEMENT_NAME, SolverConfig.XML_NAMESPACE));
        } else { // If there is an unexpected namespace, fail fast.
            String errorMessage = String.format("The <%s/> element belongs to a different namespace (%s) than expected (%s).",
                    PlannerBenchmarkConfig.XML_ELEMENT_NAME, rootElementNamespace, PlannerBenchmarkConfig.XML_NAMESPACE);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    @Override
    public void write(PlannerBenchmarkConfig plannerBenchmarkConfig, Writer writer) {
        genericJaxbIO.writeWithoutNamespaces(plannerBenchmarkConfig, writer);
    }
}
