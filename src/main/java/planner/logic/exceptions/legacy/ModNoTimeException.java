package planner.logic.exceptions.legacy;

public class ModNoTimeException extends ModException {
    @Override
    public String getMessage() {
        return super.getMessage() + "Cannot set time for this task!";
    }
}
