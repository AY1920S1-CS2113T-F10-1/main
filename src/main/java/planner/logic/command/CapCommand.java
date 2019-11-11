//@@author andrewleow97

package planner.logic.command;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import planner.credential.user.User;
import planner.logic.exceptions.legacy.ModException;
import planner.logic.exceptions.legacy.ModMissingArgumentException;
import planner.logic.exceptions.planner.ModBadGradeException;
import planner.logic.exceptions.planner.ModNoPrerequisiteException;
import planner.logic.exceptions.planner.ModNotFoundException;
import planner.logic.modules.TaskList;
import planner.logic.modules.module.ModuleInfoDetailed;
import planner.logic.modules.module.ModuleTask;
import planner.util.crawler.JsonWrapper;
import planner.ui.cli.PlannerUi;
import planner.util.storage.Storage;
import planner.logic.exceptions.legacy.ModCommandException;
import planner.logic.exceptions.legacy.ModEmptyCommandException;
import planner.logic.exceptions.legacy.ModEmptyListException;

public class CapCommand extends ModuleCommand {

    public String[] command;
    //public ArrayList<ModuleInfoSummary> completedModuleList = new ArrayList<>();
    //public ModuleList specificModuleCap;
    private double currentCap;
    private double projectedModuleCap;
    private double projectedCap;
    private double mcCount;
    private ArrayList<String> validGrades = new ArrayList<>(Arrays.asList("A+", "A", "A-", "B+", "B",
        "B-", "C+", "C", "D+", "D", "F", "S", "U", "CS", "CU"));

    /**
     * Constructor for the CapCommand class where user can enquire information about their CAP.
     * Such as overall CAP and what-if reports about predicted CAP.
     * Input format can be in three forms
     * `cap` overall cap
     * `cap list` to calculate cap from grades in module list
     * `cap module to check predicted cap for a specific module from prerequisites
     */
    public CapCommand(Arguments args) {
        super(args);
        mcCount = 0;
        currentCap = 0;
        projectedModuleCap = 0;
        projectedCap = 0;
    }

    /**
     * Constructor for testing.
     */
    public CapCommand() {
        mcCount = 0;
        currentCap = 0;
        projectedModuleCap = 0;
        projectedCap = 0;
    }

    public boolean isComplete(String input) {
        return input.equalsIgnoreCase("done");
    }

    public double getCurrentCap() {
        return currentCap;
    }

    public double getProjectedModuleCap() {
        return projectedModuleCap;
    }

    public double getProjectedCap() {
        return projectedCap;
    }

    /**
     * Converts String grade to a double value according to NUS guidelines.
     */
    public double letterGradeToCap(String grade) {
        switch (grade) {
            case "A+":
            case "A":
                return 5.00;
            case "A-":
                return 4.50;
            case "B+":
                return 4.00;
            case "B":
                return 3.50;
            case "B-":
                return 3.00;
            case "C+":
                return 2.50;
            case "C":
                return 2.00;
            case "D+":
                return 1.50;
            case "D":
                return 1.00;
            case "F":
                return 0.50;
            default:
                return 0.00;
        }
    }

    /**
     * Execute of 3 different forms of user input according to the arguments of the user input.
     */
    @Override
    public void execute(HashMap<String, ModuleInfoDetailed> detailedMap,
                        PlannerUi plannerUi,
                        Storage store,
                        JsonWrapper jsonWrapper,
                        User profile)
        throws ModException {
        switch (arg("toCap")) {
            case "overall":
                plannerUi.capStartMsg();
                calculateOverallCap(profile.getModules(), detailedMap, plannerUi, store);
                break;
            case "module":
                plannerUi.capModStartMsg();
                calculateModuleCap(detailedMap, plannerUi, store, profile);
                break;
            case "list":
                TaskList<ModuleTask> hold = profile.getModules();
                plannerUi.capListStartMsg(hold);
                calculateListCap(profile.getModules(), detailedMap, plannerUi, store, hold);
                break;
            default:
                throw new ModCommandException();
        }
    }

    /**
     * User will keep inputting "[moduleCode] [letterGrade]" until satisfied.
     * Then user inputs "done" and the user's CAP will be calculated and printed.
     */
    public void calculateOverallCap(TaskList<ModuleTask> moduleTasksList,
                                    HashMap<String, ModuleInfoDetailed> detailedMap,
                                    PlannerUi plannerUi,
                                    Storage store)
        throws ModMissingArgumentException, ModNotFoundException, ModEmptyCommandException,
        ModBadGradeException {
        String userInput = plannerUi.readInput();
        while (!isComplete(userInput)) {
            if (userInput.isEmpty()) {
                throw new ModEmptyCommandException();
            }
            String[] userInfo = userInput.split(" ");
            if (userInfo.length <= 1) {
                throw new ModBadGradeException();
            }
            if (!validGrades.contains(userInfo[1].toUpperCase())) {
                throw new ModBadGradeException();
            }
            if (!detailedMap.containsKey(userInfo[0].toUpperCase())) {
                throw new ModNotFoundException();
            }
            double mcTemp = detailedMap.get(userInfo[0].toUpperCase()).getModuleCredit();
            if (letterGradeToCap(userInfo[1].toUpperCase()) != 0.00) {
                mcCount += mcTemp;
                currentCap += (letterGradeToCap(userInfo[1].toUpperCase()) * mcTemp);
            }
            if (userInfo[1].isEmpty() || userInfo[1].isBlank()) {
                throw new ModMissingArgumentException("Please input a letter grade for this module.");
            }
            userInput = plannerUi.readInput();
        }
        if (currentCap == 0 && mcCount == 0) {
            plannerUi.capMsg(0.00);
        } else {
            double averageCap = currentCap / mcCount;
            plannerUi.capMsg(averageCap);
        }
    }

