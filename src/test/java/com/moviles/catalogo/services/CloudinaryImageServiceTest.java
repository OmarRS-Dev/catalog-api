package com.moviles.catalogo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import com.moviles.catalogo.exceptions.BusinessException;

class CloudinaryImageServiceTest {

    @Test
    void storesImagesInTheBusinessSpecificFolders() {
        assertEquals(
            "EvorEZ/Negocios/42/logo",
            CloudinaryImageService.businessLogoFolder(42)
        );
        assertEquals(
            "EvorEZ/Negocios/42/productos",
            CloudinaryImageService.productFolder(42)
        );
    }

    @Test
    void addsAutomaticFormatQualityAndResponsiveSizeToDeliveryUrl() {
        assertEquals(
            "https://res.cloudinary.com/demo/image/upload/f_auto,q_auto:good,w_1200,h_1200,c_limit/v123/EvorEZ/Negocios/42/productos/item.jpg",
            CloudinaryImageService.optimizedDeliveryUrl(
                "https://res.cloudinary.com/demo/image/upload/v123/EvorEZ/Negocios/42/productos/item.jpg",
                "f_auto,q_auto:good,w_1200,h_1200,c_limit"
            )
        );
    }

    @Test
    void reportsMissingCloudinaryConfiguration() {
        CloudinaryImageService service = new CloudinaryImageService("", "", "");
        MockMultipartFile image = new MockMultipartFile(
            "image",
            "product.jpg",
            "image/jpeg",
            new byte[] {1, 2, 3}
        );

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> service.uploadBusinessLogo(image, 42)
        );

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void rejectsUnsupportedImageTypes() {
        CloudinaryImageService service = new CloudinaryImageService("", "", "");
        MockMultipartFile image = new MockMultipartFile(
            "image",
            "script.svg",
            "image/svg+xml",
            "<svg/>".getBytes()
        );

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> service.uploadBusinessLogo(image, 42)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
