package xyz.refinedev.practice.event.impl.koth.task;

import xyz.refinedev.practice.event.impl.koth.Koth;
import xyz.refinedev.practice.event.task.EventRoundEndTask;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 11/5/2021
 * Project: Array
 */

public class KothRoundEndTask extends EventRoundEndTask {

    public KothRoundEndTask(Koth event) {
        super(event);
    }

    @Override
    public void onRun() {

    }
}
