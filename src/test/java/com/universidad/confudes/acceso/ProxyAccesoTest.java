package com.universidad.confudes.acceso;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProxyAccesoTest {
    
    @Test
    void permiteDescargaAOrganizador() {
        ServicioDescargaMasiva real = new ServicioDescargaMasivaReal();
        ServicioDescargaMasiva proxy = new ProxyDescargaMasiva(real); // Se instancia el vigilante
        
        Usuario organizador = new Usuario("Maria", "ORGANIZADOR");
        
        // Se espera que pase sin problemas
        assertDoesNotThrow(() -> {
            proxy.descargarTodos("EVT-001", organizador);
        });
    }
    
    @Test
    void deniegaDescargaAAsistente() {
        ServicioDescargaMasiva real = new ServicioDescargaMasivaReal();
        ServicioDescargaMasiva proxy = new ProxyDescargaMasiva(real); // Se instancia el vigilante
        
        Usuario asistente = new Usuario("Juan", "ASISTENTE");
        
        // Se verifica que se lance la excepción de seguridad
        assertThrows(SecurityException.class, () -> {
            proxy.descargarTodos("EVT-001", asistente);
        });
    }
}