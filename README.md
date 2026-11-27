# EvorEZ

EvorEZ es un marketplace monolítico: Spring Boot sirve tanto la API REST como la interfaz web responsive. Los visitantes pueden explorar productos y filtrar por categoría; los negocios pueden iniciar sesión y administrar sus propios productos.

## Configuración

Copia `.env.example` a `.env` y completa tus valores. Para subir imágenes necesitas una cuenta de Cloudinary; en su consola encontrarás **Cloud name**, **API Key** y **API Secret**. Colócalos en:

```env
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret
```

La aplicación nunca envía las credenciales de Cloudinary al navegador. Cada negocio sube sus imágenes dentro de `EvorEZ/Negocios/{id}/logo` y `EvorEZ/Negocios/{id}/productos`. Las URLs de entrega usan `f_auto` y `q_auto:good`: Cloudinary sirve el formato moderno apropiado al navegador y ajusta la compresión automáticamente. Las fotos de producto se limitan a 1200 × 1200 px y los logos a 512 × 512 px. Las imágenes ya existentes de Cloudinary también reciben estas transformaciones al mostrarse en la web. El registro solicita el logo; si la subida falla, se puede reintentar desde el panel del negocio. Se aceptan JPG, PNG, WEBP y GIF, con un tamaño máximo configurable mediante `IMAGE_MAX_FILE_SIZE` (5 MB por defecto).

`.env` está excluido de Git; no compartas ni subas ese archivo.

## Ejecutar

Requiere Java 21 y MySQL. Configura la conexión a MySQL y el resto de valores en `.env`; después inicia la aplicación:

```bash
bash mvnw spring-boot:run
```

Abre <http://localhost:9020/> para el marketplace. Las imágenes requieren credenciales válidas de Cloudinary; el resto del catálogo puede seguir consultándose sin configurarlas.
