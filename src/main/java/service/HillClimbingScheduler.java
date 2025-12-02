package service;

import model.CourseClassScheduleDTO;
import model.ScheduleDetail;

import java.security.SecureRandom;
import java.util.*;

public class HillClimbingScheduler {

    public static class Settings {
        public int minCredits = 12;
        public int maxCredits = 20;
        public int maxRestarts = 8;
        public int maxStepsPerRestart = 500;
        public int sidewaysLimit = 10;
        public long seed = 42L;
    }

    public static class Solution {
        public List<CourseClassScheduleDTO> classes = new ArrayList<>();
        public int totalCredits;
        public int score;
        public int daysUsed;
        public int totalBreaks;

        @Override
        public String toString() {
            return "Solution{credits=" + totalCredits + ", score=" + score +
                    ", days=" + daysUsed + ", breaks=" + totalBreaks +
                    ", count=" + classes.size() + "}";
        }
    }

    private static final Map<String, Integer> DAY_IDX = Map.of(
            "Mon", 0, "Tue", 1, "Wed", 2, "Thu", 3, "Fri", 4, "Sat", 5, "Sun", 6
    );
    private static final int DAYS = 7;
    private static final int MAX_PERIOD = 16;

    public Solution suggest(List<CourseClassScheduleDTO> offerings, Settings settings) {
        List<CourseClassScheduleDTO> feasible = new ArrayList<>();
        for (CourseClassScheduleDTO dto : offerings) {
            Integer rem = dto.getRemainingNumber();
            if (rem == null || rem > 0) feasible.add(dto);
        }
        if (feasible.isEmpty()) return new Solution();

        Map<String, List<CourseClassScheduleDTO>> byCourse = groupByCourse(feasible);
        SecureRandom rnd = new SecureRandom();
        rnd.setSeed(settings.seed);

        Solution best = new Solution();
        int bestScore = Integer.MIN_VALUE;

        for (int r = 0; r < settings.maxRestarts; r++) {
            Candidate cand = randomFeasibleInit(byCourse, settings, rnd);
            int noImprove = 0;
            int steps = 0;

            while (steps < settings.maxStepsPerRestart) {
                steps++;
                NeighborResult neigh = bestImprovingNeighbor(cand, byCourse, settings, rnd);
                if (neigh == null) break;

                if (neigh.improved) {
                    cand = neigh.next;
                    noImprove = 0;
                } else {
                    if (++noImprove > settings.sidewaysLimit) break;
                    cand = neigh.next;
                }
            }

            Solution sol = cand.toSolution();
            if (sol.score > bestScore) {
                bestScore = sol.score;
                best = sol;
            }
        }
        return best;
    }

    private Map<String, List<CourseClassScheduleDTO>> groupByCourse(List<CourseClassScheduleDTO> list) {
        Map<String, List<CourseClassScheduleDTO>> map = new HashMap<>();
        for (CourseClassScheduleDTO dto : list) {
            map.computeIfAbsent(dto.getCourseId(), k -> new ArrayList<>()).add(dto);
        }
        return map;
    }

    private static class Candidate {
        Map<String, CourseClassScheduleDTO> selected = new HashMap<>();
        int[] dayMask = new int[DAYS];
        int credits = 0;
        int score = Integer.MIN_VALUE;
        int daysUsed = 0;
        int breaks = 0;

        void recomputeScore() {
            int days = 0;
            int breaksTotal = 0;

            for (int d = 0; d < DAYS; d++) {
                int mask = dayMask[d];
                if (mask != 0) {
                    days++;
                    int first = firstBit(mask);
                    int last = lastBit(mask);
                    int span = (last - first + 1);
                    int count = Integer.bitCount(mask);
                    breaksTotal += Math.max(0, span - count);
                }
            }

            this.daysUsed = days;
            this.breaks = breaksTotal;
            this.score = 1000 * credits - 10 * breaksTotal - 5 * days;
        }

        Solution toSolution() {
            Solution s = new Solution();
            s.classes = new ArrayList<>(selected.values());
            s.totalCredits = credits;
            s.daysUsed = daysUsed;
            s.totalBreaks = breaks;
            s.score = score;
            return s;
        }
    }

    private static int firstBit(int mask) {
        if (mask == 0) return -1;
        return Integer.numberOfTrailingZeros(mask);
    }
    private static int lastBit(int mask) {
        if (mask == 0) return -1;
        return 31 - Integer.numberOfLeadingZeros(mask);
    }

    private Candidate randomFeasibleInit(Map<String, List<CourseClassScheduleDTO>> byCourse,
                                         Settings settings, Random rnd) {
        Candidate cand = new Candidate();
        List<String> courses = new ArrayList<>(byCourse.keySet());
        Collections.shuffle(courses, rnd);

        for (String courseId : courses) {
            if (cand.credits >= settings.maxCredits) break;

            List<CourseClassScheduleDTO> sections = byCourse.get(courseId);
            Collections.shuffle(sections, rnd);

            for (CourseClassScheduleDTO sec : sections) {
                int credits = safeInt(sec.getCredits());
                if (cand.credits + credits > settings.maxCredits) continue;
                if (conflict(cand.dayMask, sec)) continue;

                applyAdd(cand, sec);
                break;
            }
        }
        cand.recomputeScore();
        return cand;
    }

