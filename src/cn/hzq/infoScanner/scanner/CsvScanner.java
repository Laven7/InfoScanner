package cn.hzq.infoScanner.scanner;

import cn.hzq.infoScanner.annotation.ColumnProcesser;
import cn.hzq.infoScanner.annotation.ColumnType;
import cn.hzq.infoScanner.base.ResultMap;
import cn.hzq.infoScanner.base.SaveListDao;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CSV类型数据文件录入
 *
 * @author hxhaaj
 */
public class CsvScanner  extends CsvScannerUtil{
	private CsvScanner(){}
	/**
	 *
	 * @param filePath 文件绝对路径
	 * @param entityClazz 实体类class对象
	 * @param dao 导入数据接口
	 * @param cellProcessor csv文件单元处理器
	 * @param <T>
	 * @return ResultMap
	 */
	public static <T> ResultMap scanAndSave(String filePath, Class<T> entityClazz, SaveListDao<T> dao, CellProcessor[] cellProcessor) {
		ResultMap cm = new CsvScannerUtil().checkPostfix(filePath);
		if(!cm.isOkObj()){
			return cm;
		}else{
			List<T> list = new ArrayList<>();
			ICsvBeanReader inFile =null;
			try {
				InputStreamReader reader = new InputStreamReader(new FileInputStream(new File(
						filePath)), "GBK");
				// 采用BeanReader方式 读取CSV文件
				inFile = new CsvBeanReader(reader,
						CsvPreference.EXCEL_PREFERENCE);
				// 获取CSV文件的第一行 字段信息
				String[] CsvHeader = inFile.getHeader(true);

				//CellProcessor 单元处理器 由形参传递  具体entity中单元处理器不一样
				/*CellProcessor[] xsxkProcessors = new CellProcessor[] { null,
					null, null, null, null, null, null, null, null, null, null,
					null, null, null };*/

				// 获取entity中key为中文的 中英文字段Map集合
				Map<String, String> exportColumnMap = ColumnProcesser.getExportColumnMap(entityClazz, ColumnType.CN);
				// 获取entity中文字段列表
//				List<String> cnColumnNameList = ColumnProcesser.getColumnName(entityClazz, ColumnType.CN);
				if (!checkCloumns(entityClazz,CsvHeader).isOkObj()){
					return checkCloumns(entityClazz,CsvHeader);
				}else{
					// 根据CSV文件第一行头部中文字段信息 匹配对应的英文字段名
					for (int i = 0; i < CsvHeader.length; i++) {
						CsvHeader[i] = exportColumnMap.get(CsvHeader[i]);
					}
					T t;
					while ((t = inFile.read(entityClazz, CsvHeader, cellProcessor)) != null) {
						list.add(t);
					}
					System.err.println(list.size());
					if(list.size() < 1 ){
						return ResultMap.error("csv文件为空");
					}else{
						// DAO接口 存入数据库
						int saveNum = dao.saveList2DB(list);
						return ResultMap.ok("共存入"+saveNum+"条数据");
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Csv FileStream road filed");
			}finally{
				try {
					inFile.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException("Csv FileStream close filed");
				}
			}
		}

	}

	public static <T> ResultMap checkCloumns(Class<T> entityClazz, String[] csvHeader){
		List<String> columnNameList = ColumnProcesser.getColumnNameList(entityClazz, ColumnType.CN);
		for (int i = 0; i < csvHeader.length;i++){
			for (int j = 0; j < columnNameList.size(); j++){
				if(columnNameList.get(j).equals(csvHeader[i])){
					columnNameList.remove(j);
					break;
				}
			}
		}
		if (columnNameList.size() < 1)
			return ResultMap.putOk();
		else
			return ResultMap.error("未检测到字段名:" + columnNameList.toString() + " 请核对");
	}

}
