package duke.tasks;

import duke.exceptions.DukeInvalidTimeException;
import duke.exceptions.DukeInvalidTimePeriodException;
import duke.util.DateTimeParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {

    /**
     * Constructor for Deadline class, using String Varargs.
     * @param input Parsed user string input, first input being name,
     *              second input being the date.
     */
    public Deadline(String... input) throws DukeInvalidTimeException, DukeInvalidTimePeriodException {
        super(input[0]);
        this.period.setPeriod(null, DateTimeParser.getStringToDate(input[input.length - 1]));
    }

    @Override
    public String writingFile() {
        return "D"
            + "|"
            + super.writingFile()
            + "|"
            + this.getEnd().format(DateTimeFormatter.ofPattern("dd-MM-yyyy [HH:mm]"));
    }

    @Override
    public String toString() {
        return "[D]"
            + super.toString()
            + " (by: "
            + this.getEnd().format(DateTimeFormatter.ofPattern("dd-MM-yyyy [HH:mm]"))
            + ")";
    }
}
