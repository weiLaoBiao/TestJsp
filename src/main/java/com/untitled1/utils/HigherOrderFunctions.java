package com.untitled1.utils;

import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

/**
 * Created by RXLiuli on 2017/9/12.
 * 高阶函数工具方法
 * 尝试使用 this 模拟连续调用(失败...)
 * 注: 方法参数应该尽可能的 "宽容", 而方法的返回值应该尽可能的 "小气".
 * (方法的参数是逆变的, 而返回值则是协变的.)
 * 该类暂时未废弃, 因为 java 原生的 Stream 流仅提供一部分的高阶函数,
 * Stream 也可以与 Collection, Map 互相转化.
 */
public class HigherOrderFunctions {
    private HigherOrderFunctions() {
    }

    /**
     * 集合的过滤默认工具方法
     *
     * @param coll 要进行过滤的集合
     * @param f    过滤的谓词 f
     * @param <T>  集合中元素的类型
     * @return 符合过滤条件的元素组成的新集合
     */
    public static <T> List<T> filter(Collection<? extends T> coll, Function<T, Boolean> f) {
        List<T> newList = new ArrayList<>();
        for (T t : coll) {
            if (f.apply(t)) newList.add(t);
        }
        return newList;
    }

    /**
     * Collection 集合的映射默认工具方法
     *
     * @param coll 映射的原集合
     * @param f    映射的函数 f
     * @param <T>  原集合的类型
     * @param <R>  新集合的类型
     * @return 原集合映射产生的新集合
     */
    public static <T, R> List<R> map(Collection<? extends T> coll, Function<T, R> f) {
        List<R> newList = new ArrayList<>();
        for (T t : coll) newList.add(f.apply(t));
        return newList;
    }

    /**
     * Map 集合的映射默认工具方法
     *
     * @param k1V1Map 原 Map<K1, V1> 集合
     * @param f       映射的方法
     * @param <K1>    原 Map 集合的键
     * @param <V1>    原 Map 集合的值
     * @param <K2>    新 Map 集合的键
     * @param <V2>    新 Map 集合的值
     * @return 映射产生的新 Map 集合
     */
    public static <K1, V1, K2, V2> Map<K2, V2> map(Map<? extends K1, ? extends V1> k1V1Map, Function<Pair<K1, V1>, Pair<K2, V2>> f) {
        Map<K2, V2> k2V2Map = new HashMap<>();
        k1V1Map.forEach((k, v) -> {
            Pair<K2, V2> pair = f.apply(new Pair<>(k, v));
            k2V2Map.put(pair.getKey(), pair.getValue());
        });
        return k2V2Map;
    }

    /**
     * 将 Collection 映射为 Map
     *
     * @param coll 要进行映射的 List 集合
     * @param f    映射的方法
     * @param <T>  原 List 集合元素的类型
     * @param <K>  映射产生的 Map 集合元素的 key 类型
     * @param <V>  映射产生的 Map 集合元素的 value 类型
     * @return 转化后的键值映射表
     */
    public static <T, K, V> Map<K, V> collToMap(Collection<? extends T> coll, Function<T, Pair<K, V>> f) {
        Map<K, V> kvMap = new HashMap<>();
        for (T t : coll) {
            Pair<K, V> pair = f.apply(t);
            kvMap.put(pair.getKey(), pair.getValue());
        }
        return kvMap;
    }

    /**
     * 将 Map 映射为 Collection
     *
     * @param kvMap 要进行映射的 Map 集合
     * @param f     映射的方法
     * @param <K>   原 Map 集合元素的 key 类型
     * @param <V>   原 Map 集合元素的 value 类型
     * @param <T>   映射产生的新 List 集合元素的类型
     * @return 转化后的新 List 集合
     */
    public static <K, V, T> List<T> mapToColl(Map<? extends K, ? extends V> kvMap, Function<Pair<K, V>, T> f) {
        List<T> newList = new ArrayList<>();
        System.out.println();
        kvMap.forEach((k, v) -> newList.add(f.apply(new Pair<>(k, v))));
        return newList;
    }

    /**
     * 将 Collection 转化为 String 字符串
     *
     * @param coll 要进行转化的 Collection 集合
     * @param <T>  转化集合的类型
     * @return 转化后的字符串
     */
    @NotNull
    public static <T> String mkString(Collection<? extends T> coll) {
        return mkString(coll, "", "", "");
    }

    /**
     * 将 Collection 转化为 String 字符串
     *
     * @param coll 要进行转化的 Collection 集合
     * @param sep  分隔字符串
     * @param <T>  转化集合的类型
     * @return 转化后的字符串
     */
    @NotNull
    public static <T> String mkString(Collection<? extends T> coll, String sep) {
        return mkString(coll, "", sep, "");
    }

    /**
     * 将 Collection 转化为 String 字符串
     *
     * @param coll  要进行转化的 Collection 集合
     * @param start 首位字符串
     * @param sep   分隔字符串
     * @param end   结尾字符串
     * @param <T>   转化集合的类型
     * @return 转化后的字符串
     */
    @NotNull
    public static <T> String mkString(Collection<? extends T> coll, String start, String sep, String end) {
        StringBuilder sb = new StringBuilder(start);
        Integer i = 0;
        for (T t : coll)
            if (i++ != 0) sb.append(sep).append(t);
            else sb.append(t);
        sb.append(end);
        return sb.toString();
    }

    /**
     * 为 Collection 集合排序的方法(实际上仅仅只是调用了 Arrays 的工具方法)
     * 此处不应用于严格意义上的排序, 毕竟只是 toString 然后对 String[] 进行了排序
     *
     * @param coll 要进行排序的集合
     * @param <T>  排序集合元素的类型
     * @return 排序完成后的新集合
     */
    public static <T> List<T> sort(Collection<? extends T> coll) {
        Object[] arr = coll.toArray();
        Arrays.sort(arr);
        List<T> newColl = new ArrayList<>();
        for (Object o : arr) {
            newColl.add((T) o);
        }
        return newColl;
    }

    /**
     * 根据某个 "东西" 将集合分组数个 "东西" -> 集合 的键值映射表
     */
}
