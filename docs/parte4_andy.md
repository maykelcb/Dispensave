# Parte 4 - Backend, API y Persistencia Real
**Responsable:** Andy
**Rama recomendada:** git checkout -b feature/parte4-andy

---

## Objetivo General
Reemplazar todos los repositorios en memoria (RescueRepository, UserRepository) por llamadas
reales a un backend, implementar autenticacion con Firebase Auth o JWT, persistencia remota en
base de datos (Firebase Firestore o API REST propia), subida de imagenes, notificaciones push
y geolocalizacion real.

Esta es la parte mas tecnica del proyecto. Todo el codigo de las Partes 1, 2 y 3 fue escrito
pensando en que TU lo conectaras al backend real.

---

## Tecnologias Recomendadas

| Tecnologia | Para que |
|------------|---------|
| Firebase Auth | Registro e inicio de sesion real |
| Firebase Firestore | Base de datos en la nube (usuarios, productos, pedidos) |
| Firebase Storage | Subida de imagenes de productos y logos de locales |
| Firebase Cloud Messaging (FCM) | Notificaciones push |
| Retrofit + OkHttp | Si prefieres API REST propia en lugar de Firebase |
| Google Maps SDK | Geolocalizacion y calculo de distancias reales |
| Coil | Carga de imagenes remotas en Compose |

---

## Archivos que debes modificar/crear

| Archivo | Que hacer |
|---------|-----------|
| data/repository/UserRepository.kt | Reemplazar lista en memoria por Firebase Auth + Firestore |
| data/repository/RescueRepository.kt | Reemplazar StateFlow en memoria por Firestore coleccion "ofertas" |
| data/remote/ (nueva carpeta) | Cliente HTTP, interceptores, modelos de red |
| data/local/AppDatabase.kt | Agregar tablas Room para cache offline |
| util/GeolocalizacionUtils.kt | Calcular distancia real con GPS del dispositivo |
| AndroidManifest.xml | Agregar permisos de ubicacion e internet |

---

## Tareas Detalladas

### TAREA 4.1 - Configurar Firebase en el Proyecto
1. Ir a https://console.firebase.google.com
2. Crear proyecto "dispensave-prod"
3. Agregar app Android con package: com.example.dispensave
4. Descargar google-services.json y colocarlo en: app/google-services.json
5. Agregar en build.gradle (proyecto):
   classpath 'com.google.gms:google-services:4.4.0'
6. Agregar en build.gradle (app):
   apply plugin: 'com.google.gms.google-services'
   implementation platform('com.google.firebase:firebase-bom:32.7.0')
   implementation 'com.google.firebase:firebase-auth-ktx'
   implementation 'com.google.firebase:firebase-firestore-ktx'
   implementation 'com.google.firebase:firebase-storage-ktx'
   implementation 'com.google.firebase:firebase-messaging-ktx'

---

### TAREA 4.2 - Autenticacion Real con Firebase Auth
**Archivo:** data/repository/UserRepository.kt

Reemplaza el sistema de autenticacion local por Firebase Authentication:

- registerUser(): usar FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
- login(): usar FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
- Al registrar, guardar datos adicionales del usuario (nombre, rol, categoria) en Firestore:
  Coleccion: "usuarios"
  Documento: uid del usuario
  Campos: { nombre, correo, rol, nombreLocal, categoriaLocal, fechaRegistro }

IMPORTANTE: Mantener la misma firma de metodos (registerUser, loginUser) para no romper
el codigo de PantryViewModel.kt que ya existe.

Los sellados LoginResult y RegisterResult que ya existen deben seguir funcionando igual.

---

### TAREA 4.3 - Persistencia de Ofertas en Firestore
**Archivo:** data/repository/RescueRepository.kt

Reemplaza la lista en memoria por una coleccion de Firestore "ofertas":

- surplusBags: Flow<List<SurplusBag>> debe leer de Firestore en tiempo real usando
  db.collection("ofertas").addSnapshotListener { snapshot, _ -> ... }
- publishBag(): guardar el nuevo SurplusBag en Firestore
- reserveBag(): usar transaccion de Firestore para decrementar stockAvailable atomicamente
- confirmPickup(): actualizar el estado de la Order en Firestore

Para los pedidos (Order):
- Coleccion en Firestore: "pedidos"
- Campos: bagId, storeName, bagTitle, rescuePrice, pickupTimeWindow, reservationCode, status, userId, timestamp

---

### TAREA 4.4 - Subida de Imagenes con Firebase Storage
**Nueva funcionalidad:**

