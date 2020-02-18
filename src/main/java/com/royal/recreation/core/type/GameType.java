package com.royal.recreation.core.type;

import com.royal.recreation.core.IGame;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public enum GameType implements IGame {

    CQSSC(1, "重庆时时彩", 20, 5*60) {
        public boolean inAction(LocalDateTime dateTime) {
            int second = LocalDateTime.now().toLocalTime().toSecondOfDay();
            return (second > 1800 && second < 11400) || (second > 2700 && second <= 85800);
        }
    },
    XJSSC(2, "新疆时时彩", 20, 5*60) {
        public boolean inAction(LocalDateTime dateTime) {
            int second = LocalDateTime.now().toLocalTime().toSecondOfDay();
            return second > 37200 || second < 7200;
        }
    },
    Australia(12, "澳洲幸运5", 5, 60) {
        public boolean inAction(LocalDateTime dateTime) {
            return true;
        }
    };


    private static final Map<Integer, GameType> data;
    private int id;
    private String name;
    // 开奖间隔时间单位分
    private int duration;
    // 开机大约需要时间秒
    private int kjTime;

    static {
        data = new HashMap<>();
        for (GameType value : GameType.values()) {
            data.put(value.id, value);
        }
    }

    GameType(int id, String name, int duration, int kjTime) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.kjTime = kjTime;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public int getKjTime() {
        return kjTime;
    }

    public static GameType find(Integer id) {
        return data.get(id);
    }

    @Override
    public boolean inAction(LocalDateTime dateTime) {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        System.out.println(PlayedType.P_458.hit("3,4,3,5,9", "总和大"));
    }

}