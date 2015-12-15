/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.persistence.xstream.impl.domain.solution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import org.apache.commons.io.IOUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class XStreamSolutionFileIO implements SolutionFileIO {

    public static final String FILE_EXTENSION = "xml";

    private XStream xStream;

    public XStreamSolutionFileIO() {
        xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
    }

    public XStreamSolutionFileIO(Class... xStreamAnnotatedClasses) {
        this();
        xStream.processAnnotations(xStreamAnnotatedClasses);
    }

    public String getInputFileExtension() {
        return FILE_EXTENSION;
    }

    public String getOutputFileExtension() {
        return FILE_EXTENSION;
    }

    public Solution read(File inputSolutionFile) {
        Solution unsolvedSolution;
        Reader reader = null;
        try {
            // xStream.fromXml(InputStream) does not use UTF-8
            reader = new InputStreamReader(new FileInputStream(inputSolutionFile), "UTF-8");
            unsolvedSolution = (Solution) xStream.fromXML(reader);
        } catch (XStreamException e) {
            throw new IllegalArgumentException("Problem reading inputSolutionFile (" + inputSolutionFile + ").", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem reading inputSolutionFile (" + inputSolutionFile + ").", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return unsolvedSolution;
    }

    public void write(Solution solution, File outputSolutionFile) {
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(outputSolutionFile), "UTF-8");
            xStream.toXML(solution, writer);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing outputSolutionFile (" + outputSolutionFile + ").", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

}
