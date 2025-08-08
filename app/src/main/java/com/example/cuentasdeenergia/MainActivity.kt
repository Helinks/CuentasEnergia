package com.example.cuentasdeenergia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuentasdeenergia.ui.theme.CuentasDeEnergiaTheme
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.navigation.navArgument
import java.net.URLDecoder
import java.net.URLEncoder
import androidx.navigation.NavType
import java.text.NumberFormat
import java.util.Locale

data class ContadorLecturas(
    val id: String = java.util.UUID.randomUUID().toString(), // Un ID único para cada contador (útil para gestionar la lista)
    var numeroAnterior: String,
    var numeroActual: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CuentasDeEnergiaTheme {
                }
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ){
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "calculadora_screen"
                ){
                    composable("calculadora_screen"){
                        CuentasEnergia (navController = navController)
                    }
                    composable(
                        route = "resultados_screen/{totalAPagarPorcontador}/{consumoKWPorContador}/{precioTotalPorContadorSinAseo}/{precioKWPesos}/{totalAseoPorCocina}/{totalAPagarPiso1}/{totalAPagarPiso1SinAseo}/{consumoPiso1}/{totalAPAgarPorContadorRedondeado}/{pagarPiso1Redondeado}",
                        arguments = listOf(
                            navArgument("totalAPagarPorcontador"){ type = NavType.StringType },
                            navArgument("consumoKWPorContador"){ type = NavType.StringType },
                            navArgument("precioTotalPorContadorSinAseo"){ type = NavType.StringType },
                            navArgument("precioKWPesos"){ type = NavType.StringType },
                            navArgument("totalAseoPorCocina"){ type = NavType.StringType },
                            navArgument("totalAPagarPiso1"){ type = NavType.StringType },
                            navArgument("totalAPagarPiso1SinAseo"){ type = NavType.StringType },
                            navArgument("consumoPiso1") { type = NavType.StringType },
                            navArgument("totalAPAgarPorContadorRedondeado"){ type = NavType.StringType },
                            navArgument("pagarPiso1Redondeado"){ type = NavType.StringType }

                        )
                    ){bakcStackEntry ->
                        val totalAPagarPorContadorStr = bakcStackEntry.arguments?.getString("totalAPagarPorcontador")?:""
                        val consumoKWStr = bakcStackEntry.arguments?.getString("consumoKWPorContador") ?: ""
                        val totalSinAseoStr = bakcStackEntry.arguments?.getString("precioTotalPorContadorSinAseo") ?: ""
                        val precioKW = bakcStackEntry.arguments?.getString("precioKWPesos") ?: ""
                        val aseoPorCocina = bakcStackEntry.arguments?.getString("totalAseoPorCocina") ?: ""
                        val totalAPagarPiso1 = bakcStackEntry.arguments?.getString("totalAPagarPiso1") ?: ""
                        val totalAPagarPiso1SinAseo = bakcStackEntry.arguments?.getString("totalAPagarPiso1SinAseo") ?: ""
                        val consumoPiso1 = bakcStackEntry.arguments?.getString("consumoPiso1") ?: ""
                        val totalAPAgarPorContadorRedondeadoStr = bakcStackEntry.arguments?.getString("totalAPAgarPorContadorRedondeado") ?: ""
                        val pagarPiso1RedondeadoStr = bakcStackEntry.arguments?.getString("pagarPiso1Redondeado") ?: ""


                        val charset = Charsets.UTF_8.name()
                        val totalAPagarList = URLDecoder.decode(totalAPagarPorContadorStr, charset).split('|')
                        val consumoKWList = URLDecoder.decode(consumoKWStr, charset).split('|')
                        val totalSinAseoList = URLDecoder.decode(totalSinAseoStr, charset).split('|')
                        val totalAPAgarPorContadorRedondeadoList = URLDecoder.decode(totalAPAgarPorContadorRedondeadoStr, charset).split('|')

                        ResultadosScreen(
                            navController = navController,
                            totalAPagarPorcontador = totalAPagarList,
                            consumoKWPorContador = consumoKWList,
                            precioTotalPorContadorSinAseo = totalSinAseoList,
                            precioKWPesos = URLDecoder.decode(precioKW, charset),
                            totalAseoPorCocina = URLDecoder.decode(aseoPorCocina, charset),
                            pagarPiso1 = URLDecoder.decode(totalAPagarPiso1, charset),
                            pagarPiso1SinAseo = URLDecoder.decode(totalAPagarPiso1SinAseo, charset),
                            consumoPiso1 = URLDecoder.decode(consumoPiso1, charset),
                            totalAPAgarPorContadorRedondeado = totalAPAgarPorContadorRedondeadoList,
                            pagarPiso1Redondeado = URLDecoder.decode(pagarPiso1RedondeadoStr, charset)
                        )
                    }
                }

            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentasEnergia(navController: NavController) {

    val listaContadores = remember { mutableStateListOf<ContadorLecturas>() }
    val consumoKWPorContador = remember { mutableStateListOf<String>() }
    val precioTotalPorContadorSinAseo = remember { mutableStateListOf<String>() }
    val totalAPagarPorcontador = remember { mutableStateListOf<String>() }
    var precioConsumido by remember { mutableStateOf("") }
    var consumoKWDelMes by remember { mutableStateOf("") }
    var precioKWPesos by remember { mutableStateOf("") }
    var aseo by remember { mutableStateOf("") }
    var numeroCocinas by remember { mutableStateOf("") }
    var totalAseoPorCocina by remember { mutableStateOf("") }
    var consumoPiso1 by remember { mutableStateOf("") }
    var pagarPiso1SinAseo by remember { mutableStateOf("") }
    var pagarPiso1 by remember { mutableStateOf("") }

    //Variables de resultados redondeados
    val totalAPAgarPorContadorRedondeado = remember { mutableStateListOf<String>() }
    var pagarPiso1Redondeado by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(listaContadores.isEmpty()){
        listaContadores.add(ContadorLecturas(numeroAnterior = "", numeroActual = ""))
        listaContadores.add(ContadorLecturas(numeroAnterior = "", numeroActual = ""))
        listaContadores.add(ContadorLecturas(numeroAnterior = "", numeroActual = ""))
        listaContadores.add(ContadorLecturas(numeroAnterior = "", numeroActual = ""))
        }

        OutlinedTextField(
            value = precioConsumido,
            onValueChange = { newValue -> precioConsumido = newValue },
            label = { Text("Precio de energia consumida") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = consumoKWDelMes,
            onValueChange = { newValue -> consumoKWDelMes = newValue },
            label = { Text("KW consumidos en el mes") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
        OutlinedTextField(
            value = aseo,
            onValueChange = { newValue -> aseo = newValue },
            label = { Text("Costo del aseo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
        OutlinedTextField(
            value = numeroCocinas,
            onValueChange = { newValue -> numeroCocinas = newValue },
            label = { Text("numero de cocinas") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Esto permite que el LazyColumn ocupe el espacio restante
                .padding(bottom = 16.dp)
        ) {
            itemsIndexed(listaContadores, key = { _, contador -> contador.id }) { index, contador ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Contador ${index + 1}", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = contador.numeroAnterior,
                            onValueChange = { newValue ->
                                if (newValue.count { it == '.' } <= 1) {
                                    val filteredValue =
                                        newValue.filter { it.isDigit() || it == '.' }
                                    // Actualiza la variable directamente en el objeto de la lista
                                    listaContadores[index] =
                                        contador.copy(numeroAnterior = filteredValue)
                                }
                            },
                            label = { Text("Número anterior") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = contador.numeroActual,
                            onValueChange = { newValue ->
                                if (newValue.count { it == '.' } <= 1) {
                                    val filteredValue =
                                        newValue.filter { it.isDigit() || it == '.' }
                                    // Actualiza la variable directamente en el objeto de la lista
                                    listaContadores[index] =
                                        contador.copy(numeroActual = filteredValue)
                                }
                            },
                            label = { Text("Número actual") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = {
                    var totalConsumoTodosContadores = 0.0
                    var kwConsumidosRestantes: Double? = null
                    var totalAPagarPisoSinAseo1: Double? = null
                    var totalAPagarPiso1: Double? = null
                    var pesosKW: Double? = null
                    var aseoPorCocina: Double? = null
                    val precioEnergia = precioConsumido.toDoubleOrNull()
                    val kwConsumidos = consumoKWDelMes.toDoubleOrNull()
                    val costoAseo = aseo.toDoubleOrNull()
                    val numCocinas = numeroCocinas.toDoubleOrNull()
                    consumoKWPorContador.clear()

                    if(precioEnergia != null && kwConsumidos!=null && costoAseo != null && numCocinas != null){
                        pesosKW= precioEnergia/kwConsumidos
                        aseoPorCocina= costoAseo/numCocinas

                        totalAseoPorCocina= "Por cocina el precio del aseo es: \$${formatearConSeparadorMiles(aseoPorCocina)}"
                        precioKWPesos= "Precio del KW en pesos: \$${formatearConSeparadorMiles(pesosKW)}"
                    }

                    for ((index, contador) in listaContadores.withIndex()) {
                        val numAnterior = contador.numeroAnterior.toDoubleOrNull()
                        val numActual = contador.numeroActual.toDoubleOrNull()

                        if (numAnterior != null && numActual != null && pesosKW != null && aseoPorCocina!= null) {
                            val consumoIndividual = numActual - numAnterior
                            val precioPorContadorSinAseo = pesosKW * consumoIndividual
                            val precioPorContadorConAseo = precioPorContadorSinAseo + aseoPorCocina
                            totalConsumoTodosContadores += consumoIndividual

                            precioTotalPorContadorSinAseo.add("C${index + 1}: \$${formatearConSeparadorMiles(precioPorContadorSinAseo)}")
                            totalAPagarPorcontador.add("C${index + 1}: \$${formatearConSeparadorMiles(precioPorContadorConAseo)}")
                            consumoKWPorContador.add("C${index + 1}: ${"%.0f".format(consumoIndividual)}")

                            //Resultados redondeados
                            totalAPAgarPorContadorRedondeado.add("C${index + 1}: \$${formatearConSeparadorMiles(redondearResultado(precioPorContadorConAseo))}")
                        } else {
                            consumoKWPorContador.add("C${index + 1} Error: Números inválidos.")
                        }
                        if(kwConsumidos != null && pesosKW != null && aseoPorCocina != null ) {
                            kwConsumidosRestantes = kwConsumidos - totalConsumoTodosContadores
                            totalAPagarPisoSinAseo1 = pesosKW * kwConsumidosRestantes
                            totalAPagarPiso1 = totalAPagarPisoSinAseo1 + aseoPorCocina

                            pagarPiso1SinAseo = "Total a pagar piso 1 sin aseo: \$${
                                formatearConSeparadorMiles(totalAPagarPisoSinAseo1)
                            }"
                            pagarPiso1 = "Total a pagar piso 1: \$${formatearConSeparadorMiles(totalAPagarPiso1)}"
                            consumoPiso1 = "Consumo KW piso 1: ${"%.0f".format(kwConsumidosRestantes)}"

                            //Resultados redondeados
                            pagarPiso1Redondeado= "Total a pagar piso 1: \$${formatearConSeparadorMiles(redondearResultado(totalAPagarPiso1))}"
                        }
                    }


                    val charset = Charsets.UTF_8.name()
                    val encodedTotalAPagar = URLEncoder.encode(totalAPagarPorcontador.joinToString("|"), charset)
                    val encodedConsumoKW = URLEncoder.encode(consumoKWPorContador.joinToString("|"), charset)
                    val encodedTotalSinAseo = URLEncoder.encode(precioTotalPorContadorSinAseo.joinToString("|"), charset)
                    val encodedpagarPiso1 = URLEncoder.encode(pagarPiso1, charset)
                    val encodedpagarPiso1SinAseo = URLEncoder.encode(pagarPiso1SinAseo, charset)
                    val encodedconsumoPiso1 = URLEncoder.encode(consumoPiso1, charset)
                    val encodedPrecioKW = URLEncoder.encode(precioKWPesos, charset)
                    val encodedAseoPorCocina = URLEncoder.encode(totalAseoPorCocina, charset)

                    //Encode de resultados redondeados
                    val encodedTotalAPagarRedondeado = URLEncoder.encode(totalAPAgarPorContadorRedondeado.joinToString("|"), charset)
                    val encodedPagarPiso1Redondeado = URLEncoder.encode(pagarPiso1Redondeado, charset)

                    navController.navigate("resultados_screen" +
                            "/$encodedTotalAPagar" +
                            "/$encodedConsumoKW" +
                            "/$encodedTotalSinAseo" +
                            "/$encodedPrecioKW" +
                            "/$encodedAseoPorCocina" +
                            "/$encodedpagarPiso1" +
                            "/$encodedpagarPiso1SinAseo" +
                            "/$encodedconsumoPiso1"+
                            "/$encodedTotalAPagarRedondeado" +
                            "/$encodedPagarPiso1Redondeado")

        },
                modifier = Modifier.weight(1f)
            ) {
                Text("Calcular cuentas", fontSize = 20.sp)
            }
            }



        Spacer(modifier = Modifier.height(24.dp)) // Espacio vertical

    }

}

fun redondearResultado(valor: Double): Double {
    val base = (valor / 100).toInt() * 100
    val resto = valor - base

    return when {
        resto <= 50 -> (base + 50).toDouble()
        else -> (base + 100).toDouble()
    }
}
fun formatearConSeparadorMiles(valor: Double): String {
    val formato = NumberFormat.getNumberInstance(Locale("es", "CO")) // o Locale.US si prefieres con coma
    formato.maximumFractionDigits = 0
    return formato.format(valor)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadosScreen(
    navController: NavController,
    // Aquí recibes los datos que quieres mostrar
    totalAPagarPorcontador: List<String>,
    consumoKWPorContador: List<String>,
    precioTotalPorContadorSinAseo: List<String>,
    precioKWPesos: String,
    totalAseoPorCocina: String,
    pagarPiso1: String,
    pagarPiso1SinAseo: String,
    consumoPiso1: String,
    totalAPAgarPorContadorRedondeado: List<String>,
    pagarPiso1Redondeado: String
){
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("resultados del cálculo")},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack()}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ){paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ){
            if (totalAPagarPorcontador.isNotEmpty() && totalAPagarPorcontador.first().isNotBlank()) {
                item {
                    Text(
                        text = "Resultado Total a Pagar",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                item {
                    Text(
                        text = "Resultados redondeados",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(totalAPAgarPorContadorRedondeado) { resultado ->
                    Text(
                        text = resultado,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                item(pagarPiso1Redondeado){
                    Text(
                        text = pagarPiso1Redondeado,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                item {
                    Text(
                        text = "Resultados exactos",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(totalAPagarPorcontador) { resultado ->
                    Text(
                        text = resultado,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                item(pagarPiso1){
                    Text(
                        text = pagarPiso1,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }

                item {
                    Text(
                        text = "Otros Datos",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                item {
                    Text(
                        text = "Consumo de KW por Contador",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(consumoKWPorContador) { resultado ->
                    Text(text = resultado, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 4.dp))
                }
                item(consumoPiso1) {
                    Text(
                        text = consumoPiso1,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 4.dp))
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Text(
                        text = "Total sin Aseo",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(precioTotalPorContadorSinAseo) { resultado ->
                    Text(text = resultado, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 4.dp))
                }
                item(pagarPiso1SinAseo) {
                    Text(text = pagarPiso1SinAseo,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 4.dp))
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Text(text = precioKWPesos, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 4.dp))
                }
                item {
                    Text(text = totalAseoPorCocina, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 4.dp))
                }
            } else {
                item {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        Text("No se pudieron calcular los resultados. Vuelve y revisa los datos.", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculadoraPreview() {
    CuentasDeEnergiaTheme {
        CuentasEnergia(navController = rememberNavController())
    }
}