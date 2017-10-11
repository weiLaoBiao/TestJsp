package other;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by RXLiuli on 2017/10/9.
 * Date 日期格式化相关的例子
 */
public class SimpleDateFormatDemo {
    public static void main(String[] args) throws ParseException {
        //获取 Date 日期实例的方法 01:
        //不带有任何参数代表当前日期
        Date date01 = new Date();
        //此处使用的当前已经过的时间(并以此推算出当前日期)
        Date date02 = new Date(1495814400000L);
        //也可以使用被废弃的字符串参数(很危险!!!)
//        Date date03 = new Date("2017-05-27 00:00:00");
        System.out.println(date01 + "\n" + date02);

        //更好的方法是使用 java 提供的 SimpleDateFormat 实例去规定日期字符串的格式并以此获取 Date 实例
        //获取一个日期格式化类的实例
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); //规定日期的格式
        //使用 SimpleDateFormat 的实例获取一个 Date 日期实例
        Date date04 = format.parse("2017-05-27"); //此处可能会抛出异常 ParseException
        System.out.println(date04);

    }
}
