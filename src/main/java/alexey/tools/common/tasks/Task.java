package alexey.tools.common.tasks;

public class Task extends TaskPool {
    TaskPool parent;

    public float getProgress() {
        return -1;
    }

    public boolean isDone() {
        return true;
    }

    public void remove() {
        if (parent != null) parent.remove(this);
    }
}
