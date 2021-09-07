package com.springl.learn.thymleaf;

import lombok.Data;

@Data
public class FormBean {
    private String welcome="hello 你好";
    private String name;

    private int age;

    private AddressBean mainAddr;

    private AddressBean[] addrs;
}
