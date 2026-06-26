package persona.documentacion;

import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;

import javax.persistence.*;

@Entity
@Table(name = "documentacion")
@Getter
public class Documentacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "docu_id")
    private Long id;

    @Column(name = "docu_cuil")
    @Setter private String cuil;

    @Column(name = "docu_tipo_documentacion")
    @Enumerated(EnumType.STRING)
    private final TipoDocumentacion tipoDocumentacion;

    @Column(name = "docu_numero")
    private final String numero;

    public Documentacion(TipoDocumentacion tipoDocumentacion, String numero){
        this.tipoDocumentacion = tipoDocumentacion;
        this.numero = numero;
        ClaseCRUD.getInstance().add(this);
    }

    public Documentacion() {
        this.tipoDocumentacion = null;
        this.numero = null;
    }
}
