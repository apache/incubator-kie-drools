/*
 * Copyright 2012 JBoss Inc
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

package org.drools.scorecards.parser.xls;

public interface XLSKeywords {

    public static final String SCORECARD_NAME = "Scorecard Name";
    public static final String SCORECARD_RESULTANT_SCORE_CLASS = "Resultant Score Class";
    public static final String SCORECARD_RESULTANT_SCORE_FIELD = "Resultant Score Field";
    public static final String SCORECARD_RESULTANT_REASONCODES_FIELD = "Resultant Reasoncodes Field";
    public static final String SCORECARD_CHARACTERISTIC_EXTERNAL_CLASS = "Full Class Name";

    public static final String SCORECARD_BASE_SCORE = "Initial Score";
    public static final String SCORECARD_IMPORTS = "imports";
    public static final String SCORECARD_PACKAGE = "package";
    public static final String SCORECARD_USE_REASONCODES = "Use Reason Codes";
    public static final String SCORECARD_REASONCODE = "Reason Code";
    public static final String SCORECARD_REASONCODE_ALGORITHM = "Reason Code Algorithm";

    public static final String SCORECARD_SCORING_STRATEGY = "Scoring Strategy";
    public static final String SCORECARD_WEIGHT = "Weight";

    public static final String SCORECARD_CHARACTERISTIC_NAME = "Name";
    public static final String SCORECARD_CHARACTERISTIC_DATATYPE = "Data Type";
    public static final String SCORECARD_CHARACTERISTIC_BASELINE_SCORE = "Baseline Score";

    public static final String SCORECARD_CHARACTERISTIC_BIN_ATTRIBUTE = "Characteristic";
    public static final String SCORECARD_CHARACTERISTIC_BIN_OPERATOR = "Operator";
    public static final String SCORECARD_CHARACTERISTIC_BIN_LABEL = "Value";
    public static final String SCORECARD_CHARACTERISTIC_BIN_INITIALSCORE = "Partial Score";
    public static final String SCORECARD_CHARACTERISTIC_BIN_DESC = "Description";
    public static final String DATATYPE_NUMBER = "Number";
    public static final String DATATYPE_TEXT = "Text";
    public static final String DATATYPE_BOOLEAN = "Boolean";
}
