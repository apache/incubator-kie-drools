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

package org.optaplanner.webexamples.cloudbalancing;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.http.HttpSession;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.XmlSolverFactory;
import org.optaplanner.core.api.solver.Solver;

import org.optaplanner.core.impl.event.BestSolutionChangedEvent;
import org.optaplanner.core.impl.event.SolverEventListener;

import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.cloudbalancing.persistence.CloudBalancingGenerator;

public class CloudWebAction {


    private static ExecutorService solvingExecutor = Executors.newFixedThreadPool(4);

    public void setup(HttpSession session) {
        SolverFactory solverFactory = new XmlSolverFactory( "/org/optaplanner/examples/cloudbalancing/solver/cloudBalancingSolverConfig.xml");
        Solver solver = solverFactory.buildSolver();
        session.setAttribute(CloudSessionAttributeName.SOLVER, solver);

        // Load a problem with 40 computers and 120 processes
        CloudBalance unsolvedCloudBalance = new CloudBalancingGenerator().createCloudBalance(40, 120);

        //URL unsolvedSolutionURL = getClass().getResource("/org/drools/planner/webexamples/vehiclerouting/A-n33-k6.vrp");
        //VrpSchedule unsolvedSolution = (VrpSchedule) new VehicleRoutingSolutionImporter().readSolution(unsolvedSolutionURL);


        session.setAttribute(CloudSessionAttributeName.SHOWN_SOLUTION, unsolvedCloudBalance);
    }


    public void solve(final HttpSession session) {
        final Solver solver = (Solver) session.getAttribute(CloudSessionAttributeName.SOLVER);
        CloudBalance unsolvedCloudBalance = (CloudBalance) session.getAttribute(CloudSessionAttributeName.SHOWN_SOLUTION);

        solver.setPlanningProblem(unsolvedCloudBalance);
        solver.addEventListener(new SolverEventListener() {
            public void bestSolutionChanged(BestSolutionChangedEvent event) {
                CloudBalance bestSolutionCloudBalance = (CloudBalance) event.getNewBestSolution();
                session.setAttribute(CloudSessionAttributeName.SHOWN_SOLUTION, bestSolutionCloudBalance);
            }
        });
        solvingExecutor.submit(new Runnable() {
            public void run() {
                solver.solve();
            }
        });
    }

    public void terminateEarly(HttpSession session) {
        final Solver solver = (Solver) session.getAttribute(CloudSessionAttributeName.SOLVER);
        solver.terminateEarly();
    }

    public TreeMap toDisplayString(HttpSession session) {

        CloudBalance bestSolutionCloudBalance = (CloudBalance) session.getAttribute(CloudSessionAttributeName.SHOWN_SOLUTION);
        Hashtable ht = new Hashtable();
	//Hashtable htTmp = new Hashtable();
	TreeMap tm2 = new TreeMap();

        for (CloudProcess process : bestSolutionCloudBalance.getProcessList()) {
            String[ ] computerArray = new String[9];
            CloudComputer computer = process.getComputer();
            if (computer == null){
                computerArray[0] = "N/A";
                computerArray[1] = "N/A";
                computerArray[2] = "N/A";
                computerArray[3] = "N/A";
                computerArray[4] = "N/A";
                computerArray[5] = "N/A";
                computerArray[6] = "N/A";
                computerArray[7] = "N/A";
                computerArray[8] = "N/A";
                ht.put(process.getLabel(),computerArray);
            }
            else
            {
                computerArray[0] = computer.getLabel();
                computerArray[1] = String.valueOf(computer.getCpuPower());
                computerArray[2] = String.valueOf(computer.getMemory());
                computerArray[3] = String.valueOf(computer.getNetworkBandwidth());
                computerArray[4] = String.valueOf(computer.getCost());
                computerArray[5] = String.valueOf(process.getRequiredCpuPower());
                computerArray[6] = String.valueOf(process.getRequiredMemory());
                computerArray[7] = String.valueOf(process.getRequiredNetworkBandwidth());
		computerArray[8] = process.getLabel();
		ht.put(process.getLabel(),computerArray);
            }
	}
	tm2=sortValue(ht);
	//return htTmp;
	return tm2;
	//return ht;
    }

	  



