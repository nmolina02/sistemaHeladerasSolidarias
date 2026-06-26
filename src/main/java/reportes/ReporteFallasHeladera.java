package reportes;

import javax.persistence.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.Getter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;

import java.io.FileOutputStream;
import java.io.IOException;

@Entity
@DiscriminatorValue("FALLAS_HELADERA")
@javax.persistence.Table(name = "reporte_fallas_heladera")
@Getter
public class ReporteFallasHeladera extends Reporte {
    @OneToMany(mappedBy = "reporte", cascade = CascadeType.ALL)
    public List<FallasHeladera> fallasPorHeladera;

    @Transient
    public TipoReporte tipoReporte;

    public ReporteFallasHeladera(){
        super();
        this.fallasPorHeladera = GeneradorDeReportes.getInstance().getFallasPorHeladera();
        this.tipoReporte = TipoReporte.FALLAS_HELADERA;
        for (FallasHeladera falla : fallasPorHeladera) {
            falla.setReporte(this);
        }
    }

    @Override
    public void generarReporteExcel() throws IOException {
        Path directoryPath = Paths.get(this.getPathDirectorio());
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        String fechaInicial = this.getFechaInicial().format(this.getFormatter());
        String fechaInicialEncabezado = fechaInicial.replace("/", "-");
        String fechaFinal = this.getFechaFinal().format(this.getFormatter());
        String fechaFinalEncabezado = fechaFinal.replace("/", "-");
        String fileName = "Fallas Heladera (" + fechaInicialEncabezado + " - " + fechaFinalEncabezado + ").xlsx";
        String filePath = directoryPath.resolve(fileName).toString();

        this.setPathArchivo(filePath);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Fallas Heladera (" + fechaInicialEncabezado + " - " + fechaFinalEncabezado + ")");

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"ID", "Heladera", "Cantidad de fallas"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
        }

        int id = 1;
        int rowNum = 1;
        for (FallasHeladera falla : fallasPorHeladera) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(String.valueOf(id));
            row.createCell(1).setCellValue(falla.getHeladera().getNombreHeladera());
            row.createCell(2).setCellValue(String.valueOf(falla.getCantidadFallas()));
            id++;
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }

    @Override
    public void convertirExcelAPdf(String excelFilePath, String pdfFilePath) throws IOException {
        Workbook workbook = new XSSFWorkbook(excelFilePath);
        Sheet sheet = workbook.getSheetAt(0);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        String fechaInicial = this.getFechaInicial().format(this.getFormatter());
        String fechaFinal = this.getFechaFinal().format(this.getFormatter());
        String mainTitle = "Reporte de Fallas Heladera";
        String dateTitle = "(" + fechaInicial + " - " + fechaFinal + ")";

        // Encabezado con logo y título
        ImageData logo = ImageDataFactory.create("src/main/resources/mapaInteractivoHeladeras/sistema/images/principal/logo2.png");
        Image logoImage = new Image(logo).scaleToFit(70, 70);

        Table headerTable = new Table(new float[]{1, 3});
        headerTable.setWidth(UnitValue.createPercentValue(100));

        com.itextpdf.layout.element.Cell combinedCell = new com.itextpdf.layout.element.Cell()
                .add(logoImage.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)) // Centrar el logo
                .add(new Paragraph(mainTitle)
                        .setFontSize(20).setBold().setTextAlignment(TextAlignment.CENTER)) // Título principal
                .add(new Paragraph(dateTitle)
                        .setFontSize(14).setTextAlignment(TextAlignment.CENTER)) // Subtítulo
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        headerTable.addCell(combinedCell);

        document.add(headerTable);

        document.add(new Paragraph("\n")); // Espacio debajo del encabezado

        Table table = new Table(sheet.getRow(0).getPhysicalNumberOfCells());
        table.setWidth(UnitValue.createPercentValue(100));

        for (Cell cell : sheet.getRow(0)) {
            table.addHeaderCell(new Paragraph(cell.getStringCellValue()).setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(new DeviceRgb(211, 211, 211)));
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            for (Cell cell : row) {
                table.addCell(new Paragraph(cell.toString()).setTextAlignment(TextAlignment.CENTER));
            }
        }

        document.add(table);

        // Sección del pie de página
        int totalPages = pdfDoc.getNumberOfPages();
        for (int i = 1; i <= totalPages; i++) {
            document.setMargins(20, 20, 40, 20);
            document.showTextAligned(
                    new Paragraph("Página " + i + " de " + totalPages)
                            .setFontColor(new DeviceRgb(43, 137, 241))
                            .setFontSize(10),
                    520, 20, i,
                    TextAlignment.CENTER,
                    VerticalAlignment.BOTTOM, 0);

            // Borde celeste
            PdfCanvas pdfCanvas = new PdfCanvas(pdfDoc.getPage(i));
            pdfCanvas.setLineWidth(1)
                    .setStrokeColor(new DeviceRgb(43, 137, 241))
                    .moveTo(20, 40)
                    .lineTo(580, 40)
                    .stroke();
        }

        document.close();

        try (FileOutputStream fos = new FileOutputStream(pdfFilePath)) {
            fos.write(byteArrayOutputStream.toByteArray());
        }

        workbook.close();
    }
}