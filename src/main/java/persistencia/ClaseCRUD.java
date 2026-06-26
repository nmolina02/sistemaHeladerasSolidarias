package persistencia;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static persistencia.LogToFile.logData;

@Getter
@Setter
public class ClaseCRUD {
    private static ClaseCRUD instancia = null;
    private List<Object> objectList = new ArrayList<>();
    private List<Object> objectListDelete = new ArrayList<>();

    private ClaseCRUD() {}

    public static ClaseCRUD getInstance() {
        if(instancia == null)
            instancia = new ClaseCRUD();
        return instancia;
    }

    public void add(Object object) {
        objectList.add(object);
        logData(object);
    }

    public void remove(Object object) {
        objectListDelete.add(object);
    }

    public void create(Object object, EntityManager em) {
        em.persist(object);
    }

    public void delete(Object object, EntityManager em) {
        em.remove(object);
    }
}
