package org.sonarqube.utility;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sonarqube.model.Issue;
import org.sonarqube.model.TextRange;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.joining;

public class Excel {

  private XSSFWorkbook workbook;
  private XSSFSheet sheet;

  public void write(Set<Issue> issues) throws IOException {
    workbook = new XSSFWorkbook();
    String safeName = WorkbookUtil.createSafeSheetName("Issues");
    sheet = workbook.createSheet(safeName);

    createHeader();
    writeRows(issues);
    autoSizeColumns();

    FileOutputStream stream = new FileOutputStream("sonarReport.xlsx");
    workbook.write(stream);
    stream.close();
  }

  private void createHeader() {
    XSSFFont font = workbook.createFont();
    XSSFCellStyle cellStyle = workbook.createCellStyle();
    font.setBold(true);
    font.setColor(HSSFColor.WHITE.index);
    cellStyle.setFont(font);
    cellStyle.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyle.setAlignment(HorizontalAlignment.CENTER);

    int rowNum = 0;
    XSSFRow row = sheet.createRow(rowNum);

    XSSFCell key = row.createCell(0, CellType.STRING);
    key.setCellValue("Key");
    key.setCellStyle(cellStyle);
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));

    XSSFCell component = row.createCell(1);
    component.setCellValue("Component");
    component.setCellStyle(cellStyle);
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));

    XSSFCell project = row.createCell(2);
    project.setCellValue("Project");
    project.setCellStyle(cellStyle);
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));

    XSSFCell rule = row.createCell(3);
    rule.setCellValue("Rule");
    rule.setCellStyle(cellStyle);
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 3, 3));

    XSSFCell status = row.createCell(4);
    status.setCellValue("Status");
    status.setCellStyle(cellStyle);
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 4, 4));

    XSSFCell resolution = row.createCell(5);
    resolution.setCellValue("Resolution");
    resolution.setCellStyle(cellStyle);
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 5, 5));

    XSSFCell severity = row.createCell(6);
    severity.setCellValue("Severity");
    severity.setCellStyle(cellStyle);
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 6, 6));

    XSSFCell message = row.createCell(7);
    message.setCellValue("Message");
    message.setCellStyle(cellStyle);
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 7, 7));

    XSSFCell lineNumber = row.createCell(8);
    lineNumber.setCellValue("Line Number");
    lineNumber.setCellStyle(cellStyle);
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 8, 8));

    XSSFCell textRange = row.createCell(9);
    textRange.setCellValue("Text Range");
    textRange.setCellStyle(cellStyle);
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 9, 12));

    XSSFCell author = row.createCell(13);
    author.setCellValue("Author");
    author.setCellStyle(cellStyle);
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 13, 13));

    XSSFCell effort = row.createCell(14);
    effort.setCellValue("Effort");
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 14, 14));
    effort.setCellStyle(cellStyle);

    XSSFCell creationDate = row.createCell(15);
    creationDate.setCellValue("Creation Date");
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 15, 15));
    creationDate.setCellStyle(cellStyle);

    XSSFCell updateDate = row.createCell(16, CellType.STRING);
    updateDate.setCellValue("Updated Date");
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 16, 16));
    updateDate.setCellStyle(cellStyle);

    XSSFCell tags = row.createCell(17);
    tags.setCellValue("Tags");
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 17, 17));
    tags.setCellStyle(cellStyle);

    XSSFCell type = row.createCell(18);
    type.setCellValue("Type");
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 18, 18));
    type.setCellStyle(cellStyle);

    row = sheet.createRow(1);

    XSSFCell startLine = row.createCell(9);
    startLine.setCellValue("Start Line");
    startLine.setCellStyle(cellStyle);

    XSSFCell endLine = row.createCell(10);
    endLine.setCellValue("End Line");
    endLine.setCellStyle(cellStyle);

    XSSFCell startOffset = row.createCell(11);
    startOffset.setCellValue("Start Offset");
    startOffset.setCellStyle(cellStyle);

    XSSFCell endOffset = row.createCell(12);
    endOffset.setCellValue("End Offset");
    endOffset.setCellStyle(cellStyle);
  }

  private void autoSizeColumns() {
    for (short i = sheet.getRow(2).getFirstCellNum(),
         end = sheet.getRow(0).getLastCellNum(); i < end; i++) {
      sheet.autoSizeColumn(i);
    }
  }

  private void writeRows(Set<Issue> issues) {
    final AtomicInteger rowNum = new AtomicInteger(2);
    issues.forEach(issue -> createRow(rowNum, issue));
  }

  private void createRow(AtomicInteger rowNum, Issue issue) {
    XSSFRow row = sheet.createRow(rowNum.getAndIncrement());

    XSSFCell key = row.createCell(0);
    key.setCellValue(issue.getKey());

    XSSFCell component = row.createCell(1);
    component.setCellValue(issue.getComponent());

    XSSFCell project = row.createCell(2);
    project.setCellValue(issue.getProject());

    XSSFCell rule = row.createCell(3);
    rule.setCellValue(issue.getRule());

    XSSFCell status = row.createCell(4);
    status.setCellValue(issue.getStatus());

    XSSFCell resolution = row.createCell(5);
    resolution.setCellValue(issue.getResolution());

    XSSFCell severity = row.createCell(6);
    severity.setCellValue(issue.getSeverity());

    XSSFCell message = row.createCell(7);
    message.setCellValue(issue.getMessage());

    XSSFCell lineNumber = row.createCell(8);
    lineNumber.setCellValue(issue.getLine());

    if (issue.getTextRange() != null) {
      writeTextRange(issue.getTextRange(), row);
    }

    XSSFCell author = row.createCell(13);
    author.setCellValue(issue.getAuthor());

    XSSFCell effort = row.createCell(14);
    effort.setCellValue(issue.getEffort());

    XSSFCell creationDate = row.createCell(15);
    creationDate.setCellValue(issue.getCreationDate().toString());

    XSSFCell updateDate = row.createCell(16);
    updateDate.setCellValue(issue.getUpdateDate().toString());

    XSSFCell tags = row.createCell(17);
    tags.setCellValue(getCommaSeparatedTags(issue));

    XSSFCell type = row.createCell(18);
    type.setCellValue(issue.getType());
  }

  private void writeTextRange(TextRange textRange, XSSFRow row) {
    XSSFCell startLine = row.createCell(9);
    startLine.setCellValue(textRange.getStartLine());

    XSSFCell endLine = row.createCell(10);
    endLine.setCellValue(textRange.getEndLine());

    XSSFCell startOffset = row.createCell(11);
    startOffset.setCellValue(textRange.getStartOffset());

    XSSFCell endOffset = row.createCell(12);
    endOffset.setCellValue(textRange.getEndOffset());
  }

  private String getCommaSeparatedTags(Issue issue) {
    return issue.getTags().stream().collect(joining(", "));
  }
}