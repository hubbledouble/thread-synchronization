/*
 *    Copyright (c) 2020, HubbleDouble
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.hubbledouble.thread.synchronization.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * Collection to store thread process status
 *
 * @author Jorge Saldivar
 */
@Document(collection = "hubbleDoubleProcess")
public class Process {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String processName;
    private String processSourceId;
    private String version;
    private Instant heartbeat;

    public Process() {
    }

    public Process(String processName, String processSourceId) {
        this.processName = processName;
        this.processSourceId = processSourceId;
        setVersion();
        setHeartbeat();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessSourceId() {
        return processSourceId;
    }

    public void setProcessSourceId(String processSourceId) {
        this.processSourceId = processSourceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setVersion() {
        this.version = UUID.randomUUID().toString();
    }

    public Instant getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(Instant heartbeat) {
        this.heartbeat = heartbeat;
    }

    public void setHeartbeat() {
        this.heartbeat = Instant.now();
    }

}
