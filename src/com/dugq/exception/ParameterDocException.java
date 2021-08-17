package com.dugq.exception;

/**
 * @author dugq
 * @date 2021/7/6 9:59 下午
 */
public class ParameterDocException extends ErrorException{
    private static final long serialVersionUID = 6099454767564445141L;

    public ParameterDocException(String desc) {
        super(desc);
    }

    @Override
    public String getFullMessage() {
        return super.getFullMessage()+"\n\n\n 正确注释参考："+rightMethodDesc;
    }

    private String rightMethodDesc = "\n /**\n" +
            "     * 第一行必须是接口的简单描述 比如：查询直播详情（最好不要附带任何标点符号和间隔符）\n" +
            "     * 新起一行，书写接口的实现逻辑（简单接口可跳过，复杂接口必须书写接口的逻辑，或者置顶到类的doc中。这一行开始到@param之前，插件都不会用到）\n" +
            "     * @param 参数名 参数的简单描述（在这里点回车键，会自动换行到参数名的后面）\n" +
            "     *            e.g: 示例值\n" +
            "     *            {@link X.X.X} 链接对象（如果是enum类型，会把enum的所有对象添加到参数的可选值列表，只会列举枚举的第一个属性为可选值，最后一个属性为可选值的描述。如果属性是可枚举的，必须链接枚举类）\n" +
            "     *            字段的详细描述（必要时书写）\n" +
            "     * @return 返回值描述（一般情况下必须返回Result封装对象，data如果是基本类型【8中基本类型和他们的封装类、String、date】，这里必须写清楚该字段的意义，如果是封装类型也应该书写，只是插件不会用到）\n" +
            "     *         新起一行作为返回值的详细描述（必要时书写）\n" +
            "     *         针对异步servlet必须在@return的详细注释中添加返回值的具体类型 {@link cn.com.duiba.live.clue.web.bean.result<cn.com.duiba.live.clue.web.vo.red.LiveUserRedListVo>}\n" +
            "     *         在使用插件时把返回值复制替换void，不用关心具体的实现报错，插件运行不对项目进行编译。但是包含import在内的声明（类名、方法、参数、返回值）部分必须正确，否则将无法链接到class\n" +
            "     *\n" +"   补充说明：\n" +
            "          1、请求只能选择@GetMapping 或者 @PostMapping 禁止@RequestMapping，哪有什么请求即需要get又需要post的。\n" +
            "          一般情况，Post请求的所有参数都必须放在body中，禁止post请求还从request param中读取参数。脱裤子放屁。共有参数如：tku等，都是用header实现的。像腾讯的accessToken，那是老一代编码风格的产物\n" +
            "          2、基本类型参数非空校验请使用\n" +
            "                          javax.validation.constraints.NotNull\n" +
            "                          javax.validation.constraints.NotBlank\n" +
            "                          javax.validation.constraints.NotEmpty\n" +
            "          等javax包下的注解，如果需要非空，必须添加注解。同时在类上加注解 org.springframework.validation.annotation.@Validated。spring自动对ctrl进行AOP，在AOP中实现参数校验\n" +
            "          3、如果参数列表或者返回体中使用了范型，务必加上范型的具体类型声明！不要搞什么运行时多态来恶心人。对外接口的声明就是要简单明了\n" +
            "          4、废弃参数，废弃返回值，请使用@Deprecated注解，插件将会自动过滤掉"
           ;
}
