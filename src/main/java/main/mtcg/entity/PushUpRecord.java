package main.mtcg.entity;

public class PushUpRecord {
    private int count;
    private int duration;
    private int userId;

    public PushUpRecord(int count, int duration, int userId) {
        this.count = count;
        this.duration = duration;
        this.userId=userId;
    }

    public int getCount() {
        return count;
    }

    public int getDuration() {
        return duration;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
