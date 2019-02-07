/*
 * Created by Kultala Aki on 9.9.2017 9:29
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 27.11.2016 18:27
 */

package kultalaaki.vpkapuri;

import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class OhjeitaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ohjeita_uusi);

        final ViewGroup transitionsContainer = findViewById(R.id.cont);
        final Button alkusanat = findViewById(R.id.buttonAlkusanat);
        final TextView talkusanat = findViewById(R.id.textAlkusanat);
        //talkusanat.setVisibility(View.GONE);
        alkusanat.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                visible = !visible;
                talkusanat.setVisibility(visible ? View.GONE : View.VISIBLE);
            }
        });

        final Button halytys = findViewById(R.id.buttonHalytys);
        final TextView thalytys = findViewById(R.id.thalytys);
        thalytys.setVisibility(View.GONE);
        halytys.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                visible = !visible;
                thalytys.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

        final Button asetukset = findViewById(R.id.buttonAsetukset);
        final TextView tasetukset = findViewById(R.id.tasetukset);
        tasetukset.setVisibility(View.GONE);
        asetukset.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                visible = !visible;
                tasetukset.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

        final Button arkisto = findViewById(R.id.buttonarkisto);
        final TextView tarkisto = findViewById(R.id.tarkisto);
        tarkisto.setVisibility(View.GONE);
        arkisto.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                visible = !visible;
                tarkisto.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

        final Button whatsapp = findViewById(R.id.buttonWhatsapp);
        final TextView twhatsapp = findViewById(R.id.twhatsapp);
        twhatsapp.setVisibility(View.GONE);
        whatsapp.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                visible = !visible;
                twhatsapp.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

        final Button yleista = findViewById(R.id.buttonYleista);
        final TextView tyleista = findViewById(R.id.tyleista);
        tyleista.setVisibility(View.GONE);
        yleista.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                visible = !visible;
                tyleista.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });
    }
}
