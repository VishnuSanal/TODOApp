package phone.vishnu.todoapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class Shelve {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private final String title;

    private final String description;

    private final String dateDue;

    public Shelve(String title, String description, String dateDue) {
        this.title = title;
        this.description = description;
        this.dateDue = dateDue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDateDue() {
        return dateDue;
    }

    @Override
    public String toString() {
        return "Shelve{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dateDue='" + dateDue + '\'' +
                '}';
    }
}
