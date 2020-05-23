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

package com.hubbledouble.thread.synchronization.repository.impl;

import com.hubbledouble.thread.synchronization.domain.Process;
import com.hubbledouble.thread.synchronization.repository.ProcessRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Instant;

/**
 * Operations used internally by this library to interact with mongo database
 *
 * @author Jorge Saldivar
 */
public class MongoProcessRepositoryImpl implements ProcessRepository {

    private static final String FIELD_ID = "_id";
    private static final String FIELD_PROCESS_NAME = "processName";
    private static final String FIELD_PROCESS_SOURCE_ID = "processSourceId";
    private static final String FIELD_VERSION = "version";
    private static final String FIELD_HEARTBEAT = "heartbeat";
    private final MongoOperations mongoOperations;

    public MongoProcessRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
        mongoOperations.indexOps(Process.class).ensureIndex(new Index().on(FIELD_PROCESS_NAME, Sort.Direction.ASC).unique());
    }

    @Override
    public Process findByProcessName(String processName) {

        Query find = new Query();
        find.addCriteria(Criteria.where(FIELD_PROCESS_NAME).is(processName));
        return mongoOperations.findOne(find, Process.class);

    }

    @Override
    public Process findByProcessNameAndProcessSourceId(String processName, String processSourceId) {

        Query find = new Query();
        find
                .addCriteria(Criteria.where(FIELD_PROCESS_NAME).is(processName))
                .addCriteria(Criteria.where(FIELD_PROCESS_SOURCE_ID).is(processSourceId));
        return mongoOperations.findOne(find, Process.class);

    }

    @Override
    public boolean insertLock(String processName, String processSourceId) {

        try {
            mongoOperations.insert(new Process(processName, processSourceId));
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }

    }

    @Override
    public boolean transferLock(Process process, String processSourceId) {

        Query find = new Query();
        find
                .addCriteria(Criteria.where(FIELD_ID).is(process.getId()))
                .addCriteria(Criteria.where(FIELD_PROCESS_NAME).is(process.getProcessName()))
                .addCriteria(Criteria.where(FIELD_VERSION).is(process.getVersion()));

        Update update = createUpdate(process, processSourceId);

        try {
            UpdateResult updateResult = mongoOperations.upsert(find, update, Process.class);
            return null != updateResult && updateResult.getMatchedCount() > 0;
        } catch (DuplicateKeyException e) {
            return false;
        }

    }

    @Override
    public void performHeartBeat(Process process) {

        Query find = new Query();
        find
                .addCriteria(Criteria.where(FIELD_ID).is(process.getId()))
                .addCriteria(Criteria.where(FIELD_PROCESS_NAME).is(process.getProcessName()))
                .addCriteria(Criteria.where(FIELD_VERSION).is(process.getVersion()));

        Update update = createUpdate(process, process.getProcessSourceId());
        mongoOperations.upsert(find, update, Process.class);

    }

    @Override
    public void delete(Process process) {
        mongoOperations.remove(process);
    }

    @Override
    public void deleteProcessOlderThan(Instant date) {

        Criteria criteria = new Criteria();
        criteria.orOperator(
                Criteria.where(FIELD_HEARTBEAT).lte(date),
                Criteria.where(FIELD_HEARTBEAT).exists(false));

        Query find = new Query(criteria);
        mongoOperations.findAllAndRemove(find, Process.class);

    }

    private Update createUpdate(Process process, String processSourceId) {

        process.setProcessSourceId(processSourceId);
        process.setHeartbeat();
        process.setVersion();

        BasicDBObject basicDBObject = new BasicDBObject();
        mongoOperations.getConverter().write(process, basicDBObject);
        BasicDBObject updateDoc = new BasicDBObject("$set", basicDBObject);

        return Update.fromDocument(new Document(updateDoc.toMap()));

    }

}