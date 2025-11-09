package bracketcraft;

import java.io.Serializable;
public class Participant implements Serializable {
    private String name;

    public Participant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // toString() is called by UI components like JButtons to display the name.
    @Override
    public String toString() {
        return name;
    }
}