package com.example.shick.stepcounter;

import static com.example.shick.stepcounter.TimeRecorder.STATE_PAUSE;
import static com.example.shick.stepcounter.TimeRecorder.STATE_START;
import static com.example.shick.stepcounter.TimeRecorder.STATE_STOP;

/**
 * Created by Jack on 2017/5/15.
 */

public class StepRecorder {
    private long stepCount;
    private long actualStep;
    private long stepStamp;

    public StepRecorder() {
        stepCount = 0;
        actualStep = 0;
        stepStamp = 0;
    }

    public void reset() {
        stepCount = 0;
        actualStep = 0;
        stepStamp = 0;
    }

    public void updateActualStep(long val) {
        actualStep = val;
    }

    public void updateStepCount() {
        stepCount = actualStep - stepStamp;
    }

    public void updateStepStamp(int state) {
        if (state == STATE_STOP) {
            stepStamp = actualStep;
        } else if (state == STATE_PAUSE) {
            stepStamp = actualStep - stepCount;
        }
    }

    public long getStepCount() {
        return stepCount;
    }
}
