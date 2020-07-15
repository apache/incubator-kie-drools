/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapters({
        @XmlJavaTypeAdapter(value = PolymorphicScoreJaxbAdapter.class, type = Score.class),
        @XmlJavaTypeAdapter(value = OffsetDateTimeJaxbAdapter.class, type = OffsetDateTime.class)
})
package org.optaplanner.benchmark.impl.result;

import java.time.OffsetDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.io.jaxb.OffsetDateTimeJaxbAdapter;
import org.optaplanner.persistence.jaxb.api.score.PolymorphicScoreJaxbAdapter;
