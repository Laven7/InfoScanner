package cn.hzq.infoScanner.base;


import java.util.List;

/**
 * 提供保存List集合数据到数据库的函数式接口
 * @param <T>
 */
@FunctionalInterface
public interface SaveListDao<T> {
    int saveList2DB(List<T> list);
}
