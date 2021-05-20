import java.util.UUID;

public class SampleTask implements Task<String> {

    private final String request;
    private final String requestId;

    public SampleTask(String request) {
        this.request = request;
        this.requestId = UUID.randomUUID().toString();
    }

    @Override
    public String getRequestid() {
        return this.requestId;
    }

    @Override
    public void performRequest(String request) {
        System.out.println(request);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        this.performRequest(request);
    }
}
