/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.wso2.appcloud.tierapi.server.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appcloud.tierapi.bean.ContainerSpecifications;
import org.wso2.appcloud.tierapi.bean.Plan;
import org.wso2.appcloud.tierapi.dao.impl.PlanDaoImpl;
import org.wso2.appcloud.tierapi.delegate.DAOdelegate;
import org.wso2.appcloud.tierapi.server.PlanService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of {@link PlanService}.
 */

@Path("/plans")
public class PlanServiceImpl implements PlanService {

    private PlanDaoImpl planInstance = (PlanDaoImpl) DAOdelegate.getPlanInstance();
    private static final Log log = LogFactory.getLog(PlanServiceImpl.class);

    /**
     * Get all Plans
     *
     *  @return {@link Response}
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPlans() {

        try {
            List<Plan> plansList = planInstance.getAllPlans();
            GenericEntity<List<Plan>> entity = new
                    GenericEntity<List<Plan>>(plansList) {};
            return Response.ok().entity(entity).type(MediaType.APPLICATION_JSON_TYPE).build();
        } catch (SQLException e) {
            String msg = "Error while gettings plans list";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE).build();
        }
    }

    /**
     * Get Plan using Plan ID
     *
     * @param planId    Plan ID of the plan
     * @return {@link Response}
     */
    @GET
    @Path("/{planId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPlan(@PathParam("planId") int planId) {
        try {
            Plan plan = planInstance.getPlanByPlanId(planId);
            return Response.ok().entity(plan).type(MediaType.APPLICATION_JSON_TYPE).build();
        } catch (SQLException e) {
            String msg = "Error while getting details for plan with plan id: " + planId;
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE).build();
        }
    }

    /**
     * Get allowed container specifications using Plan ID
     *
     * @param planId    Plan ID of the plan
     * @return {@link Response}
     */
    @GET
    @Path("/allowedSpecs/{planId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllowedConSpecs(@PathParam("planId") int planId) {
        try {
            List<ContainerSpecifications> containerSpecificationsList = planInstance.getAllowedConSpecs(planId);
            GenericEntity<List<ContainerSpecifications>> entity = new
                    GenericEntity<List<ContainerSpecifications>>(containerSpecificationsList) {};
            return Response.ok().entity(entity).type(MediaType.APPLICATION_JSON_TYPE).build();
        } catch (SQLException e) {
            String msg = "Error while getting allowed container specifications for plan with plan id: " + planId;
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE).build();
        }
    }
}
