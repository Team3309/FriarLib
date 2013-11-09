/*
 * Copyright (c) 2013, FRC Team 3309 All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.team3309.friarlib.motors;

import edu.wpi.first.wpilibj.SpeedController;

public class MultiSpeedController implements SpeedController {

    private boolean negateInput = false;
    private SpeedController[] motors = null;
    private boolean[] motorsReverse = null;
    private double x = 0;

    private MultiSpeedController() {
    }

    @Override
    public double get() {
        return x;
    }

    @Override
    public void set(double speed, byte syncGroup) {
        set(speed);
    }

    @Override
    public void set(double x) {
        if (negateInput)
            x = -x;
        this.x = x;
        for (int i = 0; i < motors.length; i++) {
            if (motorsReverse[i])
                motors[i].set(-x);
            else
                motors[i].set(x);
        }
    }

    @Override
    public void disable() {
        set(0);
    }

    @Override
    public void pidWrite(double output) {
        set(output);
    }

    public static class Builder {

        private MultiSpeedController controller = null;

        public Builder() {
            controller = new MultiSpeedController();
        }

        public Builder motors(SpeedController[] motors) {
            controller.motors = motors;
            return this;
        }

        public Builder reverse(int motor) {
            controller.motorsReverse[motor] = true;
            return this;
        }

        public Builder reverseAll() {
            for (int i = 0; i < controller.motorsReverse.length; i++) {
                controller.motorsReverse[i] = true;
            }
            return this;
        }

        public Builder reverseEveryOther() {
            for (int i = 0; i < controller.motorsReverse.length / 2; i++) {
                controller.motorsReverse[i] = false;
                controller.motorsReverse[i] = true;
            }
            return this;
        }

        public Builder negateInput() {
            controller.negateInput = true;
            return this;
        }

        public MultiSpeedController build() {
            return controller;
        }

    }

}
