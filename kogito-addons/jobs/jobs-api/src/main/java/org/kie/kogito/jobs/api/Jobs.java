/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Jobs encapsulate the life cycle of the jobs service that is managing
 * individual job instances.
 */
@Path("/jobs")
public interface Jobs {

    /**
     * Create and schedule given job instance. Before scheduling it will validate the job based
     * <ul>
     *  <li>required attributes of the job</li>
     *  <li>uniqueness of the job identifier</li>
     *  <li>expiration date to be in future</li>
     * </ul>
     * 
     * @param job job to be scheduled
     * @throws InvalidJobException in case the validation of the job failed    
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    void create(Job job);
    
    /**
     * Returns the job identified by given identifier
     * @param jobId unique identifier of the job
     * @return found job or JobNotFoundException
     * @throws JobNotFoundException
     */
    @GET
    @Path("/{jobId}")
    @Produces(MediaType.APPLICATION_JSON)
    Job getOne(@PathParam("jobId") String jobId);
    
    /**
     * Returns list of found jobs that are currently scheduled
     * @return list of jobs or an empty list
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Job> get();
    
    /**
     * Updates the job identified by given identifier.
     * @param jobId unique identifier of the job
     * @param job updated job
     * @throws InvalidJobException in case the validation of the job failed
     * @throws JobNotFoundException
     */
    @PUT
    @Path("/{jobId}")
    @Consumes(MediaType.APPLICATION_JSON)
    void update(@PathParam("jobId") String jobId, Job job);
    
    /**
     * Deletes (and unschedules) the job instance
     * @param jobId unique identifier of the job
     * @throws JobNotFoundException
     */
    @DELETE
    @Path("/{jobId}")
    void delete(@PathParam("jobId") String jobId);
}
