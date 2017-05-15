package com.example.shick.stepcounter;

/**
 * Created by Jack on 2017/5/15.
 */

public class TimeRecorder {
    private int _state;
    private long _time;

    public static final int STATE_START = 2;
    public static final int STATE_PAUSE = 1;
    public static final int STATE_STOP = 0;

    public TimeRecorder() {
        _state = STATE_STOP;
        _time = 0;
    }

    public void setTime(long time) {
        _time = time;
    }

    public long getTime() {
        return _time;
    }

    public int getState() {
        return _state;
    }

    public boolean isStart() {
        return _state == STATE_START;
    }

    public boolean isPause() {
        return _state == STATE_PAUSE;
    }

    public boolean isStop() {
        return _state == STATE_STOP;
    }

    public void stop() {
        _state = STATE_STOP;
        _time = 0;
    }

    public void start() {
        _state = STATE_START;
    }

    public void pause() {
        _state = STATE_PAUSE;
    }

    public void setState(int state) {
        _state = state;
    }

    public void reset() {
        _state = STATE_STOP;
        _time = 0;
    }

    public String getFormattedTime() {
        long hour = _time / 360000;
        long minute = (_time - hour* 360000)/ 6000;
        long second = (_time - hour * 360000 - minute*6000) / 100;
        String h = hour < 10 ? "0" + String.valueOf(hour) : String.valueOf(hour);
        String m = minute < 10 ? "0" + String.valueOf(minute) : String.valueOf(minute);
        String s = second < 10 ? "0" + String.valueOf(second) : String.valueOf(second);
        return h + ":" + m + ":" + s;
    }
}
