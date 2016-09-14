/*
 * Copyright 2016 GERCOM, Lab. UFPA.
 *
 * Developer: fernando
 * Serial: 13/09/16 12:05
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

import com.google.common.base.Objects;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.slf4j.Logger;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

public class HostStorageMgm {

    private Neo4jDriver driver;
    private final Logger log = getLogger(getClass());


    public HostStorageMgm(Neo4jDriver driver) {
        this.driver = driver;
    }

    public boolean create(Host host) throws RuntimeException {

        checkNotNull(host, "Host Object cannot be null");

        String CREATE =
                "MERGE" +
                        "(a:%s" +
                        "{" +
                        "host_id:'%s'," +
                        "mac:'%s'," +
                        "ip:%s," +
                        "vlan:'%s'" +
                        "})";



        String type = Host.class.getSimpleName();
        String host_id = host.id().toString();
        String mac =  host.mac().toString();
        String vlan = host.vlan().toString();
        Set<String> ip  = new LinkedHashSet<>();

        host.ipAddresses().forEach(ipAddress -> {
            ip.add("\'"+ipAddress.toString()+"\'");
        });


        String query = String.format(CREATE,type,host_id,mac,ip.toString(),vlan);


        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (summary.counters().nodesCreated() == 0){
            log.error("Host id ({}) cannot be create by host already exists or error transaction", host_id);
            return false;
        }

        log.info("Host id ({}) had created with sucessfully", host_id);
        return true;
    }


    public boolean update (Host host) throws RuntimeException {

        checkNotNull(host, "Host Object cannot be null");

        String UPDATE =
                "MATCH" +
                        "(a:%s)" +
                        "WHERE" +
                        "a.host_id = '%s'" +
                        "SET" +
                        "a.mac = '%s'," +
                        "a.vlan = '%s'," +
                        "a.ip = %s";

        String type = Host.class.getSimpleName();
        String id = host.id().toString();
        String mac = host.mac().toString();
        String vlan = host.vlan().toString();
        Set<String> ip = new LinkedHashSet<>();

        host.ipAddresses().forEach(ipAddress -> {
            ip.add("\'"+ipAddress.toString()+"\'");
        });

        String query = String.format(UPDATE,type,id,mac,vlan,ip.toString());

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (!summary.counters().containsUpdates()){
            log.error("Host id ({}) cannot be updated, data is not different or transaction error");
            return false;
        }

        log.info("Host id ({}) was updated", id);
        return true;
    }

    public boolean delete(Host host) throws RuntimeException{
        checkNotNull(host, "Host Object cannot be null");

        String DELETE =
                "MATCH (a:%s)" +
                        "WHERE" +
                        "a.host_id = '%s'" +
                        "DELETE" +
                        "a";

        String type = Host.class.getSimpleName();
        String host_id = host.id().toString();

        String query = String.format(DELETE, type,host_id);

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (summary.counters().nodesDeleted() == 0){
            log.error("Host id ({}) cannot be updated, host not exist or transaction error", host_id);
            return false;
        }

        log.info("Host id ({}) was deleted", host_id);
        return true;
    }

    public boolean exist(HostId id) throws RuntimeException{

        checkNotNull(id, "Host id Object cannot be null");

        String EXIST =
                "MATCH (a:%s)" +
                        "WHERE" +
                        "a.host_id = '%s'" +
                        "RETURN " +
                        "a IS NOT NULL as result";

        String type = Host.class.getSimpleName();

        String query = String.format(EXIST, type, id.toString());

        StatementResult result = driver.executeCypherQuery(query);


        if (result.list().isEmpty()){
            return false;
        }

        Record record = result.single();
        return Boolean.getBoolean(record.get("result").asString());
    }

    public boolean setHostLabel (HostId id,String label) throws RuntimeException{

        checkNotNull(id, "Host id cannot be null");
        checkNotNull(label, "Host label cannot be null");


        String SETLABEL =
                "MATCH (a:%s)" +
                        "WHERE" +
                        "a.host_id = '%s'" +
                        "SET a:%s";


        String type = Host.class.getSimpleName();

        String query = String.format(SETLABEL, type, id.toString(), label);

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (summary.counters().labelsAdded() == 0){
            log.error("Label already added to host ({})", id.toString());
            return false;
        }

        log.info("New label was inserted to Host ({})", id.toString());
        return true;

    }

    public List<String> getHostLabels(HostId id) throws RuntimeException {

        checkNotNull(id, "Host id cannot be null");

        String GETLABEL =
                "MATCH (a:%s)" +
                        "WHERE" +
                        "a.host_id = '%s'" +
                        "RETURN" +
                        "labels(a) as labels";


        String type = Host.class.getSimpleName();

        String query = String.format(GETLABEL,type,id.toString());

        StatementResult result = driver.executeCypherQuery(query);

        if (result.list().isEmpty()){
            log.error("Object id ({}) could be not exist", id.toString());
            return null;
        }

        Record record = result.single();
        return record.get("labels").asList(Value::asString);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HostStorageMgm that = (HostStorageMgm) o;
        return Objects.equal(driver, that.driver);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(driver);
    }
}















