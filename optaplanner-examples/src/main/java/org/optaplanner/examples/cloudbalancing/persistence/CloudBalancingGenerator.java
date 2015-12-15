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

package org.optaplanner.examples.cloudbalancing.persistence;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;

public class CloudBalancingGenerator extends LoggingMain {

    private static class Price {

        private int hardwareValue;
        private String description;
        private int cost;

        private Price(int hardwareValue, String description, int cost) {
            this.hardwareValue = hardwareValue;
            this.description = description;
            this.cost = cost;
        }

        public int getHardwareValue() {
            return hardwareValue;
        }

        public String getDescription() {
            return description;
        }

        public int getCost() {
            return cost;
        }
    }

    private static final Price[] CPU_POWER_PRICES = { // in gigahertz
            new Price(3, "single core 3ghz", 110),
            new Price(4, "dual core 2ghz", 140),
            new Price(6, "dual core 3ghz", 180),
            new Price(8, "quad core 2ghz", 270),
            new Price(12, "quad core 3ghz", 400),
            new Price(16, "quad core 4ghz", 1000),
            new Price(24, "eight core 3ghz", 3000),
    };
    private static final Price[] MEMORY_PRICES = { // in gigabyte RAM
            new Price(2, "2 gigabyte", 140),
            new Price(4, "4 gigabyte", 180),
            new Price(8, "8 gigabyte", 220),
            new Price(16, "16 gigabyte", 300),
            new Price(32, "32 gigabyte", 400),
            new Price(64, "64 gigabyte", 600),
            new Price(96, "96 gigabyte", 1000),
    };
    private static final Price[] NETWORK_BANDWIDTH_PRICES = { // in gigabyte per hour
            new Price(2, "2 gigabyte", 100),
            new Price(4, "4 gigabyte", 200),
            new Price(6, "6 gigabyte", 300),
            new Price(8, "8 gigabyte", 400),
            new Price(12, "12 gigabyte", 600),
            new Price(16, "16 gigabyte", 800),
            new Price(20, "20 gigabyte", 1000),
    };

    private static final int MAXIMUM_REQUIRED_CPU_POWER = 12; // in gigahertz
    private static final int MAXIMUM_REQUIRED_MEMORY = 32; // in gigabyte RAM
    private static final int MAXIMUM_REQUIRED_NETWORK_BANDWIDTH = 12; // in gigabyte per hour

    public static void main(String[] args) {
        new CloudBalancingGenerator().generate();
    }

    protected final SolutionDao solutionDao;
    protected final File outputDir;
    protected Random random;

    public CloudBalancingGenerator() {
        checkConfiguration();
        solutionDao = new CloudBalancingDao();
        outputDir = new File(solutionDao.getDataDir(), "unsolved");
    }

    public CloudBalancingGenerator(boolean withoutDao) {
        if (!withoutDao) {
            throw new IllegalArgumentException("The parameter withoutDao (" + withoutDao + ") must be true.");
        }
        checkConfiguration();
        solutionDao = null;
        outputDir = null;
    }

    public void generate() {
        writeCloudBalance(2, 6);
        writeCloudBalance(3, 9);
        writeCloudBalance(4, 12);
//        writeCloudBalance(5, 15);
//        writeCloudBalance(6, 18);
//        writeCloudBalance(7, 21);
//        writeCloudBalance(8, 24);
//        writeCloudBalance(9, 27);
//        writeCloudBalance(10, 30);
//        writeCloudBalance(11, 33);
//        writeCloudBalance(12, 36);
//        writeCloudBalance(13, 39);
//        writeCloudBalance(14, 42);
//        writeCloudBalance(15, 45);
//        writeCloudBalance(16, 48);
//        writeCloudBalance(17, 51);
//        writeCloudBalance(18, 54);
//        writeCloudBalance(19, 57);
//        writeCloudBalance(20, 60);
        writeCloudBalance(100, 300);
        writeCloudBalance(200, 600);
        writeCloudBalance(400, 1200);
        writeCloudBalance(800, 2400);
        writeCloudBalance(1600, 4800);
    }

