/*
 * Copyright 2011 JBoss Inc
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

package org.optaplanner.persistence.xstream;

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
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.NativeFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import org.apache.commons.io.IOUtils;
import org.optaplanner.core.impl.solution.ProblemIO;
import org.optaplanner.core.impl.solution.Solution;

public class XStreamProblemIO implements ProblemIO {

    private XStream xStream;

    public XStreamProblemIO() {
        // TODO From Xstream 1.3.3 that KeySorter will be the default. See http://jira.codehaus.org/browse/XSTR-363
        xStream = new XStream(new PureJavaReflectionProvider(new FieldDictionary(new NativeFieldKeySorter())));
        xStream.setMode(XStream.ID_REFERENCES);
    }

    public XStreamProblemIO(Class... xstreamAnnotatedClasses) {
        this();
        xStream.processAnnotations(xstreamAnnotatedClasses);
    }

    public String getFileExtension() {
        return "xml";
    }

    public Solution read(File inputSolutionFile) {
        Solution unsolvedSolution;
        Reader reader = null;
        try {
            // xStream.fromXml(InputStream) does not use UTF-8
            reader = new InputStreamReader(new FileInputStream(inputSolutionFile), "UTF-8");
            unsolvedSolution = (Solution) xStream.fromXML(reader);
        } catch (XStreamException e) {
            throw new IllegalArgumentException("Problem reading inputSolutionFile: " + inputSolutionFile, e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem reading inputSolutionFile: " + inputSolutionFile, e);
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
            throw new IllegalArgumentException("Problem writing outputSolutionFile: " + outputSolutionFile, e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

}
