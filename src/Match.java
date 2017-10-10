import java.util.ArrayList;
import java.util.List;

/**
 * A match object represents a single session and the set of people that have been matched to it
 *
 * Created by amstone326 on 10/10/17.
 */
public class Match {

    protected String sessionName;
    protected List<String> assignedPeople;

    public Match(String sessionName) {
        this.sessionName = sessionName;
        assignedPeople = new ArrayList<>();
    }

    public void assignPerson(String name) {
        this.assignedPeople.add(name);
    }

    public void removePerson(String name) {
        this.assignedPeople.remove(name);
    }

    public int getSessionSize() {
        return assignedPeople.size();
    }

}
