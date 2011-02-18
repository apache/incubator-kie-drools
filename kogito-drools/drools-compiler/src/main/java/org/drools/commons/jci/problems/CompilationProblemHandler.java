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
 * A CompilationProblemHandler gets access to a problem
 * as soon as the problem is available while the
 * CompilationResult only represents a summary at the
 * end.
 * The handler can be used to asynchronously update a
 * GUI or stop compilation by returning false (e.g.
 * when a maximum number of erros has been reached)
 * 
 * NOTE:
 * has to be supported by the compiler implementation!!
 * 
 * @author tcurdt
 */
public interface CompilationProblemHandler {

    boolean handle( final CompilationProblem pProblem );

}
