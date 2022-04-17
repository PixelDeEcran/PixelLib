package fr.pixeldeecran.pixellib.utils.task;

public interface Task<T> {

    void run(T data, Runnable done, Runnable error);
}
