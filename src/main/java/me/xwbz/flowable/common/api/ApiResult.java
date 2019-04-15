package me.xwbz.flowable.common.api;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import me.xwbz.flowable.common.exception.BizException;
import org.slf4j.Logger;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ApiResult<T> implements Serializable {
    private static final long serialVersionUID = 5655283784182989462L;

    private Integer code;

    private String msg;

    private T data;

    private Map<String, Object> extraMap;

    public ApiResult(Code code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
    }

    public ApiResult(Code code, T data) {
        this(code);
        this.data = data;
    }

    public static ApiResultMap withMap(Code code) {
        ApiResult result = new ApiResult(code);
        ApiResultMap map = new ApiResultMap(result);
        result.data = map;
        return map;
    }

    public static ApiResult withData(Code code, Object data) {
        ApiResult result = new ApiResult(code);
        result.data = data;
        return result;
    }

    public ApiResult withMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public ApiResult withExtra(Map<String, Object> extraMap) {
        this.extraMap = extraMap;
        return this;
    }

    public static ApiResult handleException(Throwable e, Logger log) {
        if (e instanceof UndeclaredThrowableException) {
            e = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
        }
        if (e instanceof InvocationTargetException) {
            e = ((InvocationTargetException) e).getTargetException();
        }
        //自定义业务异常
        if (e instanceof BizException) {
            // 自定义的就不要打印那么多信息了
            StackTraceElement[] stacks = e.getStackTrace();
            if(stacks.length > 0) {
                StackTraceElement last = stacks[0];
                log.error("{} ({}:{})", e.getMessage(), last.getFileName(), last.getLineNumber());
            } else {
                log.error(e.getMessage());
            }
            return ApiResult.withError(Code.SYSTEM_EXCEPTION, e.getMessage());
        } else {
            log.error(e.getMessage(), e);
        }
        //未知异常 空指针什么的
        return new ApiResult(Code.SYSTEM_EXCEPTION);
    }

    public static ApiResult withError(Code code, String err) {
        return new ApiResult(code).withMsg(err);
    }

    public static class ApiResultMap extends HashMap<String, Object> {

        private static final long serialVersionUID = 1976696885051802767L;

        @JSONField(serialize = false, deserialize = false)
        private ApiResult result;

        private ApiResultMap(ApiResult result) {
            this.result = result;
        }

        public ApiResultMap add(String key, Object value) {
            super.put(key, value);
            return this;
        }

        public ApiResultMap addAll(Map<String, Object> otherMap) {
            super.putAll(otherMap);
            return this;
        }

        public ApiResult build() {
            return result;
        }
    }

    public enum Code {

        /** 服务器正常接收和处理请求 */
        SUCCESS(2000, "响应成功"),

        /** 参数错误 */
        ILLEGAL_PARAMS(4001, "参数错误"),

        /** 访问被拒绝 */
        ACCESS_DENIED(4003, "访问被拒绝"),

        /** 该数据不存在 */
        DATA_NOT_FOUND(4004, "该数据不存在"),

        /** 需要授权认证的api接口要求请求方提供授权认证的参数值，而请求方没有提供该参数 */
        TO_LOGIN(4101, "请先登录"),

        /** 登录信息已过期 */
        LOGIN_EXPIRED(4102, "登录信息已过期"),

        /** 服务器内部错误 */
        SYSTEM_EXCEPTION(5000, "系统异常");

        /**
         * 业务处理的状态代码
         */
        private final int code;

        /**
         * 业务处理的状态提示信息
         */
        private final String msg;

        Code(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

    }
}
