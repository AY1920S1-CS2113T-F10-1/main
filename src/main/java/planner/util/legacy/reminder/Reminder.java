//@@author LongLeCE

package planner.util.legacy.reminder;

import java.sql.Time;
import java.time.LocalDateTime;

import com.google.gson.internal.bind.TimeTypeAdapter;
import planner.logic.exceptions.legacy.ModTimeIntervalTooCloseException;
import planner.util.legacy.periods.TimeInterval;

public abstract class Reminder {
    TimeInterval remindBefore;
    TimeInterval checkEvery;
    Thread thread;
    static final TimeInterval minBefore = TimeInterval.ofMinutes(1);
    volatile boolean kill;

    /**
     * Constructor for Reminder.
     * @param remindBefore TimeInterval object indicating the amount of time to start reminding beforehand
     * @param checkEvery TimeInterval object indicating the amount of time to wait between reminds
     */
    public Reminder(TimeInterval remindBefore, TimeInterval checkEvery)
            throws ModTimeIntervalTooCloseException {
        if (remindBefore.isLessThan(Reminder.minBefore)) {
            throw new ModTimeIntervalTooCloseException();
        }
        this.remindBefore = remindBefore;
        this.checkEvery = checkEvery;
        this.kill = true;
        this.thread = new Thread(this::remind);
    }

    public Reminder(int minutesBefore, int minutesEvery) throws ModTimeIntervalTooCloseException {
        this(TimeInterval.ofMinutes(minutesBefore), TimeInterval.ofMinutes(minutesEvery));
    }

    public Reminder(TimeInterval remindBefore) throws ModTimeIntervalTooCloseException {
        this(remindBefore, TimeInterval.min(TimeInterval.ofHours(1), remindBefore));
    }

    public Reminder(int minutesBefore) throws ModTimeIntervalTooCloseException {
        this(minutesBefore, Math.min(60, minutesBefore));
    }

    public Reminder() throws ModTimeIntervalTooCloseException {
        this(TimeInterval.ofHours(6), TimeInterval.ofHours(1));
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
        //TimeInterval timeIntervals;
        long sleepSeconds;
        while (!this.kill) {
            now = LocalDateTime.now();
            if (now.isAfter(targetTime)) {
                targetTime = now.plus(this.checkEvery);
                this.execute(now);
                // timeIntervals = TimeInterval.ofMinutes(timeInterval);
                TimeInterval.ofMinutes(12);
            }
            sleepSeconds = Math.max(TimeInterval.between(LocalDateTime.now(), targetTime)
                    .toDuration().getSeconds() - 1, 0);
            if (sleepSeconds > 0 && !this.kill) {
                try {
                    Thread.sleep(sleepSeconds * 1000);
                } catch (InterruptedException ignored) {
                    targetTime = LocalDateTime.now();
                }
            }
        }
    }


    abstract void execute(LocalDateTime now);
}
