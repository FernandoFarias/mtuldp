/*
 * Copyright 2016 GERCOM, Lab. UFPA.
 *
 * Developer: fernando
 * Serial: 01/09/16 15:56
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
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.Device;
import org.onosproject.net.Host;
import org.onosproject.net.Link;

import java.util.List;

/**
 * Created by fernando on 01/09/16.
 */
public interface MtuldpStorageService {


    void create(Device device);
    void create(Link link);
    void create(Host host);
    void create(ConnectPoint edgelink);

    void delete(Device device);
    void delete(Link link);
    void delete(Host host);
    void delete(ConnectPoint edgelink);

    void update (Device device);
    void update (Link link);
    void update (Host host);

    boolean exist (Device device);
    boolean exist (Link link);
    boolean exist (Host host);
    boolean exist (ConnectPoint edgelink);


    void setLabel(Device device, String s);
    void setLabel(Link link, String s);
    void setLabel(Host host, String s);
    void setLabel(ConnectPoint edgelink, String s);

    List<String> getLabels(Device device);
    List<String> getLabels(Link link);
    List<String> getLabels(Host host);
    List<String> getLabels(ConnectPoint edgelink);

    StatementResult runCypherQuery(String query);

}
