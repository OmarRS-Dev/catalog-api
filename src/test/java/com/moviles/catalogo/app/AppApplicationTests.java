package com.moviles.catalogo.app;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AppApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void servesMarketplacePage() throws Exception {
		mockMvc.perform(get("/index.html"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Productos para descubrir")));
	}

	@Test
	void exposesCategoriesForMarketplaceFilters() throws Exception {
		mockMvc.perform(get("/api/categorias"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}

	@Test
	void protectsProductCreation() throws Exception {
		mockMvc.perform(post("/api/productos/guardar")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"nombreProducto":"Producto","idCategoria":1,"precioProducto":10}
					"""))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void protectsCloudinaryImageUploads() throws Exception {
		mockMvc.perform(multipart("/api/productos/imagenes")
				.file("image", new byte[] {1, 2, 3}))
			.andExpect(status().isUnauthorized());
	}
}
