package com.xothia.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.util
 * @ClassName : .java
 * @createTime : 2022/4/11 14:47
 * @Email : huaxia889900@126.com
 * @Description : 描述属性名称及其modbus slave内存坐标。
 */
@Valid
@Component("attribute")
@Scope("prototype")
public class Attribute {
    @NotBlank
    private String attrName;
    @NotNull
    private Integer address;
    @NotNull
    private Integer quantity;

    public Attribute() {
    }

    public Attribute(String attrName, Integer address, Integer quantity) {
        this.attrName = attrName;
        this.address = address;
        this.quantity = quantity;
    }

    public String getAttrName() {
        return attrName;
    }

    public Integer getAddress() {
        return address;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
