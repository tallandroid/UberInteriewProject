public interface  SchedulerService {

    Task submitTask(String request);

    Task submitScheduledTask(long delay, String request);

    Task submitRecurrentTask(long recurrentIntervalTask, String request);

}
