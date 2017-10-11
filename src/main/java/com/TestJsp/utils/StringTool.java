package com.TestJsp.utils;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by RXLiuli on 2017/9/2. 有关 String 类型的工具类
 */
public class StringTool {
    private StringTool() {
    }

    private static Logger logger = Logger.getLogger(StringTool.class.getName());

    /**
     * 截取字符串
     *
     * @param str    要进行截取的字符串
     * @param maxLen 要进行截取的长度
     * @return 截取之后的字符串
     */
    public static String strLenFormat(String str, int maxLen) {
        return (str.length() > maxLen) ? str.substring(0, maxLen - 3) + "..." : str;
    }

    /**
     * 将一个字符串转化为 Integer 对象
     *
     * @param str 要进行转化的字符串
     * @return 转化后的 Integer 数值, 或者 null
     */
    @Contract("null -> null")
    public static Integer strToInteger(String str) {
        if (str == null)
            return null;
        // 定义一个判断字符串是否是数字的正则表达式模式
        Pattern r = Pattern.compile("^ *(-?\\d+) *$");
        // 定义正则表达式匹配类(用于执行各种匹配方法)
        Matcher m = r.matcher(str);
        return m.matches() ? Integer.valueOf(m.group(1)) : null;
    }

    /**
     * 获取指定日期与现有时间的间隔(或者说差距) 未实现!!!
     *
     * @param date 指定的日期实例
     * @return 相差日期()
     */
    public static String strUpdated(Date date) {
        return strUpdated(date, "更新");
    }

    /**
     * 获取指定日期与现有时间的间隔(或者说差距) 未实现!!!
     *
     * @param date 指定的日期实例
     * @param date 结束
     * @return 相差日期()
     */
    @Nullable
    public static String strUpdated(Date date, String strEnd) {
        Date dateNow = new Date();
        long betweenDate = Math.abs(dateNow.getTime() - date.getTime());
        // 计算年份
        int years = (int) (betweenDate / 1000 / 60 / 60 / 24 / 365);
        if (years > 0)
            return years + " 年前" + strEnd;
        // 计算月份
        int months = (int) (betweenDate / 1000 / 60 / 60 / 24 / 30);
        if (months > 0)
            return months + " 月前" + strEnd;
        // 计算日
        int days = (int) (betweenDate / 1000 / 60 / 60 / 24);
        if (days > 0)
            return days + " 日前" + strEnd;
        // 计算小时
        int hours = (int) (betweenDate / 1000 / 60 / 60);
        if (hours > 0)
            return hours + " 小时前" + strEnd;
        // 计算分钟
        int minutes = (int) (betweenDate / 1000 / 60);
        if (minutes > 0)
            return minutes + " 分钟前" + strEnd;
        // 计算秒数
        int seconds = (int) (betweenDate / 1000);
        if (seconds > 0)
            return seconds + " 秒前" + strEnd;
        return null;
    }

    /**
     * 字符串日期常见的三种格式
     */
    public static final String YMD = "yyyy-MM-dd";
    public static final String YMDHM = "yyyy-MM-dd hh:mm";
    public static final String YMDHMS = "yyyy-MM-dd hh:mm:sss";

    /**
     * 将字符串转化成日期的方法
     *
     * @param pattern 日期的格式
     * @param strDate 日期的字符串
     * @return 转化成功就返回 Date 的实例,否则返回 null
     */
    @Nullable
    public static Date strToDate(String pattern, String strDate) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(strDate);
        } catch (ParseException e) {
            logger.info(e);
        }
        return null;
    }

    /**
     * 利用java原生的摘要实现SHA256加密
     *
     * @param str 要进行加密的字符串
     * @return 加密后的报文
     */
    public static String encryptSHA256(String str) {
        String encodeStr = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.info(e);
        }
        return encodeStr;
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes 要进行转换的 byte 数组转化为 String
     * @return 转换得到的 String 字符串
     */
    @NotNull
    private static String byte2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString();
    }
}
