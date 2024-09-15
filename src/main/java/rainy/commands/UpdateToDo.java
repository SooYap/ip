package rainy.commands;

import rainy.tasks.TaskTracker;

public class UpdateToDo extends UpdateCommand {

    public UpdateToDo(int validResponse, TaskTracker taskTracker, String[] updateParameters) {
        super(validResponse, taskTracker, updateParameters);
    }

    public TaskTracker getResponse() {
        String toDoName = this.updateParameters[0];
        this.taskTracker.updateToDo(validResponse - 1, toDoName);
        this.ui.taskHasBeenUpdated();
        return this.taskTracker;
    }
}