/**
 * Created by amstone326 on 10/10/17.
 */
public class SessionPreferenceReader extends CsvReader<SinglePersonPreferences> {

    public SessionPreferenceReader(String csvFilename) {
        super(csvFilename);
    }

    @Override
    public SinglePersonPreferences processRow(String csvRow) {
        return new SinglePersonPreferences(csvRow);
    }
}
