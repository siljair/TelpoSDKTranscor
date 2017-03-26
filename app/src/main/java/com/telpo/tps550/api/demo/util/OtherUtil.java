package com.telpo.tps550.api.demo.util;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;

/**
 * 单位:广东天波信息技术股份有限公司
 * 创建人:luyq
 * 功能：其他作用的组件
 * 日期:2014-7-16
 */
public class OtherUtil {

    /**
     * 获取系统版本号
     *
     * @return
     */
    public static String getKernelCode() {
        String buildNumber = "";
        try {
             buildNumber = runSysProperty("getprop ro.rksdk.version");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return  buildNumber;
    }

    /**
     * 运行系统调用，获取或设置系统属性
     *
     * @param command 命令字符串
     * @return 系统属性
     */
    public static String runSysProperty(String command) {//运行普通linux命令
        String prop = "";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line = null;
            if ((line = in.readLine()) != null) {
                prop = line;
            }

            process.waitFor();

        } catch (Exception e) {
//            e.printStackTrace();
            prop = "";
        }
        return prop;
    }


    /**
     * 获得当天日期（客户端系统日期）
     *
     * @return 字符串 格式为 yyyymmdd
     */
    public static String getCurrentDateStr() {
        String curDateStr = "";

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        curDateStr = String.valueOf(year) + "";
        curDateStr += ((month < 10) ? "0" + String.valueOf(month) : String.valueOf(month)) + "";
        curDateStr += ((day < 10) ? "0" + String.valueOf(day) : String.valueOf(day));

        return curDateStr;
    }


    /**
     * 获取当前时间，精度为秒
     *
     * @return 结果字符串
     */
    public static String getCurrentTimeStrWithSecond() {
        String curTimeSr = "";

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        curTimeSr = ((hour < 10) ? "0" + String.valueOf(hour) : String.valueOf(hour)) ;
        curTimeSr += ((minute < 10) ? "0" + String.valueOf(minute) : String.valueOf(minute));
        curTimeSr += ((second < 10) ? "0" + String.valueOf(second) : String.valueOf(second));

        return curTimeSr;
    }

    public static String byteToHexString( byte[] b,int length) {
        String a = "";
        for (int i = 0; i < length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            a = a+hex;
        }

        return a;
    }
}
