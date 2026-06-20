
-- MS_CONFIGURACION
CREATE DATABASE IF NOT EXISTS db_configuracion
 CHARACTER SET utf8mb4
 COLLATE utf8mb4_unicode_ci;

-- MS_USUARIOS
CREATE DATABASE IF NOT EXISTS db_usuarios
 CHARACTER SET utf8mb4
 COLLATE utf8mb4_unicode_ci;

-- MS_DIRECCION
CREATE DATABASE IF NOT EXISTS db_direccion
 CHARACTER SET utf8mb4
 COLLATE utf8mb4_unicode_ci;

-- MS - INVENTARIO
CREATE DATABASE IF NOT EXISTS db_inventario
 CHARACTER SET utf8mb4
 COLLATE utf8mb4_unicode_ci;

-- MS_PRODUCTOS
CREATE DATABASE IF NOT EXISTS db_productos
 CHARACTER SET utf8mb4
 COLLATE utf8mb4_unicode_ci;

-- MS_ENVIO
CREATE DATABASE IF NOT EXISTS db_envio
 CHARACTER SET utf8mb4
 COLLATE utf8mb4_unicode_ci;

-- MS_VENTAS
CREATE DATABASE IF NOT EXISTS db_ventas
 CHARACTER SET utf8mb4
 COLLATE utf8mb4_unicode_ci;

 -- Otorgar todos los permisos al usuario root
GRANT ALL PRIVILEGES ON db_configuracion.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_usuarios.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_direccion.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_inventario.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_productos.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_envio.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_ventas.* TO 'root'@'%';

-- Permisos inmediatamente
FLUSH PRIVILEGES;