/*
 * Copyright 2016-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.ufpa.gercom.mtuldp.store;

import org.apache.felix.scr.annotations.*;
import org.neo4j.driver.v1.StatementResult;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.Device;
import org.onosproject.net.Host;
import org.onosproject.net.Link;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Component(immediate = true)
@Service
public class MtuldpStorageManager implements MtuldpStorageService {


    private final Logger log = getLogger(getClass());

    private static final String APP_NAME = "br.ufpa.gercom.mtuldp.store";

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private CoreService coreService;

    private Neo4jIntegration driver;
    private EdgeLinkStorageMgm edstore;
    private LinkStorageMgm lstore;
    private HostStorageMgm hstore;
    private DeviceStorageMgm dstore;

    private ApplicationId appid;

    @Activate
    public void activate() {

        appid = coreService.registerApplication(APP_NAME);
        driver = new Neo4jIntegration("bolt://neo4j", "neo4j", "admin");
        edstore = new EdgeLinkStorageMgm(driver);
        lstore = new LinkStorageMgm(driver);
        hstore = new HostStorageMgm(driver);
        dstore = new DeviceStorageMgm(driver);

        log.info("Started");

    }

    @Deactivate
    public void deactivate() {

        driver.close();
        log.info("Stopped");
    }

    @Override
    public void create(Device device) {
        if (!dstore.create(device))
            throw new RuntimeException("Cannot create device");
    }

    @Override
    public void create(Link link) {
        if (!lstore.create(link))
            throw new RuntimeException("Cannot create link");
    }

    @Override
    public void create(Host host) {
        if (!hstore.create(host))
            throw new RuntimeException("Cannot create host");
    }

    @Override
    public void create(ConnectPoint edgelink) {
        if (!edstore.create(edgelink))
            throw new RuntimeException("Cannot create edge link");
    }

    @Override
    public void delete(Device device) {
        if (!dstore.delete(device))
            throw new RuntimeException("Cannot delete device");
    }

    @Override
    public void delete(Link link) {
        if (!lstore.delete(link))
            throw new RuntimeException("Cannot delete link");
    }

    @Override
    public void delete(Host host) {
        if (!hstore.delete(host))
            throw new RuntimeException("Cannot delete host");
    }

    @Override
    public void delete(ConnectPoint edgelink) {
        if (!edstore.delete(edgelink))
            throw new RuntimeException("Cannot delete edge link");
    }

    @Override
    public void update(Device device) {
        if (!dstore.update(device))
            throw new RuntimeException("Cannot update device");
    }

    @Override
    public void update(Link link) {
        if (!lstore.update(link))
            throw new RuntimeException("Cannot update link");
    }

    @Override
    public void update(Host host) {
        if (!hstore.update(host))
            throw new RuntimeException("Cannot update host");
    }

    @Override
    public boolean exist(Device device) {
        return dstore.exists(device);
    }

    @Override
    public boolean exist(Link link) {
        return lstore.exist(link);
    }

    @Override
    public boolean exist(Host host) {
        return hstore.exist(host);
    }

    @Override
    public boolean exist(ConnectPoint edgelink) {
        return edstore.exist(edgelink);
    }

    @Override
    public void setLabel(Device device, String s) {
        dstore.setDeviceLabel(device, s);
    }

    @Override
    public void setLabel(Link link, String s) {
        lstore.setLinkLabel(link, s);
    }

    @Override
    public void setLabel(Host host, String s) {
        hstore.setHostLabel(host, s);
    }

    @Override
    public void setLabel(ConnectPoint edgelink, String s) {
        edstore.setEdgeLinkLabel(edgelink, s);
    }

    @Override
    public List<String> getLabels(Device device) {
        return dstore.getDeviceLabels(device);
    }

    @Override
    public List<String> getLabels(Link link) {
        return lstore.getLinkLabels(link);
    }

    @Override
    public List<String> getLabels(Host host) {
        return hstore.getHostLabels(host);
    }

    @Override
    public List<String> getLabels(ConnectPoint edgelink) {
        return edstore.getEdgeLinkLabels(edgelink);
    }

    @Override
    public StatementResult runCypherQuery(String query) {
        return driver.executeCypherQuery(query);
    }
}

