package cn.hzq.infoScanner.scanner;

import cn.hzq.infoScanner.annotation.ColumnProcesser;
import cn.hzq.infoScanner.annotation.ColumnType;
import cn.hzq.infoScanner.base.ResultMap;
import cn.hzq.infoScanner.base.SaveListDao;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Excel类型数据文件录入
 * @author hxhaaj
 * 2018.03
 */
public class ExcelScanner extends  ExcelScannerUtil{
    private ExcelScanner(){}

    public static final int RANGE_NUM = 60000;
    public static final int COLUMN_ROW = 0;
    public static final int INFO_SHEETAT = 0;


    /**
     * 读取Excel数据源的信息存入数据库中  excel文件中的工作表默认为0
     * @param filePath Excel文件路径
     * @param entityClazz 具体实体类class对象
     * @param dao 存入数据库的dao接口
     * @return
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> ResultMap scanAndSave(String filePath, Class<T> entityClazz, SaveListDao<T> dao) throws ReflectiveOperationException {
        return  scanAndSave(filePath, entityClazz, dao, INFO_SHEETAT);
    }

    /**
     * 读取Excel数据源的信息存入数据库中
     * @param filePath Excel文件路径
     * @param entityClazz 具体实体类class对象
     * @param dao 存入数据库的dao接口
     * @param sheetAt excel文件中的工作表号
     * @return
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T>  ResultMap scanAndSave(String filePath, Class<T> entityClazz, SaveListDao<T> dao, int sheetAt) throws ReflectiveOperationException {
        int saveNum = 0 ;
        ResultMap postfixResMap =
                new ExcelScannerUtil().checkPostfix(filePath);
        if( !postfixResMap.isOkObj()){
            // 文件类型不正确直接返回错误信息
            return postfixResMap;
        }else{
            Workbook excelWorkBook = getExcelWorkBook(filePath, (String) postfixResMap.getOk());
            if(excelWorkBook != null) {
                Sheet sheet = excelWorkBook.getSheetAt(sheetAt);
                // 如果有错误字段名，直接返回
                if ( !checkColumns(sheet, entityClazz).isOkObj()) {
                    return checkColumns(sheet, entityClazz);
                } else {
                    // 字段信息没有错误
                    int  rowCount = sheet.getLastRowNum();
                    int[] c = getNumsInRange(rowCount, RANGE_NUM);
                    // 分段 分批次 读取存入
                    for (int i = 0; i < c.length; i++) {
                        int startNum = 1 + RANGE_NUM * i;
                        int endNum = c[i];
                        List<T> list = readExcel(sheet, entityClazz, startNum, endNum);
                        System.err.println("s:" + startNum + ",e:" + endNum);
                        if (list == null || list.size() < 1) {
                            return ResultMap.error("数据为空，请检查");
                        } else {
                            // 存入数据库  具体实现看接口实现类
                            saveNum += dao.saveList2DB(list);
                        }
                    }
                    return ResultMap.ok("本次读取了"+rowCount+"条数据,共导入了"+saveNum+"条,相差"+(saveNum-rowCount)+"条未导入，请核对！");
                }
            }else{
                return ResultMap.error("Excel文件读取错误");
            }

        }
    }


    /**
     * 获取ExcelWorkBook对象
     * @param path
     * @param postfix
     * @return
     */
    public static Workbook getExcelWorkBook(String path,String postfix ){
        InputStream is = null;
        try {
            is  = new FileInputStream(path);
            if( OFFICE_EXCEL_2003_POSTFIX.equals(postfix) ){
                return  new HSSFWorkbook(is);
            }else if(OFFICE_EXCEL_2010_POSTFIX.equals(postfix)){
                return  new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Excel文件读取失败");
        }finally{
            try {
                if(is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("ExcelIO流关闭错误");
            }
        }
        return null;
    }


    /**
     * 检查Excel文件中的字段信息是否和实体中的字段一致，
     * 不符合直接返回未检测到的字段名列表
     * 符合，返回 sheet对象
     * @param path
     * @param entityClazz
     * @param postfix
     * @param <T>
     * @return
     */
   /* public static <T> ResultMap checkInfo(String path, Class<T> entityClazz, String postfix) {
        Workbook excelWorkBook = getExcelWorkBook(path, postfix);
        if(excelWorkBook != null) {
            Sheet sheet = excelWorkBook.getSheetAt(0);
            List<String> errColumnList = checkColumns(sheet, entityClazz);
            // 如果有错误字段名，直接返回
            if (errColumnList != null) {
                return ResultMap.error("未检测到字段名:" + errColumnList.toString() + ",请核对");
            } else {
                // 字段信息没有错误 直接返回根据workBook得到的sheet对象
                return ResultMap.ok().put("sheet",sheet);
            }
        }else{
            return ResultMap.error("Excel文件读取错误");
        }
    }
*/
    /**
     * 分段读取Excel中内容，存到List集合中
     * @param entityClazz 读取Excel的实体class
     * @param startNum 读取开始行号
     * @param endNum 读取结束行号
     * @return 实体List集合
     * @throws ReflectiveOperationException
     */
    public static <T>  List<T> readExcel(Sheet sheet, Class<T> entityClazz, int startNum ,int endNum ) throws ReflectiveOperationException {

        List<T> list = new ArrayList<>();
        List<String> columnNameList = ColumnProcesser.getColumnNameList(entityClazz, ColumnType.EN);
        for (int rowNum = startNum; rowNum <= endNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row != null) {
                // 实例化实体class
                T t = entityClazz.getConstructor().newInstance();
                for (String s : columnNameList) {
                    // 构造setter方法名 setXxx
                    String setterName = "set" + s.substring(0, 1).toUpperCase() + s.substring(1);
                    // 根据反射获取setter方法 参数类型为String
                    Method method = entityClazz.getMethod(setterName,String.class);
                    //获取key为英文字段名的map
                    Map<String, String> exportColumnMap = ColumnProcesser.getExportColumnMap(entityClazz,ColumnType.EN);
                    //执行setter方法 参数为对应的Excel相应字段的值
                    method.invoke(t, getValue(row.getCell(getValIndex(exportColumnMap.get(s), sheet,COLUMN_ROW))));
                }
                list.add(t);
            }
        }
        return list;

    }


    /**
     * 检查字段 返回按要求没有的字段名
     * @param sheet
     * @param entityClazz
     * @return
     */
    public static <T>  ResultMap checkColumns(Sheet sheet,  Class<T> entityClazz) {
        List<String> cl = new ArrayList<>();
        //获取中文字段列表
        List<String> clist = ColumnProcesser.getColumnNameList(entityClazz, ColumnType.CN);
        for (String s : clist){
            Integer columNum = getValIndex(s,sheet,COLUMN_ROW);
            if(columNum == null){
                cl.add(s);
            }
        }
        if (cl.size() > 1) {
            return ResultMap.error("未检测到字段名:" + cl.toString() + ",请核对");
        } else {
            return ResultMap.putOk();
        }
    }



    /**
     * 根据中文字段列表获取该字段所在的引号
     * @param value
     * @param sheet
     * @return
     */
    public static Integer getValIndex(String value, Sheet sheet, int columnRow) {
        Row row = sheet.getRow(columnRow);
        for (int i = 0;; i++) {
            if (row.getCell(i) != null) {
                if (value.equals(getValue(row.getCell(i)))) {
                    return i;
                }
            } else
                return null;
        }
    }


    /**
     * 获取单元格中的信息，并使其转换成字符串形式
     * 空信息返回空串
     * @param cell
     * @return
     */
    public static  String getValue(Cell cell) {
        if (cell == null) {
            return "";
        } else {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            return String.valueOf(cell.getStringCellValue()).trim();
        }
    }



    /**
     * 分段读取数据文件中的数据时使用
     * 暂时只运用于Excel文件
     * @param total  总量
     * @param range  每段范围
     * @return  返回int[] 数组长度为总段数 值为每段结束值即为每次循环的结束值
     */
    public static int[] getNumsInRange(int total,int range){
        int i ,a ;
        if(total <= range){
            i = 1;
            a = total;
        }else{
            if((total% range) != 0){
                i = total /range +1;
                a = total % range;
            }
            else {
                i =total/range;
                a = range;
            }
        }
        int c[] = new int[i] ;
        for (int j = 0; j < i; j++) {
            if(i == 1){
                c[j] = a;
            }else{
                if(j == i-1){
                    c[j] = a + c[j-1];
                }else {
                    c[j] = range * (j+1);
                }
            }
        }

        return c;
    }


}