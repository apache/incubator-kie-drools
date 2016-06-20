/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.jaxb.impl.domain.solution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import org.apache.commons.io.IOUtils;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class JaxbSolutionFileIO<Solution_> implements SolutionFileIO<Solution_> {

    public static final String FILE_EXTENSION = "xml";

    private final Marshaller marshaller;
    private final Unmarshaller unmarshaller;

    public JaxbSolutionFileIO(Class... jaxbAnnotatedClasses) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(jaxbAnnotatedClasses);
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            throw new IllegalArgumentException("JAXB creation for classes (" + Arrays.toString(jaxbAnnotatedClasses)
                    + ") has failed.", e);
        }
    }

    @Override
    public String getInputFileExtension() {
        return FILE_EXTENSION;
    }

    @Override
    public String getOutputFileExtension() {
        return FILE_EXTENSION;
    }

    @Override
    public Solution_ read(File inputSolutionFile) {
        Solution_ unsolvedSolution;
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(inputSolutionFile), "UTF-8");
            unsolvedSolution = (Solution_) unmarshaller.unmarshal(reader);
        } catch (IOException | JAXBException e) {
            throw new IllegalArgumentException("Problem reading inputSolutionFile (" + inputSolutionFile + ").", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return unsolvedSolution;
    }

    @Override
    public void write(Solution_ solution, File outputSolutionFile) {
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(outputSolutionFile), "UTF-8");
            marshaller.marshal(solution, writer);
        } catch (IOException | JAXBException e) {
            throw new IllegalArgumentException("Problem writing outputSolutionFile (" + outputSolutionFile + ").", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

}
