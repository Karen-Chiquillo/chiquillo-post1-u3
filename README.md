# Post-contenido — Unidad 3: Patrones Estructurales en ConfUDES

## Descripción

Repositorio del post-contenido de la Unidad 3 de Patrones de Diseño de Software. Un único proyecto Spring Boot (`confudes-patrones-estructurales`) que resuelve cuatro necesidades reales del backend de ConfUDES, una plataforma de gestión de congresos académicos: registro de asistencia con un proveedor externo, emisión de certificados, mejoras opcionales sobre el certificado emitido y control de acceso a la descarga masiva.

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
 
Se aplicó el patrón **Decorator** (Decorador). El síntoma de diseño era la necesidad de añadir responsabilidades dinámicas y combinables (marca de agua, código QR de verificación, traducción al inglés) a un certificado ya emitido, en cualquier orden y en tiempo de ejecución, sin alterar su estructura base. Se estandarizó la operación detrás del contrato `ServicioCertificados`, permitiendo que los decoradores concretos (`MarcaDeAguaDecorator`, `CodigoQRDecorator`, `TraduccionDecorator`) envuelvan a `EmisionCertificadoFacade` o a otro decorador ya aplicado, apilando mejoras como capas sucesivas sin tocar el colaborador base de la Necesidad 2.
 
Se descartaron dos alternativas. La **herencia** produciría una explosión de subclases: con solo tres mejoras combinables ya existen ocho combinaciones posibles, lo que exigiría una clase por cada una (`CertificadoConQRYTraduccion`, `CertificadoConMarcaYQR`, etc.). Los **parámetros booleanos** en el método `emitir()` (`activarMarcaDeAgua`, `activarQR`, `activarTraduccion`) violarían el principio Open/Closed: cada mejora nueva obligaría a modificar la firma y la lógica del método base, en lugar de extenderlo sin tocarlo.
 
### Necesidad 4 — Control de acceso a la descarga masiva
 
Se aplicó el patrón **Proxy** (de protección). El síntoma de diseño era una operación costosa y sensible (la descarga masiva de certificados de un evento) sin control de rol, expuesta igual que cualquier otra operación de `ServicioCertificados`. `ProxyControlAcceso implements ServicioCertificados` actúa como intermediario: antes de delegar en la emisión real, consulta `ContextoUsuario.rolActual()` y, si el rol no es `ORGANIZADOR` ni `ADMIN`, lanza una `SecurityException` sin invocar la lógica costosa; si el rol es válido, delega normalmente en el colaborador real. El resto del sistema (incluida la emisión individual de la Necesidad 2) sigue inyectando `ServicioCertificados` sin saber que existe esta verificación adicional.
 
Se descartó **Decorator** por diferencia de intención, a pesar de la similitud estructural: ambos envuelven un objeto que implementa la misma interfaz. El Decorator siempre delega en el objeto real y añade capacidades al resultado (como en la Necesidad 3); el Proxy decide *si* delega o no, y puede evitar por completo la ejecución del objeto real. Usar Decorator aquí ejecutaría igual la operación costosa antes de decidir si el usuario tiene permiso, y usar Proxy para las mejoras de la Necesidad 3 no permitiría combinarlas libremente, porque un proxy no está pensado para apilar comportamiento sino para controlar el acceso.

### Reflexión — Composite y Flyweight
 
La agenda de cada congreso (tracks, sesiones y actividades) es una estructura árbol-parte-todo donde **Composite** encajaría naturalmente, tratando esos tres niveles bajo una interfaz común. Las credenciales QR, en cambio, no ameritan **Flyweight**: cada una contiene datos únicos e irrepetibles por participante (estado extrínseco), y no hay estado intrínseco compartible que justifique el patrón.

## Herramientas utilizadas

- Java 17, Spring Boot 3.2, Apache Maven, JUnit 5
- VS Code o IntelliJ IDEA, Git, GitHub

## Conclusiones
 
Este laboratorio mostró que la elección del patrón estructural depende del síntoma de diseño y no de la superficie del problema: Adapter y Facade, aunque ambos "envuelven" código existente, resuelven incompatibilidad de contratos y exceso de colaboradores respectivamente; Decorator y Proxy, aunque ambos envuelven un objeto detrás de la misma interfaz, se distinguen por su intención (añadir capacidades siempre delegando, frente a controlar si se delega o no). En los cuatro casos, la composición sobre interfaces estables permitió resolver el problema real sin modificar el código dado (SDK externo, contratos internos, controladores en producción y servicios ya usados por otros módulos), preservando el principio Open/Closed y evitando tanto la explosión de subclases como el acoplamiento excesivo entre colaboradores.