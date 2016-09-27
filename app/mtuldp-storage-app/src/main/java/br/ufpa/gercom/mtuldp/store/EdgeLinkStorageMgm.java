/*
 * Copyright 2016 GERCOM, Lab. UFPA.
 *
 * Developer: fernando
 * Serial: 23/09/16 14:50
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


import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.onosproject.net.Device;
import org.onosproject.net.EdgeLink;
import org.onosproject.net.Host;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;


public class EdgeLinkStorageMgm {

    private Neo4jIntegration driver;
    private final Logger log = getLogger(getClass());


    public EdgeLinkStorageMgm(Neo4jIntegration driver) {
        this.driver = driver;
    }


    public boolean create(EdgeLink edgeLink) throws RuntimeException {

        checkNotNull(edgeLink,"EdgeLink object cannot be null" );

        String CREATE =
                "MATCH " +
                        "(a: %s {host_id:'%s'})," +
                        "(b: %s {device_id:'%s'})" +
                        "MERGE" +
                        "(a)-[r:%s" +
                        "{" +
                        "id:'%s'," +
                        "host:'%s'," +
                        "edge:'%s'," +
                        "edge_port:'%s'" +
                        "state: '%s'" +
                        "}]-(b)";

        String id = getId(edgeLink);
        String host = edgeLink.hostId().toString();
        String edge = edgeLink.hostLocation().deviceId().toString();
        String edge_port = edgeLink.hostLocation().port().name();
        String type_link = EdgeLink.Type.EDGE.name();
        String type_edge = Device.Type.SWITCH.name();
        String type_host = Host.class.getSimpleName();
        String state = edgeLink.state().name();

        String query =  String.format(CREATE,type_host,host,type_edge,
                edge,type_link,id,host,edge,edge_port);

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (summary.counters().relationshipsCreated() == 0){

            log.error("EdgeLink already exists or error on transaction");
            return false;
        }

        log.info("New edge link had created between host ({}) -> edge ({})", host, edge);
        return true;
    }

    public boolean update (EdgeLink edgeLink) throws RuntimeException {

        String UPDATE =
                "MATCH ()-[r:%s {id:'%s'}]-()" +
                        "SET" +
                        "edge_port:'%s'" +
                        "state: '%s'";

        String type = EdgeLink.Type.EDGE.name();
        String id = getId(edgeLink);
        String edge_port = edgeLink.hostLocation().port().name();
        String state = edgeLink.state().name();

        String query = String.format(UPDATE, type,id,edge_port,state);

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (!summary.counters().containsUpdates()){
            log.error("The EdgeLink ({}) cannot be update or not found", id);
            return false;
        }

        log.info("The EdgeLink ({}) had been updated", id);
        return true;
    }

    public boolean delete() throws RuntimeException {
        String DELETE =
                "a";
    }

    private String getId(EdgeLink edgeLink){

        // id = object:type:host:edge
        String id = "%s:%s:%s:%s";

        return StringUtils.lowerCase(String.format(id,EdgeLink.class.getSimpleName(),
                EdgeLink.Type.EDGE.name(),edgeLink.hostId().toString(),
                edgeLink.hostLocation().deviceId().toString()));
    }
}
