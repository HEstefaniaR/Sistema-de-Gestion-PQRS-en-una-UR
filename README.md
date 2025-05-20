# Sistema de Gestión PQRS en una Unidad Residencial
Proyecto del Curso: Almacenamiento de Datos
Integrantes: 
- Estefanía Hernández Rojas
- Valeria Franco Córdoba
- Mohamed Raid Abdallah Parra
- Ana Isabel Lopera Perafan

# Estructura del Repositorio

El repositorio contiene las siguientes carpetas y archivos principales:
- Pruebas Postman: Carpeta que incluye las colecciones de pruebas realizadas con Postman para verificar el correcto funcionamiento de la API REST desarrollada.
- backend: Directorio principal del código fuente del backend de la aplicación, desarrollado en Java utilizando el framework Spring Boot.
- uploads: Carpeta destinada al almacenamiento de archivos subidos a la aplicación, como evidencias asociadas a las solicitudes.
- consultas.sql: Archivo SQL que contiene las consultas implementadas para interactuar con la base de datos del sistema.
- data.sql: Script SQL utilizado para llenar la base de datos con datos iniciales necesarios para el funcionamiento del sistema.
- script.sql: Archivo que incluye la estructura de la base de datos, así como los objetos almacenados en ella como triggers y procedimientos.
# Instrucciones para Ejecutar el Proyecto

1.	Utilizar los archivos script.sql y data.sql para crear y llenar la base de datos.
2.	Importar el proyecto ubicado en la carpeta backend en un entorno de desarrollo compatible con Java y Spring Boot.
3.	Ejecución del Backend: Iniciar la aplicación Spring Boot. La API estará disponible en http://localhost:8080/UR/.
4.	Pruebas de la API: Utilizar las colecciones de Postman proporcionadas en la carpeta Pruebas Postman para verificar el correcto funcionamiento de los endpoints.
