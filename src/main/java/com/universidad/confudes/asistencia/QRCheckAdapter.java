package com.universidad.confudes.asistencia;

import com.universidad.confudes.externo.qrcheck.QRCheckClient;
import com.universidad.confudes.externo.qrcheck.QRCheckRequest;
import com.universidad.confudes.externo.qrcheck.QRCheckResponse;
import org.springframework.stereotype.Service;

@Service 
public class QRCheckAdapter implements ServicioAsistencia {

    // Se instancia el cliente externo para la validación de accesos
    private final QRCheckClient qrClient = new QRCheckClient();

    @Override
    public ResultadoCheckIn registrarAsistencia(String eventoId, String participanteId, String credencialQR) {
        
        // 1. Se extraen solo los números del identificador
        long idEventoNum = Long.parseLong(eventoId.replaceAll("\\D+", "")); 
        
        // 2. Se envía la credencial a validar al sistema externo
        QRCheckRequest request = new QRCheckRequest(credencialQR, idEventoNum);
        QRCheckResponse response = qrClient.validar(request);

        // 3. Se adapta la respuesta al formato interno
        boolean exitoso = (response.getCodigoRespuesta() == 200);
        return new ResultadoCheckIn(exitoso, response.getDetalle());
    }
}