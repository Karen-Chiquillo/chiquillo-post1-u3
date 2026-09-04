package com.universidad.confudes.certificados;

public abstract class MejoraCertificadoDecorator implements ServicioCertificados {
    protected final ServicioCertificados envoltorio;

    public MejoraCertificadoDecorator(ServicioCertificados envoltorio) {
        this.envoltorio = envoltorio;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        // Delega la emisión al objeto envuelto
        return envoltorio.emitir(solicitud); 
    }
}