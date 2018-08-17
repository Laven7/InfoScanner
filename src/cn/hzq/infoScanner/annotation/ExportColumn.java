package cn.hzq.infoScanner.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解 用于指定实体类中指定字段
 * 其value值应为字段对应的中文名，为了与数据文件中的字段匹配
 * 该注解的处理器类为 ColumnProcesser
 * @author hxhaaj
 * 2018.03
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportColumn {
    String value();
}
