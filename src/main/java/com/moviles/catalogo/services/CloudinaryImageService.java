package com.moviles.catalogo.services;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.moviles.catalogo.exceptions.BusinessException;

@Service
public class CloudinaryImageService {

    private static final String BUSINESS_FOLDER = "EvorEZ/Negocios";
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        "image/jpeg",
        "image/png",
        "image/webp",
        "image/gif"
    );
    private static final String PRODUCT_DELIVERY_TRANSFORMATION =
        "f_auto,q_auto:good,w_1200,h_1200,c_limit";
    private static final String LOGO_DELIVERY_TRANSFORMATION =
        "f_auto,q_auto:good,w_512,h_512,c_fill,g_auto";

    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;

    public CloudinaryImageService(
        @Value("${cloudinary.cloud-name:}") String cloudName,
        @Value("${cloudinary.api-key:}") String apiKey,
        @Value("${cloudinary.api-secret:}") String apiSecret
    ) {
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public String uploadProductImage(MultipartFile image, long businessId) {
        return upload(image, productFolder(businessId), PRODUCT_DELIVERY_TRANSFORMATION);
    }

    public String uploadBusinessLogo(MultipartFile image, long businessId) {
        return upload(image, businessLogoFolder(businessId), LOGO_DELIVERY_TRANSFORMATION);
    }

    static String productFolder(long businessId) {
        return BUSINESS_FOLDER + "/" + businessId + "/productos";
    }

    static String businessLogoFolder(long businessId) {
        return BUSINESS_FOLDER + "/" + businessId + "/logo";
    }

    private String upload(MultipartFile image, String folder, String deliveryTransformation) {
        if (image.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Selecciona una imagen para subir");
        }

        if (image.getContentType() == null
            || !ALLOWED_CONTENT_TYPES.contains(image.getContentType())) {
            throw new BusinessException(
                HttpStatus.BAD_REQUEST,
                "Formato no admitido. Usa JPG, PNG, WEBP o GIF"
            );
        }

        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new BusinessException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "La subida de imágenes no está configurada. Revisa las variables CLOUDINARY_* del .env"
            );
        }

        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret,
            "secure", true
        ));

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                image.getBytes(),
                ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "image"
                )
            );
            Object secureUrl = result.get("secure_url");
            if (!(secureUrl instanceof String url) || url.isBlank()) {
                throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "Cloudinary no devolvió la URL segura de la imagen"
                );
            }
            return optimizedDeliveryUrl(url, deliveryTransformation);
        } catch (IOException | RuntimeException exception) {
            if (exception instanceof BusinessException businessException) {
                throw businessException;
            }
            throw new BusinessException(
                HttpStatus.BAD_GATEWAY,
                "No se pudo subir la imagen a Cloudinary"
            );
        }
    }

    static String optimizedDeliveryUrl(String secureUrl, String transformation) {
        String uploadPath = "/image/upload/";
        int uploadIndex = secureUrl.indexOf(uploadPath);
        if (uploadIndex < 0) {
            throw new BusinessException(
                HttpStatus.BAD_GATEWAY,
                "Cloudinary devolvió una URL de imagen no válida"
            );
        }

        int insertionIndex = uploadIndex + uploadPath.length();
        return secureUrl.substring(0, insertionIndex)
            + transformation + "/"
            + secureUrl.substring(insertionIndex);
    }
}
