/**
 * Classes which represent the XML Solver configuration of OptaPlanner.
 * <p>
 * The XML Solver configuration is backwards compatible for all elements,
 * except for elements that require the use of non public API classes.
 */
@javax.xml.bind.annotation.XmlSchema(
        namespace = SolverConfig.XML_NAMESPACE,
        elementFormDefault = XmlNsForm.QUALIFIED)
package org.optaplanner.core.config;

import javax.xml.bind.annotation.XmlNsForm;

import org.optaplanner.core.config.solver.SolverConfig;
