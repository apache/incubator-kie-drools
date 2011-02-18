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

package org.drools.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;



/**
 * This interface is mostly provided so a DRL provider can live in
 * drools-compiler, without it, we would have circular references.
 * @author Michael Neale
 *
 */
public interface FileLoader {

    public org.drools.rule.Package loadPackage(File drl) throws FileNotFoundException, IOException;

}
