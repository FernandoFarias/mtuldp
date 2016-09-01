/*
 * Copyright 2016 GERCOM, Lab. UFPA.
 *
 * Developer: fernando
 * Serial: 01/09/16 16:12
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

package br.ufpa.gercom.mtuldp.store;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.onosproject.net.Device;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by fernando on 01/09/16.
 */
public class DeviceManager {

    private Neo4jDriver driver;
    private final Logger log = getLogger(getClass());

    public DeviceManager(Neo4jDriver driver) {
        this.driver = driver;
    }

    public boolean create(Device device) throws RuntimeException {

        String type = device.type().name();
        String device_id = device.id().toString();
        String manufacturer = device.manufacturer();
        String hwVersion = device.hwVersion();
        String swVersion = device.swVersion();
        String serialNumber = device.serialNumber();


        String query = String.format("MERGE (a:%s {type:'%s', device_id:'%s', manufacturer:'%s', hwVersion:'%s', " +
                "swVersion:'%s', serialNumber:'%s'})", type, type, device_id, manufacturer, hwVersion, swVersion,
                serialNumber);

        StatementResult result = driver.doCypherQuery(query);
        ResultSummary summary = result.consume();


        result.list().isEmpty()
        /*
        try {
             result = driver.doCypherQuery(query);
             summary = result.consume();

        } catch (RuntimeException e) {
            log.debug(e.getCause().getMessage());
            log.error("A error was encounted to insert node () on database");
            return false;
        }
        */

        if (summary.counters().nodesCreated() == 0){
            log.info("the node ({}) already exists on database");
            return false;
        }

        log.info("New node ({}) inserted on database", device_id);
        return true;
    }


    public boolean isExist(Device d) {

        String query = String.format("MATCH (a:%s) WHERE a.device_id = '%s' return true ");


    }


}
