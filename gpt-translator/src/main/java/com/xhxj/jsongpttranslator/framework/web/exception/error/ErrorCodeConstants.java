package com.xhxj.jsongpttranslator.framework.web.exception.error;


import com.xhxj.jsongpttranslator.framework.web.exception.ErrorCode;

/**
 * 错误码枚举类
 * <p>
 * ，使用 1-000-000-000 段
 */
public interface ErrorCodeConstants {
    ErrorCode OPENAIKEY_DOES_NOT_EXIST = new ErrorCode(1000000000, "请先配置openai的key");
    ErrorCode PROGRAM_STOPPED = new ErrorCode(1000000001, "程序已停止");


}
