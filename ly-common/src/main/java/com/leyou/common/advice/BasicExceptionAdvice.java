package com.leyou.common.advice;

import com.leyou.common.exception.ExceptionResult;
import com.leyou.common.exception.LyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice   /*controller中的通知器，只要有异常，这个业务自动生效，相当于添加了try*/
@Slf4j
public class BasicExceptionAdvice {

    @ExceptionHandler(RuntimeException.class)/*异常处理器，相当于catch*/
    public ResponseEntity<String> handleException(RuntimeException e) {
        // 我们暂定返回状态码为400， 然后从异常中获取友好提示信息
        return ResponseEntity.status(400).body(e.getMessage());
    }
    @ExceptionHandler(LyException.class)/*异常处理器，相当于catch*/
    public ResponseEntity<ExceptionResult> handleException(LyException e) {

        return ResponseEntity.status(e.getStatus()).body(new ExceptionResult(e));
    }


}
