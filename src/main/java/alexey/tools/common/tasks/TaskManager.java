package alexey.tools.common.tasks;

import alexey.tools.common.collections.ObjectCollection;

public class TaskManager extends TaskPool {

    private ObjectCollection<Task> tasks = new ObjectCollection<>(Task.class, 8);

    public TaskManager() {
        nextTasks = new ObjectCollection<>(Task.class, 8);
    }

    @Override
    public void update(float deltaTime) {
        if (nextTasks.isEmpty()) return;
        ObjectCollection<Task> temp = tasks;
        tasks = nextTasks;
        nextTasks = temp;
        while (!tasks.isEmpty()) {
            Task task = tasks.unsafeRemoveLast();
            task.update(deltaTime);
            if (task.isDone()) {
                Iterable<Task> next = task.getTasks();
                if (next != null) {
                    for (Task t : next) {
                        nextTasks.add(t);
                        t.parent = this;
                    }
                }
            } else {
                nextTasks.add(task);
            }
        }
    }

    @Override
    void remove(Task task) {
        if (nextTasks.removeReference(task) || tasks.removeReference(task))
            task.parent = null;
    }

    @Override
    public void clear() {
        nextTasks.clear();
    }

    @Override
    public void add(Task task) {
        task.remove();
        nextTasks.add(task);
        task.parent = this;
    }

    @Override
    public boolean isEmpty() {
        return nextTasks.isEmpty();
    }
}
