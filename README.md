# Refactoring Service - Code Smells y Arquitectura

## Descripción

Repositorio correspondiente al post-contenido de la Unidad 11 de la asignatura Patrones de Diseño de Software.

El proyecto consiste en una aplicación Java gestionada con Maven y Spring Boot Starter, enfocada en el dominio de inventario y generación de reportes.

El objetivo principal es completar un ciclo de refactorización segura, dividido en dos etapas:

1. **Refactorización de Code Smells:** Implementación de pruebas de caracterización para proteger el comportamiento original del código y posterior eliminación de problemas de diseño a nivel de método y clase, principalmente *Long Method* y *Switch Statements*.
2. **Refactorización arquitectónica:** Diagnóstico y resolución de una dependencia cíclica entre paquetes mediante pruebas de arquitectura con ArchUnit y aplicación del **Principio de Inversión de Dependencias (DIP)**.

El resultado es una arquitectura con mayor cohesión, menor acoplamiento y una estructura más mantenible, conservando el comportamiento funcional original.

---

## Estructura del proyecto

La organización del código fuente y las pruebas se distribuye de la siguiente manera:

```text
src/main/java/com/universidad/refactoringu11/
├── inventario/
│   ├── TipoProducto.java
│   ├── Producto.java
│   ├── InventarioService.java
│   ├── ClasificadorProducto.java
│   ├── EtiquetadoProducto.java
│   └── NotificadorBajoStock.java
│
└── reportes/
    └── ReporteBajoStockService.java

src/test/java/com/universidad/refactoringu11/
├── ArquitecturaPaquetesTest.java
│
└── inventario/
    ├── InventarioServiceTest.java
    ├── ClasificadorProductoTest.java
    └── EtiquetadoProductoTest.java
```

### Descripción de los componentes principales

| **Componente** | **Responsabilidad** |
|:---|:---|
| `TipoProducto.java` | Enum que implementa polimorfismo mediante métodos específicos para cada tipo de producto. |
| `Producto.java` | Representación del objeto de dominio Producto. |
| `InventarioService.java` | Orquestador principal de las operaciones de inventario, refactorizado para delegar responsabilidades. |
| `ClasificadorProducto.java` | Encargado de la clasificación de productos. |
| `EtiquetadoProducto.java` | Gestiona la asignación de etiquetas según el tipo de producto. |
| `NotificadorBajoStock.java` | Interfaz de abstracción que permite aplicar el Principio de Inversión de Dependencias (DIP). |
| `ReporteBajoStockService.java` | Implementación concreta del servicio de notificación de bajo stock. |
| `ArquitecturaPaquetesTest.java` | Prueba de arquitectura implementada con ArchUnit para verificar las dependencias entre paquetes. |

---

## Ejecución de pruebas

Para ejecutar la suite completa de pruebas unitarias y de arquitectura, utiliza el siguiente comando:

### Ejecutar todas las pruebas

```bash
mvn clean test
```

Este comando realiza las siguientes verificaciones:

- Ejecuta las pruebas de caracterización y comportamiento de los componentes del inventario.
- Ejecuta las pruebas de arquitectura mediante ArchUnit.
- Verifica que las dependencias entre paquetes cumplan las reglas arquitectónicas establecidas.

**Resultado esperado:** 15 pruebas ejecutadas correctamente (14 pruebas de negocio y 1 prueba de arquitectura).

---

## Evidencia de arquitectura (Acoplamiento)

La siguiente tabla presenta la evolución de la arquitectura antes y después de aplicar el Principio de Inversión de Dependencias (DIP).

| **Estado** | **Diagrama de dependencias** | **Resultado de ArchUnit** |
|:---|:---:|:---:|
| **Antes (Dependencia cíclica)** | `inventario ↔ reportes` | ❌ `BUILD FAILURE` |
| **Después (Aplicación del DIP)** | `inventario ← reportes` | ✅ `BUILD SUCCESS` |

### Interpretación de los resultados

- **Antes:** Existía una dependencia bidireccional entre los paquetes `inventario` y `reportes`. Esta situación generaba un acoplamiento innecesario y dificultaba el mantenimiento de la arquitectura.
- **Después:** Se introdujo la interfaz `NotificadorBajoStock` en el paquete `inventario`, permitiendo que `reportes` implemente dicha abstracción. De esta manera, la dependencia se vuelve unidireccional y se elimina el ciclo arquitectónico.

---

## Comparación de diseño (Antes / Después)

La siguiente comparación resume los cambios realizados durante el proceso de refactorización.

