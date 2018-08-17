package cn.hzq.infoScanner.base;

import java.util.HashMap;

/**
 * 自定义结果Map
 * @author hxhaaj
 * 2018.03
 */
public class ResultMap extends HashMap<String,Object> {
    private static final String KEY_CODE = "code";
    private static final String KEY_MESSAGE = "msg";
    private static final String OK_MESSAGE = "操作成功";
    private static final String ERR_MESSAGE = "操作失败";
    private static final String OK_FLAG = "1";
    private static final String ERR_FLAG = "0";
    private static final int OK_CODE = 200;
    private static final int ERR_CODE = 500;

    private ResultMap(){}

    /**
     * 默认成功
     * 提示信息默认为 操作成功
     * @return  ResultMap
     */
    public static ResultMap ok(){
        return ok(OK_MESSAGE);
    }

    /**
     * 可以设置消息的成功
     * @param message 成功提示信息
     * @return ResultMap
     */
    public static ResultMap ok(String message) {
        return ok(OK_CODE,message);
    }

    /**
     * 可以设置状态码和提示信息的成功
     * @param code 状态码
     * @param message 成功提示信息
     * @return ResultMap
     */
    public static ResultMap ok(int code, String message) {
        ResultMap map = new ResultMap();
        map.put(KEY_CODE,code);
        map.put(KEY_MESSAGE,message);
        return map;
    }

    /**
     * 默认错误
     * 提示信息默认为 操作失败
     * @return  ResultMap
     */
    public static ResultMap error(){
        return error(ERR_MESSAGE);
    }

    /**
     * 可以设置消息的错误
     * @param message 错误提示信息
     * @return ResultMap
     */
    public static ResultMap error(String message){
        return error(ERR_CODE,message);
    }

    /**
     * 可以设置状态码和提示信息的错误
     * @param code 状态码
     * @param message 成功提示信息
     * @return ResultMap
     */
    public static ResultMap error(int code, String message) {
        return ok(code,message);
    }

    /**
     * 设置状态码 非静态
     * 链式
     * @param code 状态码
     * @return this对象
     */
    public ResultMap setCode(int code){
        super.put(KEY_CODE,code);
        return this;
    }

    /**
     * 设置信息
     * 链式
     * @param message 信息
     * @return this
     */
    public ResultMap setMessage(String message){
        super.put(KEY_MESSAGE,message);
        return this;
    }

    /**
     * 存入Object
     * 链式
     * @param key String类型 key
     * @param obj Objcet类型 obj
     * @return this
     */
    @Override
    public ResultMap put(String key,Object obj){
        super.put(key,obj);
        return this;
    }

    public static ResultMap putKV(String key,Object obj){
        ResultMap m = new ResultMap();
        return m.put(key,obj);
    }

    public static ResultMap putOk(Object obj){
        return putKV(OK_FLAG,obj);
    }
    public static ResultMap putOk(){
        return putKV(OK_FLAG,"OK");
    }

    public static ResultMap putErr(Object obj){
        return putKV(ERR_FLAG,obj);
    }
    public static ResultMap putErr(){
        return putKV(ERR_FLAG,"ERR");
    }


    public Object getOk(){
        return get(OK_FLAG);
    }

    public Object getErr(){
        return get(ERR_FLAG);
    }

    public  boolean isOkObj(){
        return this.containsKey(OK_FLAG);
    }

    public  boolean isErrObj(){
        return this.containsKey(ERR_FLAG);
    }


    public  boolean isOkKey(){
       return  this.get(KEY_CODE) != null && (Integer) this.get(KEY_CODE) == OK_CODE;

    }

    public  boolean isErrKey(){
        return  this.get(KEY_CODE) != null && (Integer) this.get(KEY_CODE) == ERR_CODE;
    }

    public  boolean isOkVal(){
        return this.containsValue(OK_CODE);
    }

    public  boolean isErrVal(){
        return this.containsValue(ERR_CODE);
    }


    public String getMessage(){
        return (String) this.get(KEY_MESSAGE);
    }




}
