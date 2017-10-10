import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amstone326 on 10/10/17.
 */
public class SessionMatcherMain {

    private static final int MAX_SESSION_SIZE = 10;
    private static List<String> peopleAlreadyReassignedOnce = new ArrayList<>();

    static {
    }

    public static void main(String[] args) {
        String csvFileForWorkingGroup = args[0];
        String session1Name = args[1];
        String session2Name = args[2];
        String session3Name = args[3];
        List<SinglePersonPreferences> parsedPreferences =
                (new SessionPreferenceReader(csvFileForWorkingGroup)).readAndParseFile();

        Map<String, Match> sessionNamesToAssignments = new HashMap<>();
        sessionNamesToAssignments.put(session1Name, new Match(session1Name));
        sessionNamesToAssignments.put(session2Name, new Match(session2Name));
        sessionNamesToAssignments.put(session3Name, new Match(session3Name));

        createFirstChoiceMatches(sessionNamesToAssignments, parsedPreferences);

        try {
            adjustAssignmentsBasedOnNumbers(sessionNamesToAssignments, parsedPreferences);
        } catch (ReassignmentFailureException e) {
            System.out.println("MATCHING FAILED: " + e.getMessage());
        }

        printAssignments(sessionNamesToAssignments);
    }

    private static void createFirstChoiceMatches(Map<String, Match> sessionNamesToAssignments,
                                                 List<SinglePersonPreferences> parsedPreferences) {
        for (SinglePersonPreferences preference : parsedPreferences) {
            String firstChoiceSessionName = preference.getFirstChoiceSession();
            Match session = sessionNamesToAssignments.get(firstChoiceSessionName);
            session.assignPerson(preference.name);
        }
    }

    private static void adjustAssignmentsBasedOnNumbers(Map<String, Match> sessionNamesToAssignments,
                                                        List<SinglePersonPreferences> parsedPreferences) {
        Match overCapacitySession;
        while ((overCapacitySession = getMostOverCapacitySession(sessionNamesToAssignments)) != null) {
            bringSessionDownToMaxSize(overCapacitySession, sessionNamesToAssignments, parsedPreferences);
        }
    }

    private static Match getMostOverCapacitySession(Map<String, Match> sessionNamesToAssignments) {
        Match mostOverCapacitySession = null;
        int sizeOfMostOverCapacitySession = -1;
        for (Match session : sessionNamesToAssignments.values()) {
            int sessionSize = session.getSessionSize();
            if (sessionSize > MAX_SESSION_SIZE && sessionSize > sizeOfMostOverCapacitySession) {
                mostOverCapacitySession = session;
                sizeOfMostOverCapacitySession = sessionSize;
            }
        }
        return mostOverCapacitySession;
    }

    private static void bringSessionDownToMaxSize(Match overCapacitySession,
                                                  Map<String, Match> sessionNamesToAssignments,
                                                  List<SinglePersonPreferences> parsedPreferences) {
        int numReassignmentsNeeded = overCapacitySession.getSessionSize() - MAX_SESSION_SIZE;
        List<SinglePersonPreferences> peopleWhoCanBeReassigned =
                getPeopleWithSecondChoiceAvailable(overCapacitySession.sessionName, sessionNamesToAssignments,
                        parsedPreferences);

        if (peopleWhoCanBeReassigned.size() < numReassignmentsNeeded) {
            throw new ReassignmentFailureException("There are not enough people who can be moved to their 2nd choice session");
        } else {
            doReassignments(peopleWhoCanBeReassigned, sessionNamesToAssignments, numReassignmentsNeeded);
        }
    }

    private static List<SinglePersonPreferences> getPeopleWithSecondChoiceAvailable(String nameOfOverCapacitySession,
                                                                                    Map<String, Match> sessionNamesToAssignments,
                                                                                    List<SinglePersonPreferences> parsedPreferences) {
        List<SinglePersonPreferences> canBeReassigned = new ArrayList<>();
        for (SinglePersonPreferences personPreference : parsedPreferences) {
            if (nameOfOverCapacitySession.equals(personPreference.getFirstChoiceSession()) &&
                    sessionHasCapacity(personPreference.getSecondChoiceSession(), sessionNamesToAssignments)) {
                canBeReassigned.add(personPreference);
            }
        }
        return canBeReassigned;
    }

    private static boolean sessionHasCapacity(String sessionName, Map<String, Match> sessionNamesToAssignments) {
        return sessionNamesToAssignments.get(sessionName).getSessionSize() < MAX_SESSION_SIZE;
    }

    private static void doReassignments(List<SinglePersonPreferences> peopleWhoCanBeReassigned,
                                        Map<String, Match> sessionNamesToAssignments, int numReassignmentsNeeded) {
        for (SinglePersonPreferences personPreference : peopleWhoCanBeReassigned) {
            if (!peopleAlreadyReassignedOnce.contains(personPreference.name)) {
                sessionNamesToAssignments.get(personPreference.getFirstChoiceSession()).removePerson(personPreference.name);
                sessionNamesToAssignments.get(personPreference.getSecondChoiceSession()).assignPerson(personPreference.name);
                peopleAlreadyReassignedOnce.add(personPreference.name);
                numReassignmentsNeeded--;
            }
            if (numReassignmentsNeeded == 0) {
                break;
            }
        }
        if (numReassignmentsNeeded > 0) {
            throw new ReassignmentFailureException("There are not enough people to reassign who haven't already been " +
                    "reassigned before. Number of reassignments still needed: " + numReassignmentsNeeded);
        }
    }

    private static void printAssignments(Map<String, Match> sessionNamesToAssignments) {
        for (Match session : sessionNamesToAssignments.values()) {
            System.out.println(session.getSessionSize() + " PEOPLE ASSIGNED TO SESSION '" + session.sessionName.toUpperCase() + "':");
            for (String name : session.assignedPeople) {
                System.out.println(name);
            }
            System.out.println();
        }
    }

}
