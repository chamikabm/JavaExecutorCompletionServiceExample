import java.util.concurrent.*;

public class RetryableAsyncExecutor {

    private final ExecutorService executorService;
    private final CompletionService<RetryableAsyncCouchDocFetchTask> completionService;

    public RetryableAsyncExecutor() {
        executorService = Executors.newCachedThreadPool();
        completionService = new ExecutorCompletionService<>(executorService);
    }

    public void submit( RetryableAsyncCouchDocFetchTask task ) {
        completionService.submit( task );
    }

    public RetryableAsyncCouchDocFetchTask get() throws ExecutionException, InterruptedException {
        final Future<RetryableAsyncCouchDocFetchTask> future = completionService.take();
        final RetryableAsyncCouchDocFetchTask task = future.get();
        if( task.isRetryableException()) {
            completionService.submit( task );
        }

        return task;
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
