package com.example.campingscv

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campingscv.databinding.FragmentListaCampsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class ListaCamps : Fragment() {

    private lateinit var searchView: SearchView
    private var originalList = mutableListOf<Camping>()
    private var filteredList = mutableListOf<Camping>()
    private lateinit var binding: FragmentListaCampsBinding
    private lateinit var myAdapter: MyAdapter
    private lateinit var camping: Camping
    private var datosCamps: ArrayList<Camping> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)  // Agregar esta línea para indicar que el fragmento tiene opciones de menú
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListaCampsBinding.inflate(inflater, container, false)
        CoroutineScope(Dispatchers.Main).launch {
            datosCamps= withContext(Dispatchers.IO) {
                // Si no hay conexión o la API falla, se usa la copia local de los datos
                try {
                    getDataInternet()
                } catch (e: Exception) {
                    getData()
                }
            }
            originalList = datosCamps.toMutableList()
            myAdapter.updateData(datosCamps)
        }

        searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredList = if (newText.isNullOrBlank()) {
                    originalList.toMutableList() // Si la consulta está vacía, la lista filtrada es igual a la lista original.
                } else {
                    originalList.filter { it.nombre.contains(newText, ignoreCase = true) }.toMutableList() // Filtra la lista original basándote en la consulta.
                }
                myAdapter.updateData(filteredList) // Actualiza los datos del adaptador con la lista filtrada.
                return true
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_listaCamps_to_listaCampingFavs)
        }
        binding.rv.layoutManager = LinearLayoutManager(activity)
        myAdapter = MyAdapter(datosCamps, object: MyAdapter.CampingListener {
            override fun onItemClick(camping: Camping) {
                val bundle = Bundle().apply {
                    putSerializable("camping", camping)
                }
                findNavController().navigate(R.id.action_listaCamps_to_detalleCamps, bundle)
            }
        })
        binding.rv.adapter = myAdapter // Asignar el adaptador al RecyclerView
    }

    private fun readJsonFromRaw(resources: Resources, rawResourceId: Int): String {
        val inputStream: InputStream = resources.openRawResource(rawResourceId)
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charsets.UTF_8)
    }

    private fun getData(): ArrayList<Camping> {
        val listaCampings = ArrayList<Camping>()
        val rawResourceId = R.raw.camping
        val jsonFileContent = readJsonFromRaw(requireContext().resources, rawResourceId)
        // Parsear el contenido JSON utilizando JSONObject o JSONArray
        val jsonObject = JSONObject(jsonFileContent)
        val jsonResult = jsonObject.getJSONObject("result")
        val recordsArray = jsonResult.getJSONArray("records")

        for (i in 0 until recordsArray.length()) {
            val recordObject = recordsArray.getJSONObject(i)

            val id = recordObject.getInt("_id")
            val categoria = recordObject.getString("Categoria")
            val nombre = recordObject.getString("Nombre")
            val web = recordObject.getString("Web")
            val plazas = recordObject.getInt("Plazas")
            val municipio = recordObject.getString("Municipio")
            val direccion = recordObject.getString("Direccion")
            val periodo = recordObject.getString("Periodo")

            val camping = Camping(id, categoria, nombre, municipio, direccion,  web, plazas, periodo)
            listaCampings.add(camping)
        }

        return listaCampings
    }

    fun getDataInternet(): ArrayList<Camping> {
        val url = "https://dadesobertes.gva.es/api/3/action/datastore_search?id=2ddaf823-5da4-4459-aa57-5bfe9f9eb474"
        val listaCamping = ArrayList<Camping>()

        val request = Request.Builder().url(url).addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
            .addHeader("Accept", "application/json").addHeader("Accept-Charset", "es").build()

        val client = OkHttpClient()

        val response = client.newCall(request).execute()

        if(!response.isSuccessful) throw IOException("Unexpected code $response")
        val body = response.body?.string()
        val jsonObject = JSONObject(body)
        val jsonResult = jsonObject.getJSONObject("result")
        val recordsArray = jsonResult.getJSONArray("records")

        if(recordsArray != null){
            for (i in 0 until recordsArray.length()) {
                val recordObject = recordsArray.getJSONObject(i)

                val id = recordObject.getInt("_id")
                val categoria = recordObject.getString("Categoria")
                val nombre = recordObject.getString("Nombre")
                val web = recordObject.getString("Web")
                val plazas = recordObject.getInt("Plazas")
                val municipio = recordObject.getString("Municipio")
                val direccion = recordObject.getString("Direccion")
                val periodo = recordObject.getString("Periodo")

                val camping = Camping(id, categoria, nombre, municipio, direccion,  web, plazas, periodo)
                listaCamping.add(camping)
            }
        }

        return listaCamping
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_ordenar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort_az -> {
                // Ordenar la lista A-Z por nombre
                datosCamps.sortBy { it.nombre }
                myAdapter.notifyDataSetChanged()
                myAdapter.updateData(datosCamps)
                return true
            }
            R.id.menu_sort_za -> {
                // Ordenar la lista Z-A por nombre
                datosCamps.sortByDescending { it.nombre }
                myAdapter.notifyDataSetChanged()
                myAdapter.updateData(datosCamps)
                return true
            }
            R.id.menu_sort_by_plazas -> {
                // Ordenar la lista por número de plazas
                datosCamps.sortBy { it.plazas }
                myAdapter.notifyDataSetChanged()
                myAdapter.updateData(datosCamps)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
