/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.pmml;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.feel.util.Either;
import org.kie.dmn.model.api.Import;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNImportPMMLInfo extends PMMLInfo<DMNPMMLModelInfo> {

    private static final Logger LOG = LoggerFactory.getLogger(DMNImportPMMLInfo.class);

    private final Import i;

    public DMNImportPMMLInfo(Import i, Collection<DMNPMMLModelInfo> models, PMMLHeaderInfo h) {
        super(models, h);
        this.i = i;
    }

    public static Either<Exception, DMNImportPMMLInfo> from(InputStream is, DMNModelImpl model, Import i) {
        try {
            PMMLInfo<?> prototype = PMMLInfo.from(is);
            PMMLHeaderInfo h = prototype.getHeader();
            List<DMNPMMLModelInfo> models = prototype.getModels().stream().map(proto -> DMNPMMLModelInfo.from(proto, model)).collect(Collectors.toList());
            DMNImportPMMLInfo info = new DMNImportPMMLInfo(i, models, h);
            return Either.ofRight(info);
        } catch (Throwable e) {
            return Either.ofLeft(new Exception(e));
        }
    }

    public String getImportName() {
        return i.getName();
    }

}
