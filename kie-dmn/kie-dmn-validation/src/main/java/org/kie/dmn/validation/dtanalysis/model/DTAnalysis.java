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

package org.kie.dmn.validation.dtanalysis.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.validation.dtanalysis.DMNDTAnalysisMessage;

public class DTAnalysis {

    private final List<Hyperrectangle> gaps = new ArrayList<>();
    private final List<Overlap> overlaps = new ArrayList<>();
    private final DecisionTable sourceDT;
    private final Throwable error;
    private final DDTATable ddtaTable;

    public DTAnalysis(DecisionTable sourceDT, DDTATable ddtaTable) {
        this.sourceDT = sourceDT;
        this.error = null;
        this.ddtaTable = ddtaTable;
    }

    private DTAnalysis(DecisionTable sourceDT, Throwable error) {
        this.sourceDT = sourceDT;
        this.error = error;
        this.ddtaTable = null;
    }

    public static DTAnalysis ofError(DecisionTable sourceDT, Throwable error) {
        return new DTAnalysis(sourceDT, error);
    }

    public boolean isError() {
        return error != null;
    }

    public DDTATable getDdtaTable() {
        return ddtaTable;
    }

    public Collection<Hyperrectangle> getGaps() {
        return Collections.unmodifiableList(gaps);
    }

    public void addGap(Hyperrectangle gap) {
        this.gaps.add(gap);
    }

    public DMNModelInstrumentedBase getSource() {
        return sourceDT;
    }

    public List<Overlap> getOverlaps() {
        return Collections.unmodifiableList(overlaps);
    }

    public void addOverlap(Overlap overlap) {
        this.overlaps.add(overlap);
    }

    public void normalize() {
        int prevSize = this.overlaps.size();
        internal_normalize();
        int curSize = this.overlaps.size();
        if (curSize != prevSize) {
            normalize();
        }
    }

    private void internal_normalize() {
        List<Overlap> newOverlaps = new ArrayList<>();
        List<Overlap> overlapsProcessing = new ArrayList<>();
        overlapsProcessing.addAll(overlaps);
        while (!overlapsProcessing.isEmpty()) {
            Overlap curOverlap = overlapsProcessing.remove(0);
            for (Overlap otherOverlap : overlapsProcessing) {
                int x = curOverlap.contigousOnDimension(otherOverlap);
                if (x > 0) {
                    Overlap mergedOverlap = Overlap.newByMergeOnDimension(curOverlap, otherOverlap, x);
                    curOverlap = null;
                    overlapsProcessing.remove(otherOverlap);
                    overlapsProcessing.add(0, mergedOverlap);
                }
            }
            if (curOverlap != null) {
                newOverlaps.add(curOverlap);
            }
        }
        this.overlaps.clear();
        this.overlaps.addAll(newOverlaps);
    }

    public List<? extends DMNMessage> asDMNMessages() {
        List<? extends DMNMessage> results = new ArrayList<>();
        if (isError()) {
            DMNMessage m = new DMNDTAnalysisMessage(this,
                                                    Severity.WARN,
                                                    MsgUtil.createMessage(Msg.DTANALYSIS_ERROR_ANALYSIS_SKIPPED,
                                                                          sourceDT.getOutputLabel(),
                                                                          error.getMessage()),
                                                    Msg.DTANALYSIS_ERROR_ANALYSIS_SKIPPED.getType());
            results.addAll((Collection) Arrays.asList(m));
            return results;
        }
        results.addAll(gapsAsMessages());
        results.addAll(overlapsAsMessages());

        // keep last.
        if (results.isEmpty()) {
            DMNMessage m = new DMNDTAnalysisMessage(this,
                                                    Severity.INFO,
                                                    MsgUtil.createMessage(Msg.DTANALYSIS_EMPTY,
                                                                          sourceDT.getOutputLabel()),
                                                    Msg.DTANALYSIS_EMPTY.getType());
            results.addAll((Collection) Arrays.asList(m));
            return results;
        }
        return results;
    }

    private Collection overlapsAsMessages() {
        List<DMNDTAnalysisMessage> results = new ArrayList<>();
        for (Overlap overlap : overlaps) {
            switch (sourceDT.getHitPolicy()) {
                case UNIQUE:
                    results.add(new DMNDTAnalysisMessage(this,
                                                         Severity.ERROR,
                                                         MsgUtil.createMessage(Msg.DTANALYSIS_OVERLAP_HITPOLICY_UNIQUE,
                                                                               overlap.asHumanFriendly(ddtaTable)),
                                                         Msg.DTANALYSIS_OVERLAP_HITPOLICY_UNIQUE.getType()));
                    break;
                case ANY:
                    List<Comparable<?>> prevValue = ddtaTable.getRule().get(overlap.getRules().get(0).intValue() - 1).getOutputEntry();
                    for (int i = 1; i < overlap.getRules().size(); i++) { // deliberately start index 1 for 2nd overlapping rule number.
                        int curIndex = overlap.getRules().get(i).intValue() - 1;
                        List<Comparable<?>> curValue = ddtaTable.getRule().get(curIndex).getOutputEntry();
                        if (!prevValue.equals(curValue)) {
                            results.add(new DMNDTAnalysisMessage(this,
                                                                 Severity.ERROR,
                                                                 MsgUtil.createMessage(Msg.DTANALYSIS_OVERLAP_HITPOLICY_ANY,
                                                                                       overlap.asHumanFriendly(ddtaTable)),
                                                                 Msg.DTANALYSIS_OVERLAP_HITPOLICY_ANY.getType()));
                            break;
                        } else {
                            prevValue = curValue;
                        }
                    }
                    break;
                default:
                    results.add(new DMNDTAnalysisMessage(this,
                                                         Severity.WARN,
                                                         MsgUtil.createMessage(Msg.DTANALYSIS_OVERLAP,
                                                                               overlap.asHumanFriendly(ddtaTable)),
                                                         Msg.DTANALYSIS_OVERLAP.getType()));
                    break;
            }
        }
        return results;
    }

    private Collection gapsAsMessages() {
        List<DMNDTAnalysisMessage> results = new ArrayList<>();
        for (Hyperrectangle gap : gaps) {
            results.add(new DMNDTAnalysisMessage(this,
                                                 Severity.WARN,
                                                 MsgUtil.createMessage(Msg.DTANALYSIS_GAP,
                                                                       gap.asHumanFriendly(ddtaTable)),
                                                 Msg.DTANALYSIS_GAP.getType()));
        }
        return results;
    }

}
