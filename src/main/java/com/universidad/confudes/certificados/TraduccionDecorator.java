package com.universidad.confudes.certificados;

public class TraduccionDecorator extends MejoraCertificadoDecorator {
    
    public TraduccionDecorator(ServicioCertificados envoltorio) {
        super(envoltorio);
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] documentoBase = super.emitir(solicitud);
        return UtilidadesPDF.traducirAIngles(documentoBase);
    }
}
