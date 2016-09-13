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

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.onosproject.net.Host;
import org.slf4j.Logger;

import java.util.LinkedHashSet;
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
                        "mac:'%s'" +
                        "ip:%s" +
                        "vlan:'%s'" +
                        "})";



        String type = Host.class.getName();
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

        checkNotNull(device, "Device Object cannot be null");

    }
}
