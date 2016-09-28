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
import org.onosproject.net.host.HostService;
import org.onosproject.net.link.LinkService;
import org.slf4j.Logger;

import static javaslang.API.*;
import static org.slf4j.LoggerFactory.getLogger;
import static org.onosproject.net.device.DeviceEvent.Type.*;

@Component(immediate = true)
public class MtuldpStoreManager  {


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

    @Activate
    public void activate(){

        driver = new Neo4jIntegration("bolt://neo4j", "neo4j", "admin");

        edstore = new EdgeLinkStorageMgm(driver);
        lstore = new LinkStorageMgm(driver);
        hstore = new HostStorageMgm(driver);
        dstore = new DeviceStorageMgm(driver);

        appid = coreService.registerApplication("br.ufpa.gercom.mtuldp.store");

        deviceService.addListener(deviceListener);

        log.info("Started");

    }

    @Deactivate
    public void deactivate(){

        driver.close();
        log.info("Stopped");
    }


    private class InnerDeviceListener implements DeviceListener {

        @Override
        public void event(DeviceEvent event) {
            Match(event.type()).of(
                    Case($(DEVICE_ADDED),dstore.create(event.subject())),
                    Case($(DEVICE_REMOVED), dstore.delete(event.subject())),
                    Case($(DEVICE_UPDATED), dstore.update(event.subject()))
            );
        }
    }

}

