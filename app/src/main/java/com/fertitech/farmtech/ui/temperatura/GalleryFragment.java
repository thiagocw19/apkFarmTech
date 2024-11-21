package com.fertitech.farmtech.ui.temperatura;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fertitech.farmtech.DataHolder;
import com.fertitech.farmtech.R;
import com.fertitech.farmtech.databinding.FragmentGalleryBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private BarChart chartTemperatura;
    private BarChart chartUmidade;

    private BarChart chartUmidadeSolo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicialize os gráficos
        chartUmidadeSolo = binding.chartSolo;
        chartTemperatura = binding.chartTemp;
        chartUmidade = binding.chartUmid;

        // Configure os gráficos
        configureChart(chartTemperatura);
        configureChart(chartUmidade);
        configureChart(chartUmidadeSolo);

        updateChartTemperatura(chartTemperatura);
        updateChartUmidade(chartUmidade);
        updateChartUmidadeSolo(chartUmidadeSolo);

        return root;
    }

    private void configureChart(BarChart chart) {
        // Configurações gerais do gráfico
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(60);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        chart.setBackgroundResource(R.drawable.fundograph);

        // Configurações do eixo X
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getFormattedDates())); // Formate as datas aqui

        // Configurações do eixo Y
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(0f); // Para começar do zero

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setAxisMinimum(0f); // Para começar do zero

        Legend l = chart.getLegend();
        l.setTextSize(15f);
        l.setTextColor(Color.WHITE);
    }

    private void updateChartTemperatura(BarChart chart) {
        // Obtenha os dados de DataHolder
        DataHolder dataHolder = DataHolder.getInstance();
        Map<String, List<Float>> temperaturasPorData = dataHolder.getTemperaturasPorData();

        // Adicione o valor ao conjunto de dados
        ArrayList<BarEntry> values = new ArrayList<>();
        int i = 0;
        for (String data : temperaturasPorData.keySet()) {
            List<Float> temperaturas = temperaturasPorData.get(data);
            float soma = 0;
            for (float temp : temperaturas) {
                soma += temp;
            }
            float media = soma / temperaturas.size();
            values.add(new BarEntry(i, media));
            i++;
        }

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Temperatura do Ar");
            set1.setDrawIcons(false);
            set1.setValueTextSize(15f);
            set1.setValueTextColor(Color.WHITE);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            chart.setData(data);
        }
    }

    private void updateChartUmidade(BarChart chart) {
        // Obtenha os dados de DataHolder
        DataHolder dataHolder = DataHolder.getInstance();
        Map<String, List<Float>> umidadesPorData = dataHolder.getUmidadesPorData();

        // Adicione o valor ao conjunto de dados
        ArrayList<BarEntry> values = new ArrayList<>();
        int i = 0;
        for (String data : umidadesPorData.keySet()) {
            List<Float> umidades = umidadesPorData.get(data);
            float soma = 0;
            for (float umid : umidades) {
                soma += umid;
            }
            float media = soma / umidades.size();
            values.add(new BarEntry(i, media));
            i++;
        }

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Umidade do Ar");
            set1.setDrawIcons(false);
            set1.setValueTextSize(15f);
            set1.setValueTextColor(Color.WHITE);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            chart.setData(data);
        }
    }

    private void updateChartUmidadeSolo(BarChart chart) {
        // Obtenha os dados de DataHolder
        DataHolder dataHolder = DataHolder.getInstance();
        Map<String, List<Float>> umidadeSoloPorData = dataHolder.getUmidadeSoloPorData();

        // Adicione o valor ao conjunto de dados
        ArrayList<BarEntry> values = new ArrayList<>();
        int i = 0;
        for (String data : umidadeSoloPorData.keySet()) {
            List<Float> umidadesolo = umidadeSoloPorData.get(data);
            float soma = 0;
            for (float umida : umidadesolo) {
                soma += umida;
            }
            float media = soma / umidadesolo.size();
            values.add(new BarEntry(i, media));
            i++;
        }

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Umidade do Solo");
            set1.setDrawIcons(false);
            set1.setValueTextSize(15f);
            set1.setValueTextColor(Color.WHITE);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            chart.setData(data);
        }
    }

    public class SensorDataViewModel extends ViewModel {
        private final MutableLiveData<Map<String, List<Float>>> temperaturasLiveData = new MutableLiveData<>();
        private final MutableLiveData<Map<String, List<Float>>> umidadesLiveData = new MutableLiveData<>();
        private final MutableLiveData<Map<String, List<Float>>> umidadeSoloLiveData = new MutableLiveData<>();

        public void setTemperaturas(Map<String, List<Float>> temperaturas) {
            temperaturasLiveData.setValue(temperaturas);
        }

        public LiveData<Map<String, List<Float>>> getTemperaturas() {
            return temperaturasLiveData;
        }

        public void setUmidades(Map<String, List<Float>> umidades) {
            umidadesLiveData.setValue(umidades);
        }

        public LiveData<Map<String, List<Float>>> getUmidades() {
            return umidadesLiveData;
        }
        public void setUmidadeSolo(Map<String, List<Float>> umidadeSolo) {
            umidadeSoloLiveData.setValue(umidadeSolo);
        }

        public LiveData<Map<String, List<Float>>> getUmidadeSolo() {
            return umidadeSoloLiveData;
        }
    }

    private List<String> getFormattedDates() {
        List<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 30; i++) {
            dates.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        return dates;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}