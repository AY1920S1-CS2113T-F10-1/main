package duke.util;

import duke.exceptions.ModInvalidTimePeriodException;
import duke.exceptions.ModTimeIntervalTooCloseException;

import duke.modules.Task;
import duke.modules.TaskWithPeriod;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reminder {
    private TimeInterval remindBefore;
    private TimeInterval checkEvery;
    private List<Task> tasks;
    private Thread thread;
    private static final TimeInterval minBefore = TimeInterval.ofMinutes(1);
    private volatile boolean kill;

    /**
     * Constructor for Reminder.
     * @param tasks TaskList object containing current active taskList.
     * @param remindBefore TimeInterval object indicating the amount of time to start reminding beforehand
     * @param checkEvery TimeInterval object indicating the amount of time to wait between reminds
     */
    public Reminder(List<Task> tasks, TimeInterval remindBefore, TimeInterval checkEvery)
            throws ModTimeIntervalTooCloseException {
        if (remindBefore.isLessThan(Reminder.minBefore)) {
            throw new ModTimeIntervalTooCloseException();
        }
        this.tasks = tasks;
        this.remindBefore = remindBefore;
        this.checkEvery = checkEvery;
        this.kill = true;
        this.thread = new Thread(this::remind);
    }

    public Reminder(List<Task> tasks, int minutesBefore, int minutesEvery) throws ModTimeIntervalTooCloseException {
        this(tasks, TimeInterval.ofMinutes(minutesBefore), TimeInterval.ofMinutes(minutesEvery));
    }

    public Reminder(List<Task> tasks, TimeInterval remindBefore) throws ModTimeIntervalTooCloseException {
        this(tasks, remindBefore, TimeInterval.min(TimeInterval.ofHours(1), remindBefore));
    }

    public Reminder(List<Task> tasks, int minutesBefore) throws ModTimeIntervalTooCloseException {
        this(tasks, minutesBefore, Math.min(60, minutesBefore));
    }

    public Reminder(List<Task> tasks) throws ModTimeIntervalTooCloseException {
        this(tasks, TimeInterval.ofHours(6), TimeInterval.ofHours(1));
    }

    /**
     * Start the reminder if it's not running.
     */
    public void run() {
        this.kill = false;
        this.thread.start();
    }

    /**
     * Kill the reminder if it's running.
     */
    public void stop() {
        if (this.thread.isAlive()) {
            this.kill = true;
            if (this.thread.getState().equals(Thread.State.TIMED_WAITING)) {
                this.thread.interrupt();
            }
        }
    }

    public boolean isStopped() {
        return !this.thread.isAlive();
    }

    /**
     * Force reminder to check upcoming tasks and remind immediately.
     */
    public void forceCheckReminder() {
        this.kill = false;
        if (!this.thread.isAlive()) {
            this.thread.start();
        } else if (this.thread.getState().equals(Thread.State.TIMED_WAITING)) {
            this.thread.interrupt();
        }
    }

    /**
     * Core logic for reminder to run.
     */
    private void remind() {
        LocalDateTime targetTime = LocalDateTime.now();
        LocalDateTime now;
        long sleepSeconds;
        while (!this.kill) {
            now = LocalDateTime.now();
            if (now.isAfter(targetTime)) {
                targetTime = now.plus(this.checkEvery);
                try {
                    new Ui().printUpcomingTasks(
                            this.getUpcomingTasks(
                                    new TimePeriod(now, now.plus(this.remindBefore))));
                } catch (ModInvalidTimePeriodException e) {
                    System.out.println(e.getMessage());
                }
            }
            sleepSeconds = Math.max(TimeInterval.between(LocalDateTime.now(), targetTime)
                    .toDuration().getSeconds() - 1, 0);
            if (sleepSeconds > 0) {
                try {
                    Thread.sleep(sleepSeconds * 1000);
                } catch (InterruptedException ignored) {
                    targetTime = LocalDateTime.now();
                }
            }
        }
    }

    /**
     * Get upcoming tasks.
     * @param timePeriod How long before the task begin to remind
     * @return the upcoming tasks
     */
    private List<Task> getUpcomingTasks(TimePeriod timePeriod) {
        List<Task> upcomingTasks = new ArrayList<>();
        for (Task task: this.tasks) {
            if (task instanceof TaskWithPeriod
                    && !task.isDone()
                    && timePeriod.isClashing(((TaskWithPeriod)task).getPeriod().getBegin())) {
                upcomingTasks.add(task);
            }
        }
        return upcomingTasks;
    }
}
