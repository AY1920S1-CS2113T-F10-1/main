//@@author e0313687

package planner.logic.command;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import planner.logic.modules.cca.Cca;
import planner.logic.modules.cca.CcaList;
import planner.logic.modules.legacy.task.Task;
import planner.logic.modules.module.ModuleInfoDetailed;
import planner.logic.modules.module.ModuleTask;
import planner.logic.modules.module.ModuleTasksList;
import planner.ui.cli.PlannerUi;
import planner.util.crawler.JsonWrapper;
import planner.util.storage.Storage;

public class SortCommand extends ModuleCommand {

    public SortCommand(Arguments args) {
        super(args);
    }

    @Override
    public void execute(HashMap<String, ModuleInfoDetailed> detailedMap,
                        ModuleTasksList tasks,
                        CcaList ccas,
                        PlannerUi plannerUi,
                        Storage store,
                        JsonWrapper jsonWrapper) {
        String toSort = arg("toSort");
        plannerUi.sortMsg(toSort);
        List<?> hold;
        switch (toSort) {
            case ("ccas"): {
                hold = ccas;
                hold.sort(Comparator.comparing((Object t) -> ((Cca) t).getTask()));
                break;
            }
            //case ("times"): {
            //hold = ccas;
            //List<?> temp = tasks.getTasks();
            //hold.sort(Comparator.comparing((Object t) -> ((Task) t).getTime()));
            //}
            case ("modules"):
            default: {
                hold = tasks.getTasks();
                switch (arg("type")) {
                    case ("level"): {
                        hold.sort(Comparator.comparing((Object t) -> ((ModuleTask) t).getModuleLevel()));
                        break;
                    }
                    case ("mc"): {
                        hold.sort(Comparator.comparing((Object t) -> ((ModuleTask) t).getModuleCredit()));
                        break;
                    }
                    case ("code"):
                    default: {
                        hold.sort(Comparator.comparing((Object t) -> ((ModuleTask) t).getModuleCode()));
                        break;
                    }
                }
                break;
            }
        }
        plannerUi.showSorted(hold);
    }
}