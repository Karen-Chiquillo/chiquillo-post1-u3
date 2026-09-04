package com.universidad.confudes.certificados;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MejorasCertificadoTest {
    
    @Test
    void aplicaMultiplesMejorasAlCertificado() {
        // 1. Se crea la base (el objeto original)
        ServicioCertificados base = new EmisionCertificadoFacade(
            new ValidadorAsistencia(), new GeneradorCertificadoPDF(),
            new FirmaDigitalService(), new EnvioCorreoService()
        );
        
        // 2. Se envuelve con las tres mejoras (Decorator)
        ServicioCertificados conMarca = new MarcaDeAguaDecorator(base);
        ServicioCertificados conQR = new CodigoQRDecorator(conMarca);
        ServicioCertificados conTraduccion = new TraduccionDecorator(conQR);
        
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana Rios", "ana@correo.com");
        
        // 3. Se ejecuta y se verifica que no lance errores
        assertDoesNotThrow(() -> {
            conTraduccion.emitir(solicitud);
        });
    }
}