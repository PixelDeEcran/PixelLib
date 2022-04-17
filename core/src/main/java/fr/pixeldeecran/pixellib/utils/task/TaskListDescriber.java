package fr.pixeldeecran.pixellib.utils.task;

import java.util.List;

public class TaskListDescriber<T> {

    private final List<Task<T>> taskList;
    private T data;
    private int currentTaskIndex;

    public TaskListDescriber(List<Task<T>> taskList) {
        this.taskList = taskList;
    }

    public TaskListDescriber<T> then(Task<T> task) {
        taskList.add(task);
        return this;
    }

    public void run(T defaultData, boolean skipError, TaskListResult whenDone) {
        this.data = defaultData;
        this.currentTaskIndex = 0;

        this.runNextTask(skipError, whenDone);
    }

    private void runNextTask(boolean skipError, TaskListResult whenDone) {
        if (currentTaskIndex >= taskList.size()) {
            whenDone.whenDone(this.currentTaskIndex - 1, TaskState.DONE);
            return;
        }

        Task<T> task = this.taskList.get(this.currentTaskIndex);
        task.run(this.data, () -> {
            this.currentTaskIndex++;

            this.runNextTask(skipError, whenDone);
        }, () -> {
            if (skipError) {
                this.currentTaskIndex++;

                //noinspection ConstantConditions
                this.runNextTask(skipError, whenDone);
            } else {
                whenDone.whenDone(this.currentTaskIndex, TaskState.ERROR);
            }
        });
    }

    public List<Task<T>> getTaskList() {
        return taskList;
    }
}
