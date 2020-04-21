package net.tccn.qtask.impl;

import net.tccn.base.Kv;
import net.tccn.qtask.QTask;
import net.tccn.qtask.Task;

public abstract class QTaskAbs implements QTask {

    protected Task task;

    public QTaskAbs(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public Kv getPara() {
        return task.getPara();
    }

    public String getContent() {
        return task.getContent();
    }
}
