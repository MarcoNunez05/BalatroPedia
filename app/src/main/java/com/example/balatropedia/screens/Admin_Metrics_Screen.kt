// Última modificación: 01/06/2026
// Autor: Marco Núñez

package com.example.balatropedia.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.models.AdminMetricsViewModel
import com.example.balatropedia.ui.theme._BALATRO_FONT
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.component.lineComponent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.balatropedia.components._KpiCard
import com.example.balatropedia.ui.theme.COLOR_BLINDS_TEXT
import com.example.balatropedia.ui.theme.COLOR_BOOSTER_TEXT
import com.example.balatropedia.ui.theme.COLOR_CHALLENGES_TEXT
import com.example.balatropedia.ui.theme.COLOR_CONSUMIBLES_TEXT
import com.example.balatropedia.ui.theme.COLOR_JOKER_TEXT
import com.example.balatropedia.ui.theme.COLOR_MANOS_TEXT
import com.example.balatropedia.ui.theme.COLOR_MAZOS_TEXT
import com.example.balatropedia.ui.theme.COLOR_VOUCHERS_TEXT

@Composable
fun _Admin_Metrics_Screen(
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: AdminMetricsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    var vIsRecalculating by remember { mutableStateOf(false) }

    val reporte by viewModel.reporte.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarEstadisticas()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        topBar = {
            _Balatropedia_Header(
                comeBack = true,
                onBackClick = onNavigateBack,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E222B))
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Métricas de Comunidad",
                color = Color.White,
                fontSize = 28.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    vIsRecalculating = true
                    viewModel.recalcularEstadisticas(
                        onSuccess = {
                            vIsRecalculating = false
                            Toast.makeText(context, "Métricas actualizadas con éxito", Toast.LENGTH_SHORT).show()
                        },
                        onError = { error ->
                            vIsRecalculating = false
                            Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC33C3C)),
                shape = RoundedCornerShape(8.dp),
                enabled = !vIsRecalculating,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                if (vIsRecalculating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Recalculando datos...", color = Color.White, fontFamily = _BALATRO_FONT)
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Recalcular Métricas Ahora", color = Color.White, fontSize = 16.sp, fontFamily = _BALATRO_FONT)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    _QuickStats_Row(
                        totalUsuarios = reporte?.total_usuarios ?: 0,
                        totalVotos = reporte?.total_votos ?: 0,
                        promedioGlobal = reporte?.promedio_global ?: 0f
                    )
                }

                item {
                    _Geography_Bar_Chart(paisMap = reporte?.top_paises ?: emptyMap())
                }

                item {
                    _Age_Histogram_Chart(edadMap = reporte?.edad_distribucion ?: emptyMap())
                }

                item {
                    _Category_Donut_Chart(categoriasMap = reporte?.actividad_por_categoria ?: emptyMap())
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
// Gráfica de placeholder para gráficas que no existen
fun _Placeholder_Chart_Card(titulo: String, subtitulo: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2C323F), RoundedCornerShape(8.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = titulo,
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = _BALATRO_FONT,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFF1E222B), RoundedCornerShape(4.dp))
                .border(1.dp, Color(0xFF3F4552), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = subtitulo, color = Color.Gray)
        }
    }
}

@Composable
// Histograma de rangos de edades
fun _Age_Histogram_Chart(edadMap: Map<String, Int>) {
    val franjasOrdenadas = listOf("13-17", "18-24", "25-34", "35+")

    val chartEntries = franjasOrdenadas.mapIndexed { index, franja ->
        FloatEntry(
            x = index.toFloat(),
            y = (edadMap[franja] ?: 0).toFloat()
        )
    }

    if (edadMap.isEmpty()) {
        _Placeholder_Chart_Card("Distribución por Edad", "Aún no hay datos de edad...")
        return
    }

    val bottomAxisFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        franjasOrdenadas.getOrNull(value.toInt()) ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2C323F), RoundedCornerShape(8.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Distribución por Edad",
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = _BALATRO_FONT,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color(0xFF1E222B), RoundedCornerShape(4.dp))
                .border(1.dp, Color(0xFF3F4552), RoundedCornerShape(4.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Chart(
                chart = columnChart(
                    columns = listOf(
                        lineComponent(
                            color = Color(0xFFC33C3C),
                            thickness = 24.dp
                        )
                    )
                ),
                model = entryModelOf(chartEntries),
                startAxis = rememberStartAxis(
                    label = axisLabelComponent(color = Color(0xFFE0E0E0)),
                    axis = lineComponent(color = Color(0xFF555555), thickness = 1.dp),
                    guideline = lineComponent(color = Color(0xFF3F4552), thickness = 1.dp)
                ),
                bottomAxis = rememberBottomAxis(
                    valueFormatter = bottomAxisFormatter,
                    label = axisLabelComponent(color = Color(0xFFE0E0E0)),
                    axis = lineComponent(color = Color(0xFF555555), thickness = 1.dp),
                    guideline = null
                )
            )
        }
    }
}

