package rainy.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

import rainy.rainyexceptions.InvalidIndexException;
import rainy.rainyexceptions.InvalidMarkAndUnmarkException;
import rainy.tasks.TaskTracker;

/**
 * Takes in a <code>File</code> object and either reads the file or writes over it entirely.
 */
public class Storage {
    /**
     * Constructs a new <code>Storage</code> object.
     */
    private static int START_INDEX = 0;
    private static int TASK_TYPE = 8;
    private static int MARKED_TASK = 4;
    private static int SKIP_FIRST = 1;
    private static int START_TASK_DESC = 11;
    private static char MARKED_LABEL = 'X';

    public Storage() {

    }

    /**
     * Takes in a <code>File</code> object and reads the data.
     * @param newFile                        Represents the file object to be read.
     * @return                               This method copies the previously established tasks to a newly
     *                                       created <code>TaskTracker</code>
     *                                       object and returns it to be used by the Rainy chatbot.
     * @throws InvalidIndexException         Thrown by <code>TaskManager</code> object when user provides
     *                                       a non-existent task number.
     * @throws InvalidMarkAndUnmarkException Thrown by <code>Task</code> object when user wants to mark a
     *                                       marked tasked or unmark an unmarked task.
     */
    public TaskTracker copyPreviousFiles(File newFile) throws InvalidIndexException, InvalidMarkAndUnmarkException, IOException {
        TaskTracker newTask;
        try {
            newTask = this.processFile(newFile);
        } catch (FileNotFoundException e) {
            newTask = new TaskTracker();
        }
        assert(newTask.getCounter() >= START_INDEX);
        newTask.toggleReceivedInputs();
        return newTask;
    }

    public TaskTracker processFile(File newFile) throws IOException {
        TaskTracker newTask = new TaskTracker();
        TaskTracker[] taskHolder = new TaskTracker[]{newTask};
        AtomicInteger trace = new AtomicInteger(START_INDEX);
        AtomicInteger nextCounter = new AtomicInteger(TASK_TYPE);
        AtomicInteger markedCounter = new AtomicInteger(MARKED_TASK);
        Files.lines(newFile.toPath()).skip(SKIP_FIRST).forEach(x -> {
            int charLabel, markedLabel = 0;
            if ((trace.get() + 1) % 10 == 0) {
                charLabel = nextCounter.incrementAndGet();
                markedLabel = markedCounter.incrementAndGet();
            } else {
                charLabel = nextCounter.get();
                markedLabel = markedCounter.get();
            }
            if (x.charAt(charLabel) == 'T') {
                taskHolder[START_INDEX] = this.updateToDo(taskHolder[START_INDEX], x);
            } else if (x.charAt(charLabel) == 'D') {
                taskHolder[START_INDEX] = this.updateDeadline(taskHolder[START_INDEX], x);
            } else {
                taskHolder[START_INDEX] = this.updateEvent(taskHolder[START_INDEX], x);
            }
            int taskCount = trace.getAndIncrement();
            try {
                taskHolder[START_INDEX] = this.markSavedTask(taskHolder[START_INDEX], x, taskCount, markedLabel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return taskHolder[START_INDEX];
    }

    public TaskTracker markSavedTask(TaskTracker taskTracker, String userInput, int taskCount, int markedLabel) throws InvalidIndexException, InvalidMarkAndUnmarkException {
        if (userInput.charAt(markedLabel) == MARKED_LABEL) {
            try {
                taskTracker.markDone(taskCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return taskTracker;
    }

    public TaskTracker updateToDo(TaskTracker taskTracker, String userInput) {
        taskTracker.addListToDo(userInput.substring(START_TASK_DESC));
        return taskTracker;
    }

    public TaskTracker updateDeadline(TaskTracker taskTracker, String userInput) {
        String updatedOldData = userInput.substring(START_TASK_DESC, userInput.length() - 1);
        String[] deadlineSplit = updatedOldData.split(" \\(");
        taskTracker.addListDeadline(deadlineSplit[START_INDEX] + " ", deadlineSplit[SKIP_FIRST]);
        return taskTracker;
    }

    public TaskTracker updateEvent(TaskTracker taskTracker, String userInput) {
        String updatedOldData = userInput.substring(START_TASK_DESC, userInput.length() - 1);
        String[] eventSplit = updatedOldData.split(" \\(");
        String newDate = eventSplit[SKIP_FIRST].split(" from ")[START_INDEX];
        String newTime = eventSplit[SKIP_FIRST].split(" from ")[SKIP_FIRST];
        taskTracker.addListEvent(eventSplit[START_INDEX] + " ", newDate, newTime);
        return taskTracker;
    }

    /**
     * Writes over the existing file to save the newly added tasks when program ends.
     *
     * @param filename Represents the file that this method will write over.
     * @param tm       Provides the list of task for this method to extract and write into the <code>File</code> object.
     */
    public void writeOverFile(File filename, TaskTracker tm) {
        try {
            filename.createNewFile();
            FileWriter fw = new FileWriter(filename);
            fw.write(tm.printList());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

