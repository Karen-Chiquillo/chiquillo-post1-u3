package com.universidad.confudes.certificados;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/certificados")
public class ControladorCertificados {

    // Se instancia la fachada para procesar las solicitudes de certificados
    private final EmisionCertificadoFacade facade;

    public ControladorCertificados(EmisionCertificadoFacade facade) {
        this.facade = facade;
    }

    @PostMapping("/{eventoId}/{participanteId}")
    public ResponseEntity<String> emitir(@PathVariable String eventoId, @PathVariable String participanteId,
                                         @RequestParam String nombre, @RequestParam String correoDestino) {
        
        boolean exito = facade.emitirCertificado(eventoId, participanteId, nombre, correoDestino);
        
        return exito ? ResponseEntity.ok("Certificado emitido y enviado")
                     : ResponseEntity.status(403).body("Asistencia insuficiente");
    }
}