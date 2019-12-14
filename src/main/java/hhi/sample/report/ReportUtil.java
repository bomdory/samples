package hhi.sample.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReportUtil {

	public static List<List<Object>> readExcelAsList(File file) {
		List<List<Object>> result = new ArrayList<List<Object>>();
		InputStream myxls = null;
		Workbook book;
		try {
			myxls = new FileInputStream(file);
			try {
				book = new XSSFWorkbook(myxls);
			} catch (Exception e) {
				book = new HSSFWorkbook(myxls);
			}
			FormulaEvaluator eval = book.getCreationHelper().createFormulaEvaluator();
			Sheet sheet = book.getSheetAt(0);
			for (Row row : sheet) {
				List<Object> rowData = new ArrayList<Object>();
				int cellSize = row.getLastCellNum();
				for (int i = 0; i < cellSize; i++) {
					Cell cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
					Object val = getCellData(cell, eval);
					rowData.add(val);
				}
				result.add(rowData);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				myxls.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	public static List<Object[]> readExcel(File file) {
		List<Object[]> result = new ArrayList<Object[]>();
		InputStream myxls = null;
		Workbook book = null;
		try {
			myxls = new FileInputStream(file);
			try {
				book = new HSSFWorkbook(myxls);
			} catch (Exception e) {
				book = new XSSFWorkbook(myxls);
			}
			FormulaEvaluator eval = book.getCreationHelper().createFormulaEvaluator();
			Sheet sheet = book.getSheetAt(0);
			for (Row row : sheet) {
				List<Object> rowData = new ArrayList<Object>();
				int cellSize = row.getLastCellNum();
				for (int i = 0; i < cellSize; i++) {
					Cell cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
					Object val = getCellData(cell, eval);
					rowData.add(val);
				}
				result.add(rowData.toArray(new Object[0]));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				myxls.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				book.close();
			} catch (IOException e) {
			}
		}
		return result;

	}

	private static Object getCellData(Cell cell, FormulaEvaluator eval) {
		CellValue cellValue = eval.evaluate(cell);
		Object result = null;
		if (cellValue == null) {
			return "";
		}
		if (cellValue.getCellType() == CellType.STRING) {
			result = cellValue.getStringValue();
		} else if (cellValue.getCellType() == CellType.NUMERIC) {
			if (DateUtil.isCellDateFormatted(cell)) {
				result = cell.getDateCellValue();
			} else {
				result = cell.getNumericCellValue();
			}
		} else if (cellValue.getCellType() == CellType.BOOLEAN) {
			result = cell.getBooleanCellValue();
		} else if (cellValue.getCellType() == CellType.FORMULA) {
			result = cell.getCellFormula();
		} else if (cellValue.getCellType() == CellType._NONE) {
			result = "";
		} else {
			result = "#N/A";
		}
		if (result == null) {
			result = "";
		}
		return result;
	}

	public static File write(List<Object[]> data, String filePath, String fileName) throws Exception {
		if (new File(filePath).isDirectory() == false) {
			new File(filePath).mkdirs();
		}
		return write(data, new File(filePath, fileName));
	}

	public static File write(Object[] header, List<Object[]> data, String filePath, String fileName) throws Exception {
		data.add(0, header);
		return write(data, filePath, fileName);
	}

	public static File write(List<Object[]> data, File resultFile) throws Exception {
		String sheetName = "Sheet1";
		return write(data, resultFile, sheetName, false);
	}

	public static File write(List<Object[]> data, File resultFile, String sheetName, boolean append) throws Exception {
//		String sheetName = "Sheet1";
		Workbook workbook;
		if (resultFile.getName().toLowerCase().endsWith(".xlsx") || data.size() > 65536) {
			if (append) {
				workbook = WorkbookFactory.create(resultFile);
			} else {
				workbook = new XSSFWorkbook();
			}
		} else {
			if (append) {
				workbook = WorkbookFactory.create(resultFile);
			} else {
				workbook = new HSSFWorkbook();
			}
		}
		Sheet sheet = workbook.createSheet(sheetName);
		int rownum = 0;
		for (Object[] rowData : data) {
			Row row = sheet.createRow(rownum++);
			int cellnum = 0;
			for (Object obj : rowData) {
				Cell cell = row.createCell(cellnum++);
				obj = obj == null ? "" : obj;
				if (obj instanceof Date) {
					cell.setCellValue((Date) obj);
				} else if (obj instanceof Boolean) {
					cell.setCellValue((Boolean) obj);
				} else if (obj instanceof Number) {
					try {
						double val = Double.parseDouble(obj.toString());
						cell.setCellValue((double) val);
					} catch (Exception e) {
						cell.setCellValue(obj.toString());
					}
				} else {

					cell.setCellValue(obj.toString());
				}
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(resultFile, append);
			workbook.write(out);
			out.close();
			workbook.close();
			System.out.println("Excel written successfully..");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultFile;
	}

//	public static File csvToExcel(File csvFile, String delimeter, File resultFile, String sheetName, boolean append) throws Exception {
//		Csv csv = new Csv();
//		csv.setFieldSeparatorRead(delimeter.charAt(0));
//		ResultSet rs = csv.read(csvFile.getAbsolutePath(), null, "utf8");
//		ResultSetMetaData meta = rs.getMetaData();
//		List<Object[]> data = new ArrayList<Object[]>();
//		int cnt = meta.getColumnCount();
//		List<Object> columns = new ArrayList<Object>();
//		for(int i =1 ; i <= cnt ; i++){
//			String col = meta.getColumnLabel(i);
//			columns.add(col);
//		}
//		data.add(columns.toArray(new Object[0]));
//		while(rs.next()){
//			List<Object> row = new ArrayList<Object>();
//			for(int i =1 ; i <= cnt ; i++){
//				Object val = rs.getObject(i);
//				row.add(val);
//			}
//			data.add(row.toArray(new Object[0]));
//		}
//		
//		return write(data, resultFile, sheetName, append);
//	}

//	public static File csvToExcel(File csvFile, String delimeter) throws Exception {
//		String resultFileName = csvFile.getName() + ".xlsx";
//		File resultFile = new File(csvFile.getParentFile(), resultFileName);
//		return csvToExcel(csvFile, delimeter, resultFile, );
//	}

//	public static void resultSetToExcel(ResultSet rs, File excelFile,
//			String sheetName, boolean append) throws Exception {
//		try {
//			rs.setFetchSize(2000);
//		} catch (Exception e) {
//		}
//		ResultSetMetaData meta = rs.getMetaData();
//		int cnt = meta.getColumnCount();
//		List<String> colList = new ArrayList<String>();
//		for (int i = 1; i <= cnt; i++) {
//			String col = meta.getColumnLabel(i);
//			colList.add(col);
//		}
//
//		SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook());
//		workbook.setCompressTempFiles(true);
//		SXSSFSheet sheet = (SXSSFSheet) workbook.getSheet(sheetName);
//		if (sheet == null) {
//			sheet = (SXSSFSheet) workbook.createSheet(sheetName);
//		}
//
//		sheet.setRandomAccessWindowSize(10000);
//		int rownum = 0;
//		Row row = sheet.getRow(rownum);
//		if (row == null) {
//			row = sheet.createRow(rownum);
//		}
//		rownum++;
//		for (int i = 0; i < colList.size(); i++) {
//			String col = colList.get(i);
//			Cell cell = row.getCell(i);
//			if (cell == null) {
//				cell = row.createCell(i);
//			}
//			cell.setCellValue(col);
//		}
//
//		while (rs.next()) {
//			row = sheet.getRow(rownum);
//			if (row == null) {
//				row = sheet.createRow(rownum);
//			}
//			rownum++;
//			for (int i = 0; i < colList.size(); i++) {
//				Object obj = rs.getObject(colList.get(i));
//				Cell cell = row.getCell(i);
//				if (cell == null) {
//					cell = row.createCell(i);
//				}
//				setValue(cell, obj);
//			}
//			if (rownum % 10000 == 0) {
//				System.out.println(rownum);
//			}
//		}
//
//		System.out.println(rownum);
//		try {
//			FileOutputStream out = new FileOutputStream(excelFile, append);
//			workbook.write(out);
//			workbook.close();
//			out.close();
//			System.out.println("Excel written successfully..");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			rs.close();
//		} catch (Exception e) {
//		}
//	}

	private static void setValue(Cell cell, Object val) {
		if (val == null) {
			return;
		}
		if (val instanceof Date) {
			cell.setCellValue((Date) val);
		} else if (val instanceof Boolean) {
			cell.setCellValue((Boolean) val);
		} else if (val instanceof Number) {
			try {
				double dval = Double.parseDouble(val.toString());
				cell.setCellValue((double) dval);
			} catch (Exception e) {
				cell.setCellValue(val.toString());
			}
		} else {
			cell.setCellValue(val.toString());
		}
	}

//	public void excelRead(File file) {
//
//		try {
//			InputStream is = new FileInputStream(file);
//			StreamingReader reader = StreamingReader.builder()
//					.rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
//					.bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
//					.sheetIndex(0)        // index of sheet to use (defaults to 0)
//					.sheetName("sheet1")  // name of sheet to use (overrides sheetIndex)
//					.read(is);            // InputStream or File for XLSX file (required)
//			List<List<String>> data = new ArrayList<List<String>>();
//			for (Row r : reader) {
//				List<String> values = new ArrayList<String>();
//				  for (Cell c : r) {
////				    System.out.println(c.getStringCellValue());
//				    values.add(c.getStringCellValue());
//				  }
//				  data.add(values);
//				}  
//			is.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public static Workbook getWorkvook(File excelFile) {
		Workbook book = null;
		try {
//			FileInputStream fis = new FileInputStream(excelFile);
			try {
				book = new XSSFWorkbook(excelFile);
			} catch (Exception e) {
				e.printStackTrace();
//				book = new HSSFWorkbook(fis);
			} finally {
//				fis.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return book;
	}

	public static File report(File reportFile) {

		Workbook workbook = getWorkvook(reportFile);
//		CreationHelper helper = workbook.getCreationHelper();
		FormulaEvaluator eval = workbook.getCreationHelper().createFormulaEvaluator();
		String sheetName = "Sheet1";
		Sheet sheet = workbook.getSheet(sheetName);
//		1. getData //sheet, cellId, table
//		2. Find cellId( 
//		3. replaceOrApend
		List<List<Object>> data = getSampleData();
		while (true) {
			String cellId = null;
			for (int rownum = 0; rownum < sheet.getLastRowNum(); rownum++) {
				Row row = sheet.getRow(rownum);
				if (row == null) {
					continue;
				}
				int cellSize = row.getLastCellNum();
//				row.getPhysicalNumberOfCells();
				for (int celnum = 0; celnum < cellSize; celnum++) {
					Cell cell = row.getCell(celnum, MissingCellPolicy.CREATE_NULL_AS_BLANK);
					Object val = getCellData(cell, eval);
//					compare val and cellId key
					if ("{test}".equals(val)) {
						cellId = "test"; // {test} -> test
						cell.setAsActiveCell();
						break;
					}
				}
			}
			if (cellId != null) {
				// getData by cellId
				CellAddress cell = sheet.getActiveCell();
				int rownum = cell.getRow();
				int colnum = cell.getColumn();
//							insertRow
//				sheet.shiftRows(rownum + 1, sheet.getLastRowNum(), data.size(), true, false);
				for (int rowIdx = 0; rowIdx < data.size(); rowIdx++) {
					List<Object> rowData = data.get(rowIdx);

					if(rowIdx > 0) {
						addRow(workbook, sheet, rownum+rowIdx);
					}
					Row tmpRow = sheet.getRow(rownum + rowIdx);
					if (tmpRow == null) {
						tmpRow = sheet.createRow(rownum + rowIdx);
					}
					for (int cellIdx = 0; cellIdx < rowData.size(); cellIdx++) {
						Cell tmpcel = tmpRow.getCell(colnum + cellIdx, MissingCellPolicy.CREATE_NULL_AS_BLANK);
						Object value = rowData.get(cellIdx);
						setValue(tmpcel, value);
					}
					
				}

			} else {
				break;
//				result.add(rowData.toArray(new Object[0]));
			}

		}
		try {
			FileOutputStream out = new FileOutputStream(reportFile, true);
			workbook.write(out);
			out.close();
			workbook.close();
			System.out.println("Excel written successfully..");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return reportFile;
	}

	private static void addRow(Workbook workbook, Sheet sheet, int sourceRowNum) {

		sheet.shiftRows(sourceRowNum, sheet.getLastRowNum() + 1, 1, true, false);
		XSSFSheet xSSFSheet = (XSSFSheet) sheet;
		// correcting bug that shiftRows does not adjusting references of the cells
		// if row 3 is shifted down, then reference in the cells remain r="A3", r="B3",
		// ...
		// they must be adjusted to the new row thoug: r="A4", r="B4", ...
		// apache poi 3.17 has done this properly but had have other bugs in shiftRows.
		for (int r = xSSFSheet.getFirstRowNum(); r < sheet.getLastRowNum() + 1; r++) {
			XSSFRow row = xSSFSheet.getRow(r);
			if (row != null) {
				long rRef = row.getCTRow().getR();
				for (Cell cell : row) {
					String cRef = ((XSSFCell) cell).getCTCell().getR();
					((XSSFCell) cell).getCTCell().setR(cRef.replaceAll("[0-9]", "") + rRef);
				}
			}
		}
		// end correcting bug

		for (int i = sheet.getNumMergedRegions()-1 ; i >= 0  ; i--) {
			CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
			if (cellRangeAddress.getFirstRow() <= sourceRowNum
					&& sourceRowNum <= cellRangeAddress.getLastRow()) {
				
				CellRangeAddress newCellRangeAddress = new CellRangeAddress(
						cellRangeAddress.getFirstRow()
						, cellRangeAddress.getLastRow() + 1
						, cellRangeAddress.getFirstColumn()
						, cellRangeAddress.getLastColumn());
				sheet.removeMergedRegion(i);
				sheet.addMergedRegion(newCellRangeAddress);
			}
		}
		
		Row sourceRow = sheet.getRow(sourceRowNum -1);
		Row newRow = sheet.getRow(sourceRowNum );	
		if(newRow == null) {
			newRow = sheet.createRow(sourceRowNum);
		}
		for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
			Cell oldCell = sourceRow.getCell(i);
			Cell newCell = newRow.getCell(i);
			if(newCell == null) {
				newCell = newRow.createCell(i,CellType.BLANK);
			}
			if (oldCell == null) {
				continue;
			}
			CellStyle oldStype = oldCell.getCellStyle();
			newCell.setCellStyle(oldStype);
		}

	}

	private static List<List<Object>> getSampleData() {

		List<List<Object>> data = new ArrayList();
		List<Object> row = new ArrayList<Object>();
		row.add("aaa");
		row.add("bbb");
		row.add("ccc");
		row.add("ddd");
		data.add(row);
		data.add(row);
		data.add(row);
		return data;
	}

	public static void main(String[] args) throws IOException {
		File template = new File("x:/tmp/report/test.xlsx");
		String reportFileName = template.getName().split("[.]")[0] + "-" + System.currentTimeMillis() + ".xlsx";
		File resultFile = new File(template.getParentFile(), reportFileName);
		FileUtils.copyFile(template, resultFile);;
		report(resultFile);
	}

};