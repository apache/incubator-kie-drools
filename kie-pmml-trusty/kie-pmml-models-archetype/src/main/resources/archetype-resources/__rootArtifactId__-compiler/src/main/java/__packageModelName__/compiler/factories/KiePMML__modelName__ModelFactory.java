#set($str="")
#set($dt=$str.getClass().forName("java.util.Date").newInstance())
#set($year=$dt.getYear()+1900)
/*
 * Copyright ${year} Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package  ${package}.${packageModelName}.compiler.factories;

import java.util.Map;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.${packageModelName}.${modelName}Model;
import org.kie.pmml.commons.model.HasClassLoader;
import ${package}.${packageModelName}.model.KiePMML${modelName}Model;

public class KiePMML${modelName}ModelFactory {

    private KiePMML${modelName}ModelFactory(){
        // Avoid instantiation
    }

    public static KiePMML${modelName}Model getKiePMML${modelName}Model(final DataDictionary dataDictionary,
                                                                       final TransformationDictionary transformationDictionary,
                                                                       final ${modelName}Model model,
                                                                       final String packageName,
                                                                       final HasClassLoader hasClassLoader) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public static Map<String, String> getKiePMML${modelName}ModelSourcesMap(final DataDictionary dataDictionary,
                                                                                 final TransformationDictionary transformationDictionary,
                                                                                 final ${modelName}Model model,
                                                                                 final String packageName) {
        // TODO
        throw new UnsupportedOperationException();
    }


}
