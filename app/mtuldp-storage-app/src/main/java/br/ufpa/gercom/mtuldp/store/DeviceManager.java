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

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.neo4j.driver.v1.util.Function;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static com.google.common.base.Preconditions.checkNotNull;


public class DeviceManager {

    private Neo4jDriver driver;
    private final Logger log = getLogger(getClass());

    private static String CREATE =
            "MERGE " +
                    "(a:%s " +
                    "{" +
                    "type:'%s', " +
                    "device_id:'%s', " +
                    "manufacturer:'%s', " +
                    "hwVersion:'%s', " +
                    "swVersion:'%s', " +
                    "serialNumber:'%s'" +
                    "}" +
                    ")";

    private static String UPDATE =
            "MATCH (a:%s ) " +
                    "WHERE " +
                    "a.device_id = '%s'," +
                    "SET" +
                    "a.type = '%s'" +
                    "a.manufacturer = '%s'," +
                    "a.hwVersion = '%s'," +
                    "a.swVersion = '%s'," +
                    "a.serialNumber = '%s'";


    private static String DELETE =
            "MATCH (a:%s)" +
                    "WHERE " +
                    "a.device_id = '%s'" +
                    "DELETE" +
                    "a";

    private static String EXISTS =
            "MATCH " +
                    "(" +
                    "a:%s" +
                    ") " +
                    "WHERE " +
                    "a.device_id = '%s' " +
                    "RETURN  " +
                    "a IS NOT NULL as result";

    public DeviceManager(Neo4jDriver driver) {
        this.driver = driver;
    }

    public boolean create(Device device) throws RuntimeException {

        checkNotNull(device, "Device Object cannot be null");

        String type = device.type().name();
        String device_id = device.id().toString();
        String manufacturer = device.manufacturer();
        String hwVersion = device.hwVersion();
        String swVersion = device.swVersion();
        String serialNumber = device.serialNumber();


        String query = String.format(CREATE, type, type, device_id, manufacturer,
                hwVersion, swVersion, serialNumber);

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (summary.counters().nodesCreated() == 0){
            log.info("the node ({}) already exists on database", device_id);
            return false;
        }

        log.info("New node ({}) inserted on database", device_id);
        return true;
    }

    public boolean update(DeviceId id, Device device) throws RuntimeException{

        checkNotNull(id, "Device ID on UPDATE cannot be null");
        checkNotNull(device, "Device Object on UPDATE cannot be null");

        String type = device.type().name();
        String device_id = device.id().toString();
        String manufacturer = device.manufacturer();
        String hwVersion = device.hwVersion();
        String swVersion = device.swVersion();
        String serialNumber = device.serialNumber();

        String query = String.format(UPDATE,type,device_id, type,manufacturer,
                hwVersion,swVersion,serialNumber);

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (summary.counters().containsUpdates()){
            log.info("the node ({}) cannot be updated", device_id);
            return true;
        }

        return false;
    }

    public boolean delete(Device device) throws RuntimeException{

        checkNotNull(device, "Device Object on DELETE cannot be null");

        String query = String.format(DELETE,device.type().name(), device.id().toString());

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (summary.counters().nodesDeleted() == 0 ){
            log.info("the node ({}) cannot be updated", device.id().toString());
            return false;
        }

        log.info("The node ({}) was deleted", device.id().toString());

        return true;
    }


    public boolean exists(DeviceId id, Device.Type type) throws RuntimeException {

        String query = String.format(EXISTS,type, id.toString());

        StatementResult result = driver.executeCypherQuery(query);
        Record record = result.single();

        return Boolean.getBoolean(record.get("result").asString());

    }

    public boolean setLabel(DeviceId id, String label) throws RuntimeException {
        String query = String.format(
                "MATCH (a) " +
                        "WHERE " +
                        "a.device_id = '%s' " +
                        "SET a:%s ");
    }

    public List<String> getLabels(DeviceId id){

    }

}
