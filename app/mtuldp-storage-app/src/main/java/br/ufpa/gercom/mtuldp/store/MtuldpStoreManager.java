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
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.Device;
import org.onosproject.net.Link;
import org.slf4j.Logger;

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
        }

        private boolean createDevice(Device device) {

            String query = String.format("CREATE (a:%s {type: '%s', device_id: '%s'})",
                    device.type().name(), device.type().name(), device.id().toString());

            ResultSummary result = session.run(query).consume();

            if (result.counters().nodesCreated() == 0) {
                return false;
            }
            return true;
        }
        private boolean createLink(Link link){

            ConnectPoint src = link.src();
            ConnectPoint dst = link.dst();

            String query = String.format("MATCH (a {device_id:'%s'}), (b {device_id:'%s'}) " +
                            "MERGE (a)-[r:%s{port_src: %d, port_dst: %d, mtu:'null'}]->(b)",
                    src.deviceId().toString(), dst.deviceId().toString(), link.type().name(),
                    Integer.getInteger(src.port().name()), Integer.getInteger(dst.port().name()));

            ResultSummary result = session.run(query).consume();

            if (result.counters().relationshipsCreated() == 0){
                return false;
            }

            return true;
        }
    }


}

