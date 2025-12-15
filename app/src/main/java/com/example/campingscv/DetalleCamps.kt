package com.example.campingscv

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.campingscv.databinding.FragmentDetalleCampsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalleCamps : Fragment() {

    private lateinit var binding: FragmentDetalleCampsBinding
    private lateinit var camping: Camping
    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        camping = arguments?.getSerializable("camping") as Camping
        binding = FragmentDetalleCampsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        comprobarFavs(camping)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_detalleCamps_to_listaCampingFavs)
        }

        binding.tvCategoria.text = getFormattedText("Categoria", camping.categoria.toString())
        binding.tvNombre.text = getFormattedText("Nombre del Camping", camping.nombre)
        binding.tvPlazas.text = getFormattedText("Número de plazas", camping.plazas.toString())
        binding.tvCalle.text = getFormattedText("Dirección", camping.direccion)
        binding.tvMunicipio.text = getFormattedText("Municipio", camping.municipio)
        binding.tvPeriodo.text = getFormattedText("Periodo", camping.periodo)
    }

    private fun getFormattedText(label: String, value: String): Spanned {
        val formattedText = "<b>$label:</b><br>$value"
        return Html.fromHtml(formattedText, Html.FROM_HTML_MODE_COMPACT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Indicar que este fragmento manejará los eventos del menú de opciones
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_detalle, menu) // Inflar el menú de opciones
        this.menu = menu
        super.onCreateOptionsMenu(menu,inflater)
    }

    private fun openWebsite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        startActivity(intent)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // Configura el ícono de estrella según el estado de favoritos del camping
        if (camping.favorito) {
            menu.findItem(R.id.action_favoritos)?.setIcon(R.drawable.ic_star_filled)
        } else {
            menu.findItem(R.id.action_favoritos)?.setIcon(R.drawable.ic_star_outline)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_visit_website -> {
                openWebsite(camping.web)
                return true
            }
            R.id.action_view_map -> {
                openGoogleMaps(camping.direccion)
                return true
            }
            R.id.action_favoritos -> {
                // Cambiar el estado de favoritos del camping al hacer clic en el ícono de estrella

                if(!camping.favorito){
                    añadirFavs(camping)
                    menu?.findItem(R.id.action_favoritos)?.setIcon(R.drawable.ic_star_filled)
                } else {
                    borrarFavs(camping)
                    menu?.findItem(R.id.action_favoritos)?.setIcon(R.drawable.ic_star_outline)
                }
                camping.favorito = !camping.favorito
                activity?.invalidateOptionsMenu() // Actualizar el menú
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun añadirFavs(camping: Camping){
        val BD = Room.databaseBuilder(requireContext(), CampingDatabase::class.java, "basedatos").build()
        val Dao = BD.campingDao()
        val camping = Camping(camping.id, camping.categoria, camping.nombre, camping.municipio, camping.direccion, camping.web, camping.plazas, camping.periodo)
        GlobalScope.launch {
            try {
                Dao.insert(camping)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Camping ${camping.nombre} añadido", Toast.LENGTH_SHORT).show()

                }

                println("Lista campings: ${Dao.getAllCampings()}")
                println("Camping ${camping.nombre} añadido")
            } catch (e: Exception){
                Log.e("CampingDetails", "Error al añadir: ${e.message}")
            }
        }
    }

    private fun borrarFavs(camping: Camping){
        val BD = Room.databaseBuilder(requireContext(), CampingDatabase::class.java, "basedatos").build()
        val Dao = BD.campingDao()
        val camping = Camping(camping.id, camping.categoria, camping.nombre, camping.municipio, camping.direccion, camping.web, camping.plazas, camping.periodo)
        GlobalScope.launch {
            try {
                Dao.deleteCamping(camping)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Camping ${camping.nombre} eliminado", Toast.LENGTH_SHORT).show()
                }
                println("Camping ${camping.nombre} eliminado")
            } catch (e: Exception){
                Log.e("CampingDetails", "Error al añadir: ${e.message}")
            }
        }
    }

    private fun comprobarFavs(camping: Camping){
        val BD = Room.databaseBuilder(requireContext(), CampingDatabase::class.java, "basedatos").build()
        val Dao = BD.campingDao()
        GlobalScope.launch(Dispatchers.IO) {
            val campingEnBD = Dao.getCampingById(camping.id)
            withContext(Dispatchers.Main) {
                if (campingEnBD != null) {
                    camping.favorito = true
                    menu?.findItem(R.id.action_favoritos)?.setIcon(R.drawable.ic_star_filled)
                } else {
                    camping.favorito = false
                    menu?.findItem(R.id.action_favoritos)?.setIcon(R.drawable.ic_star_outline)
                }
            }
        }
    }

    private fun openGoogleMaps(direccion: String) {
        val uri = Uri.parse("geo:0,0?q=$direccion")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps") // Asegúrate de que la aplicación de mapas de Google esté instalada

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            // Si no se encuentra la aplicación de mapas de Google, puedes manejarlo de otra manera o mostrar un mensaje al usuario.
            // Por ejemplo, puedes abrir un navegador web con Google Maps en la versión web.
            openWebsite("https://www.google.com/maps/search/?api=1&query=$direccion")
        }
    }





}
