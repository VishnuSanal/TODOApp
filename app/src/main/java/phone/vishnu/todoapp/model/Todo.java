package phone.vishnu.todoapp.model;
//FIXME: Changed on 17-12-2019
public class Todo {
    private String todo, target;

    public Todo() {
    }

    public Todo(String todo, String target) {
        this.todo = todo;
        this.target = target;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
