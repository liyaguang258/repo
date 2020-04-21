import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: liangxianyou
 */
public class TimerKit {
    private static Timer timer = new Timer();

    /**
     * 延迟执行
     *
     * @param runnable 执行的任务
     * @param delay    延迟时间
     */
    public static void schedule(Runnable runnable, long delay) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, delay);
    }

    /**
     * 延迟周期执行
     *
     * @param runnable 执行的任务
     * @param delay    延迟时间
     * @param period   执行周期
     */
    public static void schedule(Runnable runnable, long delay, long period) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, delay, period);
    }
}
