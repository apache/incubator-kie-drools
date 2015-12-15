/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.dinnerparty.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.dinnerparty.domain.DinnerParty;
import org.optaplanner.examples.dinnerparty.domain.Gender;
import org.optaplanner.examples.dinnerparty.domain.Guest;
import org.optaplanner.examples.dinnerparty.domain.Hobby;
import org.optaplanner.examples.dinnerparty.domain.HobbyPractician;
import org.optaplanner.examples.dinnerparty.domain.Job;
import org.optaplanner.examples.dinnerparty.domain.JobType;
import org.optaplanner.examples.dinnerparty.domain.Seat;
import org.optaplanner.examples.dinnerparty.domain.SeatDesignation;
import org.optaplanner.examples.dinnerparty.domain.Table;

public class DinnerPartyImporter extends AbstractTxtSolutionImporter {

    public static void main(String[] args) {
        new DinnerPartyImporter().convertAll();
    }

    public DinnerPartyImporter() {
        super(new DinnerPartyDao());
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new DinnerPartyInputBuilder();
    }

    public static class DinnerPartyInputBuilder extends TxtInputBuilder {

        public Solution readSolution() throws IOException {
            DinnerParty dinnerParty = new DinnerParty();
            dinnerParty.setId(0L);

            readTableListAndSeatList(dinnerParty);
            readJobListGuestListAndHobbyPracticianList(dinnerParty);
            createSeatDesignationList(dinnerParty);

            BigInteger possibleSolutionSize = BigInteger.valueOf(dinnerParty.getGuestList().size()).pow(
                    dinnerParty.getSeatDesignationList().size());
            logger.info("DinnerParty {} has {} jobs, {} guests, {} hobby practicians, {} tables and {} seats"
                    + " with a search space of {}.",
                    getInputId(),
                    dinnerParty.getJobList().size(),
                    dinnerParty.getGuestList().size(),
                    dinnerParty.getHobbyPracticianList().size(),
                    dinnerParty.getTableList().size(),
                    dinnerParty.getSeatList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return dinnerParty;
        }

        private void readTableListAndSeatList(DinnerParty dinnerParty)
                throws IOException {
            int tableListSize = readIntegerValue("Tables:");
            int seatsPerTable = readIntegerValue("SeatsPerTable:");
            List<Table> tableList = new ArrayList<Table>(tableListSize);
            List<Seat> seatList = new ArrayList<Seat>(tableListSize * seatsPerTable);
            for (int i = 0; i < tableListSize; i++) {
                Table table = new Table();
                table.setId((long) i);
                table.setTableIndex(i);
                List<Seat> tableSeatList = new ArrayList<Seat>(seatsPerTable);
                Seat firstSeat = null;
                Seat previousSeat = null;
                for (int j = 0; j < seatsPerTable; j++) {
                    Seat seat = new Seat();
                    seat.setId((long) ((i * seatsPerTable) + j));
                    seat.setTable(table);
                    seat.setSeatIndexInTable(j);
                    if (previousSeat != null) {
                        seat.setLeftSeat(previousSeat);
                        previousSeat.setRightSeat(seat);
                    } else {
                        firstSeat = seat;
                    }
                    tableSeatList.add(seat);
                    seatList.add(seat);
                    previousSeat = seat;
                }
                firstSeat.setLeftSeat(previousSeat);
                previousSeat.setRightSeat(firstSeat);
                table.setSeatList(tableSeatList);
                tableList.add(table);
            }
            dinnerParty.setTableList(tableList);
            dinnerParty.setSeatList(seatList);
        }

        private void readJobListGuestListAndHobbyPracticianList(DinnerParty dinnerParty)
                throws IOException {
            readConstantLine("Code,Name,JobType,Job,Gender,Hobby1,Hobby2,Hobby3");
            readConstantLine("\\-+");
            int guestSize = dinnerParty.getSeatList().size();

            List<Guest> guestList = new ArrayList<Guest>(guestSize);
            List<HobbyPractician> hobbyPracticianList = new ArrayList<HobbyPractician>(guestSize * 3);
            Map<String, Job> jobMap = new HashMap<String, Job>(JobType.values().length * 5);
            int jobNextId = 0;
            int hobbyPracticianJobId = 0;
            for (int i = 0; i < guestSize; i++) {
                Guest guest = new Guest();
                guest.setId((long) i);
                String[] lineTokens = splitByCommaAndTrim(bufferedReader.readLine(), 6, null);
                guest.setCode(lineTokens[0]);
                guest.setName(lineTokens[1]);
                JobType jobType = JobType.valueOfCode(lineTokens[2]);
                String jobName = lineTokens[3];
                String jobMapKey = jobType + "/" + jobName;
                Job job = jobMap.get(jobMapKey);
                if (job == null) {
                    job = new Job();
                    job.setId((long) jobNextId);
                    jobNextId++;
                    job.setJobType(jobType);
                    job.setName(jobName);
                    jobMap.put(jobMapKey, job);
                }
                guest.setJob(job);
                guest.setGender(Gender.valueOfCode(lineTokens[4]));
                List<HobbyPractician> hobbyPracticianOfGuestList = new ArrayList<HobbyPractician>(lineTokens.length - 5);
                for (int j = 5; j < lineTokens.length; j++) {
                    HobbyPractician hobbyPractician = new HobbyPractician();
                    hobbyPractician.setId((long) hobbyPracticianJobId);
                    hobbyPracticianJobId++;
                    hobbyPractician.setGuest(guest);
                    hobbyPractician.setHobby(Hobby.valueOfCode(lineTokens[j]));
                    hobbyPracticianOfGuestList.add(hobbyPractician);
                    hobbyPracticianList.add(hobbyPractician);
                }
                guest.setHobbyPracticianList(hobbyPracticianOfGuestList);
                guestList.add(guest);
            }
            dinnerParty.setJobList(new ArrayList<Job>(jobMap.values()));
            dinnerParty.setGuestList(guestList);
            dinnerParty.setHobbyPracticianList(hobbyPracticianList);
        }

        private void createSeatDesignationList(DinnerParty dinnerParty) {
            List<Guest> guestList = dinnerParty.getGuestList();
            List<SeatDesignation> seatDesignationList = new ArrayList<SeatDesignation>(guestList.size());
            long id = 0L;
            for (Guest guest : guestList) {
                SeatDesignation seatDesignation = new SeatDesignation();
                seatDesignation.setId(id);
                id++;
                seatDesignation.setGuest(guest);
                // Notice that we leave the PlanningVariable properties on null
                seatDesignationList.add(seatDesignation);
            }
            dinnerParty.setSeatDesignationList(seatDesignationList);
        }

    }

}
