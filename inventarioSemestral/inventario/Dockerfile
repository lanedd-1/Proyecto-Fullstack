#Stage 1: Compilación del proyecto con Maven
FROM eclipse-temurin:21-jdk-alpine AS build
#Instalar e gestor de maven ya que alpine no lo trae
RUN apk add --no-cache maven
#Indicar el directorio de trabajo dentro del contenedor
WORKDIR /app
#copiar la configuración que tiene el proyecto
COPY pom.xml .
#Descargar las dependencias de Maven pero sin compilar
RUN mvn dependency:go-offline -B -q
#Copiar el código fuente
COPY src ./src
#Compilar y empaquetar --- No incluir los Test
RUN mvn package -DskipTests -B -q

#Stage 2: Ejecutar la imagen de manera liviana
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
#copiar el JAR de la app
COPY --from=build /app/target/*.jar app.jar
#documente el puerto de salida pero sin abrirlo directamente
EXPOSE 8090
#cual es la imagen empaquetada para arrancar la aplicación spring boot
ENTRYPOINT [ "java","-jar","app.jar" ]