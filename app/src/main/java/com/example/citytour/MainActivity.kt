
package com.example.citytour

// Importamos las clases necesarias para trabajar
// con audio, ventanas y elementos visuales.
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

// AppCompatActivity permite administrar
// la pantalla principal de nuestra aplicación.
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // -------------------------------------------------
    // 1. DECLARACIÓN DE VARIABLES
    // -------------------------------------------------

    // Variable que almacenará el reproductor de audio.
    // Inicialmente es null porque todavía no se reproduce nada.
    private var reproductor: MediaPlayer? = null

    // Controla la posición de la fotografía que se muestra.
    private var indiceImagen = 0

    // Lista de imágenes que forman nuestro carrusel.
    // Cada elemento corresponde a una imagen de drawable-nodpi.
    private val imagenesMonumento = intArrayOf(
        R.drawable.arco_01,
        R.drawable.arco_02,
        R.drawable.arco_03
    )

    // Declaramos los elementos visuales de la pantalla.
    private lateinit var imgMonumento: ImageView
    private lateinit var tvContador: TextView
    private lateinit var tvEstadoAudio: TextView

    // -------------------------------------------------
    // 2. MÉTODO PRINCIPAL DE LA ACTIVIDAD
    // -------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Conectamos esta actividad con el diseño XML.
        setContentView(R.layout.activity_main)

        // Relacionamos las variables de Kotlin con los
        // identificadores definidos en activity_main.xml.
        imgMonumento = findViewById(R.id.imgMonumento)
        tvContador = findViewById(R.id.tvContador)
        tvEstadoAudio = findViewById(R.id.tvEstadoAudio)

        // Obtenemos los botones del carrusel.
        val btnAnterior = findViewById<Button>(R.id.btnAnterior)
        val btnSiguiente = findViewById<Button>(R.id.btnSiguiente)

        // Obtenemos los botones del reproductor.
        val btnPlay = findViewById<Button>(R.id.btnPlay)
        val btnPause = findViewById<Button>(R.id.btnPause)
        val btnStop = findViewById<Button>(R.id.btnStop)

        // Mostramos la primera fotografía al iniciar.
        mostrarImagen()

        // -------------------------------------------------
        // 3. EVENTOS DEL CARRUSEL
        // -------------------------------------------------

        // Al presionar Anterior, retrocedemos una imagen.
        btnAnterior.setOnClickListener {
            cambiarImagen(-1)
        }

        // Al presionar Siguiente, avanzamos una imagen.
        btnSiguiente.setOnClickListener {
            cambiarImagen(1)
        }

        // -------------------------------------------------
        // 4. EVENTOS DEL REPRODUCTOR DE AUDIO
        // -------------------------------------------------

        // Iniciar o reanudar la reproducción.
        btnPlay.setOnClickListener {
            reproducirAudio()
        }

        // Pausar la reproducción actual.
        btnPause.setOnClickListener {
            pausarAudio()
        }

        // Detener completamente la reproducción.
        btnStop.setOnClickListener {
            detenerAudio()
        }
    }

    // -------------------------------------------------
    // 5. MÉTODOS PARA CONTROLAR EL CARRUSEL
    // -------------------------------------------------

    // Cambia la posición de la imagen actual.
    // El parámetro paso puede ser 1 o -1.
    private fun cambiarImagen(paso: Int) {

        // Calculamos el nuevo índice de la imagen.
        // El módulo permite regresar al principio
        // o al final cuando llegamos a los extremos.
        indiceImagen =
            (indiceImagen + paso + imagenesMonumento.size) %
                    imagenesMonumento.size

        // Actualizamos la fotografía en pantalla.
        mostrarImagen()
    }

    // Muestra la imagen correspondiente al índice actual.
    private fun mostrarImagen() {

        // Cargamos la imagen desde los recursos locales.
        imgMonumento.setImageResource(
            imagenesMonumento[indiceImagen]
        )

        // Actualizamos el texto que indica la posición.
        tvContador.text =
            "Imagen ${indiceImagen + 1} de ${imagenesMonumento.size}"

        // Actualizamos la descripción accesible de la imagen.
        imgMonumento.contentDescription =
            "Fotografía ${indiceImagen + 1} del Arco de la Calzada"
    }

    // -------------------------------------------------
    // 6. MÉTODO PARA REPRODUCIR Y REANUDAR EL AUDIO
    // -------------------------------------------------

    private fun reproducirAudio() {

        // Si no existe un reproductor, creamos uno nuevo.
        if (reproductor == null) {

            // Cargamos el archivo MP3 desde res/raw.
            // create() prepara automáticamente el audio.
            reproductor = MediaPlayer.create(
                this,
                R.raw.audio_arco_calzada
            )

            // Comprobamos que el reproductor se haya creado.
            if (reproductor == null) {

                tvEstadoAudio.text =
                    "No fue posible cargar el audio."

                return
            }

            // Configuramos un evento que se ejecuta
            // automáticamente cuando termina la narración.
            reproductor?.setOnCompletionListener { player ->

                // Liberamos los recursos utilizados.
                player.release()

                // Indicamos que ya no existe un reproductor activo.
                reproductor = null

                // Actualizamos el estado de la pantalla.
                tvEstadoAudio.text =
                    "Narración finalizada."
            }
        }

        // Obtenemos el reproductor disponible.
        val player = reproductor

        // Si el audio no está reproduciéndose,
        // iniciamos o reanudamos su reproducción.
        if (player != null && !player.isPlaying) {

            player.start()

            // Informamos al usuario del estado actual.
            tvEstadoAudio.text = "Audio reproduciéndose..."

        } else {

            // Evitamos iniciar otra reproducción
            // si el audio ya se encuentra activo.
            tvEstadoAudio.text = "El audio ya está reproduciéndose."
        }
    }

    // -------------------------------------------------
    // 7. MÉTODO PARA PAUSAR EL AUDIO
    // -------------------------------------------------

    private fun pausarAudio() {

        // Comprobamos que exista un reproductor
        // y que actualmente esté reproduciendo audio.
        if (reproductor != null && reproductor!!.isPlaying) {

            // Pausamos sin eliminar la posición actual.
            reproductor?.pause()

            // Actualizamos el mensaje de estado.
            tvEstadoAudio.text = "Audio pausado."

        } else if (reproductor != null) {

            // Existe el reproductor, pero ya está pausado.
            tvEstadoAudio.text = "El audio ya está pausado."

        } else {

            // Todavía no se ha iniciado ninguna reproducción.
            tvEstadoAudio.text = "Primero debes reproducir el audio."
        }
    }

    // -------------------------------------------------
    // 8. MÉTODO PARA DETENER EL AUDIO
    // -------------------------------------------------

    private fun detenerAudio() {

        // Guardamos temporalmente el reproductor actual.
        val player = reproductor

        if (player != null) {

            // Detenemos la reproducción.
            player.stop()

            // Liberamos los recursos utilizados.
            player.release()

            // Eliminamos la referencia al reproductor.
            reproductor = null

            // Actualizamos el estado mostrado.
            tvEstadoAudio.text =
                "Audio detenido. Puedes reproducirlo nuevamente."

        } else {

            // Informamos cuando no existe audio activo.
            tvEstadoAudio.text = "El audio ya está detenido."
        }
    }

    // -------------------------------------------------
    // 9. LIBERACIÓN DE RECURSOS
    // -------------------------------------------------

    override fun onStop() {
        super.onStop()

        // Si el usuario sale de la pantalla,
        // liberamos el reproductor para no conservar
        // recursos multimedia innecesariamente.
        if (reproductor != null) {

            reproductor?.release()
            reproductor = null

            tvEstadoAudio.text =
                "Audio liberado al salir de la pantalla."
        }
    }
}
