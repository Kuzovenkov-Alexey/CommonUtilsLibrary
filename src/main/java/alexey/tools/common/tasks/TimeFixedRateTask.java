package alexey.tools.common.tasks;

public class TimeFixedRateTask extends ConditionFixedRateTask {

    public int times;

    public TimeFixedRateTask(int times, float delay, float remainingDelay) {
        super(delay, remainingDelay);
        this.times = times;
    }

    @Override
    public void update(float deltaTime) {
        remainingDelay -= deltaTime;
        while (remainingDelay <= 0.0F && times != 0) {
            action(delay);
            remainingDelay += delay;
            times -= 1;
        }
    }

    @Override
    public boolean isDone() {
        return times == 0;
    }
}
