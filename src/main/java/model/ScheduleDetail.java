package model;

public class ScheduleDetail {
	private int scheduleId;       // khóa chính AUTO_INCREMENT
    private String classId;       // khóa ngoại -> CourseClass.class_id
    private String dayOfWeek;     // ENUM('Mon','Tue','Wed','Thu','Fri','Sat','Sun')
    private int startPeriod;      // tiết bắt đầu
    private int endPeriod;        // tiết kết thúc
    private String room;          // phòng học

    public ScheduleDetail() {
    }

    public ScheduleDetail(int scheduleId, String classId, String dayOfWeek, int startPeriod, int endPeriod, String room) {
        this.scheduleId = scheduleId;
        this.classId = classId;
        this.dayOfWeek = dayOfWeek;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.room = room;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(int startPeriod) {
        this.startPeriod = startPeriod;
    }

    public int getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(int endPeriod) {
        this.endPeriod = endPeriod;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

}
