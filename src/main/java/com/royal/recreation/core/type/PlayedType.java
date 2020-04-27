package com.royal.recreation.core.type;

import com.royal.recreation.core.IPlayed;
import com.royal.recreation.core.entity.BonusSetting;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.royal.recreation.core.type.BonusLimitType.*;

/**
 * 记录所有玩法的中奖算法
 */
public enum PlayedType implements IPlayed {

    P_431(431, "四码定位") {
        @Override
        public int getActionNum(String actionData) {
            List<Set<Character>> charsList = PlayedType.getCharsList(actionData);
            AtomicInteger actionNum = new AtomicInteger();
            PlayedType.action4(0, 0, 4, 5, new Stack<>(), stack -> actionNum.addAndGet(
                    charsList.get(stack.elementAt(0) - 1).size()
                            * charsList.get(stack.elementAt(1) - 1).size()
                            * charsList.get(stack.elementAt(2) - 1).size()
                            * charsList.get(stack.elementAt(3) - 1).size()));
            return actionNum.get();
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA14();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            return PlayedType.action5(code, actionData, 4);
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return L_21;
        }
    },

    P_432(432, "三码定位") {
        @Override
        public int getActionNum(String actionData) {
            List<Set<Character>> charsList = PlayedType.getCharsList(actionData);
            AtomicInteger actionNum = new AtomicInteger();
            PlayedType.action4(0, 0, 3, 5, new Stack<>(), stack -> actionNum.addAndGet(charsList.get(stack.elementAt(0) - 1).size() * charsList.get(stack.elementAt(1) - 1).size() * charsList.get(stack.elementAt(2) - 1).size()));
            return actionNum.get();
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA13();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            return PlayedType.action5(code, actionData, 3);
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return L_8;
        }
    },

