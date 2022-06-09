package org.optaplanner.persistence.xstream.impl.domain.solution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.security.AnyTypePermission;

/**
 * Security warning: only use this class with XML files from a trusted source,
 * because {@link XStream} is configured to allow all permissions,
 * which can be exploited if the XML comes from an untrusted source.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class XStreamSolutionFileIO<Solution_> implements SolutionFileIO<Solution_> {

    protected XStream xStream;

    public XStreamSolutionFileIO(Class... xStreamAnnotatedClasses) {
        xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.processAnnotations(xStreamAnnotatedClasses);
        XStream.setupDefaultSecurity(xStream);
        // Presume the XML file comes from a trusted source so it works out of the box. See class javadoc.
        xStream.addPermission(new AnyTypePermission());
    }

    public XStream getXStream() {
        return xStream;
    }

    @Override
    public String getInputFileExtension() {
        return "xml";
    }

    @Override
    public Solution_ read(File inputSolutionFile) {
        try (InputStream inputSolutionStream = Files.newInputStream(inputSolutionFile.toPath())) {
            return read(inputSolutionStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed reading inputSolutionFile (" + inputSolutionFile + ").", e);
        }
    }

    public Solution_ read(InputStream inputSolutionStream) {
        // xStream.fromXml(InputStream) does not use UTF-8
        try (Reader reader = new InputStreamReader(inputSolutionStream, StandardCharsets.UTF_8)) {
            return (Solution_) xStream.fromXML(reader);
        } catch (XStreamException | IOException e) {
            throw new IllegalArgumentException("Failed reading inputSolutionStream.", e);
        }
    }

    @Override
    public void write(Solution_ solution, File outputSolutionFile) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(outputSolutionFile), StandardCharsets.UTF_8)) {
            xStream.toXML(solution, writer);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed writing outputSolutionFile (" + outputSolutionFile + ").", e);
        }
    }

}
