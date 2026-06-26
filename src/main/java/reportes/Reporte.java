package reportes;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "reporte")
@Getter
public abstract class Reporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repo_id")
    private int id;

    @Column(name = "repo_fecha_inicial")
    private LocalDate fechaInicial;

    @Column(name = "repo_fecha_final")
    private LocalDate fechaFinal;

    @Transient
    private int idReporte;  // ID único para cada reporte

    @Transient
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transient
    private String pathDirectorio = Paths.get("resources", "reportes").toString();

    @Column(name = "repo_path")
    @Setter private String pathArchivo = Paths.get("resources", "reportes").toString();

    public Reporte(){
        this.idReporte = generarNuevoId();
        this.fechaFinal = LocalDate.now();
        DayOfWeek diaDeLaSemana = fechaFinal.getDayOfWeek();
        // Calcular cuántos días hay que restar para llegar al lunes anterior por si piden el reporte en el medio de la semana
        int diasARestar = (diaDeLaSemana.getValue() >= DayOfWeek.MONDAY.getValue())
                ? diaDeLaSemana.getValue() - DayOfWeek.MONDAY.getValue()
                : 7 - (DayOfWeek.MONDAY.getValue() - diaDeLaSemana.getValue());
        this.fechaInicial = fechaFinal.minusDays(diasARestar);
    }

    // Método sincronizado para evitar problemas en entornos concurrentes
    private synchronized static int generarNuevoId() {
        return GeneradorDeReportes.getInstance().getContadorReportes();  // Incrementa el contador y devuelve el nuevo ID
    }

    public abstract void generarReporteExcel() throws IOException;
    public abstract void convertirExcelAPdf(String excelFilePath, String pdfFilePath) throws IOException;

    public void generarReporteFisico() throws IOException {
        this.generarReporteExcel();
        this.convertirExcelAPdf(pathArchivo, pathArchivo.split("\\.")[0] + ".pdf");
        this.borrarExcel();
    }

    private void borrarExcel() throws IOException {
        Files.deleteIfExists(Paths.get(pathArchivo));
    }

    public void generarNombreArchivo() {
        String fechaInicial = this.getFechaInicial().format(this.getFormatter());
        String fechaInicialEncabezado = fechaInicial.replace("/", "-");
        String fechaFinal = this.getFechaFinal().format(this.getFormatter());
        String fechaFinalEncabezado = fechaFinal.replace("/", "-");
        pathArchivo = Paths.get("resources", "reportes").toString();
        pathArchivo = pathArchivo.replace("\\", "/") + "/";
        switch (this.getClass().getSimpleName()) {
            case "ReporteFallasHeladera":
                pathArchivo = pathArchivo + "Fallas Heladera (" + fechaInicialEncabezado + " - " + fechaFinalEncabezado + ").pdf";
                break;
            case "ReporteMovimientosHeladera":
                pathArchivo = pathArchivo + "Movimientos Heladera (" + fechaInicialEncabezado + " - " + fechaFinalEncabezado + ").pdf";
                break;
            case "ReporteViandasPorColaborador":
                pathArchivo = pathArchivo + "Viandas Por Colaborador (" + fechaInicialEncabezado + " - " + fechaFinalEncabezado + ").pdf";
                break;
        }
    }
}

