package codalog;


public class CodalogException extends Exception {
    private static final long serialVersionUID = 1L;
    private static Log log = new Log();

    public CodalogException(String message) {
        log.log(message);
    }

    public CodalogException(Exception cause) {
    	log.log(cause.getMessage());
    }

    public CodalogException(String message, Exception cause) {
    	log.log(message + " : " +cause.getMessage());
    }
}