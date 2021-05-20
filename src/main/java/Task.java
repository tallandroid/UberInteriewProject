public interface Task<T> extends Runnable{
    String getRequestid();

    void performRequest(T request);
}