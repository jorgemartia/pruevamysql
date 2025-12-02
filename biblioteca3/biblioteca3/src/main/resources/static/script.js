const API_URL = 'https://pruevamysql.onrender.com';

let usuarioActual = null;

// ===== GESTIÓN DE VISTAS =====
function mostrarLogin() {
    document.getElementById('loginView').style.display = 'flex';
    document.getElementById('registroView').style.display = 'none';
    document.getElementById('dashboardView').style.display = 'none';
}

function mostrarRegistro() {
    document.getElementById('loginView').style.display = 'none';
    document.getElementById('registroView').style.display = 'flex';
    document.getElementById('dashboardView').style.display = 'none';
}

function mostrarDashboard() {
    document.getElementById('loginView').style.display = 'none';
    document.getElementById('registroView').style.display = 'none';
    document.getElementById('dashboardView').style.display = 'block';

    if (!usuarioActual) return;

    // Mostrar información del usuario
    document.getElementById('usuarioNombre').textContent = 
        `${usuarioActual.nombre || ''} ${usuarioActual.apellido || ''}`;
    document.getElementById('rolBadge').textContent = usuarioActual.rol || '';

    // Mostrar pestaña de administración solo para ADMIN
    const btnAdmin = document.getElementById('btnAdminTab');
    if (usuarioActual.rol === 'ADMIN') {
        btnAdmin.style.display = 'block';
    } else {
        btnAdmin.style.display = 'none';
    }

    // Configurar eventos de pestañas principales
    configurarEventosPestanas();
    
    // Mostrar catálogo por defecto
    mostrarPestana('catalogo');
}

// ===== CONFIGURAR EVENTOS DE PESTAÑAS =====
function configurarEventosPestanas() {
    const botonesPestana = document.querySelectorAll('[data-tab]');
    botonesPestana.forEach(boton => {
        boton.onclick = function() {
            const tab = this.getAttribute('data-tab');
            mostrarPestana(tab);
        };
    });
}

// ===== MOSTRAR PESTAÑA PRINCIPAL =====
function mostrarPestana(nombreTab) {
    // Ocultar todas las pestañas
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.style.display = 'none';
        tab.classList.remove('active');
    });

    // Desactivar todos los botones
    document.querySelectorAll('[data-tab]').forEach(boton => {
        boton.classList.remove('active');
    });

    // Activar pestaña seleccionada
    const tabActiva = document.getElementById(nombreTab + 'Tab');
    if (tabActiva) {
        tabActiva.style.display = 'block';
        tabActiva.classList.add('active');
    }

    // Activar botón correspondiente
    const botonActivo = document.querySelector(`[data-tab="${nombreTab}"]`);
    if (botonActivo) {
        botonActivo.classList.add('active');
    }

    // Cargar datos según la pestaña
    if (nombreTab === 'catalogo') {
        cargarLibros();
    } else if (nombreTab === 'prestamos') {
        cargarPrestamos();
    } else if (nombreTab === 'admin' && usuarioActual && usuarioActual.rol === 'ADMIN') {
        // Mostrar primera subpestaña de admin por defecto
        cambiarSubTab('prestamosAdmin', document.querySelector('.nav-link'));
    }
}

// ===== CAMBIAR SUB-PESTAÑAS DE ADMINISTRACIÓN =====
function cambiarSubTab(idSubTab, botonElement) {
    // Ocultar todas las sub-pestañas
    document.querySelectorAll('.subtab-content').forEach(subtab => {
        subtab.style.display = 'none';
        subtab.classList.remove('active');
    });

    // Mostrar sub-pestaña seleccionada
    const subtabActiva = document.getElementById(idSubTab);
    if (subtabActiva) {
        subtabActiva.style.display = 'block';
        subtabActiva.classList.add('active');
    }

    // Actualizar botones de navegación
    document.querySelectorAll('.nav-link').forEach(btn => {
        btn.classList.remove('active');
    });
    if (botonElement) {
        botonElement.classList.add('active');
    }

    // Cargar datos según sub-pestaña
    if (idSubTab === 'prestamosAdmin') {
        cargarTodosPrestamos();
    }
}

