/**
 * Classes which represent the XML Benchmark configuration of OptaPlanner Benchmark.
 * <p>
 * The XML Benchmark configuration is backwards compatible for all elements,
 * except for elements that require the use of non public API classes.
 */
@javax.xml.bind.annotation.XmlSchema(
        namespace = PlannerBenchmarkConfig.XML_NAMESPACE,
        elementFormDefault = XmlNsForm.QUALIFIED,
        xmlns = {
                @XmlNs(namespaceURI = SolverConfig.XML_NAMESPACE, prefix = PlannerBenchmarkConfig.SOLVER_NAMESPACE_PREFIX)
        })
package org.optaplanner.benchmark.config;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;

import org.optaplanner.core.config.solver.SolverConfig;
