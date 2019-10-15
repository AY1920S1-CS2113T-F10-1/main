package duke.util;

import java.util.Scanner;

import duke.modules.data.ModuleInfoDetailed;
import duke.modules.data.ModuleTask;

/**
 * Mod Planner inherits functionality from Original Duke Ui.
 */
public class PlannerUi {

    private Scanner scan;
    private static final String LINE = "_______________________________";

    /**
     * Default constructor for Ui.
     */
    public PlannerUi() {
        scan = new Scanner(System.in);
    }

    public void showLine() {
        System.out.println(LINE);
    }

    private void closeScanner() {
        scan.close();
    }

    public String readCommand() {
        return scan.nextLine().strip();
    }

    /**
     * Helper function to print any object.
     * @param mod to be printed.
     */
    public void showObject(ModuleInfoDetailed mod) {
        System.out.println(mod);
    }

    /**
     * Added Message for new mods.
     * @param mod Module Tasks to be added.
     */
    public void addedMsg(ModuleInfoDetailed mod) {
        System.out.println("Got it, added the follow module!");
        showObject(mod);
    }

    /**
     * Delete Message for new mods.
     * @param mod Module Tasks to be added.
     */
    public void deleteMsg(ModuleInfoDetailed mod) {
        System.out.println("Got it, module will be deleted");
        showObject(mod);
    }

    public void listMsg() {
        System.out.println("All modules in the list!");
    }

    /**
     * Start up message upon running mod planner.
     */
    public void helloMsg() {
        showLine();
        System.out.println(
                "Welcome to ModPlanner, your one stop solution to module planning!\n"
                + "Begin typing get started!"
        );
        showLine();
    }

    /**
     * Ending message upon termination.
     */
    public void goodbyeMsg() {
        showLine();
        System.out.println(
                "Thanks for using ModPlanner!\n"
                 + "Your data will be stored in file shortly!\n"
        );
        showLine();
        closeScanner();
    }

    /**
     * Message shown at start of CapCommand.
     */
    public void capStartMsg() {
        System.out.println("Start typing the module you have taken, along with it's letter grade");
        System.out.println("Type 'done' when you are ready to calculate your CAP");
    }

    /**
     * Message to print average CAP to 2 decimal places.
     */
    public void capMsg(double averageCap) {
        showLine();
        System.out.println("Here is your current cumulative/predicted CAP");
        System.out.printf("%.2f\n", averageCap);
    }
}
