package com.dlcat.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 利用开源组件POI3.9动态导出EXCEL文档
 */
public class ExportExcelUtil {

	/**
	 * @param sheetName
	 *            sheet名字
	 * @param headers
	 *            表格属性列名数组
	 * @param resMapList
	 *            需要显示的数据集合
	 * @param filedNames
	 *            对应列明的实体字段名字
	 * @param out
	 *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void exportExcel(String sheetName, String[] headers, List<Map> resMapList, String[] filedNames,
			OutputStream out) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(sheetName);
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth((short) 15);
		// 生成一个样式
		HSSFCellStyle style = workbook.createCellStyle();
		// 设置这些样式
		style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// 生成一个字体
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式
		style.setFont(font);
		// 生成并设置另一个样式
		HSSFCellStyle style2 = workbook.createCellStyle();
		style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 生成另一个字体
		HSSFFont font2 = workbook.createFont();
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		// 把字体应用到当前的样式
		style2.setFont(font2);

		HSSFFont font3 = workbook.createFont();
		font3.setColor(HSSFColor.BLUE.index);

		// 产生表格标题行
		HSSFRow row = sheet.createRow(0);
		for (short i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		// 遍历集合数据，产生数据行
		int index = 0;
		for (Map itMap : resMapList) {
			index++;
			row = sheet.createRow(index);
			int i = 0;
			for (String item : filedNames) {
				if (item != null) {
					item = item.trim();
				}
				HSSFCell cell = row.createCell(i);
				cell.setCellStyle(style2);
				HSSFRichTextString richString = new HSSFRichTextString(
						itMap.get(item) == null ? "" : itMap.get(item).toString());
				richString.applyFont(font3);
				cell.setCellValue(richString.getString());
				i++;
			}
		}
		try {
			workbook.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	* @author:zhaozhongyuan 
	* @Description:导入单表数据，map：key 中文名字，value 对应的数据库字段名
	* @return void   
	* @date 2017年5月17日 下午8:39:36  
	*/
	public static void imporeExcel(String tableName, Map<String, String> map, File file) throws Exception {

		InputStream is = new FileInputStream(file);

		List<Record> recordList = new ArrayList<Record>();
		HSSFWorkbook wb = new HSSFWorkbook(is);

		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow heards = sheet.getRow(0);
		HSSFRow row = null;

		// 标题总列数
		int colNum = heards.getPhysicalNumberOfCells();
		
		// 标题总行数
		int rowNum = sheet.getLastRowNum();
		String column = "";
		for (int i = 1; i <= rowNum; i++) {
			row = sheet.getRow(i);
			Record record = new Record();
			//给每一列赋值
			for (int j = 0; j < colNum; j++) {
				column = heards.getCell(j).getStringCellValue();
				//有可能map中找不到此列名
				try {
					record.set(map.get(column), getCellFormatValue(row.getCell(j)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			recordList.add(record);
		}
		Db.batchSave(tableName, recordList, recordList.size());

		is.close();

	}

	/**
	 * 根据HSSFCell类型设置数据
	 * 
	 * @param cell
	 * @return
	 * @throws ParseException 
	 */
	private static String getCellFormatValue(HSSFCell cell){
		String cellvalue = "";
		if (cell != null) {
			// 判断当前Cell的Type
			switch (cell.getCellType()) {
			// 如果当前Cell的Type为NUMERIC
			case HSSFCell.CELL_TYPE_NUMERIC:
			case HSSFCell.CELL_TYPE_FORMULA: {
				// 判断当前的cell是否为Date
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// 如果是Date类型则，转化为Data格式

					// 方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
					// cellvalue = cell.getDateCellValue().toLocaleString();

					// 方法2：这样子的data格式是不带带时分秒的：2011-10-12
					Date date = cell.getDateCellValue();
					/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					cellvalue = sdf.format(date);*/
					//把日期转为毫秒数
					cellvalue=String.valueOf(date.getTime()/1000);
					
					

				}
				// 如果是纯数字
				else {
					DecimalFormat df = new DecimalFormat("########");
					// 取得当前Cell的数值
					cellvalue = df.format(cell.getNumericCellValue());
				}
				break;
			}
			// 如果当前Cell的Type为STRIN
			case HSSFCell.CELL_TYPE_STRING:
				// 取得当前的Cell字符串
				cellvalue = cell.getRichStringCellValue().getString();
				break;
			// 默认的Cell值
			default:
				cellvalue = " ";
			}
		} else {
			cellvalue = "";
		}
		return cellvalue;

	}

}