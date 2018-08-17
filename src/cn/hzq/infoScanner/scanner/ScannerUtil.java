package cn.hzq.infoScanner.scanner;

import cn.hzq.infoScanner.base.ResultMap;

public abstract class ScannerUtil {

    public static final String EMPTY = "";
    public static final String POINT = ".";

    /**
     * 根据文件路径 检验其后缀名是否符合条件
     * @param path 文件绝对路径
     * @return ResultMap
     */
    public ResultMap checkPostfix(String path){
        if (path == null || EMPTY.equals(path.trim())) {
            return ResultMap.error("文件路径为空");
        }
        if (path.contains(POINT)) {
            String postfix = path.substring(path.trim().lastIndexOf(POINT) + 1,
                    path.length());
            if ( check(postfix)) {
                return ResultMap.putOk(postfix);
            }else{
                return ResultMap.error("不支持该文件类型");
            }
        }
        return ResultMap.error("文件路径错误");
    }

    /**
     * 文件后缀名校验 具体类实现
     * @param postfix 待检验后缀字符串
     * @return boolean
     */
    public abstract boolean check(String postfix);
}
