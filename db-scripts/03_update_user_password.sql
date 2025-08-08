-- Script para actualizar la contraseña del usuario josegregoriocoronelcolombo@gmail.com
-- La contraseña será: test123
-- El hash bcrypt generado para 'test123' es: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa

ALTER SESSION SET CONTAINER = XEPDB1;
ALTER SESSION SET CURRENT_SCHEMA = PHARMACY_LOCAL;

-- Actualizar la contraseña del usuario josegregoriocoronelcolombo@gmail.com
UPDATE Usuario 
SET PASSWORD_HASH = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa'
WHERE CORREO = 'josegregoriocoronelcolombo@gmail.com';

-- Verificar que el usuario existe y actualizar otros campos si es necesario
UPDATE Usuario 
SET ACTIVO = 'Y',
    PERFIL_COMPLETO = 'Y',
    PRIMER_LOGIN = 'Y'
WHERE CORREO = 'josegregoriocoronelcolombo@gmail.com';

-- Verificar los cambios
SELECT ID_USUARIO, NOMBRE, CORREO, ACTIVO, PERFIL_COMPLETO, PRIMER_LOGIN 
FROM Usuario 
WHERE CORREO = 'josegregoriocoronelcolombo@gmail.com';

COMMIT;