    private void checkConfiguration() {
        if (CPU_POWER_PRICES.length != MEMORY_PRICES.length || CPU_POWER_PRICES.length != NETWORK_BANDWIDTH_PRICES.length) {
            throw new IllegalStateException("All price arrays must be equal in length.");
        }
    }

    private void writeCloudBalance(int computerListSize, int processListSize) {
        String fileName = determineFileName(computerListSize, processListSize);
        File outputFile = new File(outputDir, fileName + ".xml");
        CloudBalance cloudBalance = createCloudBalance(fileName, computerListSize, processListSize);
        solutionDao.writeSolution(cloudBalance, outputFile);
    }

    public CloudBalance createCloudBalance(int computerListSize, int processListSize) {
        return createCloudBalance(determineFileName(computerListSize, processListSize),
                computerListSize, processListSize);
    }

    private String determineFileName(int computerListSize, int processListSize) {
        return computerListSize + "computers-" + processListSize + "processes";
    }

    public CloudBalance createCloudBalance(String inputId, int computerListSize, int processListSize) {
        random = new Random(47);
        CloudBalance cloudBalance = new CloudBalance();
        cloudBalance.setId(0L);
        createComputerList(cloudBalance, computerListSize);
        createProcessList(cloudBalance, processListSize);
        assureComputerCapacityTotalAtLeastProcessRequiredTotal(cloudBalance);
        BigInteger possibleSolutionSize = BigInteger.valueOf(cloudBalance.getComputerList().size()).pow(
                cloudBalance.getProcessList().size());
        logger.info("CloudBalance {} has {} computers and {} processes with a search space of {}.",
                inputId, computerListSize, processListSize,
                AbstractSolutionImporter.getFlooredPossibleSolutionSize(possibleSolutionSize));
        return cloudBalance;
    }

    private void createComputerList(CloudBalance cloudBalance, int computerListSize) {
        List<CloudComputer> computerList = new ArrayList<CloudComputer>(computerListSize);
        for (int i = 0; i < computerListSize; i++) {
            CloudComputer computer = new CloudComputer();
            computer.setId((long) i);
            int cpuPowerPricesIndex = random.nextInt(CPU_POWER_PRICES.length);
            computer.setCpuPower(CPU_POWER_PRICES[cpuPowerPricesIndex].getHardwareValue());
            int memoryPricesIndex = distortIndex(cpuPowerPricesIndex, MEMORY_PRICES.length);
            computer.setMemory(MEMORY_PRICES[memoryPricesIndex].getHardwareValue());
            int networkBandwidthPricesIndex = distortIndex(cpuPowerPricesIndex, NETWORK_BANDWIDTH_PRICES.length);
            computer.setNetworkBandwidth(NETWORK_BANDWIDTH_PRICES[networkBandwidthPricesIndex].getHardwareValue());
            int cost = CPU_POWER_PRICES[cpuPowerPricesIndex].getCost()
                    + MEMORY_PRICES[memoryPricesIndex].getCost()
                    + NETWORK_BANDWIDTH_PRICES[networkBandwidthPricesIndex].getCost();
            computer.setCost(cost);
            logger.trace("Created computer with cpuPowerPricesIndex ({}), memoryPricesIndex ({}),"
                    + " networkBandwidthPricesIndex ({}).",
                    cpuPowerPricesIndex, memoryPricesIndex, networkBandwidthPricesIndex);
            computerList.add(computer);
        }
        cloudBalance.setComputerList(computerList);
    }

    private int distortIndex(int referenceIndex, int length) {
        int index = referenceIndex;
        double randomDouble = random.nextDouble();
        double loweringThreshold = 0.25;
        while (randomDouble < loweringThreshold && index >= 1) {
            index--;
            loweringThreshold *= 0.10;
        }
        double heighteningThreshold = 0.75;
        while (randomDouble >= heighteningThreshold && index <= (length - 2)) {
            index++;
            heighteningThreshold = (1.0 - ((1.0 - heighteningThreshold) * 0.10));
        }
        return index;
    }

