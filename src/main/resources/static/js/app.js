const state = {
  products: [],
  categories: [],
  token: sessionStorage.getItem("market-token"),
  business: JSON.parse(sessionStorage.getItem("market-business") || "null"),
  adminVisible: false
};

const elements = {
  grid: document.querySelector("#product-grid"),
  adminGrid: document.querySelector("#admin-grid"),
  search: document.querySelector("#search"),
  categoryFilter: document.querySelector("#category-filter"),
  resultCount: document.querySelector("#result-count"),
  catalogMessage: document.querySelector("#catalog-message"),
  adminMessage: document.querySelector("#admin-message"),
  loginOpen: document.querySelector("#login-open"),
  adminOpen: document.querySelector("#admin-open"),
  logout: document.querySelector("#logout"),
  businessName: document.querySelector("#business-name"),
  authMessage: document.querySelector("#auth-message"),
  loginForm: document.querySelector("#login-form"),
  registerForm: document.querySelector("#register-form"),
  authSwitch: document.querySelector("#auth-switch"),
  authSwitchPrompt: document.querySelector("#auth-switch-prompt"),
  productForm: document.querySelector("#product-form"),
  productFormMessage: document.querySelector("#product-form-message"),
  productImageFile: document.querySelector("#product-image-file"),
  productImagePreviewWrap: document.querySelector("#product-image-preview-wrap"),
  productImagePreview: document.querySelector("#product-image-preview"),
  productImageCurrentLabel: document.querySelector("#product-image-current-label"),
  productCategory: document.querySelector("#product-category"),
  categoryProductHelp: document.querySelector("#no-categories-help")
};

function showMessage(element, message, type = "info") {
  element.textContent = message;
  element.className = `alert alert-${type}`;
}

function hideMessage(element) {
  element.textContent = "";
  element.className = "alert d-none";
}

async function api(url, options = {}) {
  const headers = new Headers(options.headers || {});
  if (options.body && !(options.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }
  if (state.token) headers.set("Authorization", `Bearer ${state.token}`);

  const response = await fetch(url, { ...options, headers });
  if (response.status === 204) return null;

  const contentType = response.headers.get("content-type") || "";
  const body = contentType.includes("application/json")
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message = typeof body === "object"
      ? body.message || body.error || "No se pudo completar la solicitud."
      : body || "No se pudo completar la solicitud.";
    throw new Error(message);
  }
  return body;
}

function safeImageUrl(value) {
  if (!value) return null;
  try {
    const url = new URL(value, window.location.origin);
    return ["http:", "https:"].includes(url.protocol) ? url.href : null;
  } catch {
    return null;
  }
}

function makeProductImage(product, className) {
  const wrapper = document.createElement("div");
  wrapper.className = className;
  const url = safeImageUrl(product.portadaProducto);
  if (!url) {
    const placeholder = document.createElement("div");
    placeholder.className = "product-placeholder";
    placeholder.textContent = "✳";
    wrapper.append(placeholder);
    return wrapper;
  }

  const image = document.createElement("img");
  image.className = className === "product-image-wrap" ? "product-image" : "admin-thumb";
  image.src = url;
  image.alt = product.nombreProducto || "Producto";
  image.loading = "lazy";
  image.addEventListener("error", () => {
    const placeholder = document.createElement("div");
    placeholder.className = "product-placeholder";
    placeholder.textContent = "✳";
    wrapper.replaceChildren(placeholder);
  }, { once: true });
  wrapper.append(image);
  return wrapper;
}

function formatPrice(price) {
  return new Intl.NumberFormat("es-MX", {
    style: "currency",
    currency: "MXN"
  }).format(Number(price) || 0);
}

function makeProductCard(product) {
  const column = document.createElement("div");
  column.className = "col-6 col-lg-3";
  const card = document.createElement("article");
  card.className = "product-card";
  card.append(makeProductImage(product, "product-image-wrap"));

  const content = document.createElement("div");
  content.className = "product-content";
  const category = document.createElement("div");
  category.className = "product-category";
  category.textContent = product.categoria?.nombreCategoria || "Producto";
  const name = document.createElement("h3");
  name.className = "product-name";
  name.textContent = product.nombreProducto || "Sin nombre";
  const description = document.createElement("p");
  description.className = "product-description mb-0";
  description.textContent = product.descripcionProducto || "Pregunta en el negocio para conocer más.";
  const meta = document.createElement("div");
  meta.className = "product-meta";
  const price = document.createElement("span");
  price.className = "product-price";
  price.textContent = formatPrice(product.precioProducto);
  const business = document.createElement("span");
  business.className = "product-business";
  business.textContent = product.negocio?.nombreNegocio || "";
  business.title = business.textContent;
  meta.append(price, business);
  content.append(category, name, description, meta);
  card.append(content);
  column.append(card);
  return column;
}

