package util;

public interface IObserver<T> {
    void update(T o, Object arg);
}