    public TreeMap sortValue(Hashtable<String, String[ ]> ht){
        Enumeration enumeration;
        Hashtable htTmp1 = new Hashtable();
	TreeMap tm = new TreeMap();
        //Hashtable htTmp2 = new Hashtable();
	String key;
	String computerKey="Process 0";
	String computerName="N/A";
	String[ ] valueArray;
	int i = 0;
	int j = 0;
	int k = 0;
	String delims = "[ ]";
	String[ ] computerArray = new String[9];
	String[ ] computerNameArray = new String[2];
	String computerId = "0";
	String highestComputerId = "0";
	String originalHighestComputerId = "0";
	String oldComputerId = "0";

	System.out.println(" ");
	System.out.println("//////////////////////");
	System.out.println("NEW CALL TO THE METHOD");

		htTmp1=(Hashtable) ht.clone();
		enumeration = htTmp1.keys();
		while (enumeration.hasMoreElements()) {
                	                key = (String) enumeration.nextElement();
                        	        valueArray = (String[ ]) htTmp1.get(key);
					if (valueArray[0].equals("N/A")){
						i = 1;
						j = 1;
						//htTmp2=(Hashtable) htTmp1.clone();
						tm.putAll(htTmp1);
						break;
					}
					else
					{
                               			computerNameArray = valueArray[0].split(delims);
						computerId = computerNameArray[1];
						//System.out.println("COMPUTERID : " + computerId + " >=  OLDCOMPUTERID : " + oldComputerId);
                                		if ( Integer.valueOf(computerId) >= Integer.valueOf(oldComputerId) ) {
							highestComputerId = computerId;
							oldComputerId = highestComputerId;
                                		}
					}
		}	

		originalHighestComputerId=highestComputerId;
		while ( i == 0) {
			//System.out.println(" ");
			//System.out.println("REAL VALUE");
			//System.out.println(highestComputerId);
                        if (htTmp1.isEmpty())
                        {
				System.out.println("OUT IS EMPTY ");
				i = 1;
			}
			else
			{
				enumeration = htTmp1.keys();
        			while (enumeration.hasMoreElements()) {
	        			        key = (String) enumeration.nextElement();
               					valueArray = (String[ ]) htTmp1.get(key);
						computerNameArray = valueArray[0].split(delims);
						computerId = computerNameArray[1];
						if ( Integer.valueOf(computerId) <= Integer.valueOf(highestComputerId) )
						{
							computerKey = key;
							computerArray = valueArray;
							computerNameArray = computerArray[0].split(delims);
							highestComputerId = computerNameArray[1];
							//System.out.println(" HIGHEST COMPUTER : " + computerArray[0]);
							
						}
				}
			//	System.out.println(" ");
			//	System.out.println("computer : " + computerArray[0] + " PROCESS : " + computerKey);
				computerName=String.format("%06d", k++);
				computerArray[8]=computerKey;			
				//htTmp2.put(computerName,computerArray);
				System.out.println("COMPUTER ID : " + computerName  + " COMPUTER NAME : " + computerArray[0]);
				tm.put(computerName,computerArray);
                        	htTmp1.remove(computerKey);
				highestComputerId=originalHighestComputerId;
				if (htTmp1.isEmpty())
                        	{
		//			System.out.println("OUT DO NOT CONTAIN ANY KEY");
					i = 1;
                        	}

			}
		}	
	System.out.println("//////////////////////");
	System.out.println("//////////////////////");
//	if (j == 0) {
//		Set ref = htTmp2.keySet();
//		Iterator it = ref.iterator();
//		while (it.hasNext()) {
//  			key = (String)it.next();
//			valueArray = (String[ ]) htTmp2.get(key);
//			System.out.println("COMPUTER : "+ key + " has PROCESS : " + valueArray[0]);
//		}
	//	enumeration = htTmp2.keys();
        //	while (enumeration.hasMoreElements()) {
        //		key = (String) enumeration.nextElement();
        //       		valueArray = (String[ ]) htTmp2.get(key);
	//		System.out.println("COMPUTER : "+ valueArray[0] + "has PROCESS : " + key); 
	//	}
//	}
	
	return tm;
	}
			

// COMMENTAIRE //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* 
    public Hashtable toDisplayString(HttpSession session) {
        CloudBalance bestSolutionCloudBalance = (CloudBalance) session.getAttribute(CloudSessionAttributeName.SHOWN_SOLUTION);
        StringBuilder displayString = new StringBuilder();
        Hashtable ht = new Hashtable();
        Hashtable htTmp = new Hashtable();
	String computerName="N/A";
	String delims = "[ ]";
	String[ ] token1;
	String[ ] token2;
	String[ ] token3;
	String[ ] valueArray= new String[8];
	int i=0;
	int j=0;
	String key;
	String[ ] oldValue = {"computer","0"};
        ArrayList<Hashtable<String, String[ ]>> computerArrayList = new ArrayList<Hashtable<String, String[ ]>>();

        for (CloudProcess process : bestSolutionCloudBalance.getProcessList()) {
            String[ ] computerArray = new String[8];
            CloudComputer computer = process.getComputer();
	    if (computer == null){
            	computerArray[0] = "N/A";
            	computerArray[1] = "N/A";
            	computerArray[2] = "N/A";
            	computerArray[3] = "N/A";
            	computerArray[4] = "N/A";
            	computerArray[5] = "N/A";
            	ht.put(process.getLabel(),computerArray);
	    }
	    else
	    {
	        computerArray[0] = computer.getLabel();
		computerArray[1] = String.valueOf(computer.getCpuPower());
		computerArray[2] = String.valueOf(computer.getMemory());
		computerArray[3] = String.valueOf(computer.getNetworkBandwidth());
		computerArray[4] = String.valueOf(computer.getCost());
		computerArray[5] = String.valueOf(process.getRequiredCpuPower());
		computerArray[6] = String.valueOf(process.getRequiredMemory());
		computerArray[7] = String.valueOf(process.getRequiredNetworkBandwidth());

		
		if (computerName.toLowerCase().contains("computer")) {
                	token1 = computerName.split(delims);
                	token2 = computer.getLabel().split(delims);
			//                       OLD VALUE                             OBJECT VALUE
			if (Integer.valueOf(token1[1]) <= Integer.valueOf(token2[1]))
			{
				System.out.println("2.1] Tocken 1 (LAST COMPUTER NAME) " + " -> " + token1[1] + " <= " + "Tocken 2 (OBJECT VALUE) " + " -> " + token2[1]);
				ht.put(process.getLabel(),computerArray);
				computerName = computer.getLabel();
			        System.out.println("2.2] " +  computerName );
			}
			else
			{
				System.out.println(" ");
				System.out.println(" ");
				System.out.println("3.1] Tocken 1 (LAST COMPUTER NAME) " + " -> " + token1[1] + " > " + "Tocken 2 (OBJECT VALUE) " + " -> " + token2[1]);
				System.out.println("WE RESTART THE LOOP");
				//htTmp=(Hashtable) ht.clone();
				//ht.clear();


			for (int i=0; i < list.size(); i++) {
			  String s = (String) list.get(i);
			  System.out.println(s);
			}


				
				System.out.println(oldValue[1]);
				Enumeration enumeration = ht.keys();
				while (enumeration.hasMoreElements()) {
        				key = (String) enumeration.nextElement();
        				valueArray = (String[ ]) htTmp.get(key);
                        		token3 = valueArray[0].split(delims);
					System.out.println("++++++++++++++++++++++++++++++ "+  token3[0] + " " + token3[1] );
					if ( i == 0 ){
						if ( j == 0 ){
							if ( Integer.valueOf(token3[1]) >= Integer.valueOf(token2[1]) ){
							        System.out.println("3.2] Tocken 3 (HTTMP) " + " -> " + token3[1] + " > " + "Tocken 2 (OBJECT VALUE) " + " -> " + token2[1]);

                                                                System.out.println("3.2.1] " + computerArray[0]);
                                                                ht.put(process.getLabel(),computerArray);
								System.out.println("Process Name: " + process.getLabel());

                                                                System.out.println("3.2.2] " +  valueArray[0] );
                                                                ht.put(key,valueArray);
								System.out.println("Process Name: " + key);

                                                                i=1;

							}
						}
						else {
						
							if ( ( Integer.valueOf(token3[1]) >= Integer.valueOf(token2[1]) ) && ( Integer.valueOf(oldValue[1]) <= Integer.valueOf(token2[1]) ) ){
								System.out.println("3.3] OLD VALUE " + " -> " + oldValue[1] + " <= " + "Tocken 2 (OBJECT VALUE) " + " -> " + token2[1]);
								System.out.println("3.3] Tocken 3 (HTTMP) " + " -> " + token3[1] + " >= " + "Tocken 2 (OBJECT VALUE) " + " -> " + token2[1]);

                                               			ht.put(process.getLabel(),computerArray);
                                       	 			System.out.println(computerArray[0]);
								System.out.println("Process Name: " + process.getLabel());

								ht.put(key,valueArray);
                                               			System.out.println("3.3.1] " +  valueArray[0] );
								System.out.println("Process Name: " + key);
			
								i=1;

							}
							else
							{
								System.out.println("3.4] Tocken 3 (HTTMP) " + " -> " + token3[1] + " < " + "Tocken 2 (OBJECT VALUE) " + " -> " + token2[1]);
								ht.put(key,valueArray);
                                               			System.out.println("3.4.1] " + valueArray[0]);
								System.out.println("Process Name: " + key);
							}
						}
					
					}
					else
					{
						ht.put(key,valueArray);
                                                System.out.println("3.5] " + valueArray[0]);
					        System.out.println("Process Name: " + key);
					}
					j++;
					oldValue=valueArray [0].split(delims);
					token3[0]=" ";
					token3[1]=" ";
				}
				
				computerName = valueArray [0];
				j=0;	
				i=0;
				htTmp.clear();
			}
			
		}
		else
		{
			ht.put(process.getLabel(),computerArray);
			computerName = computer.getLabel();
			System.out.println("1] " + computerName);
			
		}
		
	    }

            //ht.put(process.getLabel(),computerName);
            //displayString.append("  ").append(process.getLabel()).append(" -> ").append(computer == null ? null : computer.getLabel()).append("\n");
        }

        //return displayString.toString();
        return ht;
    }

*/
}
