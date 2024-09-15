package rainy.tasks;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a deadline task. This class is a sub-class of the <code>Task</code> class.
 */
public class Deadline extends Task {
    private String endDate;

    /**
     * Constructs a new <code>Deadline</code> object.
     * @param name     Represents the name of the task.
     * @param endDate  Represents the endDate of the task.
     */
    public Deadline(String name, String endDate) {
        super(name);
        this.endDate = endDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.endDate = date;
    }

    /**
     * Represents the task in a readable format. If the deadline is read from an existing file, it is directly read into
     * name of the deadline. Else, this method does additional formatting to represent the date in a standard format.
     * {@code
     * Deadline deadline = new Deadline("return book", "02-12/2024", "1800");
     * System.out.println(deadline);
     *
     * // Expected output:
     * // [D] return book  (by Dec 2 2019 18:00)
     * }
     * @return  Returns a string representing the <code>Deadline</code> object.
     */
    @Override
    public String toString() {
        try {
            this.compareDate = LocalDate.parse(this.endDate.substring(0, 10));
            String newDate = this.compareDate.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
            String newTime = LocalTime.parse(this.endDate.substring(11, 13) + ":"
                    + this.endDate.substring(13, 15)).format(DateTimeFormatter.ofPattern("HH:mm"));
            return "[D] " + super.getName() + "(by " + newDate + " " + newTime + ")";
        } catch (Exception e) {
            String secondDate = "";
            try {
                this.compareDate = LocalDate.parse(this.endDate.substring(3, 13),
                        DateTimeFormatter.ofPattern("MMM d yyyy"));
                secondDate = this.endDate.substring(14, 19);
            } catch (Exception d) {
                this.compareDate = LocalDate.parse(this.endDate.substring(3, 14),
                        DateTimeFormatter.ofPattern("MMM d yyyy"));
                secondDate = this.endDate.substring(15, 20);
            }
            String newDate = this.compareDate.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
            return "[D] " + super.getName() + "(" + "by " + newDate + " " + secondDate + ")";
        }
    }
}
