package com.universidad.confudes.certificados;

public class MarcaDeAguaDecorator extends MejoraCertificadoDecorator {
    
    public MarcaDeAguaDecorator(ServicioCertificados envoltorio) {
        super(envoltorio);
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] documentoBase = super.emitir(solicitud);
        return UtilidadesPDF.aplicarMarcaDeAgua(documentoBase, "ConfUDES Institucional");
    }
}