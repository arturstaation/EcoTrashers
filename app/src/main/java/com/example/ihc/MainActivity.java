package com.example.ihc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {



    int vidas;
    int pontos;

    int lixo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onJogar(View view) {
        vidas = 3;
        pontos = 0;
        setContentView(R.layout.activity_jogar);

        final TextView texto_pontos = findViewById(R.id.texto_pontos);
        final TextView texto_vidas = findViewById(R.id.texto_vidas);
        final ImageView lixo_metal = findViewById(R.id.lixo_metal);
        final ImageView lixo_papel = findViewById(R.id.lixo_papel);
        final ImageView lixo_plastico = findViewById(R.id.lixo_plastico);
        final ImageView lixo_vidro = findViewById(R.id.lixo_vidro);
        final ImageView lixo_imagem = findViewById(R.id.lixo_imagem);

        lixo_metal.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_orange_light));
        lixo_papel.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_blue_light));
        lixo_plastico.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));
        lixo_vidro.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_green_light));

        texto_vidas.setText("Vidas: " + vidas);
        texto_pontos.setText("Pontos: " + pontos);

        lixo = new Random().nextInt(4);
        if (lixo == 0) {
            lixo_imagem.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_orange_light));
        } else if (lixo == 1) {
            lixo_imagem.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_blue_light));
        } else if (lixo == 2) {
            lixo_imagem.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));
        } else {
            lixo_imagem.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_green_light));
        }

        final Handler handler = new Handler();

        View.OnClickListener lixoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lixoSelecionado = -1;
                int id = v.getId();

                if (id == R.id.lixo_metal) {
                    lixoSelecionado = 0;
                } else if (id == R.id.lixo_papel) {
                    lixoSelecionado = 1;
                } else if (id == R.id.lixo_plastico) {
                    lixoSelecionado = 2;
                } else if (id == R.id.lixo_vidro) {
                    lixoSelecionado = 3;
                }

                if (lixoSelecionado == lixo) {
                    pontos++;
                    texto_pontos.setText("Pontos: " + pontos);
                } else {
                    vidas--;
                    texto_vidas.setText("Vidas: " + vidas);
                }

                // Sorteie um novo lixo após cada clique
                lixo = new Random().nextInt(4);

                if (lixo == 0) {
                    lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_light));
                } else if (lixo == 1) {
                    lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_blue_light));
                } else if (lixo == 2) {
                    lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_light));
                } else {
                    lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_green_light));
                }

                // Verifique se o jogo acabou
                if (vidas <= 0) {
                    setContentView(R.layout.activity_perder);
                    TextView derrota = findViewById(R.id.texto_derrota);
                    derrota.setText("Sua pontuacao foi: " + pontos);

                }
            }
        };

        lixo_metal.setOnClickListener(lixoClickListener);
        lixo_papel.setOnClickListener(lixoClickListener);
        lixo_plastico.setOnClickListener(lixoClickListener);
        lixo_vidro.setOnClickListener(lixoClickListener);
    }


    public void onPerder(View view){

        setContentView(R.layout.activity_main);
    }



}