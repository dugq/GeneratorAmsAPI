package com.dugq.util;

import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dugq on 2021/4/8.
 */
public class MyRandomUtils {
    private static final List<String> randomStringList = new ArrayList<>();
    static {
        randomStringList.add("asdfaf");
        randomStringList.add("阿沙发沙发沙发是短发");
        randomStringList.add("阿斯顿发大水发送发送到发送发大水发生短发的说法的说法是否");
        randomStringList.add("阿斯顿发送发送发送发送发送到发送发大水的发生短发是短发短发的说法是短发");
        randomStringList.add("阿斯顿发送发发送发拉德斯基发酒疯了；阿世界的看法；乐山大佛拉萨的肌肤；了");
    }

    public static Long randomLong() {
        return RandomUtils.nextLong(0,Long.MAX_VALUE);
    }

    public static Integer randomInt() {
        return RandomUtils.nextInt(0,Integer.MAX_VALUE);
    }

    public static boolean randomBoolean() {
        return RandomUtils.nextInt(0,10)%2==0;
    }

    public static String randomString() {
        return randomStringList.get(RandomUtils.nextInt(0,randomStringList.size()));
    }
}
