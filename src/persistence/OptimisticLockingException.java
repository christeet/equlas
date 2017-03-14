package persistence;

public class OptimisticLockingException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public OptimisticLockingException(String string) {
		super(string);
	}

}
