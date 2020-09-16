package com.bigdata.gmalllogger.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @ description:
 * @ author: spencer
 * @ date: 2020/9/3 9:23
 */
public class TestApp {

    public static void main(String[] args) throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        System.out.println(address.getHostName());
    }
}
