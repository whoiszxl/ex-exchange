package com.whoiszxl.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Result2<T> {

    /** 返回码 */
    private Integer code;
    /** 返回信息 */
    private String message;
    /** 返回数据 */
    private T data;

    public Result2(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> Result2<T> buildError() {
        Result2<T> result = new Result2<T>();
        return result.setCode(StatusCode.ERROR).setMessage("error");
    }

    public static <T> Result2<T> buildError(String message) {
        Result2<T> result = new Result2<T>();
        return result.setCode(StatusCode.ERROR).setMessage(message);
    }

    public static <T> Result2<T> buildError(int errorCode, String message) {
        Result2<T> result = new Result2<T>();
        return result.setCode(errorCode).setMessage(message);
    }

    public static <T> Result2<T> buildSuccess(T data) {
        return buildSuccess("success", data);
    }

    public static <T> Result2<T> buildSuccess(String message, T data) {
        Result2<T> result = new Result2<T>();
        result.setCode(StatusCode.OK).setMessage(message).setData(data);
        return result;
    }

    public static <T> Result2<T> buildSuccess() {
        Result2<T> result = new Result2<T>();
        return result.setCode(StatusCode.OK).setMessage("success");
    }
}