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

package org.drools.verifier.report.html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.mvel2.templates.TemplateRuntime;


abstract class ReportVisitor {

	protected static String processHeader(String folder) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sourceFolder", folder);

		map.put("objectTypesFile", UrlFactory.HTML_FILE_INDEX);
		map.put("packagesFile", UrlFactory.HTML_FILE_PACKAGES);
		map.put("messagesFile", UrlFactory.HTML_FILE_VERIFIER_MESSAGES);

		String myTemplate = readFile("header.htm");

		return String.valueOf(TemplateRuntime.eval(myTemplate, map));
	}

	protected static String readFile(String fileName) {
		StringBuffer str = new StringBuffer("");
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					ReportVisitor.class.getResourceAsStream(fileName)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				str.append(line);
				str.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("File " + fileName + " was not found.");
			e.printStackTrace();
		}
		return str.toString();
	}

	protected static String createStyleTag(String path) {
		StringBuffer str = new StringBuffer("");

		str.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
		str.append(path);
		str.append("\" />");

		return str.toString();
	}
}
