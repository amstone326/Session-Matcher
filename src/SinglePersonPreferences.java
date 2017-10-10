/**
 * Created by amstone326 on 10/10/17.
 */
public class SinglePersonPreferences {

    protected String name;
    protected String[] rankedChoices = new String[3];

    public SinglePersonPreferences(String csvRow) {
        String[] tokens = csvRow.split(",");
        this.name = tokens[0];
        for (int i = 0; i <3; i++) {
            rankedChoices[i] = tokens[i+1];
        }
    }

    public String getFirstChoiceSession() {
        return rankedChoices[0];
    }

    public String getSecondChoiceSession() {
        return rankedChoices[1];
    }
}
