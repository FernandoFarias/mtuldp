/*
 * Copyright 2016 GERCOM, Lab. UFPA.
 *
 * Developer: fernando
 * Serial: 12/09/16 10:41
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
import org.onosproject.net.Link;
import org.slf4j.Logger;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;


public class LinkStorageMgm {

    private Neo4jDriver driver;
    private Logger log = getLogger(getClass());


    public LinkStorageMgm(Neo4jDriver driver) {
        this.driver = driver;
    }

    public boolean create(Link link) throws RuntimeException {


        checkNotNull(link, "Link object cannot be null");

        String CREATE =
                "MATCH " +
                        "(a:{device_id:'%s'})," +
                        "(b:{device_id:'%s'})" +
                        "MERGE" +
                        "(a)-[r:%s" +
                        "{" +
                        "id:%s," +
                        "src:%s," +
                        "src_port:%s," +
                        "dst:%s," +
                        "dst_port:%s," +
                        "state:%s" +
                        "}]->(b)";


        String id = link.providerId().id();
        String src_id = link.src().deviceId().toString();
        String src_port = link.src().port().name();
        String dst_id = link.dst().deviceId().toString();
        String dst_port = link.dst().port().name();
        String state = link.state().name();
        String type = link.type().name();


        String query = String.format(CREATE, src_id, dst_id, type, id,
                src_id, src_port, dst_id, dst_port, state);

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (summary.counters().relationshipsCreated() == 0) {

            log.error("Link already exists or error on transaction");
            return false;
        }
        log.info("New relationship had created between ({}) -> ({})", src_id, dst_id);
        return true;
    }

    public boolean update(Link link) throws RuntimeException {

        checkNotNull(link, "Link object cannot be null");

        String UPDATE =
                "MATCH ()-[r]->() " +
                        "WHERE" +
                        "r.src = '%s'" +
                        "r.dst = '%s'" +
                        "SET" +
                        "r.id = '%s'," +
                        "r.src_port = '%s'," +
                        "r.dst_port = '%s'," +
                        "r.state = '%s'";

        String src = link.src().deviceId().toString();
        String dst = link.dst().deviceId().toString();

        String id = link.providerId().id();
        String src_port = link.src().port().name();
        String dst_port = link.dst().port().name();
        String state = link.state().name();

        String query = String.format(UPDATE, src, dst, id, src_port, dst_port, state);

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (!summary.counters().containsUpdates()) {

            log.error("The link ({}) cannot be update", link.providerId().id());
            return false;
        }

        log.info("The Link ({}) had been updated", link.providerId().id());
        return true;

    }

    public boolean delete(Link link) throws RuntimeException {

        checkNotNull(link, "Link object cannot be null");

        String DELETE =
                "MATCH ()-[r]->()" +
                        "WHERE" +
                        "src = '%s'" +
                        "dst = '%s'" +
                        "DELETE" +
                        "r";

        String src = link.src().deviceId().toString();
        String dst = link.dst().deviceId().toString();

        String query = String.format(DELETE, src, dst);

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();

        if (summary.counters().relationshipsDeleted() == 0){
            log.error("The link ({}) cannot be deleted", link.providerId().id());
            return false;
        }

        log.info("The link ({}) had deleted with sucessfully");
        return true;
    }

    public boolean exist(Link link) throws RuntimeException{

        checkNotNull(link, "Link object cannot be null");

        String EXIST =
                "MATCH ()-[r]->()" +
                        "WHERE" +
                        "r.src = '%s'" +
                        "r.dst = '%s'" +
                        "RETURN" +
                        "a IS NOT NULL as result";


        String src = link.src().deviceId().toString();
        String dst = link.dst().deviceId().toString();

        String query = String.format(EXIST,src,dst);

        StatementResult result = driver.executeCypherQuery(query);

        if (result.list().isEmpty()){
            return false;
        }

        Record record = result.single();

        return record.get("result").asBoolean();
    }

    public boolean setLinkLabel(Link link, String label){

        checkNotNull(link, "Link object cannot be null");
        checkNotNull(label, "Label name cannot be null");

        String SETLABEL =
                "MATCH ()-[r]->()" +
                        "WHERE" +
                        "r.src = '%s'" +
                        "r.dst = '%s'" +
                        "SET" +
                        "r:%s";


        String src = link.src().deviceId().toString();
        String dst = link.dst().deviceId().toString();

        String query = String.format(SETLABEL, src, dst, label);

        StatementResult result = driver.executeCypherQuery(query);
        ResultSummary summary = result.consume();


        if (summary.counters().labelsAdded() == 0){
            log.error("Label already added to link ({})->({})", src,dst);
            return false;
        }

        log.info("New label was inserted to link ({})->({})",src,dst);
        return true;
    }

    public List<String> getLinkLabels(Link link){
        checkNotNull(link, "Link object cannot be null");

        String GETLABELS =
                "MATCH ()-[r]->()" +
                        "WHERE" +
                        "r.src = '%s'" +
                        "r.dst = '%s'" +
                        "RETURN" +
                        "lables(r) as lables";

        String src = link.src().deviceId().toString();
        String dst = link.dst().deviceId().toString();

        String query = String.format(GETLABELS,src,dst);

        StatementResult result = driver.executeCypherQuery(query);

        if (result.list().isEmpty()){
            log.error("link could be not exist");
            return null;
        }

        Record record = result.single();
        return record.get("labels").asList(Value::asString);

    }
}
