package org.optaplanner.examples.common.persistence;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.optaplanner.core.api.domain.solution.PlanningSolution;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractXmlSolutionExporter<Solution_> extends AbstractSolutionExporter<Solution_> {

    protected static final String DEFAULT_OUTPUT_FILE_SUFFIX = "xml";

    @Override
    public String getOutputFileSuffix() {
        return DEFAULT_OUTPUT_FILE_SUFFIX;
    }

    public abstract XmlOutputBuilder<Solution_> createXmlOutputBuilder();

    @Override
    public void writeSolution(Solution_ solution, File outputFile) {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            Document document = new Document();
            XmlOutputBuilder<Solution_> xmlOutputBuilder = createXmlOutputBuilder();
            xmlOutputBuilder.setDocument(document);
            xmlOutputBuilder.setSolution(solution);
            xmlOutputBuilder.writeSolution();
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(document, out);
            logger.info("Exported: {}", outputFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not write the file (" + outputFile.getName() + ").", e);
        } catch (JDOMException e) {
            throw new IllegalArgumentException("Could not format the XML file (" + outputFile.getName() + ").", e);
        }
    }

    public static abstract class XmlOutputBuilder<Solution_> extends OutputBuilder {

        protected Document document;

        public void setDocument(Document document) {
            this.document = document;
        }

        public abstract void setSolution(Solution_ solution);

        public abstract void writeSolution() throws IOException, JDOMException;

        // ************************************************************************
        // Helper methods
        // ************************************************************************

    }

}