// ===== LOGIN =====
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;

        try {
            const response = await fetch(`${API_URL}/api/usuarios/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, clave: password })
            });

            const data = await response.json();
            if (response.ok && data.exito) {
                usuarioActual = data.datos;
                mostrarDashboard();
                cargarLibros();
                cargarPrestamos();
                if (usuarioActual.rol === 'ADMIN') {
                    cargarTodosPrestamos();
                }
            } else {
                alert(data.mensaje || 'Credenciales inválidas');
            }
        } catch (error) {
            console.error('Error login:', error);
            alert('Error al iniciar sesión');
        }
    });
}

// ===== REGISTRO =====
const registroForm = document.getElementById('registroForm');
if (registroForm) {
    registroForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const usuario = {
            nombre: document.getElementById('nombre').value.trim(),
            apellido: document.getElementById('apellido').value.trim(),
            cedula: document.getElementById('cedula').value.trim(),
            telefono: document.getElementById('telefono').value.trim(),
            email: document.getElementById('emailReg').value.trim(),
            clave: document.getElementById('claveReg').value
        };

        try {
            const response = await fetch(`${API_URL}/api/usuarios/registro`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(usuario)
            });

            const data = await response.json();
            if (response.ok && data.exito) {
                alert('✅ Usuario registrado exitosamente');
                mostrarLogin();
                registroForm.reset();
            } else {
                alert('❌ Error: ' + (data.mensaje || 'Error desconocido'));
            }
        } catch (error) {
            console.error('Error registro:', error);
            alert('Error de conexión');
        }
    });
}

// ===== CARGAR LIBROS =====
async function cargarLibros() {
    const librosList = document.getElementById('librosList');
    if (!librosList) return;

    librosList.innerHTML = '<div class="col-12"><div class="loader"></div></div>';

    try {
        const response = await fetch(`${API_URL}/api/libros`);
        const data = await response.json();
        librosList.innerHTML = '';

        const libros = data?.datos ?? [];
        if (!Array.isArray(libros) || libros.length === 0) {
            librosList.innerHTML = '<div class="col-12"><p class="text-center">No hay libros disponibles</p></div>';
            return;
        }

        libros.forEach(libro => {
            const disponible = (libro.cantidadDisponible ?? 0) > 0;
            const col = document.createElement('div');
            col.className = 'col';
            col.innerHTML = `
                <div class="libro-card">
                    <h4>${escapeHtml(libro.titulo)}</h4>
                    <p><strong>Autor:</strong> ${escapeHtml(libro.autor || '')}</p>
                    <p><strong>ISBN:</strong> ${escapeHtml(libro.isbn || '')}</p>
                    <p><strong>Categoría:</strong> ${escapeHtml(libro.categoria || '')}</p>
                    <span class="disponibilidad ${disponible ? 'disponible' : 'no-disponible'}">
                        ${libro.cantidadDisponible || 0} de ${libro.cantidadTotal || 0} disponibles
                    </span>
                    <br><br>
                    ${disponible ? 
                        `<button class="btn btn-success" onclick="realizarPrestamo(${libro.id})">Solicitar Préstamo</button>` :
                        `<button class="btn btn-secondary" disabled>No disponible</button>`
                    }
                </div>
            `;
            librosList.appendChild(col);
        });
    } catch (error) {
        console.error('Error cargarLibros:', error);
        librosList.innerHTML = '<div class="col-12"><p class="text-center text-danger">Error al cargar libros</p></div>';
    }
}

// ===== CARGAR PRÉSTAMOS DEL USUARIO =====
async function cargarPrestamos() {
    const prestamosList = document.getElementById('prestamosList');
    if (!prestamosList || !usuarioActual) return;

    prestamosList.innerHTML = '<div class="loader"></div>';

    try {
        const response = await fetch(`${API_URL}/api/prestamos/usuario/${usuarioActual.id}`);
        const data = await response.json();
        prestamosList.innerHTML = '';

        const prestamos = data?.datos ?? [];
        const activos = prestamos.filter(p => p.estado === 'ACTIVO');

        if (activos.length === 0) {
            prestamosList.innerHTML = '<p class="text-center text-muted">No tienes préstamos activos</p>';
            return;
        }

        activos.forEach(prestamo => {
            const item = document.createElement('div');
            item.className = 'prestamo-item';
            item.innerHTML = `
                <div class="prestamo-info">
                    <h4>${escapeHtml(prestamo.libro?.titulo || '')}</h4>
                    <p>📅 Préstamo: ${formatDate(prestamo.fechaPrestamo)}</p>
                    <p>⏰ Devolución esperada: ${formatDate(prestamo.fechaDevolucionEsperada)}</p>
                    <span class="estado-badge estado-${(prestamo.estado || '').toLowerCase()}">
                        ${escapeHtml(prestamo.estado)}
                    </span>
                </div>
                <button class="btn btn-danger" onclick="devolverPrestamo(${prestamo.id})">Devolver</button>
            `;
            prestamosList.appendChild(item);
        });
    } catch (error) {
        console.error('Error cargarPrestamos:', error);
        prestamosList.innerHTML = '<p class="text-center text-danger">Error al cargar préstamos</p>';
    }
}

// ===== CARGAR TODOS LOS PRÉSTAMOS (ADMIN) =====
async function cargarTodosPrestamos() {
    if (!usuarioActual || usuarioActual.rol !== 'ADMIN') return;
    
    const todosPrestamosList = document.getElementById('todosPrestamosList');
    if (!todosPrestamosList) return;

    todosPrestamosList.innerHTML = '<div class="loader"></div>';

    try {
        const response = await fetch(`${API_URL}/api/prestamos?usuarioId=${usuarioActual.id}`);
        const data = await response.json();
        todosPrestamosList.innerHTML = '';

        const prestamos = data?.datos ?? [];
        if (prestamos.length === 0) {
            todosPrestamosList.innerHTML = '<p class="text-center text-muted">No hay préstamos registrados</p>';
            return;
        }

        prestamos.forEach(prestamo => {
            const item = document.createElement('div');
            item.className = 'prestamo-item';
            item.innerHTML = `
                <div class="prestamo-info">
                    <h4>${escapeHtml(prestamo.libro?.titulo || '')}</h4>
                    <p class="usuarios-info">👤 ${escapeHtml(prestamo.usuario?.nombre || '')} ${escapeHtml(prestamo.usuario?.apellido || '')}</p>
                    <p class="usuarios-info">📧 ${escapeHtml(prestamo.usuario?.email || '')}</p>
                    <p>📅 Préstamo: ${formatDate(prestamo.fechaPrestamo)}</p>
                    <p>⏰ Devolución esperada: ${formatDate(prestamo.fechaDevolucionEsperada)}</p>
                    ${prestamo.fechaDevolucionReal ? `<p>✅ Devuelto: ${formatDate(prestamo.fechaDevolucionReal)}</p>` : ''}
                    <span class="estado-badge estado-${(prestamo.estado || '').toLowerCase()}">
                        ${escapeHtml(prestamo.estado)}
                    </span>
                </div>
                ${prestamo.estado === 'ACTIVO' ? 
                    `<button class="btn btn-danger" onclick="devolverPrestamo(${prestamo.id})">Devolver</button>` : 
                    `<span class="text-success">✓ Devuelto</span>`
                }
            `;
            todosPrestamosList.appendChild(item);
        });
    } catch (error) {
        console.error('Error cargarTodosPrestamos:', error);
        todosPrestamosList.innerHTML = '<p class="text-center text-danger">Error al cargar préstamos</p>';
    }
}

// ===== REALIZAR PRÉSTAMO =====
async function realizarPrestamo(libroId) {
    if (!usuarioActual) {
        alert('Debes iniciar sesión');
        return;
    }

    try {
        const response = await fetch(`${API_URL}/api/prestamos`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                usuarioId: usuarioActual.id, 
                libroId: libroId 
            })
        });

        const data = await response.json();
        if (response.ok && data.exito) {
            alert('✅ Préstamo realizado exitosamente');
            cargarLibros();
            cargarPrestamos();
        } else {
            alert('❌ ' + (data.mensaje || 'Error al realizar préstamo'));
        }
    } catch (error) {
        console.error('Error realizarPrestamo:', error);
        alert('Error al realizar préstamo');
    }
}

// ===== DEVOLVER PRÉSTAMO =====
async function devolverPrestamo(prestamoId) {
    if (!usuarioActual) return;
    if (!confirm('¿Confirmar devolución del libro?')) return;

    try {
        const response = await fetch(`${API_URL}/api/prestamos/${prestamoId}/devolver?usuarioId=${usuarioActual.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' }
        });

        const data = await response.json();
        if (response.ok && data.exito) {
            alert('✅ ' + (data.mensaje || 'Libro devuelto exitosamente'));
            cargarLibros();
            cargarPrestamos();
            if (usuarioActual.rol === 'ADMIN') {
                cargarTodosPrestamos();
            }
        } else {
            alert('❌ ' + (data.mensaje || 'Error al devolver'));
        }
    } catch (error) {
        console.error('Error devolverPrestamo:', error);
        alert('Error al devolver préstamo');
    }
}

