package org.optaplanner.examples.batchscheduling.solver.score;

import java.util.HashMap;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.examples.batchscheduling.app.BatchSchedulingApp;
import org.optaplanner.examples.batchscheduling.domain.Allocation;
import org.optaplanner.examples.batchscheduling.domain.AllocationPath;
import org.optaplanner.examples.batchscheduling.domain.Batch;
import org.optaplanner.examples.batchscheduling.domain.RoutePath;
import org.optaplanner.examples.batchscheduling.domain.Schedule;
import org.optaplanner.examples.batchscheduling.domain.Segment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchSchedulingIncrementalScoreCalculator
        implements IncrementalScoreCalculator<Schedule, BendableLongScore> {

    final Logger logger = LoggerFactory.getLogger(BatchSchedulingIncrementalScoreCalculator.class);

    // hard0Score is sum of penalties for all the Batches. It is computed at Batch
    // level. There are 2 types of Penalties.
    //
    // a) SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY:
    // a1) Applicable if RoutePath is selected for a Batch but delay is not set for
    // one or more segments present in the RoutePath
    // OR
    // a2) if RoutePath is not set for a Batch
    //
    // b) NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY:
    // Set if RoutePath is not the selected RoutePath but delay is set for a segment
    // in the RoutePath
    private long hard0Score = 0;

    // hard1Score is computed at segment level (i.e. not at Batch level). It helps
    // arriving at result faster because it operates at segment level.
    //
    // a) For scenarios where RoutePath is selected, it is computed by adding
    // following 2 segment counts:
    // a1) Count of segments which are part of RoutePath but delay is not set and
    // a2) Count of segments which are not part of RoutePath but delay is set
    //
    // b) For scenarios where RoutePath is not selected for a Batch, it is the count
    // of all segments across all the RoutePaths for the given Batch.
    private long hard1Score = 0;

    // hard2score computes overlap (i.e. time overlap) across batches (i.e. No two
    // bathes should be present in the same segment at the same time)
    private long hard2Score = 0;

    // soft0Score is the time taken to inject first Batch till delivery of last
    // batch
    private long soft0Score = 0;

    // Count of segments through which commodity has not traversed.
    // Ideal value is 0 (i.e. All segments have been utilized)
    // This soft score helps all routePaths to be utilized.
    private long soft1Score = 0;

    // Map to store BatchId and selectedRoutePath for that Batch
    private Map<Long, String> batchRoutePathMap;

    // Map to store BatchId and count of segments that are part of selectedRoutePath
    // but don't have delay assigned
    private Map<Long, Long> batchCurrentPenaltyValueMap;

    // Map to store BatchId and count of segments that are not part of
    // selectedRoutePath but have delay assigned
    private Map<Long, Long> batchOtherPenaltyValueMap;

    // Map to store Maximum EndTime for every batch
    private Map<Long, Long> batchEndTimeMap;

    // Stores mapping between segmentId and Delay value
    private Map<Long, Long> allocationDelayMap;

    // For calculating hardscore2
    // This map will always contain even number of entries (i.e. If Segment A
    // overlaps B, then B also overlaps A)
    private Map<String, Long> segmentOverlapMap;

    // Following 4 Maps contain mapping between SegmentId and different timings
    // Used for determining overlap values
    private Map<Long, Long> allocationStartInjectionTime;
    private Map<Long, Long> allocationEndInjectionTimeMap;
    private Map<Long, Long> allocationStartDeliveryTimeMap;
    private Map<Long, Long> allocationEndDeliveryTimeMap;

    private Map<Long, Segment> segmentMap;

    // List to store unique segmentString (not segmentId). Boolean field is not
    // used.
    private Map<String, Boolean> segmentStringMap;

    public static String generateCompositeKey(String key1, String key2) {
        return key1 + "#" + key2;
    }

    @Override
    public void resetWorkingSolution(Schedule schedule) {

        batchRoutePathMap = new HashMap<Long, String>();
        segmentMap = new HashMap<Long, Segment>();
        segmentStringMap = new HashMap<String, Boolean>();
        batchOtherPenaltyValueMap = new HashMap<Long, Long>();
        batchCurrentPenaltyValueMap = new HashMap<Long, Long>();
        batchEndTimeMap = new HashMap<Long, Long>();
        segmentOverlapMap = new HashMap<String, Long>();

        allocationDelayMap = new HashMap<Long, Long>();
        allocationStartInjectionTime = new HashMap<Long, Long>();
        allocationEndInjectionTimeMap = new HashMap<Long, Long>();
        allocationStartDeliveryTimeMap = new HashMap<Long, Long>();
        allocationEndDeliveryTimeMap = new HashMap<Long, Long>();

        hard0Score = 0L;
        hard1Score = 0L;
        hard2Score = 0L;
        soft0Score = 0L;
        soft1Score = 0L;

        for (Batch batch : schedule.getBatchList()) {
            batchRoutePathMap.put(batch.getId(), null);
            batchOtherPenaltyValueMap.put(batch.getId(), 0L);
            batchCurrentPenaltyValueMap.put(batch.getId(), 0L);
            batchEndTimeMap.put(batch.getId(), 0L);

            for (RoutePath routePath : batch.getRoutePathList()) {
                for (Segment segment : routePath.getSegmentList()) {
                    segmentMap.put(segment.getId(), segment);
                    segmentStringMap.put(segment.getName(), true);
                }
            }
        }

        for (AllocationPath allocationPath : schedule.getAllocationPathList()) {
            insert(allocationPath);
        }

        for (Allocation allocation : schedule.getAllocationList()) {
            insert(allocation);
        }

        printScore("init");
    }

    @Override
    public void beforeEntityAdded(Object entity) {
        // Do Nothing
    }

    @Override
    public void afterEntityAdded(Object entity) {
        // Do Nothing
    }

    @Override
    public void beforeVariableChanged(Object entity, String variableName) {
        if (entity instanceof Allocation) {
            if (!(variableName.equals("predecessorsDoneDate"))) {
                retract((Allocation) entity);
            } else {
                retractPredecessorDate((Allocation) entity);
            }
        } else if (entity instanceof AllocationPath) {
            retract((AllocationPath) entity);
        }
    }

    @Override
    public void afterVariableChanged(Object entity, String variableName) {
        if (entity instanceof Allocation) {
            if (!(variableName.equals("predecessorsDoneDate"))) {
                insert((Allocation) entity);
            } else {
                insertPredecessorDate((Allocation) entity);
            }

        } else if (entity instanceof AllocationPath) {
            insert((AllocationPath) entity);
        }
    }

    @Override
    public void beforeEntityRemoved(Object entity) {
        // Do Nothing
    }

    @Override
    public void afterEntityRemoved(Object entity) {
        // Do Nothing
    }

    public void printScore(String temp) {
        logger.debug(String.format("%-" + 50 + "." + 50 + "s", temp) + ":: " + hard0Score + " " + hard1Score + " "
                + hard2Score + " / " + soft0Score + " " + soft1Score);
    }

    // Compute Penalties (i.e. hardScore0 and hardScore1), Overlaps (i.e.
    // hardScore2), softScore0 and softScore1
    private void insert(AllocationPath allocationPath) {
        printScore("Insert Start AllocationPath");

        // Get existing current and other penalty values for the batch
        Long oldOtherPenaltyValue = batchOtherPenaltyValueMap.get(allocationPath.getBatch().getId());
        Long oldCurrentPenaltyValue = batchCurrentPenaltyValueMap.get(allocationPath.getBatch().getId());

        Long newOtherPenaltyValue = 0L;
        Long newCurrentPenaltyValue = 0L;

        // Start of compute overlap
        for (Map.Entry<Long, Segment> entry1 : segmentMap.entrySet()) {

            Segment mainSegment = entry1.getValue();

            // Continue if Segment Batch is not same as the Input Parameter Batch
            if (mainSegment.getBatch().getId() != allocationPath.getBatch().getId()) {
                continue;
            }

            // Continue if RoutePath is set to null
            if (allocationPath.getRoutePath() == null) {
                newCurrentPenaltyValue += 1;
                continue;
            }

            // Continue if Segment RoutePath is different from the Input Parameter RoutePath
            if (mainSegment.getRoutePath().getId() != allocationPath.getRoutePath().getId()) {

                // Same Batch Different RoutePath from the Preferred RoutePath
                if (allocationDelayMap.get(entry1.getKey()) != null) {
                    newOtherPenaltyValue += 1;
                }
                continue;
            }

            if (allocationDelayMap.get(entry1.getKey()) == null) {
                newCurrentPenaltyValue += 1;
                continue;
            }

            computeOverlap(allocationPath.getBatch().getId(), entry1.getKey(), entry1.getValue().getName(),
                    allocationStartInjectionTime.get(entry1.getKey()),
                    allocationEndInjectionTimeMap.get(entry1.getKey()),
                    allocationStartDeliveryTimeMap.get(entry1.getKey()),
                    allocationEndDeliveryTimeMap.get(entry1.getKey()));
        }

        // Apply new penalty if any segment in nonSelectedRoutePath has delay assigned
        // Applicable if no NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldOtherPenaltyValue == 0) && (newOtherPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Remove existing penalty if any segment in nonSelectedRoutePath has delay
        // assigned
        // Applicable only if NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldOtherPenaltyValue > 0) && (newOtherPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Apply penalty if any segment in selectedRoutePath has delay not assigned
        // Applicable if no SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldCurrentPenaltyValue == 0) && (newCurrentPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        // Remove penalty if all segments in selectedRoutePath have delay assigned
        // Applicable only if SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldCurrentPenaltyValue > 0) && (newCurrentPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        if (allocationPath.getRoutePath() != null) {
            batchRoutePathMap.put(allocationPath.getBatch().getId(), allocationPath.getRoutePath().getPath());
        } else {
            batchRoutePathMap.remove(allocationPath.getBatch().getId());
        }

        updateBatchEndDate(allocationPath);

        // Compute hardscore1
        batchOtherPenaltyValueMap.put(allocationPath.getBatch().getId(), newOtherPenaltyValue);
        batchCurrentPenaltyValueMap.put(allocationPath.getBatch().getId(), newCurrentPenaltyValue);
        hard1Score = hard1Score - newOtherPenaltyValue - newCurrentPenaltyValue + oldOtherPenaltyValue
                + oldCurrentPenaltyValue;

        soft0Score = -getMaxEndTime();
        soft1Score = -computeRoutePathSegmentOverlap();

        printScore("Insert End AllocationPath");
    }

    // Compute Penalties (i.e. hardScore0 and hardScore1), Overlaps (i.e.
    // hardScore2), softScore0 and softScore1
    private void retract(AllocationPath allocationPath) {

        printScore("Retract Start AllocationPath");

        // Get existing current and other penalty values for the batch
        Long oldOtherPenaltyValue = batchOtherPenaltyValueMap.get(allocationPath.getBatch().getId());
        Long oldCurrentPenaltyValue = batchCurrentPenaltyValueMap.get(allocationPath.getBatch().getId());
        Long newOtherPenaltyValue = 0L;
        Long newCurrentPenaltyValue = 0L;

        // Start of compute overlap
        for (Map.Entry<Long, Segment> entry1 : segmentMap.entrySet()) {

            // Continue if Segment Batch is not same as the Input Parameter Batch
            if (entry1.getValue().getBatch().getId() != allocationPath.getBatch().getId()) {
                continue;
            }

            newCurrentPenaltyValue = newCurrentPenaltyValue + 1;

            for (Map.Entry<Long, Segment> entry2 : segmentMap.entrySet()) {

                // Continue if Segment Batch is not same as the Input Parameter Batch
                if (entry2.getValue().getBatch().getId() == allocationPath.getBatch().getId()) {
                    continue;
                }

                // Continue if Inner Segment Name is same as the Outer Segment Name
                if (!(entry2.getValue().getName().equals(entry1.getValue().getName()))) {
                    continue;
                }

                // Check if overlap exists in the map. If no overlap exists then continue.
                if (segmentOverlapMap
                        .get(generateCompositeKey(entry1.getKey().toString(), entry2.getKey().toString())) == null) {
                    continue;
                }

                // If overlap exists then remove the overlap score from hard2score. Also remove
                // the overlap from the Map.
                // Notice the multiplication factor of 2 because if A overlaps B, then B also
                // overlaps A
                hard2Score += (2 * segmentOverlapMap
                        .get(generateCompositeKey(entry1.getKey().toString(), entry2.getKey().toString())));
                segmentOverlapMap.remove(generateCompositeKey(entry1.getKey().toString(), entry2.getKey().toString()));
                segmentOverlapMap.remove(generateCompositeKey(entry2.getKey().toString(), entry1.getKey().toString()));
            }
        }
        // End of compute overlap

        // Apply new penalty if any segment in nonSelectedRoutePath has delay assigned
        // Applicable if no NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldOtherPenaltyValue == 0) && (newOtherPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Remove existing penalty if any segment in nonSelectedRoutePath has delay
        // assigned
        // Applicable only if NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldOtherPenaltyValue > 0) && (newOtherPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Apply penalty if any segment in selectedRoutePath has delay not assigned
        // Applicable if no SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldCurrentPenaltyValue == 0) && (newCurrentPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        // Remove penalty if all segments in selectedRoutePath have delay assigned
        // Applicable only if SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldCurrentPenaltyValue > 0) && (newCurrentPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        batchRoutePathMap.remove(allocationPath.getBatch().getId());
        batchOtherPenaltyValueMap.put(allocationPath.getBatch().getId(), newOtherPenaltyValue);
        batchCurrentPenaltyValueMap.put(allocationPath.getBatch().getId(), newCurrentPenaltyValue);
        hard1Score = hard1Score - newOtherPenaltyValue - newCurrentPenaltyValue + oldOtherPenaltyValue
                + oldCurrentPenaltyValue;

        printScore("Retract End AllocationPath");
    }

    // Compute Penalties (i.e. hardScore0 and hardScore1), Overlaps (i.e.
    // hardScore2), softScore0 and softScore1
    private void insert(Allocation allocation) {

        printScore("Insert Start Allocation");

        // If RoutePath is not set for the Input Parameter Batch then update Map values
        // and return
        if (batchRoutePathMap.get(allocation.getBatch().getId()) == null) {
            allocationDelayMap.put(allocation.getSegment().getId(), allocation.getDelay());
            allocationStartInjectionTime.put(allocation.getSegment().getId(), allocation.getStartInjectionTime());
            allocationEndInjectionTimeMap.put(allocation.getSegment().getId(), allocation.getEndInjectionTime());
            allocationStartDeliveryTimeMap.put(allocation.getSegment().getId(), allocation.getStartDeliveryTime());
            allocationEndDeliveryTimeMap.put(allocation.getSegment().getId(), allocation.getEndDeliveryTime());
            printScore("Insert End Allocation");
            return;
        }

        Long oldCurrentPenaltyValue = batchCurrentPenaltyValueMap.get(allocation.getBatch().getId());
        Long newCurrentPenaltyValue = oldCurrentPenaltyValue;
        Long oldOtherPenaltyValue = batchOtherPenaltyValueMap.get(allocation.getBatch().getId());
        Long newOtherPenaltyValue = oldOtherPenaltyValue;

        // If input segment is not part of the selectedRoutePath, then compare previous
        // delay value with new delay value.
        // If previous value is null but new value is not null, then add penalty for
        // that segment
        // else if previous value is not null but new value is null, then remove penalty
        // for that segment
        if (!(allocation.getRoutePath().getPath().equals(batchRoutePathMap.get(allocation.getBatch().getId())))) {
            if ((allocation.getDelay() != null) && (allocationDelayMap.get(allocation.getSegment().getId()) == null)) {
                newOtherPenaltyValue = newOtherPenaltyValue + 1;
            } else if ((allocation.getDelay() != null)
                    && (allocationDelayMap.get(allocation.getSegment().getId()) == null)) {
                newOtherPenaltyValue = newOtherPenaltyValue - 1;
            }
        }

        // If input segment is part of the selectedRoutePath, then compare previous
        // delay value with new delay value.
        // If previous value is not null but new value is null, then add penalty for
        // that segment
        // else if previous value is null but new value is not null, then remove penalty
        // for that segment
        if (allocation.getRoutePath().getPath().equals(batchRoutePathMap.get(allocation.getBatch().getId()))) {
            if ((allocation.getDelay() != null) && (allocationDelayMap.get(allocation.getSegment().getId()) == null)) {
                newCurrentPenaltyValue = newCurrentPenaltyValue - 1;
            } else if ((allocation.getDelay() == null)
                    && (allocationDelayMap.get(allocation.getSegment().getId()) != null)) {
                newCurrentPenaltyValue = newCurrentPenaltyValue + 1;
            }

        }

        boolean computeOverLap = false;

        // Determine if overlap needs to be computed.
        // Overlap is computed if input segment is part of the selectedRoutePath and
        // delay and Injection start time are not null
        if (allocation.getRoutePath().getPath().equals(batchRoutePathMap.get(allocation.getBatch().getId()))) {
            if ((allocation.getDelay() != null) && (allocationDelayMap.get(allocation.getSegment().getId()) == null)) {
                if (allocation.getStartInjectionTime() != null) {
                    computeOverLap = true;
                }
            }
        }

        // Start of compute overlap
        if (computeOverLap) {
            computeOverlap(allocation.getBatch().getId(), allocation.getSegment().getId(),
                    allocation.getSegment().getName(), allocation.getStartInjectionTime(),
                    allocation.getEndInjectionTime(), allocation.getStartDeliveryTime(),
                    allocation.getEndDeliveryTime());
        }
        // End of compute overlap

        // Apply new penalty if any segment in nonSelectedRoutePath has delay assigned
        // Applicable if no NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldOtherPenaltyValue == 0) && (newOtherPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Remove existing penalty if any segment in nonSelectedRoutePath has delay
        // assigned
        // Applicable only if NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldOtherPenaltyValue > 0) && (newOtherPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Apply penalty if any segment in selectedRoutePath has delay not assigned
        // Applicable if no SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldCurrentPenaltyValue == 0) && (newCurrentPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        // Remove penalty if all segments in selectedRoutePath have delay assigned
        // Applicable only if SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldCurrentPenaltyValue > 0) && (newCurrentPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        allocationDelayMap.put(allocation.getSegment().getId(), allocation.getDelay());
        allocationStartInjectionTime.put(allocation.getSegment().getId(), allocation.getStartInjectionTime());
        allocationEndInjectionTimeMap.put(allocation.getSegment().getId(), allocation.getEndInjectionTime());
        allocationStartDeliveryTimeMap.put(allocation.getSegment().getId(), allocation.getStartDeliveryTime());
        allocationEndDeliveryTimeMap.put(allocation.getSegment().getId(), allocation.getEndDeliveryTime());

        updateBatchEndDate(allocation);

        batchOtherPenaltyValueMap.put(allocation.getBatch().getId(), newOtherPenaltyValue);
        batchCurrentPenaltyValueMap.put(allocation.getBatch().getId(), newCurrentPenaltyValue);
        hard1Score = hard1Score - newOtherPenaltyValue - newCurrentPenaltyValue + oldOtherPenaltyValue
                + oldCurrentPenaltyValue;
        soft0Score = -getMaxEndTime();

        printScore("Insert End Allocation");
    }

    private void computeOverlap(Long batchId, Long segmentId, String segmentName, Long mainStartTime1,
            Long mainEndTime1, Long mainStartTime2, Long mainEndTime2) {

        for (Map.Entry<Long, Segment> entry2 : segmentMap.entrySet()) {

            // Continue if Segment Delay is null
            if (allocationDelayMap.get(entry2.getKey()) == null) {
                continue;
            }

            // Continue if Segment Batch is not same as the Input Parameter Batch
            if (entry2.getValue().getBatch().getId() == batchId) {
                continue;
            }

            // Continue if RoutePath has not been selected for the Segment Batch
            if (batchRoutePathMap.get(entry2.getValue().getBatch().getId()) == null) {
                continue;
            }

            // Continue if Segment is not part of the selectedRoutepath
            if (!(entry2.getValue().getRoutePath().getPath()
                    .equals(batchRoutePathMap.get(entry2.getValue().getBatch().getId())))) {
                continue;
            }

            // Continue if Segment Name is not same as the Input parameter Segment Name
            // Note that comparison is made using Segment Name and not Segment Id as same
            // Segment Name may be part of different RoutePath (hence different Segment Id)
            if (!(entry2.getValue().getName().equals(segmentName))) {
                continue;
            }

            Long newOverlapPenaltyValue = 0L;

            // Check for following 4 conditions for Injection:
            // Condition 1) If inner segment injection start time is less than outer segment
            // injection start time and inner segment injection end date is more than outer
            // segment injection end time
            // Condition 2) If inner segment injection start time is more than outer segment
            // injection start time and inner segment injection end date is less than outer
            // segment injection end time
            // Condition 3) If inner segment injection start time is less than outer segment
            // injection start time and inner segment injection end date is more than outer
            // segment injection start time
            // Condition 4) If inner segment injection start time is less than outer segment
            // injection end time and inner segment injection end date is more than outer
            // segment injection end time

            if ((allocationStartInjectionTime.get(entry2.getKey()) <= mainStartTime1)
                    && (allocationEndInjectionTimeMap.get(entry2.getKey()) >= mainEndTime1)) {
                newOverlapPenaltyValue = mainEndTime1 - mainStartTime1;
            } else if ((allocationStartInjectionTime.get(entry2.getKey()) >= mainStartTime1)
                    && (allocationEndInjectionTimeMap.get(entry2.getKey()) <= mainEndTime1)) {
                newOverlapPenaltyValue = allocationEndInjectionTimeMap.get(entry2.getKey())
                        - allocationStartInjectionTime.get(entry2.getKey());
            } else if ((allocationStartInjectionTime.get(entry2.getKey()) <= mainStartTime1)
                    && (allocationEndInjectionTimeMap.get(entry2.getKey()) > mainStartTime1)) {
                newOverlapPenaltyValue = allocationEndInjectionTimeMap.get(entry2.getKey()) - mainStartTime1;
            } else if ((allocationStartInjectionTime.get(entry2.getKey()) < mainEndTime1)
                    && (allocationEndInjectionTimeMap.get(entry2.getKey()) >= mainEndTime1)) {
                newOverlapPenaltyValue = mainEndTime1 - allocationStartInjectionTime.get(entry2.getKey());
            }

            // Check for 4 overlap conditions for Delivery
            if ((allocationStartDeliveryTimeMap.get(entry2.getKey()) <= mainStartTime2)
                    && (allocationEndDeliveryTimeMap.get(entry2.getKey()) >= mainEndTime2)) {
                newOverlapPenaltyValue += mainEndTime2 - mainStartTime2;
            } else if ((allocationStartDeliveryTimeMap.get(entry2.getKey()) >= mainStartTime2)
                    && (allocationEndDeliveryTimeMap.get(entry2.getKey()) <= mainEndTime2)) {
                newOverlapPenaltyValue += allocationEndDeliveryTimeMap.get(entry2.getKey())
                        - allocationStartDeliveryTimeMap.get(entry2.getKey());
            } else if ((allocationStartDeliveryTimeMap.get(entry2.getKey()) <= mainStartTime2)
                    && (allocationEndDeliveryTimeMap.get(entry2.getKey()) > mainStartTime2)) {
                newOverlapPenaltyValue += allocationEndDeliveryTimeMap.get(entry2.getKey()) - mainStartTime2;
            } else if ((allocationStartDeliveryTimeMap.get(entry2.getKey()) < mainEndTime2)
                    && (allocationEndDeliveryTimeMap.get(entry2.getKey()) >= mainEndTime2)) {
                newOverlapPenaltyValue += mainEndTime2 - allocationStartDeliveryTimeMap.get(entry2.getKey());
            }

            // Check for overlap scenario where inner segment Injection start time is more
            // than outer segment Injection start time and inner segment delivery end time
            // is less than outer segment Delivery end time and
            if ((allocationStartInjectionTime.get(entry2.getKey()) >= mainStartTime1)
                    && (allocationEndDeliveryTimeMap.get(entry2.getKey()) <= mainEndTime2)) {
                newOverlapPenaltyValue += mainEndTime2 - allocationEndDeliveryTimeMap.get(entry2.getKey());
            }

            // Check for overlap scenario where inner segment Injection start time is less
            // than outer segment Injection start time and inner segment delivery end time
            // is more than outer segment Delivery end time and
            if ((allocationStartInjectionTime.get(entry2.getKey()) <= mainStartTime1)
                    && (allocationEndDeliveryTimeMap.get(entry2.getKey()) >= mainEndTime2)) {
                newOverlapPenaltyValue += allocationEndDeliveryTimeMap.get(entry2.getKey()) - mainEndTime2;
            }

            // If overlap exists then add overlap time in the segmentOverlapMap hashmap
            // Notice the multiplication factor of 2 because if A overlaps B, then B also
            // overlaps A
            if (newOverlapPenaltyValue > 0L) {
                hard2Score -= (2 * newOverlapPenaltyValue);
                segmentOverlapMap.put(generateCompositeKey(segmentId.toString(), entry2.getKey().toString()),
                        newOverlapPenaltyValue);
                segmentOverlapMap.put(generateCompositeKey(entry2.getKey().toString(), segmentId.toString()),
                        newOverlapPenaltyValue);
            }
        }
    }

    // Compute Overlap and softScore0. Other Scores (i.e. hardScore0, hardScore1 and
    // softScore1 are not computed)
    private void insertPredecessorDate(Allocation allocation) {

        // If RoutePath is not set for the Input Parameter Batch then update Map values
        // and return
        if (batchRoutePathMap.get(allocation.getBatch().getId()) == null) {

            allocationDelayMap.put(allocation.getSegment().getId(), allocation.getDelay());
            allocationStartInjectionTime.put(allocation.getSegment().getId(), allocation.getStartInjectionTime());
            allocationEndInjectionTimeMap.put(allocation.getSegment().getId(), allocation.getEndInjectionTime());
            allocationStartDeliveryTimeMap.put(allocation.getSegment().getId(), allocation.getStartDeliveryTime());
            allocationEndDeliveryTimeMap.put(allocation.getSegment().getId(), allocation.getEndDeliveryTime());
            return;
        }

        boolean computeOverlap = false;

        // Determine if overlap needs to be computed.
        // Overlap is computed if input segment is part of the selectedRoutePath and
        // delay and Injection start time are not null
        if (allocation.getRoutePath().getPath().equals(batchRoutePathMap.get(allocation.getBatch().getId()))) {
            if ((allocation.getDelay() != null) && (allocationDelayMap.get(allocation.getSegment().getId()) == null)) {
                if (allocation.getStartInjectionTime() != null) {
                    computeOverlap = true;
                }
            }
        }

        // Start of compute overlap
        if (computeOverlap) {
            computeOverlap(allocation.getBatch().getId(), allocation.getSegment().getId(),
                    allocation.getSegment().getName(), allocation.getStartInjectionTime(),
                    allocation.getEndInjectionTime(), allocation.getStartDeliveryTime(),
                    allocation.getEndDeliveryTime());
        }
        // End of compute overlap

        allocationDelayMap.put(allocation.getSegment().getId(), allocation.getDelay());
        allocationStartInjectionTime.put(allocation.getSegment().getId(), allocation.getStartInjectionTime());
        allocationEndInjectionTimeMap.put(allocation.getSegment().getId(), allocation.getEndInjectionTime());
        allocationStartDeliveryTimeMap.put(allocation.getSegment().getId(), allocation.getStartDeliveryTime());
        allocationEndDeliveryTimeMap.put(allocation.getSegment().getId(), allocation.getEndDeliveryTime());
        updateBatchEndDate(allocation);

        soft0Score = -getMaxEndTime();
    }

    // Compute Penalties (i.e. hardScore0 and hardScore1), Overlaps (i.e.
    // hardScore2), softScore0 and softScore1
    private void retract(Allocation allocation) {

        printScore("Retract Start Allocation");

        for (Map.Entry<Long, Segment> entry2 : segmentMap.entrySet()) {

            // Continue if Segment Batch is not same as the Input Parameter Batch
            if (entry2.getValue().getBatch().getId() == allocation.getBatch().getId()) {
                continue;
            }

            // Continue if Segment Name is not same as the Input Parameter Segment Name
            // Note that comparison is made using Segment Name and not Segment Id as same
            // Segment Name may be part of different RoutePath (hence different Segment Id)
            if (!(entry2.getValue().getName().equals(allocation.getSegment().getName()))) {
                continue;
            }

            // Check if overlap exists in the map. If no overlap exists then continue.
            if ((segmentOverlapMap.get(generateCompositeKey(allocation.getSegment().getId().toString(),
                    entry2.getKey().toString())) == null)) {
                continue;
            }

            // If overlap exists then remove the overlap score from hard2score. Also remove
            // the overlap from the Map.
            // Notice the multiplication factor of 2 because if A overlaps B, then B also
            // overlaps A
            hard2Score += (2 * segmentOverlapMap
                    .get(generateCompositeKey(allocation.getSegment().getId().toString(), entry2.getKey().toString())));
            segmentOverlapMap.remove(
                    generateCompositeKey(allocation.getSegment().getId().toString(), entry2.getKey().toString()));
            segmentOverlapMap.remove(
                    generateCompositeKey(entry2.getKey().toString(), allocation.getSegment().getId().toString()));
        }

        if (batchRoutePathMap.get(allocation.getBatch().getId()) == null) {
            allocationDelayMap.remove(allocation.getSegment().getId());
            allocationStartInjectionTime.remove(allocation.getSegment().getId());
            allocationEndInjectionTimeMap.remove(allocation.getSegment().getId());
            allocationStartDeliveryTimeMap.remove(allocation.getSegment().getId());
            allocationEndDeliveryTimeMap.remove(allocation.getSegment().getId());
            printScore("Retract End Allocation");
            return;
        }

        Long oldCurrentPenaltyValue = batchCurrentPenaltyValueMap.get(allocation.getBatch().getId());
        Long newCurrentPenaltyValue = oldCurrentPenaltyValue;
        Long oldOtherPenaltyValue = batchOtherPenaltyValueMap.get(allocation.getBatch().getId());
        Long newOtherPenaltyValue = oldOtherPenaltyValue;

        if (allocation.getRoutePath().getPath().equals(batchRoutePathMap.get(allocation.getBatch().getId()))) {
            if ((allocation.getDelay() != null) && (allocationDelayMap.get(allocation.getSegment().getId()) != null)) {
                newCurrentPenaltyValue = newCurrentPenaltyValue + 1;
            }
        } else {
            if ((allocation.getDelay() != null) && (allocationDelayMap.get(allocation.getSegment().getId()) != null)) {
                newOtherPenaltyValue = newOtherPenaltyValue - 1;
            }
        }

        // Apply new penalty if any segment in nonSelectedRoutePath has delay assigned
        // Applicable if no NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldOtherPenaltyValue == 0) && (newOtherPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Remove existing penalty if any segment in nonSelectedRoutePath has delay
        // assigned
        // Applicable only if NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldOtherPenaltyValue > 0) && (newOtherPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Apply penalty if any segment in selectedRoutePath has delay not assigned
        // Applicable if no SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldCurrentPenaltyValue == 0) && (newCurrentPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        // Remove penalty if all segments in selectedRoutePath have delay assigned
        // Applicable only if SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the
        // batch)
        if ((oldCurrentPenaltyValue > 0) && (newCurrentPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        batchOtherPenaltyValueMap.put(allocation.getBatch().getId(), newOtherPenaltyValue);
        batchCurrentPenaltyValueMap.put(allocation.getBatch().getId(), newCurrentPenaltyValue);

        allocationDelayMap.remove(allocation.getSegment().getId());
        allocationStartInjectionTime.remove(allocation.getSegment().getId());
        allocationEndInjectionTimeMap.remove(allocation.getSegment().getId());
        allocationStartDeliveryTimeMap.remove(allocation.getSegment().getId());
        allocationEndDeliveryTimeMap.remove(allocation.getSegment().getId());
        hard1Score = hard1Score - newOtherPenaltyValue - newCurrentPenaltyValue + oldOtherPenaltyValue
                + oldCurrentPenaltyValue;

        printScore("Retract End Allocation");

    }

    // Compute Overlap and softScore0. Other Scores (i.e. hardScore0, hardScore1 and
    // softScore1 are not computed)
    private void retractPredecessorDate(Allocation allocation) {
        for (Map.Entry<Long, Segment> entry2 : segmentMap.entrySet()) {

            // Continue if Segment Batch is not same as the Input Parameter Batch
            if (entry2.getValue().getBatch().getId() == allocation.getBatch().getId()) {
                continue;
            }

            // Continue if Segment Name is not same as the Input Parameter Segment Name
            // Note that comparison is made using Segment Name and not Segment Id as same
            // Segment Name may be part of different RoutePath (hence different Segment Id)
            if (!(entry2.getValue().getName().equals(allocation.getSegment().getName()))) {
                continue;
            }

            // Check if overlap exists in the map. If no overlap exists then continue.
            if (segmentOverlapMap.get(generateCompositeKey(allocation.getSegment().getId().toString(),
                    entry2.getKey().toString())) == null) {
                continue;
            }

            // If overlap exists then remove the overlap score from hard2score. Also remove
            // the overlap from the Map.
            // Notice the multiplication factor of 2 because if A overlaps B, then B also
            // overlaps A
            hard2Score += (2 * segmentOverlapMap
                    .get(generateCompositeKey(allocation.getSegment().getId().toString(), entry2.getKey().toString())));
            segmentOverlapMap.remove(
                    generateCompositeKey(allocation.getSegment().getId().toString(), entry2.getKey().toString()));
            segmentOverlapMap.remove(
                    generateCompositeKey(entry2.getKey().toString(), allocation.getSegment().getId().toString()));

        }

        allocationDelayMap.remove(allocation.getSegment().getId());
        allocationStartInjectionTime.remove(allocation.getSegment().getId());
        allocationEndInjectionTimeMap.remove(allocation.getSegment().getId());
        allocationStartDeliveryTimeMap.remove(allocation.getSegment().getId());
        allocationEndDeliveryTimeMap.remove(allocation.getSegment().getId());

    }

    // This method updates MaxTime (i.e. Delivery End Time) for the Batch.
    public void updateBatchEndDate(AllocationPath allocationPath) {
        Long maxEndTime = 0L;

        for (Map.Entry<Long, Segment> entry2 : segmentMap.entrySet()) {

            // Continue if Segment Delay is null
            if (allocationEndDeliveryTimeMap.get(entry2.getKey()) == null) {
                continue;
            }

            // Continue if RoutePath has not been selected for the Segment Batch
            if (batchRoutePathMap.get(entry2.getValue().getBatch().getId()) == null) {
                continue;
            }

            // Continue if Segment Batch is not same as the Input Parameter Batch
            if (entry2.getValue().getBatch().getId() != allocationPath.getBatch().getId()) {
                continue;
            }

            // Continue if Segment is not part of the selectedRoutepath
            if (!(entry2.getValue().getRoutePath().getPath()
                    .equals(batchRoutePathMap.get(entry2.getValue().getBatch().getId())))) {
                continue;
            }

            // Check if Max End time is the maximum. If not then, no need to update.
            if ((Long) (allocationEndDeliveryTimeMap.get(entry2.getKey())) <= maxEndTime) {
                continue;
            }

            maxEndTime = (Long) (allocationEndDeliveryTimeMap.get(entry2.getKey()));

        }

        batchEndTimeMap.put(allocationPath.getBatch().getId(), maxEndTime);
    }

    // This method updates MaxTime (i.e. Delivery End Time) for the Batch.
    public void updateBatchEndDate(Allocation allocation) {

        Long maxEndTime = 0L;

        for (Map.Entry<Long, Segment> entry2 : segmentMap.entrySet()) {

            // Continue if Segment Delay is null
            if (allocationEndDeliveryTimeMap.get(entry2.getKey()) == null) {
                continue;
            }

            // Continue if RoutePath has not been selected for the Segment Batch
            if (batchRoutePathMap.get(entry2.getValue().getBatch().getId()) == null) {
                continue;
            }

            // Continue if Segment Batch is not same as the Input Parameter Batch
            if (entry2.getValue().getBatch().getId() != allocation.getBatch().getId()) {
                continue;
            }

            // Continue if Segment is not part of the selectedRoutepath
            if (!(entry2.getValue().getRoutePath().getPath()
                    .equals(batchRoutePathMap.get(entry2.getValue().getBatch().getId())))) {
                continue;
            }

            // Check if Max End time is the maximum. If not then, no need to update.
            if ((Long) (allocationEndDeliveryTimeMap.get(entry2.getKey())) <= maxEndTime) {
                continue;
            }

            maxEndTime = (Long) (allocationEndDeliveryTimeMap.get(entry2.getKey()));

        }

        batchEndTimeMap.put(allocation.getBatch().getId(), maxEndTime);
    }

    // Computes Maximum Delivery End Date across all the batches
    public long getMaxEndTime() {
        long maxEndTime = 0L;

        for (Map.Entry<Long, Long> entry : batchEndTimeMap.entrySet()) {

            if (entry.getValue() == null) {
                continue;
            }

            if ((Long) (entry.getValue()) <= maxEndTime) {
                continue;
            }

            maxEndTime = (Long) entry.getValue();
        }

        return maxEndTime;
    }

    // Method to compute softScore2
    // Return value of 0 indicates that all segments have been utilized
    private long computeRoutePathSegmentOverlap() {
        Map<String, Boolean> segmentMap1 = new HashMap<String, Boolean>();

        for (Map.Entry<Long, String> allocationPath : batchRoutePathMap.entrySet()) {

            if (allocationPath.getValue() == null) {
                continue;
            }

            for (String s : RoutePath.getSegmentArray(allocationPath.getValue())) {
                segmentMap1.put(s, true);
            }
        }

        return segmentStringMap.size() - segmentMap1.size();
    }

    public BendableLongScore calculateScore() {
        return BendableLongScore.of(new long[] { hard0Score, hard1Score, hard2Score },
                new long[] { soft0Score, soft1Score });
    }
}