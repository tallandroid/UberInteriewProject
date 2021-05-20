import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractIdleService;

public class SchedulerServiceImpl extends AbstractIdleService implements SchedulerService {

    private final Map<String, Integer> fixedRequests;
    private final Map<String, Integer> scheduledRequests;
    private final Map<String, Integer> recurrentRequests;
    private final Map<String, Task> taskScheduled;
    private final ExecutorService service;
    private final Integer defaultDelaySecs;
    private final AtomicInteger startupTime;

    public SchedulerServiceImpl(Integer numWorkerThreads, Integer defaultDelay) {
        service = Executors.newFixedThreadPool(numWorkerThreads);
        fixedRequests = Maps.newConcurrentMap();
        scheduledRequests = Maps.newConcurrentMap();
        recurrentRequests = Maps.newConcurrentMap();
        taskScheduled = Maps.newConcurrentMap();
        defaultDelaySecs = defaultDelay;
        startupTime = new AtomicInteger(0);
    }

    @Override
    public Task submitTask(String request) {
        Task<String> scheduledTask = new SampleTask(request);
        fixedRequests.put(scheduledTask.getRequestid(), startupTime.intValue() + defaultDelaySecs);
        taskScheduled.put(scheduledTask.getRequestid(), scheduledTask);
        return scheduledTask;
    }

    @Override
    public Task submitScheduledTask(long delay, String request) {
        Task<String> scheduledTask = new SampleTask(request);
        scheduledRequests.put(scheduledTask.getRequestid(), startupTime.intValue() + defaultDelaySecs);
        taskScheduled.put(scheduledTask.getRequestid(), scheduledTask);
        return scheduledTask;
    }

    @Override
    public Task submitRecurrentTask(long recurrentIntervalTask, String request) {
        Task<String> scheduledTask = new SampleTask(request);
        recurrentRequests.put(scheduledTask.getRequestid(), startupTime.intValue() + defaultDelaySecs);
        taskScheduled.put(scheduledTask.getRequestid(), scheduledTask);
        return scheduledTask;
    }

    @Override
    protected void startUp() throws Exception {
        while(true) {
            fixedRequests.entrySet().stream().forEach(entry -> {
                if(startupTime.intValue() == (entry.getValue())){
                   Future<?> task = service.submit(taskScheduled.get(entry.getKey()));
                   taskScheduled.remove(entry.getKey());
                }
            });

            scheduledRequests.entrySet().stream().forEach(entry -> {
                if(startupTime.intValue() == (entry.getValue())) {
                    Future<?> task = service.submit(taskScheduled.get(entry.getKey()));
                    recurrentRequests.put(entry.getKey(), scheduledRequests.get(entry.getKey()) + startupTime.intValue());
                }
            });

            recurrentRequests.entrySet().stream().forEach(entry -> {
                if(startupTime.intValue() == (entry.getValue())){
                    Future<?> task = service.submit(taskScheduled.get(entry.getKey()));
                    recurrentRequests.put(entry.getKey(), recurrentRequests.get(entry.getKey()) + startupTime.intValue());
                }
            });

            Thread.sleep(1000);
            System.out.println("CurrentTime" + startupTime.toString());
            startupTime.incrementAndGet();
        }
    }

    @Override
    protected void shutDown() throws Exception {

    }
}