Cuando el comercio publica un Pack o Producto Individual, permitirle:
1. Seleccionar una foto de su galeria o tomar una foto con la camara
2. Subir la imagen a Firebase Storage en: "imagenes/ofertas/{ofertaId}.jpg"
3. Guardar la URL de descarga en el campo "imageUrl" del documento en Firestore
4. En ExploreScreen.kt (Ruben lo dejara listo), cargar la imagen con Coil:
   AsyncImage(model = bag.imageUrl, ...)

Para seleccionar imagen, usar ActivityResultContracts.GetContent() o
ActivityResultContracts.TakePicture() de Jetpack.

Permisos necesarios en AndroidManifest.xml:
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-feature android:name="android.hardware.camera" android:required="false" />

---

### TAREA 4.5 - Notificaciones Push con Firebase Cloud Messaging
**Nuevo archivo:** util/ServicioNotificaciones.kt

Crea un FirebaseMessagingService para:
- Recibir notificaciones cuando hay nuevos packs disponibles cerca del cliente
- Notificar al comercio cuando un cliente reserva uno de sus packs
- Recordatorio a los clientes antes de la franja horaria de recojo

En ServicioNotificaciones.kt:
class ServicioNotificaciones : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // mostrar notificacion local con NotificationManager
    }
    override fun onNewToken(token: String) {
        // guardar el token en Firestore del usuario para enviarle notificaciones
    }
}

Registrar en AndroidManifest.xml:
<service android:name=".util.ServicioNotificaciones"
         android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT"/>
    </intent-filter>
</service>

---

### TAREA 4.6 - Geolocalizacion Real
**Nuevo archivo:** util/GeolocalizacionUtils.kt

Reemplaza el campo distanceKm hardcodeado en SurplusBag por distancias calculadas con GPS real:

1. Solicitar permisos de ubicacion al usuario:
   Manifest.permission.ACCESS_FINE_LOCATION
   Manifest.permission.ACCESS_COARSE_LOCATION

2. Obtener ubicacion actual con FusedLocationProviderClient de Google Play Services:
   implementation 'com.google.android.gms:play-services-location:21.1.0'

3. Calcular distancia con la formula Haversine o usando:
   Location.distanceBetween(lat1, lon1, lat2, lon2, results)

4. Actualizar SurplusBag con la distancia real en km

Para el mapa en la pantalla de detalle (Parte 2 de Ruben):
5. Usar Google Maps Compose:
   implementation 'com.google.maps.android:maps-compose:4.3.0'

---

### TAREA 4.7 - Cache Offline con Room
**Archivo:** data/local/AppDatabase.kt (ya existe parcialmente)

Implementar estrategia offline-first:
- Al cargar datos de Firestore, guardarlos en Room como cache local
- Si no hay internet, mostrar los datos del cache con un banner "Sin conexion - mostrando datos recientes"
- Cuando se recupere la conexion, sincronizar automaticamente

Agrega en AppDatabase.kt las tablas:
- OfertaEntity (para cachear SurplusBag)
- PedidoEntity (para cachear Order)
- UsuarioEntity (para cachear datos del usuario actual)

Usa ConnectivityManager para detectar si hay internet:
val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
val tieneInternet = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET) == true

---

### Criterios de Aceptacion (Revision de Maykel)
- [ ] El registro con Firebase Auth funciona y guarda datos en Firestore
- [ ] El login con Firebase Auth funciona y carga los datos del usuario
- [ ] Las ofertas publicadas por el comercio se ven en Firestore console
- [ ] Una reserva decrementa el stock en Firestore de forma atomica
- [ ] Las imagenes se suben a Firebase Storage y se muestran en la app
- [ ] Las notificaciones push llegan al dispositivo
- [ ] La distancia al local se calcula con GPS real (no hardcodeada)
- [ ] La app funciona sin internet usando el cache de Room

---

### Como integrar tu trabajo
1. git checkout -b feature/parte4-andy
2. Hacer tus cambios modulo por modulo
3. git add -A && git commit -m "feat(parte4): [descripcion]"
4. git push origin feature/parte4-andy
5. Crear Pull Request hacia main
6. Avisar a Maykel para revision

### IMPORTANTE: NO rompas las interfaces existentes
Los metodos publicos de UserRepository y RescueRepository deben mantener las mismas firmas
para que el codigo de PantryViewModel.kt (Parte 1) siga funcionando sin modificaciones.
Si necesitas cambiar una firma, coordina con Maykel primero.
