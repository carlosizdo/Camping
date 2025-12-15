package com.example.campingscv

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tabla_favoritos")
data class Camping(
    @PrimaryKey @ColumnInfo(name = "id_camping") val id: Int,
    /*var signatura: String = "",
    var c_estado: String = "",
    var estado: String = "",
    var c_catergoria: String = "",*/
    var categoria:String = "",
    @ColumnInfo(name = "nombre_camping") var nombre:String = "",
    /*var c_provincia:Int = 0,
    var provincia:String = "",
    var c_municipio:Int = 0,*/
    var municipio:String = "",
    //var CP: Int = 0,
    var direccion:String = "",
    /*var c_tipo_via: String = "",
    var numero:String = "",
    var email: String = "",*/
    var web:String = "",
    /*var c_mod:String = "",
    var modalidad:String = "",
    var n_parcelas:Int = 0,
    var p_plazas:String = "",
    var n_bungalows:String = "",
    var p_bungalows: String = "",
    var s_libre_acampada:  String = "",*/
    var plazas: Int = 0,
    /*var fecha_alta: String = "",
    var fecha_baja : String = ""*/
    var periodo: String = "",
    /*var d_periodo: String = "",
    var tipo_via:String = "",
    var via: String = "",*/
    var favorito: Boolean = false
): Serializable