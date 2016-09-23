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
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.slf4j.Logger;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;


public class DeviceStorageMgm {

    private Neo4jIntegration driver;
    private final Logger log = getLogger(getClass());

    public DeviceStorageMgm(Neo4jIntegration driver) {
        this.driver = driver;
    }

    public boolean create(Device device) throws RuntimeException {

        checkNotNull(device, "Device Object cannot be null");

        String CREATE =
                "MERGE " +
                        "(a:%s " +
                        "{" +
                        "device_id:'%s', " +
                        "manufacturer:'%s', " +
                        "hwVersion:'%s', " +
                        "swVersion:'%s', " +
                        "serialNumber:'%s'" +
                        "}" +
                        ")";

        String type = device.type().name();
        String device_id = device.id().toString();
        String manufacturer = device.manufacturer();
        String hwVersion = device.hwVersion();
        String swVersion = device.swVersion();
        String serialNumber = device.serialNumber();


        String query = String.format(CREATE, type, device_id, manufacturer,
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

        String UPDATE =
                "MATCH (a) " +
                        "WHERE " +
                        "a.device_id = '%s'," +
                        "SET" +
                        "a.manufacturer = '%s'," +
                        "a.hwVersion = '%s'," +
                        "a.swVersion = '%s'," +
                        "a.serialNumber = '%s'";

        String device_id = device.id().toString();
        String manufacturer = device.manufacturer();
        String hwVersion = device.hwVersion();
        String swVersion = device.swVersion();
        String serialNumber = device.serialNumber();

        String query = String.format(UPDATE,device_id,manufacturer,
                hwVersion,swVersion,serialNumber);

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (summary.counters().containsUpdates()){
            log.error("Device id ({}) cannot be updated, data is not different or transaction error", device_id);
            return true;
        }

        log.info("Device id ({}) was updated", device_id);
        return false;
    }

    public boolean delete(Device device) throws RuntimeException{

        checkNotNull(device, "Device Object on DELETE cannot be null");

        String DELETE =
                "MATCH (a)" +
                        "WHERE " +
                        "a.device_id = '%s'" +
                        "DELETE" +
                        "a";

        String query = String.format(DELETE, device.id().toString());

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (summary.counters().nodesDeleted() == 0 ){
            log.info("the node ({}) cannot be updated", device.id().toString());
            return false;
        }

        log.info("The node ({}) was deleted", device.id().toString());

        return true;
    }


    public boolean exists(DeviceId id) throws RuntimeException {

        checkNotNull(id,"Device id cannot be null");

        String EXISTS =
                "MATCH (a)" +
                        "WHERE " +
                        "a.device_id = '%s' " +
                        "RETURN  " +
                        "a IS NOT NULL as result";

        String query = String.format(EXISTS, id.toString());

        StatementResult result = driver.executeCypherQuery(query);

        if (result.list().isEmpty()){
           return false;
        }

        Record record = result.single();

        return record.get("result").asBoolean();

    }

    public boolean setDeviceLabel(DeviceId id, String label) throws RuntimeException {

        checkNotNull(id, "Device id cannot be null");
        checkNotNull(label, "Device label cannot be null");

        String SETLABEL =
                "MATCH (a)" +
                        "WHERE" +
                        "a.device_id = '%s'" +
                        "SET a:%s";

        String query = String.format(SETLABEL, id.toString(), label);
        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (summary.counters().labelsAdded() == 0){
            log.error("Label already added to device ({})", id.toString());
            return false;
        }
        log.info("New label was inserted to device ({})", id.toString());
        return true;
    }

    public List<String> getDeviceLabels(DeviceId id) throws RuntimeException {

        checkNotNull(id, "Device id cannot be null");

        String GETLABELS =
                "MATCH (a)" +
                        "WHERE " +
                        "a.device_id = %s" +
                        "RETURN" +
                        "labels(a) as labels";

        String query = String.format(GETLABELS, id.toString());

        StatementResult result = driver.executeCypherQuery(query);

        if (result.list().isEmpty()){
            log.error("Object id ({}) could be not exist", id.toString());
            return null;
        }

        Record record = result.single();
        return record.get("labels").asList(Value::asString);
    }
}
