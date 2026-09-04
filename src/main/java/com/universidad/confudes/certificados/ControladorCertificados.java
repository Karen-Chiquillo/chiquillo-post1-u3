package com.universidad.confudes.certificados;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/certificados")
public class ControladorCertificados {

    private final ServicioCertificados servicio; // <-- Depende de la interfaz

    public ControladorCertificados(ServicioCertificados servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/{eventoId}/{participanteId}")
    public ResponseEntity<String> emitir(@PathVariable String eventoId, @PathVariable String participanteId,
                                         @RequestParam String nombre, @RequestParam String correoDestino) {
        
        SolicitudCertificado solicitud = new SolicitudCertificado(eventoId, participanteId, nombre, correoDestino);
        
        try {
            servicio.emitir(solicitud);
            return ResponseEntity.ok("Certificado emitido y enviado");
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("Asistencia insuficiente");
        }
    }
}