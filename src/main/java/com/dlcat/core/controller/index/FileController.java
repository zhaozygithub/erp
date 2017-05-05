package com.dlcat.core.controller.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.border.Border;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.dlcat.common.BaseController;
import com.dlcat.core.model.SysUser;
import com.dlcat.service.echarts.IndexEchartsService;
import com.dlcat.service.echarts.impl.IndexEchartsImpl;

/** 
* @author zhaozhongyuan
* @date 2017年5月2日 下午2:45:56 
* @Description: 对文件的操作
*/
public class FileController extends BaseController {
	
	private IndexEchartsService indexEchartsService=new IndexEchartsImpl();
	/**
	* @author:zhaozhongyuan 
	* @Description:下载生成的excel
	* @return void   
	* @date 2017年5月2日 下午6:21:06  
	*/
	public void index() {
		File file=new File(getPara("path"));
		renderFile(file);
	}
	/**
	* @author:zhaozhongyuan 
	* @Description:首页生成excel
	* @return void   
	 * @throws IOException 
	* @date 2017年5月2日 下午2:46:11  
	*/
	public void exportData() throws IOException {
       
		String year=getPara("data");
		
		Map<String, Number> map=indexEchartsService.getMdNum(year, (SysUser)getSessionAttr("user"));
		
		 String[] heards={"时间","门店名称","金额"};
         dataToExcel(map,heards, "机构年度数据",year);
		 Map<String, String> map2=new HashMap<String, String>();
		 map2.put("success", "true");
		 map2.put("path", "E:/1.xls");
    		
		renderJson(map2);
    
	}
	
	private void dataToExcel(Map<String, Number> map,String[] heards,String sheetName,String year) {
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet(sheetName);
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow(0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		HSSFCell cell = null;
		for (int i = 0; i < heards.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(heards[i]);
			cell.setCellStyle(style);
		}

		
		Set<String> keys=map.keySet();
		
		int count=1;
		for (String key : keys) {
			row = sheet.createRow(count);
			row.createCell(0).setCellValue(year);
			row.createCell(1).setCellValue(key);
			row.createCell(2).setCellValue(Double.parseDouble(map.get(key).toString()));
			row.setRowStyle(style);
        	count++;
		}
		
       
          // 自适应列宽
     		for (int i = 0; i < heards.length; i++) {
     			sheet.autoSizeColumn(i, true);
     		}
     		
     		sheet.addMergedRegion(new CellRangeAddress(1,count-1, 0, 0));
     		
    		for (int i = 1; i <= count-1; i++) {
    			sheet.getRow(i).getCell(0).setCellStyle(style);
    			sheet.getRow(i).getCell(1).setCellStyle(style);
    			sheet.getRow(i).getCell(2).setCellStyle(style);

    		}
     		
     		File file=new File("E:/1.xls");
     	// 第六步，将文件存到指定位置
    		try {
    			FileOutputStream fout = new FileOutputStream(file);
    			wb.write(fout);
    			fout.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
	}
}
