package duke.command.logic;

import java.util.HashMap;
import java.util.List;

import duke.exceptions.ModException;
import duke.modules.data.ModuleInfoDetailed;
import duke.util.JsonWrapper;
import duke.util.PlannerUi;
import duke.util.Storage;
import duke.util.commons.ModuleTasksList;

public class ShowModuleCommand extends ModuleCommand {

    @Override
    public void execute(HashMap<String, ModuleInfoDetailed> detailedMap,
                        ModuleTasksList tasks,
                        PlannerUi plannerUi,
                        Storage store,
                        JsonWrapper jsonWrapper) throws ModException {
        plannerUi.listMsg();
        int counter = 1;
        List<ModuleInfoDetailed> hold = tasks.getTasks();
        for (ModuleInfoDetailed temp : hold) {
            System.out.print(counter++ + " ");
            plannerUi.showObject(temp);
        }
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
