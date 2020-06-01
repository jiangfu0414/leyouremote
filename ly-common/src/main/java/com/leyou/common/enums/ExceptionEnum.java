package com.leyou.common.enums;

import lombok.Getter;

@Getter
public enum ExceptionEnum {
    PRICE_CANNOT_BE_NULL(400, "价格不能为空！"),
    CATEGORY_NOT_FOUND(204,"没有找到对应的商品"),
    BRAND_NOT_FOUND(204,"没有找到对应的品牌"),
    DATA_TRANSFER_ERROR(500,"通用消息转换失败"),
    BRAND_SAVE_ERROR(500,"品牌保存失败"),
    INVALID_REQUEST_PARAM(400,"前台提供的请求参数异常"),
    FILE_SAVE_ERROR(500,"上传保存失败"),
    DATA_MODIFY_ERROER(500,"服务端数据修改失败"),
    DATA_NOT_FOUND(500,"数据没有查到"),
    DATA_SAVE_ERROR(500,"数据保存失败");
    private int status;
    private String message;

    ExceptionEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }
}