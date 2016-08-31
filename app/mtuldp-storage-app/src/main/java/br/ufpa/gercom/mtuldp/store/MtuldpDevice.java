/*
 * Copyright 2016 GERCOM, Lab. UFPA.
 *
 * Developer: fernando
 * Serial: 30/08/16 17:19
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

import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceService;

/**
 * Created by fernando on 30/08/16.
 */
public class MtuldpDevice {

    // mandatory
    private String device_id;
    // optional
    private String datapath_id;
    private String types;

    enum Operations {
        CREATE, READ, UPDATE, DELETE;
    }


    private MtuldpDevice(DeviceDao dao) {

    }

    private void createDevice(){

    }

    public static class DeviceDao {
        private String device_id;
        private String datapath_id;
        private String types;

        private Operations operations;

        private MtuldpNeo4jStore driver;

        public DeviceDao(DeviceId id) {
            this.device_id = id.uri().getHost();

        }

        public DeviceDao driver(MtuldpNeo4jStore driver) {
            this.driver = driver;
            return this;
        }

        public DeviceDao datapath_id(DeviceId id) {
            this.datapath_id = id.toString();
            return this;
        }

        public DeviceDao Operation (Operations op){
            this.operations = op
        }


    }
}
