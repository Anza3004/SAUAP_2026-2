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

window.horasRequeridas = {
    CLASE: 0,
    TALLER: 0,
    LABORATORIO: 0
};

// ============ INICIALIZACIÓN ============

function inicializarGrid() {
    const tbody = document.querySelector('#gridHorario tbody');
    if (!tbody) return;

    tbody.innerHTML = '';

    window.horas.forEach(function(hora) {
        const tr = document.createElement('tr');

        // Celda de la hora (con rango)
        const tdHora = document.createElement('td');
        tdHora.className = 'grid-hora';
        tdHora.textContent = hora + ' - ' + sumarUnaHora(hora);
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

function inicializarTipos() {
    document.querySelectorAll('.tipo-hora').forEach(function(el) {
        el.addEventListener('click', function() {
            const tipo = el.dataset.tipo;
            if (!tipo) return;

            document.querySelectorAll('.tipo-hora').forEach(function(t) {
                t.classList.remove('active');
            });

            el.classList.add('active');
            window.tipoActual = tipo;
        });
    });

    const defaultTipo = document.querySelector('.tipo-hora[data-tipo="CLASE"]');
    if (defaultTipo) defaultTipo.classList.add('active');
}

// ============ LÓGICA DE PINTADO ============

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

function actualizarContadores() {
    const conteo = { CLASE: 0, TALLER: 0, LABORATORIO: 0 };

    Object.values(window.celdasPintadas).forEach(function(tipo) {
        if (conteo[tipo] !== undefined) conteo[tipo]++;
    });

    var elClase = document.getElementById('contador-clase');
    var elTaller = document.getElementById('contador-taller');
    var elLab = document.getElementById('contador-lab');

    if (elClase) elClase.textContent = conteo.CLASE + '/' + window.horasRequeridas.CLASE;
    if (elTaller) elTaller.textContent = conteo.TALLER + '/' + window.horasRequeridas.TALLER;
    if (elLab) elLab.textContent = conteo.LABORATORIO + '/' + window.horasRequeridas.LABORATORIO;

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

function setHorasRequeridas(clase, taller, laboratorio) {
    window.horasRequeridas = {
        CLASE: clase || 0,
        TALLER: taller || 0,
        LABORATORIO: laboratorio || 0
    };
    actualizarContadores();
}

function limpiarGrid() {
    window.celdasPintadas = {};
    document.querySelectorAll('.grid-celda').forEach(function(td) {
        td.classList.remove('celda-clase', 'celda-taller', 'celda-lab', 'celda-ocupada');
        td.textContent = '';
    });
    actualizarContadores();
}

function serializarGrid() {
    var input = document.querySelector('[id$="gridData"]');
    if (input) {
        input.value = JSON.stringify(window.celdasPintadas);
        console.log('Grid serializado:', input.value);
    } else {
        console.warn('⚠️ No se encontró el input gridData');
    }
}

function marcarCeldasOcupadas(celdas) {
    if (!celdas) return;

    celdas.forEach(function(key) {
        var td = document.querySelector('.grid-celda[data-key="' + key + '"]');
        if (td) {
            td.classList.add('celda-ocupada');
            td.title = 'Ya ocupada';
        }
    });
}

// ============ UTILIDADES ============

/**
 * Suma una hora a una hora en formato HH:mm.
 * "07:00" → "08:00"
 */
function sumarUnaHora(hora) {
    try {
        var partes = hora.split(':');
        var h = parseInt(partes[0]) + 1;
        if (h < 10) h = '0' + h;
        return h + ':' + partes[1];
    } catch (e) {
        return hora;
    }
}

/**
 * Valida que TODOS los contadores estén exactamente en "n/n" antes de guardar.
 * Cuenta los cuadros pintados por tipo (sin importar dónde).
 * Retorna true si todo está bien, false si hay errores.
 */
function validarContadores() {
    const conteo = { CLASE: 0, TALLER: 0, LABORATORIO: 0 };

    Object.values(window.celdasPintadas).forEach(function(tipo) {
        if (conteo[tipo] !== undefined) conteo[tipo]++;
    });

    let errores = [];

    if (conteo.CLASE !== window.horasRequeridas.CLASE) {
        errores.push('Clase: ' + conteo.CLASE + ' de ' + window.horasRequeridas.CLASE + ' horas');
    }
    if (conteo.TALLER !== window.horasRequeridas.TALLER) {
        errores.push('Taller: ' + conteo.TALLER + ' de ' + window.horasRequeridas.TALLER + ' horas');
    }
    if (conteo.LABORATORIO !== window.horasRequeridas.LABORATORIO) {
        errores.push('Laboratorio: ' + conteo.LABORATORIO + ' de ' + window.horasRequeridas.LABORATORIO + ' horas');
    }

    if (errores.length > 0) {
        alert('⚠️ Debes asignar TODAS las horas de cada tipo antes de guardar:\n\n' +
            errores.join('\n'));
        return false;
    }
    return true;
}