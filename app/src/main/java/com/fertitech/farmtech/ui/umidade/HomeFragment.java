package com.fertitech.farmtech.ui.umidade;

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
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fertitech.farmtech.DataHolder;
import com.fertitech.farmtech.R;
import com.fertitech.farmtech.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Map<String, List<Float>> temperaturasPorData = new HashMap<>();
    private Map<String, List<Float>> umidadesPorData = new HashMap<>();
    private Map<String, List<Float>> umidadeSoloPorData = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textViewCurrentTemperature = binding.TempAtual;
        final View fundo = binding.fundo;
        final TextView temp = binding.textViewTemperatura;
        final TextView textView = binding.UmidadeAtual;
        final TextView umidade = binding.textViewUmidade;
        final TextView umidadesolo = binding.textViewSolo;
        final TextView umid = binding.UmidSolo;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String url = "https://farmtech.sytes.net/BuscarTempAndroid.php";

                if (getActivity() != null) {
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject3 = new JSONObject(response);
                                        String umidadesolo = jsonObject3.getString("value3");
                                        umid.setText(umidadesolo + "%");
                                        umid.setTextColor(Color.WHITE);
                                        float umidadesol = Float.parseFloat(umidadesolo);
                                        String dataAtual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                        if (!umidadeSoloPorData.containsKey(dataAtual)) {
                                            umidadeSoloPorData.put(dataAtual, new ArrayList<Float>());
                                        }
                                        umidadeSoloPorData.get(dataAtual).add(umidadesol);
                                        JSONObject jsonObject2 = new JSONObject(response);
                                        String umidade = jsonObject2.getString("value2");
                                        textView.setText(umidade + "%");
                                        textView.setTextColor(Color.WHITE);
                                        float umidaded = Float.parseFloat(umidade);
                                        if (!umidadesPorData.containsKey(dataAtual)) {
                                            umidadesPorData.put(dataAtual, new ArrayList<Float>());
                                        }
                                        umidadesPorData.get(dataAtual).add(umidaded);
                                        JSONObject jsonObject = new JSONObject(response);
                                        String temperatura = jsonObject.getString("value1");
                                        textViewCurrentTemperature.setText(temperatura + "°C");
                                        textViewCurrentTemperature.setTextColor(Color.WHITE);
                                        float temperaturad = Float.parseFloat(temperatura);
                                        if (!temperaturasPorData.containsKey(dataAtual)) {
                                            temperaturasPorData.put(dataAtual, new ArrayList<Float>());
                                        }
                                        temperaturasPorData.get(dataAtual).add(temperaturad);

                                        DataHolder dataHolder = DataHolder.getInstance();
                                        dataHolder.setTemperaturasPorData(temperaturasPorData);
                                        dataHolder.setUmidadesPorData(umidadesPorData);
                                        dataHolder.setUmidadeSoloPorData(umidadeSoloPorData);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Trate os erros aqui
                        }
                    });

                    Volley.newRequestQueue(getActivity()).add(stringRequest);
                }

                handler.postDelayed(this, 1500);
            }
        }, 1500);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}