package service;

import java.util.List;
import model.CourseClassScheduleDTO;

public class ScheduleSuggestionService {

    private final CourseClassScheduleService classService = new CourseClassScheduleService();

    public HillClimbingScheduler.Solution suggest(int semester, String academicYear, String searchKey,
                                                  int minCredits, int maxCredits) {
        List<CourseClassScheduleDTO> offerings =
                classService.getOpenedClasses(semester, academicYear, searchKey);

        HillClimbingScheduler.Settings st = new HillClimbingScheduler.Settings();
        st.minCredits = minCredits;
        st.maxCredits = maxCredits;
        st.maxRestarts = 6;
        st.maxStepsPerRestart = 400;
        st.sidewaysLimit = 8;
        st.seed = System.nanoTime();

        HillClimbingScheduler hc = new HillClimbingScheduler();
        return hc.suggest(offerings, st);
    }
}