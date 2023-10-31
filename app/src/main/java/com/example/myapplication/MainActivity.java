package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String URL = "https://yesno.wtf/";
    private Retrofit retrofitCEP;


    private Button btnConsultar;
    private TextInputEditText txtCEP,txtPhrase, txtComplemento, txtBairro, txtUF;

    private TextView simOuNao;

    private TextInputLayout layCEP;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        simOuNao = findViewById(R.id.simOuNao);
        txtPhrase = findViewById(R.id.txtPhrase);
        btnConsultar= findViewById(R.id.btnConsultar);
        progressBar = findViewById(R.id.progressBarCEP);

        progressBar.setVisibility(View.GONE);

        retrofitCEP = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        btnConsultar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnConsultar) {
            if (validarCampos()) {
                esconderTeclado();
                consultarResposta();
            }
        }
    }

    private void consultarResposta() {

        RestService restService = retrofitCEP.create(RestService.class);

        Call<Frase> call = restService.consultarResposta();

        progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<Frase>() {

            @Override
            public void onResponse(Call<Frase> call, Response<Frase> response) {
                if (response.isSuccessful()) {
                    Frase frase = response.body();
                    if (frase.getAnswer() == "yes"){simOuNao.setText("SIM");}
                    else{simOuNao.setText("NÃO");}
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Frase> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Ocorreu um erro ao ler sua frase: " + t.getMessage(), Toast.LENGTH_LONG).show();

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private Boolean validarCampos() {
        Boolean status = true;

        String phrase = txtPhrase.getText().toString();

        if (phrase.isEmpty()) {
            txtPhrase.setError("Digite sua pergunta:");
            status = false;
        }

        return status;
    }

    private void esconderTeclado() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

}