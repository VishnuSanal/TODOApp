package phone.vishnu.todoapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Shelve(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val description: String,
    val dateDue: Long,
) : Serializable {
    constructor(
        title: String,
        description: String,
        dateDue: Long,
    ) : this(id = 0, title = title, description = description, dateDue = dateDue)

    override fun toString(): String {
        return "Shelve{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dateDue='" + dateDue + '\'' +
                '}'
    }
}