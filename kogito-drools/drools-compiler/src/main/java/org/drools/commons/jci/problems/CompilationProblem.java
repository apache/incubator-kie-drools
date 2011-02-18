/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.commons.jci.problems;

/**
 * An abstract definition of a compilation problem
 */
public interface CompilationProblem {

    /**
     * is the problem an error and compilation cannot continue
     * or just a warning and compilation can proceed
     * 
     * @return
     */
    boolean isError();

    /**
     * name of the file where the problem occurred
     * 
     * @return
     */
    String getFileName();

    /**
     * position of where the problem starts in the source code
     * 
     * @return
     */
    int getStartLine();
    int getStartColumn();

    /**
     * position of where the problem stops in the source code
     * 
     * @return
     */
    int getEndLine();
    int getEndColumn();

    /**
     * the description of the problem
     * 
     * @return
     */
    String getMessage();

}
