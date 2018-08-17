package cn.hzq.infoScanner.annotation;

import javax.annotation.processing.SupportedAnnotationTypes;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExportColumn自定义注解的处理器
 * @author hxhaaj
 * 2018.03
 */
@SupportedAnnotationTypes("ExportColumn")
public class ColumnProcesser {
    private ColumnProcesser(){}
    /**
     * @param entityClazz  实体类class
     * @return  英文和中文对应的字段名的Map集合
     */
    public static <T> Map<String,String> getExportColumnMap(Class<T> entityClazz,ColumnType keyType) {
        Map<String,String>  cm = new HashMap<>();
        for (Field f : entityClazz.getDeclaredFields() ){
            f.setAccessible(true);
            // 获取字段注解为ExportColumn的注解
            ExportColumn e = f.getAnnotation(ExportColumn.class);
            // 如果不为null 就说明该字段有ExportColumn注解
            if(e != null ){
                // 获取有该注解对象的字段名
                // 获取该注解对象中的value值 中文名
                if(keyType.equals(ColumnType.EN))
                    cm.put(f.getName(),e.value());
                else if(keyType.equals(ColumnType.CN))
                    cm.put(e.value(),f.getName());
            }
        }
        return  cm;
    }

    /**
     * @param entityClazz  实体类class
     * @param columnType  字段类型 中 CN  英 EN
     * @return  字段名List集合
     */
    public static <T> List<String> getColumnNameList(Class<T> entityClazz , ColumnType columnType){
        List<String> list = new ArrayList<>();
        for (Field f : entityClazz.getDeclaredFields() ){
            f.setAccessible(true);
            // 获取字段注解为ExportColumn的注解
            ExportColumn e = f.getAnnotation(ExportColumn.class);
            // 如果不为null 就说明该字段有ExportColumn注解
            if(e != null ){
                if(columnType.equals(ColumnType.EN)) {
                    // 获取有该注解对象的字段名 英文字段名
                    list.add(f.getName());
                }
                else if(columnType.equals(ColumnType.CN))
                    // 获取有该注解对象的注解中的value值 中文字段名
                    list.add(e.value());
            }
        }
        return list;
    }
}

