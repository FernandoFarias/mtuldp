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
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.edge.EdgePortService;
import org.onosproject.net.host.HostEvent;
import org.onosproject.net.host.HostListener;
import org.onosproject.net.host.HostService;
import org.onosproject.net.link.LinkEvent;
import org.onosproject.net.link.LinkListener;
import org.onosproject.net.link.LinkService;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

@Component(immediate = true)
public class MtuldpStoreManager {


    private final Logger log = getLogger(getClass());


    /*
     * Injections objects
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private LinkService linkService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private EdgePortService edgePortService;

    /*
    * Project Objects
    * */

    private Neo4jIntegration driver;
    private EdgeLinkStorageMgm edstore;
    private LinkStorageMgm lstore;
    private HostStorageMgm hstore;
    private DeviceStorageMgm dstore;

    /*
     * Framework Objects
     */
    private ApplicationId appid;

    /*
     * Internal Objects
     */

    private final InnerDeviceListener deviceListener = new InnerDeviceListener();
    private final InnerLinkListener linkListener = new InnerLinkListener();

    @Activate
    public void activate(){

        appid = coreService.registerApplication("br.ufpa.gercom.mtuldp.store");

        driver = new Neo4jIntegration("bolt://neo4j", "neo4j", "admin");

        // Services
        edstore = new EdgeLinkStorageMgm(driver);
        lstore = new LinkStorageMgm(driver);
        hstore = new HostStorageMgm(driver);
        dstore = new DeviceStorageMgm(driver);


        // Listeners
        deviceService.addListener(deviceListener);
        linkService.addListener(linkListener);

        log.info("Started");



    }

    @Deactivate
    public void deactivate(){

        deviceService.removeListener(deviceListener);
        driver.close();
        log.info("Stopped");
    }


    private class InnerDeviceListener implements DeviceListener {

        @Override
        public void event(DeviceEvent event) {
            switch (event.type()){
                case DEVICE_ADDED:
                    dstore.create(event.subject());
                    break;
                case DEVICE_REMOVED:
                    dstore.delete(event.subject());
                    break;
                case DEVICE_UPDATED:
                    dstore.update(event.subject());
                    break;
                case PORT_STATS_UPDATED:
                    break;
                default:
                    log.info("Event ({}) was not implemented", event.type().toString());
            }
        }
    }

    private class InnerLinkListener implements LinkListener {
        @Override
        public void event(LinkEvent event) {

            switch (event.type()){
                case LINK_ADDED:
                    lstore.create(event.subject());
                    break;
                case LINK_UPDATED:
                    lstore.update(event.subject());
                    break;
                case LINK_REMOVED:
                    lstore.delete(event.subject());
                    break;
                default:
                    log.info("Event ({}) was not implemented", event.type().toString());
            }
        }
    }
    private class InnerHostListener implements HostListener {
        @Override
        public void event(HostEvent event) {
            switch (event.type()){
                case HOST_ADDED:
                    hstore.create(event.subject());
                    break;
                case HOST_MOVED:
                    break;
                case HOST_REMOVED:
                    hstore.delete(event.subject());
                    break;
                case HOST_UPDATED:
                    hstore.update(event.subject());
                    break;
                default:
                    log.info("Event ({}) was not implemented", event.type().toString());

            }
        }
    }
    

}

