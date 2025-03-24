/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.acme;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;

@Path("/find-approved")
public class LoanApplicationEndPoint {

	@Inject
	KieRuntimeBuilder kieRuntimeBuilder;

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public List<LoanApplication> executeQuery(LoanAppDTO loanAppDto) {
		KieSession session = kieRuntimeBuilder.newKieSession();
		List<LoanApplication> approvedApplications = new ArrayList<>();

		session.setGlobal("approvedApplications", approvedApplications);
		session.setGlobal("maxAmount", loanAppDto.getMaxAmount());
		loanAppDto.getLoanApplications().forEach(session::insert);

		session.fireAllRules();
		session.dispose();
		return approvedApplications;
	}
}
