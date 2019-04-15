package me.xwbz.flowable.common.exception;

import lombok.Getter;
import lombok.Setter;
import me.xwbz.flowable.common.api.ApiResult;

/**
 * 自定义异常：事物会回滚
 *
 * @author zgq
 * 2018年9月30日 15:45:48
 */
@Getter
@Setter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 6889615150063521500L;

    public Integer code;

    public BizException(){super();}

    public BizException(Integer code, String msg){
        super(msg);
        this.code = code;
    }

    public BizException(ApiResult errorInfo) {
       this(errorInfo.getCode(), errorInfo.getMsg());
    }
}