// ===== REGISTRAR LIBRO (ADMIN) =====
const libroForm = document.getElementById('libroForm');
if (libroForm) {
    libroForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        if (!usuarioActual || usuarioActual.rol !== 'ADMIN') {
            alert('No tienes permisos para esta acción');
            return;
        }

        const libro = {
            titulo: document.getElementById('tituloLibro').value.trim(),
            autor: document.getElementById('autorLibro').value.trim(),
            isbn: document.getElementById('isbnLibro').value.trim(),
            categoria: document.getElementById('categoriaLibro').value.trim(),
            descripcion: document.getElementById('descripcionLibro').value.trim(),
            cantidadTotal: parseInt(document.getElementById('cantidadLibro').value, 10) || 1
        };

        try {
            const response = await fetch(`${API_URL}/api/libros?usuarioId=${usuarioActual.id}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(libro)
            });

            const data = await response.json();
            if (response.ok && data.exito) {
                alert('✅ Libro registrado exitosamente');
                libroForm.reset();
                cargarLibros();
            } else {
                alert('❌ ' + (data.mensaje || 'Error al registrar libro'));
            }
        } catch (error) {
            console.error('Error registrar libro:', error);
            alert('Error al registrar libro');
        }
    });
}

// ===== LIMPIAR PRÉSTAMOS DEVUELTOS =====
async function limpiarPrestamosDevueltos() {
    if (!usuarioActual || usuarioActual.rol !== 'ADMIN') {
        alert('No tienes permisos para esta acción');
        return;
    }

    if (!confirm('⚠️ ¿Estás seguro de que deseas eliminar todos los préstamos con estado DEVUELTO? Esta acción no se puede deshacer.')) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/api/prestamos/limpiar-devueltos?usuarioId=${usuarioActual.id}`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' }
        });

        const data = await response.json();
        if (response.ok && data.exito) {
            alert(`✅ ${data.mensaje}`);
            cargarTodosPrestamos();
        } else {
            alert('❌ ' + (data.mensaje || 'Error al limpiar préstamos'));
        }
    } catch (error) {
        console.error('Error limpiar préstamos:', error);
        alert('Error al limpiar préstamos devueltos');
    }
}

// ===== CERRAR SESIÓN =====
function cerrarSesion() {
    usuarioActual = null;
    mostrarLogin();
    const formLogin = document.getElementById('loginForm');
    if (formLogin) formLogin.reset();
}

// ===== UTILIDADES =====
function formatDate(dateStr) {
    if (!dateStr) return '';
    try {
        const d = new Date(dateStr);
        if (isNaN(d)) return dateStr;
        return d.toLocaleDateString('es-ES');
    } catch {
        return dateStr;
    }
}

function escapeHtml(unsafe) {
    if (unsafe === null || unsafe === undefined) return '';
    return String(unsafe)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

// ===== INICIALIZAR =====
window.addEventListener('load', () => {
    mostrarLogin();
});