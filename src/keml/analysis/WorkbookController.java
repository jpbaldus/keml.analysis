package keml.analysis;

import org.apache.poi.xssf.usermodel.*;

import keml.NewInformation;
import keml.PreKnowledge;

import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class WorkbookController {
	
	XSSFWorkbook wb;
	Sheet sheet;
	
	XSSFCellStyle defaultStyle;
	XSSFCellStyle headerStyle;
	XSSFCellStyle floatStyle;
	XSSFCellStyle trustStyle;
	XSSFCellStyle distrustStyle;
	XSSFCellStyle neutTrustStyle;
	XSSFCellStyle instructionStyle;
	XSSFCellStyle factStyle;
	XSSFCellStyle origLLMStyle;
	XSSFCellStyle origOtherStyle;

	public WorkbookController() {

		wb = new XSSFWorkbook();
		sheet = wb.createSheet("Trust");
	    Row headers = sheet.createRow(0);
	    
		Cell start = headers.createCell(0);
		defaultStyle = (XSSFCellStyle) start.getCellStyle();
		defaultStyle.setAlignment(HorizontalAlignment.CENTER);
		
		 //************* header style *****************
	    Font headerFont = wb.createFont();
	    headerFont.setBold(true);
	    headerStyle = wb.createCellStyle();
	    headerStyle.setRotation((short)90);
	    headerStyle.setFont(headerFont);
	    	
		start.setCellValue("Time Stamp");
		start.setCellStyle(headerStyle);
		Cell i = headers.createCell(1);
		i.setCellValue("Message");
		i.setCellStyle(headerStyle);
		i = headers.createCell(2);
		i.setCellValue("Initial Trust");
		i.setCellStyle(headerStyle);
		i = headers.createCell(3);
		i.setCellValue("Current Trust");
		i.setCellStyle(headerStyle);
		i = headers.createCell(4);
		i.setCellValue("#Arguments");
		i.setCellStyle(headerStyle);
		i = headers.createCell(5);
		i.setCellValue("#Repetitions");
		i.setCellStyle(headerStyle);

		
		// *********** styles *******************
		
		CellStyle floatStyle =  wb.createCellStyle();
	    floatStyle.setDataFormat(wb.createDataFormat().getFormat("#,##"));
	    
		
		// additional color styles:
	    // *************** Trust ****************
	    trustStyle = wb.createCellStyle();
	    trustStyle.setDataFormat(floatStyle.getDataFormat());
	    trustStyle.setAlignment(HorizontalAlignment.CENTER);
	    trustStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#339966"), null));
	    trustStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    // *************** Distrust *************
	    distrustStyle = wb.createCellStyle();
	    trustStyle.setDataFormat(floatStyle.getDataFormat());
	    distrustStyle.setAlignment(HorizontalAlignment.CENTER);
	    distrustStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#FF5F5F"), null));
	    distrustStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	 // *************** neutral about trust *************
	    neutTrustStyle = wb.createCellStyle();
	    trustStyle.setDataFormat(floatStyle.getDataFormat());
	    neutTrustStyle.setAlignment(HorizontalAlignment.CENTER);
	    neutTrustStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
	    neutTrustStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// ************** isFact *************
	    factStyle = wb.createCellStyle();
	    factStyle.setAlignment(HorizontalAlignment.CENTER);
	    factStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#99CC00"), null));
	    factStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    
	    // ************* is Instruction **********
	    instructionStyle = wb.createCellStyle();
	    instructionStyle.setAlignment(HorizontalAlignment.CENTER);
	    instructionStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#FFCC00"), null));
	    instructionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    
	    // *************** origin LLM style**************
	    origLLMStyle = wb.createCellStyle();
	    origLLMStyle.setAlignment(HorizontalAlignment.CENTER);
	    origLLMStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#CCFFFF"), null));
	    origLLMStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); 
	    
	    // *************** origin Other style**************
	    origOtherStyle = wb.createCellStyle();
	    origOtherStyle.setAlignment(HorizontalAlignment.CENTER);
	    origOtherStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#FFFF99"), null));
	    origOtherStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    
	}
	
	public void putData(List<NewInformation> newInfos, List<PreKnowledge> preKnowledge) {
//		style.setWrapText(true);
		int offset=1; // adapted in loop
		for (int i=0; i < preKnowledge.size(); i++) {
			PreKnowledge pre = preKnowledge.get(i);
			Row r = sheet.createRow(offset++);
			Cell t = r.createCell(0);
			t.setCellValue(-1);
			colorByIsInstruction(t, pre.isIsInstruction());
			Cell msg = r.createCell(1);
			msg.setCellValue(pre.getMessage());
			colorByOrigin(msg, false);
			setAndColorByValue(r.createCell(2),pre.getInitialTrust());
			setAndColorByValue(r.createCell(3),pre.getCurrentTrust());
			r.createCell(4).setCellValue(pre.getTargetedBy().size());
			r.createCell(5).setCellValue(pre.getRepeatedBy().size());
		}
		for (int i=0; i< newInfos.size();i++) {
			NewInformation info = newInfos.get(i);
			Row r = sheet.createRow(offset++);
			Cell t = r.createCell(0);
			t.setCellValue(info.getTiming());
			colorByIsInstruction(t, info.isIsInstruction());
			Cell msg = r.createCell(1);
			msg.setCellValue(info.getMessage());
			colorByOrigin(msg, info.getSourceConversationPartner().getName().equals("LLM"));
			setAndColorByValue(r.createCell(2),info.getInitialTrust());
			setAndColorByValue(r.createCell(3),info.getCurrentTrust());
			r.createCell(4).setCellValue(info.getTargetedBy().size());
			r.createCell(5).setCellValue(info.getRepeatedBy().size());
		}	
	}
	
	private void colorByIsInstruction(Cell cell, boolean isInstruction) {
		if (isInstruction) {
			cell.setCellStyle(instructionStyle);
		} else {
			cell.setCellStyle(factStyle);
		}
	}
	
	private void colorByOrigin(Cell cell, boolean isLLM) {
		if (isLLM) {
			cell.setCellStyle(origLLMStyle);
		} else {
			cell.setCellStyle(origOtherStyle);
		}
	}
	
	private void setAndColorByValue(Cell cell, float value) {
		cell.setCellValue(value);
		if (value > 0.0f)
			cell.setCellStyle(trustStyle);
		else if (value <0.0f)
			cell.setCellStyle(distrustStyle);
		else
			cell.setCellStyle(neutTrustStyle);
	}
	
	public void write(String file) throws IOException {
		String path = FilenameUtils.removeExtension(file) + "-trust.xlsx";
		try(FileOutputStream o = new FileOutputStream(path)) {
			wb.write(o);
			wb.close();
		}

	}

}