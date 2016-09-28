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

import java.util.List;

/**
 * Created by fernando on 01/09/16.
 */
public interface MtuldpStoreService<T,S> {


    boolean create(T t);

    boolean delete(T t);

    boolean update (T t);

    boolean exist (T t);

    void setLabel(T t, S s);

    List<S> getLabel(T t);

}
