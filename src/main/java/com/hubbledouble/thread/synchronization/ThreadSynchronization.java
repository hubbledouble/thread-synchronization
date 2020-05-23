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

package com.hubbledouble.thread.synchronization;

import com.hubbledouble.thread.synchronization.processor.ThreadProcessor;
import com.hubbledouble.thread.synchronization.repository.impl.MongoProcessRepositoryImpl;
import com.hubbledouble.thread.synchronization.service.impl.LockServiceImpl;
import com.hubbledouble.thread.synchronization.service.impl.ProcessServiceImpl;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Thread Synchronization.
 * Use this class to ensure two or more concurrent processes or threads
 * do not simultaneously execute some particular program segment known as critical section.
 *
 * <pre>
 * <code>
 *  Usage:
 *      ThreadSynchronization threadSynchronization = new ThreadSynchronization(mongoTemplate);
 *      threadSynchronization.execute("processName", () -> {
 *          String usage = "Usage";
 *          System.out.println(usage);
 *      });
 * </code>
 * </pre>
 *
 * @author Jorge Saldivar
 * @throws {@link com.hubbledouble.thread.synchronization.exception.RunnableCodeException}, {@link com.hubbledouble.thread.synchronization.exception.ThreadSynchronizationException}
 * @see <a href="https://en.wikipedia.org/wiki/Synchronization_(computer_science)#Thread_or_process_synchronization">
 * Thread Synchronization info
 * </a>
 */
public class ThreadSynchronization {

    private final ThreadProcessor threadProcessor;

    public ThreadSynchronization(MongoTemplate mongoTemplate) {
        this.threadProcessor =
                new ThreadProcessor(
                        new LockServiceImpl(
                                new ProcessServiceImpl(
                                        new MongoProcessRepositoryImpl(mongoTemplate))));
    }

    public boolean execute(String processName, RunnableCode runnableCode) {
        return threadProcessor.execute(processName, runnableCode);
    }

}