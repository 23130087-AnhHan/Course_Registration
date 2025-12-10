package service;

import model.CourseClassScheduleDTO;
import model.ScheduleDetail;

import java.security.SecureRandom;
import java.util.*;

/**
 * HillClimbingScheduler:
 * Lập lịch thời khóa biểu cho sinh viên sử dụng thuật toán leo đồi.
 * Đảm bảo không trùng lịch học và không giáo viên nào dạy 2 lớp cùng thời điểm.
 */
public class HillClimbingScheduler {

    /**
     * Settings: thông số cấu hình thuật toán.
     */
    public static class Settings {
        public int minCredits = 12;         // Số tín chỉ tối thiểu
        public int maxCredits = 20;         // Số tín chỉ tối đa
        public int maxRestarts = 8;         // Số lần khởi tạo lại
        public int maxStepsPerRestart = 500;// Số bước leo đồi mỗi lần khởi tạo
        public int sidewaysLimit = 10;      // Số bước cho phép đứng yên ở cùng giá trị score
        public long seed = 42L;             // Seed cho random
    }

    /**
     * Solution: cấu trúc lưu thời khóa biểu tối ưu tìm được
     */
    public static class Solution {
        public List<CourseClassScheduleDTO> classes = new ArrayList<>(); // Danh sách lớp học đã chọn
        public int totalCredits; // Tổng số tín chỉ
        public int score;        // Điểm đánh giá chất lượng lịch
        public int daysUsed;     // Số ngày cần đến trường
        public int totalBreaks;  // Tổng số lần nghỉ giữa các tiết

        @Override
        public String toString() {
            return "Solution{credits=" + totalCredits + ", score=" + score +
                    ", days=" + daysUsed + ", breaks=" + totalBreaks +
                    ", count=" + classes.size() + "}";
        }
    }

    // Ánh xạ các ngày sang index trong mảng
    private static final Map<String, Integer> DAY_IDX = Map.of(
            "Mon", 0, "Tue", 1, "Wed", 2, "Thu", 3, "Fri", 4, "Sat", 5, "Sun", 6
    );
    private static final int DAYS = 7;            // Số ngày trong tuần
    private static final int MAX_PERIOD = 16;     // Số tiết tối đa một ngày

