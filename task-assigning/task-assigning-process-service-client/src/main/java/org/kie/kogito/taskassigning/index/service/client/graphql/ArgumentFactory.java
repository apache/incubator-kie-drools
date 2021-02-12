/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.taskassigning.index.service.client.graphql;

import java.time.ZonedDateTime;
import java.util.List;

import org.kie.kogito.taskassigning.index.service.client.graphql.date.DateEqualArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.date.DateGreaterThanArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.date.DateGreaterThanEqualArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.date.DateIsNullArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.date.DateLessThanArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.date.DateLessThanEqualArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.pagination.PaginationArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.string.StringEqualArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.string.StringInArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.string.StringIsNullArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.string.StringLikeArgument;

public class ArgumentFactory {

    private ArgumentFactory() {
    }

    public static StringInArgument newStringIn(List<String> values) {
        return new StringInArgument(values);
    }

    public static StringEqualArgument newStringEqual(String value) {
        return new StringEqualArgument(value);
    }

    public static StringIsNullArgument newStringIsNull(boolean isNull) {
        return new StringIsNullArgument(isNull);
    }

    public static StringLikeArgument newStringLike(String like) {
        return new StringLikeArgument(like);
    }

    public static DateGreaterThanArgument newDateGreaterThan(ZonedDateTime value) {
        return new DateGreaterThanArgument(value);
    }

    public static DateGreaterThanEqualArgument newDateGreaterThanEqual(ZonedDateTime value) {
        return new DateGreaterThanEqualArgument(value);
    }

    public static DateEqualArgument newDateEqual(ZonedDateTime value) {
        return new DateEqualArgument(value);
    }

    public static DateLessThanArgument newDateLessThan(ZonedDateTime value) {
        return new DateLessThanArgument(value);
    }

    public static DateLessThanEqualArgument newDateLessThanEqual(ZonedDateTime value) {
        return new DateLessThanEqualArgument(value);
    }

    public static DateIsNullArgument newDateIsNull(boolean isNull) {
        return new DateIsNullArgument(isNull);
    }

    public static PaginationArgument newPagination(int offset, int limit) {
        return new PaginationArgument(offset, limit);
    }
}
