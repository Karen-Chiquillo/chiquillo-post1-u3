package com.universidad.confudes.acceso;

import java.util.List; 

public class ProxyDescargaMasiva implements ServicioDescargaMasiva {
    
    private final ServicioDescargaMasiva servicioReal;

    public ProxyDescargaMasiva(ServicioDescargaMasiva servicioReal) {
        this.servicioReal = servicioReal;
    }

    @Override
    public List<byte[]> descargarTodos(String eventoId, Usuario usuario) {
        String rol = usuario.getRol().toUpperCase();
        
        // Verifica si el rol está permitido
        if (rol.equals("ORGANIZADOR") || rol.equals("ADMIN")) {
            System.out.println("Proxy: Permiso concedido al usuario " + usuario.getNombre());
            return servicioReal.descargarTodos(eventoId, usuario);
        }
        
        // Si no tiene permisos, lanza una excepción
        System.out.println("Proxy: Acceso DENEGADO al usuario " + usuario.getNombre());
        throw new SecurityException("Acceso denegado: El usuario no tiene permisos para la descarga masiva");
    }
}