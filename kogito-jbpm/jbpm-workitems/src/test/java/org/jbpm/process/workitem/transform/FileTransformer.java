/**
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

package org.jbpm.process.workitem.transform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.jbpm.process.workitem.transform.Transformer;

public class FileTransformer {

	// All transform methods must be static and must contain the @Transformer annotation
	@Transformer
	public static String fileToString(File file) {
		try {
			String fileString = "";
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = reader.readLine()) != null) {
				fileString += line;
			}
			return fileString;
		} catch (Exception e) {
			System.err.println("Failed to read file " + file.getName());
		}
		return null;
	}

	@Transformer
	public static BufferedReader fileToBufferedReader(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			return reader;
		} catch (Exception e) {
			System.err.println("Failed to read file " + file.getName());
		}
		return null;
	}
}
