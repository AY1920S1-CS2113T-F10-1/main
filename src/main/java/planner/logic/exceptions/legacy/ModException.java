package planner.logic.exceptions.legacy;

public class ModException extends Throwable {

    public ModException() {
    }

    public ModException(String message) {
        super(message);
    }

    public ModException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModException(Throwable cause) {
        super(cause);
    }

    protected ModException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Over-writing the exception class GetMessage method,
     * so that other sub-classes would have this message.
     * @return Starting portion indicating a ModException.
     */

    @Override
    public String getMessage() {
        String extraMessage = "";
        if (super.getMessage() != null) {
            extraMessage = super.getMessage();
        }
        return "Error: " + extraMessage;
    }
}