function renderCatalog() {
  const query = elements.search.value.trim().toLocaleLowerCase();
  const categoryId = elements.categoryFilter.value;
  const filtered = state.products.filter((product) => {
    const text = `${product.nombreProducto || ""} ${product.descripcionProducto || ""} ${product.negocio?.nombreNegocio || ""}`.toLocaleLowerCase();
    const matchesText = !query || text.includes(query);
    const matchesCategory = !categoryId || String(product.categoria?.idCategoria) === categoryId;
    return matchesText && matchesCategory;
  });

  elements.grid.replaceChildren(...filtered.map(makeProductCard));
  elements.resultCount.textContent = `${filtered.length} ${filtered.length === 1 ? "producto" : "productos"}`;
  if (state.products.length === 0) {
    showMessage(elements.catalogMessage, "Aún no hay productos publicados. Vuelve pronto para descubrir nuevos negocios.", "light");
  } else if (filtered.length === 0) {
    showMessage(elements.catalogMessage, "No encontramos productos con esos filtros. Prueba otra búsqueda.", "light");
  } else {
    hideMessage(elements.catalogMessage);
  }
}

function populateCategorySelect(select, includeAllOption) {
  const firstOption = includeAllOption
    ? new Option("Todas las categorías", "")
    : new Option("Selecciona una categoría", "");
  select.replaceChildren(firstOption);
  state.categories.forEach((category) => {
    select.add(new Option(category.nombreCategoria, category.idCategoria));
  });
}

async function loadCatalog() {
  try {
    const [products, categories] = await Promise.all([
      api("/api/productos"),
      api("/api/categorias")
    ]);
    state.products = products;
    state.categories = categories;
    populateCategorySelect(elements.categoryFilter, true);
    populateCategorySelect(elements.productCategory, false);
    elements.categoryProductHelp.classList.toggle("d-none", categories.length > 0);
    elements.productCategory.disabled = categories.length === 0;
    renderCatalog();
  } catch (error) {
    showMessage(elements.catalogMessage, `No se pudo cargar el catálogo: ${error.message}`, "danger");
  }
}

function updateAuthControls() {
  const loggedIn = Boolean(state.token && state.business);
  elements.loginOpen.classList.toggle("d-none", loggedIn);
  elements.adminOpen.classList.toggle("d-none", !loggedIn);
  elements.logout.classList.toggle("d-none", !loggedIn);
  elements.businessName.textContent = loggedIn ? state.business.nombreNegocio : "";
  if (!loggedIn) {
    elements.adminOpen.classList.add("d-none");
    elements.businessName.textContent = "";
  }
}

async function login(nombreNegocio, passwordNegocio) {
  const result = await api("/api/negocios/login", {
    method: "POST",
    body: JSON.stringify({ nombreNegocio, passwordNegocio })
  });
  state.token = result.token;
  state.business = result.negocio;
  sessionStorage.setItem("market-token", result.token);
  sessionStorage.setItem("market-business", JSON.stringify(result.negocio));
  updateAuthControls();
  bootstrap.Modal.getOrCreateInstance(document.querySelector("#login-modal")).hide();
  await openAdminPanel();
}

async function loadAdminProducts() {
  if (!state.business) return;
  try {
    const products = await api(`/api/productos/negocio/${encodeURIComponent(state.business.idNegocio)}`);
    renderAdminProducts(products);
  } catch (error) {
    showMessage(elements.adminMessage, error.message, "danger");
  }
}

