package com.fertitech.farmtech;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        final EditText editEmail = findViewById(R.id.edit_email);
        final EditText editSenha = findViewById(R.id.edit_senha);
        Button btEntrar = findViewById(R.id.bt_entrar);
        final ProgressBar progressBar = findViewById(R.id.Progressbar);
        Button btCad = findViewById(R.id.bt_cad);

        btCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://farmtech.sytes.net/register.php";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();
                if (email.isEmpty() || senha.isEmpty())
                {
                    Toast.makeText(Login.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                }

                String url = "https://farmtech.sytes.net/autenticacao.php?email=" + email + "&senha=" + senha;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("Login successful")) {
                                    SharedPrefManager.getInstance(getApplicationContext()).saveUser(email);
                                    progressBar.setVisibility(View.VISIBLE);
                                    Toast.makeText(Login.this, "Entrando...", Toast.LENGTH_SHORT).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.INVISIBLE); // Esconde a ProgressBar
                                            Intent intent = new Intent(Login.this, MainActivity.class);
                                            intent.putExtra("userEmail", email);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 3000); // Atraso de 3 segundos
                                } if (response.equals("Login failed")) {
                                    progressBar.setVisibility(View.INVISIBLE); // Esconde a ProgressBar
                                    // Mostre uma mensagem de erro
                                    Toast.makeText(Login.this, "Email ou Senha inválido...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE); // Esconde a ProgressBar
                        Toast.makeText(Login.this, "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                // Adicione a requisição à fila de requisições
                Volley.newRequestQueue(Login.this).add(stringRequest);
            }
        });
    }
}