    private static class NeighborResult {
        Candidate next;
        boolean improved;
    }

    private NeighborResult bestImprovingNeighbor(Candidate cur,
                                                 Map<String, List<CourseClassScheduleDTO>> byCourse,
                                                 Settings settings, Random rnd) {
        NeighborResult best = null;
        int bestScore = cur.score;
        boolean foundBetter = false;

        Set<String> chosenCourses = cur.selected.keySet();
        List<String> notChosen = new ArrayList<>(byCourse.keySet());
        notChosen.removeAll(chosenCourses);

        // Thay lớp cùng môn
        for (String cId : chosenCourses) {
            CourseClassScheduleDTO currentSec = cur.selected.get(cId);
            for (CourseClassScheduleDTO other : byCourse.getOrDefault(cId, List.of())) {
                if (other.getClassId().equals(currentSec.getClassId())) continue;

                Candidate next = cloneCand(cur);
                applyRemove(next, currentSec);

                if (safeInt(other.getCredits()) + next.credits > settings.maxCredits) {
                    applyAdd(next, currentSec);
                    continue;
                }
                if (conflict(next.dayMask, other)) {
                    applyAdd(next, currentSec);
                    continue;
                }
                applyAdd(next, other);
                next.recomputeScore();

                if (best == null || next.score > bestScore) {
                    best = new NeighborResult();
                    best.next = next;
                    bestScore = next.score;
                    foundBetter = next.score > cur.score;
                }
            }
        }

        // Thêm một môn mới
        if (cur.credits < settings.maxCredits) {
            Collections.shuffle(notChosen, rnd);
            for (String cId : notChosen) {
                for (CourseClassScheduleDTO sec : byCourse.get(cId)) {
                    int cred = safeInt(sec.getCredits());
                    if (cur.credits + cred > settings.maxCredits) continue;
                    if (conflict(cur.dayMask, sec)) continue;

                    Candidate next = cloneCand(cur);
                    applyAdd(next, sec);
                    next.recomputeScore();

                    if (best == null || next.score > bestScore) {
                        best = new NeighborResult();
                        best.next = next;
                        bestScore = next.score;
                        foundBetter = next.score > cur.score;
                    }
                    break;
                }
            }
        }

        // Bỏ một lớp
        for (String cId : chosenCourses) {
            CourseClassScheduleDTO sec = cur.selected.get(cId);
            Candidate next = cloneCand(cur);
            applyRemove(next, sec);
            next.recomputeScore();

            if (best == null || next.score > bestScore) {
                best = new NeighborResult();
                best.next = next;
                bestScore = next.score;
                foundBetter = next.score > cur.score;
            }
        }

        if (best != null) best.improved = foundBetter;
        return best;
    }

    private static void applyAdd(Candidate cand, CourseClassScheduleDTO sec) {
        cand.selected.put(sec.getCourseId(), sec);
        cand.credits += safeInt(sec.getCredits());

        for (ScheduleDetail s : safeSchedules(sec)) {
            Integer d = DAY_IDX.get(s.getDayOfWeek());
            if (d == null) continue;
            cand.dayMask[d] |= buildMaskForRange(s.getStartPeriod(), s.getEndPeriod());
        }
    }

    private static void applyRemove(Candidate cand, CourseClassScheduleDTO sec) {
        cand.selected.remove(sec.getCourseId());
        cand.credits -= safeInt(sec.getCredits());

        Arrays.fill(cand.dayMask, 0);
        for (CourseClassScheduleDTO x : cand.selected.values()) {
            for (ScheduleDetail s : safeSchedules(x)) {
                Integer d = DAY_IDX.get(s.getDayOfWeek());
                if (d == null) continue;
                cand.dayMask[d] |= buildMaskForRange(s.getStartPeriod(), s.getEndPeriod());
            }
        }
    }

    private static boolean conflict(int[] dayMask, CourseClassScheduleDTO sec) {
        for (ScheduleDetail s : safeSchedules(sec)) {
            Integer d = DAY_IDX.get(s.getDayOfWeek());
            if (d == null) continue;
            int mask = buildMaskForRange(s.getStartPeriod(), s.getEndPeriod());
            if ((dayMask[d] & mask) != 0) return true;
        }
        return false;
    }

    private static int buildMaskForRange(int start, int end) {
        start = Math.max(1, Math.min(start, MAX_PERIOD));
        end = Math.max(1, Math.min(end, MAX_PERIOD));
        if (end < start) { int t = start; start = end; end = t; }
        int len = end - start + 1;
        int mask = (len >= 32) ? -1 : ((1 << len) - 1);
        return mask << (start - 1);
    }

    private static int safeInt(Integer i) { return i == null ? 0 : i; }
    private static List<ScheduleDetail> safeSchedules(CourseClassScheduleDTO dto) {
        List<ScheduleDetail> l = dto.getScheduleList();
        return l == null ? List.of() : l;
    }
    private static Candidate cloneCand(Candidate c) {
        Candidate n = new Candidate();
        n.selected = new HashMap<>(c.selected);
        n.dayMask = Arrays.copyOf(c.dayMask, c.dayMask.length);
        n.credits = c.credits;
        n.score = c.score;
        n.daysUsed = c.daysUsed;
        n.breaks = c.breaks;
        return n;
    }
}