# Campings CV

App Android para consultar los **campings de la Comunitat Valenciana**. Descarga los datos oficiales del portal de datos abiertos de la Generalitat Valenciana y permite buscarlos, ordenarlos, ver su ficha y guardar tus favoritos en el móvil.

## Funcionalidades

- **Listado de campings** cargado desde la API de datos abiertos de la GVA.
- **Buscador** por nombre del camping, que filtra la lista mientras escribes.
- **Ordenación** de la lista de la A a la Z, de la Z a la A o por número de plazas.
- **Ficha de detalle** de cada camping: categoría, nombre, número de plazas, dirección, municipio y periodo de apertura.
- **Ver en el mapa**: abre la dirección del camping en Google Maps.
- **Visitar la web** del camping en el navegador.
- **Favoritos**: marca un camping con la estrella y se guarda en una base de datos local, así que sigue ahí al cerrar la app.
- **Pantalla de favoritos** con todos los campings guardados.
- **Pantalla de bienvenida** al abrir la app.

## Tecnologías

| Área | Herramientas |
|---|---|
| Lenguaje | Kotlin |
| Interfaz | Fragments, RecyclerView, CardView, Material Components, View Binding |
| Navegación | Navigation Component (gráfico de navegación entre fragments) |
| Datos remotos | OkHttp + `org.json` contra la API CKAN de la GVA |
| Persistencia | Room (SQLite) para los favoritos |
| Concurrencia | Corrutinas de Kotlin (la descarga se hace en segundo plano con `Dispatchers.IO`) |
| Integraciones | Intents a Google Maps y al navegador |

## Fuente de datos

Los datos vienen del conjunto de **campings** del portal [Dades Obertes de la Generalitat Valenciana](https://dadesobertes.gva.es/), a través de su API:

```
https://dadesobertes.gva.es/api/3/action/datastore_search?id=2ddaf823-5da4-4459-aa57-5bfe9f9eb474
```

El proyecto también incluye una copia de los datos en `app/src/main/res/raw/camping.json` (100 campings), con un método para leerla sin conexión.

## Estructura del proyecto

```
app/src/main/
├─ java/com/example/campingscv/
│  ├─ MainActivity.kt         # Actividad principal y barra de navegación
│  ├─ ListaCamps.kt           # Listado: descarga, búsqueda y ordenación
│  ├─ MyAdapter.kt            # Adaptador del RecyclerView del listado
│  ├─ DetalleCamps.kt         # Ficha del camping, mapa, web y favorito
│  ├─ ListaCampingFavs.kt     # Pantalla de favoritos
│  ├─ AdapterFavs.kt          # Adaptador de la lista de favoritos
│  ├─ Camping.kt              # Modelo de datos (entidad de Room)
│  ├─ CampingDao.kt           # Consultas a la base de datos
│  └─ CampingDatabase.kt      # Base de datos Room
└─ res/
   ├─ layout/                 # Pantallas y fila de la lista
   ├─ menu/                   # Menús de ordenación y de la ficha
   ├─ navigation/navegador.xml
   └─ raw/camping.json        # Copia local de los datos
```

## Cómo ejecutarla

**Requisitos:** Android Studio (Hedgehog o posterior) y un dispositivo o emulador con **Android 7.0 (API 24)** o superior y conexión a internet.

1. Clona el repositorio:
   ```bash
   git clone https://github.com/carlosizdo/Camping.git
   ```
2. Ábrelo en Android Studio con **File → Open** y espera a que Gradle sincronice.
3. Conecta un móvil o arranca un emulador.
4. Pulsa **Run ▶**.

| Configuración | Versión |
|---|---|
| `minSdk` / `targetSdk` | 24 / 34 |
| Android Gradle Plugin | 8.2.2 |
| Kotlin | 1.9.22 |
| Room | 2.4.1 |

## Autor

**Carlos Izquierdo Gil** — [GitHub](https://github.com/carlosizdo) · [LinkedIn](https://www.linkedin.com/in/carlosizquierdogil)
