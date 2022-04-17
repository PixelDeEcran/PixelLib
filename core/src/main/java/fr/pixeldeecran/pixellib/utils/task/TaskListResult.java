package fr.pixeldeecran.pixellib.utils.task;

public interface TaskListResult {

    void whenDone(int finalIndex, TaskState finalState);
}
