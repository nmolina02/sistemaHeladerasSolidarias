package reportes;

import Heladera.Heladera;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import persistencia.ClaseCRUD;
import persona.personas.PersonaFisica;
import persona.roles.colaborador.Colaborador;
import repository.RepositoryReportes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GeneradorDeReportes {
    private static GeneradorDeReportes instancia = null;
    @Setter private List<FallasHeladera> fallasPorHeladera = new ArrayList<>();
    @Setter private List<MovimientosHeladera> movimientosPorHeladera = new ArrayList<>();
    @Setter private List<ViandasPorColaborador> viandasPorColaborador = new ArrayList<>();
    private int contadorReportes = 0;

    private GeneradorDeReportes(){}

    public static GeneradorDeReportes getInstance() {
        if(instancia == null)
            instancia = new GeneradorDeReportes();
        return instancia;
    }

    public void falla_heladera(Heladera heladera){
        FallasHeladera fallasHeladeraAux = this.buscar_en_lista_de_fallas(heladera);

        if (fallasHeladeraAux!=null){
            fallasHeladeraAux.cantidadFallas++;
        } else {
            FallasHeladera nuevaFalla = new FallasHeladera(heladera);
            fallasPorHeladera.add(nuevaFalla);
        }
    }

    public void donacion_viandas(Colaborador colaborador, int viandasDonadas){
        ViandasPorColaborador viandasPorColaboradorAux = this.buscar_en_lista_de_viandas_por_colaborador(colaborador);

        if(viandasPorColaboradorAux!=null){
            viandasPorColaboradorAux.viandasDonadas += viandasDonadas;
        }else{
            ViandasPorColaborador colaboradorNuevo = new ViandasPorColaborador(colaborador,viandasDonadas);
            viandasPorColaborador.add(colaboradorNuevo);
        }
    }

    public void movimientos_heladera(Heladera heladera, int movimientos) {
        MovimientosHeladera movimientosHeladeraAux = this.buscar_en_lista_de_movimientos_por_heladera(heladera);
        if (movimientosHeladeraAux != null) {
            if (movimientos < 0) {
                movimientosHeladeraAux.cantViandasRetiradas -= movimientos;
            }else {
                movimientosHeladeraAux.cantViandasColocadas += movimientos;
            }
        } else {
            MovimientosHeladera nuevoMovimiento = new MovimientosHeladera(heladera, movimientos);
            movimientosPorHeladera.add(nuevoMovimiento);
        }
    }

    private FallasHeladera buscar_en_lista_de_fallas(Heladera heladera){
        for(FallasHeladera falla : fallasPorHeladera){
            if(falla.getHeladera().getNombreHeladera() == null){
                return null;
            }
            else if(falla.getHeladera().getNombreHeladera().equals(heladera.getNombreHeladera())){
                return falla;
            }
        }
        return null;
    }

    private ViandasPorColaborador buscar_en_lista_de_viandas_por_colaborador(Colaborador colaboradorHumano){
        for(ViandasPorColaborador vianda : viandasPorColaborador){
            PersonaFisica persona = (PersonaFisica) vianda.getColaboradorHumano().getPersona();
            String nombreApellidoPersona = persona.getNombre() + " " + persona.getApellido();

            PersonaFisica personaColaborador = (PersonaFisica) colaboradorHumano.getPersona();
            String nombreApellidoColaborador = personaColaborador.getNombre() + " " + personaColaborador.getApellido();
            if(nombreApellidoPersona.equals(nombreApellidoColaborador)){
                return vianda;
            }
        }
        return null;
    }

    private MovimientosHeladera buscar_en_lista_de_movimientos_por_heladera(Heladera heladera){
        for(MovimientosHeladera movimiento : movimientosPorHeladera){
            if(movimiento.getHeladera().getNombreHeladera().equals(heladera.getNombreHeladera())){
                return movimiento;
            }
        }
        return null;
    }

    public ReporteFallasHeladera generarReporteFallasHeladera(){
        this.contadorReportes++;
        ReporteFallasHeladera nuevoReporteFallas = new ReporteFallasHeladera();
        RepositoryReportes repositoryReportes = RepositoryReportes.getInstance();
        repositoryReportes.getReportesHistoricos().add(nuevoReporteFallas);
        return nuevoReporteFallas;
    }

    public ReporteViandasPorColaborador generarReporteViandasPorColaborador(){
        this.contadorReportes++;
        ReporteViandasPorColaborador nuevoReporteViandasPorColaborador = new ReporteViandasPorColaborador();
        RepositoryReportes repositoryReportes = RepositoryReportes.getInstance();
        repositoryReportes.getReportesHistoricos().add(nuevoReporteViandasPorColaborador);
        return nuevoReporteViandasPorColaborador;
    }

    public ReporteMovimientosHeladera generarReporteMovimientosHeladera(){
        this.contadorReportes++;
        ReporteMovimientosHeladera nuevoReporteMovimientos = new ReporteMovimientosHeladera();
        RepositoryReportes repositoryReportes = RepositoryReportes.getInstance();
        repositoryReportes.getReportesHistoricos().add(nuevoReporteMovimientos);
        return nuevoReporteMovimientos;
    }

    public void generar_reporte() throws IOException {
        ReporteMovimientosHeladera reporteMovimientosHeladera = this.generarReporteMovimientosHeladera();
        ReporteFallasHeladera reporteFallasHeladera = this.generarReporteFallasHeladera();
        ReporteViandasPorColaborador reporteViandasPorColaborador = this.generarReporteViandasPorColaborador();

        /*reporteMovimientosHeladera.generarReporteFisico();
        reporteFallasHeladera.generarReporteFisico();
        reporteViandasPorColaborador.generarReporteFisico();

        reporteMovimientosHeladera.generarNombreArchivo();
        reporteFallasHeladera.generarNombreArchivo();
        reporteViandasPorColaborador.generarNombreArchivo();*/

        ClaseCRUD.getInstance().add(reporteMovimientosHeladera);
        ClaseCRUD.getInstance().add(reporteFallasHeladera);
        ClaseCRUD.getInstance().add(reporteViandasPorColaborador);

        //vaciar listas
        this.fallasPorHeladera.clear();
        this.viandasPorColaborador.clear();
        this.movimientosPorHeladera.clear();
    }

    public List<?> generar_reporte_personalizado(String tipoReporte, LocalDateTime horario) throws IOException {
        TipoReporte tipoReporteEnum = TipoReporte.valueOf(tipoReporte);
        List<?> reporte = null;
        switch (tipoReporteEnum) {
            case FALLAS_HELADERA:
                reporte = this.generarReporteFallasHeladeraIndividual(horario);
                break;
            case MOVIMIENTOS_HELADERA:
                reporte = this.generarReporteMovimientosHeladeraIndividual(horario);
                break;
            case VIANDAS_COLABORADOR:
                reporte = this.generarReporteViandasPorColaboradorIndividual(horario);
                break;
            default:
                break;
        }
        return reporte;
    }

    private List<FallasHeladera> generarReporteFallasHeladeraIndividual(LocalDateTime horario) {
        return fallasPorHeladera.stream()
                .filter(fallaHeladera -> fallaHeladera.getHorario().isBefore(horario))
                .collect(Collectors.toList());
    }

    private List<ViandasPorColaborador> generarReporteViandasPorColaboradorIndividual(LocalDateTime horario){
        return viandasPorColaborador.stream()
                .filter(viandasPorColaborador -> viandasPorColaborador.getHorario().isBefore(horario))
                .collect(Collectors.toList());
    }

    private List<MovimientosHeladera> generarReporteMovimientosHeladeraIndividual(LocalDateTime horario){
        return movimientosPorHeladera.stream()
                .filter(movimientosHeladera -> movimientosHeladera.getHorario().isBefore(horario))
                .collect(Collectors.toList());
    }

    private void generarSolicitudReporteExcel(SolicitudReporteIndividual solicitudReporteIndividual) throws IOException {
        Path directoryPath = Paths.get("resources/reportes");
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaInicial = solicitudReporteIndividual.getFechaSolicitud().format(formatter);
        String fechaInicialEncabezado = fechaInicial.replace("/", "-");
        LocalDateTime primerDomingo = solicitudReporteIndividual.getFechaSolicitud().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        String fechaFinal = primerDomingo.format(formatter);
        String fechaFinalEncabezado = fechaFinal.replace("/", "-");
        String fileName = "";
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet;
        Row headerRow;
        int id;
        int rowNum;
        TipoReporte tipoReporte = TipoReporte.valueOf(solicitudReporteIndividual.getTipoSolicitud());
        switch (tipoReporte) {
            case FALLAS_HELADERA:
                fileName = "Fallas Heladera (" + fechaInicialEncabezado + " - " + fechaFinalEncabezado + ").xlsx";
                sheet = workbook.createSheet("Fallas Heladera (" + fechaInicialEncabezado + " - " + fechaFinalEncabezado + ")");

                headerRow = sheet.createRow(0);
                String[] columnHeadersFalla = {"ID", "Heladera", "Cantidad de fallas"};
                for (int i = 0; i < columnHeadersFalla.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columnHeadersFalla[i]);
                }

                id = 1;
                rowNum = 1;
                for (FallasHeladera falla : fallasPorHeladera) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(String.valueOf(id));
                    row.createCell(1).setCellValue(falla.getHeladera().getNombreHeladera());
                    row.createCell(2).setCellValue(String.valueOf(falla.getCantidadFallas()));
                    id++;
                }
                break;
            case MOVIMIENTOS_HELADERA: {
                fileName = "Movimientos Heladera (" + fechaInicialEncabezado + " - " + fechaFinalEncabezado + ").xlsx";
                sheet = workbook.createSheet("Movimientos Heladera (" + fechaInicialEncabezado + " - " + fechaFinalEncabezado + ")");

                headerRow = sheet.createRow(0);
                String[] columnHeadersMovimientos = {"ID", "Heladera", "Cantidad de viandas ingresadas", "Cantidad de viandas retiradas"};
                for (int i = 0; i < columnHeadersMovimientos.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columnHeadersMovimientos[i]);
                }

                rowNum = 1;
                id = 1;
                for (MovimientosHeladera movimiento : movimientosPorHeladera) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(String.valueOf(id));
                    row.createCell(1).setCellValue(movimiento.getHeladera().getNombreHeladera());
                    row.createCell(2).setCellValue(String.valueOf(movimiento.getCantViandasColocadas()));
                    row.createCell(3).setCellValue(String.valueOf(movimiento.getCantViandasRetiradas()));
                    id++;
                }
                break;
            }
            case VIANDAS_COLABORADOR: {
                fileName = "Viandas Por Colaborador (" + fechaInicialEncabezado + " - " + fechaFinalEncabezado + ").xlsx";
                sheet = workbook.createSheet("Viandas Por Colaborador (" + fechaInicialEncabezado + " - " + fechaFinalEncabezado + ")");

                headerRow = sheet.createRow(0);
                String[] columnHeadersViandas = {"ID", "Colaborador", "Cantidad de viandas donadas"};
                for (int i = 0; i < columnHeadersViandas.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columnHeadersViandas[i]);
                }

                id = 1;
                rowNum = 1;
                for (ViandasPorColaborador viandas : viandasPorColaborador) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(String.valueOf(id));
                    PersonaFisica persona = (PersonaFisica) viandas.getColaboradorHumano().getPersona();
                    row.createCell(1).setCellValue(persona.getNombre() + " " + persona.getApellido());
                    row.createCell(2).setCellValue(String.valueOf(viandas.getViandasDonadas()));
                    id++;
                }
                break;
            }
        }

        String filePath = directoryPath.resolve(fileName).toString();
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    private void convertirSolicitudExcelAPdf(String excelFilePath, String pdfFilePath, SolicitudReporteIndividual solicitudReporteIndividual) throws IOException {
        Workbook workbook = new XSSFWorkbook(excelFilePath);
        Sheet sheet = workbook.getSheetAt(0);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaInicial = solicitudReporteIndividual.getFechaSolicitud().format(formatter);
        LocalDateTime primerDomingo = solicitudReporteIndividual.getFechaSolicitud().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        String fechaFinal = primerDomingo.format(formatter);
        String mainTitle = "";
        TipoReporte tipoReporte = TipoReporte.valueOf(solicitudReporteIndividual.getTipoSolicitud());
        switch (tipoReporte) {
            case FALLAS_HELADERA:
                mainTitle = "Reporte de Fallas Heladera";
                break;
            case MOVIMIENTOS_HELADERA:
                mainTitle = "Reporte de Movimientos Heladera";
                break;
            case VIANDAS_COLABORADOR:
                mainTitle = "Reporte de Viandas Por Colaborador";
                break;
        }
        String dateTitle = "(" + fechaInicial + " - " + fechaFinal + ")";

        // Encabezado con logo y título
        ImageData logo = ImageDataFactory.create("src/main/resources/mapaInteractivoHeladeras/sistema/images/principal/logo2.png");
        Image logoImage = new Image(logo).scaleToFit(70, 70);

        Table headerTable = new Table(new float[]{1, 3});
        headerTable.setWidth(UnitValue.createPercentValue(100));

        com.itextpdf.layout.element.Cell combinedCell = new com.itextpdf.layout.element.Cell()
                .add(logoImage.setHorizontalAlignment(HorizontalAlignment.CENTER)) // Centrar el logo
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

    public void generarSolicitudReporteFisico(SolicitudReporteIndividual solicitudReporteIndividual) throws IOException {
        this.generarSolicitudReporteExcel(solicitudReporteIndividual);
        String pathArchivo = "resources/reportes/" + GeneradorDeReportes.getInstance().buscarFileNameExcelSolicitudReporte(solicitudReporteIndividual);
        this.convertirSolicitudExcelAPdf(pathArchivo, pathArchivo.split("\\.")[0] + ".pdf", solicitudReporteIndividual);
        this.borrarSolicitudExcel(pathArchivo);
    }

    private void borrarSolicitudExcel(String pathArchivo) throws IOException {
        Files.deleteIfExists(Paths.get(pathArchivo));
    }

    public String buscarFileNameExcelSolicitudReporte(SolicitudReporteIndividual solicitudReporteIndividual) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaInicial = solicitudReporteIndividual.getFechaSolicitud().format(formatter);
        String fechaInicialEncabezado = fechaInicial.replace("/", "-");
        LocalDateTime primerDomingo = solicitudReporteIndividual.getFechaSolicitud().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        String fechaFinal = primerDomingo.format(formatter);
        String fechaFinalEncabezado = fechaFinal.replace("/", "-");
        TipoReporte tipoReporte = TipoReporte.valueOf(solicitudReporteIndividual.getTipoSolicitud());
        String pathArchivo = "";
        switch (tipoReporte) {
            case FALLAS_HELADERA:
                pathArchivo = pathArchivo + "Fallas Heladera (" + fechaInicialEncabezado + " - " + fechaFinalEncabezado + ").xlsx";
                break;
            case MOVIMIENTOS_HELADERA:
                pathArchivo = pathArchivo + "Movimientos Heladera (" + fechaInicialEncabezado + " - " + fechaFinalEncabezado + ").xlsx";
                break;
            case VIANDAS_COLABORADOR:
                pathArchivo = pathArchivo + "Viandas Por Colaborador (" + fechaInicialEncabezado + " - " + fechaFinalEncabezado + ").xlsx";
                break;
        }
        return pathArchivo;
    }
}
