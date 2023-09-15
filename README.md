# Sistema POS para una farmacia (Farmacia "El Difunto")

<div align="center">
  <img src="https://i.imgur.com/QGRAxgx.png" alt="Farmacia El Difunto" width="100"/>
</div>

Este repositorio contiene el proyecto de un Sistema POS (Punto de Venta) diseñado para la farmacia "Farmacia El Difunto". El sistema aborda el control de ventas e inventario, incorporando un sistema de manejo de usuarios con roles definidos (Empleado y Administrador) que poseen distintos niveles de acceso al sistema. El sistema permite gestionar el inventario, incluyendo operaciones para añadir, modificar y borrar productos. Además, ofrece la capacidad de realizar ventas y generar informes detallados de las transacciones realizadas.

## Tecnologías Utilizadas

<div style="display: flex;">
  <img src="https://repository-images.githubusercontent.com/400161932/257a8be2-bbf2-4218-a55b-219d819578b2" alt="JavaFX" width="150"/>
  <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/6/62/CSS3_logo.svg/800px-CSS3_logo.svg.png" alt="CSS3" width="100"/>
  <img src="https://www.freepnglogos.com/uploads/logo-mysql-png/logo-mysql-mysql-logo-png-images-are-download-crazypng-21.png" alt="MySQL" width="100"/>
  <img src="https://i0.wp.com/gluonhq.com/wp-content/uploads/2015/02/SceneBuilderLogo.png?fit=781%2C781&ssl=1" alt="Scene Builder" width="100"/>
  <img src="https://static.vecteezy.com/system/resources/previews/009/617/298/non_2x/dap-triangle-letter-logo-design-with-triangle-shape-dap-triangle-logo-design-monogram-dap-triangle-logo-template-with-red-color-dap-triangular-logo-simple-elegant-and-luxurious-logo-vector.jpg" alt="Jexcelapi" width="100"/>
</div>

## Configuración del Proyecto

Para este proyecto, se utilizaron las siguientes tecnologías:

- **[JavaFX]** Plataforma para el desarrollo de aplicaciones de interfaz gráfica en Java.
- **[JFoenix]** Librería de diseño de material para JavaFX.
- **[Scene Builder]** Herramienta visual para diseñar interfaces gráficas de usuario.
- **[MySQL Connector]** Conector para trabajar con la base de datos MySQL.
- **[JExcelApi]** Librería para generar y exportar archivos en formato xls (Excel).

## Manual de Usuario

### 1. Iniciar Sesión

- Inicia sesión con una cuenta de "Administrador" en el menú principal para acceder a tres opciones: Ventas, Control de Productos y Control de Usuarios.

