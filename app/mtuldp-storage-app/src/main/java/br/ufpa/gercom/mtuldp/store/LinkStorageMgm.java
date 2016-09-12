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


import org.onosproject.net.Link;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;


public class LinkStorageMgm {

    private Neo4jDriver driver;
    private Logger log = getLogger(getClass());


    public LinkStorageMgm(Neo4jDriver driver) {
        this.driver = driver;
    }

    public boolean create(Link link) {


        String CREATE =
                "MATCH " +
                        "(a:%s{device_id:'%s'})," +
                        "(b:%s{device_id:'%s'}" +
                        "MERGE" +
                        "(a)-[r:%s{}] ";


        String id = link.providerId().id();
        String src_id = link.src().deviceId().toString();
        String dst_id = link.dst().deviceId().toString();
        String state = link.state().name();
        String type = link.type().name();




        return false;
    }





            ;
}
