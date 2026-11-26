package com.moviles.catalogo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import com.moviles.catalogo.exceptions.BusinessException;

class CloudinaryImageServiceTest {

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
            () -> service.upload(image)
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
            () -> service.upload(image)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
