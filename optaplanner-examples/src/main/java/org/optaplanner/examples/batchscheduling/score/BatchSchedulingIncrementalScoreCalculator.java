package org.optaplanner.examples.batchscheduling.score;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.examples.batchscheduling.app.BatchSchedulingApp;
import org.optaplanner.examples.batchscheduling.domain.Allocation;
import org.optaplanner.examples.batchscheduling.domain.AllocationPath;
import org.optaplanner.examples.batchscheduling.domain.Batch;
import org.optaplanner.examples.batchscheduling.domain.BatchSchedule;
import org.optaplanner.examples.batchscheduling.domain.RoutePath;
import org.optaplanner.examples.batchscheduling.domain.Segment;

public class BatchSchedulingIncrementalScoreCalculator
        implements IncrementalScoreCalculator<BatchSchedule, BendableLongScore> {

    // hard0Score is sum of penalties for all the Batches. It is computed at Batch level.
    // There are 2 types of Penalties.
    //
    // a) SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY:
    // a1) Applicable if RoutePath is selected for a Batch but delay is not set for one or more segments present in the
    // RoutePath OR
    // a2) if RoutePath is not set for a Batch.
    //
    // b) NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY:
    // Set if RoutePath is not the selected RoutePath but delay is set for a segment in the RoutePath.
    private long hard0Score = 0;

    // hard1Score is computed at segment level (i.e. not at Batch level).
    // It helps arriving at result faster because it operates at segment level.
    //
    // a) For scenarios where RoutePath is selected, it is computed by adding following 2 segment counts:
    // a1) Count of segments which are part of RoutePath but delay is not set and
    // a2) Count of segments which are not part of RoutePath but delay is set
    //
    // b) For scenarios where RoutePath is not selected for a Batch, it is the count of all segments across all the
    // RoutePaths for the given Batch.
    private long hard1Score = 0;

    // hard2score computes overlap (i.e. time overlap) across batches
    // (i.e. No two batches should be present in the same segment at the same time).
    private long hard2Score = 0;

    // soft0Score is the time taken to inject first Batch till delivery of last batch.
    private long soft0Score = 0;

    // Count of segments through which commodity has not traversed.
    // Ideal value is 0 (i.e. All segments have been utilized).
    // This soft score helps all routePaths to be utilized.
    private long soft1Score = 0;

    // Map to store BatchId and selectedRoutePath for that Batch.
    private Map<Long, String> batchRoutePathMap;

    // Map to store BatchId and count of segments that are part of selectedRoutePath but don't have delay assigned.
    private Map<Long, Long> batchCurrentPenaltyValueMap;

    // Map to store BatchId and count of segments that are not part of selectedRoutePath but have delay assigned.
    private Map<Long, Long> batchOtherPenaltyValueMap;

    // Map to store Maximum EndTime for every batch.
    private Map<Long, Long> batchEndTimeMap;

    // Stores mapping between segmentId and Delay value.
    private Map<Long, Long> allocationDelayMap;

    // For calculating hardscore2.
    // This map will always contain even number of entries (i.e. If Segment A overlaps B, then B also overlaps A).
    private Map<String, Long> segmentOverlapMap;

    // Following 4 Maps contain mapping between SegmentId and different timings.
    // Used for determining overlap values.
    private Map<Long, Long> allocationStartInjectionTimeMap;
    private Map<Long, Long> allocationEndInjectionTimeMap;
    private Map<Long, Long> allocationStartDeliveryTimeMap;
    private Map<Long, Long> allocationEndDeliveryTimeMap;

    private Map<Long, Segment> segmentMap;

    // List to store unique segmentString (not segmentId). Boolean field is not used.
    private Map<String, Boolean> segmentStringMap;

    private static String generateCompositeKey(String key1, String key2) {
        return key1 + "#" + key2;
    }

    @Override
    public void resetWorkingSolution(BatchSchedule schedule) {
        batchRoutePathMap = new HashMap<>();
        segmentMap = new HashMap<>();
        segmentStringMap = new HashMap<>();
        batchOtherPenaltyValueMap = new HashMap<>();
        batchCurrentPenaltyValueMap = new HashMap<>();
        batchEndTimeMap = new HashMap<>();
        segmentOverlapMap = new HashMap<>();
        allocationDelayMap = new HashMap<>();
        allocationStartInjectionTimeMap = new HashMap<>();
        allocationEndInjectionTimeMap = new HashMap<>();
        allocationStartDeliveryTimeMap = new HashMap<>();
        allocationEndDeliveryTimeMap = new HashMap<>();

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

    // Compute Penalties (i.e. hardScore0 and hardScore1), Overlaps (i.e.
    // hardScore2), softScore0 and softScore1
    private void insert(AllocationPath allocationPath) {
        Long allocationPathBatchId = allocationPath.getBatch().getId();

        // Get existing current and other penalty values for the batch
        Long oldOtherPenaltyValue = batchOtherPenaltyValueMap.get(allocationPathBatchId);
        Long oldCurrentPenaltyValue = batchCurrentPenaltyValueMap.get(allocationPathBatchId);

        long newOtherPenaltyValue = 0L;
        long newCurrentPenaltyValue = 0L;

        // Start of compute overlap
        for (Map.Entry<Long, Segment> segmentEntry : segmentMap.entrySet()) {
            Long allocationSegmentId = segmentEntry.getKey();
            Segment mainSegment = segmentEntry.getValue();

            // Continue if Segment Batch is not same as the Input Parameter Batch.
            if (!Objects.equals(mainSegment.getBatch().getId(), allocationPathBatchId)) {
                continue;
            }

            // Continue if RoutePath is set to null.
            if (allocationPath.getRoutePath() == null) {
                newCurrentPenaltyValue += 1;
                continue;
            }

            // Continue if Segment RoutePath is different from the Input Parameter RoutePath.
            Long allocationDelay = allocationDelayMap.get(allocationSegmentId);
            if (!Objects.equals(mainSegment.getRoutePath().getId(), allocationPath.getRoutePath().getId())) {
                // Same Batch Different RoutePath from the Preferred RoutePath.
                if (allocationDelay != null) {
                    newOtherPenaltyValue += 1;
                }
                continue;
            }

            if (allocationDelay == null) {
                newCurrentPenaltyValue += 1;
                continue;
            }

            computeOverlap(allocationPathBatchId, allocationSegmentId, segmentEntry.getValue().getName(),
                    allocationStartInjectionTimeMap.get(allocationSegmentId),
                    allocationEndInjectionTimeMap.get(allocationSegmentId),
                    allocationStartDeliveryTimeMap.get(allocationSegmentId),
                    allocationEndDeliveryTimeMap.get(allocationSegmentId));
        }

        // Apply new penalty if any segment in nonSelectedRoutePath has delay assigned.
        // Applicable if no NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the batch).
        if ((oldOtherPenaltyValue == 0) && (newOtherPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Remove existing penalty if any segment in nonSelectedRoutePath has delay assigned.
        // Applicable only if NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the batch).
        if ((oldOtherPenaltyValue > 0) && (newOtherPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Apply penalty if any segment in selectedRoutePath has delay not assigned.
        // Applicable if no SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the batch).
        if ((oldCurrentPenaltyValue == 0) && (newCurrentPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        // Remove penalty if all segments in selectedRoutePath have delay assigned.
        // Applicable only if SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the batch).
        if ((oldCurrentPenaltyValue > 0) && (newCurrentPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        if (allocationPath.getRoutePath() != null) {
            batchRoutePathMap.put(allocationPathBatchId, allocationPath.getRoutePath().getPath());
        } else {
            batchRoutePathMap.remove(allocationPathBatchId);
        }

        updateBatchEndDate(allocationPath);

        // Compute hardscore1.
        batchOtherPenaltyValueMap.put(allocationPathBatchId, newOtherPenaltyValue);
        batchCurrentPenaltyValueMap.put(allocationPathBatchId, newCurrentPenaltyValue);
        hard1Score = hard1Score - newOtherPenaltyValue - newCurrentPenaltyValue + oldOtherPenaltyValue
                + oldCurrentPenaltyValue;

        soft0Score = -getMaxEndTime();
        soft1Score = -computeRoutePathSegmentOverlap();
    }

    // Compute Penalties (i.e. hardScore0 and hardScore1), Overlaps (i.e. hardScore2), softScore0 and softScore1.
    private void retract(AllocationPath allocationPath) {
        Long allocationBatchId = allocationPath.getBatch().getId();

        // Get existing current and other penalty values for the batch
        Long oldOtherPenaltyValue = batchOtherPenaltyValueMap.get(allocationBatchId);
        Long oldCurrentPenaltyValue = batchCurrentPenaltyValueMap.get(allocationBatchId);
        long newOtherPenaltyValue = 0L;
        long newCurrentPenaltyValue = 0L;

        // Start of compute overlap.
        for (Map.Entry<Long, Segment> segmentEntry : segmentMap.entrySet()) {
            // Continue if Segment Batch is not same as the Input Parameter Batch.
            if (!Objects.equals(segmentEntry.getValue().getBatch().getId(), allocationBatchId)) {
                continue;
            }

            newCurrentPenaltyValue = newCurrentPenaltyValue + 1;
            for (Map.Entry<Long, Segment> segmentEntry2 : segmentMap.entrySet()) {
                // Continue if Segment Batch is not same as the Input Parameter Batch.
                if (Objects.equals(segmentEntry2.getValue().getBatch().getId(), allocationBatchId)) {
                    continue;
                }

                // Continue if Inner Segment Name is same as the Outer Segment Name.
                if (!(segmentEntry2.getValue().getName().equals(segmentEntry.getValue().getName()))) {
                    continue;
                }

                // Check if overlap exists in the map. If no overlap exists then continue.
                String segmentId1 = segmentEntry.getKey().toString();
                String segmentId2 = segmentEntry2.getKey().toString();
                String compositeKey = generateCompositeKey(segmentId1, segmentId2);
                Long segmentOverlap = segmentOverlapMap.get(compositeKey);
                if (segmentOverlap == null) {
                    continue;
                }

                // If overlap exists then remove the overlap score from hard2score.
                // Also remove the overlap from the Map.
                // Notice the multiplication factor of 2 because if A overlaps B, then B also overlaps A.
                hard2Score += 2 * segmentOverlap;
                segmentOverlapMap.remove(compositeKey);
                segmentOverlapMap.remove(generateCompositeKey(segmentId2, segmentId1));
            }
        }
        // End of compute overlap.

        // Remove existing penalty if any segment in nonSelectedRoutePath has delay assigned.
        // Applicable only if NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the batch).
        if ((oldOtherPenaltyValue > 0)) {
            hard0Score += BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Apply penalty if any segment in selectedRoutePath has delay not assigned.
        // Applicable if no SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the batch).
        if ((oldCurrentPenaltyValue == 0) && (newCurrentPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        // Remove penalty if all segments in selectedRoutePath have delay assigned.
        // Applicable only if SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the batch).
        if ((oldCurrentPenaltyValue > 0) && (newCurrentPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        batchRoutePathMap.remove(allocationBatchId);
        batchOtherPenaltyValueMap.put(allocationBatchId, newOtherPenaltyValue);
        batchCurrentPenaltyValueMap.put(allocationBatchId, newCurrentPenaltyValue);
        hard1Score = hard1Score - newOtherPenaltyValue - newCurrentPenaltyValue + oldOtherPenaltyValue
                + oldCurrentPenaltyValue;
    }

    // Compute Penalties (i.e. hardScore0 and hardScore1), Overlaps (i.e. hardScore2), softScore0 and softScore1.
    private void insert(Allocation allocation) {
        Long allocationBatchId = allocation.getBatch().getId();
        Long allocationSegmentId = allocation.getSegment().getId();

        // If RoutePath is not set for the Input Parameter Batch then update Map values and return.
        if (batchRoutePathMap.get(allocationBatchId) == null) {
            allocationDelayMap.put(allocationSegmentId, allocation.getDelay());
            allocationStartInjectionTimeMap.put(allocationSegmentId, allocation.getStartInjectionTime());
            allocationEndInjectionTimeMap.put(allocationSegmentId, allocation.getEndInjectionTime());
            allocationStartDeliveryTimeMap.put(allocationSegmentId, allocation.getStartDeliveryTime());
            allocationEndDeliveryTimeMap.put(allocationSegmentId, allocation.getEndDeliveryTime());
            return;
        }

        Long oldCurrentPenaltyValue = batchCurrentPenaltyValueMap.get(allocationBatchId);
        Long newCurrentPenaltyValue = oldCurrentPenaltyValue;
        Long oldOtherPenaltyValue = batchOtherPenaltyValueMap.get(allocationBatchId);
        Long newOtherPenaltyValue = oldOtherPenaltyValue;

        boolean computeOverLap = false;
        boolean hadDelay = allocation.getDelay() != null;
        Long allocationDelay = allocationDelayMap.get(allocationSegmentId);
        boolean hasDelay = allocationDelay != null;
        if (allocation.getRoutePath().getPath().equals(batchRoutePathMap.get(allocationBatchId))) {
            // If input segment is part of the selectedRoutePath, then compare previous delay value with new delay value.
            // If previous value is not null but new value is null, then add penalty for that segment
            // else if previous value is null but new value is not null, then remove penalty for that segment.
            if (hadDelay && !hasDelay) {
                newCurrentPenaltyValue = newCurrentPenaltyValue - 1;
                // Overlap is computed if input segment is part of the selectedRoutePath and delay and Injection
                // start time are not null.
                if (allocation.getStartInjectionTime() != null) {
                    computeOverLap = true;
                }
            } else if (!hadDelay && hasDelay) {
                newCurrentPenaltyValue = newCurrentPenaltyValue + 1;
            }
        } else {
            // If input segment is not part of the selectedRoutePath, then compare previous delay value with new delay value.
            // If previous value is null but new value is not null, then add penalty for that segment
            // else if previous value is not null but new value is null, then remove penalty for that segment.
            if (hadDelay && !hasDelay) {
                newOtherPenaltyValue = newOtherPenaltyValue + 1;
            } else if (!hadDelay && hasDelay) {
                newOtherPenaltyValue = newOtherPenaltyValue - 1;
            }
        }
        if (computeOverLap) {
            computeOverlap(allocationBatchId, allocationSegmentId,
                    allocation.getSegment().getName(), allocation.getStartInjectionTime(),
                    allocation.getEndInjectionTime(), allocation.getStartDeliveryTime(),
                    allocation.getEndDeliveryTime());
        }

        // Apply new penalty if any segment in nonSelectedRoutePath has delay assigned.
        // Applicable if no NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the batch).
        if ((oldOtherPenaltyValue == 0) && (newOtherPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Remove existing penalty if any segment in nonSelectedRoutePath has delay assigned.
        // Applicable only if NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the batch).
        if ((oldOtherPenaltyValue > 0) && (newOtherPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Apply penalty if any segment in selectedRoutePath has delay not assigned.
        // Applicable if no SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the batch).
        if ((oldCurrentPenaltyValue == 0) && (newCurrentPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        // Remove penalty if all segments in selectedRoutePath have delay assigned.
        // Applicable only if SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the batch).
        if ((oldCurrentPenaltyValue > 0) && (newCurrentPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        allocationDelayMap.put(allocationSegmentId, allocation.getDelay());
        allocationStartInjectionTimeMap.put(allocationSegmentId, allocation.getStartInjectionTime());
        allocationEndInjectionTimeMap.put(allocationSegmentId, allocation.getEndInjectionTime());
        allocationStartDeliveryTimeMap.put(allocationSegmentId, allocation.getStartDeliveryTime());
        allocationEndDeliveryTimeMap.put(allocationSegmentId, allocation.getEndDeliveryTime());

        updateBatchEndDate(allocation);

        batchOtherPenaltyValueMap.put(allocationBatchId, newOtherPenaltyValue);
        batchCurrentPenaltyValueMap.put(allocationBatchId, newCurrentPenaltyValue);
        hard1Score = hard1Score - newOtherPenaltyValue - newCurrentPenaltyValue + oldOtherPenaltyValue
                + oldCurrentPenaltyValue;
        soft0Score = -getMaxEndTime();
    }

    private void computeOverlap(Long batchId, Long segmentId, String segmentName, Long mainStartTime1,
            Long mainEndTime1, Long mainStartTime2, Long mainEndTime2) {
        for (Map.Entry<Long, Segment> segmentEntry : segmentMap.entrySet()) {
            // Continue if Segment Delay is null.
            if (allocationDelayMap.get(segmentEntry.getKey()) == null) {
                continue;
            }

            // Continue if Segment Batch is not same as the Input Parameter Batch.
            Long segmentBatchId = segmentEntry.getValue().getBatch().getId();
            if (Objects.equals(segmentBatchId, batchId)) {
                continue;
            }

            // Continue if RoutePath has not been selected for the Segment Batch.
            if (batchRoutePathMap.get(segmentBatchId) == null) {
                continue;
            }

            // Continue if Segment is not part of the selectedRoutepath.
            if (!segmentEntry.getValue().getRoutePath().getPath().equals(batchRoutePathMap.get(segmentBatchId))) {
                continue;
            }

            // Continue if Segment Name is not same as the Input parameter Segment Name.
            // Note that comparison is made using Segment Name and not Segment Id
            // as same Segment Name may be part of different RoutePath (hence different Segment Id).
            if (!(segmentEntry.getValue().getName().equals(segmentName))) {
                continue;
            }

            Long newOverlapPenaltyValue = 0L;

            // Check for following 4 conditions for Injection:
            // Condition 1) If inner segment injection start time is less than outer segment injection start time
            // and inner segment injection end date is more than outer segment injection end time.
            // Condition 2) If inner segment injection start time is more than outer segment injection start time
            // and inner segment injection end date is less than outer segment injection end time.
            // Condition 3) If inner segment injection start time is less than outer segment injection start time
            // and inner segment injection end date is more than outer segment injection start time.
            // Condition 4) If inner segment injection start time is less than outer segment injection end time
            // and inner segment injection end date is more than outer segment injection end time.
            Long startInjectionTime = allocationStartInjectionTimeMap.get(segmentEntry.getKey());
            Long endInjectionTime = allocationEndInjectionTimeMap.get(segmentEntry.getKey());
            if ((startInjectionTime <= mainStartTime1) && (endInjectionTime >= mainEndTime1)) {
                newOverlapPenaltyValue = mainEndTime1 - mainStartTime1;
            } else if ((startInjectionTime >= mainStartTime1) && (endInjectionTime <= mainEndTime1)) {
                newOverlapPenaltyValue = endInjectionTime - startInjectionTime;
            } else if ((startInjectionTime <= mainStartTime1) && (endInjectionTime > mainStartTime1)) {
                newOverlapPenaltyValue = endInjectionTime - mainStartTime1;
            } else if ((startInjectionTime < mainEndTime1) && (endInjectionTime >= mainEndTime1)) {
                newOverlapPenaltyValue = mainEndTime1 - endInjectionTime;
            }

            // Check for 4 overlap conditions for Delivery.
            Long startDeliveryTime = allocationStartDeliveryTimeMap.get(segmentEntry.getKey());
            Long endDeliveryTime = allocationEndDeliveryTimeMap.get(segmentEntry.getKey());
            if ((startDeliveryTime <= mainStartTime2) && (endDeliveryTime >= mainEndTime2)) {
                newOverlapPenaltyValue += mainEndTime2 - mainStartTime2;
            } else if ((startDeliveryTime >= mainStartTime2) && (endDeliveryTime <= mainEndTime2)) {
                newOverlapPenaltyValue += endDeliveryTime - startDeliveryTime;
            } else if ((startDeliveryTime <= mainStartTime2) && (endDeliveryTime > mainStartTime2)) {
                newOverlapPenaltyValue += endDeliveryTime - mainStartTime2;
            } else if ((startDeliveryTime < mainEndTime2) && (endDeliveryTime >= mainEndTime2)) {
                newOverlapPenaltyValue += mainEndTime2 - endDeliveryTime;
            }

            // Check for overlap scenario where inner segment Injection start time is more than outer segment Injection
            // start time and inner segment delivery end time is less than outer segment Delivery end time and.
            if ((startInjectionTime >= mainStartTime1) && (endDeliveryTime <= mainEndTime2)) {
                newOverlapPenaltyValue += mainEndTime2 - endDeliveryTime;
            }

            // Check for overlap scenario where inner segment Injection start time is less than outer segment Injection
            // start time and inner segment delivery end time is more than outer segment Delivery end time and.
            if ((startInjectionTime <= mainStartTime1) && (endDeliveryTime >= mainEndTime2)) {
                newOverlapPenaltyValue += endDeliveryTime - mainEndTime2;
            }

            // If overlap exists then add overlap time in the segmentOverlapMap hashmap.
            // Notice the multiplication factor of 2 because if A overlaps B, then B also overlaps A.
            if (newOverlapPenaltyValue > 0L) {
                hard2Score -= (2 * newOverlapPenaltyValue);
                segmentOverlapMap.put(generateCompositeKey(segmentId.toString(), segmentEntry.getKey().toString()),
                        newOverlapPenaltyValue);
                segmentOverlapMap.put(generateCompositeKey(segmentEntry.getKey().toString(), segmentId.toString()),
                        newOverlapPenaltyValue);
            }
        }
    }

    // Compute Overlap and softScore0. Other Scores (i.e. hardScore0, hardScore1 and softScore1 are not computed).
    private void insertPredecessorDate(Allocation allocation) {
        Long allocationBatchId = allocation.getBatch().getId();
        Long allocationSegmentId = allocation.getSegment().getId();

        // If RoutePath is not set for the Input Parameter Batch then update Map values and return.
        if (batchRoutePathMap.get(allocationBatchId) == null) {
            allocationDelayMap.put(allocationSegmentId, allocation.getDelay());
            allocationStartInjectionTimeMap.put(allocationSegmentId, allocation.getStartInjectionTime());
            allocationEndInjectionTimeMap.put(allocationSegmentId, allocation.getEndInjectionTime());
            allocationStartDeliveryTimeMap.put(allocationSegmentId, allocation.getStartDeliveryTime());
            allocationEndDeliveryTimeMap.put(allocationSegmentId, allocation.getEndDeliveryTime());
            return;
        }

        // Overlap is computed if input segment is part of the selectedRoutePath and delay and Injection start time
        // are not null.
        boolean computeOverlap = false;
        if (allocation.getRoutePath().getPath().equals(batchRoutePathMap.get(allocationBatchId))) {
            if ((allocation.getDelay() != null) && (allocationDelayMap.get(allocationSegmentId) == null)) {
                if (allocation.getStartInjectionTime() != null) {
                    computeOverlap = true;
                }
            }
        }
        if (computeOverlap) {
            computeOverlap(allocationBatchId, allocationSegmentId,
                    allocation.getSegment().getName(), allocation.getStartInjectionTime(),
                    allocation.getEndInjectionTime(), allocation.getStartDeliveryTime(),
                    allocation.getEndDeliveryTime());
        }

        allocationDelayMap.put(allocationSegmentId, allocation.getDelay());
        allocationStartInjectionTimeMap.put(allocationSegmentId, allocation.getStartInjectionTime());
        allocationEndInjectionTimeMap.put(allocationSegmentId, allocation.getEndInjectionTime());
        allocationStartDeliveryTimeMap.put(allocationSegmentId, allocation.getStartDeliveryTime());
        allocationEndDeliveryTimeMap.put(allocationSegmentId, allocation.getEndDeliveryTime());
        updateBatchEndDate(allocation);

        soft0Score = -getMaxEndTime();
    }

    // Compute Penalties (i.e. hardScore0 and hardScore1), Overlaps (i.e. hardScore2), softScore0 and softScore1.
    private void retract(Allocation allocation) {
        Long allocationBatchId = allocation.getBatch().getId();
        Long allocationSegmentId = allocation.getSegment().getId();
        String allocationSegmentIdString = allocationSegmentId.toString();
        for (Map.Entry<Long, Segment> segmentEntry : segmentMap.entrySet()) {
            // Continue if Segment Batch is not same as the Input Parameter Batch
            if (Objects.equals(segmentEntry.getValue().getBatch().getId(), allocationBatchId)) {
                continue;
            }

            // Continue if Segment Name is not same as the Input Parameter Segment Name.
            // Note that comparison is made using Segment Name and not Segment Id
            // as same Segment Name may be part of different RoutePath (hence different Segment Id).
            if (!segmentEntry.getValue().getName().equals(allocation.getSegment().getName())) {
                continue;
            }

            // Check if overlap exists in the map. If no overlap exists then continue.
            String subkey = segmentEntry.getKey().toString();
            String compositeKey = generateCompositeKey(allocationSegmentIdString, subkey);
            Long segmentOverlap = segmentOverlapMap.get(compositeKey);
            if (segmentOverlap == null) {
                continue;
            }

            // If overlap exists then remove the overlap score from hard2score. Also remove the overlap from the Map.
            // Notice the multiplication factor of 2 because if A overlaps B, then B also overlaps A.
            hard2Score += 2 * segmentOverlap;
            segmentOverlapMap.remove(compositeKey);
            segmentOverlapMap.remove(generateCompositeKey(subkey, allocationSegmentIdString));
        }

        if (batchRoutePathMap.get(allocationBatchId) == null) {
            allocationDelayMap.remove(allocationSegmentId);
            allocationStartInjectionTimeMap.remove(allocationSegmentId);
            allocationEndInjectionTimeMap.remove(allocationSegmentId);
            allocationStartDeliveryTimeMap.remove(allocationSegmentId);
            allocationEndDeliveryTimeMap.remove(allocationSegmentId);
            return;
        }

        Long oldCurrentPenaltyValue = batchCurrentPenaltyValueMap.get(allocationBatchId);
        Long newCurrentPenaltyValue = oldCurrentPenaltyValue;
        Long oldOtherPenaltyValue = batchOtherPenaltyValueMap.get(allocationBatchId);
        Long newOtherPenaltyValue = oldOtherPenaltyValue;

        boolean hadDelay = allocation.getDelay() != null;
        if (allocation.getRoutePath().getPath().equals(batchRoutePathMap.get(allocationBatchId))) {
            if (hadDelay && (allocationDelayMap.get(allocationSegmentId)) != null) {
                newCurrentPenaltyValue = newCurrentPenaltyValue + 1;
            }
        } else {
            if (hadDelay && (allocationDelayMap.get(allocationSegmentId) != null)) {
                newOtherPenaltyValue = newOtherPenaltyValue - 1;
            }
        }

        // Apply new penalty if any segment in nonSelectedRoutePath has delay assigned.
        // Applicable if no NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the batch).
        if ((oldOtherPenaltyValue == 0) && (newOtherPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Remove existing penalty if any segment in nonSelectedRoutePath has delay assigned.
        // Applicable only if NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY exists (for the batch).
        if ((oldOtherPenaltyValue > 0) && (newOtherPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY;
        }

        // Apply penalty if any segment in selectedRoutePath has delay not assigned.
        // Applicable if no SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the batch).
        if ((oldCurrentPenaltyValue == 0) && (newCurrentPenaltyValue > 0)) {
            hard0Score -= BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        // Remove penalty if all segments in selectedRoutePath have delay assigned.
        // Applicable only if SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY exists (for the batch).
        if ((oldCurrentPenaltyValue > 0) && (newCurrentPenaltyValue == 0)) {
            hard0Score += BatchSchedulingApp.SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY;
        }

        batchOtherPenaltyValueMap.put(allocationBatchId, newOtherPenaltyValue);
        batchCurrentPenaltyValueMap.put(allocationBatchId, newCurrentPenaltyValue);

        allocationDelayMap.remove(allocationSegmentId);
        allocationStartInjectionTimeMap.remove(allocationSegmentId);
        allocationEndInjectionTimeMap.remove(allocationSegmentId);
        allocationStartDeliveryTimeMap.remove(allocationSegmentId);
        allocationEndDeliveryTimeMap.remove(allocationSegmentId);
        hard1Score = hard1Score - newOtherPenaltyValue - newCurrentPenaltyValue + oldOtherPenaltyValue
                + oldCurrentPenaltyValue;
    }

    // Compute Overlap and softScore0. Other Scores (i.e. hardScore0, hardScore1 and softScore1 are not computed).
    private void retractPredecessorDate(Allocation allocation) {
        Long allocationSegmentId = allocation.getSegment().getId();
        String allocationSegmentIdString = allocationSegmentId.toString();
        for (Map.Entry<Long, Segment> segmentEntry : segmentMap.entrySet()) {
            // Continue if Segment Batch is not same as the Input Parameter Batch.
            if (Objects.equals(segmentEntry.getValue().getBatch().getId(), allocation.getBatch().getId())) {
                continue;
            }

            // Continue if Segment Name is not same as the Input Parameter Segment Name.
            // Note that comparison is made using Segment Name and not Segment Id
            // as same Segment Name may be part of different RoutePath (hence different Segment Id).
            if (!segmentEntry.getValue().getName().equals(allocation.getSegment().getName())) {
                continue;
            }

            // Check if overlap exists in the map. If no overlap exists then continue.
            String subkey = segmentEntry.getKey().toString();
            String compositeKey = generateCompositeKey(allocationSegmentIdString, subkey);
            Long segmentOverlap = segmentOverlapMap.get(compositeKey);
            if (segmentOverlap == null) {
                continue;
            }

            // If overlap exists then remove the overlap score from hard2score. Also remove the overlap from the Map.
            // Notice the multiplication factor of 2 because if A overlaps B, then B also overlaps A.
            hard2Score += 2 * segmentOverlap;
            segmentOverlapMap.remove(compositeKey);
            segmentOverlapMap.remove(generateCompositeKey(subkey, allocationSegmentIdString));
        }

        allocationDelayMap.remove(allocationSegmentId);
        allocationStartInjectionTimeMap.remove(allocationSegmentId);
        allocationEndInjectionTimeMap.remove(allocationSegmentId);
        allocationStartDeliveryTimeMap.remove(allocationSegmentId);
        allocationEndDeliveryTimeMap.remove(allocationSegmentId);

    }

    // This method updates MaxTime (i.e. Delivery End Time) for the Batch.
    public void updateBatchEndDate(AllocationPath allocationPath) {
        long maxEndTime = 0L;
        Long pathBatchId = allocationPath.getBatch().getId();
        for (Map.Entry<Long, Segment> segmentEntry : segmentMap.entrySet()) {
            Long allocationEndDeliveryTime = allocationEndDeliveryTimeMap.get(segmentEntry.getKey());
            // Continue if Segment Delay is null.
            if (allocationEndDeliveryTime == null) {
                continue;
            }
            Long batchId = segmentEntry.getValue().getBatch().getId();
            String batchRoute = batchRoutePathMap.get(batchId);
            // Continue if RoutePath has not been selected for the Segment Batch.
            if (batchRoute == null) {
                continue;
            }
            // Continue if Segment Batch is not same as the Input Parameter Batch.
            if (!batchId.equals(pathBatchId)) {
                continue;
            }
            // Continue if Segment is not part of the selectedRoutepath.
            if (!(segmentEntry.getValue().getRoutePath().getPath().equals(batchRoute))) {
                continue;
            }
            // Check if Max End time is the maximum. If not then, no need to update.
            if (allocationEndDeliveryTime <= maxEndTime) {
                continue;
            }
            maxEndTime = allocationEndDeliveryTime;
        }
        batchEndTimeMap.put(pathBatchId, maxEndTime);
    }

    // This method updates MaxTime (i.e. Delivery End Time) for the Batch.
    public void updateBatchEndDate(Allocation allocation) {
        long maxEndTime = 0L;
        Long allocationBatchId = allocation.getBatch().getId();
        for (Map.Entry<Long, Segment> segmentEntry : segmentMap.entrySet()) {
            Long allocationEndDeliveryTime = allocationEndDeliveryTimeMap.get(segmentEntry.getKey());
            // Continue if Segment Delay is null.
            if (allocationEndDeliveryTime == null) {
                continue;
            }
            Long batchId = segmentEntry.getValue().getBatch().getId();
            String batchRoute = batchRoutePathMap.get(batchId);
            // Continue if RoutePath has not been selected for the Segment Batch.
            if (batchRoute == null) {
                continue;
            }
            // Continue if Segment Batch is not same as the Input Parameter Batch.
            if (!batchId.equals(allocationBatchId)) {
                continue;
            }
            // Continue if Segment is not part of the selectedRoutepath.
            if (!segmentEntry.getValue().getRoutePath().getPath().equals(batchRoute)) {
                continue;
            }
            // Check if Max End time is the maximum. If not then, no need to update.
            if (allocationEndDeliveryTime <= maxEndTime) {
                continue;
            }
            maxEndTime = allocationEndDeliveryTime;
        }
        batchEndTimeMap.put(allocationBatchId, maxEndTime);
    }

    // Computes Maximum Delivery End Date across all the batches.
    public long getMaxEndTime() {
        long maxEndTime = 0L;
        for (Long endTime : batchEndTimeMap.values()) {
            if (endTime == null) {
                continue;
            }
            if (endTime <= maxEndTime) {
                continue;
            }
            maxEndTime = endTime;
        }
        return maxEndTime;
    }

    // Method to compute softScore2. Return value of 0 indicates that all segments have been utilized.
    private long computeRoutePathSegmentOverlap() {
        Set<String> segmentSet = new HashSet<>();
        for (String segments : batchRoutePathMap.values()) {
            if (segments == null) {
                continue;
            }
            segmentSet.addAll(Arrays.asList(RoutePath.getSegmentArray(segments)));
        }

        return segmentStringMap.size() - segmentSet.size();
    }

    @Override
    public BendableLongScore calculateScore() {
        return BendableLongScore.of(new long[] { hard0Score, hard1Score, hard2Score },
                new long[] { soft0Score, soft1Score });
    }
}
