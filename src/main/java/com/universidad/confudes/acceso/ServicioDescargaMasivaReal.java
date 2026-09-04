package com.universidad.confudes.acceso;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioDescargaMasivaReal implements ServicioDescargaMasiva {
    
    @Override
    public List<byte[]> descargarTodos(String eventoId, Usuario usuario) {
        System.out.println("Iniciando descarga masiva para el evento: " + eventoId);
        // Simulación de empaquetado de archivos PDF
        return new ArrayList<>(); 
    }
}