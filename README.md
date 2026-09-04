# Post-contenido — Unidad 3: Patrones Estructurales en ConfUDES

## Descripción

Repositorio del post-contenido de la Unidad 3 de Patrones de Diseño de Software. Un único proyecto Spring Boot (`confudes-patrones-estructurales`) que resuelve cuatro necesidades reales del backend de ConfUDES, una plataforma de gestión de congresos académicos: registro de asistencia con un proveedor externo, emisión de certificados, mejoras opcionales sobre el certificado emitido y control de acceso a la descarga masiva. Esta entrega corresponde a la **Parte 1** (Necesidades 1 y 2); la Parte 2 se documentará en la siguiente entrega sobre este mismo repositorio.

## Cómo ejecutar

```
$ mvn clean package
$ mvn spring-boot:run
$ mvn test
```

## Decisiones de diseño

### Necesidad 1 — Registro de asistencia

Se aplicó el patrón **Adapter** (Adaptador de objetos). El síntoma de diseño es una incompatibilidad estricta de contratos con un único colaborador externo: `ControladorCheckIn` depende de `ServicioAsistencia`, contrato interno inmodificable, mientras que el proveedor expone `QRCheckClient` con tipos y nombres distintos. El adaptador (`QRCheckAdapter implements ServicioAsistencia`) traduce `eventoId` de `String` a `long`, normaliza `credencialQR` al payload con prefijo `"QR-"` que exige el proveedor, y mapea el `codigoRespuesta` (200/401) y el `detalle` de `QRCheckResponse` a un `ResultadoCheckIn(boolean exitoso, String mensaje)`, sin alterar ninguna de las clases dadas.

Se descartó **Facade** porque este patrón simplifica el acceso a un subsistema compuesto por *múltiples* clases, y aquí el cliente ya conoce un único colaborador (`ServicioAsistencia`); el problema no es reducir colaboradores sino traducir el contrato de una única API externa hacia el contrato que el sistema receptor ya espera. Facade no traduce contratos preexistentes; Adapter sí.

### Necesidad 2 — Emisión de certificados

Se aplicó el patrón **Facade** (Fachada). El síntoma de diseño es alto acoplamiento: `ControladorCertificados` conocía y orquestaba directamente cuatro servicios independientes (`ValidadorAsistencia`, `GeneradorCertificadoPDF`, `FirmaDigitalService`, `EnvioCorreoService`), quedando expuesto a cualquier cambio en su secuencia interna. `EmisionCertificadoFacade` encapsula las cuatro etapas (validar → generar PDF → firmar digitalmente gestionando la sesión → enviar correo) detrás de una sola operación. Tras la refactorización, `ControladorCertificados` pasa de 4 dependencias a una sola inyectada, y su método `emitir()` queda por debajo de 10 líneas de cuerpo, respetando el Principio de Responsabilidad Única.

Se descartó **Adapter** porque ninguno de los cuatro servicios tiene una interfaz incompatible: todos son directamente usables tal como están y siguen siendo consumidos sin cambios por otros módulos de ConfUDES. El problema es de acoplamiento y complejidad de orquestación, no de firmas incompatibles; no existe un *Target* preexistente al cual adaptar los cuatro servicios.

### Necesidad 3 — Mejoras opcionales del certificado

*(Se completará en la Parte 2: análisis de Decorator vs. herencia / parámetros booleanos).*

### Necesidad 4 — Control de acceso a la descarga masiva

*(Se completará en la Parte 2: análisis de Proxy de protección vs. Decorator).*


## Herramientas utilizadas

- Java 17, Spring Boot 3.2, Apache Maven, JUnit 5
- VS Code o IntelliJ IDEA, Git, GitHub

## Conclusiones

La Parte 1 mostró que la elección del patrón depende del síntoma exacto y no de la superficie del problema: ambas necesidades implicaban "envolver" código existente, pero solo una respondía a una incompatibilidad de contratos y la otra a un exceso de colaboradores. Adapter tradujo un único Adaptee hacia un Target ya fijado por el sistema; Facade centralizó una secuencia de pasos entre colaboradores sin incompatibilidad alguna entre ellos. Esa distinción de adaptar interfaces existentes frente a ocultar complejidad orquestando servicios fue el criterio para descartar en cada caso la alternativa más parecida pero conceptualmente incorrecta, preservando en ambas soluciones el principio Open/Closed sin modificar el código dado.