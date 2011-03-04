/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.common.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.NativeFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import org.drools.planner.core.solution.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class XstreamSolutionDaoImpl implements SolutionDao {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private XStream xStream;
    private String dirName;
    private File dataDir;

    public XstreamSolutionDaoImpl(String dirName, Class... xstreamAnnotations) {
        this.dirName = dirName;
        dataDir = new File("data/" + dirName);
        // TODO From Xstream 1.3.3 that KeySorter will be the default. See http://jira.codehaus.org/browse/XSTR-363
        xStream = new XStream(new PureJavaReflectionProvider(new FieldDictionary(new NativeFieldKeySorter())));
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.processAnnotations(xstreamAnnotations);
    }

    public String getDirName() {
        return dirName;
    }

    public File getDataDir() {
        return dataDir;
    }

    public Solution readSolution(File file) {
        Reader reader = null;
        try {
            // xStream.fromXml(InputStream) does not use UTF-8
            reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            Solution solution = (Solution) xStream.fromXML(reader);
            logger.info("Loaded: {}", file);
            return solution;
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read file: " + file, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.warn("Problem closing file (" + file + ")", e);
                }
            }
        }
    }

    public Solution readSolution(InputStream in) {
        Reader reader = null;
        try {
            // xStream.fromXml(InputStream) does not use UTF-8
            reader = new InputStreamReader(in, "UTF-8");
            Solution solution = (Solution) xStream.fromXML(reader);
            return solution;
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read from InputStream: " + in, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.warn("Problem closing InputStream (" + in + ")", e);
                }
            }
        }
    }

    public void writeSolution(Solution solution, File file) {
        Writer writer = null;
        try {
            // xStream.toXml(OutputStream) does not use UTF-8
            writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            xStream.toXML(solution, writer);
            logger.info("Saved: {}", file);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not write file (" + file + ")", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.warn("Problem closing file (" + file + ")", e);
                }
            }
        }
    }

}
