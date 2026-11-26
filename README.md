# EvorEZ

EvorEZ es un marketplace monolítico: Spring Boot sirve tanto la API REST como la interfaz web responsive. Los visitantes pueden explorar productos y filtrar por categoría; los negocios pueden iniciar sesión y administrar sus propios productos.

## Configuración

Copia `.env.example` a `.env` y completa tus valores. Para subir imágenes necesitas una cuenta de Cloudinary; en su consola encontrarás **Cloud name**, **API Key** y **API Secret**. Colócalos en:

```env
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret
```

La aplicación nunca envía las credenciales de Cloudinary al navegador. Las imágenes se suben desde el backend a la carpeta `EvorEZ`. Se aceptan JPG, PNG, WEBP y GIF, con un tamaño máximo configurable mediante `IMAGE_MAX_FILE_SIZE` (5 MB por defecto).

`.env` está excluido de Git; no compartas ni subas ese archivo.

## Ejecutar

Requiere Java 21 y MySQL. Configura la conexión a MySQL y el resto de valores en `.env`; después inicia la aplicación:

```bash
bash mvnw spring-boot:run
```

Abre <http://localhost:9020/> para el marketplace. Las imágenes requieren credenciales válidas de Cloudinary; el resto del catálogo puede seguir consultándose sin configurarlas.
