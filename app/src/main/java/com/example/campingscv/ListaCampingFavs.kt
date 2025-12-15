package com.example.campingscv

import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.campingscv.databinding.FragmentFavsCampsBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaCampingFavs: Fragment() {
    private lateinit var binding: FragmentFavsCampsBinding
    private lateinit var myAdapter: AdapterFavs
    private lateinit var camping: Camping
    private var datosCamps: ArrayList<Camping> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)      }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavsCampsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFavs.layoutManager = LinearLayoutManager(activity)
        myAdapter = AdapterFavs(datosCamps, object: AdapterFavs.CampingFavsListener {
            override fun onItemClick(camping: Camping) {
                val bundle = Bundle().apply {
                    putSerializable("camping", camping)
                }
                findNavController().navigate(R.id.action_listaCampingFavs_to_detalleCamps, bundle)
            }
        })
        binding.rvFavs.adapter = myAdapter // Asignar el adaptador al RecyclerView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_ordenar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private var sortOrder = ""

    private fun getData(){
        Log.d("Debug", "getData started")
        CoroutineScope(Dispatchers.IO).launch {
            val BD = Room.databaseBuilder(requireContext(), CampingDatabase::class.java, "basedatos").build()
            val Dao = BD.campingDao()
            var datosCampsTemp = Dao.getAllCampings() as ArrayList<Camping>
            when (sortOrder) {
                "AZ" -> datosCampsTemp.sortBy { it.nombre }
                "ZA" -> datosCampsTemp.sortByDescending { it.nombre }
                "plazas" -> datosCampsTemp.sortBy { it.plazas }
            }
            withContext(Dispatchers.Main){
                datosCamps = datosCampsTemp
                myAdapter.updateData(datosCamps)
                myAdapter.notifyDataSetChanged() // Notificar al adaptador sobre los cambios de datos
                requireActivity().invalidateOptionsMenu()
                Log.d("Debug", "getData completed")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort_az -> sortOrder = "AZ"
            R.id.menu_sort_za -> sortOrder = "ZA"
            R.id.menu_sort_by_plazas -> sortOrder = "plazas"
            else -> return super.onOptionsItemSelected(item)
        }
        getData() // Recargar los datos en el adaptador
        return true
    }
}
