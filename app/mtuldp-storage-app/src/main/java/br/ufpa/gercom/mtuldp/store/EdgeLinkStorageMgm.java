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


import org.apache.commons.lang.StringUtils;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.onosproject.net.*;
import org.slf4j.Logger;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;


public class EdgeLinkStorageMgm {

    private Neo4jIntegration driver;
    private final Logger log = getLogger(getClass());


    public EdgeLinkStorageMgm(Neo4jIntegration driver) {
        this.driver = driver;
    }


    public boolean create(ConnectPoint edgeLink) throws RuntimeException {

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
                        "}]-(b)";

        String id = getId(edgeLink);
        String host = edgeLink.hostId().toString();
        String edge = edgeLink.deviceId().toString();
        String edge_port = edgeLink.port().name();
        String type_link = EdgeLink.Type.EDGE.name();
        String type_edge = Device.Type.SWITCH.name();
        String type_host = Host.class.getSimpleName();

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

    public boolean delete(ConnectPoint edgeLink) throws RuntimeException {
        String DELETE =
                "MATCH ()-[r:%s {id:'%s'}]-()}" +
                        "DELETE" +
                        "r";

        String type = EdgeLink.Type.EDGE.name();
        String id = getId(edgeLink);

        String query = String.format(DELETE,type,id);

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (summary.counters().relationshipsDeleted() == 0){
            log.error("The EdgeLink ({}) cannot be deleted", id);
            return false;
        }

        log.info("The EdgeLink ({}) had deleted with sucessfully", id);
        return true;
    }

    public boolean exist (ConnectPoint edgeLink) throws RuntimeException {

        String EXIST =
                "MATCH ()-[r:%s {id:'%s'}]->()" +
                        "RETURN" +
                        "r IS NOT NULL as result";

        String id = getId(edgeLink);
        String type = EdgeLink.Type.EDGE.name();

        String query = String.format(EXIST,type,id);

        StatementResult result = driver.executeCypherQuery(query);

        if (result.list().isEmpty()){
            return false;
        }

        Record record = result.single();

        return record.get("result").asBoolean();
    }

    public boolean setEdgeLinkLabel(ConnectPoint edgeLink,String label){

         String SETLABEL =
                "MATCH ()-[r:%s{id:'%s'}]->()" +
                        "SET" +
                        "r:%s";

        String id = getId(edgeLink);
        String type = EdgeLink.Type.EDGE.name();
        String query = String.format(SETLABEL,type,id,label);

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();


        if (summary.counters().labelsAdded() == 0){
            log.error("Label already added to EdgeLink");
            return false;
        }

        log.info("New label was inserted to EdgeLink ({})",id);

        return true;
    }

    public List<String> getEdgeLinkLabels(ConnectPoint edgeLink ){
        checkNotNull(edgeLink, "EdgeLink object cannot be null");

        String GETLABELS =
                "MATCH ()-[r:%s{id:'%s'}]->()" +
                        "RETURN" +
                        "lables(r) as lables";



        String id = getId(edgeLink);
        String type = EdgeLink.Type.EDGE.name();
        String query = String.format(GETLABELS,type,id);

        StatementResult result = driver.executeCypherQuery(query);

        if (result.list().isEmpty()){
            log.error("Edgelink could be not exist");
            return null;
        }

        Record record = result.single();
        return record.get("labels").asList(Value::asString);

    }

    private String getId(ConnectPoint edgeLink){

        // id = object:type:host:edge
        String id = "%s:%s:%s:%s";

        return StringUtils.lowerCase(String.format(id,EdgeLink.class.getSimpleName(),
                EdgeLink.Type.EDGE.name(),edgeLink.hostId().toString(),
                edgeLink.deviceId().toString()));
    }
}
