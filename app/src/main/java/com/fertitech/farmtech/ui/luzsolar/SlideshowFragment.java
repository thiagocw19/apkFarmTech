package com.fertitech.farmtech.ui.luzsolar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fertitech.farmtech.databinding.FragmentSlideshowBinding;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button ligar = binding.btnLigar;
        final Button desligar = binding.btnDesligar;
        final TextView texto = binding.txtStatus;
        final Handler handler = new Handler();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Farmtech", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ligar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ligar.setEnabled(false);
                texto.setText("Ligando a Bomba");
                editor.putBoolean("estado_bomba", true);
                editor.apply();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = "https://farmtech.sytes.net/BombaAndroid.php";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Trate a resposta aqui
                                        desligar.setEnabled(true);
                                        texto.setText("Bomba Ligada");
                                        texto.setTextColor(Color.RED);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Trate os erros aqui
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("estado_bomba", "1");
                                return params;
                            }
                        };

                        Volley.newRequestQueue(getActivity()).add(stringRequest);
                    }
                }, 3000); // 3000 milissegundos = 3 segundos
            }
        });

        desligar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desligar.setEnabled(false);
                texto.setText("Desligando a Bomba");
                texto.setTextColor(Color.WHITE);
                editor.putBoolean("estado_bomba", false);
                editor.apply();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = "https://farmtech.sytes.net/BombaAndroid.php";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Trate a resposta aqui
                                        ligar.setEnabled(true);
                                        texto.setText("Bomba Desligada");
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Trate os erros aqui
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("estado_bomba", "0");
                                return params;
                            }
                        };

                        Volley.newRequestQueue(getActivity()).add(stringRequest);
                    }
                }, 3000); // 3000 milissegundos = 3 segundos
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button ligar = binding.btnLigar;
        final Button desligar = binding.btnDesligar;
        final TextView texto = binding.txtStatus;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Farmtech", Context.MODE_PRIVATE);
        boolean estadoBomba = sharedPreferences.getBoolean("estado_bomba", false);

        if (estadoBomba) {
            // A bomba está ligada
            ligar.setEnabled(false);
            desligar.setEnabled(true);
            texto.setText("Bomba Ligada");
            texto.setTextColor(Color.RED);
        } else {
            // A bomba está desligada
            ligar.setEnabled(true);
            desligar.setEnabled(false);
            texto.setText("Bomba Desligada");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}