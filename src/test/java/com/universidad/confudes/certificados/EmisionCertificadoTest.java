package com.universidad.confudes.certificados;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmisionCertificadoTest {
    
    @Test
    void ordenColaboradorOrquestaLasCuatroEtapasSinExcepcion() {
        var validador = new ValidadorAsistencia();
        var generador = new GeneradorCertificadoPDF();
        var firma = new FirmaDigitalService();
        var correo = new EnvioCorreoService();
        
        // <-- Tu Fachada orquestando todo
        EmisionCertificadoFacade colaborador = new EmisionCertificadoFacade(validador, generador, firma, correo);

        assertDoesNotThrow(() -> {
            colaborador.emitirCertificado("EVT-001", "PART-123", "Ana Rios", "ana@correo.com");
        });
    }

    @Test
    void controladorCertificadosSoloDependeDeUnColaborador() {
        var constructores = ControladorCertificados.class.getDeclaredConstructors();
        assertEquals(1, constructores.length);
        assertEquals(1, constructores[0].getParameterCount());
    }
}