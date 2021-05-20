public class Main {

    public static void main(String[] args) {

        final SchedulerService service = new SchedulerServiceImpl(5, 5);
        Task task1 = service.submitRecurrentTask(10, "recurrent");
        Task task2 = service.submitTask("default");
        Task task3 = service.submitScheduledTask(10, "scheduled");
        try {
            ((SchedulerServiceImpl) service).startUp();
        } catch(Exception e){
            System.out.println(e);
        }
    }
}
