package src;
/**
 * 
 * @author Mbregg
 *
 * @param <T> Return type of apply
 * @param <F> Parameter to apply
 * A simple copy of Java 8s Function interface(more or less) 
 * Used to pass in a function to the chatbox, but can be used anywhere else it would do good.
 */
public interface Function<T, F> {
	public T apply(F foo);
}