function renderAdminProducts(products) {
  elements.adminGrid.replaceChildren();
  if (products.length === 0) {
    const empty = document.createElement("div");
    empty.className = "col-12";
    empty.textContent = "Todavía no has agregado productos. Usa “Agregar producto” para publicar el primero.";
    elements.adminGrid.append(empty);
    return;
  }

  products.forEach((product) => {
    const column = document.createElement("div");
    column.className = "col-12 col-lg-6";
    const item = document.createElement("article");
    item.className = "admin-product";
    item.append(makeProductImage(product, "admin-image-wrap"));

    const details = document.createElement("div");
    details.className = "admin-product-details";
    const title = document.createElement("h3");
    title.className = "mb-1";
    title.textContent = product.nombreProducto;
    const info = document.createElement("div");
    info.className = "text-muted small";
    info.textContent = `${formatPrice(product.precioProducto)} · ${product.categoria?.nombreCategoria || "Sin categoría"}`;
    details.append(title, info);

    const actions = document.createElement("div");
    actions.className = "d-flex flex-column flex-sm-row gap-2";
    const edit = document.createElement("button");
    edit.className = "btn btn-sm btn-outline-dark";
    edit.type = "button";
    edit.textContent = "Editar";
    edit.addEventListener("click", () => openProductForm(product));
    const remove = document.createElement("button");
    remove.className = "btn btn-sm btn-outline-danger";
    remove.type = "button";
    remove.textContent = "Eliminar";
    remove.addEventListener("click", () => deleteProduct(product));
    actions.append(edit, remove);
    item.append(details, actions);
    column.append(item);
    elements.adminGrid.append(column);
  });
}

async function openAdminPanel() {
  if (!state.token || !state.business) return;
  state.adminVisible = true;
  document.querySelector("#admin-section").classList.remove("d-none");
  document.querySelector("#catalog-section").classList.add("d-none");
  document.querySelector(".hero").classList.add("d-none");
  elements.adminOpen.classList.add("btn-primary");
  elements.adminOpen.classList.remove("btn-dark");
  hideMessage(elements.adminMessage);
  await loadAdminProducts();
  document.querySelector("#admin-section").scrollIntoView({ behavior: "smooth" });
}

