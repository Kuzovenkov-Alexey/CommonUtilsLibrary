package alexey.tools.common.tasks;

import alexey.tools.common.collections.ObjectCollection;

public class TaskPool {
    protected ObjectCollection<Task> nextTasks;

    public boolean isEmpty() {
        if (nextTasks == null) return true;
        return nextTasks.isEmpty();
    }

    public void add(Task task) {
        if (nextTasks == null) nextTasks = new ObjectCollection<>(Task.class, 2);
        task.remove();
        nextTasks.add(task);
        task.parent = this;
    }

    public void update(float deltaTime) {

    }

    public void clear() {
        if (nextTasks == null) return;
        nextTasks.clear();
    }

    Iterable<Task> getTasks() {
        return nextTasks;
    }

    void remove(Task task) {
        if (nextTasks == null) return;
        if (nextTasks.removeReference(task)) task.parent = null;
    }
}
