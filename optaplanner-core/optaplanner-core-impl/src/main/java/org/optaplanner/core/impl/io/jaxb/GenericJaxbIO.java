package org.optaplanner.core.impl.io.jaxb;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.optaplanner.core.impl.io.OptaPlannerXmlSerializationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public final class GenericJaxbIO<T> implements JaxbIO<T> {
    private static final int DEFAULT_INDENTATION = 2;

    private static final String ERR_MSG_WRITE = "Failed to marshall a root element class (%s) to XML.";
    private static final String ERR_MSG_READ = "Failed to unmarshall a root element class (%s) from XML.";
    private static final String ERR_MSG_READ_OVERRIDE_NAMESPACE =
            "Failed to unmarshall a root element class (%s) from XML with overriding elements' namespaces: (%s).";

    private final JAXBContext jaxbContext;
    private final Marshaller marshaller;
    private final Class<T> rootClass;
    private final int indentation;

    public GenericJaxbIO(Class<T> rootClass) {
        this(rootClass, DEFAULT_INDENTATION);
    }

    public GenericJaxbIO(Class<T> rootClass, int indentation) {
        Objects.requireNonNull(rootClass);
        this.rootClass = rootClass;
        this.indentation = indentation;
        try {
            jaxbContext = JAXBContext.newInstance(rootClass);
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.toString());
        } catch (JAXBException jaxbException) {
            String errorMessage = String.format("Failed to create JAXB Marshaller for a root element class (%s).",
                    rootClass.getName());
            throw new OptaPlannerXmlSerializationException(errorMessage, jaxbException);
        }
    }

    @Override
    public T read(Reader reader) {
        Objects.requireNonNull(reader);
        try {
            return (T) createUnmarshaller().unmarshal(reader);
        } catch (JAXBException jaxbException) {
            String errorMessage = String.format(ERR_MSG_READ, rootClass.getName());
            throw new OptaPlannerXmlSerializationException(errorMessage, jaxbException);
        }
    }

    public T readAndValidate(Reader reader, String schemaResource) {
        Objects.requireNonNull(reader);
        Schema schema = readSchemaResource(schemaResource);
        return readAndValidate(reader, schema);
    }

    public T readAndValidate(Document document, String schemaResource) {
        return readAndValidate(document, readSchemaResource(schemaResource));
    }

    private Schema readSchemaResource(String schemaResource) {
        String nonNullSchemaResource = Objects.requireNonNull(schemaResource);
        URL schemaResourceUrl = GenericJaxbIO.class.getResource(nonNullSchemaResource);
        if (schemaResourceUrl == null) {
            throw new IllegalArgumentException("The XML schema (" + nonNullSchemaResource + ") does not exist.\n"
                    + "Maybe build the sources with Maven first?");
        }
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        } catch (SAXNotSupportedException | SAXNotRecognizedException saxException) {
            String errorMessage = String.format(
                    "Failed to configure the %s to validate an XML for a root class (%s) using the (%s) XML Schema.",
                    SchemaFactory.class.getSimpleName(), rootClass.getName(), schemaResource);
            throw new OptaPlannerXmlSerializationException(errorMessage, saxException);
        }

        try {
            return schemaFactory.newSchema(schemaResourceUrl);
        } catch (SAXException saxException) {
            String errorMessage =
                    String.format("Failed to read an XML Schema resource (%s) to validate an XML for a root class (%s).",
                            nonNullSchemaResource, rootClass.getName());
            throw new OptaPlannerXmlSerializationException(errorMessage, saxException);
        }
    }

    public T readAndValidate(Reader reader, Schema schema) {
        Document document = parseXml(Objects.requireNonNull(reader));
        return readAndValidate(document, Objects.requireNonNull(schema));
    }

    public T readAndValidate(Document document, Schema schema) {
        Document nonNullDocument = Objects.requireNonNull(document);
        Schema nonNullSchema = Objects.requireNonNull(schema);
        Unmarshaller unmarshaller = createUnmarshaller();
        unmarshaller.setSchema(nonNullSchema);

        ValidationEventCollector validationEventCollector = new ValidationEventCollector();
        try {
            unmarshaller.setEventHandler(validationEventCollector);
        } catch (JAXBException jaxbException) {
            String errorMessage = String.format("Failed to set a validation event handler to the %s for "
                    + "a root element class (%s).", Unmarshaller.class.getSimpleName(), rootClass.getName());
            throw new OptaPlannerXmlSerializationException(errorMessage, jaxbException);
        }

        try {
            return (T) unmarshaller.unmarshal(nonNullDocument);
        } catch (JAXBException jaxbException) {
            if (validationEventCollector.hasEvents()) {
                String errorMessage =
                        String.format("XML validation failed for a root element class (%s).", rootClass.getName());
                String validationErrors = Stream.of(validationEventCollector.getEvents())
                        .map(validationEvent -> validationEvent.getMessage()
                                + "\nNode: "
                                + validationEvent.getLocator().getNode().getNodeName())
                        .collect(Collectors.joining("\n"));
                String errorMessageWithValidationEvents = errorMessage + "\n" + validationErrors;
                throw new OptaPlannerXmlSerializationException(errorMessageWithValidationEvents, jaxbException);
            } else {
                String errorMessage = String.format(ERR_MSG_READ, rootClass.getName());
                throw new OptaPlannerXmlSerializationException(errorMessage, jaxbException);
            }
        }
    }

    /**
     * Reads the input XML using the {@link Reader} overriding elements namespaces. If an element already has a namespace and
     * a {@link ElementNamespaceOverride} is defined for this element, its namespace is overridden. In case the element has no
     * namespace, new namespace defined in the {@link ElementNamespaceOverride} is added.
     *
     * @param reader input XML {@link Reader}; never null
     * @param elementNamespaceOverrides never null
     * @return deserialized object representation of the XML.
     */
    public T readOverridingNamespace(Reader reader, ElementNamespaceOverride... elementNamespaceOverrides) {
        Objects.requireNonNull(reader);
        Objects.requireNonNull(elementNamespaceOverrides);
        return readOverridingNamespace(parseXml(reader), elementNamespaceOverrides);
    }

    /**
     * Reads the input XML {@link Document} overriding namespaces. If an element already has a namespace and
     * a {@link ElementNamespaceOverride} is defined for this element, its namespace is overridden. In case the element has no
     * namespace a new namespace defined in the {@link ElementNamespaceOverride} is added.
     *
     * @param document input XML {@link Document}; never null
     * @param elementNamespaceOverrides never null
     * @return deserialized object representation of the XML.
     */
    public T readOverridingNamespace(Document document, ElementNamespaceOverride... elementNamespaceOverrides) {
        Document translatedDocument =
                overrideNamespaces(Objects.requireNonNull(document), Objects.requireNonNull(elementNamespaceOverrides));
        try {
            return (T) createUnmarshaller().unmarshal(translatedDocument);
        } catch (JAXBException e) {
            final String errorMessage = String.format(ERR_MSG_READ_OVERRIDE_NAMESPACE, rootClass.getName(),
                    Arrays.toString(elementNamespaceOverrides));
            throw new OptaPlannerXmlSerializationException(errorMessage, e);
        }
    }

    public Document parseXml(Reader reader) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder builder;
        try {
            builder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            String errorMessage = String.format("Failed to create a %s instance to parse an XML for a root class (%s).",
                    DocumentBuilder.class.getSimpleName(), rootClass.getName());
            throw new OptaPlannerXmlSerializationException(errorMessage, e);
        }

        try (Reader nonNullReader = Objects.requireNonNull(reader)) {
            return builder.parse(new InputSource(nonNullReader));
        } catch (SAXException saxException) {
            String errorMessage = String.format("Failed to parse an XML for a root class (%s).", rootClass.getName());
            throw new OptaPlannerXmlSerializationException(errorMessage, saxException);
        } catch (IOException ioException) {
            String errorMessage = String.format("Failed to read an XML for a root class (%s).", rootClass.getName());
            throw new OptaPlannerXmlSerializationException(errorMessage, ioException);
        }
    }

    private Unmarshaller createUnmarshaller() {
        try {
            return jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            String errorMessage = String.format("Failed to create a JAXB %s for a root element class (%s).",
                    Unmarshaller.class.getSimpleName(), rootClass.getName());
            throw new OptaPlannerXmlSerializationException(errorMessage, e);
        }
    }

    public void validate(Document document, String schemaResource) {
        Schema schema = readSchemaResource(Objects.requireNonNull(schemaResource));
        validate(Objects.requireNonNull(document), schema);
    }

    public void validate(Document document, Schema schema) {
        Validator validator = Objects.requireNonNull(schema).newValidator();
        try {
            validator.validate(new DOMSource(Objects.requireNonNull(document)));
        } catch (SAXException saxException) {
            String errorMessage =
                    String.format("XML validation failed for a root element class (%s).", rootClass.getName())
                            + "\n"
                            + saxException.getMessage();
            throw new OptaPlannerXmlSerializationException(errorMessage, saxException);
        } catch (IOException ioException) {
            String errorMessage = String.format("Failed to read an XML for a root element class (%s) during validation.",
                    rootClass.getName());
            throw new OptaPlannerXmlSerializationException(errorMessage, ioException);
        }
    }

    @Override
    public void write(T root, Writer writer) {
        DOMResult domResult = marshall(Objects.requireNonNull(root));
        formatXml(new DOMSource(domResult.getNode()), null, Objects.requireNonNull(writer));
    }

    public void writeWithoutNamespaces(T root, Writer writer) {
        DOMResult domResult = marshall(Objects.requireNonNull(root));
        Writer nonNullWriter = Objects.requireNonNull(writer);
        try (InputStream xsltInputStream = getClass().getResourceAsStream("removeNamespaces.xslt")) {
            formatXml(new DOMSource(domResult.getNode()), new StreamSource(xsltInputStream), nonNullWriter);
        } catch (IOException e) {
            throw new OptaPlannerXmlSerializationException(String.format(ERR_MSG_WRITE, rootClass.getName()), e);
        }
    }

    private DOMResult marshall(T root) {
        Objects.requireNonNull(root);
        DOMResult domResult = new DOMResult();
        try {
            marshaller.marshal(root, domResult);
        } catch (JAXBException jaxbException) {
            throw new OptaPlannerXmlSerializationException(String.format(ERR_MSG_WRITE, rootClass.getName()), jaxbException);
        }
        return domResult;
    }

    private void formatXml(Source source, Source transformationTemplate, Writer writer) {
        /*
         * The code is not vulnerable to XXE-based attacks as it does not process any external XML nor XSL input.
         * Should the transformerFactory be used for such purposes, it has to be appropriately secured:
         * https://owasp.org/www-project-top-ten/OWASP_Top_Ten_2017/Top_10-2017_A4-XML_External_Entities_(XXE)
         */
        @SuppressWarnings({ "java:S2755", "java:S4435" })
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformationTemplate == null ? transformerFactory.newTransformer()
                    : transformerFactory.newTransformer(transformationTemplate);
            // See https://stackoverflow.com/questions/46708498/jaxb-marshaller-indentation.
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indentation));
            transformer.transform(source, new StreamResult(writer));
        } catch (TransformerException transformerException) {
            String errorMessage = String.format("Failed to format XML for a root element class (%s).", rootClass.getName());
            throw new OptaPlannerXmlSerializationException(errorMessage, transformerException);
        }
    }

    private Document overrideNamespaces(Document document, ElementNamespaceOverride... elementNamespaceOverrides) {
        Document nonNullDocument = Objects.requireNonNull(document);
        final Map<String, String> elementNamespaceOverridesMap = new HashMap<>();
        for (ElementNamespaceOverride namespaceOverride : Objects.requireNonNull(elementNamespaceOverrides)) {
            elementNamespaceOverridesMap.put(namespaceOverride.getElementLocalName(),
                    namespaceOverride.getNamespaceOverride());
        }

        final Deque<NamespaceOverride> preOrderNodes = new LinkedList<>();
        preOrderNodes.push(new NamespaceOverride(nonNullDocument.getDocumentElement(), null));
        while (!preOrderNodes.isEmpty()) {
            NamespaceOverride currentNodeOverride = preOrderNodes.pop();
            Node currentNode = currentNodeOverride.node;
            final String elementLocalName =
                    currentNode.getLocalName() == null ? currentNode.getNodeName() : currentNode.getLocalName();

            // Is there any override defined for the current node?
            String detectedNamespaceOverride = elementNamespaceOverridesMap.get(elementLocalName);
            String effectiveNamespaceOverride =
                    detectedNamespaceOverride != null ? detectedNamespaceOverride : currentNodeOverride.namespace;

            if (effectiveNamespaceOverride != null) {
                nonNullDocument.renameNode(currentNode, effectiveNamespaceOverride, elementLocalName);
            }

            processChildNodes(currentNode,
                    (childNode -> {
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            preOrderNodes.push(new NamespaceOverride(childNode, effectiveNamespaceOverride));
                        }
                    }));
        }

        return nonNullDocument;
    }

    private void processChildNodes(Node node, Consumer<Node> nodeConsumer) {
        NodeList childNodes = node.getChildNodes();
        if (childNodes != null) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode != null) {
                    nodeConsumer.accept(childNode);
                }
            }
        }
    }

    private static final class NamespaceOverride {
        private final Node node;
        private final String namespace;

        private NamespaceOverride(Node node, String namespace) {
            this.node = node;
            this.namespace = namespace;
        }
    }
}
