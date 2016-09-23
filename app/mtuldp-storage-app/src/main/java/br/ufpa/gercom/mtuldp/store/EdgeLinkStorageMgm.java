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


import org.onosproject.net.EdgeLink;
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
                        "(a:%s {host_id:'%s'})," +
                        "(b {device_id:'%s'})" +
                        "MERGE" +
                        "(a)-[r:%s" +
                        "{" +
                        "id:%s" +
                        "host:%s" +
                        "edge:%s" +
                        "edge_port:%s" +
                        
                        "}";

    }
}