    private void createProcessList(CloudBalance cloudBalance, int processListSize) {
        List<CloudProcess> processList = new ArrayList<CloudProcess>(processListSize);
        for (int i = 0; i < processListSize; i++) {
            CloudProcess process = new CloudProcess();
            process.setId((long) i);
            int requiredCpuPower = generateRandom(MAXIMUM_REQUIRED_CPU_POWER);
            process.setRequiredCpuPower(requiredCpuPower);
            int requiredMemory = generateRandom(MAXIMUM_REQUIRED_MEMORY);
            process.setRequiredMemory(requiredMemory);
            int requiredNetworkBandwidth = generateRandom(MAXIMUM_REQUIRED_NETWORK_BANDWIDTH);
            process.setRequiredNetworkBandwidth(requiredNetworkBandwidth);
            logger.trace("Created CloudProcess with requiredCpuPower ({}), requiredMemory ({}),"
                    + " requiredNetworkBandwidth ({}).",
                    requiredCpuPower, requiredMemory, requiredNetworkBandwidth);
            // Notice that we leave the PlanningVariable properties on null
            processList.add(process);
        }
        cloudBalance.setProcessList(processList);
    }

    private int generateRandom(int maximumValue) {
        double randomDouble = random.nextDouble();
        double parabolaBase = 2000.0;
        double parabolaRandomDouble = (Math.pow(parabolaBase, randomDouble) - 1.0) / (parabolaBase - 1.0);
        if (parabolaRandomDouble < 0.0 || parabolaRandomDouble >= 1.0) {
            throw new IllegalArgumentException("Invalid generated parabolaRandomDouble (" + parabolaRandomDouble + ")");
        }
        int value = ((int) Math.floor(parabolaRandomDouble * ((double) maximumValue))) + 1;
        if (value < 1 || value > maximumValue) {
            throw new IllegalArgumentException("Invalid generated value (" + value + ")");
        }
        return value;
    }

    private void assureComputerCapacityTotalAtLeastProcessRequiredTotal(CloudBalance cloudBalance) {
        List<CloudComputer> computerList = cloudBalance.getComputerList();
        int cpuPowerTotal = 0;
        int memoryTotal = 0;
        int networkBandwidthTotal = 0;
        for (CloudComputer computer : computerList) {
            cpuPowerTotal += computer.getCpuPower();
            memoryTotal += computer.getMemory();
            networkBandwidthTotal += computer.getNetworkBandwidth();
        }
        int requiredCpuPowerTotal = 0;
        int requiredMemoryTotal = 0;
        int requiredNetworkBandwidthTotal = 0;
        for (CloudProcess process : cloudBalance.getProcessList()) {
            requiredCpuPowerTotal += process.getRequiredCpuPower();
            requiredMemoryTotal += process.getRequiredMemory();
            requiredNetworkBandwidthTotal += process.getRequiredNetworkBandwidth();
        }
        int cpuPowerLacking = requiredCpuPowerTotal - cpuPowerTotal;
        while (cpuPowerLacking > 0) {
            CloudComputer computer = computerList.get(random.nextInt(computerList.size()));
            int upgrade = determineUpgrade(cpuPowerLacking);
            computer.setCpuPower(computer.getCpuPower() + upgrade);
            cpuPowerLacking -= upgrade;
        }
        int memoryLacking = requiredMemoryTotal - memoryTotal;
        while (memoryLacking > 0) {
            CloudComputer computer = computerList.get(random.nextInt(computerList.size()));
            int upgrade = determineUpgrade(memoryLacking);
            computer.setMemory(computer.getMemory() + upgrade);
            memoryLacking -= upgrade;
        }
        int networkBandwidthLacking = requiredNetworkBandwidthTotal - networkBandwidthTotal;
        while (networkBandwidthLacking > 0) {
            CloudComputer computer = computerList.get(random.nextInt(computerList.size()));
            int upgrade = determineUpgrade(networkBandwidthLacking);
            computer.setNetworkBandwidth(computer.getNetworkBandwidth() + upgrade);
            networkBandwidthLacking -= upgrade;
        }
    }

    private int determineUpgrade(int lacking) {
        for (int upgrade : new int[] {8, 4, 2, 1}) {
            if (lacking >= upgrade) {
                return upgrade;
            }
        }
        throw new IllegalStateException("Lacking (" + lacking + ") should be at least 1.");
    }

}
