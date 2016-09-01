/*
 * Copyright 2016 GERCOM, Lab. UFPA.
 *
 * Developer: fernando
 * Serial: 30/08/16 16:43
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
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class Neo4jDriver {

    protected final Logger log = getLogger(getClass());

    private Driver driver;
    private Session session;

    Neo4jDriver(String neo4j_url) {
        this.driver = GraphDatabase.driver(neo4j_url);
        this.session = driver.session();
    }

    Neo4jDriver(String neo4j_url, String user, String pass) {
        this.driver = GraphDatabase.driver(neo4j_url, AuthTokens.basic(user, pass));
        this.session = driver.session();
        if (session.isOpen()) {
            log.info("Connection with neo4j database had opened");
        }
    }

    public StatementResult doCypherQuery(String query) {
        StatementResult result = session.run(query);
        return result;
    }

    public void close(){
        session.close();
        driver.close();
        log.info("Connection wit neo4j was closed");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Neo4jDriver that = (Neo4jDriver) o;
        return Objects.equal(driver, that.driver) &&
                Objects.equal(session, that.session);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(driver, session);
    }
}
