/*
 * Copyright 2016 GERCOM, Lab. UFPA.
 *
 * Developer: fernando
 * Serial: 27/10/16 15:43
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

package br.ufpa.gercom.mtuldp.discovery;

import br.ufpa.gercom.mtuldp.store.MtuldpStorageService;
import org.apache.felix.scr.annotations.*;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.Device;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.edge.EdgePortService;
import org.onosproject.net.host.HostService;
import org.onosproject.net.link.LinkService;
import org.slf4j.Logger;

import java.util.concurrent.Executors;

import static org.slf4j.LoggerFactory.getLogger;

@Component(immediate = true)
public class MtuldpDiscoveryManager {

    private final Logger log = getLogger(getClass());

    private static final String APP_NAME = "br.ufpa.gercom.mtuldp.discovery";

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private LinkService linkService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private EdgePortService edgePortService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private MtuldpStorageService mtuldpStorageService;

    private final InnerDeviceListener innerDeviceListener = new InnerDeviceListener();

    private ApplicationId applicationId;

    @Activate
    public void activate() {

        applicationId = coreService.registerApplication(APP_NAME);

        deviceService.addListener(innerDeviceListener);

    }

    @Deactivate
    public void deactivate() {

        deviceService.removeListener(innerDeviceListener);

    }

    private class InnerDeviceListener implements DeviceListener {

        @Override
        public void event(DeviceEvent deviceEvent) {
            switch (deviceEvent.type()){
                case DEVICE_ADDED:

                    Device added = deviceEvent.subject();
                    mtuldpStorageService.create(added);
                    log.info("adding");
                    break;
                case DEVICE_UPDATED:
                    Device updated = deviceEvent.subject();
                    mtuldpStorageService.update(updated);
                    log.info("updating");
                    break;
                default:
                    log.info("Nothing to do");
            }

        }
    }




}
