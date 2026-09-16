// =====================================================
// SAUAP - Dashboard JavaScript
// Manejo del grid interactivo de asignación de horarios
// =====================================================

// ============ ESTADO GLOBAL ============
window.celdasPintadas = {}; // { "LUNES-08:00": "CLASE", ... }
window.tipoActual = 'CLASE';
window.dias = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES'];
window.horas = ['07:00', '08:00', '09:00', '10:00', '11:00', '12:00',
    '13:00', '14:00', '15:00', '16:00', '17:00', '18:00',
    '19:00', '20:00'];

// Horas requeridas por la unidad seleccionada
window.horasRequeridas = {
    CLASE: 0,
    TALLER: 0,
    LABORATORIO: 0
};

// ============ INICIALIZACIÓN ============

/**
 * Inicializa el grid con las filas y celdas.
 */
function inicializarGrid() {
    const tbody = document.querySelector('#gridHorario tbody');
    if (!tbody) return;

    tbody.innerHTML = '';

    window.horas.forEach(function(hora) {
        const tr = document.createElement('tr');

        // Celda de la hora
        const tdHora = document.createElement('td');
        tdHora.className = 'grid-hora';
        tdHora.textContent = hora;
        tr.appendChild(tdHora);

        // Celda para cada día
        window.dias.forEach(function(dia) {
            const td = document.createElement('td');
            td.className = 'grid-celda';
            td.dataset.dia = dia;
            td.dataset.hora = hora;
            td.dataset.key = dia + '-' + hora;

            td.addEventListener('click', function() {
                toggleCelda(td);
            });

            tr.appendChild(td);
        });

        tbody.appendChild(tr);
    });
}

/**
 * Inicializa los listeners de los tipos de hora.
 */
function inicializarTipos() {
    document.querySelectorAll('.tipo-hora').forEach(function(el) {
        el.addEventListener('click', function() {
            const tipo = el.dataset.tipo;
            if (!tipo) return;

            // Quitar active de todos
            document.querySelectorAll('.tipo-hora').forEach(function(t) {
                t.classList.remove('active');
            });

            // Activar el actual
            el.classList.add('active');
            window.tipoActual = tipo;
        });
    });

    // Activar CLASE por defecto
    const defaultTipo = document.querySelector('.tipo-hora[data-tipo="CLASE"]');
    if (defaultTipo) defaultTipo.classList.add('active');
}

// ============ LÓGICA DE PINTADO ============

/**
 * Alterna el estado de una celda (pintar/despintar).
 */
function toggleCelda(td) {
    const key = td.dataset.key;

    if (window.celdasPintadas[key]) {
        // Despintar
        delete window.celdasPintadas[key];
        td.classList.remove('celda-clase', 'celda-taller', 'celda-lab');
        td.textContent = '';
    } else {
        // Pintar
        window.celdasPintadas[key] = window.tipoActual;
        pintarCelda(td, window.tipoActual);
    }

    actualizarContadores();
}

/**
 * Pinta una celda con el color del tipo.
 */
function pintarCelda(td, tipo) {
    td.classList.remove('celda-clase', 'celda-taller', 'celda-lab');

    switch(tipo) {
        case 'CLASE':
            td.classList.add('celda-clase');
            break;
        case 'TALLER':
            td.classList.add('celda-taller');
            break;
        case 'LABORATORIO':
            td.classList.add('celda-lab');
            break;
    }

    td.textContent = '●';
}

// ============ CONTADORES ============

/**
 * Actualiza los contadores de horas asignadas vs requeridas.
 */
function actualizarContadores() {
    const conteo = { CLASE: 0, TALLER: 0, LABORATORIO: 0 };

    Object.values(window.celdasPintadas).forEach(function(tipo) {
        if (conteo[tipo] !== undefined) conteo[tipo]++;
    });

    document.getElementById('contador-clase').textContent =
        conteo.CLASE + '/' + window.horasRequeridas.CLASE;
    document.getElementById('contador-taller').textContent =
        conteo.TALLER + '/' + window.horasRequeridas.TALLER;
    document.getElementById('contador-lab').textContent =
        conteo.LABORATORIO + '/' + window.horasRequeridas.LABORATORIO;

    // Colores condicionales
    marcarContador('contador-clase', conteo.CLASE, window.horasRequeridas.CLASE);
    marcarContador('contador-taller', conteo.TALLER, window.horasRequeridas.TALLER);
    marcarContador('contador-lab', conteo.LABORATORIO, window.horasRequeridas.LABORATORIO);
}

function marcarContador(id, actual, requerido) {
    const el = document.getElementById(id);
    if (!el) return;

    el.classList.remove('ok', 'excedido', 'incompleto');

    if (requerido === 0) {
        el.classList.add('ok');
    } else if (actual === requerido) {
        el.classList.add('ok');
    } else if (actual > requerido) {
        el.classList.add('excedido');
    } else {
        el.classList.add('incompleto');
    }
}

// ============ ACTUALIZACIÓN DESDE EL SERVIDOR ============

/**
 * Actualiza las horas requeridas (llamado desde JSF con los datos de la unidad).
 */
function setHorasRequeridas(clase, taller, laboratorio) {
    window.horasRequeridas = {
        CLASE: clase || 0,
        TALLER: taller || 0,
        LABORATORIO: laboratorio || 0
    };
    actualizarContadores();
}

/**
 * Limpia el grid.
 */
function limpiarGrid() {
    window.celdasPintadas = {};
    document.querySelectorAll('.grid-celda').forEach(function(td) {
        td.classList.remove('celda-clase', 'celda-taller', 'celda-lab', 'celda-ocupada');
        td.textContent = '';
    });
    actualizarContadores();
}

/**
 * Serializa el grid a un input hidden antes de enviar al servidor.
 */
function serializarGrid() {
    const input = document.getElementById('asignacionForm:gridData');
    if (input) {
        input.value = JSON.stringify(window.celdasPintadas);
    }
}

/**
 * Marca celdas ocupadas (con asignaciones previas del profesor).
 */
function marcarCeldasOcupadas(celdas) {
    if (!celdas) return;

    celdas.forEach(function(key) {
        const td = document.querySelector('.grid-celda[data-key="' + key + '"]');
        if (td) {
            td.classList.add('celda-ocupada');
            td.title = 'Ya ocupada';
        }
    });
}