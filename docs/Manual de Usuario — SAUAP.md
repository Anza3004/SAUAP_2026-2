# Manual de Usuario — SAUAP

**Sistema de Asignación de Unidades de Aprendizaje a los Profesores**
Universidad Autónoma de Baja California · Desarrollo de Software · 2026-2

> Este manual explica cómo usar el sistema paso a paso. Está basado en el comportamiento
> de la versión que se encuentra en la rama `integracion`. Las líneas marcadas con 📷 son
> lugares recomendados para insertar una captura de pantalla.

---

## Índice

1. [¿Qué es SAUAP y para qué sirve?](#1-qué-es-sauap-y-para-qué-sirve)
2. [Conceptos básicos](#2-conceptos-básicos)
3. [Iniciar sesión](#3-iniciar-sesión)
4. [Pantalla principal](#4-pantalla-principal)
5. [Profesores](#5-profesores)
6. [Materias](#6-materias-unidades-de-aprendizaje)
7. [Asignaciones](#7-asignaciones)
8. [Consultas](#8-consultas-buscar-modificar-y-eliminar-asignaciones)
9. [Cerrar sesión](#9-cerrar-sesión)
10. [Mensajes del sistema y qué hacer](#10-mensajes-del-sistema-y-qué-hacer)
11. [Preguntas frecuentes](#11-preguntas-frecuentes)

---

## 1. ¿Qué es SAUAP y para qué sirve?

SAUAP permite a un usuario autorizado:

* Registrar **profesores**.
* Registrar **materias** (unidades de aprendizaje) con sus horas de clase, taller y laboratorio.
* **Asignar** una materia a un profesor eligiendo su horario semanal en una cuadrícula visual.
* **Consultar** las asignaciones por profesor o por materia, **cambiar** el horario de un bloque o **eliminar** una asignación.

El sistema evita automáticamente que un mismo profesor tenga **dos clases al mismo tiempo**.

---

## 2. Conceptos básicos

| Término | Significado |
|---|---|
| **Unidad de aprendizaje / Materia** | Una asignatura con sus horas semanales: **horas de clase**, **horas de taller** y **horas de laboratorio** (de 0 a 4 cada una) |
| **Asignación** | La relación "este profesor imparte esta materia en este horario" |
| **Bloque / horario** | Cada tramo continuo de tiempo de una asignación (por ejemplo: lunes de 08:00 a 10:00) |
| **Tipo** | Cada bloque es de tipo **Clase (C)**, **Taller (T)** o **Laboratorio (L)** |
| **Grupo** | Código que identifica a todos los bloques que pertenecen a la **misma asignación** |
| **Horas requeridas** | Suma de las horas de clase, taller y laboratorio de la materia. Se deben cubrir **todas** al asignar |

**Horario disponible:** lunes a viernes, de **07:00 a 21:00**, en bloques de **1 hora**.

---

## 3. Iniciar sesión

1. Abre el sistema en tu navegador. Se muestra la pantalla **Iniciar Sesión**.
2. Escribe tu correo en el campo **Usuario**.
3. Escribe tu **Contraseña**.
4. Pulsa **Iniciar sesión**.

📷 *Captura: pantalla de inicio de sesión.*

* Si los datos son correctos, entras a la pantalla principal.
* Si son incorrectos, aparece **"Credenciales incorrectas — Verifique su correo y contraseña"**. Revisa los datos e inténtalo de nuevo.

> Por seguridad, la sesión se cierra automáticamente tras **30 minutos sin actividad**.

---

## 4. Pantalla principal

📷 *Captura: pantalla principal con el menú lateral.*

* **Menú lateral (izquierda):**
  * **Inicio** — pantalla de bienvenida con el logotipo.
  * **Profesores** — alta y lista de profesores.
  * **Materias** — alta y lista de materias.
  * **Asignaciones** — asignar una materia a un profesor.
  * **Consultas** — buscar, modificar o eliminar asignaciones.
  * **Cerrar sesión** — al final del menú.
* **Barra superior:** muestra el correo del usuario con el que iniciaste sesión.

La sección en la que estás aparece resaltada en el menú.

---

## 5. Profesores

Entra desde **Profesores** en el menú.

### 5.1 Registrar un profesor

1. Llena los cuatro campos:
   * **Nombre**
   * **Apellido paterno**
   * **Apellido materno**
   * **RFC**
2. Pulsa **Guardar profesor**.

📷 *Captura: formulario "Alta de Profesor".*

**Reglas:**

* Todos los campos son obligatorios.
* Nombre y apellidos: máximo **50 caracteres**.
* El **RFC** debe tener el formato mexicano: **3 o 4 letras + 6 dígitos + 3 caracteres** (letras o números). Ejemplo: `PEGJ850101AB3`.
* No puede haber **dos profesores con el mismo RFC**.

Si todo es correcto, aparece el aviso **"Profesor creado exitosamente"**, el formulario se limpia y el profesor aparece en la tabla.

### 5.2 Lista de profesores

Debajo del formulario está **Profesores registrados**, con nombre, apellidos y RFC. Se ordena por apellido paterno y luego materno.

### 5.3 Eliminar un profesor

1. En la fila del profesor, pulsa **Eliminar**.
2. Confirma en el cuadro **"¿Seguro que quieres eliminar a …?"**.

**Importante:** si el profesor tiene materias asignadas, **no se puede eliminar**. El sistema te dirá en qué materias está asignado. Primero elimina esas asignaciones desde **Consultas** (ver sección 8.4) y luego vuelve a intentarlo.

> En esta versión los datos de un profesor ya registrado **no se pueden editar**. Si hubo un error, elimina al profesor (si no tiene asignaciones) y vuelve a registrarlo.

---

## 6. Materias (unidades de aprendizaje)

Entra desde **Materias** en el menú.

### 6.1 Registrar una materia

1. Escribe el **Nombre de la unidad de aprendizaje** (máximo 50 caracteres).
2. Elige las horas en cada lista desplegable (de **0 a 4**):
   * **Horas clase**
   * **Horas taller**
   * **Horas laboratorio**
3. Pulsa **Guardar**.

📷 *Captura: formulario "Nueva Unidad de Aprendizaje".*

**Reglas:**

* El nombre es obligatorio.
* Cada tipo de hora debe estar entre 0 y 4.
* La materia debe tener **al menos 1 hora** en total (no pueden ser 0, 0 y 0).

El botón **Limpiar** deja el formulario en blanco (horas en 0).

### 6.2 Lista de materias

**Materias registradas** muestra el nombre y las horas de clase, taller y laboratorio de cada materia.

### 6.3 Eliminar una materia

1. Pulsa **Eliminar** en la fila de la materia.
2. Confirma en **"¿Eliminar la materia: …?"**.

Si la materia tiene profesores asignados **no se puede eliminar**; el sistema muestra con qué profesores tiene asignaciones. Elimina primero esas asignaciones desde **Consultas**.

> En esta versión las materias tampoco se pueden editar una vez registradas.

---

## 7. Asignaciones

Entra desde **Asignaciones** en el menú. Aquí decides **qué profesor imparte qué materia y en qué horario**.

📷 *Captura: pantalla "Asignar Materia a Profesor" con la cuadrícula.*

### 7.1 Partes de la pantalla

* **Profesor** — lista de profesores registrados.
* **Unidad de aprendizaje** — lista de materias registradas.
* **Tres contadores:** **Clase**, **Taller** y **Laboratorio**. Muestran `horas pintadas / horas requeridas` (por ejemplo `1/3`).
  * 🟢 Verde: ya cubriste las horas de ese tipo.
  * 🟠 Naranja: todavía faltan horas.
  * 🔴 Rojo: te pasaste de las horas requeridas.
* **Horario del profesor** — cuadrícula con los días (lunes a viernes) en columnas y las horas (07:00 a 21:00) en filas.
* Botones **Cancelar** y **Confirmar**.

### 7.2 Paso a paso

1. **Elige el profesor.**
2. **Elige la materia.** Los contadores se actualizan con las horas que necesita esa materia (por ejemplo `0/3`, `0/1`, `0/2`).
3. **Selecciona el tipo de hora** que vas a pintar haciendo clic en **Clase**, **Taller** o **Laboratorio** (por defecto está activa *Clase*).
4. **Haz clic en las celdas** de la cuadrícula donde quieras ese tipo de hora. Cada celda es **1 hora**. La celda pintada muestra un ●.
   * Para **quitar** una celda pintada, haz clic sobre ella otra vez.
   * Cambia de tipo (Clase → Taller → Laboratorio) para pintar los demás.
5. Verifica que **los tres contadores estén completos** (ej. `3/3`, `1/1`, `2/2`).
6. Pulsa **Confirmar**.

Si quieres empezar de nuevo antes de confirmar, pulsa **Cancelar** (borra las celdas pintadas).

### 7.3 Qué revisa el sistema al confirmar

1. Que hayas elegido profesor y materia y pintado al menos una celda.
2. Que las horas pintadas de **cada tipo sean exactamente las requeridas** por la materia. Si faltan o sobran, te lo dice antes de enviar.
3. Que **el profesor no tenga ya otra clase que se empalme** en el mismo día y horario. Los horarios que solo se tocan **no** cuentan como empalme (por ejemplo, uno que termina a las 09:00 y otro que empieza a las 09:00 es válido).

Si hay algún problema, **no se guarda nada** y se muestra la lista de errores. Si todo está bien, aparece **"✅ N asignación(es) guardada(s) correctamente"**.

> Las horas consecutivas del mismo tipo se guardan juntas como un solo bloque. Por ejemplo, Clase el lunes a las 08:00 y a las 09:00 se guarda como *lunes 08:00–10:00*.

### 7.3.1 Consejos

* La cuadrícula **no muestra** las horas que el profesor ya tiene ocupadas. Si eliges un horario que se empalma con otra materia, el sistema te lo avisará al confirmar y tendrás que elegir otro.
* Después de confirmar (con éxito o con error) la pantalla se reinicia: vuelve a elegir profesor y materia.

---

## 8. Consultas: buscar, modificar y eliminar asignaciones

Entra desde **Consultas** en el menú.

📷 *Captura: pantalla "Consultas de Asignaciones".*

### 8.1 Buscar

Hay dos formas de buscar, **independientes** una de otra:

* **Buscar por profesor:** elige un profesor y pulsa su botón **🔍 Buscar**.
* **Buscar por unidad de aprendizaje:** elige una materia y pulsa su botón **🔍 Buscar**.

El sistema avisa **"Se encontraron N asignaciones"** o **"No se encontraron resultados"**. El botón **🔄 Limpiar** borra los resultados y las selecciones.

### 8.2 Tabla de resultados

| Columna | Qué muestra |
|---|---|
| **Tipo** | **C** = Clase, **T** = Taller, **L** = Laboratorio (pasa el cursor para ver el nombre completo) |
| **Profesor** | Nombre y apellido paterno |
| **Unidad** | Nombre de la materia |
| **Día** | Día de la semana |
| **Horario** | Hora de inicio – hora de fin |
| **Grupo** | Código corto. **Las filas con el mismo código pertenecen a la misma asignación** |
| **Acciones** | **✏️ Editar** y **🗑️ Eliminar asignación** |

Los resultados se ordenan por profesor, materia, grupo, día y hora.

### 8.3 Modificar el horario de un bloque

1. Pulsa **✏️ Editar** en la fila que quieres cambiar. Aparece el panel **Modificar horario**, con un resumen del bloque.
2. Elige el **Nuevo día** y la **Nueva hora de inicio**.
3. Pulsa **Guardar cambios** (o **Cancelar** para cerrar el panel sin cambiar nada).

**Reglas:**

* Se **conserva la duración** del bloque; la hora de fin se calcula sola (el panel te muestra la duración).
* El bloque no puede terminar después de las **21:00**.
* No puede empalmarse con otra clase del mismo profesor.
* Debes cambiar algo: si dejas el mismo día y hora, verás **"No hiciste ningún cambio en el horario"**.

Solo se mueve **ese bloque** (esa fila), no toda la asignación.

### 8.4 Eliminar una asignación

1. Pulsa **🗑️ Eliminar asignación** en cualquiera de las filas de la asignación.
2. Lee la advertencia y confirma.

**Importante:** se elimina la **asignación completa** —todos sus bloques de clase, taller y laboratorio— sin importar en qué fila pulsaste. Esto es porque una materia siempre debe tener el 100 % de sus horas asignadas; nunca queda una asignación "a medias".

El sistema informa cuántos horarios se eliminaron.

---

## 9. Cerrar sesión

Pulsa **Cerrar sesión** al final del menú lateral. Volverás a la pantalla de inicio de sesión. Usa siempre esta opción al terminar, especialmente si trabajas en un equipo compartido.

---

## 10. Mensajes del sistema y qué hacer

Los mensajes aparecen como ventanas emergentes del navegador o, en Materias, como texto dentro de la página.

| Mensaje | Dónde | Qué significa / qué hacer |
|---|---|---|
| Credenciales incorrectas | Inicio de sesión | Correo o contraseña equivocados; verifícalos |
| Profesor creado exitosamente | Profesores | Registro correcto |
| Ya existe un profesor con el RFC: … | Profesores | Ese RFC ya está registrado; revisa el dato |
| El RFC '…' no tiene un formato válido… | Profesores | Corrige el RFC (letras + 6 dígitos + 3 caracteres) |
| El nombre / apellido … es obligatorio | Profesores | Llena el campo que falta |
| No se puede eliminar al profesor … porque está asignado a: … | Profesores | Elimina primero sus asignaciones en **Consultas** |
| Profesor eliminado | Profesores | Eliminación correcta |
| Unidad registrada correctamente | Materias | Registro correcto |
| Las horas … deben estar entre 0 y 4 | Materias | Elige un valor de 0 a 4 |
| La unidad debe tener al menos 1 hora asignada… | Materias | Pon al menos 1 hora en clase, taller o laboratorio |
| No se puede eliminar la materia '…' porque tiene asignaciones con: … | Materias | Elimina primero esas asignaciones en **Consultas** |
| Debe seleccionar un profesor / una unidad de aprendizaje | Asignaciones | Elige ambos antes de confirmar |
| Debe pintar al menos una celda en el horario | Asignaciones | Marca horas en la cuadrícula |
| Debes asignar TODAS las horas de cada tipo antes de guardar | Asignaciones | Revisa los contadores: deben quedar completos |
| Faltan horas por asignar / Se excede el total de horas | Asignaciones | Ajusta las celdas pintadas a las horas requeridas |
| Existe un traslape de horario para el profesor en el día … | Asignaciones / Consultas | El profesor ya tiene algo en ese horario; elige otro |
| No se guardó ninguna asignación | Asignaciones | Hubo errores; corrígelos y confirma de nuevo |
| ✅ N asignación(es) guardada(s) correctamente | Asignaciones | Asignación registrada |
| No se encontraron resultados | Consultas | Ese profesor o materia no tiene asignaciones |
| No hiciste ningún cambio en el horario | Consultas | Cambia el día o la hora antes de guardar |
| …terminaría después de las 21:00. Elige una hora más temprana | Consultas | El bloque no cabe; elige una hora de inicio menor |
| ✅ Asignación modificada | Consultas | Cambio guardado |
| ✅ Asignación eliminada por completo (N horario(s)) | Consultas | Se eliminaron todos los bloques de la asignación |

---

## 11. Preguntas frecuentes

**¿Puedo asignar la misma materia a dos profesores?**
El sistema no lo impide: cada asignación es independiente y se valida contra el horario del profesor elegido.

**Me equivoqué en un dato de un profesor o una materia. ¿Cómo lo corrijo?**
En esta versión no hay opción de editar. Elimina el registro (si no tiene asignaciones) y vuelve a crearlo.

**No puedo eliminar un profesor o una materia.**
Tiene asignaciones. Búscalas en **Consultas**, elimínalas y vuelve a intentar.

**¿Por qué me pide pintar exactamente todas las horas?**
Porque una materia debe tener siempre el 100 % de sus horas asignadas. Si necesitas cambiar solo un bloque, usa **Editar** en Consultas.

**Al confirmar una asignación se limpió todo. ¿Se guardó?**
Sí, la pantalla se reinicia siempre después de confirmar. Lee el mensaje que aparece: indica si se guardó o qué error hubo. Puedes comprobarlo en **Consultas**.

**Aparece un traslape, pero no veo con qué.**
Busca al profesor en **Consultas** para ver todo su horario y elige un espacio libre.
