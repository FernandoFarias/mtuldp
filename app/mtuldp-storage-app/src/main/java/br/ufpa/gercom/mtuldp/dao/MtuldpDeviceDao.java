/*
 * Copyright 2016 GERCOM, Lab. UFPA.
 *
 * Developer: fernando
 * Serial: 30/08/16 16:56
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

package br.ufpa.gercom.mtuldp.dao;

import br.ufpa.gercom.mtuldp.store.MtuldpNeo4jStore;
import org.onosproject.net.Device;

public class MtuldpDeviceDao {

    private MtuldpNeo4jStore driver;
    private Device device;

    private enum Types {
        SWITCH, ROUTER, ROUTER_WIFI;
    }

    public MtuldpDeviceDao(MtuldpNeo4jStore driver, Device device) {
        this.driver = driver;
        this.device = device;
    }
}