@Composable
// Gráfico de donuts para la actividad por categoría
fun _Category_Donut_Chart(categoriasMap: Map<String, Int>) {
    if (categoriasMap.isEmpty()) {
        _Placeholder_Chart_Card("Actividad por Categoría", "Aún no hay datos...")
        return
    }

    var totalVotos = 0f
    val valoresSeguros = categoriasMap.mapValues {
        val valor = (it.value as? Number)?.toFloat() ?: 0f
        totalVotos += valor
        valor
    }

    if (totalVotos <= 0f) {
        _Placeholder_Chart_Card("Actividad por Categoría", "Esperando votos...")
        return
    }

    val colores = listOf(
        COLOR_BLINDS_TEXT.copy(alpha = 0.9f),
        COLOR_JOKER_TEXT.copy(alpha = 0.9f),
        COLOR_BOOSTER_TEXT.copy(alpha = 0.9f),
        COLOR_MANOS_TEXT.copy(alpha = 0.9f),
        COLOR_CHALLENGES_TEXT.copy(alpha = 0.9f),
        COLOR_MAZOS_TEXT.copy(alpha = 0.9f),
        COLOR_VOUCHERS_TEXT.copy(alpha = 0.9f),
        COLOR_CONSUMIBLES_TEXT.copy(alpha = 0.9f)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2C323F), RoundedCornerShape(8.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Actividad por Categoría",
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = _BALATRO_FONT,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 24.dp)
        )

        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                val strokeWidth = 30.dp.toPx()

                valoresSeguros.entries.forEachIndexed { index, entry ->
                    val sweepAngle = (entry.value / totalVotos) * 360f
                    val color = colores[index % colores.size]

                    if (sweepAngle > 0f) {
                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                    }
                    startAngle += sweepAngle
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = totalVotos.toInt().toString(),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Votos Totales",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val leyendaItems = valoresSeguros.entries.mapIndexed { index, entry ->
            Pair(entry, colores[index % colores.size])
        }.filter { it.first.value > 0f }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leyendaItems.chunked(2).forEach { filaItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    filaItems.forEach { (entry, color) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(color, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${entry.key.replaceFirstChar { it.uppercase() }} (${entry.value.toInt()})",
                                color = Color(0xFFE0E0E0),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
// Gráfico de barras de distruibución geográfica
fun _Geography_Bar_Chart(paisMap: Map<String, Int>) {
    if (paisMap.isEmpty()) {
        _Placeholder_Chart_Card("Distribución Geográfica", "Aún no hay datos...")
        return
    }

    val valoresSeguros = paisMap.mapValues { (it.value as? Number)?.toInt() ?: 0 }

    val maxVotos = valoresSeguros.values.maxOrNull()?.toFloat() ?: 1f

    val paisesOrdenados = valoresSeguros.entries.sortedByDescending { it.value }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2C323F), RoundedCornerShape(8.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Distribución Geográfica",
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = _BALATRO_FONT,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        paisesOrdenados.forEach { entry ->
            val porcentaje = if (maxVotos > 0) entry.value / maxVotos else 0f

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.key.uppercase(),
                    color = Color(0xFFE0E0E0),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.25f)
                )

                Box(
                    modifier = Modifier
                        .weight(0.60f)
                        .height(16.dp)
                        .background(Color(0xFF1E222B), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(porcentaje)
                            .background(Color(0xFF4A90E2), RoundedCornerShape(4.dp))
                    )
                }

                Text(
                    text = entry.value.toString(),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.15f)
                )
            }
        }
    }
}

@Composable
// Cajas de estadísticas simples
fun _QuickStats_Row(totalUsuarios: Int, totalVotos: Int, promedioGlobal: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        _KpiCard(
            titulo = "USUARIOS",
            valor = totalUsuarios.toString(),
            modifier = Modifier.weight(1f)
        )
        _KpiCard(
            titulo = "VOTOS",
            valor = totalVotos.toString(),
            modifier = Modifier.weight(1f)
        )
        _KpiCard(
            titulo = "PROMEDIO",
            valor = String.format("%.1f", promedioGlobal) + "★",
            modifier = Modifier.weight(1f)
        )
    }
}
