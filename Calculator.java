package com.leetcode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public final class Calculator {

    /**
     * 初始值，默认值是0
     */
    private BigDecimal initNum = new BigDecimal(0);
    /**
     * 当前累计计算值，默认值是0
     */
    private BigDecimal curTotal = initNum;
    /**
     * 历史数值结果集合
     */
    private List<BigDecimal> lastTotalList = new ArrayList<>();

    {
        lastTotalList.add(initNum);
    }

    /**
     * 被操作数值
     */
    private BigDecimal optNum;
    /**
     * 操作值集合
     */
    private List<BigDecimal> lastOptNumList = new ArrayList<>();
    /**
     * 操作符号集合
     */
    private List<String> lastOptList = new ArrayList<>();

    /**
     * 当前操作符
     */
    private String curOperator;
    /**
     * undo/redo 的操作指针
     */
    private int lastOptIndex = -1;
    /**
     * undo/redo 的最大有效指针
     */
    private int validIndexMax = -1;
    /**
     * 默认精度2位小数
     */
    private int scale = 2;
    /**
     * 默认向上取整
     */
    private final RoundingMode HALF_UP = RoundingMode.HALF_UP;


    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        calculator.setInitNum(new BigDecimal(1));
        calculator.calc("+", new BigDecimal(2));
        calculator.show();
        calculator.calc("*", new BigDecimal(3));
        calculator.show();

        calculator.undo();
        calculator.undo();
        calculator.undo();

        calculator.redo();
        calculator.redo();
        calculator.redo();
    }

    /**
     * 执行计算操作
     *
     * @param curOperator 操作符号
     * @param optNum      操作数值
     */
    public void calc(String curOperator, BigDecimal optNum) {
        if (curOperator == null || optNum == null) {
            System.out.println("请输入操作符号和操作数值!");
            return;
        }
        // 累加计算
        BigDecimal ret = calcTwoNum(curTotal, curOperator, optNum);
        if (this.lastOptIndex == -1) {
            lastOptNumList.add(optNum);
            lastOptList.add(curOperator);
            lastTotalList.add(ret);
        } else {
            // 处于redo/undo中间过程,覆盖undo/redo操作记录,并记录有效索引最大值
            this.lastOptIndex++;
            this.validIndexMax = this.lastOptIndex;
            this.lastTotalList.set(this.lastOptIndex, ret);
            this.lastOptNumList.set(this.lastOptIndex - 1, optNum);
            this.lastOptList.set(this.lastOptIndex - 1, curOperator);
        }
        curTotal = ret;
    }

    /**
     * 回撤到上一步
     */
    public void undo() {
        if (lastTotalList.size() <= 1) {
            System.out.println("undo后值:" + initNum + "," + "undo前值:" + curTotal);
            return;
        }
        // 原始指针
        if (lastOptIndex == -1) {
            lastOptIndex = lastOptList.size() - 1;
        }
        // 指针移动
        else {
            if (lastOptIndex - 1 < 0) {
                System.out.println("无法再undo!");
                return;
            }
            lastOptIndex--;
        }
        System.out.println("undo后值:" + lastTotalList.get(lastOptIndex) + ",undo前值:" + curTotal + ",undo的操作:" + lastOptList.get(lastOptIndex) + ",undo操作的值:" + lastOptNumList.get(lastOptIndex));
        curTotal = lastTotalList.get(lastOptIndex);
    }

    /**
     * 根据回撤进行重做
     */
    public void redo() {
        if (lastOptIndex == -1 || lastOptIndex + 1 == lastTotalList.size() || lastOptIndex + 1 == this.validIndexMax + 1) {
            System.out.println("无法再redo!");
            return;
        }
        lastOptIndex++;
        curTotal = lastTotalList.get(lastOptIndex);
        System.out.println("redo后值:" + curTotal + ",redo前值:" + curTotal + ",redo的操作:" + lastOptList.get(lastOptIndex - 1) + ",redo操作的值:" + lastOptNumList.get(lastOptIndex - 1));
    }


    /**
     * 进行累计计算
     *
     * @param curTotal    前面已累计值
     * @param curOperator 当前操作
     * @param newNum      新输入值
     * @return 计算结果
     */
    private BigDecimal calcTwoNum(BigDecimal curTotal, String curOperator, BigDecimal newNum) {
        BigDecimal ret = BigDecimal.ZERO;
        if ("+".equalsIgnoreCase(curOperator)) {
            ret = curTotal.add(newNum);
        }
        if ("-".equalsIgnoreCase(curOperator)) {
            ret = curTotal.subtract(newNum).setScale(scale, HALF_UP);
        }
        if ("*".equalsIgnoreCase(curOperator)) {
            ret = curTotal.multiply(newNum).setScale(scale, HALF_UP);
        }
        if ("/".equalsIgnoreCase(curOperator)) {
            ret = curTotal.divide(newNum, HALF_UP);
        }
        System.out.println("正在执行操作：" + curTotal + curOperator + newNum);
        return ret;
    }

    /**
     * 显示操作结果
     */
    public void show() {
        System.out.println("结果：" + curTotal.setScale(scale, HALF_UP));
    }

    public BigDecimal getCurTotal() {
        return curTotal;
    }

    public void setCurTotal(BigDecimal curTotal) {
        this.curTotal = curTotal;
    }

    public BigDecimal getOptNum() {
        return optNum;
    }

    public BigDecimal getInitNum() {
        return initNum;
    }

    public void setInitNum(BigDecimal initNum) {
        this.initNum = initNum;
        this.curTotal = initNum;
        lastTotalList = new ArrayList<>();
        lastTotalList.add(initNum);
    }

    public void setOptNum(BigDecimal optNum) {
        this.optNum = optNum;
    }

    public List<BigDecimal> getLastOptNumList() {
        return lastOptNumList;
    }

    public void setLastOptNumList(List<BigDecimal> lastOptNumList) {
        this.lastOptNumList = lastOptNumList;
    }

    public List<String> getLastOptList() {
        return lastOptList;
    }

    public void setLastOptList(List<String> lastOptList) {
        this.lastOptList = lastOptList;
    }

    public String getCurOperator() {
        return curOperator;
    }

    public void setCurOperator(String curOperator) {
        this.curOperator = curOperator;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public List<BigDecimal> getLastTotalList() {
        return lastTotalList;
    }

    public void setLastTotalList(List<BigDecimal> lastTotalList) {
        this.lastTotalList = lastTotalList;
    }

    public int getLastOptIndex() {
        return lastOptIndex;
    }

    public void setLastOptIndex(int lastOptIndex) {
        this.lastOptIndex = lastOptIndex;
    }

    public int getValidIndexMax() {
        return validIndexMax;
    }

    public void setValidIndexMax(int validIndexMax) {
        this.validIndexMax = validIndexMax;
    }

}
