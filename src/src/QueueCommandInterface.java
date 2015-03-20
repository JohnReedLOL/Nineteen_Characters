package src;

/**
 * 
 * @author Mbregg
 *
 * @param <T>
 */
public interface QueueCommandInterface<T> {
	
	public void enqueue(T command);
	public void sendInterrupt();

}