| **Elemento** | **Antes (Diseño inicial)** | **Después (Refactorización)** | **Mejora obtenida** |
|:---|:---|:---|:---|
| `InventarioService` | *Long Method* de más de 35 líneas, mezclando validaciones, gestión de stock y notificaciones. | Orquestador limpio de menos de 10 líneas, delegando responsabilidades en métodos específicos. | Mayor cohesión y cumplimiento del principio de responsabilidad única (SRP). |
| `Clasificador` y `Etiquetado` | *Switch Statements* duplicados para evaluar los diferentes valores de `TipoProducto`. | Polimorfismo implementado directamente en el tipo de producto. | Eliminación de duplicación estructural y simplificación del código. |
| Acoplamiento de paquetes | Dependencia cíclica: `inventario` importaba `reportes` y viceversa. | Dependencia unidireccional de `reportes` hacia las abstracciones definidas en `inventario`. | Aplicación del Principio de Inversión de Dependencias (DIP). |
| Pruebas de caracterización | 14 pruebas implementadas para proteger el comportamiento original. | Las 14 pruebas continúan pasando sin modificar ninguna aserción. | Garantía de que la refactorización conserva el comportamiento funcional. |

---

## Decisiones de diseño

### 1. Refactorización de Code Smells (Parte 1)

Durante la primera etapa se evaluaron diferentes alternativas para eliminar los problemas de diseño identificados sin introducir complejidad innecesaria.

| **Decisión** | **Elección** | **Justificación** |
|:---|:---|:---|
| **Descomposición del cálculo de reorden** | Método privado | La fórmula matemática para calcular la cantidad a reordenar es una operación de una sola línea y altamente específica de esta transacción. Extraerla a una clase independiente (`CalculadoraReorden`) introduciría *Speculative Generality*, es decir, complejidad innecesaria para una lógica que actualmente no requiere reutilización ni cambios independientes. |
| **Mecanismo de polimorfismo** | Enum con métodos abstractos | El conjunto de tipos de producto (`LIBRO`, `ELECTRONICO`, etc.) es cerrado y estable. Implementar el comportamiento directamente en el enum evita una proliferación innecesaria de clases, como ocurriría al utilizar un patrón Strategy tradicional con una interfaz y múltiples implementaciones concretas. Además, los cálculos no requieren dependencias externas ni repositorios inyectados. |

### 2. Diseño arquitectónico y DIP (Parte 2)

En la segunda etapa se abordó el problema de acoplamiento entre paquetes mediante la inversión de dependencias.

| **Decisión** | **Elección** | **Justificación** |
|:---|:---|:---|
| **Ubicación de la abstracción** | Paquete `inventario` | Siguiendo los principios de arquitectura hexagonal y el concepto de puertos, el dominio de negocio (`inventario`) representa el nivel de mayor abstracción y no debe depender de detalles de infraestructura o notificación (`reportes`). Al ubicar la interfaz en el dominio, el módulo de reportes pasa a depender de dicha abstracción, eliminando la dependencia cíclica. |
| **Granularidad de notificación** | Individual por evento (`notificar(Producto)`) | Debido a que el sistema trabaja con movimientos individuales de inventario en tiempo real, es necesario generar una notificación inmediata cuando un producto alcanza un nivel crítico de stock. Una notificación por lotes (`List<Producto>`) sería más apropiada para procesos programados, como un *cron job* nocturno, y no para operaciones bajo demanda. |

---

## Herramientas y tecnologías utilizadas

Las siguientes herramientas fueron utilizadas para el desarrollo, refactorización y validación arquitectónica del proyecto.

| **Herramienta / Tecnología** | **Uso** |
|:---|:---|
| Java 17 | Lenguaje de programación. |
| Apache Maven 3.9 | Gestión de dependencias y construcción del proyecto. |
| JUnit 5 | Implementación y ejecución de pruebas unitarias y de caracterización. |
| ArchUnit | Validación automatizada de reglas y dependencias arquitectónicas. |
| Spring Boot Starter | Soporte para la configuración e inyección de dependencias. |
| Visual Studio Code | Entorno de desarrollo. |
| Git | Control de versiones. |
| GitHub | Alojamiento y gestión del repositorio. |

---

## Conclusiones

Este laboratorio permitió demostrar que un código puede compilar y funcionar correctamente y, aun así, presentar problemas importantes de diseño y arquitectura.

La implementación de **pruebas de caracterización** antes de modificar el código proporcionó una red de seguridad que permitió descomponer métodos extensos y aplicar polimorfismo sin alterar el comportamiento funcional existente.

Por otra parte, herramientas como **ArchUnit** demostraron su utilidad para identificar y prevenir problemas arquitectónicos a nivel de paquetes, especialmente las dependencias cíclicas que incrementan el acoplamiento entre módulos.

Finalmente, la aplicación del **Principio de Inversión de Dependencias (DIP)** permitió resolver el problema de acoplamiento mediante la introducción de abstracciones en el dominio, logrando una arquitectura más limpia, mantenible y verificable mediante pruebas automatizadas.