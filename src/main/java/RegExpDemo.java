import java.util.Arrays;

/**
 * @ description:
 * @ author: spencer
 * @ date: 2020/9/16 10:31
 */
public class RegExpDemo {

    public static void main(String[] args) {

//        String s = "15183463157";
//        boolean result = s.matches("1[345678]\\d{9}");

//        String s = "spencer@sina.com";
//        boolean result = s.matches("\\w{3,15}@\\S+\\.(com|cn|edu|org)");
//        System.out.println(result);

//        String s = "s758d8a7s68a76sddg";
//        System.out.println(s.replaceAll("\\d", "#"));

//        String s = "今今天天我我我我我要要要请你们洗洗脚";
//        System.out.println(s.replaceAll("(.)\\1+", "$1"));

//        String s = "15183463157";
//        System.out.println(s.replaceAll("(\\d{3})(\\d{4})(\\d{3})", "$1****$3"));

        String s = ".192.168.1...2";
        String[] split = s.split("\\.+");
        System.out.println(Arrays.toString(split));
        System.out.println(Arrays.asList(split));
    }
}

/*
()捕获组
    \1  取第一组

数量词：
    a{m}    正好m个a
    a{m,}   至少m个a
    a{m,n}  大于等于m，小于等于n

    a+      表示至少一个，等价于a{1,}
    a*      表示至少0个， 等价于a{0,}
    a?      表示最多一个，要么0，要么1，等价于a{0,1}

则的基本语法：
    [ab] a或者b
    [a-z] 所有的小写字母
    [a-zA-Z0-9_] 数字字母下划线
    [^a] 非字符a
    [^ab] 非字符a和非字符b
          注意：^只有在[]内部才表示非，如果不是在内部表示字符的开头
    \d  表示数字，等价于[0-9]  (digital)
    \D  表示非数字，等价于[^0-9]
    \w  表示单词字符 数字字母下划线 等价于[a-zA-Z0-9_] (word)
    \W  表示非单词字符 等价于[^a-zA-Z0-9_]
    \s  表示空白字符 (space)
    \S  表示非空白字符
    .   表示任意字符 去除：\n \r
    \.  只匹配真正的 .

    ^   表示字符串的开头
    $   表示字符串的结尾
* */