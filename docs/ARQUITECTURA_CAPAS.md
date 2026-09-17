# Documentación de Arquitectura por Capas - SAUAP

**Proyecto**: Sistema de Asignación de Unidades de Aprendizaje a los Profesores (SAUAP)
**Materia**: Desarrollo de Software
**Institución**: Universidad Autónoma de Baja California (UABC)
**Período**: 2026-2

---

## Índice

1. [Introducción](#introducción)
2. [¿Qué es la arquitectura por capas?](#qué-es-la-arquitectura-por-capas)
3. [Fundamentos teóricos y fuentes](#fundamentos-teóricos-y-fuentes)
4. [Capas implementadas en SAUAP](#capas-implementadas-en-sauap)
5. [Flujo de una operación](#flujo-de-una-operación)
6. [Patrones de diseño aplicados](#patrones-de-diseño-aplicados)
7. [Tecnologías utilizadas](#tecnologías-utilizadas)
8. [Referencias bibliográficas](#referencias-bibliográficas)

---

## 1. Introducción

El sistema **SAUAP** fue desarrollado siguiendo el patrón de **arquitectura por capas** 
(también conocido como **N-Tier Architecture** o **Layered Architecture**), un modelo ampliamente utilizado
en el desarrollo de software empresarial.

**Objetivo**: separar las responsabilidades del sistema en capas independientes, facilitando:
- **Mantenibilidad**: cada capa se puede modificar sin afectar a las demás
- **Escalabilidad**: se pueden agregar nuevas funcionalidades sin reescribir el código existente
- **Testabilidad**: cada capa se puede probar de forma independiente
- **Reutilización**: componentes genéricos (ej: `AbstractDAO<T>`) se usan en múltiples entidades

---

## 2. ¿Qué es la arquitectura por capas?

### 2.1 Definición

La **arquitectura por capas** es un patrón de diseño que organiza el sistema en **niveles jerárquicos**, donde cada capa:

- Tiene una **responsabilidad específica**
- Se comunica **solo con la capa inmediatamente inferior**
- **Expone** una interfaz bien definida a la capa superior
- **Oculta** los detalles de implementación internos

### 2.2 Capas típicas
_________________________________________________________________________
|             Capa               |            Responsabilidad            |
|              ---               |                 ---                   |
| **Vista (Presentation)**       | Interfaz de usuario, captura de datos |
| **Negocio (Business Logic)**   | Reglas de negocio, validaciones       |
| **Persistencia (Data Access)** | Acceso a base de datos                |
| **Entidad (Domain/Model)**     | Objetos del dominio                   |
| **Base de datos (Database)**   | Almacenamiento físico                 |
__________________________________________________________________________

### 2.3 Fuente teórica

**Referencias principales**:
- **Martin Fowler**, *Patterns of Enterprise Application Architecture* (2002) — Capítulo 1: "Layering"
- **Eric Evans**, *Domain-Driven Design: Tackling Complexity in the Heart of Software* (2003)
- **Oracle**, *Core J2EE Patterns: Best Practices and Design Strategies* (2003)
- **Microsoft**, *Microsoft Application Architecture Guide, 2nd Edition* (2009)

> *"Layering is one of the most common techniques that software designers use to break apart a complicated software system."*
> — **Martin Fowler**, PoEAA

---

## 3. Fundamentos teóricos y fuentes

### 3.1 Separación de responsabilidades (SoC)

**Principio**: *Separation of Concerns* (SoC).

**Definición**: cada módulo del sistema debe ocuparse de **una sola responsabilidad**.

**Fuente**:
- **Edsger W. Dijkstra**, *On the Role of Scientific Thought* (1974) — acuñó el término
- **Robert C. Martin**, *Clean Architecture* (2017)

### 3.2 Principios SOLID

Aplicamos los siguientes principios SOLID en la arquitectura:
______________________________________________________________________________________________
|          Principio            |                  Aplicación en SAUAP                        |
|           -------             |                         ------                              |
| **S** — Single Responsibility | Cada clase tiene una responsabilidad (DAO solo accede a BD) |
| **O** — Open/Closed           | `AbstractDAO<T>` permite extender sin modificar             |
| **L** — Liskov Substitution   | Los DAOs específicos sustituyen a `AbstractDAO<T>`          |
| **I** — Interface Segregation | Los Facades exponen solo los métodos necesarios             |
| **D** — Dependency Inversion  | Los beans dependen de abstracciones (Facades)               |
_______________________________________________________________________________________________
**Fuente**:
- **Robert C. Martin**, *Agile Software Development: Principles, Patterns, and Practices* (2002)
- **Robert C. Martin**, *Clean Architecture* (2017)

### 3.3 Patrón MVC y su relación

El sistema usa **JSF (JavaServer Faces)**, que implementa el patrón **MVC**:

| Componente MVC  |          En SAUAP        |
|        ---      |             ---          |
| **Modelo**      | Entidades JPA + Facades  |
| **Vista**       | Archivos XHTML           |
| **Controlador** | Managed Beans (`@Named`) |

**Fuente**:
- **Trygve Reenskaug**, *MVC: XEROX PARC 1978-79* — documento original
- **Oracle**, *JavaServer Faces Technology Documentation*

---

## 4. Capas implementadas en SAUAP

### 4.1 Estructura de módulos Maven

```
SAUAP/
├── entidad/         → Capa de Entidades (JPA)
├── persistencia/    → Capa de Persistencia (DAOs)
├── negocio/         → Capa de Negocio (Facades + Delegates)
└── vista/           → Capa de Presentación (JSF + PrimeFaces)
```

**Fuente**:
- **Apache Maven**, *Guide to Working with Multiple Modules*
- **Sonatype**, *Maven: The Complete Reference*

### 4.2 Capa de Entidad (`entidad/`)

**Responsabilidad**: representar los objetos del dominio.

**Tecnología**: JPA (Jakarta Persistence API) + Hibernate.

**Clases**:
- `Profesor`, `UnidadAprendizaje`, `Asignacion`, `Alumno`, `Usuario`

**Ejemplo**:
```java
@Entity
@Table(name = "profesor")
public class Profesor implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
    
    // getters y setters
}
```

**Fuente**:
- **Jakarta Persistence**, *JPA 3.1 Specification*
- **Hibernate ORM**, *User Guide 6.4*

### 4.3 Capa de Persistencia (`persistencia/`)

**Responsabilidad**: acceso a la base de datos.

**Componentes**:
- `AbstractDAO<T>`: CRUD genérico (patrón Template Method)
- `ProfesorDAO`, `UnidadAprendizajeDAO`, `AsignacionDAO`, `UsuarioDAO`
- `ServiceLocator`: provee instancias de los DAOs (patrón Service Locator)
- `HibernateUtil`: singleton del `EntityManagerFactory`

**Ejemplo**:
```java
public class ProfesorDAO extends AbstractDAO<Profesor> {
    public ProfesorDAO() {
        super(Profesor.class);
    }
    
    public Profesor buscarPorRFC(String rfc) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Profesor> q = em.createQuery(
                "SELECT p FROM Profesor p WHERE p.rfc = :rfc", Profesor.class);
            q.setParameter("rfc", rfc);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
```

**Fuente**:
- **Martin Fowler**, *Patterns of Enterprise Application Architecture* (2002) — Capítulo 10: "Data Source Architectural Patterns" (Table Data Gateway, Row Data Gateway, Active Record, Data Mapper)
- **Oracle**, *Core J2EE Patterns* — Data Access Object
- **Sun Microsystems**, *DAO Pattern* (2002)

### 4.4 Capa de Negocio (`negocio/`)

**Responsabilidad**: reglas de negocio y validaciones.

**Subcapas**:

#### a) Facades (fachadas)
**Patrón**: Facade (GoF).

**Responsabilidad**: exponer una interfaz simplificada a la capa de presentación.

**Ejemplo**:
```java
public class FacadeProfesor {
    private final DelegateProfesor delegate;
    
    public Profesor altaProfesor(Profesor p) {
        validarProfesor(p);
        Profesor existente = delegate.buscarPorRFC(p.getRfc());
        if (existente != null) {
            throw new ValidacionException("Ya existe un profesor con ese RFC.");
        }
        return delegate.altaProfesor(p);
    }
}
```

**Fuente**:
- **Erich Gamma et al.**, *Design Patterns: Elements of Reusable Object-Oriented Software* (1994) — Facade Pattern

#### b) Delegates (delegados)
**Patrón**: Business Delegate.

**Responsabilidad**: desacoplar la capa de presentación de la capa de persistencia.

**Fuente**:
- **Oracle**, *Core J2EE Patterns* — Business Delegate

#### c) Validators (validadores)
**Responsabilidad**: encapsular reglas de validación específicas.

**Ejemplos**:
- `RFCValidator`: valida formato del RFC mexicano
- `HorasValidator`: valida rangos de 0 a 4
- `TraslapeValidator`: detecta cruces de horarios

**Fuente**:
- **Martin Fowler**, *PoEAA* — Validation patterns
- **Alura**, *Validaciones en Java*

### 4.5 Capa de Vista (`vista/`)

**Responsabilidad**: interfaz de usuario.

**Tecnología**:
- JSF (Jakarta Server Faces) 4.0
- PrimeFaces 14.0
- XHTML + CSS
- Managed Beans (`@Named`)

**Componentes**:
- `LoginBeanUI`: login
- `DashboardBean`: control de vistas
- `SessionBean`: sesión del usuario
- `AsignacionBean`: lógica del módulo de asignación

**Fuente**:
- **Jakarta Server Faces**, *JSF 4.0 Specification*
- **PrimeFaces**, *PrimeFaces 14 User Guide*
- **Bauke Scholtz & Arjan Tijms**, *The Definitive Guide to JSF in Java EE 8* (2018)

---

## 5. Flujo de una operación

### Ejemplo: Guardar una asignación

```
[VISTA]              [NEGOCIO]              [PERSISTENCIA]         [BD]
   |                     |                        |                  |
   |-- click guardar --->|                        |                  |
   |                     |                        |                  |
   |                     |-- FacadeAsignacion --->|                  |
   |                     |     .altaAsignacion()  |                  |
   |                     |                        |                  |
   |                     |    [validarTraslape]   |                  |
   |                     |         |              |                  |
   |                     |         v              |                  |
   |                     |     TraslapeValidator  |                  |
   |                     |         |              |                  |
   |                     |         v              |                  |
   |                     |     DelegateAsignacion |                  |
   |                     |         |              |                  |
   |                     |         v              |                  |
   |                     |     AsignacionDAO -----|-- SELECT/INSERT->|
   |                     |                        |                  |
   |<-- OK o Excepción --|                        |                  |
```

**Fuente**:
- **Martin Fowler**, *PoEAA* — Capítulo 1: "Layering"
- **Rod Johnson**, *Expert One-on-One J2EE Design and Development* (2002)

---

## 6. Patrones de diseño aplicados

|               Patrón               |    Categoría   |             Dónde se usa          |         Fuente      |
|                  ---               |       ---      |                ---                |           ---       |
| **DAO**                            | Persistencia   | `ProfesorDAO`, etc.               | Sun, *Core J2EE*    |
| **Abstract DAO / Template Method** | Comportamiento | `AbstractDAO<T>`                  | GoF (1994)          |
| **Singleton**                      | Creacional     | `HibernateUtil`, `ServiceLocator` | GoF (1994)          |
| **Facade**                         | Estructural    | `FacadeProfesor`, etc.            | GoF (1994)          |
| **Business Delegate**              | Arquitectónico | `DelegateProfesor`, etc.          | Oracle, *Core J2EE* |
| **Service Locator**                | Arquitectónico | `ServiceLocator`                  | Oracle, *Core J2EE* |
| **MVC**                            | Arquitectónico | JSF + Beans                       | Reenskaug (1979)    |
| **DTO**                            | Arquitectónico | Entidades JPA                     | Fowler, *PoEAA*     |

### 6.1 Referencias de patrones

**GoF (Gang of Four)**:
> **Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides**, *Design Patterns: Elements of Reusable Object-Oriented Software* (1994).

**Core J2EE Patterns**:
> **Deepak Alur, John Crupi, Dan Malks**, *Core J2EE Patterns: Best Practices and Design Strategies* (2003).

**PoEAA**:
> **Martin Fowler**, *Patterns of Enterprise Application Architecture* (2002).

---

## 7. Tecnologías utilizadas
 
|        Tecnología       | Versión  |        Propósito        |        Fuente      |
|            ---          |    ---   |             ---         |         ---        |
| **Java**                | 21 (LTS) | Lenguaje                | Oracle             |
| **Maven**               | 3.9+     | Gestión de dependencias | Apache             |
| **Hibernate ORM**       | 6.6.11   | ORM                     | Red Hat            |
| **Jakarta Persistence** | 3.1      | API JPA                 | Eclipse Foundation |
| **HikariCP**            | 5.0.1    | Pool de conexiones      | Brett Wooldridge   |
| **MySQL Connector**     | 9.2.0    | Driver JDBC             | Oracle             |
| **JSF (Mojarra)**       | 4.0.1    | Framework web           | Eclipse            |
| **PrimeFaces**          | 14.0     | Componentes UI          | PrimeTek           |
| **Weld**                | 5.1.1    | CDI                     | Red Hat            |
| **Tomcat**              | 11.0.8   | Servidor                | Apache             |
| **MySQL**               | 9.4      | Base de datos           | Oracle             |

---

## 8. Referencias bibliográficas

### Libros

1. **Martin Fowler**, *Patterns of Enterprise Application Architecture*. Addison-Wesley, 2002. ISBN: 978-0321127426.

2. **Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides**, *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley, 1994. ISBN: 978-0201633610.

3. **Robert C. Martin**, *Clean Architecture: A Craftsman's Guide to Software Structure and Design*. Prentice Hall, 2017. ISBN: 978-0134494166.

4. **Robert C. Martin**, *Agile Software Development: Principles, Patterns, and Practices*. Prentice Hall, 2002. ISBN: 978-0135974445.

5. **Eric Evans**, *Domain-Driven Design: Tackling Complexity in the Heart of Software*. Addison-Wesley, 2003. ISBN: 978-0321125217.

6. **Deepak Alur, John Crupi, Dan Malks**, *Core J2EE Patterns: Best Practices and Design Strategies*, 2nd Edition. Prentice Hall, 2003. ISBN: 978-0131422469.

7. **Bauke Scholtz & Arjan Tijms**, *The Definitive Guide to JSF in Java EE 8*. Apress, 2018. ISBN: 978-1484233863.

8. **Rod Johnson**, *Expert One-on-One J2EE Design and Development*. Wrox, 2002. ISBN: 978-0764543852.

### Documentación oficial

9. **Oracle**, *JavaServer Faces Technology Documentation*. https://jakarta.ee/specifications/faces/

10. **Red Hat**, *Hibernate ORM 6.4 User Guide*. https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/Hibernate_User_Guide.html

11. **Eclipse Foundation**, *Jakarta Persistence 3.1 Specification*. https://jakarta.ee/specifications/persistence/3.1/

12. **PrimeTek**, *PrimeFaces 14 User Guide*. https://primefaces.org/showcase/

13. **Apache Software Foundation**, *Apache Maven Guide*. https://maven.apache.org/guides/

14. **Microsoft**, *Microsoft Application Architecture Guide*, 2nd Edition. https://docs.microsoft.com/en-us/previous-versions/msp-n-p/ff650706(v=pandp.10)

### Artículos y tutoriales

15. **Martin Fowler**, *Presentation Model*. https://martinfowler.com/eaaDev/PresentationModel.html

16. **Martin Fowler**, *Inversion of Control Containers and the Dependency Injection pattern*. https://martinfowler.com/articles/injection.html

17. **Baeldung**, *The DAO Pattern in Java*. https://www.baeldung.com/java-dao-pattern

18. **Baeldung**, *Guide to the Business Delegate Pattern*. https://www.baeldung.com/java-business-delegate-pattern

---

## 📝 Notas finales

### Buenas prácticas aplicadas

- ✅ **Separación de responsabilidades** (SoC)
- ✅ **Arquitectura en capas** bien definida
- ✅ **Patrones de diseño** reconocidos
- ✅ **Documentación** con referencias
- ✅ **Control de versiones** con Git
- ✅ **Pruebas unitarias** por capa

### Decisiones de diseño

1. **¿Por qué Maven multi-módulo?** Porque separa físicamente las capas y evita dependencias cíclicas.
2. **¿Por qué JPA/Hibernate?** Para no escribir SQL manual y facilitar el mapeo objeto-relacional.
3. **¿Por qué Facade + Delegate?** Para aislar la vista de la persistencia y centralizar validaciones.
4. **¿Por qué Singleton en HibernateUtil?** Porque crear el `EntityManagerFactory` es costoso.

### Contribuciones del equipo

|     Integrante  |   Módulo   |
|        ---      |     ---    |
| Antonio Zamora  | Asignación |
| Arturo Maldonado| Profesores |
| Juan  Rojo Lara | Materias   |
| Karen Chavira   | Consultas  |
| Ricardo Molina  | Consultas  |

---

**Última actualización**: Septiembre 2026
**Versión**: 1.0
