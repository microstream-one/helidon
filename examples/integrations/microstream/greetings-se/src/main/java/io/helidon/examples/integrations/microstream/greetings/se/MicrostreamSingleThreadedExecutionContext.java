package io.helidon.examples.integrations.microstream.greetings.se;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import io.helidon.common.reactive.Single;
import one.microstream.reference.LazyReferenceManager;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

/**
 *
 * The MicrostreamSingleThreadedExecutionContext provides a very simply way to ensure
 * thread safe access to a Microstream storage and it's associated data.
 *
 * This example just uses a single-treaded ExecutorService to avoid the need to manually synchronize
 * any multi-threaded access to the storage and the user provided object-graph.
 *
 */
public class MicrostreamSingleThreadedExecutionContext {

    private EmbeddedStorageManager storage;
    private ExecutorService executor;

    public MicrostreamSingleThreadedExecutionContext(EmbeddedStorageManager storageManager) {
        super();
        this.storage = storageManager;
        executor = Executors.newSingleThreadExecutor();
    }

    public EmbeddedStorageManager storageManager() {
        return storage;
    }

    public  ExecutorService executor() {
        return executor;
    }

    /**
     * Start the storage
     */
    public Single<EmbeddedStorageManager> start() {
        CompletableFuture<EmbeddedStorageManager> completableFuture = CompletableFuture.supplyAsync(()->storage.start(), executor);
        return Single.create(completableFuture);
    }

    /**
     * Shutdown the storage
     */
    public Single<EmbeddedStorageManager> shutdown() {
        CompletableFuture<EmbeddedStorageManager> completableFuture = CompletableFuture.supplyAsync(
                ()->{
                    storage.shutdown();
                    LazyReferenceManager.get().stop();
                    executor.shutdown();
                    return storage;
                }
                , executor);

        return Single.create(completableFuture);
    }

    /**
     * Return the persistent object graph's root object
     *
     * @param <T>
     * @return a Single containing the graph's root object casted to <T>
     */
    public <T> Single<T> root() {
        @SuppressWarnings("unchecked")
        CompletableFuture<T> completableFuture = CompletableFuture.supplyAsync(()->{return (T)storage.root();}, executor);
        return (Single<T>)Single.create(completableFuture);
    }

    /**
     * Sets the passed instance as the new root for the persistent object graph
     *
     * @param object the new root object
     * @return Single containing the new root object
     */
    public Single<Object> setRoot(Object object) {
        CompletableFuture<Object> completableFuture = CompletableFuture.supplyAsync(()->{return storage.setRoot(object);}, executor);
        return Single.create(completableFuture);
    }

    /**
     * Stores the registered root instance
     *
     * @return Single containing the root instance's objectId.
     */
    public Single<Long> storeRoot() {
        CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(()->{return storage.storeRoot();}, executor);
        return Single.create(completableFuture);
    }

    /**
     * Stores the passed object
     *
     * @param object
     * @return Single containing the object id representing the passed instance.
     */
    public Single<Long> store(Object object) {
        CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(()->{return storage.store(object);}, executor);
        return Single.create(completableFuture);
    }

    /**
     * Creates a new CompletableFuture that executes in this context
     *
     * @param <R> the return type
     * @param supplier a function returning the value to be used to complete the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public <R> CompletableFuture<R> execute(Supplier<R> supplier) {
        CompletableFuture<R> completableFuture = CompletableFuture.supplyAsync(()->{return supplier.get();}, executor);
        return completableFuture;
    }
}