    /**
     * suggest: Hàm chính thực hiện lập lịch.
     * Đầu vào là danh sách các lớp có thể chọn và settings cấu hình,
     * Trả về Solution tốt nhất tìm được.
     */
    public Solution suggest(List<CourseClassScheduleDTO> offerings, Settings settings) {
        // Lọc các lớp còn chỗ và đủ điều kiện
        List<CourseClassScheduleDTO> feasible = new ArrayList<>();
        for (CourseClassScheduleDTO dto : offerings) {
            Integer rem = dto.getRemainingNumber();
            if (rem == null || rem > 0) feasible.add(dto);
        }
        if (feasible.isEmpty()) return new Solution();

        // Nhóm các lớp theo môn học
        Map<String, List<CourseClassScheduleDTO>> byCourse = groupByCourse(feasible);
        SecureRandom rnd = new SecureRandom();
        rnd.setSeed(settings.seed);

        // Solution tốt nhất tìm được
        Solution best = new Solution();
        int bestScore = Integer.MIN_VALUE;

        // Lặp lại nhiều lần để tránh kẹt ở local optimum
        for (int r = 0; r < settings.maxRestarts; r++) {
            Candidate cand = randomFeasibleInit(byCourse, settings, rnd);
            int noImprove = 0;
            int steps = 0;

            while (steps < settings.maxStepsPerRestart) {
                steps++;
                NeighborResult neigh = bestImprovingNeighbor(cand, byCourse, settings, rnd);
                if (neigh == null) break;

                // Nếu cải thiện được điểm số thì reset bộ đếm sideways
                if (neigh.improved) {
                    cand = neigh.next;
                    noImprove = 0;
                } else {
                    // Nếu liên tục không cải thiện, dừng khi vượt sidewaysLimit
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

    /**
     * Nhóm các lớp theo môn học
     */
    private Map<String, List<CourseClassScheduleDTO>> groupByCourse(List<CourseClassScheduleDTO> list) {
        Map<String, List<CourseClassScheduleDTO>> map = new HashMap<>();
        for (CourseClassScheduleDTO dto : list) {
            map.computeIfAbsent(dto.getCourseId(), k -> new ArrayList<>()).add(dto);
        }
        return map;
    }

    /**
     * Candidate: biểu diễn trạng thái hiện tại của lịch (giải pháp tạm thời)
     */
    private static class Candidate {
        Map<String, CourseClassScheduleDTO> selected = new HashMap<>();      // Các lớp đã chọn (key: courseId)
        int[] dayMask = new int[DAYS]; // Bitmask tiết đã học cho từng ngày (giống lịch sinh viên)
        Map<String, int[]> teacherSchedule = new HashMap<>(); // Lịch dạy của giáo viên (mỗi giáo viên: mảng bitmask cho từng ngày)
        int credits = 0;      // Tổng số tín chỉ
        int score = Integer.MIN_VALUE; // Điểm đánh giá hiện tại
        int daysUsed = 0;     // Số ngày phải đi học
        int breaks = 0;       // Số lần có tiết trống/nghỉ

        /**
         * Tính lại điểm đánh giá, breaks, daysUsed của lịch này
         */
        void recomputeScore() {
            int days = 0;
            int breaksTotal = 0;

            for (int d = 0; d < DAYS; d++) {
                int mask = dayMask[d];
                if (mask != 0) {
                    days++;
                    int first = firstBit(mask);
                    int last = lastBit(mask);
                    int span = (last - first + 1); // Khoảng từ tiết đầu đến tiết cuối của ngày đó
                    int count = Integer.bitCount(mask); // Số tiết thực sự học
                    breaksTotal += Math.max(0, span - count); // break là những tiết trống giữa
                }
            }

            this.daysUsed = days;
            this.breaks = breaksTotal;
            this.score = 1000 * credits - 10 * breaksTotal - 5 * days; // Score tối ưu: ưu tiên tín chỉ, giảm breaks và ngày học
        }

        /**
         * Chuyển Candidate thành Solution (giải cuối cùng)
         */
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

    /**
     * Tìm tiết đầu tiên xuất hiện trong bitmask
     */
    private static int firstBit(int mask) {
        if (mask == 0) return -1;
        return Integer.numberOfTrailingZeros(mask);
    }

    /**
     * Tìm tiết cuối cùng xuất hiện trong bitmask
     */
    private static int lastBit(int mask) {
        if (mask == 0) return -1;
        return 31 - Integer.numberOfLeadingZeros(mask);
    }

    /**
     * Sinh một Candidate hợp lệ ngẫu nhiên để bắt đầu quá trình leo đồi
     */
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
                if (conflict(cand.dayMask, cand.teacherSchedule, sec)) continue;

                applyAdd(cand, sec);
                break;
            }
        }
        cand.recomputeScore();
        return cand;
    }

    /**
     * Biểu diễn kết quả láng giềng của một trạng thái Candidate
     */
    private static class NeighborResult {
        Candidate next;      // Trạng thái Candidate mới
        boolean improved;    // Có cải thiện điểm score hay không
    }

    /**
     * Sinh tất cả các láng giềng có thể, chọn láng giềng tốt nhất (score cao nhất)
     * Các thao tác: đổi lớp cùng môn, thêm môn mới, bỏ môn đang chọn
     */
    private NeighborResult bestImprovingNeighbor(Candidate cur,
                                                 Map<String, List<CourseClassScheduleDTO>> byCourse,
                                                 Settings settings, Random rnd) {
        NeighborResult best = null;
        int bestScore = cur.score;
        boolean foundBetter = false;

        Set<String> chosenCourses = cur.selected.keySet();
        List<String> notChosen = new ArrayList<>(byCourse.keySet());
        notChosen.removeAll(chosenCourses);

        // Đổi lớp cùng môn (section swap)
        for (String cId : chosenCourses) {
            CourseClassScheduleDTO currentSec = cur.selected.get(cId);
            for (CourseClassScheduleDTO other : byCourse.getOrDefault(cId, List.of())) {
                if (other.getClassId().equals(currentSec.getClassId())) continue;

                Candidate next = cloneCand(cur);
                applyRemove(next, currentSec);

                if (safeInt(other.getCredits()) + next.credits > settings.maxCredits) {
                    applyAdd(next, currentSec); // hoàn tác nếu vi phạm tín chỉ
                    continue;
                }
                if (conflict(next.dayMask, next.teacherSchedule, other)) {
                    applyAdd(next, currentSec); // hoàn tác nếu vi phạm xung đột
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

        // Thêm môn mới (add course)
        if (cur.credits < settings.maxCredits) {
            Collections.shuffle(notChosen, rnd);
            for (String cId : notChosen) {
                for (CourseClassScheduleDTO sec : byCourse.get(cId)) {
                    int cred = safeInt(sec.getCredits());
                    if (cur.credits + cred > settings.maxCredits) continue;
                    if (conflict(cur.dayMask, cur.teacherSchedule, sec)) continue;

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

        // Bỏ một môn (remove course)
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

    /**
     * Thêm một lớp vào lịch Candidate, cập nhật bitmask cả sinh viên và lịch giáo viên
     */
    private static void applyAdd(Candidate cand, CourseClassScheduleDTO sec) {
        cand.selected.put(sec.getCourseId(), sec);
        cand.credits += safeInt(sec.getCredits());

        for (ScheduleDetail s : safeSchedules(sec)) {
            Integer d = DAY_IDX.get(s.getDayOfWeek());
            if (d == null) continue;
            int mask = buildMaskForRange(s.getStartPeriod(), s.getEndPeriod());
            cand.dayMask[d] |= mask;

            // Cập nhật lịch giáo viên
            String teacherId = sec.getTeacherId();
            if (teacherId != null && !teacherId.isEmpty()) {
                int[] teaMask = cand.teacherSchedule.computeIfAbsent(teacherId, k -> new int[DAYS]);
                teaMask[d] |= mask;
            }
        }
    }

    /**
     * Xóa một lớp khỏi lịch Candidate, cập nhật lại toàn bộ lịch học và lịch giáo viên
     */
    private static void applyRemove(Candidate cand, CourseClassScheduleDTO sec) {
        cand.selected.remove(sec.getCourseId());
        cand.credits -= safeInt(sec.getCredits());

        Arrays.fill(cand.dayMask, 0);       // Reset lịch học sinh viên
        cand.teacherSchedule.clear();       // Xóa lịch dạy giáo viên

        // Tính lại toàn bộ lịch với các lớp còn lại
        for (CourseClassScheduleDTO x : cand.selected.values()) {
            for (ScheduleDetail s : safeSchedules(x)) {
                Integer d = DAY_IDX.get(s.getDayOfWeek());
                if (d == null) continue;
                int mask = buildMaskForRange(s.getStartPeriod(), s.getEndPeriod());
                cand.dayMask[d] |= mask;

                String teacherId = x.getTeacherId();
                if (teacherId != null && !teacherId.isEmpty()) {
                    int[] teaMask = cand.teacherSchedule.computeIfAbsent(teacherId, k -> new int[DAYS]);
                    teaMask[d] |= mask;
                }
            }
        }
    }

    /**
     * conflict: kiểm tra xung đột về thời gian học (cho sinh viên) và thời gian dạy (giáo viên)
     */
    private static boolean conflict(int[] dayMask, Map<String, int[]> teacherSchedule, CourseClassScheduleDTO sec) {
        for (ScheduleDetail s : safeSchedules(sec)) {
            Integer d = DAY_IDX.get(s.getDayOfWeek());
            if (d == null) continue;
            int mask = buildMaskForRange(s.getStartPeriod(), s.getEndPeriod());

            // Xung đột lịch học sinh viên
            if ((dayMask[d] & mask) != 0) return true;

            // Xung đột lịch dạy giáo viên
            String teacherId = sec.getTeacherId();
            if (teacherId != null && !teacherId.isEmpty() && teacherSchedule.containsKey(teacherId)) {
                if ((teacherSchedule.get(teacherId)[d] & mask) != 0) return true;
            }
        }
        return false;
    }

    /**
     * Tạo mask bit cho các tiết từ start đến end (giả sử start, end bắt đầu từ 1)
     */
    private static int buildMaskForRange(int start, int end) {
        start = Math.max(1, Math.min(start, MAX_PERIOD));
        end = Math.max(1, Math.min(end, MAX_PERIOD));
        if (end < start) { int t = start; start = end; end = t; }
        int len = end - start + 1;
        int mask = (len >= 32) ? -1 : ((1 << len) - 1);
        return mask << (start - 1);
    }

    /**
     * safeInt: lấy giá trị int hoặc trả về 0 nếu null
     */
    private static int safeInt(Integer i) { return i == null ? 0 : i; }

    /**
     * safeSchedules: lấy danh sách lịch học hoặc trả về list rỗng nếu null
     */
    private static List<ScheduleDetail> safeSchedules(CourseClassScheduleDTO dto) {
        List<ScheduleDetail> l = dto.getScheduleList();
        return l == null ? List.of() : l;
    }

    /**
     * cloneCand: Sao chép một Candidate (bao gồm cả lịch giáo viên)
     */
    private static Candidate cloneCand(Candidate c) {
        Candidate n = new Candidate();
        n.selected = new HashMap<>(c.selected);
        n.dayMask = Arrays.copyOf(c.dayMask, c.dayMask.length);
        // Sao chép sâu lịch giáo viên
        n.teacherSchedule = new HashMap<>();
        for (Map.Entry<String, int[]> entry : c.teacherSchedule.entrySet()) {
            n.teacherSchedule.put(entry.getKey(), Arrays.copyOf(entry.getValue(), entry.getValue().length));
        }
        n.credits = c.credits;
        n.score = c.score;
        n.daysUsed = c.daysUsed;
        n.breaks = c.breaks;
        return n;
    }
}