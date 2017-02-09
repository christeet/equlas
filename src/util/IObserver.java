package util;

/**
 * IObserver<T> is an observer for the Type T
 *
 * @param <T> the Observable is of Type T
 */
public interface IObserver<T> {
    void update(T o);
}
