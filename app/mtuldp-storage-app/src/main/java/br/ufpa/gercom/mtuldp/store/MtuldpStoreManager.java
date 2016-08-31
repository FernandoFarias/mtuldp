/*
 * Copyright 2016-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.ufpa.gercom.mtuldp.store;

import org.apache.felix.scr.annotations.*;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.*;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.slf4j.Logger;

import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class MtuldpStoreManager {

    private final Logger log = getLogger(getClass());


    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private CoreService coreService;


    private ApplicationId appID;

    @Activate
    protected void activate() {

        appID = coreService.getAppId("br.ufpa.gercom.mtuldp.store");

        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped");
    }


    private enum DeviceTypes{
        HOST, SWITCH, ROUTER;
    }

    private enum LinkTypes  {
        DIRECT, EDGE;
    }

    private class MtuldpStore {

        private Driver driver;
        private Session session;

        MtuldpStore(String neo4j_url) {
            this.driver = GraphDatabase.driver(neo4j_url);
            this.session = driver.session();
        }

        MtuldpStore(String neo4j_url, String user, String pass) {
            this.driver = GraphDatabase.driver(neo4j_url, AuthTokens.basic(user, pass));
            this.session = driver.session();
            if (session.isOpen()){
                log.info("Connection with neo4j database had opened");
            }
        }

        public StatementResult doQuery(String query){

        }

        private boolean createDevice(Device device) {

            String deviceType = DeviceTypes.SWITCH.name();
            DeviceId deviceId = device.id();

            String query = String.format("CREATE (a:%s {type: '%s', device_id: '%s'})", deviceType,
                    deviceType, deviceId.toString());

            ResultSummary result = session.run(query).consume();

            if (result.counters().nodesCreated() == 0) {
                return false;
            }
            return true;
        }
        private boolean createLink(Link link){

            ConnectPoint src = link.src();
            ConnectPoint dst = link.dst();

            String deviceType = DeviceTypes.SWITCH.name();
            DeviceId srcDeviceId = src.deviceId();
            DeviceId dstDeviceId = dst.deviceId();


            String query = String.format("MATCH (a:%s {device_id:'%s'}), (b:%s {device_id:'%s'}) " +
                            "MERGE (a)-[r:%s{port_src: %d, port_dst: %d, mtu:NULL}]->(b)",
                    deviceType,deviceType,srcDeviceId.toString(), dstDeviceId.toString(),
                    link.type().name(), Integer.getInteger(src.port().name()), Integer.getInteger(dst.port().name()));

            ResultSummary result = session.run(query).consume();

            if (result.counters().relationshipsCreated() == 0){
                return false;
            }
            return true;
        }

        private boolean createHost(Host host){

            String type = DeviceTypes.HOST.name();
            HostId hostId = host.id();
            MacAddress mac = host.mac();
            Set<IpAddress> ipAddressSet = host.ipAddresses();

            String query = String.format("CREATE(a:%s{ type:'%s',host_id:'%s', mac:'%s', ip:%s})",
                    type,type, hostId.toString(), mac.toString(),ipAddressSet.toArray().toString());

            ResultSummary result = session.run(query).consume();

            if (result.counters().nodesCreated() == 0 ){
                return false;
            }
            return true;
        }

        private boolean createEdgeLink(EdgeLink edgeLink){

            String deviceType = DeviceTypes.SWITCH.name();
            String edgeDeviceType = DeviceTypes.HOST.name();
            String linkType = LinkTypes.EDGE.name();
            DeviceId deviceId = edgeLink.hostLocation().deviceId();
            PortNumber edgePort = edgeLink.hostLocation().port();
            HostId hostId = edgeLink.hostId();

            String query = String.format("MATCH (a:%s {host_id:'%s'}), (b:%s{device_id:'%s'}) " +
                    "MERGE (a)-[r:'%s'{edge_port:%d, mtu: NULL} ]->(b)", edgeDeviceType, hostId.toString(),
                    deviceType, deviceId.toString(),linkType, Integer.getInteger(edgePort.name()));

            ResultSummary result = session.run(query).consume();

            if (result.counters().relationshipsCreated() == 0){
                return false;
            }
            return true;
        }

        private boolean deleteDevice()

        public void setDevice(Device dev){
            if (createDevice(dev)){
                log.info("New device ({}) was created in database", dev.id().toString());
            } else {
                throw new RuntimeException("Device cannot be stored on database, review database connection");
            }

        }

        public void setHost(Host host){

            if (createHost(host)){
                log.info("New Host ({}) was created on database", host.id().toString());
             } else {
                 throw new RuntimeException("The host cannot be stored on database, please review your database connection");
            }
        }

        public void setEdgeLink(EdgeLink edge){
            if (createEdgeLink(edge)){
                log.info("New Edge Link ({}) was created on database", edge.hostId().toString());
            } else {
                throw new RuntimeException("Edge Link cannot be stored on database, review neo4j database connection");
            }
        }

        public void setLink(Link link){
            if (createLink(link)){
                log.info("New Link ({}) was created on database", link.toString());
            } else {
                throw new RuntimeException("Link cannot be stored on database, review neo4j database connection");
            }
        }

    }

    private class InnerDeviceListener implements DeviceListener {

        @Override
        public void event(DeviceEvent event) {
            switch (event.type()){
                case
            }
        }
    }

}