    /**
     * Calculates a predicted CAP for a module based on the grades attained for it's prerequisites.
     */
    public void calculateModuleCap(HashMap<String, ModuleInfoDetailed> detailedMap,
                                    PlannerUi plannerUi,
                                    Storage store,
                                    User profile)
        throws ModNotFoundException,
        ModNoPrerequisiteException,
        ModEmptyListException {
        String moduleCode = plannerUi.readInput().toUpperCase();
        if (!detailedMap.containsKey(moduleCode)) {
            throw new ModNotFoundException();
        }
        if (detailedMap.get(moduleCode).getPrerequisites().isEmpty()
            ||
            detailedMap.get(moduleCode).getPrerequisites().isBlank()) {
            throw new ModNoPrerequisiteException();
        }
        if (profile.getModules().isEmpty()) {
            throw new ModEmptyListException();
        }
        ArrayList<String> prunedModules = parsePrerequisiteTree(detailedMap.get(moduleCode).getPrerequisites());
        for (ModuleTask x : profile.getModules()) {
            if (prunedModules.contains(x.getModuleCode())) {
                if (letterGradeToCap(x.getGrade()) != 0.00) {
                    mcCount += x.getModuleCredit();
                    projectedModuleCap += letterGradeToCap(x.getGrade()) * x.getModuleCredit();
                }
                prunedModules.remove(x.getModuleCode());
            }
        }
        ArrayList<String> toBeRemoved = new ArrayList<>();
        if (!prunedModules.isEmpty()) {
            for (String module : prunedModules) {
                boolean hasPreclusions = false;
                for (ModuleTask x : profile.getModules()) {
                    if (detailedMap.get(module).getPreclusion().contains(x.getModuleCode())
                        ||
                        (detailedMap.get(x.getModuleCode()).getPreclusion().contains(module))) {
                        hasPreclusions = true;
                        mcCount += x.getModuleCredit();
                        projectedModuleCap += letterGradeToCap(x.getGrade()) * x.getModuleCredit();
                        break;
                    }
                }
                if (hasPreclusions) {
                    toBeRemoved.add(module);
                }
            }
        }
        for (String x : toBeRemoved) {
            prunedModules.remove(x);
        }
        if (prunedModules.isEmpty()) {
            if (projectedModuleCap == 0 && mcCount == 0) {
                plannerUi.capModMsg(0.00, moduleCode);
            } else {
                double averageCap = projectedModuleCap / mcCount;
                plannerUi.capModMsg(averageCap, moduleCode);
            }
        } else {
            plannerUi.capModuleIncompleteMsg(prunedModules);
        }
    }

    /**
     * Calculates CAP according to the modules with grades in the ModuleTaskList.
     */
    public void calculateListCap(TaskList<ModuleTask> moduleTasksList,
                                 HashMap<String, ModuleInfoDetailed> detailedMap,
                                 PlannerUi plannerUi,
                                 Storage store,
                                 TaskList<ModuleTask> moduleList) {
        for (ModuleTask task : moduleList) {
            if (letterGradeToCap(task.getGrade()) != 0.00) {
                mcCount += task.getModuleCredit();
                projectedCap += (letterGradeToCap(task.getGrade()) * task.getModuleCredit());
            }
        }
        double averageCap = projectedCap / mcCount;
        if (projectedCap == 0 && mcCount == 0) {
            plannerUi.capListErrorMsg();
        } else {
            plannerUi.capMsg(averageCap);
        }
    }

    /**
     * Method to parse prerequisites from ModuleInfoDetailed and splice it into a List of Lists of String.
     * Overall is List of Lists, for each internal List it contains modules that are 'or'ed with each other
     * i.e taking one of the modules in the internal list is enough to fulfill one list of prerequisites
     * Across the whole list is modules that are 'and'ed with each other
     * The whole List of Lists must be complete and graded in order for prerequisites to be fulfilled
     *
     * @return A List of lists of string of prerequisite modules to be graded before calculating cap
     */
    public ArrayList<String> parsePrerequisiteTree(String prerequisites) {
        Matcher matcher = Pattern.compile("[a-zA-Z][a-zA-Z][a-zA-Z]?[0-9][0-9][0-9][0-9][a-zA-Z]?")
            .matcher(prerequisites);
        ArrayList<String> prerequisiteModules = new ArrayList<>();
        while (matcher.find()) {
            prerequisiteModules.add(matcher.group());
        }
        return prerequisiteModules;
    }
}