![Iniciar Sesión](https://i.imgur.com/WyQv5Hx.png)

### 2. Menú de Control de Usuarios (Solo para Administradores)

- En esta sección puedes gestionar usuarios, incluyendo: 
  - Ingresar un nuevo usuario.
  - Modificar un usuario existente.
  - Eliminar un usuario.

![Menú de Control de Usuarios](https://i.imgur.com/D4ZlYZu.png)

### 3. Ingresar un Nuevo Usuario

- Rellena los campos con la información requerida (nombre de usuario y contraseña).
- Selecciona el tipo de usuario (Administrador o Empleado).
- Haz clic en el botón de guardar (ícono de disquete) para añadir el usuario a la base de datos.

![Ingresar Nuevo Usuario](https://i.imgur.com/VwU0PLw.png)

### 4. Modificar un Usuario

- En la tabla de usuarios, selecciona el usuario que deseas modificar.
- Haz clic en el botón verde con el ícono de actualizar.
- Modifica los datos del usuario en la ventana emergente y guarda los cambios.

![Modificar Usuario](https://i.imgur.com/k2OsgSW.png)

### 5. Eliminar un Usuario

- En la tabla de usuarios, selecciona el usuario que deseas eliminar.
- Haz clic en el botón rojo con el ícono de papelera de reciclaje.
- Confirma la eliminación haciendo clic en "Aceptar".

![Eliminar Usuario](https://i.imgur.com/9KDQVrY.png)

### 6. Menú de Productos

- En esta sección puedes gestionar productos, incluyendo:
  - Ingresar un nuevo producto.
  - Modificar un producto existente.
  - Eliminar un producto.
  - Buscar productos por código, nombre o cantidad.
  - Realizar ventas y generar informes.

### 7. Ingresar un Nuevo Producto

- Rellena los campos con la información del producto.
- Selecciona una fecha de vencimiento en el calendario.
- Haz clic en el botón de guardar (ícono de disquete) para añadir el producto.

![Ingresar Nuevo Producto](https://i.imgur.com/ed83Q0v.png)

### 8. Modificar un Producto

- En la tabla de productos, selecciona el producto que deseas modificar.
- Haz clic en el botón verde con el ícono de actualizar.
- Modifica los datos del producto en la ventana emergente y guarda los cambios.

![Modificar Producto](https://i.imgur.com/QN19jUa.png)

### 9. Eliminar un Producto

- En la tabla de productos, selecciona el producto que deseas eliminar.
- Haz clic en el botón rojo con el ícono de papelera de reciclaje.
- Confirma la eliminación haciendo clic en "Aceptar".

![Eliminar Producto](https://i.imgur.com/PXL3eHF.png)

### 10. Buscar Producto por Código

- Ingresa el código del producto que deseas buscar en el espacio correspondiente.
- Haz clic en el botón de búsqueda (ícono de lupa).

![Buscar por Código](https://i.imgur.com/GKVElJF.png)

### 11. Buscar por Nombre

- Ingresa el nombre del producto que deseas buscar en el espacio correspondiente.
- Haz clic en el botón de búsqueda (ícono de lupa).

![Buscar por Nombre](https://i.imgur.com/EZkCQo3.png)

### 12. Buscar por Cantidad

- Marca la casilla "Buscar por Cantidad".
- Selecciona la cantidad de existencias por la que deseas buscar en el espacio de búsqueda.
- Haz clic en el botón de búsqueda (ícono de lupa).

![Buscar por Cantidad](https://i.imgur.com/yzTdJzy.png)

### 13. Realizar Venta

- Ingresa la información del producto que deseas vender en los campos correspondientes.
- Haz clic en el botón "Agregar" para añadir el producto a la lista de venta.
- Visualiza los productos agregados y el total de la venta en la tabla.

![Realizar Venta](https://i.imgur.com/aibgj7d.png)

### 14. Confirmar la Venta

- Una vez agregados los productos que deseas vender, haz clic en el botón "Vender".
- Si la venta es exitosa, se mostrará un mensaje de éxito.

![Confirmar Venta](https://i.imgur.com/rj9fFXy.png)

### 15. Generar Reporte

- Utiliza los calendarios en la sección derecha de la pantalla para seleccionar un rango de fechas.
- Haz clic en el botón "Exportar" para generar un archivo de reporte (.xls) de las ventas en el rango seleccionado.
- El archivo se creará en el escritorio con el nombre "ventas.xls".

![Generar Reporte](https://i.imgur.com/l68nKFO.png)

## Diagrama Entidad Relación de la Base de Datos

![Diagrama Entidad Relación de la Base de Datos](https://i.imgur.com/EQtiuqc.png)

## Enlaces Relevantes
- [IDE IntelliJ ](https://www.jetbrains.com/es-es/idea/download/?section=windows)
- [Librería JavaFX](https://openjfx.io/)
- [Librería JFoenix](https://github.com/sshahine/JFoenix)
- [Scene Builder](https://gluonhq.com/products/scene-builder/)
- [MySQL Connector](https://dev.mysql.com/downloads/connector/j/)
- [JExcel Api](https://jexcelapi.sourceforge.net/)

## Contacto

- Correo Electrónico: eduardoajuchan@gmail.com
- Instagram: [edoamaze](https://www.instagram.com/edoamaze/)

