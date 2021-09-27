package alexey.tools.common.tasks;

public class ConditionFixedRateTask extends Task {
    public float delay;
    public float remainingDelay;

    public ConditionFixedRateTask(float delay, float remainingDelay) {
        this.delay = delay;
        this.remainingDelay = remainingDelay;
    }

    @Override
    public void update(float deltaTime) {
        remainingDelay -= deltaTime;
        while (remainingDelay <= 0.0F) {
            action(delay);
            remainingDelay += delay;
        }
    }

    public void action(float deltaTime) {

    }

    @Override
    public boolean isDone() {
        return false;
    }
}
