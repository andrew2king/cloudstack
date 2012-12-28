// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package org.apache.cloudstack.api.command.admin.vlan;

import org.apache.cloudstack.api.*;
import org.apache.log4j.Logger;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.response.DomainResponse;
import org.apache.cloudstack.api.response.PodResponse;
import org.apache.cloudstack.api.response.PhysicalNetworkResponse;
import org.apache.cloudstack.api.response.ProjectResponse;
import org.apache.cloudstack.api.response.NetworkResponse;
import org.apache.cloudstack.api.response.VlanIpRangeResponse;
import org.apache.cloudstack.api.response.ZoneResponse;
import com.cloud.dc.Vlan;
import com.cloud.exception.ConcurrentOperationException;
import com.cloud.exception.InsufficientCapacityException;
import com.cloud.exception.ResourceAllocationException;
import com.cloud.exception.ResourceUnavailableException;
import com.cloud.user.Account;

@APICommand(name = "createVlanIpRange", description="Creates a VLAN IP range.", responseObject=VlanIpRangeResponse.class)
public class CreateVlanIpRangeCmd extends BaseCmd {
    public static final Logger s_logger = Logger.getLogger(CreateVlanIpRangeCmd.class.getName());

    private static final String s_name = "createvlaniprangeresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name=ApiConstants.ACCOUNT, type=CommandType.STRING, description="account who will own the VLAN. If VLAN is Zone wide, this parameter should be ommited")
    private String accountName;

    @Parameter(name=ApiConstants.PROJECT_ID, type=CommandType.UUID, entityType = ProjectResponse.class,
            description="project who will own the VLAN. If VLAN is Zone wide, this parameter should be ommited")
    private Long projectId;

    @Parameter(name=ApiConstants.DOMAIN_ID, type=CommandType.UUID, entityType = DomainResponse.class,
            description="domain ID of the account owning a VLAN")
    private Long domainId;

    @Parameter(name=ApiConstants.END_IP, type=CommandType.STRING, description="the ending IP address in the VLAN IP range")
    private String endIp;

    @Parameter(name=ApiConstants.FOR_VIRTUAL_NETWORK, type=CommandType.BOOLEAN, description="true if VLAN is of Virtual type, false if Direct")
    private Boolean forVirtualNetwork;

    @Parameter(name=ApiConstants.GATEWAY, type=CommandType.STRING, description="the gateway of the VLAN IP range")
    private String gateway;

    @Parameter(name=ApiConstants.NETMASK, type=CommandType.STRING, description="the netmask of the VLAN IP range")
    private String netmask;

    @Parameter(name=ApiConstants.POD_ID, type=CommandType.UUID, entityType = PodResponse.class,
            description="optional parameter. Have to be specified for Direct Untagged vlan only.")
    private Long podId;

    @Parameter(name=ApiConstants.START_IP, type=CommandType.STRING, required=true, description="the beginning IP address in the VLAN IP range")
    private String startIp;

    @Parameter(name=ApiConstants.VLAN, type=CommandType.STRING, description="the ID or VID of the VLAN. If not specified," +
            " will be defaulted to the vlan of the network or if vlan of the network is null - to Untagged")
    private String vlan;

    @Parameter(name=ApiConstants.ZONE_ID, type=CommandType.UUID, entityType = ZoneResponse.class,
            description="the Zone ID of the VLAN IP range")
    private Long zoneId;

    @Parameter(name=ApiConstants.NETWORK_ID, type=CommandType.UUID, entityType = NetworkResponse.class,
            description="the network id")
    private Long networkID;

    @Parameter(name=ApiConstants.PHYSICAL_NETWORK_ID, type=CommandType.UUID, entityType = PhysicalNetworkResponse.class,
            description="the physical network id")
    private Long physicalNetworkId;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public String getAccountName() {
        return accountName;
    }

    public Long getDomainId() {
        return domainId;
    }

    public String getEndIp() {
        return endIp;
    }

    public Boolean isForVirtualNetwork() {
        return forVirtualNetwork == null ? true : forVirtualNetwork;
    }

    public String getGateway() {
        return gateway;
    }

    public String getNetmask() {
        return netmask;
    }

    public Long getPodId() {
        return podId;
    }

    public String getStartIp() {
        return startIp;
    }

    public String getVlan() {
        return vlan;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public Long getProjectId() {
        return projectId;
    }
    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    public Long getNetworkID() {
        return networkID;
    }

    public Long getPhysicalNetworkId() {
        return physicalNetworkId;
    }


    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public long getEntityOwnerId() {
        return Account.ACCOUNT_ID_SYSTEM;
    }

    @Override
    public void execute() throws ResourceUnavailableException, ResourceAllocationException{
        try {
            Vlan result = _configService.createVlanAndPublicIpRange(this);
            if (result != null) {
                VlanIpRangeResponse response = _responseGenerator.createVlanIpRangeResponse(result);
                response.setResponseName(getCommandName());
                this.setResponseObject(response);
            }else {
                throw new ServerApiException(BaseCmd.INTERNAL_ERROR, "Failed to create vlan ip range");
            }
        } catch (ConcurrentOperationException ex) {
            s_logger.warn("Exception: ", ex);
            throw new ServerApiException(BaseCmd.INTERNAL_ERROR, ex.getMessage());
        } catch (InsufficientCapacityException ex) {
            s_logger.info(ex);
            throw new ServerApiException(BaseCmd.INSUFFICIENT_CAPACITY_ERROR, ex.getMessage());
        }
    }
}
