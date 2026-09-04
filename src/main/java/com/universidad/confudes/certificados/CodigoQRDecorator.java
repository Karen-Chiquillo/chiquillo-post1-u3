package com.universidad.confudes.certificados;

public class CodigoQRDecorator extends MejoraCertificadoDecorator {
    
    public CodigoQRDecorator(ServicioCertificados envoltorio) {
        super(envoltorio);
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] documentoBase = super.emitir(solicitud);
        return UtilidadesPDF.insertarCodigoQR(documentoBase, "https://confudes.edu.co/validar/" + solicitud.getParticipanteId());
    }
}
