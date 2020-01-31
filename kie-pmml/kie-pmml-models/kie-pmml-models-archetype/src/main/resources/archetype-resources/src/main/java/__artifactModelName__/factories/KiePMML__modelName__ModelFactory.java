#set( $str = "" )
#set( $dt = $str.getClass().forName("java.util.Date").newInstance() )
#set( $year = $dt.getYear() + 1900 )
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
package org.kie.pmml.models.${artifactModelName}.factories;

import java.util.logging.Logger;

import javax.swing.text.html.Option;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.${artifactModelName}.${modelName}Model;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.${artifactModelName}.KiePMML${modelName}Model;


public class KiePMML${modelName}ModelFactory {

    private static final Logger log = Logger.getLogger(KiePMML${modelName}ModelFactory.class.getName());

    private KiePMML${modelName}ModelFactory() {
    }

    public static KiePMML${modelName}Model getKiePMML${modelName}Model(DataDictionary dataDictionary, ${modelName}Model model) throws KiePMMLException {
        log.info("getKiePMMLModel " + model);
        // TODO
        return null;
    }
}
