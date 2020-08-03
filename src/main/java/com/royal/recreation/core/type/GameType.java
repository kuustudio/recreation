package com.royal.recreation.core.type;

import com.royal.recreation.core.ActionInfo;
import com.royal.recreation.core.IGame;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum GameType implements IGame {

    CQSSC(1, "重庆时时彩", 20, 5 * 60) {
        public boolean inAction(LocalDateTime dateTime) {
            int second = LocalDateTime.now().toLocalTime().toSecondOfDay();
            return (second > 1800 && second < 11400) || (second > 2700 && second <= 85800);
        }

        public long getNextNo(long actionNo) {
            return actionNo % 100 == 59 ? actionNo + 942 : actionNo + 1;
        }

        public List<ActionInfo> actionInfoList(long currentNo, LocalDateTime currentEndTime, LocalDateTime maxEndTime) {
            List<ActionInfo> result = new ArrayList<>();
            while (currentEndTime.isBefore(maxEndTime)) {
                long d = currentNo % 100;
                if (d == 9) {
                    currentEndTime = currentEndTime.plusMinutes(260);
                } else if (d == 59) {
                    currentEndTime = currentEndTime.plusMinutes(40);
                } else {
                    currentEndTime = currentEndTime.plusMinutes(getDuration());
                }
                currentNo = getNextNo(currentNo);
                if (currentEndTime.isBefore(maxEndTime)) {
                    result.add(new ActionInfo(currentNo, currentEndTime));
                } else {
                    break;
                }
            }
            return result;

        }
    },
    XJSSC(2, "新疆时时彩", 20, 5 * 60) {
        public boolean inAction(LocalDateTime dateTime) {
            int second = LocalDateTime.now().toLocalTime().toSecondOfDay();
            return second > 37200 || second < 7200;
        }

        public long getNextNo(long actionNo) {
            return actionNo % 100 == 48 ? actionNo + 53 : actionNo + 1;
        }

        public List<ActionInfo> actionInfoList(long currentNo, LocalDateTime currentEndTime, LocalDateTime maxEndTime) {
            List<ActionInfo> result = new ArrayList<>();
            while (true) {
                long d = currentNo % 100;
                if (d == 48) {
                    currentEndTime = currentEndTime.plusMinutes(500);
                } else {
                    currentEndTime = currentEndTime.plusMinutes(getDuration());
                }
                currentNo = getNextNo(currentNo);
                if (currentEndTime.isBefore(maxEndTime)) {
                    result.add(new ActionInfo(currentNo, currentEndTime));
                } else {
                    break;
                }

            }
            return result;
        }
    },
    Australia(12, "澳洲幸运5", 5, 60) {
        public boolean inAction(LocalDateTime dateTime) {
            return true;
        }

        public long getNextNo(long actionNo) {
            return actionNo + 1;
        }

        public List<ActionInfo> actionInfoList(long currentNo, LocalDateTime currentEndTime, LocalDateTime maxEndTime) {
            List<ActionInfo> result = new ArrayList<>();
            while (true) {
                currentEndTime = currentEndTime.plusMinutes(getDuration());
                currentNo = getNextNo(currentNo);
                if (currentEndTime.isBefore(maxEndTime)) {
                    result.add(new ActionInfo(currentNo, currentEndTime));
                } else {
                    break;
                }
            }
            return result;
        }
    };


    private static final Map<Integer, GameType> data;
    private final int id;
    private final String name;
    // 开奖间隔时间单位分
    private final int duration;
    // 开机大约需要时间秒
    private final int kjTime;

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

}