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
dashbuilder = {
  /*
    // possible modes are EDITOR and CLIENT - if dashboards is set then CLIENT mode is assumed
	mode: "CLIENT",
	// The list of client dashboards - if CLIENT mode is used and no list is provided, than the dashboard "dashboard.yml" will be attempted to load. If no dashboard is found, then client opens for upload. 
	// The dashboard can also be an URL
	dashboards: [ "dashboard1.yaml", "dashboard2.json"],

	// base path to look for dashboards. Default is /
	path: "/path",

	// Set this as true to always hide the nav bar
	hideNavBar: true
*/
  hideNavBar: true,
  dashboards: ['MonitoringReport.dash.yml', 'WorkflowDetails.dash.yml']
};
