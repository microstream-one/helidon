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
