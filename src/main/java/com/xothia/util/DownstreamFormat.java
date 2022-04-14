package com.xothia.util;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.util
 * @ClassName : .java
 * @createTime : 2022/4/11 14:07
 * @Email : huaxia889900@126.com
 * @Description : 控制报文的对应bean。
 */

public class DownstreamFormat {

    @NotNull
    private WriteType type;

    @NotNull
    private Integer address;

    @NotNull
    private Integer quantity=1;

    //写registers时的值
    private int reg;
    private int[] regs;

    //写coils时的值
    private Boolean state;
    private Boolean[] states;


    public enum WriteType{
        //05
        WRITE_SINGLE_COIL,
        //06
        WRITE_SINGLE_REG,
        //15
        WRITE_MULTI_COIL,
        //16
        WRITE_MULTI_REG,
    }


    //工厂方法
    public static DownstreamFormat byteBuf2instance(ByteBuf buf){
        return JSON.parseObject(buf.toString(CharsetUtil.UTF_8), DownstreamFormat.class);
    }

    public WriteType getType() {
        return type;
    }

    public void setType(WriteType type) {
        this.type = type;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public int getReg() {
        return reg;
    }

    public void setReg(int reg) {
        this.reg = reg;
    }

    public int[] getRegs() {
        return regs;
    }

    public void setRegs(int[] regs) {
        this.regs = regs;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public Boolean[] getStates() {
        return states;
    }

    public void setStates(Boolean[] states) {
        this.states = states;
    }
}
