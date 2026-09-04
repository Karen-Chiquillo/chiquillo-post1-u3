package com.universidad.confudes.acceso;

import java.util.List;

public interface ServicioDescargaMasiva {
    List<byte[]> descargarTodos(String eventoId, Usuario usuario);
}