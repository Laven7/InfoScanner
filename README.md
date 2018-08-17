# InfoScanner
Excel和CSV类型数据文件自动录入小工具
## 依赖包
- java8
- dom4j-1.6.1.jar
- poi-3.7-20101029.jar （POI Excel文件处理）
- poi-ooxml-3.7-20101029.jar
- poi-ooxml-schemas-3.7-20101029.jar
- super-csv-2.4.0.jar （csv文件类型）
- xmlbeans-2.3.0.jar

## 实体类中

**在实体类中字段上方@ExportColumn注解指定需要通过数据文件录入字段**

- @ExportColumn注解的value值为该字段对应的数据文件中对应的中文名

- 下方代码中，@Data注解为lombok的注解，
也可不用 不用后，需加入无参构造 setter 和 getter 
最好重写toString、hashcode、equals方法，使用lombok的Data注解这些事情可省去，其自动完成

```java
@Data 
public class Student {
    @ExportColumn("学号")
    private String xh;

    @ExportColumn("年龄")
    private String nl;

    @ExportColumn("姓名")
    private String xm;
    private String lj;
    private String nj;

}
```

## 客户端使用
### Excel文件类型  
** xlsx 和 xls 均可 **

**主类 ExcelScanner **

直接调用`ExcelScanner`类中的静态方法`scanAndSave`

```java
/**
 * 读取Excel数据源的信息存入数据库中  excel文件中的工作表默认为0
 * @param filePath Excel文件路径
 * @param entityClazz 具体实体类class对象
 * @param dao 存入数据库的dao接口
 * @return  ResultMap
 */
public static <T>  ResultMap scanAndSave(String filePath, Class<T> entityClazz, SaveListDao<T> dao) 
  throws ReflectiveOperationException
```java

指定读取Excel工作表号的数据源的信息存入数据库中 
```java
 /**
 * 读取Excel数据源的信息存入数据库中
 * @param filePath Excel文件路径
 * @param entityClazz 具体实体类class对象
 * @param dao 存入数据库的dao接口
 * @param sheetAt excel文件中的工作表号
 * @return
 * @throws ReflectiveOperationException
 */
public static <T>  ResultMap scanAndSave(String filePath, Class<T> entityClazz, SaveListDao<T> dao, int sheetAt) 
  throws ReflectiveOperationException

```

```java
   @Test
    public void test3() throws ReflectiveOperationException {
        String filePath = "D://123.xlsx";
        // 主方法 指定文件路径 录入对应的实体类class对象 DAO接口
        ExcelScanner.scanAndSave(filePath,
                Student.class,
                //模拟DAO接口 存入数据库数据
                (list) -> {
                    int i = 0;
                    for (Student s : list) {
                        System.out.println(s.toString());
                        i++;
                    }
                    return i;
        }).forEach((k, v)-> System.out.println(k+"---"+v));
    }
```
### csv文件类型
**主类 CsvScanner**
直接调用`CsvScanner`类中的静态方法`scanAndSave`
```java
  /**
	 *
	 * @param filePath 文件绝对路径
	 * @param entityClazz 实体类class对象
	 * @param dao 导入数据接口
	 * @param cellProcessor csv文件单元处理器
	 * @param <T>
	 * @return ResultMap 结果Map extends HashMa
	 */
public static <T>  ResultMap scanAndSave(String filePath, Class<T> entityClazz, SaveListDao<T> dao, CellProcessor[] cellProcessor)
```

```java
  @Test
    public void test2() throws ReflectiveOperationException {
        String filePath = "F:/123.csv";
        CsvScanner.scanAndSave(filePath, //文件路径
                Xsxk.class, //实体类class对象
                (list) -> {  //DAO数据接口
                    int i = 0;
                    for (Xsxk s : list) {
                        System.out.println(s.toString());
                        i++;
                    }
                    return i;
        },new CellProcessor[]{ //csv文件对应的实体的单元处理器
                        null, null, null, null, null,
                        null, null, null, null, null,
                        null, null, null,null,null
                }).forEach((k, v)-> System.out.println(k+"---"+v));
    }
```

## 效果

### 错误提示
- 文件中缺少指定字段
  - 方法中返回结果map，map中信息为：(k---v)
  ```
  msg---未检测到字段名:[学号, 年龄, 姓名],请核对
  code---500
  ```
- 文件后缀名错误 文件类型不匹配

### 正确提示
```
msg---本次读取了4条数据,共导入了4条,相差0条未导入，请核对！
code---200
```
## 待完善...
