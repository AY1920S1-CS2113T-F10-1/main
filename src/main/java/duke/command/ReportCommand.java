package duke.command;

import duke.modules.Task;
import duke.util.Reminder;
import duke.util.Storage;
import duke.util.TaskList;
import duke.util.Ui;
import duke.exceptions.ModEmptyListException;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class ReportCommand extends Command {

    public ReportCommand() {

    }

    /**
     * Takes in TaskList, Ui and Storage objects which then displays
     * all the actively tracked Tasks in TaskList.
     * @param tasks TaskList object containing current active taskList.
     * @param ui Ui object containing all output methods to user.
     * @param store Storage object which updates stored data.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage store, Reminder reminder) throws ModEmptyListException {
        boolean isEmpty = tasks.getTasks().isEmpty();
        if (isEmpty) {
            throw new ModEmptyListException();
        } else {
            ui.reportListMsg();
            ui.printReportOne(tasks.getTasks());

            List<Task> ETemp = tasks.find("[E]");
            ui.printReportTask(ETemp, "event");
            List<Task> DTemp = tasks.find("[D]");
            ui.printReportTask(DTemp, "deadline");
            List<Task> RTemp = tasks.find("[R]");
            ui.printReportTask(RTemp, "recurring");
            List<Task> TTemp = tasks.find("[T]");
            ui.printReportTask(TTemp, "todo");
            List<Task> WTemp = tasks.find("[W]");
            ui.printReportTask(WTemp, "do within");
            List<Task> FTemp = tasks.find("[F]");
            ui.printReportTask(FTemp, "fixed duration");
        }
    }

    private Set<String> coreModList = new HashSet<String>();

    public Set<String> getCoreModList() {
        coreModList.add("CG111");
        coreModList.add("CG112");
        coreModList.add("CS1010");
        coreModList.add("CS1231");
        coreModList.add("MA1511");
        coreModList.add("MA1512");
        coreModList.add("M1508E");
        coreModList.add("CG2023");
        coreModList.add("CG2027");
        coreModList.add("CG2028");
        coreModList.add("CG2271");
        coreModList.add("CS2040C");
        coreModList.add("CS2101");
        coreModList.add("EE2026");
        coreModList.add("EG2401A");
        coreModList.add("ST2334");
        coreModList.add("CG3207");
        coreModList.add("CP3380");
        coreModList.add("EG3611A");
        coreModList.add("CG4002");
        coreModList.add("EE4204");
        return coreModList;
    }

    public boolean isCoreMod((Task module) {
        getCoreModList();
        if(coreModList.contains(module)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
