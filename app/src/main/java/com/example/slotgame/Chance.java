package com.example.slotgame;

import android.util.Log;

public class Chance {
    int[] chance = new int[7];
    private String TAG = Chance.class.getSimpleName();

    /**
     * 初始化機率
     */
    public void init() {
        this.chance[0] = 18;
        this.chance[1] = 18;
        this.chance[2] = 18;
        this.chance[3] = 15;
        this.chance[4] = 15;
        this.chance[5] = 10;
        this.chance[6] = 6;
    }

    /**
     * 增加chance 1~3的機率1，並減少chance 4~6的機率1，以chance 7 也就是 6 為最低基準
     */
    public void add_chance() {
        int times = 0;
        if (chance[3] == 6) {
            times = times;
        } else {
            chance[3] -= 1;
            times += 1;
        }

        if (chance[4] == 6) {
            times = times;
        } else {
            chance[4] -= 1;
            times += 1;
        }

        if (chance[5] == 6) {
            times = times;
        } else {
            chance[5] -= 1;
            times += 1;
        }
        for (int j = 0; j < times; j++) {
            chance[mini()] += 1;
        }

        Log.d(TAG, "add_chance: " + chance[0] + " | " + chance[1] + " | " + chance[2] + " | " + chance[3] + " | " + chance[4] + " | " + chance[5] + " | " + chance[6]);
    }

    /**
     * 找出 chance 0 1 2順序的最小值
     * @return 0 1 2最小值位置
     */
    private int mini() {
        if (chance[1] < chance[0])
            return 1;
        if (chance[2] < chance[1])
            return 2;
        return 0;
    }

    /**
     * 從0到num的機率總和
     * @param num
     * @return 總和
     */
    public int sum(int num) {
        int sum = 0;
        for (int i = 0; i <= num; i++) {
            sum += chance[i];
        }
        return sum;
    }

    public int getChance_1() {
        return chance[0];
    }

    public void setChance_1(int chance_1) {
        this.chance[0] = chance_1;
    }

    public int getChance_2() {
        return chance[1];
    }

    public void setChance_2(int chance_2) {
        this.chance[1] = chance_2;
    }

    public int getChance_3() {
        return chance[2];
    }

    public void setChance_3(int chance_3) {
        this.chance[2] = chance_3;
    }

    public int getChance_4() {
        return chance[3];
    }

    public void setChance_4(int chance_4) {
        this.chance[3] = chance_4;
    }

    public int getChance_5() {
        return chance[4];
    }

    public void setChance_5(int chance_5) {
        this.chance[4] = chance_5;
    }

    public int getChance_6() {
        return chance[5];
    }

    public void setChance_6(int chance_6) {
        this.chance[5] = chance_6;
    }

    public int getChance_7() {
        return chance[6];
    }

    public void setChance_7(int chance_7) {
        this.chance[6] = chance_7;
    }
}
