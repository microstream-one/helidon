/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
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

package io.helidon.examples.integrations.microstream.greetings.mp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.helidon.integrations.microstream.cdi.MicrostreamStorage;

import one.microstream.storage.embedded.types.EmbeddedStorageManager;


@ApplicationScoped
public class GreetingProvider  {

    private EmbeddedStorageManager storage;
    private List<String> greetingMessages;
    private Random rnd = new Random();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    @SuppressWarnings("unchecked")
    @Inject
    public GreetingProvider(@MicrostreamStorage(configNode = "one.microstream.storage.greetings")EmbeddedStorageManager storage) {
        super();
        this.storage = storage;

        //Initialize storage if empty or load greetings
        if((greetingMessages = (List<String>)storage.root()) == null)
        {
            greetingMessages = new ArrayList<String>();
            storage.setRoot(greetingMessages);
            storage.storeRoot();
            addGreeting("Hello");
        }
    }

    public void addGreeting(String newGreeting) {
        try {
            lock.writeLock().lock();
            greetingMessages.add(newGreeting);
            storage.store(greetingMessages);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Object getGreeting() {
        try {
            lock.readLock().lock();
            return greetingMessages.get(rnd.nextInt(greetingMessages.size()));
        } finally {
            lock.readLock().unlock();
        }
    }

}
