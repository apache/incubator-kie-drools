package org.optaplanner.benchmark.impl.xsd;

import static org.optaplanner.benchmark.config.PlannerBenchmarkConfig.SOLVER_NAMESPACE_PREFIX;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.optaplanner.core.config.solver.SolverConfig;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class merges solver.xsd and benchmark.xsd into a single XML Schema file that contains both Solver and Benchmark XML
 * types under a single namespace of the benchmark.xsd.
 *
 * Both solver.xsd and benchmark.xsd declare its own namespace as they are supposed to be used for different purposes. As the
 * benchmark configuration contains solver configuration, the benchmark.xsd imports the solver.xsd. To avoid distributing
 * dependent schemas and using prefixes in users' XML configuration files, the types defined by solver.xsd are merged to
 * the benchmark.xsd under its namespace.
 */
public final class XsdAggregator {

    private static final String TNS_PREFIX = "tns";

    public static void main(String[] args) {
        if (args.length != 3) {
            String msg = "The XSD Aggregator expects 3 arguments:\n"
                    + "1) a path to the solver XSD file. \n"
                    + "2) a path to the benchmark XSD file. \n"
                    + "3) a path to an output file where the merged benchmark XSD should be saved to.";
            throw new IllegalArgumentException(msg);
        }
        File solverXsd = checkFileExists(new File(args[0]));
        File benchmarkXsd = checkFileExists(new File(args[1]));
        File outputXsd = new File(args[2]);

        if (!outputXsd.getParentFile().exists()) {
            outputXsd.getParentFile().mkdirs();
        }

        new XsdAggregator().mergeXmlSchemas(solverXsd, benchmarkXsd, outputXsd);
    }

    private static File checkFileExists(File file) {
        Objects.requireNonNull(file);
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format("The file (%s) does not exist.", file.getAbsolutePath()));
        }
        return file;
    }

    private void mergeXmlSchemas(File solverSchemaFile, File benchmarkSchemaFile, File outputSchemaFile) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document solverSchema = parseXml(solverSchemaFile, factory);
        Element solverRootElement = solverSchema.getDocumentElement();
        Document benchmarkSchema = parseXml(benchmarkSchemaFile, factory);

        removeReferencesToSolverConfig(benchmarkSchema, benchmarkSchemaFile);

        copySolverConfigTypes(benchmarkSchema, solverRootElement);

        Transformer transformer = createTransformer();
        DOMSource source = new DOMSource(benchmarkSchema);
        Result result = new StreamResult(outputSchemaFile);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new IllegalArgumentException(
                    "Failed to write the resulting XSD to a file (" + outputSchemaFile.getAbsolutePath() + ").", e);
        }
    }

    private Document parseXml(File xmlFile, DocumentBuilderFactory documentBuilderFactory) {
        DocumentBuilder builder;
        try {
            builder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalArgumentException("Failed to create a " + DocumentBuilder.class.getName() + "instance.", e);
        }

        try {
            return builder.parse(xmlFile);
        } catch (SAXException saxException) {
            throw new IllegalArgumentException("Failed to parse an XML file (" + xmlFile.getAbsolutePath() + ").",
                    saxException);
        } catch (IOException ioException) {
            throw new IllegalArgumentException("Failed to open an XML file (" + xmlFile.getAbsolutePath() + ").", ioException);
        }
    }

    private void removeReferencesToSolverConfig(Document benchmarkSchema, File benchmarkSchemaFile) {
        boolean solverNamespaceRemoved = false;
        boolean solverElementRefRemoved = false;
        boolean importRemoved = false;

        NodeList nodeList = benchmarkSchema.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Element element = (Element) node;

            if ("xs:schema".equals(node.getNodeName())) { // Remove the solver namespace declaration.
                element.removeAttribute("xmlns:" + SOLVER_NAMESPACE_PREFIX);
                solverNamespaceRemoved = true;
            }

            // Replace a reference to a solver element by a reference to type.
            if (isXsElement(node) && hasAttribute(node, "ref", SOLVER_NAMESPACE_PREFIX + ":" + SolverConfig.XML_ELEMENT_NAME)) {
                element.removeAttribute("ref");
                element.setAttribute("name", SolverConfig.XML_ELEMENT_NAME);
                element.setAttribute("type", TNS_PREFIX + ":" + SolverConfig.XML_TYPE_NAME);
                solverElementRefRemoved = true;
            }

            if ("xs:import".equals(node.getNodeName())) { // Remove the solver.xsd import.
                node.getParentNode().removeChild(node);
                importRemoved = true;
            }

            // Replace the solver namespace prefix by a standard "tns:" in all attributes.
            updateNodeAttributes(node,
                    attr -> attr.getValue() != null && attr.getValue().startsWith(SOLVER_NAMESPACE_PREFIX + ":"),
                    oldValue -> oldValue.replace(SOLVER_NAMESPACE_PREFIX + ":", TNS_PREFIX + ":"));
        }

        /*
         * Fail fast if some of the expected modifications were not done. Remaining modifications are necessary for
         * a successful validation by the resulting XML schema.
         */
        if (!solverElementRefRemoved) {
            String msg = String.format("An expected reference to the solver element was not found. Check the content of (%s).",
                    benchmarkSchemaFile);
            throw new AssertionError(msg);
        }

        if (!solverNamespaceRemoved) {
            String msg = String.format("An expected namespace (%s) declaration was not found. Check the content of (%s).",
                    SolverConfig.XML_NAMESPACE, benchmarkSchemaFile);
            throw new AssertionError(msg);
        }

        if (!importRemoved) {
            String msg = String.format("An expected import element was not found. Check the content of (%s).",
                    benchmarkSchemaFile);
            throw new AssertionError(msg);
        }
    }

    private void copySolverConfigTypes(Document benchmarkSchema, Element solverSchemaRoot) {
        Element benchmarkSchemaRoot = benchmarkSchema.getDocumentElement();
        NodeList solverChildNodes = solverSchemaRoot.getChildNodes();
        for (int i = 0; i < solverChildNodes.getLength(); i++) {
            Node node = solverChildNodes.item(i);
            boolean isSolverElementDeclaration =
                    isXsElement(node) && hasAttribute(node, "name", SolverConfig.XML_ELEMENT_NAME);
            if (!isSolverElementDeclaration) { // Skip the solver root element.
                benchmarkSchemaRoot.appendChild(benchmarkSchema.importNode(node, true));
            }
        }
    }

    private boolean isXsElement(Node node) {
        return "xs:element".equals(node.getNodeName());
    }

    private boolean hasAttribute(Node node, String attributeName, String attributeValue) {
        Objects.requireNonNull(node);
        Objects.requireNonNull(attributeName);
        Objects.requireNonNull(attributeValue);

        Attr attribute = ((Element) node).getAttributeNode(attributeName);
        return (attribute != null && attributeValue.equals(attribute.getValue()));
    }

    private void updateNodeAttributes(Node node, Predicate<Attr> attributePredicate, UnaryOperator<String> valueFunction) {
        Objects.requireNonNull(node);
        Objects.requireNonNull(attributePredicate);
        Objects.requireNonNull(valueFunction);
        for (int i = 0; i < node.getAttributes().getLength(); i++) {
            Attr attribute = (Attr) node.getAttributes().item(i);
            if (attributePredicate.test(attribute)) {
                attribute.setValue(valueFunction.apply(attribute.getValue()));
            }
        }
    }

    private Transformer createTransformer() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        // Protect the Transformer from XXE attacks.
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new IllegalArgumentException("Failed to create a " + Transformer.class.getName() + ".", e);
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        return transformer;
    }
}