function showCatalog() {
  state.adminVisible = false;
  document.querySelector("#admin-section").classList.add("d-none");
  document.querySelector("#catalog-section").classList.remove("d-none");
  document.querySelector(".hero").classList.remove("d-none");
  elements.adminOpen.classList.remove("btn-primary");
  elements.adminOpen.classList.add("btn-dark");
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function openProductForm(product = null) {
  elements.productForm.reset();
  hideMessage(elements.productFormMessage);
  document.querySelector("#product-id").value = product?.idProducto || "";
  document.querySelector("#product-name").value = product?.nombreProducto || "";
  document.querySelector("#product-description").value = product?.descripcionProducto || "";
  document.querySelector("#product-category").value = product?.categoria?.idCategoria || "";
  document.querySelector("#product-price").value = product?.precioProducto ?? "";
  elements.productImageFile.value = "";
  elements.productImagePreview.src = safeImageUrl(product?.portadaProducto) || "";
  elements.productImageCurrentLabel.textContent = product?.portadaProducto
    ? "Foto actual. Elige otra para reemplazarla."
    : "";
  elements.productImagePreviewWrap.classList.toggle("d-none", !product?.portadaProducto);
  document.querySelector("#product-modal-title").textContent = product ? "Editar producto" : "Agregar producto";
  document.querySelector("#save-product").textContent = product ? "Guardar cambios" : "Guardar producto";
  bootstrap.Modal.getOrCreateInstance(document.querySelector("#product-modal")).show();
}

async function saveProduct(event) {
  event.preventDefault();
  hideMessage(elements.productFormMessage);
  const id = document.querySelector("#product-id").value;
  const payload = {
    nombreProducto: document.querySelector("#product-name").value.trim(),
    descripcionProducto: document.querySelector("#product-description").value.trim(),
    idCategoria: Number(document.querySelector("#product-category").value),
    portadaProducto: null,
    precioProducto: Number(document.querySelector("#product-price").value)
  };

  try {
    const selectedImage = elements.productImageFile.files[0];
    if (selectedImage) {
      const formData = new FormData();
      formData.append("image", selectedImage);
      const uploadedImage = await api("/api/productos/imagenes", {
        method: "POST",
        body: formData
      });
      payload.portadaProducto = uploadedImage.secureUrl;
    } else if (id) {
      const currentProduct = state.products.find((product) => String(product.idProducto) === id);
      payload.portadaProducto = currentProduct?.portadaProducto || null;
    }

    await api(id ? `/api/productos/${encodeURIComponent(id)}` : "/api/productos/guardar", {
      method: id ? "PUT" : "POST",
      body: JSON.stringify(payload)
    });
    bootstrap.Modal.getOrCreateInstance(document.querySelector("#product-modal")).hide();
    showMessage(elements.adminMessage, id ? "Producto actualizado." : "Producto agregado.", "success");
    await Promise.all([loadAdminProducts(), loadCatalog()]);
  } catch (error) {
    showMessage(elements.productFormMessage, error.message, "danger");
  }
}

elements.productImageFile.addEventListener("change", () => {
  const [file] = elements.productImageFile.files;
  if (!file) return;

  if (!["image/jpeg", "image/png", "image/webp", "image/gif"].includes(file.type)) {
    elements.productImageFile.value = "";
    showMessage(elements.productFormMessage, "Formato no admitido. Usa JPG, PNG, WEBP o GIF.", "danger");
    return;
  }
  if (file.size > 5 * 1024 * 1024) {
    elements.productImageFile.value = "";
    showMessage(elements.productFormMessage, "La imagen debe pesar 5 MB o menos.", "danger");
    return;
  }

  hideMessage(elements.productFormMessage);
  elements.productImagePreview.src = URL.createObjectURL(file);
  elements.productImageCurrentLabel.textContent = file.name;
  elements.productImagePreviewWrap.classList.remove("d-none");
});

async function deleteProduct(product) {
  if (!window.confirm(`¿Eliminar “${product.nombreProducto}”? Esta acción no se puede deshacer.`)) return;
  hideMessage(elements.adminMessage);
  try {
    await api(`/api/productos/${encodeURIComponent(product.idProducto)}`, { method: "DELETE" });
    showMessage(elements.adminMessage, "Producto eliminado.", "success");
    await Promise.all([loadAdminProducts(), loadCatalog()]);
  } catch (error) {
    showMessage(elements.adminMessage, error.message, "danger");
  }
}

function logout() {
  state.token = null;
  state.business = null;
  sessionStorage.removeItem("market-token");
  sessionStorage.removeItem("market-business");
  updateAuthControls();
  showCatalog();
}

elements.search.addEventListener("input", renderCatalog);
elements.categoryFilter.addEventListener("change", renderCatalog);
document.querySelector("#search-button").addEventListener("click", renderCatalog);
elements.adminOpen.addEventListener("click", () => {
  if (state.adminVisible) showCatalog();
  else openAdminPanel();
});
elements.logout.addEventListener("click", logout);
elements.productForm.addEventListener("submit", saveProduct);

elements.authSwitch.addEventListener("click", () => {
  const isLogin = !elements.loginForm.classList.contains("d-none");
  elements.loginForm.classList.toggle("d-none", isLogin);
  elements.registerForm.classList.toggle("d-none", !isLogin);
  elements.authSwitchPrompt.textContent = isLogin ? "¿Ya tienes cuenta?" : "¿Aún no tienes cuenta?";
  elements.authSwitch.textContent = isLogin ? "Inicia sesión" : "Registra tu negocio";
  document.querySelector("#login-title").textContent = isLogin ? "Registra tu negocio" : "Accede a tu negocio";
  hideMessage(elements.authMessage);
});

elements.loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  hideMessage(elements.authMessage);
  try {
    await login(
      document.querySelector("#login-name").value.trim(),
      document.querySelector("#login-password").value
    );
    elements.loginForm.reset();
  } catch (error) {
    showMessage(elements.authMessage, error.message, "danger");
  }
});

elements.registerForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  hideMessage(elements.authMessage);
  const business = {
    nombreNegocio: document.querySelector("#register-name").value.trim(),
    direccionNegocio: document.querySelector("#register-address").value.trim(),
    telefonoNegocio: document.querySelector("#register-phone").value.trim(),
    fotoNegocio: null,
    passwordNegocio: document.querySelector("#register-password").value
  };
  try {
    await api("/api/negocios/guardar", { method: "POST", body: JSON.stringify(business) });
    await login(business.nombreNegocio, business.passwordNegocio);
    elements.registerForm.reset();
  } catch (error) {
    showMessage(elements.authMessage, error.message, "danger");
  }
});

document.querySelector("#login-modal").addEventListener("hidden.bs.modal", () => {
  elements.loginForm.classList.remove("d-none");
  elements.registerForm.classList.add("d-none");
  elements.authSwitchPrompt.textContent = "¿Aún no tienes cuenta?";
  elements.authSwitch.textContent = "Registra tu negocio";
  document.querySelector("#login-title").textContent = "Accede a tu negocio";
  hideMessage(elements.authMessage);
});

updateAuthControls();
loadCatalog();
