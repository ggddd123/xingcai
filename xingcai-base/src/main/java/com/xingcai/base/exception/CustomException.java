package com.xingcai.base.exception;

public class CustomException extends RuntimeException {
    private String errMessage;

    public CustomException() {
        super();
    }

    public CustomException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public static void cast(CommonError commonError){
        throw new CustomException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new CustomException(errMessage);
    }



}