    P_433(433, "二码定位") {
        @Override
        public int getActionNum(String actionData) {
            List<Set<Character>> charsList = PlayedType.getCharsList(actionData);
            AtomicInteger actionNum = new AtomicInteger();
            PlayedType.action4(0, 0, 2, 5, new Stack<>(), stack -> actionNum.addAndGet(charsList.get(stack.elementAt(0) - 1).size() * charsList.get(stack.elementAt(1) - 1).size()));
            return actionNum.get();
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA12();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            return PlayedType.action5(code, actionData, 2);
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return L_7;
        }
    },
    P_435(435, "一帆风顺") {
        @Override
        public int getActionNum(String actionData) {
            Set<Character> charsList = PlayedType.getChars(actionData);
            return charsList.size();
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA15();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            return PlayedType.action6(code, actionData, 1);
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return L_11;
        }
    },
    P_436(436, "好事成双") {
        @Override
        public int getActionNum(String actionData) {
            Set<Character> charsList = PlayedType.getChars(actionData);
            return charsList.size();
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA16();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            return PlayedType.action6(code, actionData, 2);
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return L_12;
        }
    },
    P_437(437, "三星报喜") {
        @Override
        public int getActionNum(String actionData) {
            Set<Character> charsList = PlayedType.getChars(actionData);
            return charsList.size();
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA17();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            return PlayedType.action6(code, actionData, 3);
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return L_13;
        }
    },
    P_439(439, "三码不定位") {
        @Override
        public int getActionNum(String actionData) {
            Set<Character> sArr = PlayedType.getChars(actionData);
            if (sArr.size() < 3) {
                throw new IllegalStateException();
            }
            int actionNum = 1;
            for (int i = 4; i <= sArr.size(); i++) {
                actionNum *= i;
            }
            for (int i = 2; i <= sArr.size() - 3; i++) {
                actionNum /= i;
            }
            return actionNum;
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA10();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            String sArr = actionData.replaceAll("\\D", "");
            AtomicInteger actionNum = new AtomicInteger();
            PlayedType.action4(0, 0, 3, sArr.length(), new Stack<>(), stack -> {
                if (code.contains(sArr.charAt(stack.elementAt(0) - 1) + "") && code.contains(sArr.charAt(stack.elementAt(1) - 1) + "") && code.contains(sArr.charAt(stack.elementAt(2) - 1) + "")) {
                    actionNum.incrementAndGet();
                }
            });
            return actionNum.get();
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return L_10;
        }
    },
    P_440(440, "二码不定位") {
        @Override
        public int getActionNum(String actionData) {
            Set<Character> sArr = getChars(actionData);
            if (sArr.size() < 2) {
                throw new IllegalStateException();
            }
            int actionNum = 1;
            for (int i = 3; i <= sArr.size(); i++) {
                actionNum *= i;
            }
            for (int i = 2; i <= sArr.size() - 2; i++) {
                actionNum /= i;
            }
            return actionNum;
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA11();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            String sArr = actionData.replaceAll("\\D", "");
            AtomicInteger actionNum = new AtomicInteger();
            PlayedType.action4(0, 0, 2, sArr.length(), new Stack<>(), stack -> {
                if (code.contains(sArr.charAt(stack.elementAt(0) - 1) + "") && code.contains(sArr.charAt(stack.elementAt(1) - 1) + "")) {
                    actionNum.incrementAndGet();
                }
            });
            return actionNum.get();
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return L_9;
        }
    },
    P_448(448, "第一个球大小单双") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA1();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            int c = Integer.parseInt(code.substring(0, 1));
            return action1(c, actionData) ? 1 : 0;
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    P_449(449, "第二个球大小单双") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA1();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            int c = Integer.parseInt(code.substring(2, 3));
            return action1(c, actionData) ? 1 : 0;
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    P_450(450, "第三个球大小单双") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA1();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            int c = Integer.parseInt(code.substring(4, 5));
            return action1(c, actionData) ? 1 : 0;
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    P_451(451, "第四个球大小单双") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA1();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            int c = Integer.parseInt(code.substring(6, 7));
            return action1(c, actionData) ? 1 : 0;
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    P_452(452, "第五个球大小单双") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA1();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            int c = Integer.parseInt(code.substring(8, 9));
            return action1(c, actionData) ? 1 : 0;
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    P_453(453, "第一个球数字") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA2();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            return Objects.equals(code.substring(0, 1), actionData) ? 1 : 0;
        }
        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return L_21;
        }
    },
    P_454(454, "第二个球数字") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA2();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            return Objects.equals(code.substring(2, 3), actionData) ? 1 : 0;
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return L_21;
        }
    },
    P_455(455, "第三个球数字") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA2();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            return Objects.equals(code.substring(4, 5), actionData) ? 1 : 0;
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return L_21;
        }
    },
    P_456(456, "第四个球数字") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA2();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            return Objects.equals(code.substring(6, 7), actionData) ? 1 : 0;
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return L_21;
        }
    },
    P_457(457, "第五个球数字") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            return bonusSetting.getA2();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc();
        }

        @Override
        public int hit(String code, String actionData) {
            return Objects.equals(code.substring(8, 9), actionData) ? 1 : 0;
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return L_21;
        }
    },
    P_458(458, "龙虎和") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            switch (actionData) {
                case "总和大":
                case "总和小":
                case "总和单":
                case "总和双":
                    return bonusSetting.getA1();
                case "龙":
                case "虎":
                    return bonusSetting.getA7();
            }
            throw new IllegalStateException();
        }

        @Override
        public String getPlayedName(String actionData) {
            return actionData;
        }

        @Override
        public int hit(String code, String actionData) {
            int sum = 0;
            String[] sArr = code.split(",");
            for (String s : sArr) {
                sum += Integer.parseInt(s);
            }
            boolean flag = false;
            switch (actionData) {
                case "总和大":
                    flag = sum >= 23;
                    break;
                case "总和小":
                    flag = sum < 23;
                    break;
                case "总和单":
                    flag = sum % 2 == 1;
                    break;
                case "总和双":
                    flag = sum % 2 == 0;
                    break;
                case "龙":
                    flag = code.charAt(0) > code.charAt(8);
                    break;
                case "虎":
                    flag = code.charAt(0) < code.charAt(8);
                    break;
            }
            return flag ? 1 : 0;
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    P_459(459, "总和龙虎和") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            if ("和".equals(actionData)) {
                return bonusSetting.getA3();
            }
            throw new IllegalStateException();
        }

        @Override
        public String getPlayedName(String actionData) {
            return actionData;
        }

        @Override
        public int hit(String code, String actionData) {
            return code.charAt(0) == code.charAt(8) ? 1 : 0;
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    // 豹子
    P_460(460, "前三") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            if ("豹子".equals(actionData)) {
                return bonusSetting.getA4();
            }
            throw new IllegalStateException();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc() + actionData;
        }

        @Override
        public int hit(String code, String actionData) {
            char c1 = code.charAt(0);
            char c2 = code.charAt(2);
            char c3 = code.charAt(4);
            return (c1 == c2 && c2 == c3) ? 1 : 0;
        }

        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    P_461(461, "中三") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            if ("豹子".equals(actionData)) {
                return bonusSetting.getA4();
            }
            throw new IllegalStateException();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc() + actionData;
        }

        @Override
        public int hit(String code, String actionData) {
            char c1 = code.charAt(2);
            char c2 = code.charAt(4);
            char c3 = code.charAt(6);
            return (c1 == c2 && c2 == c3) ? 1 : 0;
        }
        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    P_462(462, "后三") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            if ("豹子".equals(actionData)) {
                return bonusSetting.getA4();
            }
            throw new IllegalStateException();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc() + actionData;
        }

        @Override
        public int hit(String code, String actionData) {
            char c1 = code.charAt(4);
            char c2 = code.charAt(6);
            char c3 = code.charAt(8);
            return (c1 == c2 && c2 == c3) ? 1 : 0;
        }
        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    // 顺子
    P_463(463, "前三") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            if ("顺子".equals(actionData)) {
                return bonusSetting.getA5();
            }
            throw new IllegalStateException();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc() + actionData;
        }

        @Override
        public int hit(String code, String actionData) {
            char c1 = code.charAt(0);
            char c2 = code.charAt(2);
            char c3 = code.charAt(4);
            return actionData.equals(PlayedType.action2(c1, c2, c3)) ? 1 : 0;
        }
        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    P_464(464, "中三") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }
        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            if ("顺子".equals(actionData)) {
                return bonusSetting.getA5();
            }
            throw new IllegalStateException();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc() + actionData;
        }

        @Override
        public int hit(String code, String actionData) {
            char c1 = code.charAt(2);
            char c2 = code.charAt(4);
            char c3 = code.charAt(6);
            return actionData.equals(PlayedType.action2(c1, c2, c3)) ? 1 : 0;
        }
        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    P_465(465, "后三") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            if ("顺子".equals(actionData)) {
                return bonusSetting.getA5();
            }
            throw new IllegalStateException();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc() + actionData;
        }

        @Override
        public int hit(String code, String actionData) {
            char c1 = code.charAt(4);
            char c2 = code.charAt(6);
            char c3 = code.charAt(8);
            return actionData.equals(PlayedType.action2(c1, c2, c3)) ? 1 : 0;
        }
        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    // 对子,杂六
    P_466(466, "前三") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            if ("对子".equals(actionData)) {
                return bonusSetting.getA9();
            } else if ("半顺".equals(actionData)) {
                return bonusSetting.getA6();
            } else if ("杂六".equals(actionData)) {
                return bonusSetting.getA6();
            }
            throw new IllegalStateException();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc() + actionData;
        }

        @Override
        public int hit(String code, String actionData) {
            char c1 = code.charAt(0);
            char c2 = code.charAt(2);
            char c3 = code.charAt(4);
            return actionData.equals(PlayedType.action2(c1, c2, c3)) ? 1 : 0;
        }
        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    P_467(467, "中三") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            if ("对子".equals(actionData)) {
                return bonusSetting.getA9();
            } else if ("半顺".equals(actionData)) {
                return bonusSetting.getA6();
            } else if ("杂六".equals(actionData)) {
                return bonusSetting.getA6();
            }
            throw new IllegalStateException();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc() + actionData;
        }

        @Override
        public int hit(String code, String actionData) {
            char c1 = code.charAt(2);
            char c2 = code.charAt(4);
            char c3 = code.charAt(6);
            return actionData.equals(PlayedType.action2(c1, c2, c3)) ? 1 : 0;
        }
        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    P_468(468, "后三") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            if ("对子".equals(actionData)) {
                return bonusSetting.getA9();
            } else if ("半顺".equals(actionData)) {
                return bonusSetting.getA6();
            } else if ("杂六".equals(actionData)) {
                return bonusSetting.getA6();
            }
            throw new IllegalStateException();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc() + actionData;
        }

        @Override
        public int hit(String code, String actionData) {
            char c1 = code.charAt(4);
            char c2 = code.charAt(6);
            char c3 = code.charAt(8);
            return actionData.equals(PlayedType.action2(c1, c2, c3)) ? 1 : 0;
        }
        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    // 半顺
    P_469(469, "前三") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            if ("半顺".equals(actionData)) {
                return bonusSetting.getA6();
            }
            throw new IllegalStateException();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc() + actionData;
        }

        @Override
        public int hit(String code, String actionData) {
            char c1 = code.charAt(0);
            char c2 = code.charAt(2);
            char c3 = code.charAt(4);
            return actionData.equals(PlayedType.action2(c1, c2, c3)) ? 1 : 0;
        }
        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    P_470(470, "中三") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            if ("半顺".equals(actionData)) {
                return bonusSetting.getA6();
            }
            throw new IllegalStateException();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc() + actionData;
        }

        @Override
        public int hit(String code, String actionData) {
            char c1 = code.charAt(2);
            char c2 = code.charAt(4);
            char c3 = code.charAt(6);
            return actionData.equals(PlayedType.action2(c1, c2, c3)) ? 1 : 0;
        }
        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    },
    P_471(471, "后三") {
        @Override
        public int getActionNum(String actionData) {
            return 1;
        }

        @Override
        public BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting) {
            if ("半顺".equals(actionData)) {
                return bonusSetting.getA6();
            }
            throw new IllegalStateException();
        }

        @Override
        public String getPlayedName(String actionData) {
            return getDesc() + actionData;
        }

        @Override
        public int hit(String code, String actionData) {
            char c1 = code.charAt(4);
            char c2 = code.charAt(6);
            char c3 = code.charAt(8);
            return actionData.equals(PlayedType.action2(c1, c2, c3)) ? 1 : 0;
        }
        @Override
        public BonusLimitType getBonusLimitType(String actionData) {
            return BonusLimitType.parse(actionData);
        }
    };

    private static boolean action1(int c, String actionData) {
        switch (actionData) {
            case "大":
                return c >= 5;
            case "小":
                return c < 5;
            case "单":
                return c % 2 == 1;
            case "双":
                return c % 2 == 0;
        }
        return false;
    }

    private static String action2(char c1, char c2, char c3) {

        char[] array = new char[]{c1, c2, c3};
        Arrays.sort(array);
        if (array[0] == '0' && array[1] == '1' && array[2] == '9') {
            return "顺子";
        }
        if (array[0] == '0' && array[1] == '8' && array[2] == '9') {
            return "顺子";
        }

        int one = array[2] - array[1] == 0 ? 1 : 0;
        one = array[1] - array[0] == 0 ? ++one : one;
        if (one == 1) {
            return "对子";
        } else if (one == 2) {
            return "豹子";
        }
        one = array[2] - array[1] == 1 ? 1 : 0;
        one = array[1] - array[0] == 1 ? ++one : one;
        if (one == 1) {
            return "半顺";
        } else if (one == 2) {
            return "顺子";
        } else {
            return "杂六";
        }
    }

    /**
     * 不定位计算中奖
     */
    public static boolean action3(String code, String actionData, int condition) {
        Set<Character> sArr = getChars(actionData);
        int i = 0;
        for (Character c : sArr) {
            if (code.contains(c.toString())) {
                if (++i >= condition) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 组合,递归方法，当前已抽取的小球个数与要求抽取小球个数相同时，退出递归
     */
    public static void action4(int curnum, int curmaxv, int maxnum, int maxv, Stack<Integer> stack, Consumer<Stack<Integer>> consumer) {

        if (curnum == maxnum) {
            consumer.accept(stack);
            return;
        }

        for (int i = curmaxv + 1; i <= maxv; i++) { // i <= maxv - maxnum + curnum + 1
            stack.push(i);
            action4(curnum + 1, i, maxnum, maxv, stack, consumer);
            stack.pop();
        }
    }

    /**
     * 定位计算中奖
     */
    public static int action5(String code, String actionData, int condition) {
        List<Set<Character>> sArr = PlayedType.getCharsList(actionData);
        int hitNum = 0;
        for (int i = 0; i < 5; i++) {
            if (sArr.get(i).contains(code.charAt(i * 2))) {
                hitNum++;
            }
        }
        if (hitNum < condition) {
            return 0;
        }
        AtomicInteger actionNum = new AtomicInteger();
        PlayedType.action4(0, 0, condition, hitNum, new Stack<>(), stack -> actionNum.incrementAndGet());
        return actionNum.get();
    }

    /**
     * 一帆风顺,好事成双,三星报喜计算中奖
     */
    public static int action6(String code, String actionData, int condition) {
        Set<Character> sArr = PlayedType.getChars(actionData);
        char[] codeCharArr = code.toCharArray();
        int hitNum = 0;
        for (Character c : sArr) {
            int j = 0;
            for (char codeChar : codeCharArr) {
                if (codeChar == c && ++j >= condition) {
                    hitNum++;
                    break;
                }
            }
        }
        return hitNum;
    }

    private static final Map<Integer, PlayedType> DATA;
    private int id;
    private String desc;

    static {
        DATA = new HashMap<>();
        for (PlayedType value : PlayedType.values()) {
            DATA.put(value.getId(), value);
        }
    }

    PlayedType(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public static PlayedType find(Integer id) {
        PlayedType playedType = DATA.get(id);
        if (playedType == null) {
            throw new NullPointerException(id + "PlayedType not found");
        }
        return playedType;
    }

    public static class No {
        char c1;
        char c2;
        char c3;
        String value;

        public No(char c1, char c2, char c3) {
            this.c1 = c1;
            this.c2 = c2;
            this.c3 = c3;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            No no = (No) o;
            char[] array = new char[]{c1, c2, c3};
            char[] array2 = new char[]{no.c1, no.c2, no.c3};
            Arrays.sort(array);
            Arrays.sort(array2);
            return array[0] == array2[0] &&
                    array[1] == array2[1] &&
                    array[2] == array2[2];
        }

        @Override
        public int hashCode() {
            char[] array = new char[]{c1, c2, c3};
            Arrays.sort(array);
            return Objects.hash(array[0], array[1], array[2]);
        }

        @Override
        public String toString() {
            return String.format("%s,%s,%s,%s", c1, c2, c3, value);
        }
    }

    public static void main(String[] args) {
        List<No> noList = new ArrayList<>();
        for (int i = '0'; i <= '9'; i++) {
            for (int j = '0'; j <= '9'; j++) {
                for (int k = '0'; k <= '9'; k++) {
                    noList.add(new No((char) i, (char) j, (char) k));
                }
            }
        }
        noList.stream().peek(no -> no.value = action2(no.c1, no.c2, no.c3)).forEach(System.out::println);
    }

    private static List<Set<Character>> getCharsList(String actionData) {
        String[] lines = actionData.split(",");
        if (lines.length != 5) {
            throw new IllegalStateException();
        }
        return Arrays.asList(PlayedType.getChars(lines[0]), PlayedType.getChars(lines[1]), PlayedType.getChars(lines[2]), PlayedType.getChars(lines[3]), PlayedType.getChars(lines[4]));
    }

    private static Set<Character> getChars(String actionData) {
        char[] sArr = actionData.replaceAll("\\D", "").toCharArray();
        Set<Character> set = new HashSet<>();
        for (char c : sArr) {
            if (set.contains(c)) {
                throw new IllegalStateException();
            }
            set.add(c);
        }
        return set;
    }

}
